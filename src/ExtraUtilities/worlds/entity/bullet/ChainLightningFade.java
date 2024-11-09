//Each section of lightning causes one damage

package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.*;
import arc.struct.FloatSeq;
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
import static mindustry.Vars.*;

public class ChainLightningFade extends BulletType {
    public Color color;

    public float linkSpace;
    public float stroke;
    public boolean large = false;
    public boolean back = false;

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
        if(!(b.data instanceof Position p) || damage < 0) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(b.x, b.y, tx, ty);
        Tmp.v1.set(p).sub(b.x, b.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        int links = Mathf.ceil(dst / linkSpace);
        float spacing = dst / links;

        b.random.setSeed(b.id);
        //b.resetPos = new float[links + 1][2];
        int i;

        float ox = b.x, oy = b.y;
        b.px.add(b.x);
        b.py.add(b.y);
//        b.resetPos[0][0] = b.x;
//        b.resetPos[0][1] = b.y;
        for(i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(b.random).scl(linkSpace/2f);
                nx = b.x + normx * len + Tmp.v1.x;
                ny = b.y + normy * len + Tmp.v1.y;
            }

            b.px.add(nx);
            b.py.add(ny);
//            b.resetPos[i + 1][0] = nx;
//            b.resetPos[i + 1][1] = ny;

            float length = EUGet.pos(ox, oy).dst(nx, ny);
            float angle = EUGet.pos(ox, oy).angleTo(nx, ny);
            Damage.collideLine(b, b.team, hitEffect, ox, oy, angle, length, large, false);
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
            Lines.stroke(stroke * Mathf.curve(b.fout(), 0, 0.7f));
            Draw.color(Color.white, color, b.fin());

            //Fill.circle(b.x, b.y, Lines.getStroke() / 2);

            b.random.setSeed(b.id);
            float fin = Mathf.curve(b.fin(), 0, 0.5f);
            int i;

//            for (i = 0; i < (b.resetPos.length - 1) * fin; i++) {
//                float ox, nx, oy, ny;
//                if(!back) {
//                    ox = b.resetPos[i][0];
//                    oy = b.resetPos[i][1];
//                    nx = b.resetPos[i + 1][0];
//                    ny = b.resetPos[i + 1][1];
//                } else {
//                    int fi = b.resetPos.length - 1 - i;
//                    ox = b.resetPos[fi][0];
//                    oy = b.resetPos[fi][1];
//                    nx = b.resetPos[fi - 1][0];
//                    ny = b.resetPos[fi - 1][1];
//                }
//
//                Lines.line(ox, oy, nx, ny);
//            }
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

    @Override
    public void draw(Bullet b) {
        if(!(b instanceof chain)) return;
        draw((chain) b);
    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        chain bullet = chain.create();
        bullet.resetXY();
        return EUGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class chain extends Bullet{
        public final Rand random = new Rand();

        //public float[][] resetPos;
        public FloatSeq px = new FloatSeq();
        public FloatSeq py = new FloatSeq();

        public void resetXY(){
            px.clear();
            py.clear();
        }

        public static chain create() {
            return Pools.obtain(chain.class, chain::new);
        }
    }
}