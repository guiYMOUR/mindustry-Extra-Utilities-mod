package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;

public class mixBoom extends BulletType {
    public float size = 8;
    public float expIer = 5;
    public Color color;
    public boolean aimTo = false;
    public float aimBefore = 3;

    public mixBoom(Color color){
        this.color = color;
        scaleLife = true;
        collides = collidesAir = collidesGround = collidesTiles = false;
        despawnEffect = new MultiEffect(EUFx.expFtEffect(8, 3, 8, 20, 0.5f), new Effect(6, e -> {
            Lines.stroke(6 * e.fout(), Color.white);
            Lines.circle(e.x, e.y, 4.5f * 8 * e.fin());
        }));
        fragOnHit = false;
        damage = 0;

        hittable = absorbable = false;

        trailEffect = Fx.artilleryTrail;
        trailInterval = 2;

        despawnSound = hitSound = Sounds.shootReign;
    }

    @Override
    public void createFrags(Bullet b, float x, float y) {
        if(fragBullet != null && (fragOnAbsorb || !b.absorbed)){
            for(int i = 0; i < fragBullets; i++){
                float a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - fragBullets/2f) * fragSpread);
                fragBullet.create(b, b.team, x, y, a, -1, 1,  1, i * expIer);
            }
        }
        b.frags++;
    }

    @Override
    public void update(Bullet b) {
        if(aimTo && b.time > b.lifetime - aimBefore) {
            createFrags(b, b.x, b.y);
            b.remove();
            return;
        }
        super.update(b);
    }

    @Override
    public void updateTrailEffects(Bullet b) {
        if(trailInterval <= 0) return;
        if(b.timer(0, trailInterval)){
            trailEffect.at(b.x, b.y, size * b.fin(), color);
        }
    }

    @Override
    public void draw(Bullet b) {
        Draw.color(color);
        Fill.circle(b.x, b.y, size * b.fin() + 1);
        drawTrail(b);
        drawParts(b);
    }

    public static class mixExps extends BulletType{
        public float size = 6, minSize = 0;
        public Color color;
        public boolean circle = true;

        public mixExps(){
            collides = collidesAir = collidesTiles = false;
            keepVelocity = false;
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if(!(b.data instanceof Float)) return;
            float ier = (Float) b.data;
            b.lifetime = lifetime + ier;
            if(b.time < ier){
                b.initVel(b.rotation(), 0);
            } else {
                float fout = circle ? Math.min(1 - (b.time - (Float)b.data)/lifetime, 1) : b.fout();
                b.initVel(b.rotation(), speed * fout);
            }
        }

        @Override
        public void drawTrail(Bullet b) {
            if(!(b.data instanceof Float)) return;
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                float fout = circle ? Math.min(1 - (b.time - (Float)b.data)/lifetime, 1) : b.fout();
                b.trail.draw(trailColor, minSize + 1 + trailWidth * fout);
                Draw.z(z);
            }
        }

        @Override
        public void draw(Bullet b) {
            if(!(b.data instanceof Float)) return;
            Draw.color(color);
            float fout = circle ? Math.min(1 - (b.time - (Float)b.data)/lifetime, 1) : b.fout();
            Fill.circle(b.x, b.y, size * fout + minSize);
            super.draw(b);
        }
    }
}
