package ExtraUtilities.worlds.blocks.power;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUItems;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static ExtraUtilities.worlds.drawer.DrawFunc.circlePercent;
import static mindustry.Vars.*;

public class LightenGenerator extends NuclearReactor {
    //拉扯范围
    public float range = 36;
    //火花攻击敌方范围
    public float enemyRange = 15f * tilesize;
    //牵引力
    public float pullPower = 22*60;
    //下面是两个特效子弹，当然，可以自己写
    public BulletType effectBullet;
    public BulletType effectBullet2;

    public boolean explosionProof = true;

    public @Nullable BulletType deathBullet;

    public LightenGenerator(String name) {
        super(name);
        effectBullet = new BulletType(){{
            collidesAir = true;
            hittable = false;
            absorbable = false;
            lifetime = 6;
            speed = 1;
            damage = 0;
            splashDamageRadius = 56;
            instantDisappear = true;
            splashDamage = 120;
            hitShake = 3;
            lightningColor = EUItems.lightninAlloy.color;
            lightningDamage = 30;
            lightning = 5;
            lightningLength = 12;
            hitSound = Sounds.release;
            ammoMultiplier = 1;
            hitEffect = new Effect(60, e ->{
                Draw.color(EUItems.lightninAlloy.color);
                Lines.stroke(e.fout() * 2);
                Lines.circle(e.x, e.y, 4 + e.finpow() * splashDamageRadius);
                Draw.color(EUItems.lightninAlloy.color);
                for(int i = 0; i < 4; i++){
                    Drawf.tri(e.x, e.y, 6, 36 * e.fout(), (i - e.fin()) * 90);
                }
                Draw.color();
                for(int i = 0; i < 4; i++){
                    Drawf.tri(e.x, e.y, 3, 16 * e.fout(), (i - e.fin()) * 90);
                }
            });
        }};
        effectBullet2 = new BulletType(){{
            damage = 100;
            speed = 4;
            lifetime = 120;
            hitEffect = Fx.none;
            despawnEffect = Fx.none;
            ammoMultiplier = 3;
            trailEffect = new Effect(12, e ->{
                Draw.color(EUItems.lightninAlloy.color);
                Drawf.tri(e.x, e.y, 4 * e.fout(), 11, e.rotation);
                if(e.data instanceof Float){
                    float time = (float) e.data;
                    Drawf.tri(e.x, e.y, 4 * e.fout(), 15 * Math.min(1, time / 8 * 0.8f + 0.2f), e.rotation - 180);
                }
            });
        }
            @Override
            public void update(Bullet b) {
                if(b.time > 18){
                    Teamc target = Units.closestTarget(b.team, ((LightenGeneratorBuild)b.owner).x, ((LightenGeneratorBuild)b.owner).y, enemyRange,
                            e -> (e.isGrounded() && collidesGround) || (e.isFlying() && collidesAir),
                            t -> this.collidesGround);
                    Teamc targetTo = target != null ? target : (Teamc)b.owner;
                    float homingPower = target == null ? 0.08f : 0.5f;
                    if (targetTo != null) {
                        b.vel.setAngle(Mathf.slerpDelta(b.rotation() + 0.01f, b.angleTo(targetTo), homingPower));
                    }
                }
                trailEffect.at(b.x, b.y, b.rotation(), b.time);
            }

            @Override
            public void draw(Bullet b) {
                Draw.color(EUItems.lightninAlloy.color);
                Drawf.tri(b.x, b.y, 4, 8, b.rotation());
                Draw.reset();
            }
        };
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + this.offset, enemyRange, Pal.accent);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, effectBullet)));
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, effectBullet2)));
        if(deathBullet != null) stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, deathBullet)));
    }

    public class LightenGeneratorBuild extends NuclearReactorBuild{
        public boolean working = false;
        public float consumeTimer = 0;
        public float light = 0;

        @Override
        public void updateTile() {
            if(items.total() == block.itemCapacity && !working){
                EUFx.Start.at(x, y, 0, range);
                Sounds.lasercharge2.at(x, y, 1.5f);
                Units.nearby(null, x, y, range*2, unit -> unit.impulse(Tmp.v3.set(unit).sub(x, y).nor().scl(-pullPower)));
                working = true;
            }
            if(items.total() < 1 && working){
                working = false;
                consumeTimer = 0;
            }
            int fuel = items.get(fuelItem);
            float fullness = (float)fuel / itemCapacity;
            productionEfficiency = fullness;

            if(fuel > 0 && enabled && working){
                heat += fullness * heating * Math.min(delta(), 4f);
                consumeTimer += getProgressIncrease(itemDuration);
                if(/*timer(timerFuel, itemDuration / timeScale)*/consumeTimer >= 1){
                    consume();
                    effectBullet.create(this, team, x + Mathf.range(size * 4), y + Mathf.range(size * 4), 0);
                    float random = Mathf.random(0, 360);
                    for(int i = 0; i < 3; i++){
                        effectBullet2.create(this, x, y, 120f * i + random);
                    }
                    consumeTimer %= 1;
                }
            }else{
                productionEfficiency = 0f;
            }

            if(heat > 0){
                float maxUsed = Math.min(liquids.currentAmount(), heat / coolantPower);
                heat -= maxUsed * coolantPower;
                liquids.remove(liquids.current(), maxUsed);
            }

            if(heat > smokeThreshold){
                float smoke = 1.0f + (heat - smokeThreshold) / (1f - smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * tilesize / 2f),
                            y + Mathf.range(size * tilesize / 2f));
                }
            }

            heat = Mathf.clamp(heat);

            if(heat >= 0.999f){
                Events.fire(EventType.Trigger.thoriumReactorOverheat);
                if(deathBullet != null) Call.createBullet(deathBullet, team, x, y, 0, -1, 1, 1);
                kill();
            }
            light = Mathf.lerpDelta(light, working ? 1 : 0, 0.05f);
        }

        @Override
        public void createExplosion() {
            //if(deathBullet != null) Call.createBullet(deathBullet, team, x, y, 0, -1, 1, 1);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(EUItems.lightninAlloy.color);
            Draw.alpha(items.total() > 0 ? 1 : 0);
            Draw.z(Layer.effect);
            Lines.stroke(3);
            if(!working){
                circlePercent(x, y, range, (float) items.total()/itemCapacity, 135);
            }
            Draw.alpha(light);
            Draw.rect(Core.atlas.find(name("lightnin-generator-lights")), x, y);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x, y, enemyRange, Pal.accent);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(working);
            write.f(consumeTimer);
            write.f(light);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            working = read.bool();
            consumeTimer = read.f();
            light = read.f();
        }
    }
}
