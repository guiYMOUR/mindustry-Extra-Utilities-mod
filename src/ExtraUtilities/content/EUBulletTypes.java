package ExtraUtilities.content;

import ExtraUtilities.ui.EUI;
import ExtraUtilities.worlds.entity.bullet.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.Turret;

import javax.sound.sampled.Line;

import static ExtraUtilities.ExtraUtilitiesMod.hardMod;
import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.headless;
import static mindustry.Vars.indexer;
import static mindustry.content.Fx.rand;

public class EUBulletTypes {
    public static BulletType b1 = new mixBoom(Pal.bulletYellowBack){{
        fragBullet = new mixExps(){{
            lifetime = 20;
            speed = 2.8f;
            trailColor = color = Pal.bulletYellowBack;
            damage = 0;
            splashDamage = 32;
            splashDamageRadius = 2.8f * 8;
            trailWidth = 4;
            trailLength = 8;
            hitEffect = despawnEffect = new ExplosionEffect(){{
                lifetime = 30f;
                waveRad = splashDamageRadius;
                waveColor = sparkColor = smokeColor = Pal.bulletYellowBack;
                smokeSize = 0;
                smokeSizeBase = 0;
                smokes = 0;
                sparks = 3;
                sparkRad = splashDamageRadius;
                sparkLen = 2f;
                sparkStroke = 1.5f;
            }};
        }};
        fragBullets = 7;
        fragAngle = 15;
        fragSpread = 30;
        fragRandomSpread = 0;
        speed = 8;

        aimTo = true;
    }};

    public static BulletType b2 = new mixBoom(EUItems.crispSteel.color){{
        fragBullet = new mixExps(){{
            lifetime = 20;
            speed = 3f;
            trailColor = color = EUItems.crispSteel.color;
            damage = 0;
            splashDamage = 28;
            splashDamageRadius = 4.1f * 8;
            status = StatusEffects.freezing;
            trailWidth = 4;
            trailLength = 8;
            hitEffect = despawnEffect = new ExplosionEffect(){{
                lifetime = 30f;
                waveRad = splashDamageRadius;
                waveColor = sparkColor = smokeColor = EUItems.crispSteel.color;
                smokeSize = 2;
                smokeSizeBase = 0;
                smokes = 4;
                sparks = 5;
                sparkRad = splashDamageRadius;
                sparkLen = 2f;
                sparkStroke = 1.5f;
            }};
        }};
        fragBullets = 10;
        speed = 6;
    }};

    public static BulletType b3 = new mixBoom(Items.pyratite.color){{
        fragBullet = new mixExps(){{
            lifetime = 20;
            speed = 2.1f;
            trailColor = color = Items.pyratite.color;
            damage = 0;
            splashDamage = 36;
            splashDamageRadius = 3.5f * 8;
            status = StatusEffects.burning;
            incendChance = 0.5f;
            trailWidth = 4;
            trailLength = 8;
            hitEffect = despawnEffect = new ExplosionEffect(){{
                lifetime = 30f;
                waveRad = splashDamageRadius;
                waveColor = sparkColor = smokeColor = Items.pyratite.color;
                smokeSize = 2.2f;
                smokeSizeBase = 0;
                smokes = 6;
                sparks = 3;
                sparkRad = splashDamageRadius;
                sparkLen = 2f;
                sparkStroke = 1.5f;
            }};
        }};
        fragBullets = 9;
        speed = 5;
    }};

    public static BulletType b4 = new mixBoom(Items.surgeAlloy.color){{
        fragBullet = new mixExps(){{
            circle = false;

            lifetime = 20;
            speed = 3.5f;
            trailColor = color = Items.surgeAlloy.color;
            damage = 0;
            splashDamage = 30;
            splashDamageRadius = 3f * 8;
            status = StatusEffects.shocked;
            trailWidth = 3;
            trailLength = 8;
            hitEffect = despawnEffect = new ExplosionEffect(){{
                lifetime = 30f;
                waveRad = splashDamageRadius;
                waveColor = sparkColor = smokeColor = Items.surgeAlloy.color;
                smokeSize = 3;
                smokeSizeBase = 0;
                smokes = 5;
                sparks = 0;
            }};
            lightningDamage = 10;
            lightning = 1;
            lightningLength = 4;
        }};

        fragBullets = 10;
        fragAngle = 15;
        fragSpread = 36;
        fragRandomSpread = 0;

        lightningDamage = 20;
        lightning = 4;
        lightningLength = 10;

        speed = 6;
    }};

    public static BulletType ib = new mixBoom.mixExps(){{
        lifetime = 50;
        speed = 4f;
        size = 8;
        minSize = 3;
        trailColor = color = EUItems.lightninAlloy.color;
        damage = 0;
        splashDamage = 80;
        splashDamageRadius = 5.5f * 8;
        trailWidth = 7;
        trailLength = 15;
        hitEffect = despawnEffect = new ExplosionEffect(){{
            lifetime = 30f;
            waveRad = splashDamageRadius;
            waveColor = sparkColor = smokeColor = EUItems.lightninAlloy.color;
            smokeSize = 2.2f;
            smokeSizeBase = 0;
            smokes = 6;
            sparks = 3;
            sparkRad = splashDamageRadius;
            sparkLen = 2f;
            sparkStroke = 1.5f;
        }};

        lightning = 3;
        lightningColor = EUItems.lightninAlloy.color;
        lightningDamage = 30;
        lightningLength = 7;
    }};
    
    public static Color c1 = Pal.thoriumPink;
    public static BulletType rankB = new BasicBulletType(){{
        speed = 2;
        lifetime = 216;
        damage = 31;
        buildingDamageMultiplier = 0.5f;
        width = 10;
        height = 7;
        shrinkY = 0;
        despawnEffect = EUFx.gone(c1);
        backColor = c1;
        frontColor = c1;
    }
        @Override
        public void update(Bullet b){
            b.vel.trns(b.rotation(), this.speed * Math.max(0, b.fout() - 0.5f));
        }
    };

    public static BulletType suk = new BasicBulletType(){{
        speed = 2;
        lifetime = 115;
        damage = 52;
        buildingDamageMultiplier = 0.5f;
        width = 32;
        height = 18;
        shrinkY = 0;
        pierce = true;
        pierceBuilding = true;
        backColor = c1;
        frontColor = c1;
        fragBullets = 5;
        fragRandomSpread = 0;
        fragVelocityMin = 1;
        fragSpread = 360f/fragBullets;

        intervalBullets = fragBullets;
        intervalRandomSpread = fragRandomSpread;
        intervalSpread = fragSpread;
        bulletInterval = lifetime/4;

        intervalBullet = fragBullet = rankB;

        hitEffect = despawnEffect = EUFx.gone(c1);

        fragOnHit = false;
    }};

    public static BulletType expFlame(float range, float slpRange){
        return new FlameBulletType(Items.blastCompound.color.cpy().mul(Pal.lightFlame), Items.blastCompound.color.cpy(), Pal.lightishGray, range + 8, 22, 66, 30){{
            damage = 90;
            status = EUStatusEffects.flamePoint;
            statusDuration = 8 * 60f;
            reloadMultiplier = 0.5f;
            //ammoMultiplier = 1;
        }
            @Override
            public void hit(Bullet b) {
                if(absorbable && b.absorbed) return;
                //unit▼
                Units.nearbyEnemies(b.team, b.x, b.y, flameLength, unit -> {
                    if(Angles.within(b.rotation(), b.angleTo(unit), flameCone) && unit.checkTarget(collidesAir, collidesGround)){
                        Fx.hitFlameSmall.at(unit);
                        unit.damage(damage);
                        if(unit.hasEffect(status)){
                            Damage.damage(b.team, unit.x, unit.y, slpRange, damage/3f, false, true);
                            Damage.status(b.team, unit.x, unit.y, slpRange, status, statusDuration, false, true);
                            EUFx.easyExp.at(unit.x, unit.y, slpRange, Items.blastCompound.color);
                            unit.unapply(status);
                        } else {
                            unit.apply(status, statusDuration);
                        }

                    }
                });
                //block▼
                indexer.allBuildings(b.x, b.y, flameLength, other -> {
                    if(other.team != b.team && Angles.within(b.rotation(), b.angleTo(other), flameCone)){
                        Fx.hitFlameSmall.at(other);
                        other.damage(damage * buildingDamageMultiplier);
                    }
                });
            }
        };
    }

    public static BulletType[] rainbowLar = new BulletType[120];
    static {
        StatusEffect[] rainStatus = {
                StatusEffects.blasted,
                StatusEffects.electrified,
                StatusEffects.wet,
        };
        for(int i = 0; i < 120; i++){
            Color rbc = EUGet.rainBowRed.cpy().shiftHue(i * 3);

            int finalI = i;
            BulletType ribt = new LaserBulletType(){{
                laserAbsorb = false;
                damage = 1;
                buildingDamageMultiplier = 0.2f;
                pierceArmor = true;
                colors = new Color[]{rbc.cpy().a(0.4f), rbc.cpy().a(0.7f), rbc};
                lifetime = 18;
                length = 60f;
                laserEffect = Fx.none;
                status = rainStatus[finalI % 3];
                statusDuration = 2 * 60f;

                hitEffect = new Effect(24, e -> {
                    if(e.color != null) Draw.color(e.color.set(EUGet.rainBowRed).shiftHue(e.time * 20));
                    Angles.randLenVectors(e.id, 3, 32 * e.finpow(), e.rotation, 90, (x, y) -> Fill.square(e.x + x, e.y + y, 6 * e.foutpow()));
                });
                //hitEffect = Fx.none;
            }
                @Override
                public void init(Bullet b){
                    Damage.collideLaser(b, b.fdata, largeHit, laserAbsorb, pierceCap);
                }

                @Override
                public void draw(Bullet b){
                    float realLength = b.fdata;

                    float f = Mathf.curve(b.fin(), 0f, 0.2f);
                    float baseLen = realLength * f;
                    float cwidth = b.fdata > 50 ? 13f : b.fdata/5f;

                    for(Color color : colors){
                        Draw.color(color);
                        Lines.stroke((cwidth *= 0.5f) * b.fout());
                        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
                        Tmp.v1.trns(b.rotation(), baseLen);
                        Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), cwidth * 2f + b.fdata / 10f, b.rotation());
                    }
                    Draw.reset();
                }
            };

            rainbowLar[i] = ribt;
        }
    }

    private static final BulletType rainbowStormSmall = new RainbowStorm(8, 10){{
        homingRange = 160;
        homingPower = 0.5f;
        lifetime = 90;
        speed = 4.5f;
        inAmi = false;
        homingDelay = 15;
        sideTrailWidth = 2f;
        trailLength = 15;
        laserLength = 13;
        laserAmount = 15;
    }};
    public static BulletType setPos = new BulletType(){{
        speed = damage = 0;
        hittable = absorbable = keepVelocity = collides = collidesTiles = collidesAir = collidesGround = false;
        hitEffect = despawnEffect = Fx.none;
        hitSize = 0;
        lifetime = 80;
    }};
    public static BulletType rainbowStorm = new RainbowStorm(){{
        homingRange = 120;
        homingPower = 0.4f;
        homingDelay = 30f;
        lifetime = 180;
        speed = 3f;
        trailLength = 18;

        splashDamage = 300;
        splashDamageRadius = 12 * 8f;

        rotSpeed = -2.8f;

        Color cor = Color.valueOf("FFF8D8");
        Color liC = Color.valueOf("FFE5C0");

        despawnEffect = new MultiEffect(
                EUFx.StormExp(liC, cor)
        );

        Effect tef = new Effect(30, e -> {
            Draw.color(liC, cor, e.fin());
            float ex = e.x + Mathf.randomSeed(e.id, -4f, 4),
                    ey = e.y + Mathf.randomSeed(e.id, -4, 4);
            Fill.circle(ex, ey, 4.2f * e.fout());
        });
        fragBullet = new BulletType() {
            {
                speed = 3.2f;
                lifetime = 72;
                damage = 20;
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
                b.rotation(b.rotation() - Time.delta * 3.7f);
            }

            @Override
            public void updateBulletInterval(Bullet b) {
                if(b.data instanceof Integer data) {
                    if (b.timer.get(2, bulletInterval)) {
                        int index = ((int) (b.time + data * 3.3f)) % 120;
                        Bullet rb = rainbowLar[index].create(b, b.team, b.x, b.y, b.rotation() - 90, 45, 1, 1, null);
                        rb.fdata = 120;
                    }
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

        @Override
        public void createFrags(Bullet b, float x, float y) {
            //var psb = setPos.create(b, b.x, b.y, 0);

            for(int i = 0; i < fragBullets; i++){
                float a = Mathf.randomSeed(b.id, 360) + 60 * i;
                fragBullet.create(b, b.team, b.x, b.y, a, -1, 1, 1, i);
            }
        }

        @Override
        public void updateHoming(Bullet b) {
            if(homingPower > 0.0001f && b.time >= homingDelay) {
                float realAimX = b.aimX < 0 ? b.x : b.aimX;
                float realAimY = b.aimY < 0 ? b.y : b.aimY;

                Teamc target;
                if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                    target = b.aimTile.build;
                } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                            e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                            t -> t != null && collidesGround && !b.hasCollided(t.id));
                }

                if (target != null) {
                    if(b.time < (b.lifetime - outTime) && b.timer.get(1, 21)) rainbowStormSmall.create(b, b.x, b.y, b.rotation() -90 + Mathf.random(120f));
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
                }
            }
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if(b.owner instanceof Turret.TurretBuild build){
                if(!b.within(build, build.range())) b.remove();
            }
        }
    };

    public static BulletType effectForPenitent = new BulletType(){{
        speed = 0;
        collidesGround = hittable = absorbable = false;
        collidesAir = true;
        pierce = true;
        damage = 90;
        lifetime = 60;
        pierceArmor = true;
        trailColor = EUItems.lightninAlloy.color;
        trailLength = 14;
        trailWidth = 2f;
        hitEffect = new Effect(24, e -> {
            Draw.color(EUItems.lightninAlloy.color);
            Angles.randLenVectors(e.id, 7, 32 * e.finpow(), e.rotation, 0, (x, y) -> Fill.square(e.x + x, e.y + y, 8 * e.foutpow()));
        });
        despawnEffect = Fx.none;
    }

        @Override
        public void update(Bullet b) {
            if(b.timer.get(2, 12)) b.collided.clear();
            super.update(b);
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            Draw.color(trailColor);
            Drawf.tri(b.x, b.y, 3f, 7, b.rotation());
            Drawf.tri(b.x, b.y, 2f, 5.3f, b.rotation() + 150);
            Drawf.tri(b.x, b.y, 2f, 5.3f, b.rotation() - 150);
        }

        @Override
        public void removed(Bullet b) {

        }
    };

    static BulletType bit = new FawnFarsiaBit(){{
        homingRange = 15 * 8;
        Effect initE = EUFx.chainLightningFade(11 * 2, 1.3f, 11 * 4).layer(Layer.bullet - 0.1f);
        buildingDamageMultiplier = 0.2f;

        BulletType miss = new BulletType(){{
            damage = 50;
            pierceArmor = true;
            keepVelocity = hittable = absorbable = false;
            lifetime = 60;
            speed = 8;
            trailWidth = 2;
            trailLength = 12;
            trailColor = EUItems.lightninAlloy.color.cpy();
            buildingDamageMultiplier = 0.2f;
        }

            @Override
            public void update(Bullet b) {
                super.update(b);

                if(b.time < 30){
                    float fin = Math.min(1, b.time/30);
                    float fout = 1 - fin;
                    b.initVel(b.rotation(), fout * 4);
                } else {
                    if(b.data instanceof Unit u) {
                        b.rotation(b.angleTo(u));
                        b.initVel(b.rotation(), speed);
                    }
                }
            }

            @Override
            public void draw(Bullet b) {
                super.draw(b);
                Draw.color(trailColor);
                Fill.circle(b.x, b.y, 2);
            }

            @Override
            public void removed(Bullet b) {

            }
        };

        intervalBullet = new BulletType(){{
            damage = 50;
            buildingDamageMultiplier = 0.1f;
            speed = 16;
            pierce = true;
            pierceBuilding = true;
            lifetime = 11f;
            trailWidth = 2;
            trailLength = 12;
            trailColor = EUItems.lightninAlloy.color.cpy();
            keepVelocity = absorbable = hittable = false;
            hitEffect = despawnEffect = new Effect(21, e -> {
                Draw.color(trailColor);
                Angles.randLenVectors(e.id, 5, 16 * e.finpow(), e.rotation, 360, (x, y) -> Fill.square(e.x + x, e.y + y, 9 * e.foutpow()));
            });
        }


            @Override
            public void init(Bullet b) {
                super.init();
                float ex, ey;
                ex = b.x + Angles.trnsx(b.rotation(), speed * b.lifetime);
                ey = b.y + Angles.trnsy(b.rotation(), speed * b.lifetime);
                float len = Mathf.dst(b.x, b.y, ex, ey);
                float angle = Angles.angle(b.x, b.y, ex, ey);
                initE.at(b.x, b.y, angle, trailColor, len);
            }

            @Override
            public void draw(Bullet b) {
                super.draw(b);
                Draw.color(trailColor);
                Drawf.tri(b.x, b.y, 6, 8, b.rotation());
            }

            @Override
            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                super.hitEntity(b, entity, health);
                if(entity instanceof Unit u){
                    if(!u.dead && u.hasEffect(EUStatusEffects.awsl)){
                        if(b.owner instanceof Bit bit) {
                            miss.create(b, b.team, bit.x, bit.y, bit.rotation() - 72, -1, 1, 1, u);
                            miss.create(b, b.team, bit.x, bit.y, bit.rotation() + 72, -1, 1, 1, u);
                        }
                    }
                }
            }

            @Override
            public void removed(Bullet b) {

            }
        };
        bulletInterval = 30;
        intervalBullets = 1;

        lifetime = 360;
        ready = 60;

        hittable = true;

        trailColor = EUItems.lightninAlloy.color.cpy();

        damage = splashDamage = 300;
        splashDamageRadius = 5 * 8f;

        hitEffect = despawnEffect = EUFx.layerCircle(15, splashDamageRadius, EUItems.lightninAlloy.color);

        drawer = b -> {
            float fout = 1 - Math.min(1, b.time/ready);
            float tr = b.rotation() + 360 * 4 * Interp.slowFast.apply(fout);

            Draw.color(trailColor);

            Drawf.tri(b.x, b.y, 10, 5, tr);
            float dx = EUGet.dx(b.x, 5.5f, tr - 180),
                    dy = EUGet.dy(b.y, 5.5f, tr - 180);
            Fill.poly(dx, dy, 4, 5, tr + 45);

            dx = EUGet.dx(b.x, 7f, tr - 135);
            dy = EUGet.dy(b.y, 7f, tr - 135);
            Drawf.tri(dx, dy, 9, 4, tr - 90);

            dx = EUGet.dx(b.x, 7f, tr + 135);
            dy = EUGet.dy(b.y, 7f, tr + 135);
            Drawf.tri(dx, dy, 9, 4, tr + 90);

            Draw.color();
        };

        shootSound = Sounds.malignShoot;
        vol = 0.6f;
        pit = 1.2f;
        shootBullet = (b, s) -> {
            float dx = EUGet.dx(b.x, 7, b.rotation()),
                    dy = EUGet.dy(b.y, 7, b.rotation());
            s.create(b, b.team, dx, dy, b.rotation());
        };

        approach = b -> {
            if(b.target != null){
                b.rotation(b.angleTo(b.target));
                float dx = EUGet.dx(b.target.x(), homingRange/1.5f, b.ang),
                        dy = EUGet.dy(b.target.y(), homingRange/1.5f, b.ang);

                EUGet.movePoint(b, dx, dy, speed/100f);
            }
        };
    }};

    static int msTl = 15;
    static float dsRange = 8 * 8;
    static ChainLightningFade lightningFade = new ChainLightningFade(12, 14f, 2f, Pal.surge, 20, Fx.hitLancer){{
        buildingDamageMultiplier = 0.1f;
    }};;
    static Effect expEffSmall = new ExplosionEffect() {{
        lifetime = 20;
        waveLife = 12;
        waveStroke = 2;
        waveColor = EUItems.lightninAlloy.color.cpy();;
        waveRad = 16;
        smokeSize = 2;
        smokes = 4;
        sparks = 3;
        sparkStroke = 1;
        sparkLen = 3;
        sparkRad = smokeRad = 18;
        smokeColor = Pal.surge.cpy();
        sparkColor = EUItems.lightninAlloy.color.cpy();;
    }};
    static BulletType eBall = new BulletType(){{
        buildingDamageMultiplier = 0.2f;
        lifetime = 120;
        speed = 4;
        damage = 50;
        keepVelocity = false;
        trailColor = EUItems.lightninAlloy.color.cpy();
        trailWidth = 0;
        trailLength = 18;
        splashDamage = 50;
        splashDamageRadius = 16;
        hitEffect = expEffSmall;
        despawnEffect = Fx.none;
        buildingDamageMultiplier = 0.2f;
    }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if(!(b instanceof MagneticStormBulletType.mgs mgs)) return;
            Teamc target = Units.closestTarget(b.team, b.x, b.y, 10 * 8f,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id));
            if(b.time < 30 || target == null){
                b.initVel(b.rotation(), speed * 0.4f * Math.max(0, 1 - b.fin() * 3));
            } else {
                b.initVel(b.angleTo(target), speed);
            }
            for(int i = 0; i < 2; i++){
                if(!Vars.headless) {
                    if (mgs.trails[i] == null) mgs.trails[i] = new Trail(22);
                    mgs.trails[i].length = 22;
                }
                float dx = EUGet.dx(b.x, 5, (b.time * (8 - (i % 2 == 0 ? 0.6f : 0))) + Mathf.randomSeed(b.id, 360) + 180 * i),
                        dy = EUGet.dy(b.y, 5, (b.time * (8 - (i % 2 != 0 ? 0.6f : 0))) + Mathf.randomSeed(b.id, 360) + 180 * i);
                if(!Vars.headless) mgs.trails[i].update(dx, dy, trailInterp.apply(b.fin()) * (1 + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0)));
                if(mgs.vs[i] != null) mgs.vs[i].set(dx, dy);
            }

            if(b.timer.get(2, 15)) {
                Groups.bullet.intersect(b.x - dsRange, b.y - dsRange, dsRange * 2, dsRange * 2, ob -> {
                    if (ob instanceof MagneticStormBulletType.mgs && ob.within(b, dsRange) && ob.team == b.team && ob != b) {
                        float bdx = b.x + Mathf.random(-4, 4), bdy = b.y + Mathf.random(-4, 4);
                        lightningFade.create(b, b.team, bdx, bdy, ob.angleTo(bdx, bdy) + 180, -1, 1, 1, ob);
                    }
                });
            }
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            float vel = Math.max(0, b.vel.len()/speed);
            float out = b.time > b.lifetime - 12 ?  (b.lifetime - b.time)/12 : 1;

            Draw.color(trailColor);
            Drawf.tri(b.x, b.y, 3.5f, 6.5f * vel, b.rotation());
            Fill.circle(b.x, b.y, 4 * (1 - vel) * out);

            if(!(b instanceof MagneticStormBulletType.mgs mgs)) return;
            float z = Draw.z();
            Draw.z(z - 1e-4f);
            for(int i = 0; i < 2; i++){
                if (msTl > 0 && mgs.trails[i] != null) {
                    mgs.trails[i].draw(i % 2 == 0 ? trailColor : Pal.surge, 1.2f * (1 - vel) * out);
                }
                if(mgs.vs[i] != null){
                    Draw.color(i % 2 == 0 ? trailColor : Pal.surge);
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
                b.trail.draw(trailColor, 2.9f);
                Draw.z(z);
            }
        }

        @Override
        public void init(Bullet b) {
            super.init(b);
            if(!(b instanceof MagneticStormBulletType.mgs mgs)) return;
            for(int i = 0; i < 2; i++){
                mgs.vs[i] = new Vec2();
            }
        }

        @Override
        public boolean testCollision(Bullet bullet, Building tile) {
            return bullet.time > 30 && super.testCollision(bullet, tile);
        }

        @Override
        public @Nullable Bullet create(
                @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
                float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
        ){
            MagneticStormBulletType.mgs bullet = MagneticStormBulletType.mgs.create();

            for(int i = 0; i < 2; i++){
                if (bullet.trails[i] != null) {
                    bullet.trails[i].clear();
                }
            }
            return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
        }
    };

    public static BulletType foreshadowEUBullet = new BulletType(){{
        damage = 500;
        splashDamage = 700;
        splashDamageRadius = 9 * 8f;
        buildingDamageMultiplier = 0.25f;
        fragBullet = bit;
        fragBullets = 3;

        hittable = absorbable = false;

        intervalBullet = eBall;
        intervalBullets = 2;
        bulletInterval = 3;
        intervalRandomSpread = 0;
        intervalSpread = 180;

        trailLength = 10;
        trailWidth = 5;
        trailColor = EUItems.lightninAlloy.color.cpy();
        int[] side = {-8, 8};
        trailEffect = new Effect(30, e ->{
            color(e.color);
            for(int x : side){
                Tmp.v1.set(x, -3).rotate(e.rotation - 90);
                Lines.stroke(2.5f * e.foutpow());
                Lines.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 6, 5f * e.foutpow(), e.time * 5);
            }
        });
        trailRotation = true;
        trailInterval = 0.5f;
        speed = 30;
        lifetime = 15;
        status = EUStatusEffects.awsl;
        statusDuration = 30;
        Effect e1 = new Effect(21, e -> {
            Angles.randLenVectors(e.id, 5, splashDamageRadius * e.finpow(), e.rotation, 180, (x, y) -> {
                Lines.stroke(3 * e.foutpow(), trailColor);
                Lines.poly(e.x + x, e.y + y, 6, 5, Mathf.randomSeed(e.id, 360) + Time.delta);
            });
        });
        Effect e2 = new Effect(50, e -> {
            rand.setSeed(e.id);
            for(int i = 0; i < 5; i++){
                float a = e.rotation + rand.random(-60, 60);
                Draw.color(trailColor);
                Drawf.tri(e.x, e.y, 21 * e.foutpow(), (150 + rand.random(-40, 40)) * e.foutpow(), a);
            }
        });
        Effect e3 = new ExplosionEffect(){{
            smokes = 0;
            lifetime = 30;
            sparkColor = trailColor.cpy().a(0.6f);
            sparkLen = 7;
            sparks = 6;
            waveColor = trailColor.cpy().mul(Pal.surge);
            waveRad = splashDamageRadius;
            waveLife = 9;
        }};

        hitEffect = despawnEffect = new MultiEffect(e1, e2, e3);
    }

        @Override
        public void draw(Bullet b) {
            super.draw(b);

            Draw.color(trailColor);
            Drawf.tri(b.x, b.y, 12, 20, b.rotation());
            Drawf.tri(b.x, b.y, 7, 10, b.rotation() - 90);
            Drawf.tri(b.x, b.y, 7, 10, b.rotation() + 90);
        }

        @Override
        public void drawTrail(Bullet b) {
            super.drawTrail(b);

            if(!(b instanceof RainbowStorm.rbs r)) return;
            if(trailLength > 0) {
                for(int i = 0; i < 2; i++){
                    if(r.trails[i] != null){
                        float z = Draw.z();
                        Draw.z(z - 1e-4f);
                        r.trails[i].draw(trailColor, trailWidth * .5f);
                        Draw.z(z);
                    }
                }
            }
        }

        @Override
        public void updateTrail(Bullet b) {
            super.updateTrail(b);
            if(b instanceof RainbowStorm.rbs r) {
                if (!headless && trailLength > 0) {
                    for (int i = 0; i < 2; i++) {
                        if (r.trails[i] == null) {
                            r.trails[i] = new Trail((int) (trailLength / 1.2));
                        }
                        r.trails[i].length = (int) (trailLength / 1.2);

                        float dx = EUGet.txy(b.x, b.y, 12, 5, b.rotation() - 90, b.time * 24 + i * 180, 0),
                                dy = EUGet.txy(b.x, b.y, 12, 5, b.rotation() - 90, b.time * 24 + i * 180, 1);
                        r.trails[i].update(dx, dy, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
                    }
                }
            }
        }

        @Override
        public void updateBulletInterval(Bullet b) {
            super.updateBulletInterval(b);
        }

        @Override
        public @Nullable
        Bullet create(
                @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
                float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
        ){
            RainbowStorm.rbs bullet = RainbowStorm.rbs.create();

            for(int i = 0; i < 2; i++){
                if (bullet.trails[i] != null) {
                    bullet.trails[i].clear();
                }
            }
            if(bullet.child.size > 0) bullet.child.clear();
            return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
        }
    };
}
