package ExtraUtilities.worlds.entity.bullet;

import arc.math.Angles;
import arc.math.geom.Position;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;


public class aimToPosBullet extends BulletType {
    public float before = 30;
    public float rotSpeed = 4f;
    public aimToPosBullet(){
        reflectable = false;
        homingRange = 80;
        homingDelay = 15;
    }
    @Override
    public void updateHoming(Bullet b) {
        float realAimX = b.aimX < 0 ? b.x : b.aimX;
        float realAimY = b.aimY < 0 ? b.y : b.aimY;

        Teamc target;
        //home in on allies if possible
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

        if(b.time > homingDelay && target != null) {
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), rotSpeed * Time.delta));
        }
        if(!(b.data instanceof Position[] pos)) return;
        b.initVel(b.rotation(), b.time < before ? speed * (1 - b.time / (before + 10)) : speed * b.fin());
        if(target != null) return;
        if(b.time > homingDelay) b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(pos[1]), rotSpeed * Time.delta));
    }
}
