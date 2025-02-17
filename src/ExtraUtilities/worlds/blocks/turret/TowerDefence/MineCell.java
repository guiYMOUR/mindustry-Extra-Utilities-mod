package ExtraUtilities.worlds.blocks.turret.TowerDefence;

import ExtraUtilities.ui.ItemImage;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class MineCell extends Block {
    public Seq<String> floors = new Seq<>();
    public float mineInter = 120f;
    public float range = 80;

    public BulletType mine;
    public int mines = 4;
    public float mineSpread = 4;
    public float mineRotationSpread = 120;

    public float moveTime = 30f;

    public ItemStack[] mineConsumes = {};

    public int fms = 5;

    public MineCell(String name) {
        super(name);
        solid = update = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, mine)));
        stats.add(Stat.reload, 60f / mineInter, StatUnit.perSecond);
        stats.add(Stat.input, t -> {
            t.defaults().left();
            t.row();
            t.table(tx -> tx.add("[accent]自动从核心获取[]"));
            t.row();
            t.table(Styles.grayPanel, i -> {
                int r = 0;
                for(var is : mineConsumes){
                    if(r % 3 == 0) i.row();
                    i.add(new ItemImage(is)).pad(4);
                    r++;
                }
            });
        });

        if(floors.size > 0) {

            stats.add(Stat.tiles, (t) -> {
                t.row();
                t.table(i -> {
                    int r = 0;
                    for(var s : floors) {
                        var f = content.block(s);
                        if(f != null) {
                            if (r % 4 == 0) i.row();
                            i.image(f.uiIcon).pad(3);
                            r++;
                        }
                    }
                });
            });
        }
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("heat", (MineCellBuild entity) ->
                new Bar(() ->
                        "reload",
                        () -> Pal.lightOrange,
                        () -> entity.timeMine / mineInter));
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
                Core.atlas.find(name + "-bottom"),
                Core.atlas.find(name + "-f0"),
                Core.atlas.find(name)
        };
    }

    public class MineCellBuild extends Building{
        public float timeMine = 0;

        public Seq<Tile> fs = new Seq<>();

        public float fm = 0;
        public boolean open = false;

        @Override
        public void draw() {
            TextureRegion r = Core.atlas.find(name),
                    bt = Core.atlas.find(name + "-bottom");
            Draw.rect(bt, x, y);
            TextureRegion f = Core.atlas.find(name + "-f" + (int)Math.min(fm * fms, fms - 1));
            Draw.rect(f, x, y);
            Draw.rect(r, x, y);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(open) fm = Mathf.lerpDelta(fm, 1, 0.3f);
            else fm = Mathf.lerpDelta(fm, 0, 0.15f);
            if(fm >= 0.99f) open = false;

            if(coreCanConsume()) timeMine += edelta();
            if(timeMine >= mineInter){
                initFloor();
                creatMine();
                coreConsume();
                timeMine = 0;
            }
        }

        public void creatMine(){
            open = true;
            if(fs.size > 0){
                int i = Mathf.random(0, fs.size - 1);
                var f = fs.get(i);
                if(f != null){
                    float dst = dst(f);
                    float ang = angleTo(f);
                    for(int m = 0; m < mines; m++){
                        float rg = Mathf.random(mineRotationSpread);
                        mine.create(this, team, x + Mathf.random(-mineSpread, mineSpread), y + Mathf.random(-mineSpread, mineSpread), ang, -1, 1, 1, rg, (b) ->{
                            b.initVel(b.rotation(), dst/moveTime * 2 * Math.max(0, 1 - b.time/moveTime));
                        });
                    }
                }
            } else {
                float dst = Mathf.random(size/2f * tilesize, range);
                float ang = Mathf.random(360);
                for(int m = 0; m < mines; m++){
                    float rg = Mathf.random(mineRotationSpread);
                    mine.create(this, team, x + Mathf.random(-mineSpread, mineSpread), y + Mathf.random(-mineSpread, mineSpread), ang, -1, 1, 1, rg, (b) ->{
                        b.initVel(b.rotation(), dst/moveTime * 2 * Math.max(0, 1 - b.time/moveTime));
                    });
                }
            }
        }

        public void initFloor(){
            fs.clear();
            int tx = World.toTile(x), ty = World.toTile(y);
            int tr = (int)(range / tilesize);
            for(int x = -tr; x <= tr; x++){
                for(int y = -tr; y <= tr; y++){
                    var f = world.tile(x + tx, y + ty);
                    if(f != null && within(f, range) && f.block().isAir() && floors.contains(f.floor().name)){
                        fs.add(f);
                    }
                }
            }
        }

        public boolean coreCanConsume(){
            if(core() == null) return false;
            if(state.rules.infiniteResources || cheating()) return true;
            for(var is : mineConsumes){
                if(core().items.get(is.item) < is.amount){
                    return false;
                }
            }
            return true;
        }

        public void coreConsume(){
            if(core() == null || state.rules.infiniteResources || cheating()) return;
            if(coreCanConsume()) for(var is : mineConsumes){
                core().items.remove(is);
            }
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            if(fs.size > 0){
                for(var f : fs){
                    float sin = Mathf.absin(Time.time, 5, 1);
                    Draw.color(Tmp.c1.set(team.color).a(sin * 0.6f));
                    Fill.square(f.worldx(), f.worldy(), 2);
                    Draw.reset();
                }
            }

            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void displayConsumption(Table table) {
            super.displayConsumption(table);
            table.table(c -> {
                int i = 0;
                for(var stack : mineConsumes){
                    c.add(new ReqImage(
                            new ItemImage(stack.item.uiIcon, Math.round(stack.amount)),
                            () -> (state.rules.infiniteResources || cheating()) ||
                                    (core() != null && core().items.has(stack.item, Math.round(stack.amount)))
                    )).padRight(8);
                    if(++i % 4 == 0) c.row();
                }
            }).left();
        }

        @Override
        public BlockStatus status() {
            if (!enabled) {
                return BlockStatus.logicDisable;
            } else if (!shouldConsume()) {
                return BlockStatus.noOutput;
            } else if (!(this.efficiency <= 0) && productionValid() && coreCanConsume()) {
                return Vars.state.tick / 30 % 1 < (double)efficiency ? BlockStatus.active : BlockStatus.noInput;
            } else {
                return BlockStatus.noInput;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(timeMine);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            timeMine = read.f();
        }
    }
}
