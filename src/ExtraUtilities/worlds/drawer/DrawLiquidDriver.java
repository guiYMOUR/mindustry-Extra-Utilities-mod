package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver;
import ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver.*;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.draw.DrawBlock;

public class DrawLiquidDriver extends DrawBlock {
    @Override
    public void draw(Building build) {
        LiquidMassDriver block = (LiquidMassDriver) build.block;
        LiquidMassDriverBuild b = (LiquidMassDriverBuild) build;
        drawDriver(block, b);
    }
    public void drawDriver(LiquidMassDriver block, LiquidMassDriverBuild build){
        Draw.rect(Core.atlas.find(block.name + "-base"), build.x, build.y);
        Draw.z(Layer.turret);
        Drawf.shadow(Core.atlas.find(block.name + "-region"),
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback) - (block.size / 2f),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback) - (block.size / 2f), build.rotation - 90);
        Draw.rect(Core.atlas.find(block.name + "-bottom"),
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.rect(Core.atlas.find(block.name + "-region"),
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.color(build.liquids.current().color);
        Draw.alpha(Math.min(build.liquidTotal() / block.liquidCapacity, 1));
        Draw.rect(Core.atlas.find(block.name + "-liquid"),
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.color();
        Draw.alpha(1);
        Draw.rect(Core.atlas.find(block.name + "-top"),
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
    }
}
