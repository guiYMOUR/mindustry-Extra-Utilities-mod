package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.*;


public class DrawLAE extends DrawBlock {
    public Color[] colors;
    public float move;
    public float width;
    public float strokeFrom = 2f, strokeTo = 0.5f, pointyScaling = 0.75f;
    public float backLength = 3f, frontLength = 5f;
    public float oscScl = 3f, oscMag = 0.4f;
    public int divisions = 10;

    public DrawLAE(Color[] colors, float move, float width){
        this.colors = colors;
        this.move = move;
        this.width = width;
    }

    @Override
    public void draw(Building build) {
        Block block = build.block;
        float x = build.x, y = build.y;
        if (move == 0) move = block.size / 2f * tilesize;
        if (build.warmup() > 0) {
            for (float mx : new float[]{move, -move}) {
                for (float my : new float[]{move, -move}) {
                    float rx = x + mx, ry = y + my;
                    float rot = EUGet.pos(rx, ry).angleTo(build);
                    float baseLen = EUGet.pos(rx, ry).dst(build)/1.3f * build.warmup();
                    for(int i = 0; i < colors.length; i++){
                        Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
                        Draw.alpha(0.6f);

                        float colorFin = i / (float)(colors.length - 1);
                        float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                        float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * baseStroke * (build.efficiency > 0 ? build.efficiency/2 : build.warmup());
                        float ellipseLenScl = Mathf.lerp(1 - i / (float)(colors.length), 1f, pointyScaling);

                        Lines.stroke(stroke);
                        Lines.lineAngle(rx, ry, rot, (baseLen - frontLength) * build.warmup(), false);

                        //back ellipse
                        Drawf.flameFront(rx, ry, divisions, rot + 180f, backLength, stroke / 2f);

                        //front ellipse
                        Tmp.v1.trnsExact(rot, (baseLen - frontLength) * build.warmup());
                        Drawf.flameFront(rx + Tmp.v1.x, ry + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl * build.warmup(), stroke / 2f);
                    }

                    Tmp.v1.trns(rot, baseLen * 1.1f * build.warmup());

                    Draw.reset();
                }
            }
        }
    }
}
