package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.distribution.PhaseNode;
import ExtraUtilities.worlds.blocks.distribution.StackHelper;
import ExtraUtilities.worlds.blocks.fireWork;
import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.liquid.LiquidUnloadingValve;
import ExtraUtilities.worlds.blocks.liquid.SortLiquidRouter;
import ExtraUtilities.worlds.blocks.power.LightenGenerator;
import ExtraUtilities.worlds.blocks.power.SpaceGenerator;
import ExtraUtilities.worlds.blocks.power.ThermalReactor;
import ExtraUtilities.worlds.blocks.production.*;
//import ExtraUtilities.worlds.blocks.turret.MultiBulletTurret;
import ExtraUtilities.worlds.blocks.turret.MultiBulletTurret;
import ExtraUtilities.worlds.blocks.turret.TurretResupplyPoint;
import ExtraUtilities.worlds.blocks.turret.dissipation;
import ExtraUtilities.worlds.blocks.turret.guiY;
import ExtraUtilities.worlds.blocks.unit.ADCPayloadSource;
import ExtraUtilities.worlds.drawer.*;
import ExtraUtilities.worlds.entity.bullet.CtrlMissile;
import ExtraUtilities.worlds.entity.bullet.FireWorkBullet;
import ExtraUtilities.worlds.entity.bullet.ScarletDevil;
import arc.Core;
import arc.Events;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static mindustry.content.Fx.rand;
import static mindustry.content.Fx.v;
import static mindustry.type.ItemStack.*;
import static ExtraUtilities.ExtraUtilitiesMod.*;
import static ExtraUtilities.worlds.entity.bullet.FireWorkBullet.*;

public class EUBlocks {
    public static Block
        //drill?
            arkyciteExtractor, quantumExplosion, minerPoint, minerCenter,
        //liquid
            liquidSorter, liquidValve, communicatingValve, liquidIncinerator,
        //transport
            stackHelper, itemNode, liquidNode, reinforcedDuctBridge, phaseReinforcedBridgeConduit, ekMessDriver,
        //production
            T2oxide, cyanogenPyrolysis,
        /** 光束合金到此一游*/
            LA, ELA,
        //heat
            thermalHeater, slagReheater, heatTransfer, heatDistributor, heatDriver,
        //power
            liquidConsumeGenerator, thermalReactor, LG, nitrogenWell, heatPower, windPower, waterPower,
        //turret
            dissipation, guiY, onyxBlaster, celebration, celebrationMk2, sancta, turretResupplyPoint,
        //unit
            imaginaryReconstructor, finalF,
        //other&sandbox
            randomer, fireWork, allNode, ADC;
    public static void load(){
        arkyciteExtractor = new DrawSolidPump("arkycite-extractor"){{
            requirements(Category.production, with(Items.carbide, 35, Items.oxide, 50, Items.thorium, 150, Items.tungsten, 100));
            consumePower(8f);
            consumeLiquid(Liquids.nitrogen, 4/60f);
            consumeItem(Items.oxide);

            consumeTime = 60 * 2f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.arkycite), new DrawDefault(), new DrawRegion("-top"));
            result = Liquids.arkycite;
            liquidCapacity = 120;
            pumpAmount = 1;
            size = 3;
        }};
        quantumExplosion = new ExplodeDrill("quantum-explosion"){{
            if(!hardMod) {
                requirements(Category.production, with(Items.thorium, 600, Items.silicon, 800, Items.phaseFabric, 200, Items.surgeAlloy, 200));
                drillTime = 60f * 3f;
            } else {
                requirements(Category.production, with(Items.thorium, 800, Items.silicon, 1000, Items.phaseFabric, 240, Items.surgeAlloy, 260));
                drillTime = 60 * 4f;
                drillTimeBurst = 60f * 5.5f;
            }
            size = 5;
            drillMultipliers.put(Items.beryllium, 2f);
            drillMultipliers.put(Items.sand, 3f);
            drillMultipliers.put(Items.scrap, 3f);
//            for(Item i : Vars.content.items()){
//                if(i.hardness <= 2){
//                    drillMultipliers.put(i, 3-i.hardness);
//                }
//            }
            coolant = consumeCoolant(0.3f);
            tier = Integer.MAX_VALUE;
            //consumeLiquid(Liquids.water, 0.2f);
            itemCapacity = 100;
            hasPower = true;
            consumePower(160f / 60f);

            updateEffect = Fx.none;
            drillEffect = Fx.none;
            shake = 4;
            circleRange = 5;
            drawRim = true;


            alwaysUnlocked = true;
        }};
        minerPoint = new MinerPoint("miner-point"){{
            requirements(Category.production, with(Items.beryllium, 120, Items.graphite, 120, Items.silicon, 85, Items.tungsten, 50));
            consumePower(2);
            consumeLiquid(Liquids.ozone, 6/60f);

            blockedItem = Items.thorium;
            droneConstructTime = 60 * 10f;
            tier = 5 - (hardMod ? 1 : 0);
            //alwaysUnlocked = true;
        }};
        minerCenter = new MinerPoint("miner-center"){{
            requirements(Category.production, with(Items.tungsten, 360, Items.oxide, 125, Items.carbide, 120, Items.surgeAlloy, 130));
            consumePower(3);
            consumeLiquid(Liquids.cyanogen, 6/60f);

            range = 18;
            alwaysCons = true;
            //blockedItem = Items.thorium;
            dronesCreated = 6;
            droneConstructTime = 60 * 7f;
            tier = 7 - (hardMod ? 1 : 0);
            size = 4;
            itemCapacity = 300;

            MinerUnit = EUUnitTypes.T2miner;

            //alwaysUnlocked = true;
        }};


        liquidSorter = new SortLiquidRouter("liquid-sorter"){{
            requirements(Category.liquid, with(Items.silicon, 8, Items.beryllium, 4));
            liquidCapacity = 30f;
            liquidPadding = 3f/4f;
            researchCostMultiplier = 3;
            underBullets = true;
            rotate = false;

            //alwaysUnlocked = true;
        }};
        liquidValve = new SortLiquidRouter("liquid-valve"){{
            requirements(Category.liquid, with(Items.graphite, 6, Items.beryllium, 6));
            liquidCapacity = 30f;
            liquidPadding = 3f/4f;
            researchCostMultiplier = 3;
            underBullets = true;
            configurable = false;

            //alwaysUnlocked = true;
        }};
        communicatingValve = new LiquidUnloadingValve("communicating-valve"){{
            requirements(Category.liquid, with(Items.silicon, 20, Items.oxide, 25, Items.graphite, 30));
            health = 80;
        }};

        liquidIncinerator = new LiquidIncinerator("liquid-incinerator"){{
            requirements(Category.crafting, with(Items.oxide, 8, Items.silicon, 5));
            consumePower(0.9f);
            hasLiquids = true;
            size = 1;
        }};


        stackHelper = new StackHelper("stack-helper"){{
            requirements(Category.distribution, with(Items.silicon, 20, Items.phaseFabric, 10, Items.plastanium, 20));
            size = 1;
            health = 60;
            buildCostMultiplier = 0.6f;
        }};
        itemNode = new PhaseNode("i-node"){{
            requirements(Category.distribution, with(Items.copper, 110, Items.lead, 80, Items.silicon, 100, Items.graphite, 85, Items.titanium, 45, Items.thorium, 40, Items.phaseFabric, 18));
            buildCostMultiplier = 0.25f;
            range = 25 - (hardMod ? 5 : 0);
            hasPower = true;
            envEnabled |= Env.space;
            consumePower(1f);
            transportTime = 1f;

            placeableLiquid = true;
        }};
        liquidNode = new PhaseNode("lb"){{
            requirements(Category.liquid, with(Items.metaglass, 80, Items.silicon, 90, Items.graphite, 85, Items.titanium, 45, Items.thorium, 40, Items.phaseFabric, 25));
            buildCostMultiplier = 0.25f;
            range = 25 - (hardMod ? 5 : 0);
            hasPower = true;
            canOverdrive = false;
            hasLiquids = true;
            hasItems = false;
            outputsLiquid = true;
            consumePower(1f);

            placeableLiquid = true;
            //transportTime = 1;
        }};

        reinforcedDuctBridge = new DuctBridge("reinforced-duct-bridge"){{
            requirements(Category.distribution, with(Items.beryllium, 15, Items.tungsten, 15, Items.graphite, 10));
            speed = 4f;
            buildCostMultiplier = 1.5f;
            itemCapacity = 5;
            range = 6;
            researchCostMultiplier = 0.3f;
            health = 150;
        }};
        phaseReinforcedBridgeConduit = new DirectionLiquidBridge("phase-reinforced-bridge-conduit"){{
            requirements(Category.liquid, with(Items.graphite, 15, Items.beryllium, 15, Items.phaseFabric, 10));
            range = 7;
            hasPower = false;
            researchCostMultiplier = 0.5f;
            underBullets = true;
            floating = true;
            placeableLiquid = true;
            Block p = Vars.content.block("extra-utilities-conduit");
            if(p != null) ((ArmoredConduit)p).rotBridgeReplacement = this;

            health = 120;
        }};
        ekMessDriver = new MassDriver("Ek-md"){{
            requirements(Category.distribution, with(Items.silicon, 75, Items.tungsten, 100, Items.thorium, 55, Items.oxide, 45));
            size = 2;
            itemCapacity = 50;
            reload = 200f;
            range = 37.5f * 8;
            rotateSpeed = 0.6f;
            consumePower(1.4f);
            bullet = new MassDriverBolt(){{
                hittable = false;
                absorbable = false;
                collides = false;
                collidesAir = false;
                collidesGround = false;
            }
                @Override
                public void draw(Bullet b){
                    float w = 7f, h = 9f;

                    Draw.color(Pal.bulletYellowBack);
                    Draw.rect("shell-back", b.x, b.y, w, h, b.rotation() + 90);

                    Draw.color(Pal.bulletYellow);
                    Draw.rect("shell", b.x, b.y, w, h, b.rotation() + 90);

                    Draw.reset();
                }
            };
        }};


        T2oxide = new HeatProducer("T2oxide"){{
            requirements(Category.crafting, with(Items.oxide, 150, Items.graphite, 300, Items.silicon, 300, Items.carbide, 110, Items.thorium, 100));
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
            craftTime = 60f * 4f;
            liquidCapacity = 50f;
            itemCapacity = 80;
            heatOutput = 25f;
        }};
        cyanogenPyrolysis = new HeatCrafter("cyanogen-pyrolysis"){{
            requirements(Category.crafting, with(Items.thorium, 100, Items.silicon, 150, Items.tungsten, 100, Items.oxide, 50, Items.carbide, 20));
            size = 3;

            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(Liquids.arkycite), new DrawLiquidRegion(Liquids.cyanogen){{
                suffix = "-liquid2";
            }}, new DrawHeatInput());

            ambientSound = Sounds.fire;
            ambientSoundVolume = 0.02f;

            hasLiquids = true;
            liquidCapacity = 80f;
            consumePower(3f);
            heatRequirement = 8f;
            consumeLiquid(Liquids.arkycite, 40/60f);
            outputLiquid = new LiquidStack(Liquids.cyanogen, 2/60f);

            maxEfficiency = 4;
            craftTime = 4 * 60f;
        }};

        LA = new GenericCrafter("LA"){{
            requirements(Category.crafting, with(Items.silicon, 135, Items.lead, 200, Items.titanium, 120, Items.thorium, 100, Items.surgeAlloy, 55));
            hasPower = true;
            hasLiquids = true;
            hasItems = true;
            itemCapacity = 12;
            consumePower(7);
            outputItem = new ItemStack(EUItems.lightninAlloy, 2);
            craftTime = 3 * 60f;
            size = 4;
            consumeItems(with(Items.surgeAlloy, 3, Items.phaseFabric, 2, Items.blastCompound, 3));
            consumeLiquid(Liquids.cryofluid, 0.1f);
            craftEffect = EUFx.LACraft;
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.03f;

            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(), new DrawFlame(Color.valueOf("ffef99")), new DrawLA(Pal.surge, 1.6f * 8));
        }};

        ELA = new HeatCrafter("LA-E"){{
            requirements(Category.crafting, with(Items.graphite, 300, Items.silicon, 250, Items.tungsten, 250, Items.oxide, 200, Items.surgeAlloy, 200));
            size = 5;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault(), new DrawHeatInput(), new DrawLAE(new Color[]{Color.valueOf("f58349"), Color.valueOf("f58349"), EUItems.lightninAlloy.color}, 1f * 8, 3.2f), new DrawCrucibleFlame());

            craftEffect = new MultiEffect(new RadialEffect(Fx.surgeCruciSmoke, 4, 90f, 11f), new Effect(60, e ->{
                Draw.color(Pal.surge);
                Lines.stroke(5 * e.fout());
                Lines.circle(e.x, e.y, size * Vars.tilesize/2f * e.fin());
            }));

            ambientSound = Sounds.fire;
            ambientSoundVolume = 0.3f;

            hasLiquids = true;
            itemCapacity = 24;
            liquidCapacity = 60f;
            consumePower(4f);
            heatRequirement = 12f;
            consumeItems(with(Items.surgeAlloy, 4, Items.phaseFabric, 3));
            consumeLiquid(Liquids.nitrogen, 0.1f);
            outputItem = new ItemStack(EUItems.lightninAlloy, 3);

            maxEfficiency = 2;
            craftTime = 4 * 60f;
        }};

        thermalHeater = new ThermalHeater("thermal-heater"){{
            requirements(Category.power, with(Items.graphite, 50, Items.beryllium, 100, Items.oxide, 15));
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
        slagReheater = new HeatProducer("slag-reheater"){{
            requirements(Category.crafting, with(Items.tungsten, 30, Items.oxide, 30, Items.beryllium, 20));

            researchCostMultiplier = 3f;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag), new DrawDefault(), new DrawHeatOutput());
            size = 2;
            liquidCapacity = 24f;
            rotateDraw = false;
            regionRotated1 = 1;
            ambientSound = Sounds.hum;
            consumeLiquid(Liquids.slag, 24f / 60f);
            heatOutput = 5f;
        }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.productionTime);
            }
        };
        heatTransfer = new HeatConductor("heat-transfer"){{
            requirements(Category.crafting, with(Items.tungsten, 10, Items.graphite, 8, Items.oxide, 5));
            size = 2;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            researchCostMultiplier = 5f;//因为已经解锁大的了，再多不好吧awa
        }};
        heatDistributor = new HeatConductor("heat-distributor"){{
            requirements(Category.crafting, with(Items.tungsten, 10, Items.graphite, 6, Items.oxide, 5));

            researchCostMultiplier = 5f;

            size = 2;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(-1, false), new DrawHeatOutput(), new DrawHeatOutput(1, false), new DrawHeatInput("-heat"));
            regionRotated1 = 1;
            splitHeat = true;
        }};
        heatDriver = new HeatDriver("heat-driver"){{
            requirements(Category.crafting, with(Items.tungsten, 150, Items.silicon, 120, Items.oxide, 130, Items.carbide, 60));
            size = 3;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"), new DrawHeatDriver());
            range = 360;
            regionRotated1 = 1;
            if(hardMod) lost = 0.2f;

            consumePower(4);
        }};


        liquidConsumeGenerator = new ConsumeGenerator("liquid-generator"){{
            requirements(Category.power, with(Items.graphite, 120, Items.silicon, 115, Items.thorium, 65, Items.phaseFabric, 20));
            size = 3;
            powerProduction = 660/60f;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion(){{
                        sinMag = 0;
                        sinScl = 1;
                    }},
                    new DrawLiquidRegion()
            );
            consume(new ConsumeLiquidFlammable(0.4f, 0.2f));
            hasLiquids = true;
            generateEffect = new RadialEffect(new Effect(160f, e -> {
                color(Color.valueOf("6E685A"));
                alpha(0.6f);

                Rand rand = Fx.rand;
                Vec2 v = Fx.v;

                rand.setSeed(e.id);
                for(int i = 0; i < 3; i++){
                    float len = rand.random(6f), rot = rand.range(40f) + e.rotation;

                    e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                        v.trns(rot, len * b.finpow());
                        Fill.circle(e.x + v.x, e.y + v.y, 2f * b.fslope() + 0.2f);
                    });
                }
            }), 4, 90, 8f);
            effectChance = 0.2f;
        }};
        thermalReactor = new ThermalReactor("T2ther"){{
            requirements(Category.power, with(Items.silicon, 95, Items.titanium, 70, Items.thorium, 55, Items.metaglass, 65, Items.plastanium, 60, Items.surgeAlloy, 30));
            size = 3;
            if(!hardMod) powerProduction = 276/60f;
            else powerProduction = 220/60f;
            generateEffect = Fx.none;
            floating = true;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
        }};

        nitrogenWell = new ThermalGenerator("nitrogen-well"){{
            requirements(Category.power, with(Items.graphite, 100, Items.silicon, 120, Items.tungsten, 80, Items.oxide, 100));
            attribute = Attribute.steam;
            group = BlockGroup.liquids;
            displayEfficiencyScale = 1f / 9f;
            minEfficiency = 9f - 0.0001f;
            powerProduction = 90f/60f/9f;
            displayEfficiency = false;
            generateEffect = Fx.turbinegenerate;
            effectChance = 0.04f;
            size = 3;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 0.5f * 9f){{
                blurThresh = 0.01f;
            }});

            hasLiquids = true;
            outputLiquid = new LiquidStack(Liquids.nitrogen, (8f - (hardMod ? 2 : 0)) / 60f/ 9);
            liquidCapacity = 20f;
            fogRadius = 3;
        }};
        heatPower = new SpaceGenerator("heatPower"){{
            requirements(Category.power, with(Items.thorium, 150, Items.silicon, 150, Items.graphite, 200, Items.surgeAlloy, 80));
            size = 3;
            haveBasicPowerOutput = false;
            attribute = Attribute.heat;
            blockedOnlySolid = true;
            powerProduction = 45/60f;
            outEffect = new RadialEffect(EUFx.absorbEffect2, 4, 90f, 7f);
            outTimer = EUFx.absorbEffect2.lifetime;
            drawer = new DrawMulti(new DrawDefault(), new DrawBlock() {
                @Override
                public void draw(Building build) {
                    Draw.color(Items.pyratite.color);
                    Draw.alpha(build.warmup());
                    Draw.rect(Core.atlas.find(name + "-heat"), build.x, build.y);
                }
            });
        }};
        windPower = new SpaceGenerator("windPower"){{
            requirements(Category.power, with(Items.graphite, 300, Items.silicon, 200, Items.titanium, 100, EUItems.crispSteel, 80, Items.plastanium, 55));
            space = 2;
            size = 3;
            if(hardMod) powerProduction = 10/60f;

            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rot", 4, true), new DrawRegion("-top"));
            tileEffect = new Effect(220, (e) -> {
                float length = 3f + e.finpow() * 20f;
                rand.setSeed(e.id);

                for(int i = 0; i < 6; ++i) {
                    v.trns(rand.random(360), rand.random(length));
                    float sizer = rand.random(1f, 2f);
                    e.scaled(e.lifetime * rand.random(0.5f, 1), (b) -> {
                        Draw.color(Color.gray, b.fslope() * 0.93f);
                        Fill.circle(e.x + v.x, e.y + v.y, sizer + b.fslope());
                    });
                }

            });

            canOverdrive = false;
        }};
        waterPower = new SpaceGenerator("waterPower"){{
            requirements(Category.power, with(Items.graphite, 300, Items.silicon, 250, Items.surgeAlloy, 150, Items.phaseFabric, 120, EUItems.crispSteel, 150));
            size = 3;
            attribute = Attribute.water;
            attributeColor = Color.blue;
            negativeAttributeColor = Color.white;
            if(hardMod) powerProduction = 9/60f;
            else powerProduction = 13/60f;

            drawer = new DrawMulti(new DrawDefault(), new DrawRegion("-rot", 4, true), new DrawRegion("-top"));

            tileEffect = new Effect(220, (e) -> {
                float length = 3f + e.finpow() * 20f;
                rand.setSeed(e.id);

                for(int i = 0; i < 5; ++i) {
                    v.trns(rand.random(360), rand.random(length));
                    float sizer = rand.random(1f, 2f);
                    e.scaled(e.lifetime * rand.random(0.5f, 1), (b) -> {
                        Draw.color(Color.valueOf("39c5bb"), b.fslope() * 0.93f);
                        Fill.circle(e.x + v.x, e.y + v.y, sizer + b.fslope());
                    });
                }

            });

            canOverdrive = false;
        }};
        LG = new LightenGenerator("lightnin-generator"){{
            requirements(Category.power, with(Items.metaglass, 600, Items.graphite, 550, Items.silicon, 470, Items.surgeAlloy, 550, EUItems.lightninAlloy, 270));
            fuelItem = EUItems.lightninAlloy;
            consumeItem(fuelItem);
            liquidCapacity = 60;
            size = 6;
            itemCapacity = 30;
            heating = 0.04f;
            health = 6000;
            itemDuration = 180;
            powerProduction = 17280/60f;
            explosionRadius = 88;
            explosionDamage = 6000;
            coolantPower = 0.1f;

            consumeLiquid(Liquids.cryofluid, heating / coolantPower).update(false);
        }};


        dissipation = new dissipation("dissipation"){{
            requirements(Category.turret, with(Items.silicon, 180, Items.thorium, 100,EUItems.lightninAlloy, 60, Items.phaseFabric, 80));
            hasPower = true;
            size = 3;
            range = 220;
            shootCone = 36;
            rotateSpeed = 12;
            shootLength = 8;
            health = 250 * 3 * 3;
            coolantMultiplier = 5;
            coolant = consumeCoolant(0.3f);
            if(!hardMod) consumePower(12f);
            else consumePower(18f);
        }};

        guiY = new guiY("guiY"){{
            requirements(Category.turret, with(Items.beryllium, 65, Items.graphite, 90, Items.silicon, 66));
            size = 2;
            ammo(
                    Items.silicon, new CtrlMissile(name("胡萝卜"), 20, 20){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootBigSmoke2;
                        speed = 4.3f;
                        keepVelocity = false;
                        maxRange = 6f;
                        lifetime = 60f;
                        damage = 100 - (hardMod ? 15 : 0);
                        splashDamage = 120 - (hardMod ? 20 : 0);
                        splashDamageRadius = 32;
                        buildingDamageMultiplier = 0.8f;
                        absorbable = true;
                        hitEffect = despawnEffect = Fx.massiveExplosion;
                        trailColor = Pal.bulletYellowBack;
                        trailWidth = 1.7f;
                    }}
            );
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart(){{
                            progress = PartProgress.warmup;
                            moveRot = -22f;
                            moveX = 0f;
                            moveY = -0.8f;
                            mirror = true;
                        }},
                        new RegionPart("-mid"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            under = true;
                            moveY = -0.8f;
                        }}
                );
            }};
            scaledHealth = 190;

            accurateDelay = false;

            range = 26.25f * 8;
            ammoPerShot = 2;
            maxAmmo = ammoPerShot * 4;
            shoot = new ShootAlternate(6f);
            shake = 2f;
            recoil = 1f;
            reload = 60f;
            shootY = 0f;
            rotateSpeed = 1.2f;
            minWarmup = 0.85f;
            shootWarmupSpeed = 0.07f;
            shootSound = Sounds.missile;

            coolant = consume(new ConsumeLiquid(Liquids.water, 12f / 60f));
        }};

        // 梦幻联动
        onyxBlaster = new MultiBulletTurret("onyx-blaster"){{
            requirements(Category.turret, with(Items.graphite, 200, Items.silicon, 220, Items.thorium, 250, Items.surgeAlloy, 150));
            size = 4;
            health = 3200;
            int blockId = id;
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart(){{
                              progress = PartProgress.warmup;
                              moveRot = -18f;
                              moveX = 2f;
                              moveY = -0.8f;
                              mirror = true;
                          }},
                        new RegionPart("-mid"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            under = true;
                            moveY = -0.8f;
                        }},
                        new DrawBall(){{
                            y = 5;
                            bColor = Pal.sapBullet;
                            id = blockId;
                            layer = Layer.effect;
                        }}
                );
            }};
            minWarmup = 0.9f;

            rotateSpeed = 4.5f;
            all = true;
            range = 36 * 8;
            reload = 60;
            recoil = 4;
            coolant = consumeCoolant(0.5f);
            coolantMultiplier = 3;
            shootSound = Sounds.shootAltLong;
            smokeEffect = new Effect(20, e -> {
                Draw.color(Pal.sap);
                Angles.randLenVectors(e.id, 5, 20 * e.fin(), e.rotation, 30, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 5 * e.fout());
                });
            });
            shootEffect = new Effect(20, e -> {
                Draw.color(Pal.sapBullet);
                Angles.randLenVectors(e.id, 5, 20 * e.fin(), e.rotation, 30, (x, y) -> {
                    Lines.stroke(2 * e.fout());
                    float ag = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ag, 5);
                });
            });
            BulletType bb1 = new BulletType(){{
                homingPower = 1;
                homingRange = 10 * 8;
                damage = 88;
                speed = 9;
                lifetime = 30;
                trailWidth = 1;
                trailColor = Pal.heal;
                trailLength = 12;
                absorbable = false;
                hitEffect = despawnEffect = Fx.none;
            }};
            BulletType b1 = new BulletType(){{
                ammoMultiplier = 2;
                fragBullet = bb1;
                fragBullets = 4;
                fragVelocityMin = 1;
                fragRandomSpread = 30;
                damage = 0;
                speed = 0;
                lifetime = 0;
                hittable = false;
                absorbable = false;
                despawnEffect = hitEffect = Fx.none;
            }};

            BulletType bb2 = new BasicBulletType(){{
                damage = 55;
                speed = 9;
                sprite = name("shotgunShot");
                width = 3;
                height = 7;
                frontColor = Pal.sapBullet;
                lifetime = 30;
                hitEffect = despawnEffect = Fx.none;
                fragBullet = new BasicBulletType(){{
                    damage = 26;
                    frontColor = Pal.sapBullet;
                    width = 3;
                    height = 2;
                    shrinkY = 0;
                    speed = 10;
                    lifetime = 5;
                    hitEffect = despawnEffect = Fx.none;
                }};
                fragBullets = 6;
            }};
            BulletType b2 = new BulletType(){{
                ammoMultiplier = 2;
                fragBullet = bb2;
                fragBullets = 4;
                fragVelocityMin = 1;
                fragRandomSpread = 30;
                damage = 0;
                speed = 0;
                lifetime = 0;
                hittable = false;
                absorbable = false;
                despawnEffect = hitEffect = Fx.none;
            }};

            BulletType bb3 = new BasicBulletType(){{
                damage = 66;
                speed = 9;
                sprite = name("shotgunShot");
                width = 3;
                height = 7;
                frontColor = Items.thorium.color;
                lifetime = 30;
                hitEffect = despawnEffect = Fx.none;
            }};
            BulletType b3 = new BulletType(){{
                ammoMultiplier = 1;
                fragBullet = bb3;
                fragBullets = 4;
                fragVelocityMin = 1;
                fragRandomSpread = 30;
                damage = 0;
                speed = 0;
                lifetime = 0;
                hittable = false;
                absorbable = false;
                despawnEffect = hitEffect = Fx.none;
            }};

            BulletType bb4 = new BulletType(){{
                damage = 90;
                speed = 9;
                trailLength = 10;
                trailWidth = 1;
                trailColor = Color.valueOf("00EAFF");
                lifetime = 30;
                pierce = true;
                pierceBuilding = true;
                absorbable = false;
                hitEffect = despawnEffect = Fx.none;
            }};
            BulletType b4 = new BulletType(){{
                ammoMultiplier = 2;
                fragBullet = bb4;
                fragBullets = 4;
                fragVelocityMin = 1;
                fragRandomSpread = 30;
                damage = 0;
                speed = 0;
                lifetime = 0;
                hittable = false;
                absorbable = false;
                despawnEffect = hitEffect = Fx.none;
            }};

            BulletType bs = new BasicBulletType(9, 120, name("onyx-blaster-bullet")){{
                splashDamage = 120;
                splashDamageRadius = 10 * 8;
                lifetime = 30;
                width = height = 18;
                shrinkY = 0;
                status = StatusEffects.sapped;

                hitEffect = despawnEffect = new ExplosionEffect(){{
                    lifetime = 40f;
                    waveStroke = 5f;
                    waveLife = 8f;
                    waveColor = Pal.sap;
                    sparkColor = Pal.sapBulletBack;
                    smokeColor = Pal.sapBullet;
                    waveRad = 10 * 8;
                    smokeSize = 4;
                    smokes = 7;
                    smokeSizeBase = 0f;
                    sparks = 5;
                    sparkRad = 10 * 8;
                    sparkLen = 5f;
                    sparkStroke = 2f;
                }};

                ammoMultiplier = 1;
            }};
            BulletType[] bullets1 = new BulletType[]{b3, bs};
            BulletType[] bullets2 = new BulletType[]{b2, bs};
            BulletType[] bullets3 = new BulletType[]{b4, bs};
            BulletType[] bullets4 = new BulletType[]{b1, bs};
            ammo(Items.thorium, bullets1, Items.carbide, bullets2, Items.surgeAlloy, bullets3, EUItems.lightninAlloy, bullets4);
        }};
        celebration = new MultiBulletTurret("celebration"){{
            requirements(Category.turret, with(Items.silicon, 120, Items.titanium, 125, Items.thorium, 70, EUItems.crispSteel, 60));
            drawer = new DrawTurret("reinforced-");
            shoot = new ShootSpread(2, 4);
            inaccuracy = 3;
            scaledHealth = 180;
            size = 3;
            range = 27f * 8;
            shake = 2f;
            recoil = 1f;
            reload = 60f;
            shootY = 12f;
            rotateSpeed = 3.2f;
            coolant = consumeCoolant(0.3f);
            shootSound = Sounds.missile;

            BulletType f1 = new FireWorkBullet(100, 4, name("mb"), Color.valueOf("EA8878"), 6 * 8);
            BulletType f2 = new FireWorkBullet(100, 4, Color.valueOf("5CFAD5"));
            BulletType f3 = new FireWorkBullet(100, 4){{
                colorful = true;
                fire = new colorFire(false, 3f, 60){{
                    stopFrom = 0f;
                    stopTo = 0f;
                    trailLength = 9;
                }};
                splashDamageRadius = 10 * Vars.tilesize;
                trailInterval = 0;
                trailWidth = 2;
                trailLength = 8;
            }};
            BulletType fp1 = new FireWorkBullet(88, 4, name("mb"), Color.valueOf("EA8878"), 6 * 8){{
                status = StatusEffects.none;
            }};
            BulletType fp2 = new FireWorkBullet(88, 4, Color.valueOf("5CFAD5")){{
                status = StatusEffects.none;
            }};
            BulletType fp3 = new FireWorkBullet(88, 4, Items.plastanium.color){{
                fire = new colorFire(true, 5f, 60){{
                    trailLength = 9;
                    stopFrom = 0.1f;
                    stopTo = 0.7f;
                }};
                splashDamageRadius = 8 * Vars.tilesize;
            }};
            BulletType[] bullets1 = new BulletType[]{f1, f2, f3};
            BulletType[] bullets2 = new BulletType[]{fp1, fp2, fp3};
            ammo(Items.blastCompound, bullets1, Items.plastanium, bullets2, EUItems.lightninAlloy);
        }};

        celebrationMk2 = new MultiBulletTurret("celebration-mk2"){{
            size = 5;
            drawer = new DrawMulti(new DrawTurret("reinforced-"), new DrawMk2());
            requirements(Category.turret, with(Items.silicon, 410, Items.graphite, 330, Items.thorium, 280, EUItems.lightninAlloy, 250));
            inaccuracy = 3;
            shootEffect = EUFx.Mk2Shoot(90);
            smokeEffect = Fx.none;
            scaledHealth = 180;
            range = 32 * 8;
            shake = 2f;
            recoil = 2f;
            reload = 10;
            shootY = 20;
            rotateSpeed = 2.6f;
            coolant = consumeCoolant(0.8f);
            coolantMultiplier = 1.5f;
            shootSound = Sounds.missile;
            shootCone = 16;
            canOverdrive = false;
            maxAmmo = 10;

            //红
            BulletType f1 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("FF1A44"), 6 * 8){{
                outline = true;
                trailInterval = 20;
                trailEffect = new ExplosionEffect(){{
                    lifetime = 60f;
                    waveStroke = 5f;
                    waveLife = 8f;
                    waveColor = Color.white;
                    sparkColor = Pal.lightOrange;
                    smokeColor = Pal.darkerGray;
                    waveRad = 0;
                    smokeSize = 4;
                    smokes = 7;
                    smokeSizeBase = 0f;
                    sparks = 10;
                    sparkRad = 3 * 8;
                    sparkLen = 6f;
                    sparkStroke = 2f;
                }};
                trailWidth = 2.4f;
                trailLength = 10;
                pierce = true;
                pierceCap = 3;
                fire = new colorFire(false, 2.3f, 60){{
                    stopFrom = 0.55f;
                    stopTo = 0.55f;
                    rotSpeed = 666;
                }};
                num = 15;
            }};
            //橙
            BulletType ff2 = new FireWorkBullet(150, 6.7f, name("mb-mk2"), Color.valueOf("FFB22C"), 12 * 8){{
                outline = true;
                trailWidth = 3.5f;
                trailLength = 10;
                trailInterval = 0;
                width = 22;
                height = 22;
                fire = new colorFire(false, 3.6f, 60){{
                    stopFrom = 0.7f;
                    stopTo = 0.7f;
                    rotSpeed = 666;
                    hittable = true;
                    //speedRod = 0.3f;
                }};
                textFire = new spriteBullet(name("fire-EU"));
                status = StatusEffects.none;
                num = 18;
            }

                @Override
                public void update(Bullet b) {
                    super.update(b);
                    b.rotation(b.rotation() + Time.delta * 3f);
                    if(b.timer.get(3, 6)) EUFx.ellipse(14, 8/2, 40, color).at(b.x, b.y, b.rotation());
                }
            };
            BulletType f2 = new BulletType(){{
                ammoMultiplier = 1;
                damage = 0;
                speed = 0;
                lifetime = 0;
                fragBullet = ff2;
                fragBullets = 1;
                collides = false;
                absorbable = false;
                hittable = false;
                despawnEffect = hitEffect = Fx.none;
            }
                public void createFrags(Bullet b, float x, float y){
                    if(fragBullet != null && (fragOnAbsorb || !b.absorbed)){
                        fragBullet.create(b, b.x, b.y, b.rotation() - 60);
                    }
                }
            };
            //黄
            BulletType f3 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("FFF52B"), 6 * 8){{
                outline = true;
                trailInterval = 0;
                trailWidth = 2f;
                trailLength = 10;
                weaveMag = 8f;
                weaveScale = 2f;
                fire = new colorFire(false, 2.3f, 60){{
                    stopFrom = 0.55f;
                    stopTo = 0.55f;
                    rotSpeed = 666;
                    hittable = true;
                }};
                textFire = new spriteBullet(name("fire-Carrot"));
                status = StatusEffects.none;
                num = 18;
            }};
            //绿
            BulletType f4 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("2BFF5C"), 6 * 8){{
                outline = true;
                trailInterval = 0;
                trailWidth = 2.4f;
                trailLength = 10;
                homingPower = 1;
                homingRange = 32 * 8;
                width = 10;
                height = 10;
                status = StatusEffects.electrified;
                fire = new colorFire(false, 2.3f, 60){{
                    stopFrom = 0.55f;
                    stopTo = 0.55f;
                    rotSpeed = 666;
                }};
                num = 10;
            }};
            //蓝
            BulletType ff5 = new FireWorkBullet(110, 6, name("mb-mk2"), Color.valueOf("006AFF"), 8 * 8){{
                outline = true;
                trailInterval = 0;
                trailWidth = 3f;
                trailLength = 10;
                width = 19;
                height = 19;
                status = StatusEffects.wet;
                weaveMag = 8;
                weaveScale = 6;
                weaveRandom = false;
                fire = new colorFire(false, 2.8f, 60){{
                    stopFrom = 0.55f;
                    stopTo = 0.55f;
                    rotSpeed = 666;
                    //speedRod = 0.3f;
                }};
                num = 20;
            }
                public void updateWeaving(Bullet b){
                    if(weaveMag != 0 && b.data instanceof Integer){
                        b.vel.rotateRadExact((float)Math.sin((b.time + Math.PI * weaveScale/2f) / weaveScale) * weaveMag * Time.delta * Mathf.degRad * (int)b.data);
                    }
                }
            };
            BulletType f5 = new BulletType(){{
                ammoMultiplier = 1;
                damage = 0;
                speed = 0;
                lifetime = 0;
                fragBullet = ff5;
                fragBullets = 2;
                collides = false;
                absorbable = false;
                hittable = false;
                despawnEffect = hitEffect = Fx.none;
            }
                public void createFrags(Bullet b, float x, float y){
                    if(fragBullet != null && (fragOnAbsorb || !b.absorbed)){
                        for(int i : new int[]{-1, 1}) fragBullet.create(b, b.team, b.x, b.y, b.rotation() - 10 * i, -1, 1, 1, i);
                    }
                }
            };
            //紫
            BulletType ff6 = new FireWorkBullet(100, 5, name("mb-mk2"), Color.valueOf("B72BFF"), 4 * 8){{
                outline = true;
                trailInterval = 0;
                trailWidth = 2f;
                trailLength = 10;
                width = 9;
                height = 9;
                status = StatusEffects.sapped;
                fire = new colorFire(true, 4, 60){{
                    hittable = true;
                }};
            }

                @Override
                public void update(Bullet b) {
                    super.update(b);
                    if(b.data instanceof Float){
                        if(b.time > 10) b.rotation(Angles.moveToward(b.rotation(), (float) b.data, Time.delta * 0.5f));
                    }
                }
            };
            BulletType f6 = new BulletType(){{
                ammoMultiplier = 1;
                damage = 0;
                speed = 0;
                lifetime = 0;
                fragBullet = ff6;
                fragBullets = 3;
                collides = false;
                absorbable = false;
                hittable = false;
                despawnEffect = hitEffect = Fx.none;
            }
                public void createFrags(Bullet b, float x, float y){
                    if(fragBullet != null && (fragOnAbsorb || !b.absorbed)){
                        for(int i : new int[]{-1, 0, 1}) fragBullet.create(b, b.team, b.x, b.y, b.rotation() - 10 * i, -1, 1, 1, b.rotation());
                    }
                }
            };
            //粉
            BulletType f7 = new FireWorkBullet(125, 5, name("mb-mk2"), Color.valueOf("FF7DF4"), 10 * 8){{
                outline = true;
                trailInterval = 0;
                trailWidth = 2.4f;
                trailLength = 10;
                status = StatusEffects.none;
                textFire = new spriteBullet(name("fire-guiY"), 128, 128);
                fire = new colorFire(false, 3f, 60){{
                    stopFrom = 0.6f;
                    stopTo = 0.6f;
                    rotSpeed = 666;
                    hittable = true;
                }};
            }};

            BulletType[] bullets = new BulletType[]{f1, f2, f3, f4, f5, f6, f7};
            ammo(Items.thorium, bullets);
        }};

        sancta = new ItemTurret("sancta"){{
            requirements(Category.turret, with(EUItems.lightninAlloy, 1200, Items.phaseFabric, 1400));
            size = 7;
            ammo(
                    EUItems.lightninAlloy,
                            new ScarletDevil(EUItems.lightninAlloy.color){{
                                speed = 16;
                                lifetime = 35;
                                trailColor = EUItems.lightninAlloy.color;
                                trailLength = 10;
                                trailWidth = 10;
                                splashDamage = damage = 900 - (hardMod ? 300 : 0);
                                ammoMultiplier = 1;
                                hitSound = despawnSound = Sounds.explosionbig;
                                healColor = EUItems.lightninAlloy.color;
                                buildingDamageMultiplier = 0.7f;
                                if(hardMod) {
                                    fb.damage -= 15;
                                    ff.damage -= 15;
                                }
                            }

                                @Override
                                public void draw(Bullet b) {
                                    super.draw(b);
                                    Draw.color(Pal.surge);
                                    Drawf.tri(b.x, b.y, 20, 16, b.rotation());
                                    Drawf.tri(b.x, b.y, 10, 8, b.rotation()-180);
                                }

                                @Override
                                public void update(Bullet b) {
                                    super.update(b);
                                    float x = b.x + Angles.trnsx(b.rotation() - 90, 0, -trailLength * 2);
                                    float y = b.y + Angles.trnsy(b.rotation() - 90, 0, -trailLength * 2);
                                    float rx = b.x + Angles.trnsx(b.rotation() - 90, Mathf.random(3), 0);
                                    float ry = b.y + Angles.trnsy(b.rotation() - 90, Mathf.random(3), 0);
                                    if(b.timer.get(2, 6)){
                                        Fx.chainLightning.at(rx, ry, b.rotation(), Pal.surge, EUGet.pos(x, y));
                                    }
                                }
                            }
            );
            drawer = new DrawMulti(
                    new DrawTurret(){{
                        parts.add(
                                new RegionPart("-behind"){{
                                    progress = PartProgress.warmup;
                                    moveY = 7f;
                                    mirror = false;
                                    moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                                    under = true;
                                }},
                                new RegionPart("-mid"){{
                                    mirror = false;
                                }},
                                new RegionPart("-front"){{
                                    progress = PartProgress.warmup;
                                    moveY = 7f;
                                    mirror = false;
                                    moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                                }}
                        );
                        parts.add(
                                new BowHalo(){{
                                    progress = PartProgress.warmup.delay(0.8f);
                                }}
                        );
            }}, new RunningLight(6), new DrawBow(), new DrawTrail(2.5f, EUItems.lightninAlloy.color, 8));
            scaledHealth = 180;

            range = 80 * 8;
            ammoPerShot = 10;
            maxAmmo = ammoPerShot * 3;
            shake = 6f;
            recoil = 4f;
            reload = 360f;
            shootY = 0f;
            rotateSpeed = 1.2f;
            minWarmup = 0.95f;
            shootWarmupSpeed = 0.04f;
            shootSound = Sounds.largeCannon;

            coolant = consumeCoolant(2);
            coolantMultiplier = 0.5f;
            coolEffect = Fx.none;
            canOverdrive = false;
        }};

        turretResupplyPoint = new TurretResupplyPoint("turret-resupply-point"){{
            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
            size = 2;
            hasPower = true;
            consumePower(1);
        }};

        imaginaryReconstructor = new Reconstructor("imaginary-reconstructor"){{
            requirements(Category.units, with(Items.silicon, 3000, Items.graphite, 3500, Items.titanium, 1000, Items.thorium, 800, Items.plastanium, 600, Items.phaseFabric, 350, EUItems.lightninAlloy, 200));
            size = 11;
            upgrades.addAll(
                    new UnitType[]{UnitTypes.reign, EUUnitTypes.suzerain},
                    new UnitType[]{UnitTypes.corvus, EUUnitTypes.nebula},
                    new UnitType[]{UnitTypes.toxopid, EUUnitTypes.asphyxia},
                    new UnitType[]{UnitTypes.eclipse, EUUnitTypes.apocalypse},
                    new UnitType[]{UnitTypes.omura, EUUnitTypes.nihilo}
            );
            researchCostMultiplier = 0.4f;
            buildCostMultiplier = 0.7f;
            constructTime = 60 * 60 * 4.2f;

            consumePower(30f);
            consumeItems(with(Items.silicon, 1200, Items.titanium, 750, Items.plastanium, 450, Items.phaseFabric, 250, EUItems.lightninAlloy, 210));
            consumeLiquid(Liquids.cryofluid, 3.2f);
            liquidCapacity = 192;
        }};
        finalF = new UnitFactory("finalF"){{
            requirements(Category.units, with(EUItems.lightninAlloy, 1200, Items.silicon, 4000, Items.thorium, 2200, Items.phaseFabric, 1500));
            size = 5;
            consumePower(30);
            consumeLiquid(Liquids.water, 1);
            alwaysUnlocked = true;
            config(Integer.class, (UnitFactoryBuild tile, Integer i) -> {
                tile.currentPlan = i < 0 || i >= plans.size ? -1 : i;
                tile.progress = 0;
                tile.payload = null;
            });
            config(UnitType.class, (UnitFactoryBuild tile, UnitType val) -> {
                tile.currentPlan = plans.indexOf(p -> p.unit == val);
                tile.progress = 0;
                tile.payload = null;
            });
            liquidCapacity = 60;
            //buildVisibility = BuildVisibility.sandboxOnly;
        }

            @Override
            public void init() {
                for(int i = 0; i < Vars.content.units().size; i++){
                    UnitType u = Vars.content.unit(i);
                    if(u != null && u.getFirstRequirements() != null){
                        ItemStack[] is = u.getFirstRequirements();
                        ItemStack[] os = new ItemStack[is.length];
                        for (int a = 0; a < is.length; a++) {
                            os[a] = new ItemStack(is[a].item, is[a].amount >= 40 ? (int) (is[a].amount * (1.5 + (hardMod ? 0.5f : 0))) : is[a].amount);
                        }
                        float time = 0;
                        if(u.getFirstRequirements().length > 0) {
                            for (ItemStack itemStack : os) {
                                time += itemStack.amount * itemStack.item.cost;
                            }
                        }
                        if(u.armor < 30) plans.add(new UnitPlan(u, time * 6, os));
                        else plans.add(new UnitPlan(u, time * 2, is));
                    }
                }
                super.init();
            }
        };


        randomer = new Randomer("randomer"){{
            requirements(Category.distribution, with(Items.silicon, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        allNode = new PhaseNode("a-n"){{
            requirements(Category.effect, with(Items.silicon, 1));
            range = 35;
            hasPower = false;
            hasLiquids = true;
            hasItems = true;
            outputsLiquid = true;
            transportTime = 1;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        ADC = new ADCPayloadSource("ADC"){{
            requirements(Category.units, with());
            size = 5;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        fireWork = new fireWork("fireWork"){{
            requirements(Category.effect, with(Items.silicon, 10));
            size = 2;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
        }};
    }
}
