package ExtraUtilities.worlds.blocks.production;


import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.meta.Env;

import static mindustry.Vars.content;

public class OmniSource extends Block {
    public float powerProduction = 1000000/60f;
    public float heat = 1000;

    public OmniSource(String name){
        super(name);
        hasItems = true;
        update = true;
        solid = true;
        hasLiquids = true;
        liquidCapacity = 10000;
        noUpdateDisabled = true;
        envEnabled = Env.any;
        alwaysReplace = true;
        hasPower = true;
        consumesPower = false;
        outputsPower = true;

        configurable = true;
        copyConfig = true;
        saveConfig = true;
        clearOnDoubleTap = true;

        config(Item.class, (FillerBuild build, Item sub) -> {
            if(sub != build.select[0]) build.select[0] = sub;
        });
        config(Liquid.class, (FillerBuild build, Liquid sub) -> {
            if(sub != build.select[1]) build.select[1] = sub;
        });

        config(Object[].class, (FillerBuild build, Object[] sub) -> build.select = sub);

        configClear((FillerBuild build) -> {
            build.select[0] = null;
            build.select[1] = null;
        });
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
        removeBar("liquid");
    }


    @Override
    public boolean outputsItems(){
        return true;
    }

    public class FillerBuild extends Building implements HeatBlock {
        public Object[] select = new Object[]{null, null};

        @Override
        public void updateTile(){
            Item si = (Item) select[0];
            Liquid sl = (Liquid) select[1];

            if(si != null){
                for(int i = 0; i < 10; i++){
                    items.set(si, 1);
                    dump(si);
                    items.set(si, 0);
                }
            } else {
                for(int j = 0; j < 10; j++) {
                    for (Item i : content.items()) {
                        items.set(i, 1);
                        dump(i);
                        items.set(i, 0);
                    }
                }
            }

            if(sl != null){
                this.liquids.clear();
                this.liquids.set(sl, liquidCapacity);
                this.dumpLiquid(sl);
            } else {
                for (Liquid l : content.liquids()) {
                    this.liquids.clear();
                    this.liquids.set(l, liquidCapacity);
                    this.dumpLiquid(l);
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(OmniSource.this, table, content.items(), () -> (Item) select[0], this::configure, false, selectionRows, selectionColumns);
            ItemSelection.buildTable(OmniSource.this, table, content.liquids(), () -> (Liquid) select[1], this::configure, false, selectionRows, selectionColumns);
        }

        @Override
        public Object[] config() {
            return select;
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return 1;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(select[0] == null ? -1 : ((Item)select[0]).id);
            write.s(select[1] == null ? -1 : ((Liquid)select[1]).id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            select[0] = Vars.content.item(read.s());
            select[1] = Vars.content.liquid(read.s());
        }
    }
}
