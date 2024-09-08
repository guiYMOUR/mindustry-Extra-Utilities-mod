package ExtraUtilities.content;

import ExtraUtilities.worlds.entity.bullet.FlameBulletType;
import ExtraUtilities.worlds.entity.bullet.mixBoom;
import arc.graphics.Color;
import arc.math.Angles;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

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
}
