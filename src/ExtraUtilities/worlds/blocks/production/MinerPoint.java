package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.ExtraUtilitiesMod;
import ExtraUtilities.ai.MinerPointAI;
import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.net.EUCall;
import arc.Core;
import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Point2;
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

/**
 * 其实就是延续了猫星的特色，可能还是存在部分bug
 * @author guiY
 */

public class MinerPoint extends Block {
    //猫在钻头里面定义了这个为不可挖的矿，虽然只能写一个。。。但是应该不错了
    public @Nullable
    Item blockedItem;

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

        config(Integer.class , (MinerPointBuild tile, Integer i) -> tile.sort = i);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if (world.tile(x, y) != null) {
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

    public Rect getRect(Rect rect, float x, float y, float range) {
        rect.setCentered(x, y, range * 2 * tilesize);

        return rect;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        CoreBuild core = team.core();
        if (core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier)))
            return false;
        if (!limitSize) return true;
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, range).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.factory).contains(b -> {
            if (b instanceof MinerPointBuild build) {
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
            if (b instanceof Floor f) {
                return (f.wallOre && f.itemDrop != null && f.itemDrop != blockedItem && f.itemDrop.hardness <= tier) || (!f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && (indexer.isBlockPresent(f) || state.isMenu()));
            } else if (b instanceof StaticWall w) {
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
                        () -> (float) e.units.size / dronesCreated
                ));
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    public static void minerPointDroneSpawned(Tile tile, int id) {
        if (tile == null || !(tile.build instanceof MinerPointBuild)) return;
        ((MinerPointBuild) tile.build).spawned(id);
    }

    public class MinerPointBuild extends Building {
        public @Nullable
        Tile sortTile = null;

        public int sort = -1;
        public int lastSort = -1;
        public Seq<Tile> tiles = new Seq<>();
        protected IntSeq readUnits = new IntSeq();
        protected IntSeq whenSyncedUnits = new IntSeq();

        public Seq<Unit> units = new Seq<>();
        public float droneWarmup, powerWarmup;
        public float warmup, readyness;
        public float droneProgress, totalDroneProgress;

        public boolean placeInAir = false;

        @Override
        public void updateTile() {
            if(sort != -1 && lastSort != sort) {
                lastSort = sort;
                sortTile = world.tile(sort);
            }
            if(sort == -1 && lastSort != sort){
                lastSort = sort;
                sortTile = null;
            }
            if(sortTile != null && (!checkOre(sortTile) || !validOre(sortTile))) {
                sortTile = null;
                sort = -1;
            }

            dumpAccumulate();

            if (!readUnits.isEmpty()) {
                units.clear();
                readUnits.each(i -> {
                    Unit unit = Groups.unit.getByID(i);
                    if (unit != null) {
                        units.add(unit);
                    }
                });
                readUnits.clear();
            }

            //read newly synced drones on client end
            //same as UnitAssembler
            if (units.size < dronesCreated && whenSyncedUnits.size > 0) {
                whenSyncedUnits.each(id -> {
                    Unit unit = Groups.unit.getByID(id);
                    if (unit != null) {
                        units.addUnique(unit);
                    }
                });
            }

            units.removeAll(u -> !u.isAdded() || u.dead || !(u.controller() instanceof MinerPointAI));

            if (!allowUpdate()) {
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

            if (units.size < dronesCreated && (droneProgress += edelta() * state.rules.unitBuildSpeed(team) * powerStatus / droneConstructTime) >= 1f) {
                if (!net.client()) {
                    Unit unit = MinerUnit.create(team);
                    if (unit instanceof BuildingTetherc) {
                        ((BuildingTetherc) unit).building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();
                    units.add(unit);
                    EUCall.minerPointDroneSpawned(tile, unit.id);
                }
            }

            if (units.size >= dronesCreated) {
                droneProgress = 0f;
            }
            for (int i = 0; i < units.size; i++) {
                Unit unit = units.get(i);
                MinerPointAI ai = (MinerPointAI) unit.controller();
                ai.ore = alwaysCons ? efficiency > 0.4 ? sortTile : null : sortTile;
            }
        }

        @Override
        public boolean canPickup() {
            return canPickUp;
        }

        @Override
        public void pickedUp() {
            if (canPickUp) {
                configure(-1);
                sortTile = null;
            }
        }

        @Override
        public void draw() {
            //same as UnitCargoLoader
            Draw.rect(block.region, x, y);
            if (units.size < dronesCreated) {
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(this, MinerUnit.fullIcon, 0f, droneProgress, warmup, totalDroneProgress);
                });
            } else {
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

            if(Core.settings.getBool("eu-show-miner-point")) {
                if (tiles.size == 0 && !placeInAir) {
                    findOre();
                    if (tiles.size == 0) {
                        placeInAir = true;
                        return;
                    }
                }

                if (tiles.size > 0) {
                    float z = Draw.z();
                    Draw.z(Layer.blockUnder - 2.5f);
                    float sin = Mathf.absin(Time.time, 6, 0.8f);
                    for (Tile t : tiles) {
                        Item i = oreDrop(t);
                        if (i == null || !validOre(t)) continue;
                        Draw.color(Tmp.c1.set(i.color).a(sin));
                        Fill.square(t.worldx(), t.worldy(), tilesize / 2f);
                    }
                    Draw.reset();
                    Draw.z(z);
                }
            }

            if (sortTile == null) return;
            Tmp.v1.set(sortTile.getX(), sortTile.getY()).sub(x, y).limit((size / 2f + 1) * tilesize + 0.5f);
            float xx = x + Tmp.v1.x, yy = y + Tmp.v1.y;
            int segs = (int) (dst(sortTile.getX(), sortTile.getY()) / tilesize);
            Lines.stroke(4f, Pal.gray);
            Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
            Lines.stroke(2f, Pal.accent);
            Lines.dashLine(xx, yy, sortTile.getX(), sortTile.getY(), segs);
            Drawf.square(sortTile.getX(), sortTile.getY(), 5, Pal.accent);
        }

        public void spawned(int id) {
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if (net.client()) {
                whenSyncedUnits.add(id);
            }
        }

        public boolean checkOre(Tile t) {
            return Mathf.equal(tile.x, t.x, range) && Mathf.equal(tile.y, t.y, range);
        }

        private boolean validOre(Tile t) {
            return (t.solid() && t.wallDrop() != null && t.wallDrop() != blockedItem && t.wallDrop().hardness <= tier) ||
                    (t.block() == Blocks.air && t.drop() != null && t.drop() != blockedItem && t.drop().hardness <= tier);
        }

        private Item oreDrop(Tile t){
            if(t.solid() && t.wallDrop() != null) return t.wallDrop();
            if(t.block() == Blocks.air && t.drop() != null) return t.drop();

            return null;
        }

        private void findOre() {
            tiles.clear();

            int tx = tile.x, ty = tile.y;
            int tr = range;
            for (int x = -tr; x <= tr; x++) {
                for (int y = -tr; y <= tr; y++) {
                    Tile other = world.tile(x + tx, y + ty);
                    if (other != null && checkOre(other)) {
                        tiles.add(other);
                    }
                }
            }
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            return Math.min(itemCapacity - items.get(item), amount);
        }

        @Override
        public boolean onConfigureTapped(float x, float y) {
            Tile t = world.tileWorld(x, y);
            if(t != null && checkOre(t) && validOre(t)){
                if(sort == t.pos()){
                    configure(-1);
                } else {
                    configure(t.pos());
                }
                return true;
            }
            return false;
        }

        @Override
        public Integer config(){
            return sort;
        }

        @Override
        public boolean shouldConsume() {
            return alwaysCons || units.size < dronesCreated;
        }

        @Override
        public float totalProgress() {
            return totalDroneProgress;
        }

        @Override
        public float progress() {
            return droneProgress;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(droneWarmup);
            write.f(droneProgress);
            write.b(units.size);
            for (Unit unit : units) {
                write.i(unit.id);
            }

            write.i(sortTile == null ? -1 : sortTile.pos());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            droneWarmup = read.f();
            droneProgress = read.f();
            int count = read.b();
            readUnits.clear();
            for (int i = 0; i < count; i++) {
                readUnits.add(read.i());
            }
            whenSyncedUnits.clear();

            sort = read.i();
        }

    }
}