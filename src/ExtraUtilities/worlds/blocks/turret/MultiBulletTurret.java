package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.worlds.meta.EUStatValues;
import arc.Core;
import arc.Events;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.ObjectMap.*;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.MultiReqImage;
import mindustry.ui.ReqImage;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static mindustry.Vars.content;

public class MultiBulletTurret extends Turret {
    //一号子弹为设置主子弹
    public ObjectMap<Item, BulletType[]> ammoTypes = new ObjectMap<>();
    public boolean all = false;
    public boolean autoResetBid = false;

    public MultiBulletTurret(String name) {
        super(name);
    }

    public void ammo(Object... objects) {
        ammoTypes = ObjectMap.of(objects);
    }

    public void limitRange(){
        limitRange(9f);
    }

    public void limitRange(float margin){
        for(Entry<Item, BulletType[]> entry : ammoTypes.entries()){
            for(BulletType b : entry.value) limitRange(b, margin);
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.add(Stat.ammo, EUStatValues.ammo(ammoTypes, all));
        if(all){
            stats.remove(Stat.reload);
            stats.add(Stat.reload, Core.bundle.format("stat.extra-utilities-shootTime", Strings.autoFixed(reload/60f, 2)));
        }
    }

    @Override
    public void init(){
        consume(new ConsumeItemFilter(i -> ammoTypes.containsKey(i)){
            @Override
            public void build(Building build, Table table){
                MultiReqImage image = new MultiReqImage();
                content.items().each(i -> filter.get(i) && i.unlockedNow(),
                        item -> image.add(new ReqImage(new Image(item.uiIcon),
                                () -> build instanceof MultiBulletBuild&& !((MultiBulletBuild)build).ammo.isEmpty() && ((MultiBulletBuild)build).ammo.peek().item == item)));

                table.add(image).size(8 * 4);
            }

            @Override
            public float efficiency(Building build){
                return build instanceof MultiBulletBuild&& !((MultiBulletBuild)build).ammo.isEmpty() ? 1f : 0f;
            }

            @Override
            public void display(Stats stats){}
        });

        ammoTypes.each((item, type) -> {
            float rangeChange = type[0].rangeChange;
            placeOverlapRange = Math.max(placeOverlapRange, range + rangeChange + placeOverlapMargin);
        });

        super.init();
    }

    public class MultiBulletBuild extends TurretBuild{

        public Seq<ItemEntry> ammo = new Seq<>();
        public int bid = 0;
        public float ResetBidTimer = 0;

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            //add first ammo item to cheaty blocks so they can shoot properly
            if(cheating() && ammo.size > 0){
                handleItem(this, ammoTypes.entries().next().key);
            }
        }

        @Override
        public void updateTile(){
            unit.ammo((float)unit.type().ammoCapacity * totalAmmo / maxAmmo);

            if(autoResetBid && bid > 0) {
                if (target == null) {
                    ResetBidTimer += Time.delta;
                    if (ResetBidTimer >= 60) {
                        bid = 0;
                        ResetBidTimer = 0;
                    }
                } else {
                    ResetBidTimer = 0;
                }
            }

            super.updateTile();
        }

        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);

            bars.add(new Bar("stat.ammo", Pal.ammo, () -> (float)totalAmmo / maxAmmo)).growX();
            bars.row();
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            BulletType[] types = ammoTypes.get(item);

            if(types == null) return 0;
            for(BulletType type : types) if(type == null) return 0;

            return Math.min((int)((maxAmmo - totalAmmo) / types[0].ammoMultiplier), amount);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            for(int i = 0; i < amount; i++){
                handleItem(null, item);
            }
        }

        //currently can't remove items from turrets.
        @Override
        public int removeStack(Item item, int amount){
            return 0;
        }

        @Override
        public void handleItem(Building source, Item item){

            if(item == Items.pyratite){
                Events.fire(EventType.Trigger.flameAmmo);
            }

            if(totalAmmo == 0){
                Events.fire(EventType.Trigger.resupplyTurret);
            }

            BulletType[] types = ammoTypes.get(item);
            if(types == null) return;
            for(BulletType type : types) if(type == null) return;
            float ammoMultiplier = types[0].ammoMultiplier;

            totalAmmo += ammoMultiplier;

            //find ammo entry by type
            for(int i = 0; i < ammo.size; i++){
                ItemEntry entry = ammo.get(i);

                //if found, put it to the right
                if(entry.item == item){
                    entry.amount += ammoMultiplier;
                    ammo.swap(i, ammo.size - 1);
                    return;
                }
            }

            //must not be found
            ammo.add(new MultiBulletTurret.ItemEntry(item, (int)ammoMultiplier));
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            BulletType[] types = ammoTypes.get(item);
            if(types == null) return false;
            for(BulletType type : types) if(type == null) return false;

            float ammoMultiplier = 1;
            for(BulletType type : types){
                ammoMultiplier = Math.max(ammoMultiplier, type.ammoMultiplier);
            }
            return totalAmmo + ammoMultiplier <= maxAmmo;
        }

        public BulletType useAmmo(){
            if(cheating()) return peekAmmo();

            ItemEntry entry = ammo.peek();
            entry.amount -= ammoPerShot;
            if(entry.amount <= 0) ammo.pop();
            totalAmmo -= ammoPerShot;
            totalAmmo = Math.max(totalAmmo, 0);
            return entry.type()[0];
        }

        @Override
        public BulletType peekAmmo() {
            return ammo.size == 0 ? null : ammo.peek().type()[0];
        }

        @Override
        public boolean hasAmmo() {
            if(!canConsume()) return false;

            if(ammo.size >= 2 && ammo.peek().amount < ammoPerShot && ammo.get(ammo.size - 2).amount >= ammoPerShot){
                ammo.swap(ammo.size - 1, ammo.size - 2);
            }
            return ammo.size > 0 && ammo.peek().amount >= ammoPerShot;
        }

        public @Nullable
        BulletType[] peekAmmos(){
            return ammo.size == 0 ? new BulletType[]{null} : ammo.peek().type();
        }


        protected void updateShooting(){

            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                BulletType[] type = peekAmmos();//ammoTypes.get(((ItemEntry)ammo.peek()).item)[0];

                shoots(type);

                reloadCounter %= reload;
            }
        }

        protected void shoots(BulletType[] type){
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

            if(shoot.firstShotDelay > 0){
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                type[0].chargeEffect.at(bulletX, bulletY, rotation);
            }

            shoot.shoot(totalShots, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                if (delay > 0f) {
                    Time.run(delay, () -> bullets(type, xOffset, yOffset, angle, mover));
                } else {
                    bullets(type, xOffset, yOffset, angle, mover);
                }
                totalShots++;
            });

            if(consumeAmmoOnce){
                useAmmo();
            }
            if(!all) bid ++;
        }

        protected void bullets(BulletType[] type, float xOffset, float yOffset, float angleOffset, Mover mover){
            queuedBullets --;

            if(dead || (!consumeAmmoOnce && !hasAmmo()) || type == null) return;
            for(BulletType b : type) if(b == null) return;

            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset);

            if(all) {
                for (BulletType b : type) {
                    float shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + b.inaccuracy);
                    float lifeScl = b.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / b.range, minRange / b.range, range() / b.range) : 1f;
                    handleBullet(b.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);
                }
                (shootEffect == null ? type[0].shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type[0].hitColor);
                (smokeEffect == null ? type[0].smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type[0].hitColor);
            } else {
                BulletType b = type[bid % type.length];
                float shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + b.inaccuracy);
                float lifeScl = b.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / b.range, minRange / b.range, range() / b.range) : 1f;
                handleBullet(b.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);
                (shootEffect == null ? b.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, b.hitColor);
                (smokeEffect == null ? b.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, b.hitColor);
            }
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

            ammoUseEffect.at(
                    x - Angles.trnsx(rotation, ammoEjectBack),
                    y - Angles.trnsy(rotation, ammoEjectBack),
                    rotation * Mathf.sign(xOffset)
            );

            if(shake > 0){
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            heat = 1f;

            if(!consumeAmmoOnce){
                useAmmo();
            }
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(ammo.size);
            for(ItemEntry entry : ammo){
                write.s(entry.item.id);
                write.s(entry.amount);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            ammo.clear();
            totalAmmo = 0;
            int amount = read.ub();
            for(int i = 0; i < amount; i++){
                Item item = Vars.content.item(revision < 2 ? read.ub() : read.s());
                short a = read.s();

                //only add ammo if this is a valid ammo type
                if(item != null && ammoTypes.containsKey(item)){
                    totalAmmo += a;
                    ammo.add(new MultiBulletTurret.ItemEntry(item, a));
                }
            }
        }
    }

    public class ItemEntry{
        public Item item;
        public int amount;

        ItemEntry(Item item, int amount){
            this.item = item;
            this.amount = amount;
        }

        public BulletType[] type(){
            return ammoTypes.get(item);
        }

        @Override
        public String toString(){
            return "ItemEntry{" +
                    "item=" + item +
                    ", amount=" + amount +
                    '}';
        }
    }
}
