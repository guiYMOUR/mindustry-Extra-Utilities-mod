package ExtraUtilities.worlds.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.graphics.Layer;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.production.Separator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static arc.math.Angles.randLenVectors;

public class OutLiquidSeparator extends Separator {
    public @Nullable
    LiquidStack outputLiquid;

    Effect fullEffect;

    public OutLiquidSeparator(String name) {
        super(name);

        fullEffect = new Effect(30, e -> {
            Draw.color(Liquids.water.color);
            randLenVectors(e.id, 3, 2f + 16 * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.foutpow() * 5);
            });
        }).layer(Layer.block - 1);

        liquidCapacity = 20;
    }

    @Override
    public void init() {
        if(outputLiquid != null){
            outputsLiquid = true;
            hasLiquids = true;
        }
        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();
        if(outputLiquid != null){
            stats.add(Stat.output, StatValues.liquid(outputLiquid.liquid, outputLiquid.amount * 60f, true));
        }
    }

    public class LiquidSeparatorBuild extends SeparatorBuild{
        @Override
        public void updateTile() {
            super.updateTile();

            if(outputLiquid != null){
                if(liquids.get(outputLiquid.liquid) >= liquidCapacity && Mathf.chanceDelta(0.5f * edelta())) fullEffect.at(this);
                float added = Math.min(edelta() * outputLiquid.amount, liquidCapacity - liquids.get(outputLiquid.liquid));
                liquids.add(outputLiquid.liquid, added);
                dumpLiquid(outputLiquid.liquid);
            }
        }
    }
}
