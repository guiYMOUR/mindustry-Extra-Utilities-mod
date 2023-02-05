package ExtraUtilities.worlds.entity.bullet;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

public class PercentDamage extends BasicBulletType {
    public float percent = 0.008f;

    public PercentDamage(){
        lifetime = 54;
        shootEffect = Fx.shootLiquid;
        hitEffect = Fx.bubble;
        smokeEffect = Fx.none;
        trailEffect = Fx.none;
        despawnEffect = Fx.bubble;
        damage = 10;
        speed = 4;
        status = StatusEffects.none;
        knockback = 1.2f;
        shrinkY = 0;
        width = 15;
        height = 9;
        backColor = Color.valueOf("ec7458");
        frontColor = Color.valueOf("ec7458");
        collides = true;
        collidesAir = true;
        collidesGround = true;
        absorbable = false;
        hittable = false;
        keepVelocity = false;
        reflectable = false;
    }

    @Override
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
        hitEffect.at(x, y);
        if(build.health < damage){
            build.kill();
            return;
        }
        build.health -= Math.ceil(build.maxHealth * percent);
    }
}
