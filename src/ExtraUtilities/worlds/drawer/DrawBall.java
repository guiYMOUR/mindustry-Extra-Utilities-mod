package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUFx;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.gen.Bullet;
import mindustry.graphics.Trail;

import static mindustry.Vars.*;
import static mindustry.content.Fx.*;

public class DrawBall extends DrawPart {
    public float x, y;
    public int id = 0;
    public float scl = 15;
    public float radius = 8f, size = 2.5f;
    public int trailLen = 15;
    public Color bColor = Color.white;
    public boolean mirror = false;
    public float rotate = 0;
    public float layer = -1f, layerOffset = 0f;
    public PartProgress progress = PartProgress.warmup;
    public Vec2 offsetVec, endVec;

    public boolean line = false;

    public DrawBall(){}

    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        float p = progress.getClamp(params);
        float r = radius * p;
        //float baseRot = Time.time * rotateSpeed;

        int len = mirror && params.sideOverride == -1 ? 2 : 1;

        for(int s = 0; s < len; s++) {
            int i = params.sideOverride == -1 ? s : params.sideOverride;

            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x) * sign, y).rotate(params.rotation - 90);

            float
                    rx = params.x + Tmp.v1.x,
                    ry = params.y + Tmp.v1.y;

            //offset.setZero();
            drawBall(rx, ry, rotate, trailLen, p, offsetVec, endVec, bColor);
        }
    }

    @Override
    public void load(String name) {

    }

    public void drawBall(float x, float y, float rotation, int trailLen, float p, Vec2 lastEnd, Vec2 offset, Color color){
        if(p < 0.001) return;
        rand.setSeed(id);
        float trailWid = size * p;
        lastEnd.add(offset.trns(
                rand.random(360f) + Time.time * 2f,
                Mathf.sin(Time.time + rand.random(200f), scl, radius * 1.5f)
        )).rotate(rotation);
        lastEnd.add(x, y);
        float sin = Mathf.sin(Time.time, 6, 0.6f) * p;
        Draw.color(color);
        if (line) {
            Lines.stroke(trailWid / 2f + sin);
            Lines.circle(lastEnd.x, lastEnd.y, trailWid + sin);
        } else Fill.circle(lastEnd.x, lastEnd.y, trailWid + sin);
    }
}
