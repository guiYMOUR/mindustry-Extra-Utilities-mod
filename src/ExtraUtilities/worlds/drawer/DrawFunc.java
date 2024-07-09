package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;

import java.time.LocalDateTime;

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

    public static void drawClockTable(Building build, int size, float clockSize, float fontSize, float fontHeight, Color backColor){
        float z = Draw.z();
        Draw.z(Layer.max);

        float totalHeight = clockSize + fontHeight;
        float dy = build.y + size/2f * Vars.tilesize;
        float bx = dy + totalHeight/2f;
        Draw.color(backColor);
        Fill.rect(build.x, bx, clockSize, totalHeight);
        float cy = dy + fontHeight + clockSize/2f;
        Draw.color(Pal.lightishGray);
        float showSize = clockSize/2f;
        Fill.circle(build.x, cy, showSize);
        float angle = 90;
        for(int i = 12; i > 0; i--){
            float len = 5;
            if(i % 3 == 0) len *= 1.8f;
            float lx = EUGet.dx(build.x, showSize, angle);
            float ly = EUGet.dy(cy, showSize, angle);
            Lines.stroke(2, Color.white);
            Lines.lineAngle(lx, ly, angle + 180, len);
            Lines.stroke(1f, Color.black);
            Lines.lineAngle(lx, ly, angle + 180, len);

            angle += 30;
        }
        var localDate = LocalDateTime.now();
        var s = localDate.getSecond();
        var m = localDate.getMinute();
        var h = localDate.getHour();
        var mt = localDate.getMonthValue();
        var dt = localDate.getDayOfMonth();

        float ha = 90 - (h % 12) * 30 - 30 * (m / 60f);
        Lines.stroke(3, Color.black);
        Lines.lineAngle(build.x, cy, ha, showSize * 0.5f);
        Lines.stroke(1.5f, Color.white);
        Lines.lineAngle(build.x, cy, ha, showSize * 0.5f);
        float ma = 90 - (m / 60f * 360f) - 6 * (s / 60f);
        Lines.stroke(3, Color.black);
        Lines.lineAngle(build.x, cy, ma, showSize * 0.8f);
        Lines.stroke(1.5f, Color.white);
        Lines.lineAngle(build.x, cy, ma, showSize * 0.8f);
        float sa = 90 - (s / 60f * 360f);
        Lines.stroke(1, Color.white);
        Lines.lineAngle(build.x, cy, sa, showSize * 0.9f);
        Lines.stroke(0.5f, Color.black);
        Lines.lineAngle(build.x, cy, sa, showSize * 0.9f);

        float fy = dy + fontHeight/2f;
        Fonts.outline.draw(zeroTime(h) + " : " + zeroTime(m) + " : " + zeroTime(s), build.x, fy + fontSize * Vars.tilesize, Color.white, fontSize, false, Align.center);
        Fonts.outline.draw(zeroTime(mt) + " M " + zeroTime(dt) + " D ", build.x, fy - fontSize * Vars.tilesize, Color.white, fontSize, false, Align.center);

        Draw.reset();
        Draw.z(z);
    }

    public static void drawClockTable(Building build, int size, float clockSize, float fontSize, float fontHeight, Color backColor, TextureRegion clock, TextureRegion st, TextureRegion mt, TextureRegion ht){
        drawClockTable(Layer.max, build, size, clockSize, fontSize, fontHeight, backColor, clock, st, mt, ht);
    }
    public static void drawClockTable(float layer, Building build, int size, float clockSize, float fontSize, float fontHeight, Color backColor, TextureRegion clock, TextureRegion st, TextureRegion mt, TextureRegion ht){
        float z = Draw.z();
        if(layer >= Layer.min) Draw.z(layer);

        float totalHeight = clockSize + fontHeight;
        float dy = build.y + size/2f * Vars.tilesize;
        float by = size > 0 ? dy + totalHeight/2f : build.y;
        Draw.color(backColor);
        Fill.rect(build.x, by, clockSize, totalHeight);
        float cy = size > 0 ? dy + fontHeight + clockSize/2f : build.y;
        Draw.color();
        Draw.rect(clock, build.x, cy, clockSize, clockSize);
        var localDate = LocalDateTime.now();
        var s = localDate.getSecond();
        var m = localDate.getMinute();
        var h = localDate.getHour();
        var mth = localDate.getMonthValue();
        var day = localDate.getDayOfMonth();

        float ha = - (h % 12) * 30 - (m % 60) / 2f;
        Draw.rect(ht, build.x, cy, clockSize, clockSize, ha);
        float ma = - (m / 60f * 360f) - (s % 60) / 10f;
        Draw.rect(mt, build.x, cy, clockSize, clockSize, ma);
        float sa = - (s / 60f * 360f);
        Draw.rect(st, build.x, cy, clockSize, clockSize, sa);

        if(fontHeight > 0) {
            float fy = dy + fontHeight / 2f;
            Fonts.outline.draw(zeroTime(h) + " : " + zeroTime(m) + " : " + zeroTime(s), build.x, fy + fontSize * Vars.tilesize, Color.white, fontSize, false, Align.center);
            Fonts.outline.draw(zeroTime(mth) + " M " + zeroTime(day) + " D ", build.x, fy - fontSize * Vars.tilesize, Color.white, fontSize, false, Align.center);

        }
        Draw.reset();
        Draw.z(z);
    }

    public static String zeroTime(int time){
        if(time < 10) return "0" + time;
        else return "" + time;
    }
}