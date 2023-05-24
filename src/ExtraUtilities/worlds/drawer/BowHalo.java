package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUItems;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class BowHalo  extends DrawPart {
    public float x = 0, y = -25;
    public float stroke = 4;
    public float radius = 12f;
    public Color color = EUItems.lightninAlloy.color;
    public float rotateSpeed = 1, w1 = 3, h1 = 6, w2 = 4, h2 = 18;
    public float layer = Layer.effect;
    public PartProgress progress = PartProgress.warmup.delay(0.5f);

    @Override
    public void draw(PartParams params) {
        float warmup = progress.getClamp(params);
        Lines.stroke(stroke * warmup);
        float sin = Mathf.absin(Time.time, 10, 1.5f);
        float realR = radius + sin;
        float rot = params.rotation - 90;
        float bx = params.x, by = params.y;
        Draw.z(layer);
        Draw.color(color);
        Tmp.v1.set(x, y).rotate(rot);
        float px = bx + Tmp.v1.x, py = by + Tmp.v1.y;
        Lines.circle(px, py, realR);
        for(int i = 0; i < 2; i++){
            float angle = i* 360f / 2 + rot;
            Drawf.tri(px + Angles.trnsx(angle - Time.time * rotateSpeed, realR), py + Angles.trnsy(angle - Time.time * rotateSpeed, realR), w1, h1 * warmup, angle - Time.time * rotateSpeed);
            Drawf.tri(px + Angles.trnsx(angle, realR), py + Angles.trnsy(angle, realR), w2, h2 * warmup, angle);
        }
        Draw.reset();
    }

    @Override
    public void load(String name) {

    }
}
