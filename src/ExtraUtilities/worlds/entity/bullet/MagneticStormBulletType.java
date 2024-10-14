package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUBulletTypes;
import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;

import java.util.concurrent.atomic.AtomicBoolean;

import static ExtraUtilities.ExtraUtilitiesMod.*;

public class MagneticStormBulletType extends BasicBulletType {
    public Color cor;
    public Color liC;
    /** damage[min, mid, max, des]*/
    public float[] damages = {15, 18, 20, 32};

    public int msTl = 15;

    public float dsRange = 20 * 8;
    public ChainLightningFade lightning;

    private final Effect tef = new Effect(30, e -> {
        Draw.color(liC, cor, e.fin());
        float ex = e.x + Mathf.randomSeed(e.id, -4f, 4),
                ey = e.y + Mathf.randomSeed(e.id, -4, 4);
        Fill.circle(ex, ey, 4.2f * e.fout());
    });

    public MagneticStormBulletType(Color c1, Color c2){
        cor = c1;
        liC = c2;
        absorbable = false;

        Effect expEffSmall = new ExplosionEffect() {{
            lifetime = 20;
            waveLife = 12;
            waveStroke = 2;
            waveColor = liC;
            waveRad = 12;
            smokeSize = 2;
            smokes = 4;
            sparks = 3;
            sparkStroke = 1;
            sparkLen = 3;
            sparkRad = smokeRad = 15;
            smokeColor = cor;
            sparkColor = liC;
        }};

        intervalBullet = new BulletType(){{
            lifetime = 120;
            speed = 4;
            damage = 50;
            keepVelocity = false;
            trailColor = liC;
            trailWidth = 0;
            trailLength = 18;
            splashDamage = 50;
            splashDamageRadius = 15;
            hitEffect = expEffSmall;
            despawnEffect = Fx.none;
            buildingDamageMultiplier = 0.5f;
        }

            @Override
            public void update(Bullet b) {
                super.update(b);
                if(!(b instanceof mgs mgs)) return;
                var target = Units.closestTarget(b.team, b.x, b.y, 10 * 8f,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id));
                if(b.time < 30 || target == null){
                    b.initVel(b.rotation(), speed * 1.5f * Math.max(0, 1 - b.fin() * 3));
                } else {
                    b.initVel(b.angleTo(target), speed);
                }
                for(int i = 0; i < 2; i++){
                    if(!Vars.headless) {
                        if (mgs.trails[i] == null) mgs.trails[i] = new Trail(22);
                        mgs.trails[i].length = 22;
                    }
                    float dx = EUGet.dx(b.x, 10, (b.time * (8 - (i % 2 == 0 ? 0.6f : 0))) + Mathf.randomSeed(b.id, 360) + 180 * i),
                            dy = EUGet.dy(b.y, 10, (b.time * (8 - (i % 2 != 0 ? 0.6f : 0))) + Mathf.randomSeed(b.id, 360) + 180 * i);
                    if(!Vars.headless) mgs.trails[i].update(dx, dy, trailInterp.apply(b.fin()) * (1 + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0)));
                    if(mgs.vs[i] != null) mgs.vs[i].set(dx, dy);
                }
            }

            @Override
            public void draw(Bullet b) {
                super.draw(b);
                float vel = Math.max(0, b.vel.len()/speed);
                float out = b.time > b.lifetime - 12 ?  (b.lifetime - b.time)/12 : 1;

                Draw.color(trailColor);
                Drawf.tri(b.x, b.y, 4, 7 * vel, b.rotation());
                Fill.circle(b.x, b.y, 6 * (1 - vel) * out);

                if(!(b instanceof mgs mgs)) return;
                float z = Draw.z();
                Draw.z(z - 1e-4f);
                for(int i = 0; i < 2; i++){
                    if (msTl > 0 && mgs.trails[i] != null) {
                        mgs.trails[i].draw(i % 2 == 0 ? liC : cor, 1.2f * (1 - vel) * out);
                    }
                    if(mgs.vs[i] != null){
                        Draw.color(i % 2 == 0 ? liC : cor);
                        Fill.circle(mgs.vs[i].x, mgs.vs[i]. y, 1.2f * (1 - vel) * out);
                    }
                }
                Draw.z(z);
            }

            @Override
            public void drawTrail(Bullet b) {
                if(trailLength > 0 && b.trail != null){
                    float z = Draw.z();
                    Draw.z(z - 1e-4f);
                    b.trail.draw(trailColor, 3.1f);
                    Draw.z(z);
                }
            }

            @Override
            public void init(Bullet b) {
                super.init(b);
                if(!(b instanceof mgs mgs)) return;
                for(int i = 0; i < 2; i++){
                    mgs.vs[i] = new Vec2();
                }
            }

            @Override
            public boolean testCollision(Bullet bullet, Building tile) {
                return bullet.time > 30 && super.testCollision(bullet, tile);
            }

            @Override
            public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
                mgs bullet = mgs.create();

                for(int i = 0; i < 2; i++){
                    if (bullet.trails[i] != null) {
                        bullet.trails[i].clear();
                    }
                }
                return EUGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
            }
        };
        intervalBullets = 3;
        bulletInterval = 30;

        lightning = new ChainLightningFade(12, 12f, 2.5f, cor, 20, Fx.hitLancer){{
            buildingDamageMultiplier = 0.15f - (hardMod ? 0.1f : 0);
        }};

        despawnEffect = new MultiEffect(
                EUFx.StormExp(cor, liC)
        );

        fragBullet = new BulletType() {
                {
                    speed = 3.2f;
                    lifetime = 72;
                    damage = 20;
                    intervalBullets = 2;
                    intervalBullet = new ChainLightningFade(12, 12f, 2.5f, cor, 20, Fx.hitLancer) {{
                        despawnEffect = expEffSmall;
                        splashDamage = 30;
                        splashDamageRadius = 15;
                        buildingDamageMultiplier = 0.15f - (hardMod ? 0.1f : 0);
                        collidesTiles = false;
                        back = true;
                    }};
                    bulletInterval = 7.2f;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = Fx.none;
                    absorbable = hittable = keepVelocity = false;
                    pierce = true;
                    pierceBuilding = true;
                    trailWidth = 0;
                    trailLength = 25;
                    trailEffect = tef;
                    trailInterval = 1;

                    buildingDamageMultiplier = 0.5f;
                }

                @Override
                public void update(Bullet b) {
                    super.update(b);

                    //if(!(b.data instanceof Integer i)) return;
                    b.rotation(b.rotation() - Time.delta * 3.7f);
                }

                @Override
                public void updateBulletInterval(Bullet b) {
                    if (!(b.data instanceof Position pos)) return;
                    if (b.timer.get(2, bulletInterval)) {
                        float px = b.x + Mathf.random(-80, 80), py = b.y + Mathf.random(-80, 80);
                        intervalBullet.create(b, b.team, b.x, b.y, b.angleTo(pos), -1, 1, 1, pos);
                        intervalBullet.create(b, b.team, px, py, b.angleTo(px, py) + 180, -1, 1, 1, b);

                        Sounds.spark.at(b.x, b.y, 3f, 0.4f);
                    }
                }

            @Override
            public void drawTrail(Bullet b) {
                if (trailLength > 0 && b.trail != null) {
                    float d = b.time < b.lifetime /2 ? b.fin() * 2 : b.fout() * 2;
                    float z = Draw.z();
                    Draw.z(z - 1e-4f);
                    b.trail.draw(Tmp.c4.set(liC).lerp(cor, b.fin()), 3.5f * d + 1);
                    Draw.z(z);
                }
            }

            @Override
            public void draw(Bullet b) {
                super.draw(b);

                float d = b.time < b.lifetime /2 ? b.fin() * 2 : b.fout() * 2;
                Draw.color(liC, cor, b.fin());
                Fill.circle(b.x, b.y, 2.5f * d + 1);
            }
        };
        fragBullets = 6;
    }
    public MagneticStormBulletType(){
    }

    @Override
    public void update(Bullet b) {
        updateHoming(b);
        boolean able = !Vars.headless && msTl > 0;

        if(!(b instanceof mgs mgs)) return;
        int p = 0;
        for(int s = 0; s < 3; s ++) {
            for (int a : Mathf.signs) {
                if(able) {
                    if (mgs.trails[p] == null) mgs.trails[p] = new Trail(msTl);
                    mgs.trails[p].length = msTl;
                }

                float dx = EUGet.dx(b.x, (40 - 12 * s), (b.time * 6) * a + Mathf.randomSeed(b.id, 360) + 120 * s),
                        dy = EUGet.dy(b.y, (40 - 12 * s), (b.time * 5.8f) * a + Mathf.randomSeed(b.id, 360) + 120 * s);
                if(able) mgs.trails[p].update(dx, dy, trailInterp.apply(b.fin()) * (1 + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0)));

                if(mgs.vs[p] != null) mgs.vs[p].set(dx, dy);

                p++;
            }
        }

        if(b.timer.get(1, 12)){
            for(int i = 0; i < 6; i++){
                var v = mgs.vs[i];
                if(v != null){
                    Lightning.create(b.team, i % 2 == 0 ? liC : cor, damages[1], v.x, v.y, v.angleTo(b), (int) v.dst(b)/Vars.tilesize + 1);
                    Lightning.create(b.team, i % 2 == 0 ? cor : liC, damages[2], b.x, b.y, b.angleTo(v), (int) v.dst(b)/Vars.tilesize + 1);
                }

                Lightning.create(b.team, cor, damages[3], b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(3,7));
            }
        }

        if(b.timer.get(2, 9)) {
            Groups.bullet.intersect(b.x - dsRange, b.y - dsRange, dsRange * 2, dsRange * 2, ob -> {
                if (ob instanceof mgs && ob.within(b, dsRange) && ob.team == b.team) {
                    float bdx = b.x + Mathf.random(-20, 20), bdy = b.y + Mathf.random(-20, 20);
                    lightning.create(b, b.team, bdx, bdy, ob.angleTo(bdx, bdy) + 180, -1, 1, 1, ob);
                }
            });
            Sounds.spark.at(b.x, b.y, 3f, 0.4f);
        }

        if(b.timer.get(3, bulletInterval)){
            for(int i = 0; i < intervalBullets; i++) {
                intervalBullet.create(b, b.x, b.y, Mathf.random(360));
            }
        }
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(!(b instanceof mgs mgs)) return;
        for(int i = 0; i < 6; i++){
            mgs.vs[i] = new Vec2();
        }
    }

    @Override
    public void draw(Bullet b) {
        int plasmas = 6;
        for(int i = 0; i < plasmas; i++){
            Draw.color(liC, cor, i / 6f);
            Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)));
            Draw.rect(Core.atlas.find( ModName + "-plasma-" + i), b.x, b.y, width, height, Time.time * (12 + i * 6));
        }

        if(!(b instanceof mgs mgs)) return;
        float z = Draw.z();
        Draw.z(z - 1e-4f);
        for(int i = 0; i < 6; i++){
            if (msTl > 0 && mgs.trails[i] != null) {
                mgs.trails[i].draw(i % 2 == 0 ? liC : cor, 2.5f * b.fout() + 2f);
            }
            if(mgs.vs[i] != null){
                Draw.color(i % 2 == 0 ? liC : cor);
                Fill.circle(mgs.vs[i].x, mgs.vs[i]. y, 1.5f * b.fout() + 2f);
            }
        }
        Draw.z(z);
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        var psb = EUBulletTypes.setPos.create(b, b.x, b.y, 0);
        for(int i = 0; i < fragBullets; i++){
            float a = Mathf.randomSeed(b.id, 360) + 60 * i;
            fragBullet.create(b, b.team, b.x, b.y, a, -1, 1, 1, psb);
        }
    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        mgs bullet = mgs.create();

        for(int i = 0; i < 6; i++){
            if (bullet.trails[i] != null) {
                bullet.trails[i].clear();
            }
        }
        return EUGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class mgs extends Bullet{
        @Nullable
        public Trail[] trails = new Trail[6];

        public Vec2[] vs = new Vec2[6];

        public static mgs create() {
            return Pools.obtain(mgs.class, mgs::new);
        }
    }
}
