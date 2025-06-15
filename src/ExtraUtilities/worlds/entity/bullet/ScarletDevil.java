package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUStatusEffects;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class ScarletDevil extends BulletType {
    public BulletType fb, ff;
    public Color color;

    public ScarletDevil(Color color){
        this.color = color;
        fb  = new stopHoming(){{
            sColor = color;
            lifetime = 180;
            speed = 6;
            hitEffect = despawnEffect = new Effect(24, e -> {
                Draw.color(color);
                Angles.randLenVectors(e.id, 7, 32 * e.finpow(), e.rotation, 0, (x, y) -> Fill.square(e.x + x, e.y + y, 6 * e.foutpow()));
            });;
            trailLength = 15;
            trailWidth = 3;
            trailColor = color;
            keepVelocity = false;
            homingPower = 0.08f;
            homingRange = 40 * 8;
            healPercent = 2f;
            damage = 55;
            collidesTeam = true;
            reflectable = false;
            healColor = color;
            buildingDamageMultiplier = 0.7f;
        }};
        ff = new BulletType(){
            {
                lifetime = 63;
                speed = 3;
                hitEffect = despawnEffect = Fx.none;
                trailLength = 15;
                trailWidth = 3;
                trailColor = color;
                keepVelocity = false;
                damage = 0;
                collides = false;
                collidesAir = collidesGround = false;
                absorbable = false;
                reflectable = false;
            }
            @Override
            public void draw(Bullet b) {
                super.draw(b);
                Draw.z(Layer.bullet);
                Draw.color(color);
                Fill.circle(b.x, b.y, 3);
                Draw.color(Color.white);
                Fill.circle(b.x, b.y, 2);
                Draw.reset();
            }
            @Override
            public void despawned(Bullet b) {
                fb.create(b, b.x, b.y, 0);
            }
        };
        healPercent = 10;
        splashDamageRadius = 10 * 8;
        despawnHit = true;
        keepVelocity = false;
        hitEffect = despawnEffect = new ExplosionEffect(){{
            lifetime = 30f;
            waveStroke = 5f;
            waveLife = 10f;
            waveRad = splashDamageRadius;
            waveColor = color;
            sparkColor = color;
            smokes = 0;
            sparks = 30;
            sparkRad = splashDamageRadius;
            sparkLen = 6f;
            sparkStroke = 1f;
        }};
        fragBullet = fb;
        fragBullets = 42 * 3;
        reflectable = false;
    }

    Mover mover = bullet -> {
        if(!(bullet.data instanceof Float)) return;
        float s = (float) bullet.data;
        float fout = Math.max((60 - bullet.time)/60, 0);
        if(bullet.time < 70) bullet.initVel(bullet.rotation(), bullet.type.speed * fout * s);
    };

    public float mov(float r){
        return (Mathf.cos((float) (3 * (r/180 * Math.PI))) + 1f);
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        int fs = fragBullets/3;
        int rdp = Mathf.randomSeed(b.id, 1, 2);
        for (int i = 0; i < fs; i++) {
            float r = (i + 1) * (360f / (fs * rdp)) - 30;
            ff.create(b, b.team, b.x, b.y, r + 30, -1, 1, 1, mov(r), mover);
        }
        for (int i = 0; i < fs; i++) {
            float r = (i + 1) * (360f / (fs * rdp)) - 30;
            ff.create(b, b.team, b.x, b.y, r - 150, -1, 1, 1, mov(r), mover);
        }
        for (int i = 0; i < fs; i++) {
            float r = i * (360f / fs) + 90;
            ff.create(b, b.team, b.x, b.y, r, -1, 1, 1, 2.2f, mover);
        }
    }

    @Override
    public void removed(Bullet b) {

    }

    public static class stopHoming extends BulletType{
        public Color sColor;

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            Draw.z(Layer.bullet);
            Draw.color(sColor);
            Fill.circle(b.x, b.y, 3);
            Draw.color(Color.white);
            Fill.circle(b.x, b.y, 2);
            //Draw.reset();
        }

        @Override
        public void updateHoming(Bullet b) {
            b.hit = b.time >= homingDelay;
            if(homingPower > 0.0001f && b.time >= homingDelay){
                float realAimX = b.aimX < 0 ? b.x : b.aimX;
                float realAimY = b.aimY < 0 ? b.y : b.aimY;

                if(b.timer(2, 12)) {
                    Teamc target = null;
                    if (heals()) {
                        target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                                e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                                t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                        );
                    } else {
                        if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                            target = b.aimTile.build;
                        } else {
                            target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                                    e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                                    t -> t != null && collidesGround && !b.hasCollided(t.id));
                        }
                    }

                    if (target != null) {
                        b.initVel(b.angleTo(target), b.type.speed);
                    } else {
                        b.initVel(b.rotation(), 0);
                    }
                }
            }
        }

        @Override
        public void hitEntity(Bullet b, Hitboxc entity, float health) {
            if(entity instanceof Unit u && u.type != null) {
                if(u.type.armor > 80){
                    u.health -= u.health;
                    u.kill();
                }
                float dmg = b.damage * (1 + u.type.armor/10f);
                u.damagePierce(dmg);
                if(u.hasEffect(EUStatusEffects.breakage)){
                    if(u.health <= dmg * 0.5f) u.kill();
                    else u.health -= dmg * 0.5f;
                }
            }
            else super.hitEntity(b, entity, health);
        }

        @Override
        public void removed(Bullet b) {

        }
    }
}
