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

        float tz = Layer.turret - 1;
        Draw.z(tz+ (Layer.effect - tz) * (warmup < 0.6f ? 0 : warmup));
        for(int i : Mathf.signs) {
            float wax = params.x + Angles.trnsx(rot, 8 * i, 1),
                    way = params.y + Angles.trnsy(rot, 8 * i, 1);
            float ag = rot + 90 * i - 90;
            float ex = EUGet.dx(wax, 9 * warmup, ag - 4 * warmup * i);
            float ey = EUGet.dy(way, 9  * warmup, ag - 4 * warmup * i);
            Drawf.tri(ex, ey, 3 * warmup, 22 * warmup, ag);
            ex = EUGet.dx(wax, 9 * warmup, ag - 8 * warmup * i);
            ey = EUGet.dy(way, 9  * warmup, ag - 8 * warmup * i);
            Drawf.tri(ex, ey, 3 * warmup, 22, ag - 8 * warmup * i + 90 * i);
            ex = EUGet.dx(wax, 9 * warmup, ag - 16 * warmup * i);
            ey = EUGet.dy(way, 9  * warmup, ag - 16 * warmup * i);
            Drawf.tri(ex, ey, 4 * warmup, 22, ag - 24 * warmup * i + 90 * i);
            Drawf.tri(ex, ey, 4 * warmup, 5, ag + 180 - 24 * warmup * i + 90 * i);
        }
        Draw.z(z);
        Draw.reset();
    }

    @Override
    public void load(String name) {
        star = Core.atlas.find(name("halo-small"));
        starBig = Core.atlas.find(name("halo"));
    }
}
