package ExtraUtilities.worlds.blocks.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.io.*;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class TurretResupplyPoint extends Block {
    public float range = 64f;

    public TurretResupplyPoint(String name) {
        super(name);
        configurable = true;
        saveConfig = true;
        solid = true;
        update = true;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        hasItems = true;
        itemCapacity = 1;

        config(Item.class, (TurretResupplyPointBuild tile, Item item) -> tile.sortItem = item);
        configClear((TurretResupplyPointBuild tile) -> tile.sortItem = null);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + this.offset, range, Pal.accent);
    }

    public class TurretResupplyPointBuild extends Building {
        public Item sortItem = null;

        @Override
        public void updateTile() {
            indexer.eachBlock(this, range, b -> (b.block instanceof Turret && b.block.hasItems), b ->{
                if((!hasPower || power.status > 0.999) && sortItem != null && items.get(sortItem) > 0 && b.acceptItem(this, sortItem)){
                    b.handleItem(this, sortItem);
                    Fx.itemTransfer.at(x, y, 2, sortItem.color, b);
                    items.clear();
                }
            });
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(sortItem == null ? Color.clear : sortItem.color);
            Draw.rect(Core.atlas.find("unloader-center"), x, y);
            Draw.color();
        }

        @Override
        public void drawConfigure() {
            super.drawConfigure();
            indexer.eachBlock(this, range, b -> (b.block instanceof Turret && b.block.hasItems), b -> {
                if(sortItem != null && b.acceptItem(this, sortItem)) Drawf.square(b.x, b.y, b.block.size * tilesize / 2f + 1, sortItem.color);
            });
            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return item == sortItem && items.get(item) < 1;
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(TurretResupplyPoint.this, table, content.items(), () -> sortItem, this::configure);
        }

        @Override
        public Item config(){
            return sortItem;
        }

        @Override
        public BlockStatus status() {
            return (!hasPower || power.status > 0.999) ? BlockStatus.active : BlockStatus.noInput;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortItem = id == -1 ? null : content.item(id);
        }
    }
}
