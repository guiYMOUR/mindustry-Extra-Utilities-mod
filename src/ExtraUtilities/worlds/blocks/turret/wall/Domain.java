package ExtraUtilities.worlds.blocks.turret.wall;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUItems;
import ExtraUtilities.net.EUCall;
import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.logic.Ranged;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.*;

public class Domain extends Block {
    //shield
    public Effect abEffect = EUFx.shieldDefense;
    public Effect fullEffect = Fx.absorb;
    public Effect removeEffect = new Effect(20, e -> {
        color(e.color, e.fout());
        if(renderer.animateShields){
            Fill.circle(e.x, e.y, e.rotation * e.fout());
        }else{
            stroke(1.5f);
            Draw.alpha(0.09f);
            Fill.circle(e.x, e.y, e.rotation * e.fout());
            Draw.alpha(1f);
            Lines.circle(e.x, e.y, e.rotation * e.fout());
        }
    }).layer(Layer.shields);
    public float range = 22.5f * 8;
    public int bulletAmount = 50;
    public float coolDown = 8;
    public float coolDownBk = 5;
    public float shieldHealth = 3500f;
    public boolean canBroken = true;
    public Effect shieldBkEffect = new Effect(40, e -> {
        color(e.color);
        stroke(3f * e.fout());
        Lines.circle(e.x, e.y, e.rotation + e.fin());
    });
    //speeder
    public float upSpeed = 1.2f;
    public float upSpeedAfter = 2f;//盾破了后的加速
    public float reloadS = 60;
    //healer-block
    public float reloadH = 240f;
    public float reloadHAfter = 150;//盾破了之后的治疗冷却
    public float healPercent = 25f;
    //healer-unit
    public boolean healByPercent = false;
    public float healPercentUnit = 10;
    public float healAmountUnit = 300;
    public float reloadHU = 180;
    public float reloadHUAfter = 120;//还是破盾后的
    //群内投票结果，选择用量治疗而不是百分百

    public float unitDamage = 12;

    protected static DomainBuild db;
    protected static Effect e, ef;
    protected static int amount;
    protected static boolean canBK;
    protected static float ud;
    protected static final Cons<Bullet> bulletConsumer = bullet -> {
        if(bullet.team != db.team && bullet.type != null && bullet.type.absorbable && bullet.within(db, db.range())){
            if(canBK && db.absorbed < amount && bullet.type.reflectable && bullet.type.collides && bullet.type.collidesAir && bullet.type.collidesGround && bullet.type.collidesTiles && bullet.type.damage > 0) {
                e.at(bullet.getX(), bullet.getY(), db.team.color);
                db.reBullet.put(bullet.type.copy(), new float[]{bullet.x, bullet.y, bullet.rotation()-180, bullet.fout()});
                db.absorbed++;
            } else {
                ef.at(bullet);
            }
            bullet.absorb();
            if(canBK) {
                db.hit = 1f;
                db.buildup += bullet.damage;
            }
        }
    };
    protected static final Cons<Unit> unitShield = unit -> {
        if(db != null && unit.targetable(db.team)) {
            float overlapDst = (unit.hitSize / 2f + db.range()) - unit.dst(db);
            if (overlapDst > 0) {
                db.buildup += (unit.hitSize * ud);
                db.hit = 1;
            }
        }
    };
    protected static final Cons<Unit> unitConsumer = unit -> {
        if(db != null && unit.targetable(db.team)) {
            float overlapDst = (unit.hitSize / 2f + db.range()) - unit.dst(db);

            if (overlapDst > 0) {
                //stop
                unit.vel.setZero();
                //get out
                unit.move(Tmp.v1.set(unit).sub(db).setLength(overlapDst + 0.01f));

                if (Mathf.chanceDelta(0.12f * Time.delta)) {
                    Fx.circleColorSpark.at(unit.x, unit.y, db.team.color);
                }

            }
        }
    };

    public Domain(String name) {
        super(name);
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        emitLight = true;
        envEnabled |= Env.space;

        ambientSound = Sounds.shield;
        ambientSoundVolume = 0.08f;
    }

    @Override
    public void init(){
        updateClipRadius(range + 4);
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.repairTime, (int) (100f / healPercent * reloadH / 60f), StatUnit.seconds);
        if (healByPercent)
            stats.add(Stat.repairTime, Core.bundle.format("stat-heal-unit-by-per", (int) (100f / healPercentUnit * reloadHU / 60f)));
        else stats.add(Stat.repairTime, Core.bundle.format("stat-heal-unit", reloadHU / 60f, healAmountUnit));
        stats.add(Stat.speedIncrease, "+" + (int) (upSpeed * 100f - 100) + "%");
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        if (canBroken) {
            stats.add(Stat.shieldHealth, shieldHealth, StatUnit.none);
            stats.add(Stat.cooldownTime, (int) (shieldHealth / coolDownBk / 60f), StatUnit.seconds);
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("boost", (DomainBuild entity) -> new Bar(() -> Core.bundle.format("bar.boost", Mathf.round(Math.max((entity.updateSpeeder() * 100 - 100), 0))), () -> Pal.accent, () -> 1));
        addBar("shield", (DomainBuild entity) -> new Bar(() -> Core.bundle.get( "stat.shieldhealth"), () -> entity.bk ? Pal.redLight : Pal.accent, entity::showBar));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, player.team().color);
    }

    public static void broken(Tile tile){
        if(tile == null || !(tile.build instanceof DomainBuild)) return;
        ((DomainBuild) tile.build).broken();
    }

    public class DomainBuild extends Building implements Ranged {
        public float hit;
        public float buildup, radscl;
        public float timerH, timerHU;
        public ObjectMap<BulletType, float[]> reBullet = new ObjectMap<>();
        public int absorbed = 0;

        public boolean bk = false;

        public float showBar(){
            return Math.max(1f - buildup / (shieldHealth), 0);
        }

        @Override
        public float range() {
            return range * radscl;
        }

        @Override
        public void onRemoved(){
            float radius = range();
            if(!bk && radius > 1f) removeEffect.at(x, y, radius, team.color);
            super.onRemoved();
        }

        @Override
        public void pickedUp(){
            super.pickedUp();
            radscl = 0f;
        }

        @Override
        public void updateTile() {
            updateShield();
            updateSpeeder();
            updateHeal();
        }

        public void updateShield(){
            radscl = Mathf.lerpDelta(radscl, bk ? 0f : efficiency, 0.05f);

            if(buildup > 0){
                float scale = !bk ? coolDown : coolDownBk;

                buildup -= delta() * scale;
            }

            if(bk && buildup <= 0){
                bk = false;
            }
            if(buildup >= shieldHealth && !bk && !net.client()){
                EUCall.DomainBroken(tile);
            }

            if(hit > 0f){
                hit -= 1f / 5f * Time.delta;
            }

            deflect();
        }

        public void broken(){
            bk = true;
            buildup = shieldHealth;
            shieldBkEffect.at(x, y, range(), team.color);
            Seq<BulletType> keys = reBullet.keys().toSeq();
            for(int i = 0; i < keys.size; i++){
                BulletType b = keys.get(i);
                if(b != null && reBullet.containsKey(b) && reBullet.get(b) != null){
                    float[] f = reBullet.get(b);
                    b.create(this, team, f[0], f[1], f[2], 1, f[3]);
                }
            }
            absorbed = 0;
            reBullet.clear();
            if(team != state.rules.defaultTeam){
                Events.fire(EventType.Trigger.forceProjectorBreak);
            }
        }

        public float updateSpeeder(){
            float realSpeed = (bk ? upSpeedAfter : upSpeed) * efficiency;

            Vars.indexer.eachBlock(this, range * efficiency, other -> other != this && other.block != null && other.block.canOverdrive, other -> other.applyBoost(realSpeed, reloadS + 1));

            return realSpeed;
        }

        public void updateHeal(){
            if(efficiency < 0.001f) return;
            float realReloadH = bk ? reloadHAfter : reloadH;
            if((timerH += delta()) >= realReloadH && !checkSuppression()){
                float realRange = range * efficiency;
                timerH -= realReloadH;

                indexer.eachBlock(this, realRange, b -> b.damaged() && !b.isHealSuppressed(), other -> {
                    other.heal(other.maxHealth() * healPercent / 100f * efficiency);
                    other.recentlyHealed();
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, Pal.heal, other.block);
                });
            }
            float realReloadHU = bk ? reloadHUAfter : reloadHU;
            if((timerHU += delta()) >= realReloadH){
                float realRange = range;
                timerHU -= realReloadHU;

                Units.nearby(team, x, y, realRange, unit -> {
                    if(unit != null && !unit.dead && unit.damaged()) {
                        float am = healByPercent ? unit.maxHealth * healPercentUnit : healAmountUnit;
                        unit.heal(am * efficiency * Time.delta);
                    }
                });
            }
        }

        public void deflect(){
            if(range() > 0 && !bk){
                db = this;
                e = abEffect;
                ef = fullEffect;
                amount = bulletAmount;
                canBK = canBroken;
                ud = unitDamage;
                float r = range() + 10;
                Groups.bullet.intersect(x - r, y - r, r * 2f, r * 2f, bulletConsumer);
                //优先扣除贴边单位
                if(canBK && timer.get(30)) Units.nearbyEnemies(team, x, y, r, unitShield);
                //排除单位
                Units.nearbyEnemies(team, x, y, r, unitConsumer);
            }
        }

        @Override
        public float edelta() {
            return super.edelta();
        }

        @Override
        public void draw() {
            super.draw();
            if(buildup > 0f){
                Draw.color(Color.valueOf("bf92f9").a(buildup / shieldHealth * 0.8f));
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.rect(Core.atlas.find(name + "-top"), x, y);
                Draw.blend();
                Draw.z(Layer.block);
                Draw.reset();
            }
            drawShield();
        }

        public void drawShield(){
            if(!bk){
                float radius = range();

                Draw.z(Layer.shields);

                Draw.color(team.color, Color.white, Mathf.clamp(hit));

                if(renderer.animateShields){
                    Fill.poly(x, y, 36, radius);
                }else{
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.poly(x, y, 36, radius);
                    Draw.alpha(1f);
                    Lines.poly(x, y, 36, radius);
                    Draw.reset();
                }
            }

            Draw.reset();
        }

        @Override
        public void drawSelect(){
            float realRange = range * efficiency;

            indexer.eachBlock(this, realRange,
                    other -> true,
                    other -> Drawf.selected(other, Tmp.c1.set(other.block != null && other.block.canOverdrive ? Color.valueOf("bf92f9") : Pal.heal).a(Mathf.absin(4f, 1f)))
            );

            Drawf.dashCircle(x, y, realRange, team.color);
        }

//        public void writeFloats(Writes write, float[] floats){
//            write.s((short)floats.length);
//            for(float f : floats){
//                write.f(f);
//            }
//        }
//
//        public float[] readFloats(Reads read){
//            short length = read.s();
//            float[] out = new float[length];
//            for(int i = 0; i < length; i++){
//                out[i] = read.f();
//            }
//            return out;
//        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(bk);
            write.f(buildup);
            write.f(radscl);
//            write.i(absorbed);
//            Seq<BulletType> seq = reBullet.keys().toSeq();
//            short keySize = (short) seq.size;
//            write.s(keySize);
//            for(int i = 0; i < keySize; i++) {
//                BulletType b = seq.get(i);
//                if (b != null && reBullet.get(b) != null) {
//                    TypeIO.writeBulletType(write, b);
//                    writeFloats(write, reBullet.get(b));
//                }
//            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            bk = read.bool();
            buildup = read.f();
            radscl = read.f();
//            absorbed = read.i();
//            short keySize = read.s();
//            for(int i = 0; i < keySize; i++){
//                BulletType b = TypeIO.readBulletType(read);
//                if(b != null) reBullet.put(b, readFloats(read));
//            }
        }
    }
}
