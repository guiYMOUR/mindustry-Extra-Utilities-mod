package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.ai.MinerPointAI;
import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.net.EUCall;
import arc.Core;
import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.core.World;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class MinerPoint extends Block {
    //猫在钻头里面定义了这个为不可挖的矿，虽然只能写一个。。。但是应该不错了
    public @Nullable Item blockedItem;

    public int range = 12;
    public int tier = 2;
    public int dronesCreated = 3;
    public float droneConstructTime = 60f * 5f;
    public float polyStroke = 1.8f, polyRadius = 8f;
    public int polySides = 6;
    public float polyRotateSpeed = 1f;
    public Color polyColor = Color.valueOf("92dd7e");
    public boolean alwaysCons = false;
    public boolean limitSize = true;

    public UnitType MinerUnit = EUUnitTypes.miner;

    public boolean canPickUp = false;

    public MinerPoint(String name) {
        super(name);
        size = 3;
        solid = true;
        update = true;
        hasItems = true;
        hasPower = true;
        itemCapacity = 200;
        configurable = true;
        copyConfig = false;
        sync = true;
        buildCostMultiplier = 0;
        flags = EnumSet.of(BlockFlag.factory);

        config(Tile.class, (MinerPointBuild tile, Tile t) -> tile.sortTile = t);
        configClear((MinerPointBuild tile) -> tile.sortTile = null);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if(world.tile(x, y) != null) {
            if (!canPlaceOn(world.tile(x, y), player.team(), rotation)) {
                drawPlaceText(Core.bundle.get(
                        (player.team().core() != null && player.team().core().items.has(requirements, state.rules.buildCostMultiplier)) || state.rules.infiniteResources ?
                                "bar.extra-utilities-close" :
                                "bar.noresources"
                ), x, y, valid);
            }
        }
        x *= tilesize;
        y *= tilesize;

        Drawf.dashSquare(Pal.accent, x, y, range * tilesize * 2);
    }

    public Rect getRect(Rect rect, float x, float y, float range){
        rect.setCentered(x, y, range * 2 * tilesize);

        return rect;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        CoreBuild core = team.core();
        if(core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier))) return false;
        if(!limitSize) return true;
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, range).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.factory).contains(b -> {
            if(b instanceof MinerPointBuild) {
                MinerPointBuild build = (MinerPointBuild) b;
                MinerPoint block = (MinerPoint) b.block;
                return getRect(Tmp.r2, build.x, build.y, block.range).overlaps(rect);
            }
            return false;
        });
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.drillTier, StatValues.blocks(b -> {
            if(b instanceof  Floor){
                Floor f = (Floor) b;
                return (f.wallOre && f.itemDrop != null && f.itemDrop != blockedItem && f.itemDrop.hardness <= tier) || (!f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && (indexer.isBlockPresent(f) || state.isMenu()));
            } else if(b instanceof StaticWall){
                StaticWall w = (StaticWall) b;
                return w.itemDrop != null && w.itemDrop != blockedItem && w.itemDrop.hardness <= tier;
            } else {
                return false;
            }
        }));
        stats.add(Stat.range, range);
        stats.remove(Stat.buildTime);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("units", (MinerPointBuild e) ->
                new Bar(
                        () ->
                                Core.bundle.format("bar.unitcap",
                                        Fonts.getUnicodeStr(MinerUnit.name),
                                        e.units.size,
                                        dronesCreated
                                ),
                        () -> Pal.power,
                        () -> (float)e.units.size / dronesCreated
                ));
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    public static void minerPointDroneSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof MinerPointBuild)) return;
        MinerPointBuild build = (MinerPointBuild)tile.build;
        build.spawned(id);
    }

    public class MinerPointBuild extends Building {
        public @Nullable Tile sortTile = null;
        public Seq<Tile> tiles = new Seq<>();
        protected IntSeq readUnits = new IntSeq();
        protected IntSeq whenSyncedUnits = new IntSeq();

        public Seq<Unit> units = new Seq<>();
        public float droneWarmup, powerWarmup;
        public float warmup, readyness;
        public float droneProgress, totalDroneProgress;

        @Override
        public void updateTile() {

            dumpAccumulate();

            if(!readUnits.isEmpty()){
                units.clear();
                readUnits.each(i -> {
                    Unit unit = Groups.unit.getByID(i);
                    if(unit != null){
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            //read newly synced drones on client end
            //same as UnitAssembler
            if(units.size < dronesCreated && whenSyncedUnits.size > 0){
                whenSyncedUnits.each(id -> {
                    Unit unit = Groups.unit.getByID(id);
                    if(unit != null){
                        units.addUnique(unit);
                    }
                });
            }

            units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof MinerPointAI));

            if(!allowUpdate()){
                droneProgress = 0f;
                units.each(Unit::kill);
                units.clear();
            }

            float powerStatus = power == null ? 1f : power.status;
            powerWarmup = Mathf.lerpDelta(powerStatus, powerStatus > 0.0001f ? 1f : 0f, 0.1f);
            droneWarmup = Mathf.lerpDelta(droneWarmup, units.size < dronesCreated ? powerStatus : 0f, 0.1f);
            totalDroneProgress += droneWarmup * edelta();
            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, units.size == dronesCreated ? 1f : 0f, 1f / 60f);

            if(units.size < dronesCreated && (droneProgress += edelta() * state.rules.unitBuildSpeed(team) * powerStatus / droneConstructTime) >= 1f){
                if(!net.client()){
                    Unit unit = MinerUnit.create(team);
                    if(unit instanceof BuildingTetherc){
                        BuildingTetherc bt = (BuildingTetherc) unit;
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();
                    units.add(unit);
                    EUCall.minerPointDroneSpawned(tile, unit.id);
                }
            }

            if(units.size >= dronesCreated){
                droneProgress = 0f;
            }
            for(int i = 0; i < units.size; i++){
                Unit unit = units.get(i);
                MinerPointAI ai = (MinerPointAI)unit.controller();
                ai.ore = alwaysCons ? efficiency > 0.4 ? sortTile : null : sortTile;
            }
        }

        @Override
        public boolean canPickup() {
            return canPickUp;
        }

        @Override
        public void pickedUp() {
            if(canPickUp) configure(null);
        }

        @Override
        public void draw() {
            //same as UnitCargoLoader
            Draw.rect(block.region, x, y);
            if(units.size < dronesCreated){
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(this, MinerUnit.fullIcon, 0f, droneProgress, warmup, totalDroneProgress);
                });
            }else{
                Draw.z(Layer.bullet - 0.01f);
                Draw.color(polyColor);
                Lines.stroke(polyStroke * readyness);
                Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed);
                Draw.reset();
                Draw.z(Layer.block);
            }
        }

        @Override
        public void drawConfigure() {
            super.drawConfigure();

            Drawf.dashSquare(Pal.accent, x, y, range * tilesize * 2);

            if(sortTile == null) return;
            Tmp.v1.set(sortTile.getX(), sortTile.getY()).sub(x, y).limit((size / 2f + 1) * tilesize + 0.5f);
            float xx = x + Tmp.v1.x, yy = y + Tmp.v1.y;
            int segs = (int)(dst(sortTile.getX(), sortTile.getY()) / tilesize);
            Lines.stroke(4f, Pal.gray);
            Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
            Lines.stroke(2f, Pal.accent);
            Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
            Drawf.square(sortTile.getX(), sortTile.getY(), 5, Pal.accent);
        }

        public void spawned(int id){
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if(net.client()){
                whenSyncedUnits.add(id);
            }
        }

        public boolean checkOre(Tile t){
            return Mathf.equal(tile.x, t.x, range) && Mathf.equal(tile.y, t.y, range);
        }

        private boolean validOre(Tile tile, Item item){
            return (tile.solid() && tile.wallDrop() != null && tile.wallDrop() == item) || (tile.block() == Blocks.air && tile.drop() != null && tile.drop() == item);
        }
        private Tile findOre(Item item){
            int tx = World.toTile(x), ty = World.toTile(y);
            Tile result = null;
            int tr = range;
            for(int x = -tr; x <= tr; x++){
                for(int y = -tr; y <= tr; y++){
                    if(result != null) break;
                    Tile other = world.tile(x + tx, y + ty);
                    if(other != null && checkOre(other) && validOre(other, item)){
                        result = other;
                    }
                }
            }
            return result;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return Math.min(itemCapacity - items.get(item), amount);
        }

        @Override
        public void buildConfiguration(Table table) {
            tiles.clear();//直接清除，因为在下面直接显示了不会无效直到关闭
            int size = content.items().size;
            for(int i = 0; i < size; i++){
                Item item = content.item(i);
                if(item != null){
                    //Tile t = indexer.findClosestOre(x, y, item);
                    if(item == blockedItem) continue;
                    Tile t = findOre(item);
                    if(t != null && item.hardness <= tier && checkOre(t)) tiles.add(t);
                }
            }
            buildTable(MinerPoint.this, table, tiles, () -> sortTile, this::configure, true);
        }

        public void configure(Tile value) {
            EUCall.minerPointConfig(player, self(), value);
        }

        @Override
        public boolean shouldConsume(){
            return alwaysCons || units.size < dronesCreated;
        }

        @Override
        public float totalProgress(){
            return totalDroneProgress;
        }

        @Override
        public float progress(){
            return droneProgress;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(droneWarmup);
            write.f(droneProgress);
            write.b(units.size);
            for(Unit unit : units){
                write.i(unit.id);
            }

            write.i(sortTile == null ? -1 : sortTile.pos());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            droneWarmup = read.f();
            droneProgress = read.f();
            int count = read.b();
            readUnits.clear();
            for(int i = 0; i < count; i++){
                readUnits.add(read.i());
            }
            whenSyncedUnits.clear();

            sortTile = world.tile(read.i());
        }

        public <T extends Tile> void buildTable(@Nullable Block block, Table table, Seq<T> tiles, Prov<T> holder, Cons<T> consumer, boolean closeSelect){

            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            Table cont = new Table();
            cont.defaults().size(40);

            int i = 0;

            for(T tile : tiles){
                ImageButton button = cont.button(Tex.whiteui, Styles.clearTogglei, 24, () -> {
                    if(closeSelect) control.input.config.hideConfig();
                }).group(group).tooltip(tile.solid() ? tile.wallDrop().localizedName : tile.drop().localizedName).get();
                button.changed(() -> consumer.get(button.isChecked() ? tile : null));
                //button.getStyle().imageUp = new TextureRegionDrawable(tile.solid() ? tile.overlay().uiIcon.found() ? tile.overlay().uiIcon : tile.block().uiIcon : tile.overlay().uiIcon.found() ? tile.overlay().uiIcon : tile.floor().uiIcon);
                button.getStyle().imageUp = new TextureRegionDrawable(tile.overlay().uiIcon.found() ? tile.overlay().uiIcon : tile.solid() ? tile.block().uiIcon : tile.floor().uiIcon);
                //button.getStyle().imageUp = new TextureRegionDrawable(tile.block().uiIcon);
                button.update(() -> button.setChecked(holder.get() == tile));

                if(i++ % 4 == 3){
                    cont.row();
                }
            }

            //add extra blank spaces so it looks nice
            if(i % 4 != 0){
                int remaining = 4 - (i % 4);
                for(int j = 0; j < remaining; j++){
                    cont.image(Styles.black6);
                }
            }

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);

            if(block != null){
                pane.setScrollYForce(block.selectScroll);
                pane.update(() -> {
                    block.selectScroll = pane.getScrollY();
                });
            }

            pane.setOverscroll(false, false);
            table.add(pane).maxHeight(Scl.scl(40 * 5));
        }
    }
}

