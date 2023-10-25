package ExtraUtilities.worlds.entity.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

//是的，没做到完全还原，还在尽力

public class ScarletDevil extends BulletType {
    public BulletType fb, ff;
    public Color color;

    public ScarletDevil(Color color){
        this.color = color;
        fb  = new stopHoming(){{
            sColor = color;
            lifetime = 180;
            speed = 6;
            hitEffect = despawnEffect = Fx.none;
            trailLength = 15;
            trailWidth = 3;
            trailColor = color;
            keepVelocity = false;
            homingPower = Float.MAX_VALUE;
            homingRange = 40 * 8;
            healPercent = 2f;
            damage = 70;
            collidesTeam = true;
            reflectable = false;
            healColor = color;
            buildingDamageMultiplier = 0.7f;
        }};
        ff = new BulletType(){
            {
                lifetime = 90;
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
        for (int i = 0; i < fs; i++) {
            float r = (i + 1) * (360f / fs) - 30;
            ff.create(b, b.team, b.x, b.y, r + 30, -1, 1, 1, mov(r), mover);
        }
        for (int i = 0; i < fs; i++) {
            float r = (i + 1) * (360f / fs) - 30;
            ff.create(b, b.team, b.x, b.y, r - 150, -1, 1, 1, mov(r), mover);
        }
        for (int i = 0; i < fs; i++) {
            float r = i * (360f / fs) + 90;
            ff.create(b, b.team, b.x, b.y, r, -1, 1, 1, 2.2f, mover);
        }
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
            Draw.reset();
        }

        @Override
        public void updateHoming(Bullet b) {
            b.hit = b.time >= homingDelay;
            if(homingPower > 0.0001f && b.time >= homingDelay){
                float realAimX = b.aimX < 0 ? b.x : b.aimX;
                float realAimY = b.aimY < 0 ? b.y : b.aimY;

                Teamc target;
                if(heals()){
                    target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                            e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                            t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                    );
                }else{
                    if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                        target = b.aimTile.build;
                    }else{
                        target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                                e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                                t -> t != null && collidesGround && !b.hasCollided(t.id));
                    }
                }

                if(target != null){
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
                    b.initVel(b.rotation(), b.type.speed);
                } else {
                    b.initVel(b.rotation(), 0);
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
                u.damagePierce(b.damage * (1 + u.type.armor/10f));
            }
            else super.hitEntity(b, entity, health);
        }
    }
}
