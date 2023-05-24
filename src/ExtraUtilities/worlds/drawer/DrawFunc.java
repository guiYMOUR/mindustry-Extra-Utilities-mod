package ExtraUtilities.worlds.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.graphics.Drawf;

public class DrawFunc {
    public static void circlePercent(float x, float y, float rad, float percent, float angle){
        float p = Mathf.clamp(percent);
        if(p < 0.001) return;
        int sides = Lines.circleVertices(rad);
        float space = 360f / sides;
        float len = 2 * rad * Mathf.sinDeg(space / 2);
        float hstep = Lines.getStroke() / 2 / Mathf.cosDeg(space / 2);
        float r1 = rad - hstep;
        float r2 = rad + hstep;
        int i;
        for(i = 0; i < sides * p - 1; ++i){
            float a = space * i + angle;
            float cos = Mathf.cosDeg(a);
            float sin = Mathf.sinDeg(a);
            float cos2 = Mathf.cosDeg(a + space);
            float sin2 = Mathf.sinDeg(a + space);
            Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2, y + r1 * sin2, x + r2 * cos2, y + r2 * sin2, x + r2 * cos, y + r2 * sin);
        }
        float a = space * i + angle;
        float cos = Mathf.cosDeg(a);
        float sin = Mathf.sinDeg(a);
        float cos2 = Mathf.cosDeg(a + space);
        float sin2 = Mathf.sinDeg(a + space);
        float f = sides * p - i;
        Vec2 vec = new Vec2();
        vec.trns(a, 0, len * (f - 1));
        Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2 + vec.x, y + r1 * sin2 + vec.y, x + r2 * cos2 + vec.x, y + r2 * sin2 + vec.y, x + r2 * cos, y + r2 * sin);
    }

    public static void drawSnow(float x, float y, float rad, float rot, Color color){
        Draw.color(color);
        for(int i = 0; i < 6; i++){
            float angle = 60 * i + rot;
            Drawf.tri(x + Angles.trnsx(angle, rad), y + Angles.trnsy(angle, rad), rad/3, rad, angle - 180);
            Drawf.tri(x + Angles.trnsx(angle, rad), y + Angles.trnsy(angle, rad), rad/3, rad/4, angle);
        }
        Draw.reset();
    }
}