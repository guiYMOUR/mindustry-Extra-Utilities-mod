package ExtraUtilities.worlds.blocks.power;

import ExtraUtilities.worlds.meta.EUStatValues;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Strings;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class SpaceGenerator extends PowerGenerator {
    //就挺离谱的这个写了我两天最后放弃去寻找通风口了
    public int space = 3;
    private int edgeSpace;
    public Color validColor = Pal.accent;
    public Color invalidColor = Pal.remove;

    public boolean blockedOnlySolid = false;
    public boolean haveBasicPowerOutput = true;

    public Attribute attribute = null;
    public Color attributeColor = Pal.accent;
    public Color negativeAttributeColor = Pal.accent;
    public float efficiencyScale = 1f;
    public boolean display = true;

    public Effect outEffect = Fx.none;
    public float outTimer = 30;
    public Effect tileEffect = Fx.none;
    public float tileTimer = 30;

    public SpaceGenerator(String name) {
        super(name);
        flags = EnumSet.of();
        envEnabled = Env.any;

        powerProduction = 10/60f;//基础一格的输出
    }

    @Override
    public void init() {
        super.init();
        this.edgeSpace = this.space;
        this.space = this.space + this.size/2;
    }

    public void setStats() {
        super.setStats();
        stats.remove(generationType);
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
        if(haveBasicPowerOutput) stats.add(Stat.tiles, EUStatValues.colorString(validColor, Core.bundle.get("stat.space-generator-valid")));
        stats.add(Stat.tiles, EUStatValues.colorString(invalidColor, Core.bundle.get("stat.space-generator-invalid")));
        if(attribute != null){
            stats.add(Stat.tiles, EUStatValues.colorString(attributeColor, Core.bundle.get("stat.space-generator-attribute")));
            if(negativeAttributeColor != attributeColor) stats.add(Stat.tiles, EUStatValues.colorString(negativeAttributeColor, Core.bundle.get("stat.space-generator-negative-attribute")));
            stats.add(haveBasicPowerOutput ? Stat.affinities : Stat.tiles, attribute, true, efficiencyScale, !display);
        }
        stats.add(Stat.range, edgeSpace, StatUnit.blocks);
    }

    public void setBars() {
        super.setBars();
        if (hasPower && outputsPower) {
            this.addBar("power", (SpaceGeneratorBuild entity) ->
                    new Bar(() ->
                            Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60.0F * entity.timeScale(), 1)),
                            () -> Pal.powerBar,
                            () -> entity.productionEfficiency));
        }

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        x *= tilesize;
        y *= tilesize;

        //22会出错，先不急着修
        Drawf.dashSquare(valid ? Pal.accent : Pal.remove, x, y, (space + (size % 2)/2f) * tilesize * 2);
    }

    public class SpaceGeneratorBuild extends GeneratorBuild{
        public Seq<Tile> tiles = new Seq<>();
        public Seq<Tile> solids = new Seq<>();
        public int tileNum = 0;
        public transient Interval timer = new Interval(6);
        private float totalProgress;

        @Override
        public void updateTile() {
            totalProgress += warmup() * delta();

            if(timer.get(20)){
                tileNum = tileEmp();
            }

            if(tileNum > 0) productionEfficiency = Mathf.lerpDelta(productionEfficiency, 1, 0.02f);
            else productionEfficiency = Mathf.lerpDelta(productionEfficiency, 0, 0.02f);

            if(Mathf.equal(productionEfficiency, 1f, 0.001f)){
                productionEfficiency = 1f;
            }

            if(productionEfficiency > 0.05f){
                if(outEffect != Fx.none){
                    if(timer.get(2, outTimer)){
                        outEffect.at(this);
                    }
                }
                if(tileEffect != Fx.none && tiles.size > 0){
                    if(timer.get(3, tileTimer)){
                        int i = Mathf.random(tiles.size - 1);
                        Tile t = tiles.get(i);
                        if(t != null) tileEffect.at(t);
                    }
                }
            }
        }

        @Override
        public float getPowerProduction() {
            if(attribute == null) return productionEfficiency * powerProduction * tileNum;
            float sum = 0;
            for(int i = 0; i < tiles.size; i++){
                Tile t = tiles.get(i);
                if(t != null) sum += (1 + t.floor().attributes.get(attribute) * efficiencyScale + attribute.env());
            }
            return productionEfficiency * powerProduction * sum;
        }

        private int tileEmp(){
            solids.clear();

            int tr = space;

            int tx = World.toTile(x), ty = World.toTile(y);
            for(int x = -tr; x <= tr - (size % 2 == 0 ? 1 : 0); x++){
                for(int y = -tr; y <= tr - (size % 2 == 0 ? 1 : 0); y++){
                    Tile other = world.tile(x + tx, y + ty);
                    if(other != null){
                        tiles.addUnique(other);
                        if((blockedOnlySolid && other.block().solid) || (!blockedOnlySolid && other.block() != Blocks.air)) solids.addUnique(other);
                    }
                }
            }

            tiles.removeAll(t -> solids.contains(t) || (!haveBasicPowerOutput && attribute != null && t.floor().attributes.get(attribute) == 0));

            return tiles.size;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            Drawf.dashSquare(Pal.accent, x, y, (space + (size % 2)/2f) * tilesize * 2);

            Draw.color(invalidColor);
            Draw.z(Layer.plans + 0.1f);
            Draw.alpha(0.4f);

            for(int i = 0; i < solids.size; i++){
                Tile t = solids.get(i);
                if(t != null) {
                    Fill.square(t.worldx(), t.worldy(), tilesize / 2f);
                }
            }

            for(int i = 0; i < tiles.size; i++){
                Tile t = tiles.get(i);
                if(t != null) {
                    if(attribute != null && t.floor().attributes.get(attribute) != 0) {
                        if (t.floor().attributes.get(attribute) > 0) Draw.color(attributeColor);
                        else Draw.color(negativeAttributeColor);
                    } else Draw.color(validColor);
                    Draw.z(Layer.plans + 0.1f);
                    Draw.alpha(0.4f);
                    Fill.square(t.worldx(), t.worldy(), tilesize / 2f);
                }
            }
            Draw.reset();
        }

        @Override
        public float warmup() {
            if(!haveBasicPowerOutput) return super.warmup();
            int rad = size + edgeSpace * 2;
            int num = rad * rad - size * size;
            return productionEfficiency * tileNum/num;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }
    }
}
