package ExtraUtilities.worlds.blocks.production;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.Incinerator;

public class IntelligentIncinerator extends Incinerator {
    public IntelligentIncinerator(String name) {
        super(name);

        configurable = true;

        config(Object[].class, (IntelligentBuild ent, Object[] objects) -> {
            if(objects.length == 2 && objects[1] instanceof Item[] items && objects[2] instanceof Liquid[] liquids){

                ent.items.set(items);
                ent.liquids.set(liquids);
            }
        });
    }

    public class IntelligentBuild extends IncineratorBuild{
        public Seq<Item> items = new Seq<>();
        public Seq<Liquid> liquids = new Seq<>();
        public Seq<Object> conf = new Seq<>();

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
        }

        @Override
        public Object[] config() {
            conf.clear();
            conf.addAll(items.items, liquids.items);
            return conf.items;
        }
    }
}
