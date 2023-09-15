package ExtraUtilities.worlds.drawer;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.part.DrawPart;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class AimPart extends DrawPart {
    public Color color;

    public float x, y, rt;
    public float length, spacing, width;
    public float layer;

    public boolean drawLine = true;

    public PartProgress progress = PartProgress.reload;
    public PartProgress warmup = PartProgress.warmup;

    @Override
    public void draw(PartParams params) {
        Draw.color(color);
        Draw.z(layer);
        float wp = warmup.getClamp(params);
        float fout = 1 - progress.getClamp(params);
        float track = Mathf.curve(fout, 0, 0.25f) * Mathf.curve(fout, 0, 0.3f) * fout;

        Tmp.v1.set(x, y).rotate(params.rotation - 90 + rt);
        float px = params.x + Tmp.v1.x, py = params.y + Tmp.v1.y;
        for(int i = 0; i <= length / spacing; i++){
            Tmp.v1.trns(params.rotation + rt, i * spacing);
            float f = Interp.pow3Out.apply(Mathf.clamp((fout * length - i * spacing) / spacing)) * (0.6f + track * 0.4f) * wp;
            Draw.rect(Core.atlas.find(name("aim-shoot")), px + Tmp.v1.x, py + Tmp.v1.y, 120 * Draw.scl * f, 120 * Draw.scl * f, params.rotation - 90 + rt);
        }
        if(!drawLine) return;
        Tmp.v1.trns(params.rotation + rt, 0, (2 - track) * Vars.tilesize * width);
        Lines.stroke(track * 2 * wp);
        for(int i : Mathf.signs){
            Lines.lineAngle(px + Tmp.v1.x * i, py + Tmp.v1.y * i, params.rotation + rt, length * (0.75f + track / 4) * Mathf.curve(fout, 0, 0.1f));
        }
    }

    @Override
    public void load(String s) {

    }
}
