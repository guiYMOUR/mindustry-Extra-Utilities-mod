package ExtraUtilities.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.*;
import static mindustry.content.Fx.*;

public class EUFx {

    public static Effect StormExp(Color cor, Color liC) {
        return new Effect(60f, e -> {
            Draw.color(liC, cor, e.fin());
            Fill.circle(e.x, e.y, e.fout() * 40);
            Lines.stroke(e.fout() * 4.5f);
            Lines.circle(e.x, e.y, e.fin() * 60);
            Lines.stroke(e.fout() * 2.75f);
            Lines.circle(e.x, e.y, e.fin() * 30);
            randLenVectors(e.id, 45, 1 + 65 * e.fin(), e.rotation, 360, (x, y) -> {
                Lines.stroke(e.fout() * 2);
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 1);
            });
            randLenVectors(e.id, 85, 1 + 160 * e.fin(),  Time.time * 4, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 10);
            });
        });
    }

    public static Effect flameShoot(Color colorBegin, Color colorTo, Color colorEnd, float length, float cone, int number, float lifetime){
        return new Effect(lifetime, 80, e -> {
                Draw.color(colorBegin, colorTo, colorEnd, e.fin());
            randLenVectors(e.id, number, e.finpow() * length, e.rotation, cone, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
            });
        });
    }

    public static Effect elDsp(Color cor, Color cor2) {
        return new Effect(20, e -> {
            Draw.color(cor,cor2,e.fin());
            Lines.stroke(e.fout() * 3);
            Lines.circle(e.x, e.y, e.fin() * 60);
            Lines.stroke(e.fout() * 1.75f);
            Lines.circle(e.x, e.y, e.fin() * 45);
            Draw.color(cor);
            Fill.circle(e.x, e.y, e.fout() * 20);
            Draw.color(cor,cor2,e.fin());
            Fill.circle(e.x, e.y, e.fout() * 14);
        });
    }

    public static Effect absorbEffect = new Effect(38, e -> {
        Draw.color(Items.sand.color);
        Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3 + 1);
            Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 2);
        });
    });

    public static Effect missileTrailSmokeSmall = new Effect(90f, 90f, b -> {
        float intensity = 1.1f;

        color(b.color, 0.7f);
        for(int i = 0; i < 3; i++){
            rand.setSeed(b.id* 2L + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2f * intensity), 9f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 1.2f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 1.2f, b.color, 0.5f);
                });
            });
        }
    }).layer(Layer.bullet - 1f);

}
