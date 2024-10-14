package ExtraUtilities.content;

import ExtraUtilities.worlds.entity.bullet.ChainLightningFade;
import ExtraUtilities.worlds.entity.bullet.FlameBulletType;
import ExtraUtilities.worlds.entity.bullet.RainbowStorm;
import ExtraUtilities.worlds.entity.bullet.mixBoom;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.Turret;

import static ExtraUtilities.ExtraUtilitiesMod.hardMod;
import static mindustry.Vars.indexer;

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
                if (b.timer.get(2, bulletInterval)) {
                    int index = ((int) (b.time/3f) + b.id * 3) % 120;
                    Bullet rb = rainbowLar[index].create(b, b.team, b.x, b.y, b.rotation() - 90, 45, 1, 1, null);
                    rb.fdata = 120;
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
                fragBullet.create(b, b.team, b.x, b.y, a);
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
}
