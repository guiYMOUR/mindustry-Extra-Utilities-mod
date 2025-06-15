package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUItems;
import ExtraUtilities.content.EUStatusEffects;
import ExtraUtilities.worlds.entity.unit.bossEntity;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.world.Block;

import java.util.Objects;

public class ArbiterBulletType extends BulletType {
    public float estRange = 12 * 8f;
    public int maxAbs = 24;
    public BulletType dateBullet = new EntBulletType();
    public BulletType starBar = new BulletType(){{
        absorbable = false;
        reflectable = false;
        status = EUStatusEffects.awsl;
        statusDuration = 15f;
        splashDamageRadius = 6 * 8;
        splashDamage = 200;
        pierceArmor = true;
        homingRange = 15 * 8;
        homingDelay = 18;
        homingPower = 0.1f;

        trailWidth = 2;
        trailLength = 9;

        lifetime = 120;

        collides = false;

        buildingDamageMultiplier = 0.2f;

        despawnEffect = EUFx.layerCircle(45, splashDamageRadius);
        hitSound = despawnSound = Sounds.explosion;
    }
        @Override
        public void update(Bullet b) {
            updateTrail(b);
            if(b.time < homingDelay) return;
            updateHoming(b);
            Teamc target = Units.closestTarget(b.team, b.x, b.y, splashDamageRadius/4f,
                    e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                    t -> t != null && collidesGround && !b.hasCollided(t.id));

            if(target != null) b.remove();
        }

        @Override
        public void updateHoming(Bullet b) {
            Teamc target;
            //home in on allies if possible
            target = Units.closestTarget(b.team, b.x, b.y, homingRange,
                    e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                    t -> t != null && collidesGround && !b.hasCollided(t.id));

            Teamc dateTarget = null;
            if(b.data instanceof Teamc t) dateTarget = t;

            if(target != null){
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
            } else if(dateTarget != null){
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(dateTarget), homingPower * Time.delta * 50f));
            }
        }

        @Override
        public void drawTrail(Bullet b) {
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 1e-4f);
                b.trail.draw(b.team.color, trailWidth);
                Draw.z(z);
            }
        }

        @Override
        public void draw(Bullet b) {
            drawTrail(b);
            Draw.color(b.team.color);
            //Fill.circle(b.x, b.y, 3f);
            Drawf.tri(b.x, b.y, 3, 10, 0);
            Drawf.tri(b.x, b.y, 3, 10, 180);
            Drawf.tri(b.x, b.y, 2, 7, 90);
            Drawf.tri(b.x, b.y, 2, 7, 270);
        }

        @Override
        public void despawned(Bullet b) {
            despawnSound.at(b);
            createSplashDamage(b, b.x, b.y);
            despawnEffect.at(b.x, b.y, splashDamageRadius, b.team.color);
        }
    };

    public ArbiterBulletType(){
        absorbable = false;
        reflectable = false;
        collides = false;
        //trailColor = Pal.surge;
        trailLength = 30;
        trailWidth = 15;
        lifetime = 720;
        speed = 0.7f;

        splashDamage = 1500;
        splashDamageRadius = 24 * 8f;

        intervalBullet = starBar;
        bulletInterval = 45;
        intervalBullets = 1;

        despawnEffect = new MultiEffect(
                EUFx.diffEffect(90, 0, splashDamageRadius, 12, splashDamageRadius, 32, 24, null, 0),
                EUFx.airAsh(90, splashDamageRadius, splashDamageRadius/6f, null, 3,30)
        );

        hitSound = despawnSound = Sounds.titanExplosion;
    }

    @Override
    public float continuousDamage() {
        return damage;
    }

    @Override
    public void updateBulletInterval(Bullet b) {
        if(!(b instanceof Arbiter a)) return;
        if(b.timer.get(1, bulletInterval)){
            a.ibs.add(intervalBullet.create(b, b.team, b.x, b.y, b.rotation()));
        }

        if(a.ibs.size > 0){
            for(Bullet e : a.ibs){
                int ta = Mathf.randomSeed(e.id, 80, 150);
                float tg = Mathf.randomSeed(e.id, 360);
                float angle = b.time * 2 * (ta % 2 == 0 ? 1 : -1) + tg;
                float tx = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 0);
                float ty = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 1);
                EUGet.movePoint(e, tx, ty, 0.1f);
                e.rotation(e.angleTo(tx, ty));
                e.initVel(e.rotation(), 0);
                e.time = 0;

                Teamc target = Units.closestTarget(b.team, b.x, b.y, estRange * 2,
                        en -> en != null && en.checkTarget(collidesAir, collidesGround),
                        Objects::nonNull);

                if(target != null){
                    e.data = target;
                    e.initVel(e.angleTo(target), 3);
                    a.ibs.remove(e);
                }
            }
        }
    }

    @Override
    public void update(Bullet b) {
        if(!(b instanceof Arbiter a)) return;
        updateTrail(b);
        updateBulletInterval(b);
        if(!Vars.headless) Vars.control.sound.loop(Sounds.wind, b, 1.2f);
        float fin = Math.min(b.finpow() * 10, 1);
        float fout = b.time > b.lifetime - 24 ? Interp.smoother.apply((b.lifetime - b.time) /24) : 1;
        if(b.timer.get(1)) {
            for (int i = 1; i <= 3; i++) {
                for(int j = 0; j < 6; j++) {
                    float ag = j * 60 + b.time * 2.5f * (i % 2 == 0 ? 1 : -1);
                    //float sin = Mathf.absin(b.time, 10, 1);
                    float ax = EUGet.dx(b.x, estRange * fin * fout * i/3, ag + 20 * i),
                            ay = EUGet.dy(b.y, estRange * fin * fout * i/3, ag + 20 * i);
                    EUFx.audioEffect.at(ax, ay, b.team.color);
                    EUFx.normalTrail.at(ax, ay, 3, b.team.color);
                }
            }
        }

        if(b.timer.get(3, 5)) {
            Units.nearbyBuildings(b.x, b.y, estRange, bd -> {
                if (bd != null && bd.team != b.team) {
                    if(bd.health <= damage / 12 * buildingDamageMultiplier && a.ens.size < maxAbs && bd.block != null && bd.block.size <= 3) {
                        a.ens.addUnique(dateBullet.create(b, b.team, bd.x, bd.y, 0, -1, 0, 1, bd.block));
                        bd.kill();
                    } else bd.damagePierce(damage / 12 * buildingDamageMultiplier);
                }
            });

            Units.nearbyEnemies(b.team, b.x, b.y, estRange, ut -> {
                if(ut != null && ut.type != null && ut.targetable(b.team) && !ut.inFogTo(b.team)) {
                    if(ut.type.armor > 80){
                        if(ut.health <= damage * ut.type.armor){
                            ut.kill();
                            ut.remove();
                        } else ut.health -= damage * ut.type.armor;
                    } else {
                        if ((ut.health <= ((ut.maxHealth * 0.01f / 12) * (1 + ut.hitSize/100)) || ut.health <= damage / 12)
                                && a.ens.size < maxAbs && ut.type.hitSize <= 20) {
                            if (!(ut instanceof bossEntity)) {
                                a.ens.addUnique(dateBullet.create(b, b.team, ut.x, ut.y, 0, -1, 1, 1, ut.type));
                                ut.kill();
                                ut.remove();
                            }
                        } else {
                            ut.damagePierce(damage / 12);
                            ut.health -= ((ut.maxHealth * 0.01f / 12) * (1 + ut.type.hitSize/100));
                            if(ut.hasEffect(EUStatusEffects.breakage)){
                                float dmg = damage/24;
                                if(ut.health <= dmg) ut.kill();
                                else ut.health -= dmg;
                            }
                        }
                    }
                }
            });
        }

        Groups.bullet.intersect(b.x - estRange, b.y - estRange, estRange * 2, estRange * 2, bullet -> {
            if(bullet.type != null && bullet.team != b.team && b.within(bullet, estRange) && bullet.damage >= damage/12) {
                bullet.vel.limit(3);
                bullet.damage -= Time.delta;
            }
            if(bullet.type != null && bullet.team != b.team && b.within(bullet, estRange) && ((bullet.type.absorbable || bullet.type.hittable) && bullet.type.reflectable) && bullet.damage < damage/12 && a.ebs.size < maxAbs){
                a.ebs.addUnique(bullet);
            }
        });

        a.ens.removeAll(e -> e == null || !e.isAdded());
        a.ebs.removeAll(e -> e == null || !e.isAdded());

        if(a.ens.size > 0){
            for(Bullet e : a.ens){
                int ta = Mathf.randomSeed(e.id, 80, 150);
                float tg = Mathf.randomSeed(e.id, 360);
                float angle = b.time * 2 * (ta % 2 == 0 ? 1 : -1) + tg;
                float tx = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 0);
                float ty = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 1);
                EUGet.movePoint(e, tx, ty, 0.1f);
                e.rotation(e.angleTo(tx, ty));
                e.initVel(e.rotation(), 0);
                e.time = 0;
            }
        }
        if(a.ebs.size > 0){
            for(Bullet e : a.ebs){
                int ta = Mathf.randomSeed(e.id, 80, 150);
                float tg = Mathf.randomSeed(e.id, 360);
                float angle = b.time * 2 * (ta % 2 == 0 ? 1 : -1) + tg;
                float tx = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 0);
                float ty = EUGet.txy(b.x, b.y, ta, ta / 2.5f, tg, angle, 1);
                EUGet.movePoint(e, tx, ty, 0.1f);
                e.rotation(e.angleTo(tx, ty));
                e.initVel(e.rotation(), 0);
                e.team(b.team);
                e.owner(b.owner);
                e.time = 0;
            }
        }
    }

    @Override
    public void updateTrail(Bullet b) {
        super.updateTrail(b);
        if(!(b instanceof Arbiter a)) return;
        if (!Vars.headless) {
            float fin = Math.min(b.finpow() * 10, 1);
            float fout = b.time > b.lifetime - 24 ? Interp.smoother.apply((b.lifetime - b.time) /24) : 1;
            for (int i = 0; i < 6; i++) {
                float angle = i * 60 + b.time * 5;
                float tx = EUGet.txy(b.x, b.y, 50 * fin * fout, 20 * fin * fout, b.rotation() - 90, angle, 0);
                float ty = EUGet.txy(b.x, b.y, 50 * fin * fout, 20 * fin * fout, b.rotation() - 90, angle, 1);
                if (a.trails[i] == null) {
                    a.trails[i] = new Trail(24);
                }

                a.trails[i].length = 12;
                a.trails[i].update(tx, ty, trailInterp.apply(b.fin()));
            }
        }
    }

    @Override
    public void drawTrail(Bullet b) {
        if(trailLength > 0 && b.trail != null){
            float fin = Math.min(b.finpow() * 10, 1);
            float fout = b.time > b.lifetime - 24 ? Interp.smoother.apply((b.lifetime - b.time) /24) : 1;
            float z = Draw.z();
            Draw.z(z - 1e-4f);
            b.trail.draw(b.team.color, trailWidth * fin * fout);
            Draw.z(z);
        }
        if(!(b instanceof Arbiter a)) return;
        for (int i = 0; i < 6; i++) {
            if (a.trails[i] != null) {
                float z = Draw.z();
                Draw.z(z - 1e-4f);
                a.trails[i].draw(b.team.color, 3);
                Draw.z(z);
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        float fin = Math.min(b.finpow() * 10, 1);
        float fout = b.time > b.lifetime - 15 ? Interp.smoother.apply((b.lifetime - b.time) /15) : 1;
        float z = Draw.z();
        Draw.z(Layer.space);
        Draw.color(Color.black);
        Fill.circle(b.x, b.y, 20 * fin * fout);
        Draw.z(Layer.bullet);
        Draw.color(b.team.color);
        Fill.circle(b.x, b.y, 24 * fin * fout);
        Draw.z(z);
    }

    @Override
    public void removed(Bullet b) {

    }

    @Override
    public void despawned(Bullet b) {
        createSplashDamage(b, b.x, b.y);
        Damage.status(b.team, b.x, b.y, splashDamageRadius, EUStatusEffects.awsl, 30, collidesAir, collidesGround);
        Damage.status(b.team, b.x, b.y, splashDamageRadius, StatusEffects.freezing, 5 * 60, collidesAir, collidesGround);
        Damage.status(b.team, b.x, b.y, splashDamageRadius, StatusEffects.electrified, 5 * 60, collidesAir, collidesGround);
        Damage.status(b.team, b.x, b.y, splashDamageRadius, StatusEffects.wet, 5 * 60, collidesAir, collidesGround);
        Damage.status(b.team, b.x, b.y, splashDamageRadius, StatusEffects.sapped, 5 * 60, collidesAir, collidesGround);
        if(!Vars.headless) despawnSound.at(b.x, b.y, 2);
        Effect.shake(5, 5, b);
        EUFx.layerCircle.at(b.x, b.y, splashDamageRadius, b.team.color);
        despawnEffect.at(b.x, b.y, b.team.color);
        if(!(b instanceof Arbiter a)) return;
        if(a.ens.size > 0){
            for(Bullet e : a.ens){
                e.initVel(e.rotation(), 3);
            }
        }
        if(a.ebs.size > 0){
            for(Bullet e : a.ebs){
                if(e.type != null)
                    e.initVel(e.rotation(), e.type.speed);
            }
        }
        if(a.ibs.size > 0){
            for(Bullet e : a.ibs){
                e.remove();
            }
        }
    }

    @Override
    public @Nullable Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ){
        Arbiter bullet = Arbiter.create();

        for(int i = 0; i < 6; i++){
            if (bullet.trails[i] != null) {
                bullet.trails[i].clear();
            }
        }

        bullet.ens.clear();
        bullet.ebs.clear();
        bullet.ibs.clear();

        return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public static class Arbiter extends Bullet {
        @Nullable
        public Trail[] trails = new Trail[6];

        public Seq<Bullet> ens = new Seq<>();
        public Seq<Bullet> ebs = new Seq<>();
        public Seq<Bullet> ibs = new Seq<>();

        public static Arbiter create() {
            return Pools.obtain(Arbiter.class, Arbiter::new);
        }
    }

    public static class EntBulletType extends BulletType{
        public EntBulletType(){
            lifetime = 120;
            speed = 0;
            damage = 200;
            splashDamageRadius = 8 * 8;
            splashDamage = 200;
            pierceArmor = true;
            homingRange = 24 * 8;
            homingDelay = 18;
            homingPower = 0.1f;

            absorbable = hittable = false;
            reflectable = false;
            pierce = true;
            pierceBuilding = true;
            buildingDamageMultiplier = 0.2f;

            trailLength = 10;

            despawnEffect = EUFx.layerCircle(45, splashDamageRadius);
            hitEffect = Fx.hitLaserBlast;
            hitSound = despawnSound = Sounds.explosion;
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if(b.time < homingDelay || !(b.data instanceof UnlockableContent)) return;
            Teamc target = Units.closestTarget(b.team, b.x, b.y, splashDamageRadius/3f,
                    e -> e != null && e.checkTarget(collidesAir, collidesGround),
                    t -> t != null && collidesGround);
            if(target != null) {
                b.remove();
            }
            if(b.timer.get(3, 30)) b.collided.clear();
        }

        @Override
        public void hit(Bullet b, float x, float y) {
            hitEffect.at(x, y, splashDamageRadius, b.team.color);
            if(Vars.headless) hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

            Effect.shake(1, 1, b);
            //createSplashDamage(b, x, y);
        }

        @Override
        public void draw(Bullet b) {
            if(!(b.data instanceof UnlockableContent content)) return;
            float z = Draw.z();
            Draw.z(Layer.flyingUnitLow);
            Draw.rect(content.fullIcon, b.x, b.y, b.rotation() - 90);
            Draw.z(z);
            super.draw(b);
        }

        @Override
        public void drawTrail(Bullet b) {
            if(!(b.data instanceof UnlockableContent content)) return;
            float size;
            if(content instanceof UnitType ut){
                size = ut.hitSize/2;
            } else if(content instanceof Block bk){
                size = bk.size * 4;
            } else {
                size = 0;
            }
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(Layer.flyingUnitLow - 1e-4f);
                b.trail.draw(b.team.color, size);
                Draw.z(z);
            }
        }

        @Override
        public void despawned(Bullet b) {
            if(!Vars.headless) despawnSound.at(b);
            createSplashDamage(b, b.x, b.y);
            despawnEffect.at(b.x, b.y, splashDamageRadius, b.team.color);
        }
    }
}
