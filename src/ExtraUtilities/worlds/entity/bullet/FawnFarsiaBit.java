package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.audio.Sound;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;

public class FawnFarsiaBit extends BulletType {
    //换个风格试试
    //蛇
    public ShootBullet shootBullet = (b, s) -> {};
    public Sound shootSound = Sounds.none;
    public float pit = 1, vol = 1;
    //用EUGet里的movePoint动子弹
    public Approach approach = b -> {};
    //draw
    public BulletDrawer drawer = b -> {};

    public float ready = 60;
    public float rotateSpeed = 2;

    public float retarget = 12;

    public FawnFarsiaBit(){
        lifetime = 180;
        keepVelocity = collides = absorbable = hittable = false;
        homingRange = 10 * 8;
        speed = 4;
    }

    @Override
    public void updateBulletInterval(Bullet b) {
        //这个，不需要了
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if(b instanceof Bit bit){
            update(bit);
        }
    }

    public void update(Bit b) {
        if(b.time > ready) {
            b.initVel(b.rotation(), 0);
            if(b.timer.get(4, retarget)){
                float rx = b.x, ry = b.y;
                if(b.target != null) {
                    rx = b.target.x();
                    ry = b.target.y();
                }
                b.target = Units.closestTarget(b.team, rx, ry, homingRange,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround),
                        t -> t != null && collidesGround);
            }
            if(b.target != null) {
                approach.update(b);

                if (b.within(b.target, homingRange + 8) && intervalBullet != null) {
                    if (b.timer.get(3, bulletInterval)) {
                        shootBullet.update(b, intervalBullet);
                        shootSound.at(b.x, b.y, pit, vol);
                    }
                }
            } else {
                b.ang = Mathf.random(360);
            }
        } else {
            float fout = 1 - Math.min(1, b.time/ready);
            b.initVel(b.rotation(), speed * Interp.slowFast.apply(fout));
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);

        if(b instanceof Bit bit) drawer.draw(bit);
    }

    @Override
    public @Nullable Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ) {
        Bit bit = Bit.create();

        bit.target = null;
        bit.ang = Mathf.random(360);

        return EUGet.anyOtherCreate(bit, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public interface ShootBullet{
        void update(Bit b, BulletType shoot);
    }
    public interface Approach{
        void update(Bit b);
    }

    public interface BulletDrawer{
        void draw(Bit b);
    }

    public static class Bit extends Bullet{
        public @Nullable
        Teamc target = null;

        public float ang;

        public static Bit create(){
            return Pools.obtain(Bit.class, Bit::new);
        }
    }
}
