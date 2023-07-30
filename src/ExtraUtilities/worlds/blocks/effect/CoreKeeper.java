package ExtraUtilities.worlds.blocks.effect;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.worlds.blocks.production.MinerPoint;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.modules.ItemModule;

import java.util.Arrays;

import static mindustry.Vars.*;

public class CoreKeeper extends StorageBlock {
    public float warmupSpeed, downSpeed;
    public  int range = 15;

    public DrawBlock drawer = new DrawDefault();

    private final String[] load = new String[]{"loading", "loading .", "loading ..", "loading ..."};

    public CoreKeeper(String name) {
        super(name);
        update = true;
        hasItems = true;
        itemCapacity = 0;
        configurable = true;
        replaceable = false;
        warmupSpeed = 0.01f;
        downSpeed = 0.04f;

        buildCostMultiplier = 0;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        if(state.rules.infiniteResources) return;

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

        Drawf.square(x, y, range * tilesize * 1.414f, 90, player.team().color);
    }

    public Rect getRect(Rect rect, float x, float y, float range){
        rect.setCentered(x, y, range * 2 * tilesize);

        return rect;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(state.rules.infiniteResources) return true;

        CoreBlock.CoreBuild core = team.core();
        if(core == null || (!state.rules.infiniteResources && !core.items.has(requirements, state.rules.buildCostMultiplier))) return false;

        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, range).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.storage).contains(b -> {
            if(b instanceof CoreKeeperBuild) {
                CoreKeeperBuild build = (CoreKeeperBuild) b;
                CoreKeeper block = (CoreKeeper) b.block;
                return getRect(Tmp.r2, build.x, build.y, block.range).overlaps(rect);
            }
            return false;
        });
    }

    @Override
    public boolean isAccessible() {
        return true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, Core.bundle.format("stat.core-keeper-range", range));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("warmup", (CoreKeeperBuild entity) -> new Bar(() ->
                entity.warmup() > 0.999 ? "complete" : load[(int) (Time.time/(load.length*10)) % load.length],
                () -> Color.valueOf("ffd06d"),
                entity::warmup
        ));
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

//    @Override
//    protected TextureRegion[] icons() {
//        TextureRegion[] t = new TextureRegion[drawer.icons(this).length + 1];
//        for(int i = 0; i < drawer.icons(this).length; i++){
//            t[i] = drawer.icons(this)[i];
//        }
//
//        t[drawer.icons(this).length] = teamRegion;
//        return t;
//    }

    public class CoreKeeperBuild extends StorageBuild{
        public float warmup, progress;

        public boolean dw = false;

        public transient Trail trail = new Trail(50);

        @Override
        public boolean canPickup() {
            return false;
        }

        @Override
        public void updateTile() {
            //ues only consValid if it has
            boolean work = false;
            if(efficiency > 0 && core() != null){
                if(Mathf.equal(warmup, 1, 0.01f)) {
                    warmup = 1;
                    work = true;
                } else
                    warmup = Mathf.lerpDelta(warmup, 1, warmupSpeed);
            }else{
                if(Mathf.equal(warmup, 0, 0.01f))
                    warmup = 0;
                else
                    warmup = Mathf.lerpDelta(warmup, 0, downSpeed);
            }
            progress += Time.delta * warmup * efficiency;

            if(work){
                if(linkedCore == null || !linkedCore.isValid()){
                    linkedCore = core();
                    //items = core().items;
                    items = linkedCore.items;
                }
                if(!dw){
                    EUFx.diffuse.at(x, y, 0, team.color, size);
                    dw = true;
                }
            } else {
                linkedCore = null;
                items = new ItemModule();
                dw = false;
            }
        }

        @Override
        public void draw() {
            super.draw();
            drawer.draw(this);
        }

        @Override
        public void drawSelect() { }

        @Override
        public void drawConfigure() {
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            if(linkedCore != null && linkedCore.block != null){
                float px = x - size * tilesize / 2f, py = y + size * tilesize / 2f;
                Draw.rect(linkedCore.block.uiIcon, px, py, 8, 8);
                Drawf.arrow(px, py, linkedCore.x, linkedCore.y, tilesize + sin, 2 + sin);
            }
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public float progress() {
            return progress;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(warmup);
            write.bool(dw);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            warmup = read.f();
            dw = read.bool();
        }
    }
}
