package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUBulletTypes;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.graphics.MainRenderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;

import java.util.Arrays;

import static mindustry.Vars.headless;

public class RainbowStorm extends BulletType {
    public float size;
    public float laserDamage;
    public float laserLength = 24;
    public int laserAmount = 24;
    public boolean inAmi = true;
    public float inTime = 30f;
    public boolean fadeOut = true;
    public float outTime = 30;
    //public boolean shader = true;

    public float rotSpeed = 2;

    public float sideTrailWidth = 5f;
    public Color sideTrailColor = Color.valueOf("FFF8DB");

    public RainbowStorm(float size, float laserDamage){
        this.size = size;
        this.laserDamage = laserDamage;
        absorbable = hittable = collides = false;
        trailLength = 41;
        trailWidth = size;
        trailColor = Color.valueOf("FFF8DB").a(0.6f);

        hitEffect = despawnEffect = Fx.none;
    }

    public RainbowStorm(){
        this(18, 20f);
    }

    @Override
    public void updateBulletInterval(Bullet b) {
        if(!(b instanceof rbs r)) return;
        float in = Math.min(b.time / inTime, 1);
        float fin = inAmi ? Interp.fastSlow.apply(in) : 1;
        float out = Math.min(1, (b.lifetime - b.time) / outTime);
        float fout = fadeOut ? Interp.slowFast.apply(out) : 1;
        float realSize = size * fin * fout;
        int index = (int)b.time;
        if(b.timer.get(2, 12)){
            for(int i = 0; i < laserAmount; i++) {
                float a = 360f/laserAmount * i;
                float dx = EUGet.dx(b.x, realSize, a),
                        dy = EUGet.dy(b.y, realSize, a);
                Bullet rb = EUBulletTypes.rainbowLar[(index + i * 4) % 120].create(b, b.team, dx, dy, a + 120, laserDamage, 1, 1, null);
                rb.fdata = 0;
                r.child.put(a, rb);
            }
        }

        for(Float ag : r.child.keys().toSeq()){
            Bullet ch = r.child.get(ag);
            if(ch != null){
                float a = ag + b.time * rotSpeed;
                float dx = EUGet.dx(b.x, realSize, a)
                        , dy = EUGet.dy(b.y, realSize, a);
                ch.set(dx, dy);
                ch.rotation(a + 120);
                ch.fdata = laserLength * fin * fout;
            }
        }
    }

    //FFF8DB

    @Override
    public void updateTrail(Bullet b) {
        super.updateTrail(b);
        if(!(b instanceof rbs r)) return;
        float in = Math.min(b.time/ inTime, 1);
        float fin = inAmi ? Interp.fastSlow.apply(in) : 1;
        float out = Math.min(1, (b.lifetime - b.time) / outTime);
        float fout = fadeOut ? Interp.slowFast.apply(out) : 1;
        float realSize = size * 0.6f * fin * fout;
        if(!headless && trailLength > 0){
            for(int i = 0; i < 2; i++) {
                if (r.trails[i] == null) {
                    r.trails[i] = new Trail((int) (trailLength/1.2));
                }
                r.trails[i].length = (int) (trailLength/1.2);

                float dx = EUGet.dx(b.x, realSize, b.rotation() - 90 + i * 180)
                        , dy = EUGet.dy(b.y, realSize, b.rotation() - 90 + i * 180);
                r.trails[i].update(dx, dy, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
            }
        }
    }

    @Override
    public void drawTrail(Bullet b) {
        float in = Math.min(b.time/ inTime, 1);
        float fin = inAmi ? Interp.fastSlow.apply(in) : 1;
        float out = Math.min(1, (b.lifetime - b.time) / outTime);
        float fout = fadeOut ? Interp.slowFast.apply(out) : 1;
        if(trailLength > 0 && b.trail != null){
            float z = Draw.z();
            Draw.z(Layer.bullet - 1e-4f);
            b.trail.draw(trailColor, trailWidth * fin * fout);
            Draw.z(z);
        }
        if(!(b instanceof rbs r)) return;
        if(trailLength > 0) {
            for(int i = 0; i < 2; i++){
                if(r.trails[i] != null){
                    float z = Draw.z();
                    Draw.z(z - 1e-4f);
                    r.trails[i].draw(sideTrailColor, sideTrailWidth * fin * fout);
                    Draw.z(z);
                }
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        if(!(b instanceof rbs)) return;
        float in = Math.min(b.time/ inTime, 1);
        float fin = inAmi ? Interp.fastSlow.apply(in) : 1;
        float out = Math.min(1, (b.lifetime - b.time) / outTime);
        float fout = fadeOut ? Interp.slowFast.apply(out) : 1;
        float realSize = size * fin * fout;

        float z = Draw.z();
        Draw.z(z + 1);
        Draw.color(sideTrailColor);
        Fill.circle(b.x, b.y, realSize);
        Draw.z(z);
        //if(shader) MainRenderer.addBlackHole(b.x, b.y, realSize * 0.9f, realSize * 1.8f, 0);
        super.draw(b);
    }

    public void removed(Bullet b){
        //
    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        rbs bullet = rbs.create();

        for(int i = 0; i < 2; i++){
            if (bullet.trails[i] != null) {
                bullet.trails[i].clear();
            }
        }
        if(bullet.child.size > 0) bullet.child.clear();
        return EUGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class rbs extends Bullet {
        @Nullable
        public Trail[] trails = new Trail[2];

        public ObjectMap<Float, Bullet> child = new ObjectMap<>();

        public static rbs create() {
            return Pools.obtain(rbs.class, rbs::new);
        }
    }
}
