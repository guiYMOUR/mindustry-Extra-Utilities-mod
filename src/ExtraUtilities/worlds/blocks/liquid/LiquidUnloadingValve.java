package ExtraUtilities.worlds.blocks.liquid;

import ExtraUtilities.content.EUItems;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.*;

public class LiquidUnloadingValve extends Block {
    public String center;
    public String arrow;
    public float speed = 10f;

    public LiquidUnloadingValve(String name){
        super(name);

        update = true;
        solid = true;
        configurable = true;
        outputsLiquid = true;
        saveConfig = true;
        noUpdateDisabled = true;
        displayFlow = false;
        group = BlockGroup.liquids;
        envEnabled = Env.any;
        clearOnDoubleTap = true;
        rotate = true;

        config(Liquid.class, (LiquidUnloadingValveBuild tile, Liquid l) -> tile.sortLiquid = l);
        configClear((LiquidUnloadingValveBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void load() {
        super.load();
        center = name + "-center";
        arrow = name + "-arrow";
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
        addBar("back", (LiquidUnloadingValveBuild entity) ->
                new Bar(() ->
                        Core.bundle.get("bar.input"),
                        () -> entity.sortLiquid == null ? Color.black : entity.sortLiquid.color,
                        () -> {
                            if(entity.sortLiquid != null && entity.back() != null && entity.back().block != null && entity.back().block.hasLiquids && entity.back().block.liquidCapacity > 0) {
                                return (entity.back().liquids.get(entity.sortLiquid)/entity.back().block.liquidCapacity);
                            } else return 0;
                        }

                )
        );
        addBar("front", (LiquidUnloadingValveBuild entity) ->
                new Bar(() ->
                        Core.bundle.get("bar.output"),
                        () -> entity.sortLiquid == null ? Color.black : entity.sortLiquid.color,
                        () -> {
                            if(entity.sortLiquid != null && entity.front() != null && entity.front().block != null && entity.front().block.hasLiquids && entity.front().block.liquidCapacity > 0) {
                                return (entity.front().liquids.get(entity.sortLiquid)/entity.front().block.liquidCapacity);
                            } else return 0;
                        }
                )
        );
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Draw.rect(Core.atlas.find(name), x, y, 0);
        Draw.rect(Core.atlas.find(name + "-top"), x, y, 0);
        Draw.rect(Core.atlas.find(arrow), x, y, rotation);
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top"), Core.atlas.find(arrow)};
    }

    public class LiquidUnloadingValveBuild extends Building{
        public Liquid sortLiquid = null;

        @Override
        public void updateTile() {
            Building front = front(), back = back();
            if(front != null && back != null && front.block != null && back.block != null && back.liquids != null && front.team == team && back.team == team && sortLiquid != null){
                if(front.acceptLiquid(this, sortLiquid)){
                    float fl = front.liquids.get(sortLiquid), bl = back.liquids.get(sortLiquid), fc = front.block.liquidCapacity, bc = back.block.liquidCapacity;
                    if(bl > 0 && bl/bc > fl/fc) {
                        float amount = Math.min(speed, back.liquids.get(sortLiquid));
                        float a = Math.min(amount, front.block.liquidCapacity - front.liquids.get(sortLiquid));
                        float balance = Math.min(a, (bl / bc - fl / fc) * bc);
                        front.handleLiquid(this, sortLiquid, balance);
                        back.liquids.remove(sortLiquid, balance);
                    }
                }
            }
        }
        @Override
        public void draw() {
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color(sortLiquid == null ? Color.clear : sortLiquid.color);
            Draw.rect(Core.atlas.find(center), x, y);
            Draw.color();
            Draw.rect(Core.atlas.find(name + "-top"), x, y);
            Draw.rect(Core.atlas.find(arrow), x, y, rotdeg());
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(LiquidUnloadingValve.this, table, content.liquids(), () -> sortLiquid, this::configure);
        }

        @Override
        public Liquid config(){
            return sortLiquid;
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
