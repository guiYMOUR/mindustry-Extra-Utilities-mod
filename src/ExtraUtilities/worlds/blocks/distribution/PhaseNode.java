package ExtraUtilities.worlds.blocks.distribution;

import ExtraUtilities.worlds.drawer.DrawPhaseNode;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class PhaseNode extends ItemBridge {
    public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawPhaseNode());

    public PhaseNode(String name) {
        super(name);
        swapDiagonalPlacement = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
        Drawf.dashCircle(x * tilesize, y * tilesize, range * tilesize, Pal.accent);
    }

    @Override
    public boolean linkValid(Tile tile, Tile other, boolean checkDouble) {
        if(other == null || tile == null || other == tile) return false;
        if(!tile.within(other, (range + size) * tilesize)) return false;
        return ((other.block() == tile.block() && tile.block() == this) || (!(tile.block() instanceof PhaseNode) && other.block() == this))
                && (other.team() == tile.team() || tile.block() != this)
                && (!checkDouble || ((PhaseNodeBuild)other.build).link != tile.pos());
    }

    public Tile findLink(int x, int y){

        return null;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range, StatUnit.blocks);
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, range * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    public boolean positionsValid(int x1, int y1, int x2, int y2){
        return Mathf.dst(x1, y1, x2, y2) <= (range + size) * tilesize;
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }

    public class PhaseNodeBuild extends ItemBridgeBuild{
        @Override
        public void updateTile() {
            Building other = world.build(link);
            if(other != null && !linkValid(tile, other.tile)){
                link = -1;
                //return;
            }
            super.updateTile();
        }

        @Override
        public void updateTransport(Building other){
            if(hasItems) super.updateTransport(other);
            if(hasLiquids){
                if(warmup >= 0.25f){
                    moved |= moveLiquid(other, liquids.current()) > 0.05f;
                }
            }
        }

        @Override
        public void doDump(){
            if(hasItems) {
                for(int i = 0; i < items.total() / transportTime; i++) dumpAccumulate();
            }
            if(hasLiquids) dumpLiquid(liquids.current(), 1f);
        }

        @Override
        public void drawConfigure() {
            float sin = Mathf.absin(Time.time, 6, 1);

            Draw.color(Pal.accent);
            Lines.stroke(1);
            Drawf.circles(x, y, (block.size / 2f + 1) * tilesize + sin - 2, Pal.accent);
            Building other = world.build(link);
            if(other != null){
                Drawf.circles(other.x, other.y, (block.size / 3f + 1) * tilesize + sin - 2, Pal.place);
                Drawf.arrow(x, y, other.x, other.y, block.size * tilesize + sin, 4 + sin, Pal.accent);
            }
            Drawf.dashCircle(x, y, range * tilesize, Pal.accent);
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        protected boolean checkAccept(Building source, Tile other) {
            if(tile == null || linked(source)) return true;
            return linkValid(tile, other);
        }

        @Override
        protected boolean checkDump(Building to) {
            return true;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if(other == this) configure(-1);
            return super.onConfigureBuildTapped(other);
        }
    }
}
