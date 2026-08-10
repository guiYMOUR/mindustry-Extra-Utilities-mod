package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
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
        hitEffect = EUFx.hitOut;
        smokeEffect = Fx.none;
        trailEffect = Fx.none;
        despawnEffect = Fx.blastExplosion;
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
        hitEffect.at(build.x, build.y, b.rotation(), build);
        if(build.health <= damage) build.kill();
        else build.health -= Math.ceil(build.maxHealth * percent);
    }
}
