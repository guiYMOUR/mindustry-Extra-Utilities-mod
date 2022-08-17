package ExtraUtilities.worlds.blocks.heat;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class HeatDriver extends HeatConductor {
    public int range = 240;
    public float lost = 0.15f;

    public HeatDriver(String name) {
        super(name);
        configurable = true;
        hasPower = true;
        visualMaxHeat = 50f;

        config(Point2.class, (HeatDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (HeatDriverBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.shootRange, range, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + this.offset, y * tilesize + this.offset, range, Pal.accent);
    }

    public class HeatDriverBuild extends HeatConductorBuild{
        public float rotation = 90f;
        public float progress = 0f;
        public float resProgress = 0f;
        public int link = -1;
        public Seq<Building> owners = new Seq<>();

        @Override
        public void updateTile() {
            if(owners.size == 0 && link == -1) heat = 0;

            checkOwner();

            Building linked = world.build(this.link);
            boolean hasLink = linkValid();

            if(hasLink) {
                HeatDriverBuild other = (HeatDriverBuild) linked;
                if(other.checkOneOwner(this)) other.owners.add(this);
                float toRotation = this.angleTo(other);
                rotation = Mathf.slerpDelta(rotation, toRotation, 0.02f * power.status);
                if(Angles.near(rotation, toRotation, 2)){
                    this.updateTransfer();
                    //other.owners.add(this);
                    other.updateTransfer();
                    progress = Mathf.slerpDelta(progress, 1, 0.02f * power.status);
                } else {
                    progress = Mathf.slerpDelta(progress, 0, 0.04f);
                }
            } else {
                progress = Mathf.slerpDelta(progress, 0, 0.04f);
            }
            float p = Math.min((heat/visualMaxHeat), 1);
            if(owners.size > 0 && p > 0){
                resProgress = Mathf.slerpDelta(resProgress, 1, 0.02f * p);
            } else {
                resProgress = Mathf.slerpDelta(resProgress, 0, 0.05f);
            }
        }

        public void updateTransfer(){
            if(owners.size > 0){
                float totalHeat = 0f;
                for(int i = 0; i < owners.size; i++){
                    Building o = owners.get(i);
                    //if(o instanceof HeatDriverBuild){
                        HeatDriverBuild owner = (HeatDriverBuild)o;
                        float dst = this.dst(owner)/range;
                        if(Angles.near(owner.rotation, owner.angleTo(this), 2f)) {
                            totalHeat += owner.heat * (1 - dst * lost);
                            totalHeat *= owner.power.status;
                        //}
                    }
                }
                heat = totalHeat;
            } else {
                super.updateTile();
            }
        }

        @Override
        public float heatRequirement(){
            return linkValid() ? visualMaxHeat : Float.MAX_VALUE;
        }

        @Override
        public float heat() {
            //if(link == -1 && owners.size == 0) return 0;
            return (owners.size > 0 && link == -1)? heat : 0;
        }

        @Override
        public void drawConfigure(){
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            owners.each(owner -> {
                Drawf.circles(owner.x, owner.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(owner.x, owner.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            });


            if(linkValid()){
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                if(link == -1) deselect();
                configure(-1);
                return false;
            }

            if(link == other.pos()){
                configure(-1);
                return false;
            }else if(other.block == block && other.dst(tile) <= range && other.team == team && checkOneOwner(other)){
                configure(other.pos());
                return false;
            }

            return true;
        }

        public void checkOwner(){
            for(int i = 0; i<owners.size; i++){
                int pos = owners.get(i).pos();
                Building build = world.build(pos);
                if (build instanceof HeatDriverBuild) {
                    HeatDriverBuild owner = (HeatDriverBuild) build;
                    if (owner.block != block || owner.link != this.pos()) owners.remove(i);
                } else {
                    owners.remove(i);
                }
            }
        }

        protected boolean checkOneOwner(Building other){
            if(owners.size == 0) return true;
            return !owners.contains(other);
        }

        @Override
        public Point2 config() {
            if(tile == null) return null;
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        public boolean linkValid(){
            if(link == -1) return false;
            Building other = world.build(link);
            if(other == null){
                link = -1;
                return false;
            }
            return other.block == block && other.team == team && within(other, range);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(link);
            write.f(rotation);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            link = read.i();
            rotation = read.f();
            progress = read.f();
        }
    }
}
