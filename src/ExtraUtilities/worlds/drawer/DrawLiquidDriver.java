package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver;
import ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver.*;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawLiquidDriver extends DrawBlock {
    public TextureRegion base, region, bottom, liquid, top;

    @Override
    public void load(Block block) {
        base = Core.atlas.find(block.name + "-base");
        region = Core.atlas.find(block.name + "-region");
        bottom = Core.atlas.find(block.name + "-bottom");
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{base, bottom, region, top};
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(base, plan.drawx(), plan.drawy());
        Draw.rect(bottom, plan.drawx(), plan.drawy());
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(top, plan.drawx(), plan.drawy());
    }

    @Override
    public void draw(Building build) {
        LiquidMassDriver block = (LiquidMassDriver) build.block;
        LiquidMassDriverBuild b = (LiquidMassDriverBuild) build;
        drawDriver(block, b);
    }
    public void drawDriver(LiquidMassDriver block, LiquidMassDriverBuild build){
        Draw.rect(base, build.x, build.y);
        Draw.z(Layer.turret);
        Drawf.shadow(region,
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback) - (block.size / 2f),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback) - (block.size / 2f), build.rotation - 90);
        Draw.rect(bottom,
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.color(build.liquids.current().color);
        Draw.alpha(Math.min(build.liquidTotal() / block.liquidCapacity, 1));
        Draw.rect(liquid,
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.color();
        Draw.rect(region,
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
        Draw.alpha(1);
        Draw.rect(top,
                build.x + Angles.trnsx(build.rotation + 180, build.reloadCounter * block.knockback),
                build.y + Angles.trnsy(build.rotation + 180, build.reloadCounter * block.knockback), build.rotation - 90);
    }
}
