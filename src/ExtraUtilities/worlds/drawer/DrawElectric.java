package ExtraUtilities.worlds.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;

import static mindustry.Vars.*;

public class DrawElectric extends DrawPart {
    public float x, y;
    public float radius = 8f, linMti = 1;
    public Color bColor = Color.white, lColor = Color.white;
    public boolean mirror = false;
    public float rotateSpeed = 3f;
    public float layer = -1f, layerOffset = 0f, colorChose = 0;
    public PartProgress progress = PartProgress.warmup;
    public int point = 5;

    public boolean circle = true, square = true, colorful = true;
    //public int balls = 1;

    public float[] xp = new float[point];
    public float[] yp = new float[point];

    public float timer = 0;

    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        float p = progress.getClamp(params);
        float r = radius * p;

        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        float sin = Mathf.absin(Time.time, 6f, 1f);
        float baseRot = Time.time * rotateSpeed;

        Draw.color(lColor, bColor, colorful ? sin : colorChose);

        for(int s = 0; s < len; s++){
            int i = params.sideOverride == -1 ? s : params.sideOverride;

            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x) * sign, y).rotate(params.rotation - 90);

            float
                    rx = params.x + Tmp.v1.x,
                    ry = params.y + Tmp.v1.y;

            Lines.stroke(1.2f * p);
            if(square) Lines.square(rx, ry, radius, -baseRot);
            Lines.stroke(2f * p);
            if(circle) Lines.circle(rx, ry, radius * 1.2f);

            if(point < 2 || p < 0.0001) return;
            Lines.stroke(1.5f * p);
            if(!state.isPaused() && timer ++ > 3) {
                for (int a = 0; a < point; a++) {
                    xp[a] = rx + Mathf.range(r * linMti);
                    yp[a] = ry + Mathf.range(r * linMti);
                }
                timer = 0;
            }
            for (int a = 0; a < point; a++){
                Draw.color(bColor, lColor, a % 2);
                int A = (a + 1) % point;
                Lines.line(xp[a], yp[a], xp[A], yp[A]);
            }
            Draw.color();
        }
    }

    @Override
    public void load(String name) {

    }
}
