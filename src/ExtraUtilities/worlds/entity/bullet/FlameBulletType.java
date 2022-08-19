package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import arc.graphics.Color;
import arc.math.Angles;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;

import static mindustry.Vars.*;

public class FlameBulletType extends BulletType {
    public float flameLength;//real flame range
    public float flameCone;
    public int particleNumber;

    public float damageBoost = 4f;

    public FlameBulletType(Color colorBegin, Color colorTo, Color colorEnd, float length,  float cone, int number, float lifetime){

        this.flameLength = length;
        this.flameCone = cone;
        this.particleNumber = number;
        this.lifetime = 0;

        damage = 20;
        speed = 0;
        hitEffect = Fx.none;
        smokeEffect = Fx.none;
        trailEffect = Fx.none;
        despawnEffect = Fx.none;
        pierce = true;
        collidesAir = false;
        absorbable = false;
        hittable = false;
        keepVelocity = false;
        status = StatusEffects.burning;
        statusDuration = 60 * 4;
        buildingDamageMultiplier = 0.4f;
        despawnHit = true;
        shootEffect = EUFx.flameShoot(colorBegin, colorTo, colorEnd, length, cone, number, lifetime);
    }

    @Override
    public void hit(Bullet b) {
        if(absorbable && b.absorbed) return;
        //let's step by step
        //unit▼
        Units.nearbyEnemies(b.team, b.x, b.y, flameLength, unit -> {
            if(Angles.within(b.rotation(), b.angleTo(unit), flameCone) && unit.checkTarget(collidesAir, collidesGround)){
                Fx.hitFlameSmall.at(unit);
                unit.damage(damage * damageBoost);
                unit.apply(status, statusDuration);
            }
        });
        //block▼
        indexer.allBuildings(b.x, b.y, flameLength, other -> {
            if(other.team != b.team && Angles.within(b.rotation(), b.angleTo(other), flameCone)){
                Fx.hitFlameSmall.at(other);
                other.damage(damage * buildingDamageMultiplier * damageBoost);
            }
        });
    }
}
