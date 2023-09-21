package ExtraUtilities.worlds.consumers;

import arc.func.Func;
import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeLiquids;

public class ConsumeLiquidDynamic extends ConsumeLiquids {
    public final Func<Building, LiquidStack[]> liquids;
    public Liquid[] fit;
    
    public ConsumeLiquidDynamic(Func<Building, LiquidStack[]> liquids, Liquid[] fit){
        this.liquids = liquids;
        this.fit = fit;
    }

    @Override
    public void apply(Block block) {
        block.hasLiquids = true;
        if(fit.length > 0) {
            for (Liquid l : fit) {
                block.liquidFilter[l.id] = true;
            }
        }
    }
    public void build(Building build, Table table) {
        LiquidStack[][] current = new LiquidStack[][]{liquids.get(build)};
        table.table((c) -> c.table(c1 -> {
            c.update(() -> {
                if (current[0] != liquids.get(build)) {
                    rebuild(build, c1);
                    current[0] = liquids.get(build);
                }

            });
            rebuild(build, c1);
        })).left();
    }

    void rebuild(Building build, Table table){
        table.clear();
        int i = 0;
        LiquidStack[] stacks = liquids.get(build);
        if(stacks.length == 0) return;

        for (LiquidStack stack : stacks) {
            table.add(new ReqImage(stack.liquid.uiIcon, () -> build.liquids.get(stack.liquid) > 0)).size(32).padRight(8);
            ++i;
            if (i % 4 == 0) {
                table.row();
            }
        }
    }

    public void update(Building build) {
        float mult = multiplier.get(build);
        LiquidStack[] stacks = liquids.get(build);
        if(stacks.length == 0) return;

        for (LiquidStack stack : stacks) {
            build.liquids.remove(stack.liquid, stack.amount * build.edelta() * mult);
        }

    }

    public float efficiency(Building build) {
        float mult = multiplier.get(build);
        float ed = build.edelta();
        if (ed <= 1e-8f) {
            return 0;
        } else {
            float min = 1;
            LiquidStack[] stacks = liquids.get(build);
            if(stacks.length == 0) return 0;

            for (LiquidStack stack : stacks) {
                min = Math.min(build.liquids.get(stack.liquid) / (stack.amount * ed * mult), min);
            }

            return min;
        }
    }

//    public void display(Stats stats) {
//        stats.add(booster ? Stat.booster : Stat.input, StatValues.liquids(1, true, fit));
//    }
}
