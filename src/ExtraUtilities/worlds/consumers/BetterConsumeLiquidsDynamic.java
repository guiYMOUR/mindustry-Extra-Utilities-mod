package ExtraUtilities.worlds.consumers;

import arc.func.Func;
import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.consumers.ConsumeLiquidsDynamic;

public class BetterConsumeLiquidsDynamic extends ConsumeLiquidsDynamic {
    public Liquid[] fit;
    
    public BetterConsumeLiquidsDynamic(Func<Building, LiquidStack[]> liquids, Liquid[] fit){
        super(liquids);
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
}
