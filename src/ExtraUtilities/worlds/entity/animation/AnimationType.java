package ExtraUtilities.worlds.entity.animation;

import ExtraUtilities.content.EUGet;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;

public class AnimationType extends BasicBulletType {
    public AnimationType start;
    public AnimationType loop;
    public AnimationType end;
    public AnimationType(float drawSize){
        this.drawSize = drawSize;
        damage = speed = 0;
        hittable = absorbable = collides = collidesAir = collidesGround = false;
        hitEffect = despawnEffect = shootEffect = smokeEffect = Fx.none;
    }
    public AnimationType(){
        this(240);
    }

    @Override
    public void draw(Bullet b) {

    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        animation at = animation.create();
        if(at.loop != null) at.loop = null;
        at.started = at.ended = false;
        return EUGet.anyOtherCreate(at, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class animation extends Bullet{
        public boolean started = false;
        public Bullet loop;
        public boolean ended = false;
        public float afx, afy;
        public static animation create(){
            return Pools.obtain(animation.class, animation::new);
        }
    }
}
