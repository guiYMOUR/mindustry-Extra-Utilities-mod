package ExtraUtilities.worlds.blocks.effect;

import ExtraUtilities.net.EUCall;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/** @author guiY */

public class WaterBomb extends Block {
    public int fillSize = 3;

    public float readyTime = 5 * 60f;

    public boolean forceFill = true;

    public Effect fillEffect = new Effect(60, e -> {
        Draw.color(Tmp.c4.set(Pal.accent).a(e.foutpow()));
        //Draw.color(Pal.accent);
        float size = tilesize * Math.min(1, 3 * e.finpow());
        Fill.rect(e.x, e.y, size, size);
    });

    public WaterBomb(String name) {
        super(name);

        //目前只想了11大小，可以用泵的那个模式来做22及以上的，我就不做了
        size = 1;
        placeableLiquid = true;
        floating = true;
        update = destructible = true;

        rebuildable = false;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile != null && tile.floor() != null && tile.floor().isDeep();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Lines.stroke(1.4f, valid ? Pal.accent : Pal.remove);
        Lines.square(x * tilesize, y * tilesize, fillSize/2f * tilesize);
    }

    public class WaterBombBuild extends Building{
        public float ready = 0;

        @Override
        public void updateTile() {
            ready += Time.delta;
            if(ready >= readyTime){
                boom();
                kill();
            }
        }

        private void boom(){
            int tx = tile.x, ty = tile.y;
            int range = fillSize/2;
            for(int fx = -range; fx <= range; fx++){
                for(int fy = -range; fy <= range; fy++){
                    Tile ft = world.tile(tx + fx, ty + fy);
                    if(ft != null && ft.floor() != null && ft.floor().isDeep()){
                        fillEffect.at(ft.worldx(), ft.worldy());
                        EUCall.setFloor(ft, Blocks.metalFloor);
                    }
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            int s = Mathf.ceil((readyTime - ready)/60);
            String  text = "|" + s + "|";

            Fonts.def.draw(text, this.x, this.y, Color.red, 0.35f, true, Align.center);

            Draw.color(Pal.gray);
            Draw.alpha(0.4f);
            Fill.rect(x, y, fillSize * tilesize, fillSize * tilesize);
        }
    }
}
