package ExtraUtilities.worlds.blocks.production;

import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class Randomer extends Block {
    public int itemsPerSecond = 100;

    public Randomer(String name){
        super(name);
        hasItems = true;
        update = true;
        solid = true;
        noUpdateDisabled = true;
        envEnabled = Env.any;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
    }


    @Override
    public boolean outputsItems(){
        return true;
    }

    public class ItemSourceBuild extends Building {
        public float counter;

        @Override
        public void updateTile(){
            counter += edelta();
            float limit = 60f / itemsPerSecond;
            int r = Mathf.random(content.items().size);
            Item outputItem = content.item(r);
            while(counter >= limit && outputItem != null){
                items.set(outputItem, 1);
                dump(outputItem);
                items.set(outputItem, 0);
                counter -= limit;
            }
        }
    }
}

