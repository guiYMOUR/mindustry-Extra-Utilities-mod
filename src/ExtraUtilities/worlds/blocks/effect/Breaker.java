package ExtraUtilities.worlds.blocks.effect;

import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;

public class Breaker extends Block {
    public float timerBreak;
    public int maxsize;

    public Breaker(String name) {
        super(name);
        size = 1;
        rotate = true; //旋转找目标
        drawArrow = false;
        update = //设置了自爆，update是必要的，基本update设置true就行了
                solid = //设置固态，其实没必要
                        destructible = true;
        maxsize = 1;
        timerBreak = 300;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        x *= Vars.tilesize;
        y *= Vars.tilesize;
        x += offset;
        y += offset;
        Rect rect = Tmp.r1;
        rect.setCentered(x, y, maxsize * Vars.tilesize);
        int len = Vars.tilesize * maxsize;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    public class BreakerBuild extends Building{
        public float Timer = 0;

        @Override
        public void updateTile() {
            Tile tile = Vars.world.tile(tileX() + Geometry.d4x(rotation), this.tileY() + Geometry.d4y(rotation));
            if(tile != null && tile.block() != null && tile.build == null && tile.block().solid && !tile.block().breakable && tile.block().size <= maxsize){
                Timer += Time.delta;
                if(Timer >= timerBreak){
                    Vars.world.tile(tile.x, tile.y).setAir();
                    kill();
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            int s = Mathf.ceil((timerBreak - Timer)/60);
            String  text = "|" + s + "|";

            Fonts.def.draw(text, this.x, this.y, Color.red, 0.35f, true, Align.center);
        }
    }
}
