package ExtraUtilities.content;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Effect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static arc.math.Angles.*;

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

}
