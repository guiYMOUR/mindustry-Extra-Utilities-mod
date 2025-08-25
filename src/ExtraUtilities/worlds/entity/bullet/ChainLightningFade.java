//Each section of lightning causes one damage

package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.struct.FloatSeq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;

import static mindustry.Vars.*;

public class ChainLightningFade extends BulletType {
    public Color color;

    public float linkSpace;
    public float stroke;
    public boolean large = false;
    public boolean back = false;
    public float layer = Layer.bullet + 0.1f;

    public ChainLightningFade(float lifetime, float linkSpace, float stroke, Color color, float damage, Effect hitEffect){
        absorbable = hittable = collides = collidesTiles = keepVelocity = false;
        speed = 0;
        despawnEffect = Fx.none;
        this.lifetime = lifetime;
        this.linkSpace = linkSpace;
        this.stroke = stroke;
        this.color = color;
        this.damage = damage;
        this.hitEffect = hitEffect;
        status = StatusEffects.shocked;
    }

    private void init(chain b) {
        float tx, ty;
        if(b.data instanceof Position p) {
            tx = p.getX();
            ty = p.getY();
        } else if(b.data instanceof Float f){
            tx = EUGet.pointAngleX(b.x, b.rotation(), f);
            ty = EUGet.pointAngleY(b.y, b.rotation(), f);
        } else return;
        float dst = Mathf.dst(b.x, b.y, tx, ty);
        Tmp.v1.set(tx, ty).sub(b.x, b.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float lp = b.linkSpaceOverride > 0 ? b.linkSpaceOverride : linkSpace;
        int links = Mathf.ceil(dst / lp);
        float spacing = dst / links;

        b.random.setSeed(b.id);
        int i;

        float ox = b.x, oy = b.y;
        b.px.add(b.x);
        b.py.add(b.y);
        for(i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(b.random).scl(lp/2f);
                nx = b.x + normx * len + Tmp.v1.x;
                ny = b.y + normy * len + Tmp.v1.y;
            }

            b.px.add(nx);
            b.py.add(ny);

            if(damage > 0){
                float length = Mathf.dst(ox, oy, nx, ny);
                float angle = Angles.angle(ox, oy, nx, ny);
                //Damage.collideLine(b, b.team, hitEffect, ox, oy, angle, length, large, false);
                Damage.collideLine(b, b.team, ox, oy, angle, length, large, false);
            }
            ox = nx;
            oy = ny;
        }
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(!(b instanceof chain)) return;
        init((chain) b);
    }

    private void draw(chain b){
        if(b.px.size != b.py.size) return;
        if(b.px.size > 0) {
            float z = Draw.z();
            Draw.z(layer);
            Lines.stroke(stroke * Mathf.curve(b.fout(), 0, 0.7f));
            Draw.color(Color.white, color, b.fin());

            //Fill.circle(b.x, b.y, Lines.getStroke() / 2);

            b.random.setSeed(b.id);
            float fin = Mathf.curve(b.fin(), 0, 0.5f);
            int i;
            for(i = 0; i < (b.px.size - 1) * fin; i++){
                float ox, nx, oy, ny;
                if(!back) {
                    ox = b.px.get(i);
                    oy = b.py.get(i);
                    nx = b.px.get(i + 1);
                    ny = b.py.get(i + 1);
                } else {
                    int fi = b.px.size - 1 - i;
                    ox = b.px.get(fi);
                    oy = b.py.get(fi);
                    nx = b.px.get(fi - 1);
                    ny = b.py.get(fi - 1);
                }

                Lines.line(ox, oy, nx, ny);
            }

            Draw.z(z);
            Draw.reset();
        }
    }

    @Override
    public void despawned(Bullet b) {
        if(!(b.data instanceof Position p)) return;
        if(back) createSplashDamage(b, b.x, b.y);
        else createSplashDamage(b, p.getX(), p.getY());
        if(despawnEffect != Fx.none) {
            if(!back) despawnEffect.at(p.getX(), p.getY(), b.rotation(), this.hitColor);
            else despawnEffect.at(b.x, b.y, b.rotation(), this.hitColor);
        }
        if(despawnSound != Sounds.none) despawnSound.at(b);
        Effect.shake(despawnShake, despawnShake, b);
    }

//    @Override
//    public void removed(Bullet b) {
//        if(b.data instanceof Position p) Pools.free(p);
//    }

    @Override
    public void draw(Bullet b) {
        if(!(b instanceof chain)) return;
        draw((chain) b);
    }

    @Override
    public @Nullable Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ){
        chain bullet = chain.create();
        bullet.resetXY();
        bullet.linkSpaceOverride = -1;
        return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public @Nullable
    Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, @Nullable Teamc target, float linkSpaceOverride) {
        chain bullet = chain.create();
        bullet.resetXY();
        bullet.linkSpaceOverride = linkSpaceOverride;
        return EUGet.anyOtherCreate(bullet, this, owner, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public @Nullable
    Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, float linkSpaceOverride) {
        return create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, null, -1, -1, null, linkSpaceOverride);
    }

    public static class chain extends Bullet{
        public final Rand random = new Rand();

        //public float[][] resetPos;
        public FloatSeq px = new FloatSeq();
        public FloatSeq py = new FloatSeq();

        public float linkSpaceOverride = -1;

        public void resetXY(){
            px.clear();
            py.clear();
        }

        public static chain create() {
            return Pools.obtain(chain.class, chain::new);
        }
    }
}