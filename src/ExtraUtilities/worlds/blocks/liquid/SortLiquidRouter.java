package ExtraUtilities.worlds.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Edges;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidRouter;

import static mindustry.Vars.*;

public class SortLiquidRouter extends LiquidRouter {
    //unfinished?
    public SortLiquidRouter(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        rotate = true;
        rotateDraw = false;

        config(Liquid.class, (SortLiquidBuild tile, Liquid l) -> tile.sortLiquid = l);
        configClear((SortLiquidBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list){
        if(configurable) drawPlanConfigCenter(plan, plan.config, name + "-config", false);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), rotate ? plan.rotation * 90 : 0);
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region, topRegion};
    }

    public class SortLiquidBuild extends LiquidRouterBuild{
        public Liquid sortLiquid = null;

        @Override
        public void updateTile() {
            super.updateTile();
            if(sortLiquid != null && liquids.current() != sortLiquid) liquids.clear();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(topRegion, x, y, rotate ? rotdeg() : 0);
            if(sortLiquid != null) {
                Draw.color(sortLiquid.color);
                Draw.rect(Core.atlas.find(name + "-config"), x, y);
            }
            Draw.color();
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(SortLiquidRouter.this, table, content.liquids(), () -> sortLiquid, this::configure);
        }

        @Override
        public Liquid config(){
            return sortLiquid;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            //int rel = this.relativeToEdge(source.tile);
            return (!rotate || Edges.getFacingEdge(source.tile, tile).relativeTo(tile) == rotation) && super.acceptLiquid(source, liquid) && (sortLiquid == null || sortLiquid == liquid);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortLiquid = id == -1 ? null : content.liquid(id);
        }
    }
}
