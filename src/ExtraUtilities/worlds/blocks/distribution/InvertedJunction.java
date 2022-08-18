package ExtraUtilities.worlds.blocks.distribution;

import ExtraUtilities.worlds.drawer.DrawInvertedJunction;
import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.BufferItem;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.draw.*;

import static ExtraUtilities.ExtraUtilitiesMod.*;
import static mindustry.Vars.*;

public class InvertedJunction extends Junction {
    public String placeSprite;
    //为什么写drawer？减少一点点内存消耗
    public DrawBlock drawer = new DrawInvertedJunction();

    public InvertedJunction(String name) {
        super(name);

        sync = true;
        configurable = true;
        //saveConfig = true;
        config(Integer.class, (InvertedJunctionBuild build, Integer loc) -> build.loc = loc);
    }
    public class InvertedJunctionBuild extends JunctionBuild{
        public int loc = 1;

        @Override
        public void configured(Unit player, Object value) {
            super.configured(player, value);
            loc = (int)value;
        }

        @Override
        public void updateTile() {
            for(int i = 0; i < 4; i++){
                int p = (i + loc) % 4;
                if(buffer.indexes[i] > 0){
                    if(buffer.indexes[i] > capacity) buffer.indexes[i] = capacity;
                    long l = buffer.buffers[i][0];
                    float time = BufferItem.time(l);

                    if(Time.time >= time + speed / timeScale || Time.time < time){

                        Item item = content.item(BufferItem.item(l));
                        Building dest = nearby(p);

                        //skip blocks that don't want the item, keep waiting until they do
                        if(item == null || dest == null || !dest.acceptItem(this, item) || dest.team != team){
                            continue;
                        }

                        dest.handleItem(this, item);
                        System.arraycopy(buffer.buffers[i], 1, buffer.buffers[i], 0, buffer.indexes[i] - 1);
                        buffer.indexes[i] --;
                    }
                }
            }
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            int relative = source.relativeTo(tile);
            if(relative == -1 || !buffer.accepts(relative)) return false;
            Building to = nearby((relative + loc) % 4);
            return to != null && to.team == team;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.button(new TextureRegionDrawable(Core.atlas.find(ModName + "-flip", Core.atlas.find("clear"))), Styles.cleari, this::switchf).size(36f).tooltip("switch");
        }


        public void switchf(){
            loc = loc == 1 ? 3 : 1;
            deselect();
            configure(loc);

        }

        @Override
        public Integer config() {
            return loc;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(loc);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            loc = read.i();
        }
    }
}
