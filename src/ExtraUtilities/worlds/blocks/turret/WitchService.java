package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUStatusEffects;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.TargetPriority;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class WitchService extends Block {
    public float width = 18, height = 36;
    public StatusEffect eType = EUStatusEffects.breakage;
    public float eTime = 600;
    public ObjectMap<Unit, Float> findMap = new ObjectMap<>();
    public float AMP = 0.05f;
    public float timeApply = 60;
    public StatusEffect applyEffect = StatusEffects.slow;

    public Effect work, applyIn, applyOut;
    public Color workColor = Pal.techBlue;

    public DrawBlock drawer = new DrawDefault();

    public WitchService(String name) {
        super(name);

        rotate = true;
        drawArrow = false;
        update = true;
        solid = true;
        attacks = true;
        priority = TargetPriority.turret;
        group = BlockGroup.turrets;
        flags = EnumSet.of(BlockFlag.turret);
        work = applyIn = applyOut = Fx.none;

        canOverdrive = false;
    }

    @Override
    public void init() {
        updateClipRadius((height + 1) * tilesize);
        super.init();
    }

    protected Rect getRect(Rect rect, float x, float y, int rotation){
        float w = width * tilesize, h = height * tilesize;
        float rw = (rotation % 2 == 0 ? h : w), rh = (rotation % 2 == 0 ? w : h);
        rect.setCentered(x, y, rw, rh);
        float len = tilesize * (height - size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if (world.tile(x, y) != null) {
            if (!canPlaceOn(world.tile(x, y), player.team(), rotation)) {
                drawPlaceText(Core.bundle.get("bar.extra-utilities-close"), x, y, valid);
            }
        }

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, rotation).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.turret).contains(b -> b instanceof ServiceBuild && getRect(Tmp.r2, b.x, b.y, b.rotation).overlaps(rect));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.range);
        stats.add(Stat.range, t -> {
            t.row();
            t.add("宽(width): " + Strings.autoFixed(height, 2) + StatUnit.blocks.localized()).left();
            t.row();
            t.add("高(height): " + Strings.autoFixed(width, 2) + StatUnit.blocks.localized()).left();
            t.row();
            t.table(Styles.grayPanel, r -> {
                int amount = (int) (3 + width % 3);
                int line = (int) ((height / width) * amount);
                for(int i = 0; i < amount; i++){
                    for(int j = 0; j < line; j++){
                        r.image(((TextureRegionDrawable) Tex.whiteui).tint(i == amount/2 && j == 0 ? Pal.accent : Pal.stoneGray)).size(16).left().pad(4);
                    }
                    r.row();
                }
            }).left().margin(5);
            t.row();
        });
        if(eType != StatusEffects.none) stats.add(Stat.abilities, t -> {
            t.row();
            t.add(Core.bundle.get("statValue.showStatus")).left();
            t.row();
            t.table(Styles.grayPanel, inner -> {
                inner.left().defaults().left();
                inner.row();
                inner.add(EUGet.selfStyleImageButton(new TextureRegionDrawable(eType.uiIcon), Styles.emptyi, () -> ui.content.show(eType))).padTop(4f).padBottom(6f).size(42);
                //inner.button(new TextureRegionDrawable(s.uiIcon), () -> ui.content.show(s)).padTop(4f).padBottom(6f).size(50);
                inner.add(eType.localizedName).padLeft(5);
            }).left().growX().margin(6).pad(5).padBottom(-5).row();
        });
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    protected TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    public class ServiceBuild extends Building implements Ranged{
        protected Rect r1 = new Rect();
        protected Rect r2 = new Rect();
        public float reload = timeApply;
        public boolean working = false;

        //本来想做的是进入过范围才会触发，每座塔单独算，然后一想，可以让一座塔当信标挂标记让其他塔输出破条，好像可玩性更高
        Seq<Unit> inRange = new Seq<>();

        public Rect getR(){
            return r1;
        }

        @Override
        public float range() {
            return height * tilesize;
        }

        public float getDelta(){
            float p = hasPower ? Time.delta * power.status : Time.delta;
            return hasLiquids ? p * Math.min(liquids.currentAmount() / liquidCapacity, 1f) : p;
        }

        @Override
        public void updateTile() {
            Rect r = getRect(r1, x, y, rotation);
            if((reload += getDelta()) >= timeApply) {
                Units.nearbyEnemies(team, r, u -> {
                    if (u.targetable(team) && !u.inFogTo(team)) {
                        if (!u.hasEffect(eType)) {
                            if (!findMap.containsKey(u)) {
                                findMap.put(u, AMP);
                            } else {
                                findMap.put(u, findMap.get(u) + AMP);
                            }

                            working = true;
                            applyIn.at(u.x, u.y, u.rotation, u);
                            u.apply(applyEffect, timeApply / 2f);
                        } else {
                            if (u.isAdded() && !u.dead && findMap.containsKey(u)) {
                                findMap.remove(u);
                            }
                        }
                    }
                });
                for(Unit u : findMap.keys()){
                    if(u == null || !u.isAdded() || u.dead) {
                        findMap.remove(u);
                        continue;
                    }

                    findMap.put(u, findMap.get(u) + AMP);
                    applyOut.at(u.x, u.y, u.rotation, u);

                    if(findMap.get(u) >= 1) {
                        u.apply(eType, eTime);
                        findMap.remove(u);
                    }
                }

                reload = 0;
            }

            if(working){
                work.at(x, y, rotation * 90f, workColor, r);
                working = false;
            }
        }

        @Override
        public boolean shouldConsume() {
            return (super.shouldConsume() && findMap.size > 0);
        }

        @Override
        public void drawSelect(){
            if(team != null) Drawf.dashRect(team.color, getRect(r2, x, y, rotation));
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        //这个，不需要了
//        @Override
//        public void write(Writes write) {
//            super.write(write);
//
//            write.f(reload);
//        }
//
//        @Override
//        public void read(Reads read, byte revision) {
//            super.read(read, revision);
//
//            reload = read.f();
//        }
    }

    public enum elementsType {
        breakage
    }
}
