package ExtraUtilities.worlds.entity.bullet;

import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class fBullet extends BulletType {
    public BulletType f;
    public float fAngle = 0;

    public fBullet(BulletType f, float lifetime){
        this.f = f;
        this.lifetime = lifetime;
        collides = collidesAir = collidesGround = collidesTiles = hittable = absorbable = false;
        despawnEffect = hitEffect = Fx.none;
        speed = damage = 0;
        keepVelocity = false;
    }

    @Override
    public void despawned(Bullet b) {
        f.create(b, b.team, b.x, b.y, fAngle);
        despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        despawnSound.at(b.x, b.y, hitSoundPitch, hitSoundVolume);
    }
}
