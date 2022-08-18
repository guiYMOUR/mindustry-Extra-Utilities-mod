package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.distribution.InvertedJunction;
import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.draw.*;

import static ExtraUtilities.ExtraUtilitiesMod.ModName;

public class DrawInvertedJunction extends DrawBlock {
    @Override
    public void draw(Building build) {
        InvertedJunction block = (InvertedJunction) build.block;
        InvertedJunction.InvertedJunctionBuild b = (InvertedJunction.InvertedJunctionBuild) build;
        drawB(block, b);
    }
    public void drawB(InvertedJunction block, InvertedJunction.InvertedJunctionBuild build){
        Draw.rect(Core.atlas.find(block.placeSprite), build.x, build.y);
        Draw.rect(Core.atlas.find(ModName + "-junction-" + build.loc), build.x, build.y);
    }
}
