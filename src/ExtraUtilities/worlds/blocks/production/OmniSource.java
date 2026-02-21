package ExtraUtilities.worlds.blocks.production;


import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
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

        @Override
        public void updateTile(){
            for(Item i : content.items()){
                items.set(i, 1);
                dump(i);
                items.set(i, 0);
            }

            for(Liquid l : content.liquids()){
                this.liquids.clear();
                this.liquids.set(l, liquidCapacity);
                this.dumpLiquid(l);
            }
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
    }
}
