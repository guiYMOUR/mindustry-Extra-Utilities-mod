package ExtraUtilities.worlds.blocks.production;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.*;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class Randomer extends Block {
    public int itemsPerSecond = 60000;
    public float powerProduction = 1000000/60f;
    public float heat = 1000;

    public Randomer(String name){
        super(name);
        hasItems = true;
        update = true;
        solid = true;
        noUpdateDisabled = true;
        envEnabled = Env.any;
        alwaysReplace = true;
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("items");
    }


    @Override
    public boolean outputsItems(){
        return true;
    }

    public class RandomBuild extends Building implements HeatBlock {
        public float counter;

        @Override
        public void updateTile(){
            int r = Mathf.random(content.items().size);
            Item outputItem = content.item(r);
            int l = Mathf.random(content.liquids().size);
            Liquid outLiquid = content.liquid(l);

            counter += Time.delta;
            float limit = 60f / itemsPerSecond;
            while(counter >= limit && outputItem != null){
                items.set(outputItem, 1);
                dump(outputItem);
                items.set(outputItem, 0);
                counter -= limit;
            }

            for(int i = 0; i < proximity.size; i++) {
                Building bd = proximity.get(i);
                if(bd != null && bd.block != null){
                    boolean has = false;
                    if(bd.block.consumers != null && bd.block.consumers.length > 0){
                        for(Consume c : bd.block.consumers){
                            if (c instanceof ConsumeLiquid cl) {
                                has = true;
                                if (bd.acceptLiquid(this, cl.liquid) && bd.liquids.get(cl.liquid) < bd.block.liquidCapacity * 2) {
                                    bd.handleLiquid(this, cl.liquid, cl.amount);
                                }
                            } else if (c instanceof ConsumeLiquids cls) {
                                has = true;
                                LiquidStack[] ls = cls.liquids;
                                if (ls.length > 0) for (LiquidStack lk : ls) {
                                    if (bd.acceptLiquid(this, lk.liquid) && bd.liquids.get(lk.liquid) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lk.liquid, lk.amount);
                                    }
                                }
                            } else if (c instanceof ConsumeLiquidFilter lf) {
                                has = true;
                                for (Liquid lq : Vars.content.liquids()) {
                                    if (lf.filter.get(lq) && bd.acceptLiquid(this, lq) && bd.liquids.get(lq) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lq, lf.amount);
                                    }
                                }
                            } else if (c instanceof ConsumeLiquidsDynamic ld) {
                                has = true;
                                var ls = ld.liquids.get(bd);
                                if (ls.length > 0) for (LiquidStack lk : ls) {
                                    if (bd.acceptLiquid(this, lk.liquid) && bd.liquids.get(lk.liquid) < bd.block.liquidCapacity * 2) {
                                        bd.handleLiquid(this, lk.liquid, lk.amount);
                                    }
                                }
                            }
                        }
                    }
                    if(!has && outLiquid != null){
                        if(bd.acceptLiquid(this, outLiquid)){
                            bd.handleLiquid(this, outLiquid, bd.block.liquidCapacity - bd.liquids.get(outLiquid));
                        }
                    }
                }
            }
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction : 0f;
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return 1;
        }
    }
}

