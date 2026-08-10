package ExtraUtilities.worlds.entity.animation;

import ExtraUtilities.content.EUGet;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;

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
    public @Nullable
    Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ) {
        animation at = animation.create();
        if(at.loop != null) at.loop = null;
        at.started = at.ended = false;
        return EUGet.anyOtherCreate(at, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
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
