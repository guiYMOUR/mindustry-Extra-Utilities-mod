package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUItems;
import arc.Core;
import arc.audio.Audio;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import javax.sound.sampled.AudioSystem;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class ArbiterPart extends DrawPart {
    //public static ArbParams params;
    public int amount;
    public float fade = 0.2f;
    public float fadeInterval = 3;
    public Color lineColor = EUItems.lightninAlloy.color;
    public TextureRegion arrow;

    public float rotSpeed = 1f;
    public float adr = 20;
    public float wx, wy;

    public PartProgress progressWarmup = PartProgress.warmup;
    public PartProgress progressReload = PartProgress.reload;

    private final float[] lines;
    private final Interval timer;

    public ArbiterPart(int amount){
        this.amount = amount;
        lines = new float[amount];
        timer = new Interval(amount);
        //params = new ArbParams(amount);
    }

    @Override
    public void draw(PartParams partParams) {
        float warmup = progressWarmup.getClamp(params);
        float reload = 1 - progressReload.getClamp(params);
        float rot = params.rotation - 90;
        float ax = params.x + Angles.trnsx(rot, wx, wy),
                ay = params.y + Angles.trnsy(rot, wx, wy);
        float z = Draw.z();
        if(warmup > 1e-4f) {
            Draw.z(Layer.effect);
            for (int i = 0; i < amount; i++) {
                float ag = 360f / amount * i + Time.time * rotSpeed;
                float lx = EUGet.dx(ax, adr, ag),
                        ly = EUGet.dy(ay, adr, ag);

                if (timer.get(i, fadeInterval))
                    lines[i] = Mathf.lerpDelta(lines[i], Math.max(0, Mathf.random(-32, 16f * Core.settings.getInt("sfxvol") / 100f)), fade);

                Lines.stroke(2 * warmup, lineColor);
                Lines.lineAngle(lx, ly, ag, lines[i] + 0.1f);
                Lines.lineAngle(lx, ly, ag - 180, lines[i] * 2 + 0.1f);

                if(i < amount /2){
                    float glx = EUGet.dx(ax, adr + 2f * i, rot),
                            gly = EUGet.dy(ay, adr + 2f * i, rot);
                    Lines.stroke(1.5f * warmup, lineColor);
                    Lines.lineAngle(glx, gly, rot + 90, lines[i] * 0.5f + 0.1f);
                    Lines.lineAngle(glx, gly, rot - 90, lines[i] * 0.5f + 0.1f);
                } else {
                    float glx = EUGet.dx(ax, adr + 2f * (i - amount/2f), rot - 180),
                            gly = EUGet.dy(ay, adr + 2f * (i - amount/2f), rot - 180);
                    Lines.stroke(1.5f * warmup, lineColor);
                    Lines.lineAngle(glx, gly, rot + 90, lines[i] * 0.5f + 0.1f);
                    Lines.lineAngle(glx, gly, rot - 90, lines[i] * 0.5f + 0.1f);
                }
            }

            Draw.color(lineColor);
            //Fill.circle(ax, ay, adr/6 * warmup);
            for(int i = 0; i < 3; i++){
                Drawf.tri(ax, ay, adr/4 * warmup, 15 * (i == 0 ? 2 : 1), rot - 90 + 120 * i);
            }

            Draw.color(Tmp.c1.set(lineColor).a(warmup));
            for(int s : Mathf.signs) {
                float aix = params.x + Angles.trnsx(rot, 16 * s, -8),
                        aiy = params.y + Angles.trnsy(rot, 16 * s, -8);

                int sp = 10;
                float ln = 50;
                for (int i = 0; i <= ln / sp; i++) {
                    Tmp.v1.trns(rot + 90, i * sp);
                    float track = Mathf.curve(Interp.pow2Out.apply(reload), 0, 0.25f) * Mathf.curve(Interp.pow4Out.apply(reload), 0, 0.3f) * reload;
                    float f = Interp.pow3Out.apply(Mathf.clamp((reload * ln - i * sp) / sp)) * (0.6f + track * 0.4f);
                    Draw.rect(arrow, aix + Tmp.v1.x, aiy + Tmp.v1.y, 135 * Draw.scl * f, 135 * Draw.scl * f, rot);
                }
            }
            Draw.z(Layer.block - 5);
            Lines.stroke(1.7f * warmup, Tmp.c2.set(lineColor).a(warmup));
            Lines.circle(params.x, params.y, 4.5f * Vars.tilesize);
            for(int i = 0; i < 4; i++){
                float a = i * 90 + Time.time * warmup;
                float rx = EUGet.dx(params.x, 4.5f * Vars.tilesize, a),
                        ry = EUGet.dy(params.y, 4.5f * Vars.tilesize, a);
                Draw.rect(arrow, rx, ry, a + 90);
            }
        }
        Draw.z(z);
    }

    @Override
    public void load(String s) {
        arrow = Core.atlas.find(name("aim-shoot"));
    }
}
