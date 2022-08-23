package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.production.*;
import ExtraUtilities.worlds.drawer.*;
import mindustry.content.*;
import mindustry.gen.Sounds;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;

public class EUBlocks {
    public static Block
        //drill?
            arkyciteExtractor, minerPoint, minerCenter,
        //production
            T2oxide,
        //heat
            thermalHeater, heatTransfer, heatDriver,
        //other&sandbox
            randomer;
    public static void load(){
        arkyciteExtractor = new DrawSolidPump("arkycite-extractor"){{
            requirements(Category.production, with(Items.carbide, 35, Items.oxide, 50, Items.thorium, 150, Items.tungsten, 100));
            consumePower(8f);
            consumeLiquid(Liquids.nitrogen, 4/60f);
            consumeItem(Items.oxide);

            consumeTime = 60 * 1.5f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.arkycite), new DrawDefault(), new DrawRegion("-top"));
            result = Liquids.arkycite;
            liquidCapacity = 120;
            pumpAmount = 1;
            size = 3;
        }};
        minerPoint = new MinerPoint("miner-point"){{
            requirements(Category.production, with(Items.beryllium, 90, Items.graphite, 100, Items.silicon, 70, Items.tungsten, 40));
            consumePower(2);
            consumeLiquid(Liquids.water, 12/60f);

            blockedItem = Items.titanium;
            droneConstructTime = 60 * 10f;
            tier = 3;
            alwaysUnlocked = true;
        }};
        minerCenter = new MinerPoint("miner-center"){{
            requirements(Category.production, with(Items.tungsten, 300, Items.oxide, 100, Items.carbide, 100, Items.surgeAlloy, 100));
            consumePower(3);
            consumeLiquid(Liquids.cyanogen, 6/60f);

            range = 22;
            alwaysCons = true;
            blockedItem = Items.thorium;
            dronesCreated = 4;
            droneConstructTime = 60 * 7f;
            tier = 5;
            size = 4;

            MinerUnit = EUUnitTypes.T2miner;

            alwaysUnlocked = true;
        }};

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
