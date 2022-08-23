package ExtraUtilities.worlds.blocks.production;

import arc.graphics.g2d.TextureRegion;
import mindustry.type.Item;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DrawSolidPump extends SolidPump {
    public boolean defaultDraw = false;
    public boolean cons = true;
    //public float boost = 0;
    //public Item consItem = null;

    public DrawSolidPump(String name){
        super(name);
    }

//    @Override
//    public void setStats() {
//        super.setStats();
//        if(boost > 1) stats.add(Stat.boostEffect, boost, StatUnit.timesSpeed);
//    }

    @Override
    public TextureRegion[] icons(){
        return defaultDraw ? super.icons() : drawer.icons(this);
    }

    public class DrawSolidPumpBuild extends SolidPumpBuild{
        @Override
        public boolean shouldConsume(){
            return liquids.get(result) < liquidCapacity - 0.01f && enabled;
        }

//        public boolean checkConsume(){
//            for(int i = 0; i < content.items().size; i ++){
//                Item item = content.item(i);
//                if(item != null && block.consumesItem(item)){
//                    if (items.get(item) > 0){
//                        continue;
//                    }
//                } else {
//                    continue;
//                }
//                return false;
//            }
//            return true;
//        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(cons && efficiency > 0){
                if((consTimer += edelta()) >= consumeTime){
                    consume();
                    consTimer = 0f;
                }
            }
        }

        @Override
        public void draw() {
            if(defaultDraw) super.draw();
            else drawer.draw(this);
        }
    }
}
