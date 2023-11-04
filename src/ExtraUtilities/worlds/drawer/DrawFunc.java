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
        //good!
        Lines.arc(x, y, rad, percent, angle);
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