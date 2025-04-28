package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.ui.ItemDisplay;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;

import arc.struct.ObjectIntMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;

import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Planet;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;
import static mindustry.Vars.world;

public class AdaptiveMining extends Block {
    public DrawBlock drawer = new DrawDefault();
    public ObjectIntMap<Item> pullItem = new ObjectIntMap<>();

    public float outTime = 600;

    public AdaptiveMining(String name) {
        super(name);
        drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawRegion("-rot", 3, true),
                new DrawRegion("-top"),
                new DrawBlock() {
                    @Override
                    public TextureRegion[] icons(Block block) {
                        return new TextureRegion[]{Core.atlas.find(block.name + "-team")};
                    }
                }
        );

        update = solid = true;
        sync = true;
        canOverdrive = false;
    }

    @Override
    public void setStats() {
        stats.timePeriod = outTime;
        super.setStats();

        stats.add(Stat.productionTime, outTime / 60f, StatUnit.seconds);
        stats.add(Stat.output,  table -> {
            table.row();
            int i = 0;
            for(Item item : pullItem.keys()){
                table.add(new ItemDisplay(item)).padRight(5);

                if(i != pullItem.size - 1){
                    table.add("/");
                }
                i++;
            }
        });
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        if(!coreNearby(x, y, Vars.player.team())) drawPlaceText(Core.bundle.get("place.needCore"), x, y, valid);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        int x = tile.x;
        int y = tile.y;

        return coreNearby(x, y, team);
    }

    public boolean coreNearby(int x, int y, Team team){
        int cornerX = x - (size - 1)/2, cornerY = y - (size - 1)/2, s = size;
        for(int i = 0; i < size; i++) {
            int rx = 0, ry = 0;

            rx = cornerX + s;
            ry = cornerY + i;
            Tile other = world.tile(rx, ry);
            if (other != null && other.build != null && other.build instanceof CoreBlock.CoreBuild && other.build.team == team) {
                return true;
            }
            rx = cornerX + i;
            ry = cornerY + s;
            other = world.tile(rx, ry);
            if (other != null && other.build != null && other.build instanceof CoreBlock.CoreBuild && other.build.team == team) {
                return true;
            }
            rx = cornerX - 1;
            ry = cornerY + i;
            other = world.tile(rx, ry);
            if (other != null && other.build != null && other.build instanceof CoreBlock.CoreBuild && other.build.team == team) {
                return true;
            }
            rx = cornerX + i;
            ry = cornerY - 1;
            other = world.tile(rx, ry);
            if (other != null && other.build != null && other.build instanceof CoreBlock.CoreBuild && other.build.team == team) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void load(){
        super.load();

        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        drawer.getRegionsToOutline(this, out);
    }

    public class AdaptiveMiningBuild extends Building{
        public float rot = 0;
        public float work = 0;
        public ObjectIntMap<Item> ruleCopy = new ObjectIntMap<>();

        @Override
        public void updateTile() {
            if(!nearbyCore()) return;
            rot += Time.delta;
            if(Vars.net.client()) return;

            for(Item item : pullItem.keys()) {
                if(item.isOnPlanet(state.getPlanet()) && !item.isHidden() && (!state.isCampaign() || item.unlocked())){
                    ruleCopy.put(item, pullItem.get(item));
                }
            }

            int sum = 0;
            for(Item item : ruleCopy.keys()) sum += ruleCopy.get(item);
            if(sum <= 0) return;
            if((work += Time.delta) > outTime){
                work -= outTime;
                int i = Mathf.random(0, sum - 1);

                int count = 0;
                Item item = null;

                //guaranteed desync since items are random - won't be fixed and probably isn't too important
                for(Item out : ruleCopy.keys()){
                    if(i >= count && i < count + ruleCopy.get(out)){
                        item = out;
                        break;
                    }
                    count += ruleCopy.get(out);
                }

                core().handleItem(this, item);
            }

        }

        public boolean nearbyCore(){
            for(int i = 0; i < proximity.size; i++){
                if(proximity.get(i) instanceof CoreBlock.CoreBuild cb && cb.team == team){
                    return true;
                }
            }
            return false;
        }

        @Override
        public float totalProgress() {
            return rot;
        }

        @Override
        public void draw() {
            drawer.draw(this);
            drawTeamTop();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(rot);
            write.f(work);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            rot = read.f();
            work = read.f();
        }
    }
}
