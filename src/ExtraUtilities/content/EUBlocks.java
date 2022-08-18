package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.production.*;
import ExtraUtilities.worlds.drawer.*;
import mindustry.content.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;

public class EUBlocks {
    public static Block
        //production
            T2oxide,
        //heat
            thermalHeater, heatTransfer, heatDriver,
        //other&sandbox
            randomer;
    public static void load(){
        T2oxide = new HeatProducer("T2oxide"){{
            requirements(Category.crafting, with(Items.oxide, 50, Items.graphite, 300, Items.silicon, 300, Items.surgeAlloy, 50, Items.carbide, 90));
            size = 5;
            hasLiquids = true;
            canOverdrive = true;

            outputItem = new ItemStack(Items.oxide, 10);

            consumeLiquids(LiquidStack.with(Liquids.ozone, 4f / 60f, Liquids.nitrogen, 4f / 60f));
            //consumeLiquid(Liquids.ozone, 4f / 60f);
            consumeItems(with(Items.beryllium, 10));
            consumePower(270f/60f);

            rotateDraw = false;
            craftEffect = Fx.drillSteam;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.ozone), new DrawDefault(), new DrawRegion("-top"), new DrawHeatOutput());

            regionRotated1 = 2;
            craftTime = 60f * 5f;
            liquidCapacity = 50f;
            itemCapacity = 80;
            heatOutput = 20f;
        }};
        thermalHeater = new ThermalHeater("thermal-heater"){{
            requirements(Category.power, with(Items.graphite, 50, Items.beryllium, 100, Items.oxide, 25));
            powerProduction = 70/60f;
            generateEffect = Fx.redgeneratespark;
            effectChance = 0.01f;
            size = 2;
            floating = true;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
            canOverdrive = true;
            basicHeatOut = 2f;
        }};
        heatTransfer = new HeatConductor("heat-transfer"){{
            requirements(Category.crafting, with(Items.tungsten, 10, Items.graphite, 8, Items.oxide, 5));
            size = 2;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            researchCostMultiplier = 5f;//因为已经解锁大的了，再多不好吧awa
        }};

        heatDriver = new HeatDriver("heat-driver"){{
            requirements(Category.crafting, with(Items.tungsten, 150, Items.beryllium, 100, Items.oxide, 50, Items.graphite, 125));
            size = 3;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"), new DrawHeatDriver());
            range = 360;
            regionRotated1 = 1;

            consumePower(4);
        }};

        randomer = new Randomer("randomer"){{
            requirements(Category.distribution, with(Items.copper, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
    }
}
