package ExtraUtilities.worlds.blocks.production;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.production.Incinerator;

public class LiquidIncinerator extends Incinerator {
    public LiquidIncinerator(String name) {
        super(name);
    }
    public class liquidIncineratorBuild extends IncineratorBuild{
        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }
    }
}
