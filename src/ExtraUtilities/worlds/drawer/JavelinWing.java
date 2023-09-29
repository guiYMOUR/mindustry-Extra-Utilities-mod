package ExtraUtilities.worlds.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;

public class JavelinWing extends DrawPart {
    public float x, y,
            w1 = 6, h1 = 18,
            w2 = 4, h2 = 15;
    public Color color = Color.valueOf("c0ecff"), colorBase = Color.valueOf("6d90bc");
    public float layer = -1f, layerOffset = 0f;
    public PartProgress progress = PartProgress.warmup;
    public float rd = 20, rt = 35;
    public float ap = 0.6f;

    public boolean mirror = true;

    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        float p = progress.getClamp(params);
        if(p < 0.001f) return;
        int len = mirror && params.sideOverride == -1 ? 2 : 1;

        for(int s = 0; s < len; s++) {
            int i = params.sideOverride == -1 ? s : params.sideOverride;

            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x) * sign, y).rotate(params.rotation - 90);

            float
                    rx = params.x + Tmp.v1.x,
                    ry = params.y + Tmp.v1.y;

            float rotation = params.rotation - 90 - 180 * s;
            float rotAdd = rt * p;
            Draw.color(color.cpy().a(ap * p));
            Drawf.tri(rx + Angles.trnsx(rotation + rotAdd, rd), ry + Angles.trnsy(rotation + rotAdd, rd), w2 * p, h2 * p * 1.3f, rotation + rotAdd);
            Drawf.tri(rx + Angles.trnsx(rotation + rotAdd, rd), ry + Angles.trnsy(rotation + rotAdd, rd), w2 * p * 1.8f, h2, rotation + rotAdd - 180);
            Drawf.tri(rx + Angles.trnsx(rotation, rd), ry + Angles.trnsy(rotation, rd), w1 * p, h1 * p * 1.3f, rotation);
            Drawf.tri(rx + Angles.trnsx(rotation, rd), ry + Angles.trnsy(rotation, rd), w1 * p * 1.8f, h1, rotation - 180);
            Drawf.tri(rx + Angles.trnsx(rotation + 15, rd * 1.4f), ry + Angles.trnsy(rotation + 15, rd * 1.4f), 4 * p, 4, rotation + 20);
            Drawf.tri(rx + Angles.trnsx(rotation + 15, rd * 1.4f), ry + Angles.trnsy(rotation + 15, rd * 1.4f), 4 * p, 4, rotation + 20 - 180);
            Drawf.tri(rx + Angles.trnsx(rotation - 15, rd * 1.4f), ry + Angles.trnsy(rotation - 15, rd * 1.4f), 4 * p, 4, rotation - 20);
            Drawf.tri(rx + Angles.trnsx(rotation - 15, rd * 1.4f), ry + Angles.trnsy(rotation - 15, rd * 1.4f), 4 * p, 4, rotation - 20 - 180);
            Drawf.tri(rx + Angles.trnsx(rotation - rotAdd, rd), ry + Angles.trnsy(rotation - rotAdd, rd), w2 * p, h2 * p * 1.3f, rotation - rotAdd);
            Drawf.tri(rx + Angles.trnsx(rotation - rotAdd, rd), ry + Angles.trnsy(rotation - rotAdd, rd), w2 * p * 1.8f, h2, rotation - rotAdd - 180);
        }
        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void load(String name) {

    }
}
