package ExtraUtilities.worlds.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;

public class DrawTri extends DrawPart {
    public float x, y, get;
    public Color color = Color.white, cColor = Color.white;
    public float rotateSpeed = 0;
    public float r1 = 5, r2 = 6, maxR = 0;
    public float layer = -1, layerOffset = 0;
    public PartProgress progress = PartProgress.warmup;
    public int amount = 2;

    public boolean back = false;

    public float width = 4, length = 30;

    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        float p = progress.getClamp(params);
        float rot = Time.time * rotateSpeed;
        int i = params.sideOverride == -1 ? 0 : params.sideOverride;
        float sin = Mathf.sin(Time.time, 6, 0.6f);


        float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
        if(!back) {
            Tmp.v1.set((x) * sign, get + y * p).rotate(params.rotation - 90);
            Tmp.v3.set((x) * sign, get).rotate(params.rotation - 90);
        } else {
            Tmp.v1.set((x) * sign, get).rotate(params.rotation - 90);
            Tmp.v3.set((x) * sign, get + y * p).rotate(params.rotation - 90);
        }
        Tmp.v2.set((x) * sign, get + y * p / 2).rotate(params.rotation - 90);

        float
                rx = params.x + Tmp.v1.x,
                ry = params.y + Tmp.v1.y,
                px = params.x + Tmp.v2.x,
                py = params.y + Tmp.v2.y,
                ex = params.x + Tmp.v3.x,
                ey = params.y + Tmp.v3.y;
        Draw.color(cColor);
        for(int a = 0; a < amount; a++){
            Drawf.tri(rx, ry, width * p, length, 360f / amount *a + params.rotation + 90 + rot);
        }
        Lines.stroke(p);
        Lines.circle(rx, ry,  maxR * p);

        Draw.color(color);

        Fill.circle(rx, ry, r1 * p);
        Fill.circle(ex, ey, (r2 + sin) * p);
        Lines.stroke(p > 0.001 ? 2 : 0);
        Lines.circle(px, py,  Math.abs(y * p / 2));
    }

    @Override
    public void load(String name) {

    }
}
