package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class LingShaPart extends DrawPart {
    public TextureRegion star, starBig;
    public Color color = Color.valueOf("#f7d7fc");
    public float wx, wy;
    public PartProgress progressWarmup = PartProgress.warmup;

    @Override
    public void draw(PartParams params) {
        float warmup = progressWarmup.getClamp(params);
        if(warmup < 1e-4f) return;
        float z = Draw.z();
        Draw.z(Layer.effect);
        Draw.color(Tmp.c4.set(color).a(warmup));
        float rot = params.rotation - 90;
        float ax = params.x + Angles.trnsx(rot, wx, wy),
                ay = params.y + Angles.trnsy(rot, wx, wy);
        Draw.rect(star, ax, ay, 24, 24, rot * 2);
        Draw.rect(starBig, ax, ay, 24, 24, -rot);
        Draw.color(color);
        for(int i : Mathf.signs) {
            float ag = rot + 90 * i - 90;
            float ex = EUGet.dx(ax, 16, ag),
                    ey = EUGet.dy(ay, 16, ag);
            Drawf.tri(ex, ey, 4 * warmup, 22, ag);
            Drawf.tri(ex, ey, 4 * warmup, 5, ag + 180);
            ag += (int) (Time.time/60f) * 6;
            ex = EUGet.dx(ax, 16, ag);
            ey = EUGet.dy(ay, 16, ag);
            Drawf.tri(ex, ey, 4 * warmup, 16, ag);
            Drawf.tri(ex, ey, 4 * warmup, 5, ag + 180);
        }
        Draw.z(z);
    }

    @Override
    public void load(String name) {
        star = Core.atlas.find(name("halo-small"));
        starBig = Core.atlas.find(name("halo"));
    }
}
