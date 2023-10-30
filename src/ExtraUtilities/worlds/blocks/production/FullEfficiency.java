package ExtraUtilities.worlds.blocks.production;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.*;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class FullEfficiency extends Block {
    public float powerProduction = 10000;

    public FullEfficiency(String name) {
        super(name);
        update = true;
        solid = true;
        envEnabled = Env.any;
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    public class FullEfficiencyBuild extends Building{
        @Override
        public void updateTile() {
            for(int i = 0; i < proximity.size; i++) {
                Building bd = proximity.get(i);
                if (bd != null && bd.shouldConsume() && bd.block != null && bd.block.consumers != null) {
                    for (Consume c : bd.block.consumers) {
                        if (c instanceof ConsumeItems ci) {
                            ItemStack[] is = ci.items;
                            if (is.length > 0) for (ItemStack ik : is) {
                                for (int a = 0; a < ik.amount; a++) {
                                    if (bd.acceptItem(this, ik.item)) {
                                        bd.handleItem(this, ik.item);
                                    }
                                }
                            }
                        } else if (c instanceof ConsumeItemFilter cf) {
                            for (Item it : Vars.content.items()) {
                                if (cf.filter.get(it) && bd.acceptItem(this, it)) {
                                    bd.handleItem(this, it);
                                }
                            }
                        } else if (c instanceof ConsumeLiquid cl) {
                            if (bd.acceptLiquid(this, cl.liquid)) {
                                bd.handleLiquid(this, cl.liquid, cl.amount);
                            }
                        } else if (c instanceof ConsumeLiquids cls) {
                            LiquidStack[] ls = cls.liquids;
                            if (ls.length > 0) for (LiquidStack lk : ls) {
                                if (bd.acceptLiquid(this, lk.liquid)) {
                                    bd.handleLiquid(this, lk.liquid, lk.amount);
                                }
                            }
                        } else if (c instanceof ConsumeLiquidFilter lf) {
                            for (Liquid lq : Vars.content.liquids()) {
                                if (lf.filter.get(lq) && bd.acceptLiquid(this, lq)) {
                                    bd.handleLiquid(this, lq, lf.amount);
                                }
                            }
                        } else if (c instanceof ConsumeItemDynamic cd) {
                            var is = cd.items.get(bd);
                            if (is.length > 0) for (ItemStack ik : is) {
                                for (int a = 0; a < ik.amount; a++) {
                                    if (bd.acceptItem(this, ik.item)) {
                                        bd.handleItem(this, ik.item);
                                    }
                                }
                            }
                        } else if (c instanceof ConsumeLiquidsDynamic ld) {
                            var ls = ld.liquids.get(bd);
                            if (ls.length > 0) for (LiquidStack lk : ls) {
                                if (bd.acceptLiquid(this, lk.liquid)) {
                                    bd.handleLiquid(this, lk.liquid, lk.amount);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }
    }
}
