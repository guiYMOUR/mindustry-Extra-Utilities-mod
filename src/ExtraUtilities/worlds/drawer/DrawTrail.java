package ExtraUtilities.worlds.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;
import mindustry.world.draw.DrawBlock;

public class DrawTrail extends DrawBlock {
    public float x = 0, y = -25;
    public float scl = 15;
    public float radius = 16, size;
    public int length;
    public Color color;
    public int id = 0;

    public BulletType ball = null;

    public DrawTrail(float size, Color color, int length){
        this.size = size;
        this.color = color;
        this.length = length;
        this.ball = new Ball(color, size, length);
    }

    @Override
    public void draw(Building build) {
        if(!(build instanceof TurretBuild build1)) return;
        if(ball != null && build1.wasShooting){
            Tmp.v1.set(x, y).rotate(build1.rotation - 90);

            float
                    rx = build.x + Tmp.v1.x,
                    ry = build.y + Tmp.v1.y;
            drawBall(build, rx, ry, 0, build.warmup(), new Vec2(), new Vec2(), color);
        }
    }

    public void drawBall(Building build, float x, float y, float rotation, float p, Vec2 lastEnd, Vec2 offset, Color color){
        if(p < 0.001) return;
        rand.setSeed(build.id + id);
        lastEnd.add(offset.trns(
                rand.random(360f) + Time.time * 2f,
                Mathf.sin(Time.time + rand.random(200f), scl, radius * 1.5f)
        )).rotate(rotation);
        lastEnd.add(x, y);
        Bullet tb = ball.create(build, x, y, 0);
        tb.set(lastEnd.x, lastEnd.y);
        tb.time(Math.min(tb.time, 10));
    }

    public static class Ball extends BulletType {
        public Color color;
        public float size;
        public Ball(Color color, float size, int length){
            this.color = color;
            this.size = size;
            trailColor = color;
            trailLength = length;
            trailWidth = size/2;
            despawnEffect = healEffect = Fx.none;
            hittable = absorbable = false;
            collides = collidesAir = collidesGround = false;
            damage = speed = 0;
            lifetime = 20;
        }
        @Override
        public void draw(Bullet b) {
            super.draw(b);
            float p = b.time < 10 ? b.time/10 : 1 - (b.time - 10)/10;
            float sin1 = Mathf.sin(Time.time, 6, 0.6f) * p;
            float ra = size * p;

            Draw.color(color);
            Fill.circle(b.x, b.y, ra + sin1);
        }
    }
}
