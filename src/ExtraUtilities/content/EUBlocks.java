package ExtraUtilities.content;

import ExtraUtilities.graphics.MainRenderer;
import ExtraUtilities.worlds.blocks.distribution.PhaseNode;
import ExtraUtilities.worlds.blocks.distribution.StackHelper;
import ExtraUtilities.worlds.blocks.effect.Breaker;
import ExtraUtilities.worlds.blocks.effect.CoreKeeper;
import ExtraUtilities.worlds.blocks.effect.WaterBomb;
import ExtraUtilities.worlds.blocks.fireWork;
import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.liquid.LiquidUnloadingValve;
import ExtraUtilities.worlds.blocks.liquid.SortLiquidRouter;
import ExtraUtilities.worlds.blocks.logic.Clock;
import ExtraUtilities.worlds.blocks.logic.CopyMemoryBlock;
import ExtraUtilities.worlds.blocks.power.LightenGenerator;
import ExtraUtilities.worlds.blocks.power.SpaceGenerator;
import ExtraUtilities.worlds.blocks.power.ThermalReactor;
import ExtraUtilities.worlds.blocks.production.*;
import ExtraUtilities.worlds.blocks.turret.*;
import ExtraUtilities.worlds.blocks.turret.TowerDefence.CrystalTower;
import ExtraUtilities.worlds.blocks.turret.TowerDefence.MineCell;
import ExtraUtilities.worlds.blocks.turret.wall.Aparajito;
import ExtraUtilities.worlds.blocks.turret.wall.Domain;
import ExtraUtilities.worlds.blocks.unit.ADCPayloadSource;
import ExtraUtilities.worlds.blocks.unit.DerivativeUnitFactory;
import ExtraUtilities.worlds.blocks.unit.UnitBoost;
import ExtraUtilities.worlds.consumers.BetterConsumeLiquidsDynamic;
import ExtraUtilities.worlds.drawer.*;
import ExtraUtilities.worlds.entity.animation.AnimationType;
import ExtraUtilities.worlds.entity.animation.DeathAnimation;
import ExtraUtilities.worlds.entity.bullet.*;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.*;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssemblerModule;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.*;
import static mindustry.content.Fx.*;
import static mindustry.type.ItemStack.*;
import static ExtraUtilities.ExtraUtilitiesMod.*;

public class EUBlocks {
    public static Block
        //drill!
            arkyciteExtractor, nitrogenWell, quantumExplosion, minerPoint, minerCenter,
        //liquid
            ekPump, liquidSorter, liquidValve, communicatingValve, liquidIncinerator,
        //transport
            stackHelper, itemNode, liquidNode, reinforcedDuctBridge, phaseReinforcedBridgeConduit, ekMessDriver,
        //production
            siliconFurnace, T2blast, T2sporePress, stoneExtractor, stoneCrusher, stoneMelting, T2oxide, cyanogenPyrolysis,
        /** 光束合金到此一游*/
            LA, ELA,
        //heat
            thermalHeater, ventHeater, largeElectricHeater, slagReheater, heatTransfer, heatDistributor, heatDriver,
        //power
            liquidConsumeGenerator, thermalReactor, LG, heatPower, windPower, waterPower,
        //turret
            blackhole, dissipation, anti_Missile, sandGo, guiY, javelin, antiaircraft, onyxBlaster, celebration, celebrationMk2, sancta, RG, fiammetta, turretResupplyPoint, mineCellT1, mineCellT2,
        //unit
            imaginaryReconstructor, unitBooster, advAssemblerModule, finalF,
        //other&sandbox
            aparajito, aparajitoLarge,
            buffrerdMemoryBank, clock, tableClock,
            turretSpeeder, mendTurret, coreKeeper, quantumDomain, breaker, waterBomb,
            randomer, fireWork, allNode, ADC, guiYsDomain, crystalTower ;
    public static class LiquidUnitPlan extends UnitFactory.UnitPlan{
        public LiquidStack[] liquid;

        public LiquidUnitPlan(UnitType unit, float time, ItemStack[] requirements, LiquidStack[] liquid){
            super(unit, time, requirements);
            this.liquid = liquid;
        }
    }
    public static void load(){
        arkyciteExtractor = new AttributeCrafter("arkycite-extractor"){{
            requirements(Category.production, with(Items.carbide, 35, Items.oxide, 50, Items.thorium, 150, Items.tungsten, 100));
            consumePower(8f);
            consumeLiquid(Liquids.nitrogen, 4/60f);
            consumeItem(Items.oxide);
            baseEfficiency = 0.5f;
            attribute = EUAttribute.EKOil;
            maxBoost = 1.5f;

            hasPower = hasItems = hasLiquids = true;

            craftTime = 60 * 2f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.arkycite), new DrawDefault(), new DrawRegion("-top"));
            outputLiquid = new LiquidStack(Liquids.arkycite, 1);
            liquidCapacity = 120;
            size = 3;
        }};

        nitrogenWell = new AttributeCrafter("nitrogen-well"){{
            requirements(Category.production, with(Items.graphite, 100, Items.silicon, 120, Items.tungsten, 80, Items.oxide, 100));
            attribute = Attribute.steam;
            group = BlockGroup.liquids;
            minEfficiency = 9f - 0.0001f;
            baseEfficiency = 0f;
            displayEfficiency = false;
            craftEffect = Fx.turbinegenerate;
            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 0.5f * 9f){{
                blurThresh = 0.01f;
            }});
            craftTime = 120f;
            size = 3;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
            hasLiquids = true;
            boostScale = 1f / 9f;
            outputLiquid = new LiquidStack(Liquids.nitrogen, 10f / 60f);
            consumePower(40/60f);
            liquidCapacity = 30f;
            fogRadius = 3;
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
            consumeLiquid(Liquids.cyanogen, 3/60f);

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


        ekPump = new Pump("chemical-combustion-pump"){{
            requirements(Category.liquid, with(Items.tungsten, 80, Items.silicon, 80, Items.oxide, 60, Items.carbide, 30, Items.surgeAlloy, 50));
            consumeLiquid(Liquids.cyanogen, 1f / 60f);

            pumpAmount = 240f / 60f / 9f;
            liquidCapacity = 240f;
            size = 3;
            squareSprite = false;
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


        siliconFurnace = new GenericCrafter("silicon-furnace"){{
            requirements(Category.crafting, with(Items.silicon, 130, EUItems.crispSteel, 100, Items.lead, 160, Items.thorium, 80));
            size = 4;
            itemCapacity = 35;
            consumePower(6);
            consumeItem(Items.metaglass, 3);
            consumeLiquid(Liquids.water, 12/60f);
            liquidCapacity = 36;
            outputItem = new ItemStack(Items.silicon, 7);
            outputsLiquid = true;
            Liquid out = Liquids.ozone;
            outputLiquid = new LiquidStack(out, 6/60f);
            ignoreLiquidFullness = true;
            craftTime = 60;
            craftEffect = Fx.smeltsmoke;
            Effect cfe = new Effect(160f, e -> {
                color(Liquids.ozone.color);
                alpha(0.6f);

                rand.setSeed(e.id);
                for(int i = 0; i < 3; i++){
                    float len = rand.random(6f), rot = rand.range(40f) + e.rotation;

                    e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                        v.trns(rot, len * b.finpow());
                        Fill.circle(e.x + v.x, e.y + v.y, 2f * b.fslope() + 0.2f);
                    });
                }
            });
            Effect cf = new RadialEffect(cfe, 4, 90f, 11f){
                @Override
                public void create(float x, float y, float rotation, Color color, Object data) {
                    if(!shouldCreate()) return;

                    rotation += rotationOffset;

                    for(int i = 0; i < amount; i++){
                        effect.create(x + Angles.trnsx(rotation + 45, lengthOffset), y + Angles.trnsy(rotation + 45, lengthOffset), rotation + 45, color, data);
                        rotation += rotationSpacing;
                    }
                }
            };
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")),
                    new DrawBlock() {
                        @Override
                        public void draw(Building build) {
                            if(build.liquids.get(out)/build.block.liquidCapacity > 0.99f && build.efficiency > 0.01f){
                                if(Mathf.chance(0.05f * build.efficiency)) cf.at(build);

                                float z = Draw.z();
                                Draw.z(Layer.effect);
                                Draw.color(Pal.thoriumPink);
                                for(int i = 0; i < 4; i++){
                                    float rot = 45 + 90 * i;
                                    float ax = build.x + Angles.trnsx(rot, -40);
                                    float ay = build.y + Angles.trnsy(rot, -40);
                                    for(int a = 0; a < 3; a++){
                                        float sin = Math.max(0, Mathf.sin(Time.time + a * 30f, 20f, 1f));
                                        Draw.rect(
                                                Core.atlas.find(name("aim-shoot")),
                                                ax + Angles.trnsx(rot + 180, -4) * (tilesize / 2f + a * 1.5f),
                                                ay + Angles.trnsy(rot + 180, -4) * (tilesize / 2f + a * 1.5f),
                                                25f * sin,
                                                25f * sin,
                                                rot + 90
                                        );
                                    }
                                }
                                Draw.reset();
                                Draw.z(z);
                            }
                        }
                    }
            );
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.07f;
        }};

        T2blast = new GenericCrafter("T2-blast"){{
            requirements(Category.crafting, with(Items.lead, 200, EUItems.crispSteel, 150, Items.silicon, 160, Items.thorium, 90));
            hasItems = true;
            hasLiquids = true;
            itemCapacity = 12;
            liquidCapacity = 40;
            hasPower = true;
            outputItems = new ItemStack[]{new ItemStack(Items.blastCompound, 4), new ItemStack(Items.scrap, 1)};
            size = 3;
            envEnabled |= Env.space;
            craftTime = 120f;

            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(Liquids.water));

            updateEffect = Fx.wet;
            updateEffectChance = 0.1f;
            craftEffect = EUFx.diffuse(size, Items.blastCompound.color, 20);

            consumeItems(with(Items.pyratite, 3, Items.coal, 1));
            consumeLiquid(Liquids.water, 20f/60);
            consumePower(50f/60);
        }};
        T2sporePress = new GenericCrafter("T2-spore-press"){{
            requirements(Category.crafting, with(Items.plastanium, 60, Items.silicon, 120, EUItems.crispSteel, 45));
            liquidCapacity = 60f;
            craftTime = 30f;
            outputLiquid = new LiquidStack(Liquids.oil, 1);
            size = 3;
            scaledHealth = 60;
            hasLiquids = true;
            hasPower = true;
            craftEffect = new Effect(23, e -> {
                float scl = Math.max(e.rotation, 1);
                color(Tmp.c1.set(Pal.gray).mul(1.1f), Items.sporePod.color, e.fin());
                randLenVectors(e.id, 8, size * 8f + 4 * e.finpow() * scl, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3.5f * scl + 0.3f));
            }).layer(Layer.debris);
            updateEffect = sporeSlowed;
            drawer = new DrawMulti(new DrawDefault(), new DrawFrames(), new DrawLiquidRegion());

            consumeItem(Items.sporePod, 3);
            consumePower(1.5f);
        }};

        //stone!!!
        stoneExtractor = new AttributeCrafter("stoneExtractor"){{
            requirements(Category.production, with(Items.silicon, 100, Items.graphite, 120));
            outputItem = new ItemStack(EUItems.stone, 1);
            craftTime = 30;
            size = 2;
            hasPower = true;
            attribute = EUAttribute.stone;
            baseEfficiency = 0;
            minEfficiency = 0.001f;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion("-rot", -4){{
                        x = -2.8f;
                        y = 2.8f;
                    }},
                    new DrawRegion("-rot", 4){{
                        x = 2.8f;
                        y = 2.8f;
                    }},
                    new DrawRegion("-rot", -4){{
                        x = 2.8f;
                        y = -2.8f;
                    }},
                    new DrawRegion("-rot", 4){{
                        x = -2.8f;
                        y = -2.8f;
                    }},
                    new DrawDefault()
            );

            craftEffect = Fx.smokeCloud;
            updateEffect = new Effect(20, e -> {
                color(Pal.gray, Color.lightGray, e.fin());
                randLenVectors(e.id, 6, 3f + e.fin() * 6f, (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 2f, 45));
            });

            consumePower(1f);
        }};
        stoneCrusher = new GenericCrafter("stoneCrusher"){{
            requirements(Category.crafting, with(Items.silicon, 55, Items.thorium, 40));
            consumeItem(EUItems.stone, 2);
            outputItems = ItemStack.with(Items.sand, 1, Items.scrap, 2);
            craftTime = 60;
            size = 2;
            hasPower = hasItems = true;
            consumePower(1.5f);
            craftEffect = new Effect(23, e -> {
                float scl = Math.max(e.rotation, 1);
                color(Tmp.c1.set(Pal.gray).mul(1.1f), Items.sand.color, e.fin());
                randLenVectors(e.id, 8, 19f * e.finpow() * scl, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3.5f * scl + 0.3f));
            }).layer(Layer.debris);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawFrames(){{frames = 5;}}, new DrawDefault(), new DrawRegion("-top"));
        }};
        stoneMelting = new HeatCrafter("stoneMelting"){{
            requirements(Category.crafting, with(Items.silicon, 180, EUItems.stone, 100, Items.graphite, 80, Items.oxide, 40));
            size = 3;
            consumeItem(EUItems.stone);
            heatRequirement = 6;
            outputLiquid = new LiquidStack(Liquids.slag, 20f/60f);
            hasItems = hasLiquids = true;
            hasPower = false;
            craftTime = 30f;
            liquidCapacity = 120;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidRegion(), new DrawDefault(), new DrawHeatInput());
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

            drawer = new DrawMulti(new DrawDefault(), new DrawHeatInput());

            ambientSound = Sounds.fire;
            ambientSoundVolume = 0.02f;

            hasLiquids = true;
            liquidCapacity = 80f;
            consumePower(3f);
            heatRequirement = 8f;
            consumeLiquid(Liquids.arkycite, 1f);
            outputLiquid = new LiquidStack(Liquids.cyanogen, 3f/60f);

            maxEfficiency = 4;
            craftTime = 4 * 60f;
        }};

        LA = new GenericCrafter("LA"){{
            requirements(Category.crafting, with(Items.silicon, 135, Items.lead, 200, Items.titanium, 120, Items.thorium, 100, Items.surgeAlloy, 55));
            hasPower = true;
            hasLiquids = true;
            hasItems = true;
            itemCapacity = 24;
            consumePower(7.5f);
            outputItem = new ItemStack(EUItems.lightninAlloy, 5);
            craftTime = 3.5f * 60f;
            size = 4;
            consumeItems(with(Items.surgeAlloy, 6, Items.phaseFabric, 4, Items.blastCompound, 6));
            consumeLiquid(Liquids.cryofluid, 0.2f);
            craftEffect = EUFx.diffuse(size, EUItems.lightninAlloy.color, 60);
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.03f;

            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(), new DrawFlame(Color.valueOf("ffef99")), new DrawLA(Pal.surge, 1.6f * 8));
        }};

        ELA = new HeatCrafter("LA-E"){{
            requirements(Category.crafting, with(Items.graphite, 300, Items.silicon, 250, Items.tungsten, 250, Items.oxide, 200, Items.surgeAlloy, 200));
            size = 5;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault(), new DrawHeatInput(), new DrawLAE(new Color[]{Color.valueOf("f58349"), Color.valueOf("f58349"), EUItems.lightninAlloy.color}, 1f * 8, 3.2f), new DrawCrucibleFlame());

            craftEffect = new MultiEffect(new RadialEffect(Fx.surgeCruciSmoke, 4, 90f, 11f), EUFx.diffuse(size, EUItems.lightninAlloy.color, 60));

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

            maxEfficiency = 3;
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
        ventHeater = new ThermalHeater("vent-heater"){{
            requirements(Category.crafting, with(Items.graphite, 70, Items.tungsten, 80, Items.oxide, 50));
            attribute = Attribute.steam;
            group = BlockGroup.liquids;
            displayEfficiencyScale = 1f / 9f;
            minEfficiency = 9f - 0.0001f;
            displayEfficiency = false;
            generateEffect = Fx.turbinegenerate;
            effectChance = 0.04f;
            size = 3;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;

            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
            powerProduction = 0;
            hasPower = false;
            basicHeatOut = 15/9f;
            outputsLiquid = true;
            outputLiquid = new LiquidStack(Liquids.water, 3f/60f/9f);
            sec = 9;
        }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.basePowerGeneration);
            }
        };
        largeElectricHeater = new HeatProducer("large-electric-heater"){{
            requirements(Category.crafting, with(Items.beryllium, 100, Items.tungsten, 65, Items.oxide, 75, Items.silicon, 90, Items.carbide, 50));

            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
            rotateDraw = false;
            size = 5;
            heatOutput = 25f;
            regionRotated1 = 1;
            ambientSound = Sounds.hum;
            itemCapacity = 0;
            consumePower(600f / 60f);
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
            requirements(Category.power, with(Items.graphite, 120, Items.silicon, 115, Items.thorium, 65, Items.surgeAlloy, 15));
            size = 3;
            powerProduction = 630/60f;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawWarmupRegion(){{
                        sinMag = 0;
                        sinScl = 1;
                    }},
                    new DrawLiquidRegion()
            );
            consume(new ConsumeLiquidFlammable(0.4f, 0.1f));
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
            health = 320;
            requirements(Category.power, with(Items.silicon, 95, Items.titanium, 70, Items.thorium, 55, Items.metaglass, 65, Items.plastanium, 60, Items.surgeAlloy, 30));
            size = 3;
            if(!hardMod) powerProduction = 258/60f;
            else powerProduction = 220/60f;
            generateEffect = Fx.none;
            floating = true;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
        }};

        heatPower = new SpaceGenerator("heatPower"){{
            requirements(Category.power, with(Items.thorium, 150, Items.silicon, 150, Items.graphite, 200, Items.surgeAlloy, 80));
            size = 3;
            haveBasicPowerOutput = false;
            attribute = Attribute.heat;
            blockedOnlySolid = true;
            powerProduction = 20/60f;
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
            canOverdrive = false;
        }};
        windPower = new SpaceGenerator("windPower"){{
            requirements(Category.power, with(Items.graphite, 300, Items.silicon, 200, Items.titanium, 100, EUItems.crispSteel, 80, Items.plastanium, 55));
            space = 2;
            size = 3;
            if(hardMod) powerProduction = 4.5f/60f;

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rot", 4));
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
            if(hardMod) powerProduction = 5/60f;
            else powerProduction = 6f/60f;

            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rot", 4));

            tileEffect = new Effect(220, (e) -> {
                float length = 3f + e.finpow() * 20f;
                rand.setSeed(e.id);

                for(int i = 0; i < 5; ++i) {
                    v.trns(rand.random(360), rand.random(length));
                    float sizer = rand.random(1f, 2f);
                    e.scaled(e.lifetime * rand.random(0.5f, 1), (b) -> {
                        Draw.color(EUGet.MIKU, b.fslope() * 0.93f);
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
            powerProduction = 33600/60f;
            explosionRadius = 30 * 8;
            explosionDamage = 12000;
            coolantPower = 0.1f;

            explosionProof = false;

            BulletType bi = new BulletType(){{
                damage = 0;
                speed = 6;
                lifetime = 60;
                hitEffect = Fx.none;
                despawnEffect = Fx.none;
                ammoMultiplier = 3;
                homingPower = 0.8f;
                homingRange = 10 * 8f;
                hittable = absorbable = collides = collidesGround = collidesAir = collidesTiles = false;
                keepVelocity = false;
                trailEffect = new Effect(12, e ->{
                    Draw.color(EUItems.lightninAlloy.color);
                    Drawf.tri(e.x, e.y, 4 * e.fout(), 11, e.rotation);
                    if(e.data instanceof Float){
                        float time = (float) e.data;
                        Drawf.tri(e.x, e.y, 4 * e.fout(), 15 * Math.min(1, time / 8 * 0.8f + 0.2f), e.rotation - 180);
                    }
                });
            }
                @Override
                public void update(Bullet b) {
                    b.rotation(b.rotation() + 4f * Time.delta);
                    if(b.time < b.lifetime && b.timer.get(1, 10))
                        EUFx.chainLightningFade.at(b.x, b.y, 8, EUItems.lightninAlloy.color, b.data);
                    trailEffect.at(b.x, b.y, b.rotation(), b.time);
                }

                @Override
                public void draw(Bullet b) {
                    Draw.color(EUItems.lightninAlloy.color);
                    Drawf.tri(b.x, b.y, 4, 8, b.rotation());
                    Draw.reset();
                }
            };
            BulletType bd = new BulletType(){{
                damage = explosionDamage;
                splashDamageRadius = explosionRadius;
                hittable = absorbable = collides = collidesGround = collidesAir = collidesTiles = false;
                hitEffect = despawnEffect = none;
                keepVelocity = false;
                speed = 0;
                lifetime = 150;
            }

                @Override
                public void update(Bullet b) {
                    Seq<Healthc> seq = new Seq<>();
                    float r = splashDamageRadius * (1 - b.foutpow());
                    Vars.indexer.allBuildings(b.x, b.y, r, seq::addUnique);
                    Units.nearby(b.x - r, b.y - r, r * 2, r * 2, u -> {
                        if(u.type != null && u.type.targetable && b.within(u, r)) seq.addUnique(u);
                    });
                    for(int i = 0; i < seq.size; i++){
                        Healthc hc = seq.get(i);
                        if(hc != null && !hc.dead()) {
                            if(!b.hasCollided(hc.id())) {
                                if(hc.health() <= damage) hc.kill();
                                else hc.health(hc.health() - damage);
                                b.collided.add(hc.id());
                            }
                        }
                    }
                }

                @Override
                public void draw(Bullet b) {
                    float r = splashDamageRadius * (1 - b.foutpow());
                    Lines.stroke(32 * (1 - b.finpow()), EUItems.lightninAlloy.color);
                    Lines.circle(b.x, b.y, splashDamageRadius * (1 - b.foutpow()));
                    for(float i = 0; i < r/2; i += 0.2f){
                        float a = i/r;
                        float rr = r * a + r/2;
                        Draw.alpha(a * b.foutpow() * 2);
                        Lines.stroke(0.2f);
                        Lines.circle(b.x, b.y, rr);
                    }
                    if(b.time < b.lifetime - EUFx.chainLightningFade.lifetime && b.timer.get(1, 1)){
                        float ag = Mathf.random(360);
                        float px = EUGet.dx(b.x, r, ag), py = EUGet.dy(b.y, r, ag);
                        EUFx.chainLightningFade.at(b.x, b.y, 10, EUItems.lightninAlloy.color, EUGet.pos(px, py));
                    }
                }
            };
            BulletType fbd = new fBullet(bd, 20){{
                hitSound = despawnSound = Sounds.explosionbig;
                hitSoundVolume = 2;
            }};
            Effect g1 = EUFx.gone(EUItems.lightninAlloy.color, size * 8 * 1.6f, 6);
            Effect g2 = new ExplosionEffect(){{
                lifetime = 24f;
                waveStroke = 5f;
                waveLife = 8f;
                waveColor = EUItems.lightninAlloy.color;
                sparkColor = EUItems.lightninAlloy.color;
                smokeColor = EUItems.lightninAlloy.color;
                waveRad = 8 * 8;
                smokeSize = 4;
                smokes = 7;
                sparks = 5;
                sparkRad = 6 * 8;
                sparkLen = 4f;
                sparkStroke = 1.7f;
            }};
            deathBullet = new BulletType(){{
                damage = 0;
                splashDamage = explosionDamage;
                splashDamageRadius = explosionRadius;
                lifetime = 360;
                speed = 0;
                intervalBullet = EUBulletTypes.ib;
                intervalDelay = 6;
                despawnEffect = new Effect(fbd.lifetime, e -> {
                    Lines.stroke(3, EUItems.lightninAlloy.color);
                    Lines.circle(e.x, e.y, 10 * tilesize * e.fout());
                });
                hittable = absorbable = collides = collidesGround = collidesAir = collidesTiles = false;
                ammoMultiplier = 1;
            }

                @Override
                public void update(Bullet b) {
                    if(b.time < b.lifetime && b.timer.get(1, 18 * b.fout() + 6)){
                        bi.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1, 1, EUGet.pos(b.x, b.y));
                        g1.at(b);
                        Sounds.malignShoot.at(b);
                    }
                    if(b.timer.get(2, intervalDelay)){
                        float bx = b.x + Mathf.random(-size * tilesize, size * tilesize), by = b.y + Mathf.random(-size * tilesize, size * tilesize);
                        g2.at(bx, by);
                        EUFx.chainLightningFade.at(b.x, b.y, 6, EUItems.lightninAlloy.color, EUGet.pos(bx, by));
                        intervalBullet.create(b, b.team, bx, by, Mathf.random(360), -1, 1, 1, 0f);
                    }
                }

                @Override
                public void draw(Bullet b) {
                    Lines.stroke(3f, EUItems.lightninAlloy.color);
                    float pow = Math.min(b.finpow() * 2, 1);
                    Lines.circle(b.x, b.y, 10 * tilesize * pow);
                    Fill.circle(b.x, b.y, 2 * tilesize);
                    for(int i = 0; i < 2; i++){
                        float a1 = i * 180 + b.time * 2;
                        float a2 = i * 180 - b.time * 2;
                        Drawf.tri(b.x, b.y, 10, 15 * tilesize * pow, a1);
                        Drawf.tri(b.x, b.y, 7, 6 * tilesize, a2);
                    }
                }

                @Override
                public void despawned(Bullet b) {
                    despawnEffect.at(b);
                    if(!explosionProof || state.rules.reactorExplosions) fbd.create(b, b.team, b.x, b.y, 0);
                }
            };

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

        anti_Missile = new antiMissileTurret("anti-missile"){{
            requirements(Category.turret, with(Items.graphite, 100, Items.silicon, 130, Items.oxide, 100, Items.thorium, 60));
            size = 2;
            reload = 60f / 2.67f;
            shoot = new ShootAlternate(5);
            range = 35 * 8;
            shootType = new antiMissile(10 * 8, name("mb")){{
                width = 11;
                height = 13;
                lifetime = 40 * 8f / 15f;
            }};

            rotateSpeed = 8;
            loadSpeed = 1.2f;

            consumePower(3);

            shootEffect = smokeEffect = none;
            shootSound = Sounds.malignShoot;

            coolant = consume(new ConsumeLiquid(Liquids.water, 12f / 60f));
            drawer = new DrawTurret("reinforced-");

            squareSprite = false;
        }};

        sandGo = new ItemTurret("sand-go"){{
            requirements(Category.turret, with(Items.graphite, 85));
            health = 270;
            size = 2;
            reload = 12f;
            shoot.shots = 4;
            ammoPerShot = 4;
            maxAmmo = ammoPerShot * 5;
            xRand = 2;
            inaccuracy = 24;
            coolant = null;
            canOverdrive = false;
            range = 14.5f * 8;
            drawer = new DrawTurret("reinforced-");
            shootEffect = smokeEffect = none;
            //alwaysUnlocked = true;
            ammo(Items.sand,
                    new BulletType(){{
                        shootEffect = smokeEffect = none;
                        damage = 1;
                        speed = 3.5f;
                        lifetime = (14.5f * 8)/speed;
                        trailEffect = new Effect(36, e ->{
                            color(e.color);
                            alpha(e.foutpow() + 0.3f);
                            Fill.circle(e.x + Mathf.randomSeed(e.id, -2, 2), e.y + Mathf.randomSeed(e.id, -2, 2), e.rotation * e.foutpow());
                        }).layer(Layer.flyingUnitLow - 1e-3f);
                        trailInterval = 1;
                        trailColor = Items.sand.color;
                        trailParam = 3.1f;
                        knockback = 2.3f;
                        status = StatusEffects.slow;
                        statusDuration = 60f;
                        hitEffect = despawnEffect = new Effect(24, e -> {
                            color(trailColor);
                            alpha(e.foutpow());
                            randLenVectors(e.id, 5, 11 * e.finpow(), (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, e.finpow() * 2.9f);
                            });
                        }).layer(Layer.flyingUnitLow - 1e-4f);
                        ammoMultiplier = 1;
                    }

                        @Override
                        public void draw(Bullet b) {
                            super.draw(b);
                            float z = Draw.z();
                            Draw.z(Layer.flyingUnitLow);
                            Draw.rect(Items.sand.fullIcon, b.x, b.y, b.rotation() + b.time * 4);
                            Draw.z(z);
                        }

                        @Override
                        public void updateTrailEffects(Bullet b) {
                            if(b.timer(0, trailInterval)){
                                for(int i = 0; i < 3; i++){
                                    trailEffect.at(b.x, b.y, trailParam, trailColor);
                                }
                            }
                        }
                    }
            );
        }};

        guiY = new ItemTurret("guiY"){{
            requirements(Category.turret, with(Items.beryllium, 65, Items.graphite, 90, Items.silicon, 66));
            size = 2;
            Color eccl = Color.valueOf("87CEEB");
            Color eccb = Color.valueOf("6D90BC");

            ammo(
                    Items.silicon, new CtrlMissile(name("carrot"), 20, 20){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootBigSmoke2;
                        speed = 4.3f;
                        keepVelocity = false;
                        maxRange = 6f;
                        lifetime = 60f;
                        damage = 90 - (hardMod ? 15 : 0);
                        splashDamage = 110 - (hardMod ? 20 : 0);
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

            squareSprite = false;

            coolant = consume(new ConsumeLiquid(Liquids.water, 12f / 60f));
        }};

        javelin = new PowerTurret("javelin"){{
            //1 + 1 = ⑨
            requirements(Category.turret, with(Items.surgeAlloy, 250, Items.silicon, 850, Items.carbide, 500, Items.phaseFabric, 300));
            consumePower(12f);
            heatRequirement = 45f;
            maxHeatEfficiency = 2f;
            range = 55 * 8;
            reload = 2 * 60;
            size = 5;
            shootSound = Sounds.malignShoot;
            shootWarmupSpeed = 0.06f;
            minWarmup = 0.9f;
            health = 8000;

            smokeEffect = Fx.none;
            rotateSpeed = 2.5f;
            recoil = 2f;
            recoilTime = 60f;

            Color bcr = Color.valueOf("c0ecff");
            Color bcrb = Color.valueOf("6d90bc");

            shootEffect = EUFx.ellipse(30, 30, 15, bcr);

            drawer = new DrawTurret("reinforced-"){{
                parts.add(
                        new JavelinWing(){{
                            x = 0;
                            y = -7;
                            layer = Layer.effect;
                        }},
                        new BowHalo(){{
                            progress = PartProgress.warmup.delay(0.8f);
                            x = 0;
                            y = 18;
                            stroke = 3;
                            w2 = h2 = 0;
                            w1 = 3;
                            h1 = 6;
                            radius = 4;
                            color = bcr;
                        }}
                );
            }};

            BulletType iceBar = new aimToPosBullet(){{
                damage = 180;
                splashDamage = 180;
                splashDamageRadius = 3 * 8;
                speed = 10;
                lifetime = 140;
                hitEffect = despawnEffect = new MultiEffect(new ExplosionEffect(){{
                    lifetime = 40f;
                    sparkColor = bcr;
                    waveRad = smokeSize = smokeSizeBase = 0f;
                    smokes = 0;
                    sparks = 5;
                    sparkRad = 4 * 8;
                    sparkLen = 5f;
                    sparkStroke = 2f;
                }}, new Effect(60, e -> DrawFunc.drawSnow(e.x, e.y, 2 * 8 * e.foutpow(), 0, bcr)));
                trailInterval = 0.5f;
                trailEffect = new Effect(120, e -> {
                    Draw.color(bcr);
                   Fill.circle(e.x, e.y, 3 * e.foutpow());
                });
                trailLength = 16;
                trailWidth = 3;
                trailColor = bcr;

                buildingDamageMultiplier = 0.5f;
            }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(bcr);
                    Drawf.tri(b.x, b.y, 5, 12, b.rotation());
                    Drawf.tri(b.x, b.y, 5, 5, b.rotation() - 180);
                    Lines.stroke(1, bcrb);
                    Lines.lineAngle(b.x, b.y, b.rotation(), 9f);
                    Lines.lineAngle(b.x, b.y, b.rotation() - 180, 3f);
                }

                @Override
                public void update(Bullet b) {
                    super.update(b);
                    if(b.timer.get(1, 6)) EUFx.normalIceTrail.at(b.x + Mathf.random(-6, 6), b.y + Mathf.random(-6, 6), 7, bcr);
                }
            };
            int amount = 4;
            float spread = 40f;
            float inSpread = 5;

            shootType = new BulletType(){{
                reflectable = false;
                speed = 20;
                lifetime = 22;
                damage = 520;
                splashDamage = 390;
                splashDamageRadius = 8 * 8;
                trailColor = bcr;
                trailLength = 8;
                trailWidth = 5;
                trailEffect = new Effect(40, e -> DrawFunc.drawSnow(e.x, e.y, 12 * e.fout(), 360 * e.fin(), bcr));
                trailInterval = 3;

                fragBullets = amount;
                fragBullet = iceBar;

                status = StatusEffects.freezing;

                hitEffect = despawnEffect = new ExplosionEffect(){{
                    lifetime = 40f;
                    waveStroke = 5f;
                    waveLife = 8f;
                    waveColor = bcrb;
                    sparkColor = bcr;
                    waveRad = 8 * 8;
                    smokeSize = smokes = 0;
                    smokeSizeBase = 0f;
                    sparks = 6;
                    sparkRad = 10 * 8;
                    sparkLen = 7f;
                    sparkStroke = 3f;
                }};
                shootEffect = smokeEffect = Fx.none;
                buildingDamageMultiplier = 0.5f;
            }

                @Override
                public void hitEntity(Bullet b, Hitboxc entity, float health) {
                    if(!pierce || b.collided.size >= pierceCap) explode(b);
                    super.hitEntity(b, entity, health);
                }

                @Override
                public void hit(Bullet b) {
                    explode(b);
                    super.hit(b);
                }

                public void explode(Bullet b){
                    if(!(b.owner instanceof PowerTurretBuild tb)) return;
                    for(int i = 0; i < amount; i++){
                        float angleOffset = i * spread - (amount - 1) * spread / 2f;

                        Position p1 = EUGet.pos(tb.x, tb.y);
                        Position p2 = EUGet.pos(b.x, b.y);

                        Position[] pos = {p1, p2};

                        iceBar.create(tb, tb.team, tb.x, tb.y, tb.rotation - 180 + angleOffset + Mathf.random(-inSpread, inSpread), -1, 1, 1, pos);
                    }
                }

                @Override
                public void createFrags(Bullet b, float x, float y) { }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(bcr);
                    Drawf.tri(b.x, b.y, 15, 18, b.rotation());
                    Drawf.tri(b.x, b.y, 15, 6, b.rotation() - 180);
                    Lines.stroke(1, bcrb);
                    Lines.lineAngle(b.x, b.y, b.rotation(), 15f);
                    Lines.lineAngle(b.x, b.y, b.rotation() - 180, 4f);
                }
            };
        }};

        antiaircraft = new ItemTurret("antiaircraft"){{
            requirements(Category.turret, with(Items.silicon, 400, Items.graphite, 500, Items.surgeAlloy, 180, Items.thorium, 280));
            size = 3;
            range = 45 * 8;

            health = 2000;
            reload = 60 / .7f;
            recoil = 4;
            recoilTime = recoil * reload - 30;
            shootY = 18;
            targetGround = false;
            minRange = 8 * 8;
            shake = 4;

            shootSound = Sounds.shotgun;

            ammo(Items.silicon, new BulletType(){{
                ammoMultiplier = 1;
                speed = 24;
                lifetime = 45 * 8 / speed;
                damage = splashDamage = 350;
                splashDamageRadius = 10 * 8;
                absorbable = hittable = collides = collidesTiles = collidesGround = false;
                despawnHit = false;
                scaleLife = true;
                rangeOverride = 50 * 8;
                trailEffect = new MultiEffect(
                        new Effect(40, e -> {
                            Draw.color(e.color);
                            rand.setSeed(e.id);
                            float fin = 1 - Mathf.curve(e.fout(), 0, 0.85f);
                            Tmp.v1.set((rand.chance(0.5f) ? 10 : -10) * (rand.chance(0.2f) ? 0 : fin), 0).rotate(e.rotation - 90);
                            float ex = e.x + Tmp.v1.x;
                            float ey = e.y + Tmp.v1.y;
                            Draw.rect(name("aim-shoot"), ex, ey, 64 * e.fout(), 64 * e.fout(), e.rotation - 90);
                        }),
                        new Effect(30, e ->{
                            color(e.color);
                            float[] is = {-5.4f, 5.4f};
                            for(float x : is){
                                Tmp.v2.set(x, 0).rotate(e.rotation - 90);
                                float ex = e.x + Tmp.v2.x,
                                        ey = e.y + Tmp.v2.y;
                                Drawf.tri(ex, ey, 6 * e.fout(), 28, e.rotation - 180);
                            }
                        })
                );
                trailInterval = 0.1f;
                trailColor = Pal.surge;
                trailRotation = true;
                trailWidth = 5;
                trailLength = 9;

                despawnEffect = new MultiEffect(
                        new Effect(30, b -> {
                            float pin = (1 - b.foutpow());
                            rand.setSeed(b.id);
                            for(int i = 0; i < 5; i++){
                                float a = rand.random(180);
                                float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                Draw.color(Pal.surge);
                                Drawf.tri(lx, ly, 25 * b.foutpow(), (90 + rand.random(-15, 15)) * b.foutpow(), a + 180);
                            }
                            for(int i = 0; i < 5; i++){
                                float a = 180 + rand.random(180);
                                float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                Draw.color(Pal.surge);
                                Drawf.tri(lx, ly, 25 * b.foutpow(), (90 + rand.random(-15, 15)) * b.foutpow(), a + 180);
                            }

                            Lines.stroke(3 * b.fout());
                            Lines.circle(b.x, b.y, splashDamageRadius * pin);
                        }),
                        new Effect(50, e -> {
                            rand.setSeed(e.id);
                            for(int i = 0; i < 5; i++){
                                float a = e.rotation + rand.random(-60, 60);
                                Draw.color(Pal.surge);
                                Drawf.tri(e.x, e.y, 30 * e.foutpow(), (80 + rand.random(-10, 10)) * e.foutpow(), a);
                            }
                        })
                );
                despawnShake = 3;
            }

                @Override
                public void hitEntity(Bullet b, Hitboxc entity, float health) { }

                @Override
                public void hit(Bullet b, float x, float y) { }

                @Override
                public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) { }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Drawf.tri(b.x, b.y, trailWidth * 1.8f, trailWidth * 3, b.rotation());
                }

                @Override
                public void despawned(Bullet b) {
                    Units.nearbyEnemies(b.team, b.x, b.y, splashDamageRadius, u -> {
                        if(u.checkTarget(collidesAir, collidesGround) && u.type != null && (u.targetable(b.team) || u.hittable())){
                            u.damagePierce(splashDamage);
                            float pDamage = damage * 0.2f;
                            if(u.health <= pDamage) u.kill();
                            else u.health -= pDamage;
                        }
                    });

                    super.despawned(b);
                }
            });

            ammoPerShot = 10;
            maxAmmo = ammoPerShot * 4;
            coolantMultiplier = 1.8f;

            coolant = consumeCoolant(0.6f);
            consumePower(5);
        }};

        // 梦幻联动
        onyxBlaster = new MultiBulletTurret("onyx-blaster"){{
            requirements(Category.turret, with(Items.graphite, 400, Items.silicon, 450, Items.thorium, 300, Items.surgeAlloy, 220));
            size = 4;
            health = 3200;

            int blockId = id;
            squareSprite = false;
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
                        }}
//                        new DrawBall(){{
//                            y = 5;
//                            bColor = Pal.sapBullet;
//                            id = blockId;
//                            layer = Layer.effect;
//                            endVec = new Vec2();
//                            offsetVec = new Vec2();
//                        }}
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
                Angles.randLenVectors(e.id, 5, 20 * e.fin(), e.rotation, 30, (x, y) -> Fill.circle(e.x + x, e.y + y, 5 * e.fout()));
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
                damage = 130;
                speed = 9;
                lifetime = 30;
                trailWidth = 1;
                trailColor = Pal.heal;
                trailLength = 12;
                absorbable = false;
                hitEffect = despawnEffect = Fx.none;
                lightning = 2;
                lightningColor = Pal.heal;
                lightningDamage = 16;
                status = EUStatusEffects.awsl;
                statusDuration = 18;
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

            BulletType bst = new BasicBulletType(9, 120, name("onyx-blaster-bullet")){{
                splashDamage = 90;
                splashDamageRadius = 10 * 8;
                lifetime = 30;
                width = height = 18;
                shrinkY = 0;

                hitEffect = despawnEffect = new ExplosionEffect() {{
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
            BulletType bs = new BasicBulletType(9, 120, name("onyx-blaster-bullet")){{
                splashDamage = 90;
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
            BulletType[] bullets1 = new BulletType[]{b3, bst};
            BulletType[] bullets2 = new BulletType[]{b2, bs};
            BulletType[] bullets3 = new BulletType[]{b4, bs};
            BulletType[] bullets4 = new BulletType[]{b1, bs};
            ammo(Items.thorium, bullets1, Items.carbide, bullets2, Items.surgeAlloy, bullets3, EUItems.lightninAlloy, bullets4);
        }};
        celebration = new MultiBulletTurret("celebration"){{
            requirements(Category.turret, with(Items.silicon, 160, Items.titanium, 140, Items.thorium, 80, EUItems.crispSteel, 70));
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

            squareSprite = false;
        }};

        celebrationMk2 = new MultiBulletTurret("celebration-mk2"){{
            size = 5;
            drawer = new DrawMulti(new DrawTurret("reinforced-"), new DrawMk2());
            requirements(Category.turret, with(Items.silicon, 666, Items.graphite, 521, Items.thorium, 520, EUItems.lightninAlloy, 288 + (hardMod ? 40 : 0)));
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
            coolant = consume(new ConsumeLiquid(Liquids.water, 1));
            coolantMultiplier = 0.85f;
            shootSound = Sounds.missile;
            shootCone = 16;
            canOverdrive = false;
            maxAmmo = 10;

            //红
            BulletType f1 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("FF8097"), 6 * 8){{
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
            BulletType ff2 = new FireWorkBullet(150, 6.7f, name("mb-mk2"), Color.valueOf("FFD080"), 12 * 8){{
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
            BulletType f3 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("FFF980"), 6 * 8){{
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
            BulletType f4 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("80FF9D"), 6 * 8){{
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
            BulletType ff5 = new FireWorkBullet(110, 6, name("mb-mk2"), Color.valueOf("80B5FF"), 8 * 8){{
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
            BulletType ff6 = new FireWorkBullet(100, 5, name("mb-mk2"), Color.valueOf("D580FF"), 4 * 8){{
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

            squareSprite = false;
        }};

        sancta = new ItemTurret("sancta"){{
            //int amount = 2000 + (hardMod ? 500 : 0);
            int amount = 2000;
            requirements(Category.turret, with(EUItems.lightninAlloy, amount, Items.phaseFabric, amount, Items.surgeAlloy, amount));
            size = 7;
            BulletType normal = new ScarletDevil(EUItems.lightninAlloy.color){{
                speed = 20;
                lifetime = 28;
                trailColor = EUItems.lightninAlloy.color;
                trailLength = 11;
                trailWidth = 6;
                splashDamage = damage = 1500;
                ammoMultiplier = 1;
                hitEffect = despawnEffect = new Effect(90, e -> {
                    float line = splashDamageRadius;
                    Draw.color(EUItems.lightninAlloy.color);
                    for(int i = 0; i < 36; i++){
                        float dx = EUGet.dx(e.x, line * e.finpow(), i * 10);
                        float dy = EUGet.dy(e.y, line * e.finpow(), i * 10);
                        Fill.circle(dx, dy, 2 * e.fout() + 0.1f);
                    }
                    for(int j = 0; j < 36; j++) {
                        float a = j * 10 + 360 * e.finpow();
                        float d = Mathf.cos((float) (6 * (a / 180 * Math.PI) * e.finpow())) + 1;
                        float dx = EUGet.dx(e.x, line/1.8f * e.finpow() * d, a);
                        float dy = EUGet.dy(e.y, line/1.8f * e.finpow() * d, a);
                        Lines.stroke(3.5f * e.fout() + 0.1f);
                        Lines.lineAngle(dx, dy, a, 10 * e.fout() + 0.5f);
                        //Fill.circle(dx, dy, 2 * e.fout() + 0.1f);
                    }
                });
                hitSound = despawnSound = Sounds.explosionbig;
                healColor = EUItems.lightninAlloy.color;
                buildingDamageMultiplier = 0.7f;

                fb.splashDamage = 60;
                fb.splashDamageRadius = 4.3f * 8f;
            }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(trailColor);
                    Drawf.tri(b.x, b.y, 20, 20, b.rotation());
                    Drawf.tri(b.x, b.y, 10, 8, b.rotation()-180);
                    Draw.z(Layer.flyingUnit);
                    Draw.rect(Core.atlas.find(name("sancta-bt")), b.x, b.y, 32, 50, b.rotation() - 90);
                }

                @Override
                public void update(Bullet b) {
                    super.update(b);
                    Tmp.v1.set(Mathf.sin(b.time, 2.2f, 20), 0).rotate(b.rotation() - 90);
                    Tmp.v2.set(-Mathf.sin(b.time, 2.2f, 20), 0).rotate(b.rotation() - 90);
                    float x1 = b.x + Tmp.v1.x;
                    float y1 = b.y + Tmp.v1.y;
                    float x2 = b.x + Tmp.v2.x;
                    float y2 = b.y + Tmp.v2.y;
                    artilleryTrail.at(x1, y1, 3, trailColor);
                    artilleryTrail.at(x2, y2, 3, trailColor);
                    if(b.timer.get(2, lifetime/4)){
                        EUFx.ellipse(40, 8, 16/2, 40, trailColor).at(b.x, b.y, b.rotation());
                    }
                }
            };
            Mover mover = bullet -> {
                if(bullet.type == null) return;
                float fout = Math.max((60 - bullet.time)/60, 0);
                if(bullet.time < 70) bullet.initVel(bullet.rotation(), bullet.type.speed * fout);
            };
            BulletType hard = new ScarletDevil(EUItems.lightninAlloy.color){{
                hitSound = despawnSound = Sounds.explosionbig;
                damage = 800;
                splashDamage = 800;
                splashDamageRadius = 13 * 8f;
                hitEffect = despawnEffect = new Effect(90, e -> {
                    float line = splashDamageRadius;
                    Draw.color(EUItems.lightninAlloy.color);
                    for(int i = 0; i < 36; i++){
                        float dx = EUGet.dx(e.x, line * e.finpow(), i * 10);
                        float dy = EUGet.dy(e.y, line * e.finpow(), i * 10);
                        Fill.circle(dx, dy, 2 * e.fout() + 0.1f);
                    }
                    for(int j = 0; j < 36; j++) {
                        float a = j * 10 + 360 * e.finpow();
                        float d = Mathf.cos((float) (6 * (a / 180 * Math.PI) * e.finpow())) + 1;
                        float dx = EUGet.dx(e.x, line/1.8f * e.finpow() * d, a);
                        float dy = EUGet.dy(e.y, line/1.8f * e.finpow() * d, a);
                        Lines.stroke(3.5f * e.fout() + 0.1f);
                        Lines.lineAngle(dx, dy, a, 10 * e.fout() + 0.5f);
                        //Fill.circle(dx, dy, 2 * e.fout() + 0.1f);
                    }
                });
                hitSound = despawnSound = Sounds.explosionbig;
                healColor = EUItems.lightninAlloy.color;
                buildingDamageMultiplier = 0.7f;

                pierce = true;
                pierceCap = 2;
                pierceBuilding = true;

                speed = 20;
                lifetime = 30;
                trailWidth = 7;
                trailLength = 12;
                trailColor = EUItems.lightninAlloy.color;
                fragBullet = null;
                fragBullets= 0;
                healPercent = -1;
                ff.speed = 1.5f;
                ff.fragBullets = fragBullets;
                ff.buildingDamageMultiplier = fb.buildingDamageMultiplier = 0.6f;
                ff.status = fb.status = StatusEffects.freezing;
                ff.statusDuration = fb.statusDuration = 3 * 60f;
                fb.healPercent = -1;
                fb.damage = 65;
                fb.splashDamage = 80;
                fb.splashDamageRadius = 3.3f * 8f;
                intervalBullet = fb;
                intervalBullets = 4;
                bulletInterval = 3;
                intervalDelay = 3;
                intervalAngle = - 30;
                intervalSpread = 240;
            }

                @Override
                public void updateBulletInterval(Bullet b) {
                    if (ff != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
                        float ang = b.rotation();

                        float[] fs = {-4f, 4f};
                        for(int i = 0; i < intervalBullets/2; i++) {
                            for(float as : fs) {
                                ff.create(b, b.team, b.x, b.y, ang - 90 + as + intervalAngle + i * intervalSpread, 1, 1, mover);
                            }
                        }
                    }

                }
                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(trailColor);
                    Drawf.tri(b.x, b.y, 20, 20, b.rotation());
                    Drawf.tri(b.x, b.y, 10, 8, b.rotation()-180);
                    Draw.z(Layer.flyingUnit);
                    Draw.rect(Core.atlas.find(name("sancta-bt")), b.x, b.y, 32, 50, b.rotation() - 90);
                }
                @Override
                public void update(Bullet b) {
                    super.update(b);
                    Tmp.v1.set(Mathf.sin(b.time, 2.2f, 20), 0).rotate(b.rotation() - 90);
                    Tmp.v2.set(-Mathf.sin(b.time, 2.2f, 20), 0).rotate(b.rotation() - 90);
                    float x1 = b.x + Tmp.v1.x;
                    float y1 = b.y + Tmp.v1.y;
                    float x2 = b.x + Tmp.v2.x;
                    float y2 = b.y + Tmp.v2.y;
                    artilleryTrail.at(x1, y1, 3, trailColor);
                    artilleryTrail.at(x2, y2, 3, trailColor);
                    if(b.timer.get(3, lifetime/4)){
                        EUFx.ellipse(40, 8, 16/2, 40, trailColor).at(b.x, b.y, b.rotation());
                    }
                }
            };
            ammo(
                    EUItems.lightninAlloy, hardMod ? hard : normal
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
                                new PartBow(){{
                                    arrowSp = name("sancta-bt");
                                }},
                                new BowHalo(){{
                                    progress = PartProgress.warmup.delay(0.8f);
                                }}
                        );
            }}, new RunningLight(6)
//                    new DrawBow(){{
//                        arrowSp = name("sancta-bt");
//                    }},
                    //new DrawTrail(2.5f, EUItems.lightninAlloy.color, 8)
            );
            scaledHealth = 180;

            range = 80 * 8;
            ammoPerShot = hardMod ? 8 : 15;
            maxAmmo = ammoPerShot * 3;
            shake = 6f;
            recoil = 4f;
            reload = hardMod ? 195f : 360f;
            shootY = 0f;
            rotateSpeed = 1.2f;
            minWarmup = 0.95f;
            shootWarmupSpeed = 0.04f;
            shootSound = Sounds.largeCannon;

            coolant = consume(new ConsumeLiquid(Liquids.water, 120f / 60f));
            coolantMultiplier = 0.3f;
            coolEffect = Fx.none;
            canOverdrive = false;
            squareSprite = false;
        }};

        blackhole = new aimBulletTurret("blackhole"){{
            requirements(Category.turret, with(Items.silicon, 600, EUItems.lightninAlloy, 400, Items.phaseFabric, 300));
            coolant = consume(new ConsumeLiquid(Liquids.water, 1));
            coolantMultiplier = 0.5f;
            BulletType bt = new BlackHoleBullet(){{
                inRad =  8 * 1.8f;
                outRad = drawSize = 8f * 8;
                lifetime = 180;
            }};
            aimBullet = new fBullet(new fBullet(bt, 30), 0){{
                despawnEffect = new Effect(30, e -> {
                    MainRenderer.addBlackHole(e.x, e.y, 0, 20 * 8f * e.foutpow());
                    Draw.color(Color.white);
                    for(int i = 0; i < 4; i++){
                        float a = 90 * i;
                        float l = i % 2 == 0 ? 60 : 25;
                        Drawf.tri(e.x, e.y, 8 * e.foutpow(), l * e.fin(Interp.pow2Out), a);
                    }
                });
            }};

            ammo(Items.blastCompound, new BasicBulletType(){{
                ammoMultiplier = 1;
                sprite = "extra-utilities-blackhole-missile";
                shootEffect = Fx.none;
                smokeEffect = Fx.none;
                width = 25;
                height = 35;
                shrinkY = 0;
                damage = 1500;
                splashDamage = 1500;
                splashDamageRadius = 12 * 8f;
                buildingDamageMultiplier = 0.5f;
                pierceArmor = true;

                homingPower = 1;
                homingRange = 80;
                lifetime = 20;
                speed = 16;
                trailWidth = 3;
                trailLength = 9;
                trailChance = 1;
                trailColor = Color.valueOf("6f6f6f");
                hitSound = Sounds.laser;
                despawnSound = Sounds.laser;
                hitShake = 3;
                trailEffect = new Effect(50, e -> {
                    Draw.color(e.color);
                    Fill.circle(e.x + Mathf.randomSeed(e.id, -5, 5), e.y + Mathf.randomSeed(e.id, -5, 5), e.rotation * 2 * e.fout());
                }).layer(Layer.bullet - 1e-2f);
                fragBullets = 9;
                fragRandomSpread = 0;
                fragSpread = 360/9f;
                fragLifeMin = 0.1f;
                Color c1 = Color.valueOf("be92f9");
                fragBullet = new ArtilleryBulletType(6f, 120){{
                    buildingDamageMultiplier = 0.3f;
                    drag = 0.02f;
                    hitEffect = despawnEffect = new MultiEffect(
                            Fx.scatheSlash,
                            Fx.massiveExplosion,
                            new ExplosionEffect(){{
                                waveLife = 12;
                                waveRad = 48;
                                sparks = 8;
                                smokes = 8;
                                lifetime = 30;
                                sparkColor = c1;
                                sparkLen = 10;
                                sparkStroke = 2;
                                sparkRad = 48;
                                smokeSize = 4;
                                smokeRad = 48;
                            }}
                    );
                    knockback = 0.8f;
                    lifetime = 18;
                    width = height = 18f;
                    collidesTiles = false;
                    splashDamageRadius = 48f;
                    splashDamage = 180f;
                    backColor = trailColor = hitColor = Color.valueOf("be92f9");
                    frontColor = Color.white;
                    despawnShake = 7f;
                    lightRadius = 30f;
                    lightColor = Color.valueOf("be92f9");
                    lightOpacity = 0.5f;

                    trailLength = 20;
                    trailWidth = 3.5f;
                    trailEffect = Fx.none;
                }};

                chargeEffect = new MultiEffect(
                        EUFx.aimEffect(180, c1, 1f, 45 * 8f, 16),
                        new Effect(180, e -> {
                            Draw.color(c1);
                            Fill.circle(e.x, e.y, 8.5f * e.finpow());
                            float z = Draw.z();
                            Draw.z(Layer.max - 8);
                            Draw.color(Color.black);
                            Fill.circle(e.x, e.y, 7.8f * e.finpow());
                            Draw.z(z);
                            Angles.randLenVectors(e.id, 8, 36 * e.foutpow(), Mathf.randomSeed(e.id, 360), 360, (x, y) -> {
                                Draw.color(c1);
                                Fill.circle(e.x + x, e.y + y, 5f * e.foutpow());
                                float zs = Draw.z();
                                Draw.z(Layer.max - 10);
                                Draw.color(Color.black);
                                Fill.circle(e.x + x, e.y + y, 5f * e.foutpow());
                                Draw.z(zs);
                            });
                        })
                );
                chargeSound = Sounds.lasercharge;

                despawnEffect = hitEffect = new MultiEffect(
                        new Effect(60, 100, e -> {
                            float rad = splashDamageRadius;

                            Lines.stroke(e.foutpow() * 3, c1);
                            Lines.circle(e.x, e.y, rad * e.finpow());
                        }),
                        new ExplosionEffect(){{
                            waveLife = 0;
                            waveRad = 0;
                            sparks = 8;
                            smokes = 8;
                            lifetime = 50;
                            sparkColor = c1;
                            sparkLen = 10;
                            sparkStroke = 4;
                            sparkRad = splashDamageRadius;
                            smokeSize = 9;
                            smokeRad = splashDamageRadius;
                        }}
                );
            }});
            recoil = 4f;
            rotateSpeed = 2f;
            shootCone = 4f;
            predictTarget = false;
            moveWhileCharging = false;
            ammoPerShot = 6;
            maxAmmo = ammoPerShot * 5;
            shoot.firstShotDelay = 180;
            size = 5;
            health = 1000;
            consumePower(10);
            range = 40 * 8;
            reload = 360;
            shootSound = Sounds.missileLarge;
            canOverdrive = false;

            drawer = new DrawTurret("reinforced-");
        }};

        fiammetta = new Fiammetta("fiammetta"){{
            requirements(Category.turret, with(EUItems.lightninAlloy, 280, Items.oxide, 500, Items.carbide, 300, Items.silicon, 600, Items.surgeAlloy, 300));
            size = 5;
            shake = 10;
            reload = 3 * 60;
            ammoPerShot = 5;
            maxAmmo = ammoPerShot * 3;
            range = 57.5f * 8;
            minRange = 20 * 8f;
            shootSound = Sounds.laserbig;
            recoil = 5;
            health = 6000;
            canOverdrive = false;
            squareSprite = false;

            BulletType fall = new BulletType(){{
                speed = 0;
                lifetime = 20;
                collides = collidesTiles = hittable = absorbable = false;
                collidesAir = collidesGround = true;
                splashDamage = 1500;
                splashDamageRadius = 14 * 8f;
                despawnEffect = hitEffect = new MultiEffect(EUFx.expFtEffect(10, 15, splashDamageRadius, 30, 0.2f), EUFx.fiammettaExp(splashDamageRadius), new Effect(20, e -> {
                    Lines.stroke(16 * e.fout(), EUItems.lightninAlloy.color);
                    Lines.circle(e.x, e.y, (splashDamageRadius + 56) * e.fin());
                }));
                keepVelocity = false;
                buildingDamageMultiplier = 0.6f;

                hitSound = despawnSound = Sounds.explosionbig;
                despawnShake = hitShake = 8;
            }

                @Override
                public void draw(Bullet b) {
                    TextureRegion region = Core.atlas.find(name("mb-mk2"));
                    if(b.time < 10){
                        float fin = b.time/10, fout = 1 - fin;
                        float ww = 15 * 8, hh = 15 * 8 * fout;
                        Draw.color(EUItems.lightninAlloy.color);
                        Draw.alpha(fin);
                        Draw.rect(region, b.x, b.y, ww, hh, b.rotation() - 90);
                    }
                    Draw.color(EUItems.lightninAlloy.color);
                    Draw.alpha(b.fin());
                    Fill.circle(b.x, b.y, 20 * (b.time < 10 ? b.fin() * 2 : b.fout() * 2));
                }
            };

            Effect se = EUFx.aimEffect(40, EUItems.lightninAlloy.color, 1.5f, range, 13);
            ammo(
                    Items.surgeAlloy,
                    new BulletType(){{
                        chargeEffect = se;

                        ammoMultiplier = 1;
                        damage = 0;
                        collides = collidesTiles = false;
                        splashDamageRadius = 10 * 8;
                        splashDamage = 400;
                        lifetime = 30;
                        speed = 15;
                        pierce = true;
                        pierceBuilding = true;
                        hittable = false;
                        absorbable = false;
                        reflectable = false;
                        intervalBullet = new BulletType(){{
                            lifetime = 32;
                            speed = 0;
                            despawnEffect = hitEffect = new MultiEffect(new Effect(30, e -> {
                                float r = Math.min(10 * 8 * e.fin(), 6 * 8);
                                Draw.color(EUItems.lightninAlloy.color.cpy().a(e.fout()));
                                Fill.circle(e.x, e.y, r);
                                float ww = r * 2f, hh = r * 2f;
                                Draw.color(EUItems.lightninAlloy.color.cpy().a(e.fout()));
                                Draw.rect(Core.atlas.find(name("firebird-light")), e.x, e.y, ww, hh);
                            }), EUFx.expFtEffect(5, 12, 6 * 4, 30, 0.2f));
                            despawnSound = hitSound = Sounds.explosion;
                            collides = absorbable = hittable = false;
                            splashDamageRadius = 6 * 8;
                            splashDamage = 300;
                        }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                float ft = (b.time > 16 ? b.fout() * 2 : 1);
                                Lines.stroke(5, EUItems.lightninAlloy.color);
                                Lines.circle(b.x, b.y, 12 * ft);
                                Lines.poly(b.x, b.y, 3, 12 * ft, b.fout() * 180);
                            }
                        };
                        intervalDelay = 4;
                        intervalSpread = intervalRandomSpread = 0;
                        bulletInterval = 4;
                        hitSize = 20;
                        despawnEffect = new MultiEffect(new Effect(30, e -> {
                            float r = Math.min(16 * 8 * e.fin(), 10 * 8);
                            Draw.color(EUItems.lightninAlloy.color.cpy().a(e.fout()));
                            Fill.circle(e.x, e.y, r);
                            float ww = r * 2f, hh = r * 2f;
                            Draw.color(EUItems.lightninAlloy.color.cpy().a(e.fout()));
                            Draw.rect(Core.atlas.find(name("firebird-light")), e.x, e.y, ww, hh);
                        }), EUFx.expFtEffect(6, 15, 10 * 4, 30, 0.2f));
                        despawnSound = Sounds.explosion;
                        hitEffect = Fx.none;
                        trailLength = 15;
                        trailColor = EUItems.lightninAlloy.color;
                        trailWidth = 4;
                        trailRotation = true;
                        trailEffect = new Effect(15, e ->{
                            color(e.color);
                            for(int x : new int[]{-20, 20}){
                                Tmp.v1.set(x, -10).rotate(e.rotation - 90);
                                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4 * e.foutpow());
                            }
                        });
                        trailInterval = 0.1f;
                    }

                        @Override
                        public void draw(Bullet b) {
                            super.draw(b);
                            Draw.color(EUItems.lightninAlloy.color);
                            Draw.rect(Core.atlas.find(name("phx")), b.x, b.y,48, 48,  b.rotation() - 90);
                            //Drawf.tri(b.x + Angles.trnsx(b.rotation(), 10), b.x + Angles.trnsy(b.rotation(), 10), 10, 20, b.rotation());
                        }
                    },
                    EUItems.lightninAlloy, new ArtilleryBulletType(){{
                        speed = 10;
                        ammoMultiplier = 5;
                        splashDamage = 1500;
                        splashDamageRadius = 14 * 8f;
                        hittable = absorbable = false;
                        collides = collidesTiles = false;
                        collidesAir = collidesGround = false;
                        despawnEffect = Fx.none;
                        hitEffect = Fx.none;
                        trailEffect = Fx.none;
                        fragOnHit = false;
                        rangeChange = 10 * 8;
                        trailLength = 20;
                        trailWidth = 12;
                        trailColor = EUItems.lightninAlloy.color.cpy().a(0.6f);
                        buildingDamageMultiplier = 0.3f;
                    }

                        @Override
                        public void update(Bullet b) {
                            super.update(b);
                            EUFx.normalTrail.at(b.x + Mathf.random(-10, 10), b.y + Mathf.random(-10, 10), 15 * b.fin(), EUItems.lightninAlloy.color.cpy().a(0.6f));
                        }

                        @Override
                        public void updateTrail(Bullet b) {
                            if(!headless && trailLength > 0){
                                if(b.trail == null){
                                    b.trail = new Trail(trailLength);
                                }
                                b.trail.length = 2 + (int) (trailLength * b.fin());
                                b.trail.update(b.x, b.y, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
                            }
                        }

                        @Override
                        public void draw(Bullet b) {
                            TextureRegion region = Core.atlas.find(name("mb-mk2"));
                            float ww = 15 * 8 * b.fin(), hh = 15 * 8 * b.fin();
                            Draw.color(EUItems.lightninAlloy.color);
                            //Draw.alpha(b.fout());
                            Draw.rect(region, b.x, b.y, ww, hh, b.rotation() - 90);
                            drawTrail(b);
                        }

                        @Override
                        public void drawTrail(Bullet b) {
                            if(trailLength > 0 && b.trail != null){
                                float z = Draw.z();
                                Draw.z(z - 0.0001f);
                                b.trail.draw(trailColor, 2 + trailWidth * b.fin());
                                Draw.z(z);
                            }
                        }

                        @Override
                        public void createFrags(Bullet b, float x, float y) {
                            fall.create(b, b.x, b.y, b.rotation());
                        }
                    }
            );

            shoot.firstShotDelay = se.lifetime;
            moveWhileCharging = false;
            accurateDelay = false;

            drawer = new DrawMulti(new DrawTurret("reinforced-")
//                    new DrawTrail(2f, EUItems.lightninAlloy.color, 16){{
//                y = - 10;
//            }}
            );
        }};

        turretResupplyPoint = new TurretResupplyPoint("turret-resupply-point"){{
            requirements(Category.turret, with(Items.graphite, 80, Items.silicon, 120, Items.thorium, 70, Items.titanium, 50));
            size = 2;
            hasPower = true;
            consumePower(1);
        }};

        turretSpeeder = new TurretSpeeder("turret-speeder"){{
            requirements(Category.turret, with(Items.oxide, 120, Items.silicon, 200, Items.thorium, 100, Items.surgeAlloy, 40));
            size = 3;
            speedBoost = 1.33f;

            consumePower(280/60f);
        }};


        Seq<String> floor = Seq.with(
                name("walkFloor"),
                name("walkfloor-water"),
                name("land-water-cross")
        );
        mineCellT1 = new MineCell("mine-cell-t1"){{
            requirements(Category.turret, with(Items.silicon, 500, Items.thorium, 180, EUItems.crispSteel, 200, Items.graphite, 250, Items.titanium, 200));
            size = 2;
            range = 72;
            mineInter = 150;
            floors = floor;
            mineConsumes = with(Items.titanium, 1);
            consumePower(1.5f);

            mine = new BulletType(){{
                lifetime = 30 * 60f;
                damage = 25;
                pierceArmor = true;
                collidesTiles = collidesAir = false;
                hitEffect = despawnEffect = none;
                ammoMultiplier = mines;
                hitSize = 6;
            }
                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    if(!(b.data instanceof Float f)) return;
                    float z = Draw.z();
                    Draw.z(Layer.blockOver + 1);

                    for(int i = 0; i < 3; i ++){
                        Draw.color(Pal.gray);
                        Drawf.tri(b.x, b.y, 1.6f, 4f, f + 120 * i);
                        Draw.color(b.team.color);
                        Drawf.tri(b.x, b.y, 1.2f, 3f, f + 120 * i);
                    }
                    Draw.reset();
                    Draw.z(z);
                }
            };
        }};

        mineCellT2 = new MineCell("mine-cell-t2"){{
            requirements(Category.turret, with(Items.silicon, 600, Items.thorium, 200, EUItems.lightninAlloy, 100, Items.phaseFabric, 150));
            size = 4;
            range = 88;
            floors = floor;
            mines = 1;
            mineSpread = 3;
            mineInter = 180;
            mineConsumes = with(Items.thorium, 1, Items.graphite, 2);

            fms = 6;

            consumePower(2);

            int cap = 5;

            mine = new BulletType(){{
                lifetime = 45 * 60f;
                damage = 30;
                pierceArmor = true;
                collides = collidesAir = collidesGround = collidesTiles = false;
                hitEffect = despawnEffect = none;
                ammoMultiplier = mines;
                hitSize = 6;
                fragOnAbsorb = true;
                pierce = true;

                fragBullets = 1;
                despawnHit = true;
                fragBullet = new BulletType(){{
                    lifetime = 0;
                    speed = 0;
                    damage = 0;
                    splashDamage = 300;
                    splashDamageRadius = 10f * 8;
                    hitEffect = despawnEffect = new ExplosionEffect(){{
                        waveRad = smokeRad = sparkRad = splashDamageRadius;
                        waveLife = 12;
                        waveStroke = 3;
                        smokes = 8;
                        smokeSize = 9;
                        smokeSizeBase = 2;
                        sparkLen = 5;
                        sparks = 5;
                        sparkStroke = 2f;
                        smokeColor = Pal.gray;
                        sparkColor = Pal.darkFlame;
                    }};
                    pierceArmor = true;
                    absorbable = keepVelocity = hittable = false;
                    hitSound = despawnSound = Sounds.explosion;
                    status = EUStatusEffects.awsl;
                    statusDuration = 60f;
                }};
            }

                @Override
                public void update(Bullet b) {
                    Units.nearbyEnemies(b.team, b.x, b.y, 1.2f * 8, u -> {
                        if(u != null && !u.isFlying()) {
                            while (!u.dead && b.collided.size < cap) {
                                u.damagePierce(damage);
                                b.collided.add(u.id);
                            }
                        }
                    });

                    if(b.collided.size >= cap) b.remove();
                }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    if(!(b.data instanceof Float f)) return;
                    float z = Draw.z();
                    Draw.z(Layer.blockOver + 1);
                    for(int i = 0; i < cap - b.collided.size; i ++){
                        Draw.color(Pal.gray);
                        Drawf.tri(b.x, b.y, 5.5f, 7f, f + 360f/cap * i);
                        Draw.color(b.team.color);
                        Drawf.tri(b.x, b.y, 5f, 6f, f + 360f/cap * i);
                    }

                    for(int i : Mathf.signs){
                        Lines.stroke(2f, Pal.gray);
                        Lines.square(b.x, b.y, 3, b.time * 1.5f * i);
                        Lines.stroke(1.5f, b.team.color);
                        Lines.square(b.x, b.y, 3, b.time * 1.5f * i);
                    }

                    Draw.reset();
                    Draw.z(z);
                }
            };
        }};

        imaginaryReconstructor = new Reconstructor("imaginary-reconstructor"){{
            requirements(Category.units, with(Items.silicon, 3000, Items.graphite, 3500, Items.titanium, 1000, Items.thorium, 800, Items.plastanium, 600, Items.phaseFabric, 350, EUItems.lightninAlloy, 200));
            size = 11;
            upgrades.addAll(
                    new UnitType[]{UnitTypes.reign, EUUnitTypes.suzerain},
                    new UnitType[]{UnitTypes.corvus, EUUnitTypes.nebula},
                    new UnitType[]{UnitTypes.toxopid, EUUnitTypes.asphyxia},
                    new UnitType[]{UnitTypes.oct, EUUnitTypes.Tera},
                    new UnitType[]{UnitTypes.eclipse, EUUnitTypes.apocalypse},
                    new UnitType[]{UnitTypes.omura, EUUnitTypes.nihilo},
                    new UnitType[]{UnitTypes.navanax, EUUnitTypes.narwhal}
            );
            researchCostMultiplier = 0.4f;
            buildCostMultiplier = 0.7f;
            constructTime = 60 * 60 * 4.2f;

            consumePower(30f);
            consumeItems(with(Items.silicon, 1200, Items.titanium, 750, Items.plastanium, 450, Items.phaseFabric, 250, EUItems.lightninAlloy, 210));
            consumeLiquid(Liquids.cryofluid, 3.2f);
            liquidCapacity = 192;
        }};


        unitBooster = new UnitBoost("unit-boost"){{
            requirements(Category.units, ItemStack.with(Items.thorium, 200, Items.surgeAlloy, 100, Items.silicon, 250, Items.tungsten, 200, Items.carbide, 150));
            size = 3;
            status = new StatusEffect[]{StatusEffects.fast, StatusEffects.overclock};
            boostStatus = new StatusEffect[]{EUStatusEffects.defenseUp, EUStatusEffects.fireDamageUp};
            hasPower = true;
            consumePower(6);
            consumeItem(Items.phaseFabric).boost();
        }};

        advAssemblerModule = new UnitAssemblerModule("adv-assembler-module"){{
            requirements(Category.units, ItemStack.with(Items.carbide, 400, Items.surgeAlloy, 400, Items.thorium, 600, Items.phaseFabric, 400));
            consumePower(5.5f);
            regionSuffix = "-dark";
            researchCostMultiplier = 0.75f;
            size = 5;
            tier = 2;
        }};
        
        finalF = new DerivativeUnitFactory("finalF"){{
            requirements(Category.units, with(EUItems.lightninAlloy, 4000, Items.silicon, 6000, Items.thorium, 4000, Items.phaseFabric, 3000, Items.surgeAlloy, 3000));
            size = 5;
            consumePower(40);
            consume(new BetterConsumeLiquidsDynamic((e) -> ((UnitFactoryBuild)e).currentPlan != -1 ? ((LiquidUnitPlan)plans.get(Math.min(((UnitFactoryBuild)e).currentPlan, plans.size - 1))).liquid : LiquidStack.empty, new Liquid[]{Liquids.cryofluid, Liquids.slag, Liquids.water, Liquids.cyanogen}));
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
            placeableLiquid = true;
            floating = true;
            buildVisibility = hardMod ? BuildVisibility.sandboxOnly : BuildVisibility.shown;
            //buildVisibility = BuildVisibility.sandboxOnly;
        }

            public final ObjectMap<Integer, Seq<UnitType>> utp = new ObjectMap<>();

            BulletType frags(BulletType t){
                BulletType b = t;
                while(b != null){
                    if(b.damage > 1000 || b.splashDamage > 1000) return b;
                    b = b.fragBullet;
                }
                return null;
            }

            boolean ekOnly(ItemStack[] stacks){
                if(stacks.length == 0) return false;
                for(ItemStack stack : stacks) {
                    if(Items.erekirOnlyItems.contains(stack.item)) return true;
                }
                return false;
            }
//
//            boolean checkDamage(Seq<Weapon> ws){
//                if(ws.size == 0) return false;
//                for(Weapon w : ws){
//                    if(w.bullet != null && frags(w.bullet) != null) return true;
//                }
//                return false;
//            }


            @Override
            public void init() {
                for(int i = 1; i <= 5; i++) utp.put(i, new Seq<>());
                utp.put(1, new Seq<>());
                for(int i = 0; i < Vars.content.units().size; i++) {
                    UnitType u = Vars.content.unit(i);
                    if(u != null && u.getFirstRequirements() != null){
                        if(u.armor <= 11 && u.health <= 5000){
                            utp.get(1).addUnique(u);
                        } else if(u.armor <= 20 && u.health <= 12000){
                            utp.get(2).addUnique(u);
                        } else if(u.armor <= 30 && u.health <= 30000){
                            utp.get(3).addUnique(u);
                        } else if(u.armor <= 55 && u.health <= 65000){
                            utp.get(4).addUnique(u);
                        } else {
                            utp.get(5).addUnique(u);
                        }
                    }
                }
                LiquidStack[][] ls = new LiquidStack[][]{
                        LiquidStack.with(Liquids.water, 0.25f),
                        LiquidStack.with(Liquids.cryofluid, 1), 
                        LiquidStack.with(Liquids.cryofluid, 2, Liquids.slag, 0.2f),
                        LiquidStack.with(Liquids.cryofluid, 3f, Liquids.slag, 0.5f),
                        LiquidStack.with(Liquids.cryofluid, 4f, Liquids.slag, 1)
                };
                LiquidStack[][] le = new LiquidStack[][]{
                        LiquidStack.with(Liquids.water, 0.2f),
                        LiquidStack.with(Liquids.cyanogen, 0.15f),
                        LiquidStack.with(Liquids.cyanogen, 0.25f),
                        LiquidStack.with(Liquids.cyanogen, 0.4f, Liquids.slag, 0.5f),
                        LiquidStack.with(Liquids.cyanogen, 0.6f, Liquids.slag, 1f)
                };
                Seq<Integer> tiers = utp.keys().toSeq();
                for(int i : tiers){
                    Seq<UnitType> unitTypes = utp.get(i);
                    if(unitTypes.size > 0) {
                        for(UnitType u : unitTypes){
                            if(u != null){
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

                                LiquidStack[][] cpl = ekOnly(is) ? le : ls;

                                if(i < 5) plans.add(new LiquidUnitPlan(u, time * 6, os, cpl[i - 1]));
                                else plans.add(new LiquidUnitPlan(u, time * 2, is, cpl[i - 1]));
                            }
                        }
                    }
                }
                super.init();
            }

            final String[] nums = new String[]{"IV[]", "V[]", "VI[]"};
            String checkTier(UnitType u){
                Seq<Integer> tiers = utp.keys().toSeq();
                for(int i : tiers) {
                    Seq<UnitType> unitTypes = utp.get(i);
                    if(u == null || unitTypes.size <= 0) return "null";
                    if(unitTypes.contains(u)) {
                        return i == 1 ? "[accent]below Tier IV[]" : i == 5 ? "[accent]over Tier VI[]" : "[accent]Tier " + nums[i - 2];
                    }
                }
                return "";
            }

            @Override
            public void setStats() {
                super.setStats();
                stats.remove(Stat.input);
                stats.remove(Stat.output);
                this.stats.add(Stat.output, (table) -> {
                    table.row();

                    table.add("[red]Click on the icon to view").left().row();
                    for (UnitPlan plan : plans) {
                        if(plan.unit == null) continue;
                        table.table(Styles.grayPanel, (t) -> {
                            if (plan.unit.isBanned()) {
                                t.image(Icon.cancel).color(Pal.remove).size(40);
                            } else {
                                if (plan.unit.unlockedNow()) {
                                    t.add(EUGet.selfStyleImageButton(new TextureRegionDrawable(plan.unit.uiIcon), Styles.emptyi, () -> ui.content.show(plan.unit))).size(56).pad(10).left().scaling(Scaling.fit).tooltip(plan.unit.localizedName);
                                    //t.button(new TextureRegionDrawable(plan.unit.uiIcon), () -> ui.content.show(plan.unit)).size(70).pad(10).left().scaling(Scaling.fit);

                                    t.table((info) -> {
                                        info.add(plan.unit.localizedName + " " + checkTier(plan.unit)).left();
                                        info.row();
                                        info.add(Strings.autoFixed(plan.time / 60, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                                    }).left();
                                    t.table((req) -> {
                                        req.right();

                                        for (int i = 0; i < plan.requirements.length; ++i) {
                                            if (i % 6 == 0) {
                                                req.row();
                                            }

                                            ItemStack stack = plan.requirements[i];
                                            req.add(new ItemDisplay(stack.item, stack.amount, false)).pad(4);
                                        }
                                        req.row();
                                        if(plan instanceof LiquidUnitPlan){
                                            for (int i = 0; i < ((LiquidUnitPlan) plan).liquid.length; ++i) {
                                                if(i % 6 == 0){
                                                    req.row();
                                                }
                                                LiquidStack stack = ((LiquidUnitPlan) plan).liquid[i];
                                                Liquid liquid = stack.liquid;
                                                float amount = stack.amount * 60;
                                                req.add(new Stack() {{
                                                        add((new Image(liquid.uiIcon)).setScaling(Scaling.fit));
                                                        if (amount != 0) {
                                                            Table t = (new Table()).left().bottom();
                                                            t.add(Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
                                                            add(t);
                                                        }
                                                    }
                                                }).size(32.0F).padRight((float)(3 + (amount != 0 && Strings.autoFixed(amount, 2).length() > 2 ? 8 : 0))).pad(4);
                                            }
                                        }

                                    }).right().grow().pad(10);
                                } else {
                                    t.image(Icon.lock).color(Pal.darkerGray).size(40);
                                }

                            }
                        }).growX().pad(5);
                        table.row();
                    }
                });
            }

            @Override
            public void setBars() {
                super.setBars();
                removeBar("liquid");
            }
        };

        aparajito = new Aparajito("aparajito"){{
            requirements(Category.defense, with(Items.surgeAlloy, 5, Items.phaseFabric, 4, Items.carbide, 4));
            size = 1;
            health = 4200/4;
            armor = 25;
            healColor = Items.carbide.color;

            lightningChance = 0.1f;
            lightningDamage = 30;

            floating = true;
            placeableLiquid = true;
        }};
        aparajitoLarge = new Aparajito("aparajito-large"){{
            requirements(Category.defense, with(Items.surgeAlloy, 20, Items.phaseFabric, 16, Items.carbide, 16));
            size = 2;
            health = 4200;
            armor = 25;
            healColor = Items.carbide.color;

            lightningChance = 0.1f;
            lightningDamage = 30;

            floating = true;
            placeableLiquid = true;
        }};

        mendTurret = new MendTurret("heal"){{
            requirements(Category.effect, with(EUItems.lightninAlloy, 80, Items.silicon, 300, Items.graphite, 280, Items.thorium, 180));
            size = 3;
            range = 22 * 8;
            shootType = new HealCone(30, range - 8, false){{
                healAmount = 600;
                lifetime = 30;
                optimalLifeFract = 0.5f;
            }};

            angleBoost = 0.5f;
            shootSound = Sounds.none;
            loopSound = Sounds.spellLoop;
            consumePower(6);
            consumeItem(Items.phaseFabric).boost();
        }};
        coreKeeper = new CoreKeeper("core-keeper"){{
            requirements(Category.effect, with(EUItems.lightninAlloy, 50 + (hardMod ? 50 : 0), Items.silicon, 400, Items.thorium, 200));
            size = 3;
            health = 1080;
            range = 40 + (hardMod ? 16 : 0);
            consumePower(6);

            alwaysUnlocked = true;

            drawer = new DrawBlock() {
                @Override
                public void draw(Building build) {
                    float x = build.x, y = build.y;

                    Draw.color(build.team.color);
                    Draw.alpha(build.warmup());
                    Draw.z(Layer.effect);
                    for(int i = 0; i < 4; i++){
                        float angle = i * 90;
                        Drawf.tri(x + Angles.trnsx(angle + build.progress() * 2, size * Vars.tilesize/2f * build.warmup()), y + Angles.trnsy(angle + build.progress() * 2, size * Vars.tilesize/2f * build.warmup()), 6, -5, angle + build.progress() * 2);
                        Drawf.tri(x + Angles.trnsx(angle - build.progress() * 3, (size * Vars.tilesize/2f + 3) * build.warmup()), y + Angles.trnsy(angle - build.progress() * 3, (size * Vars.tilesize/2f + 3) * build.warmup()), 4, -3, angle - build.progress() * 3);
                    }

                    if(!(build instanceof CoreKeeperBuild)) return;
                    if(Mathf.equal(build.warmup(), 1, 0.01f)) {
                        Draw.color(build.team.color);
                        Fill.circle(x, y, size * 1.7f);

                        CoreKeeperBuild b = (CoreKeeperBuild) build;
                        float rot = (Time.time * 3) % 360;
                        Tmp.v1.trnsExact(rot, size * 2.7f);
                        float tx = x + Tmp.v1.x, ty = y + Tmp.v1.y/2.2f;
                        if(rot > 50 && rot < 230) Draw.z(Layer.effect - 0.01f);
                        else Draw.z(Layer.effect);
                        b.trail.draw(build.team.color.cpy().mul(EUItems.lightninAlloy.color), size/2f);
                        b.trail.update(tx, ty);
                        Draw.color(build.team.color.cpy().mul(EUItems.lightninAlloy.color));
                        Fill.circle(tx, ty, size / 2f);
                    }

                    Draw.reset();
                }
            };
        }};

        quantumDomain = new Domain("quantum-domain"){{
            requirements(Category.effect, with(EUItems.lightninAlloy, 300 + (hardMod ? 50 : 0), Items.silicon, 800, Items.surgeAlloy, 400, Items.phaseFabric, 350));
            size = 5;
            health = 5000;
            hasPower = true;
            hasItems = false;
            unitDamage = 16 + (hardMod ? 4 : 0);
            shieldHealth = 3500f - (hardMod ? 500 : 0);
            canOverdrive = false;
            placeableLiquid = true;
            consumePower(9);
            alwaysUnlocked = true;
        }};

        breaker = new Breaker("breaker"){{
            requirements(Category.effect, with(EUItems.lightninAlloy, 10 + (hardMod ? 5 : 0)));
            placeableLiquid = true;
            floating = true;

            alwaysUnlocked = true;
        }};
        waterBomb = new WaterBomb("water-bomb"){{
            requirements(Category.effect, with(EUItems.lightninAlloy, 20, Items.thorium, 30, Items.surgeAlloy, 20));
            //alwaysUnlocked = true;
        }};

        buffrerdMemoryBank = new CopyMemoryBlock("buffrerd-memory-bank"){{
            requirements(Category.logic, with(Items.graphite, 90, Items.silicon, 90, Items.phaseFabric, 30, Items.titanium, 40));
            memoryCapacity = 512;
            size = 2;
        }};
        //WIP...
        clock = new Clock("clock"){{
            requirements(Category.logic, with(Items.silicon, 10));
            health = 80;
            size = 1;
        }};
        tableClock = new GenericCrafter("clockBack"){{
            size = 4;
            health = 3000;
            requirements(Category.defense, with(Items.silicon, 100, Items.graphite, 100));
            drawer = new DrawBlock() {
                @Override
                public void draw(Building build) {
                    DrawFunc.drawClockTable(-12, build, 0, size * 8, 0, 0, Pal.lightishGray,
                            Core.atlas.find(name("clockBack")),
                            Core.atlas.find(name("clockSt1")),
                            Core.atlas.find(name("clockMt1")),
                            Core.atlas.find(name("clockHt1"))
                    );
                }

                @Override
                public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
                    Fill.rect(plan.drawx(), plan.drawy(), block.size * tilesize, block.size * tilesize);
                    Draw.rect(block.uiIcon, plan.drawx(), plan.drawy());
                }
            };
            buildVisibility = BuildVisibility.editorOnly;
        }};

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
            transportTime = 0.2f;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        ADC = new ADCPayloadSource("ADC"){{
            requirements(Category.units, with());
            size = 5;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
            placeableLiquid = true;
            floating = true;
        }};

        guiYsDomain = new Domain("guiYs-domain"){{
            requirements(Category.effect, with());
            size = 2;
            health = 6000;
            buildVisibility = BuildVisibility.sandboxOnly;
            shieldHealth = coolDown = coolDownBk = Float.MAX_VALUE;
            bulletAmount = 0;
            fullEffect = EUFx.shieldDefense;
            canBroken = false;
            range = 30 * 8;
            upSpeed = 5;
            healPercent = healPercentUnit= 25;
            reloadH = reloadHU = 30;
            healByPercent = true;
            hasPower = false;
        }};

        crystalTower = new CrystalTower("crystal-tower"){{
            requirements(Category.effect, with());
            buildVisibility = BuildVisibility.editorOnly;
            size = 3;
        }};

        fireWork = new fireWork("fireWork"){{
            requirements(Category.effect, with(Items.silicon, 10));
            size = 2;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
        }};

        EUGet.donorItems.addAll(largeElectricHeater, T2sporePress, javelin, waterBomb, buffrerdMemoryBank);
        EUGet.developerItems.addAll(siliconFurnace, guiY, onyxBlaster, fiammetta, guiYsDomain, allNode, ADC, randomer, fireWork, crystalTower);
    }

    //by guiY for Twilight Fall
//    public static Block ct = new PowerTurret("ct"){{
//        requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 60, Items.titanium, 30));
//        range = 25 * 8;
//        recoil = 2f;
//        reload = 80f;
//        shake = 2f;
//        shootEffect = none;
//        smokeEffect = none;
//        size = 2;
//        scaledHealth = 280;
//        targetAir = false;
//        moveWhileCharging = false;
//        accurateDelay = false;
//        shootSound = Sounds.laser;
//        coolant = consumeCoolant(0.2f);
//
//        consumePower(6f);
//
//        //上面炮塔数据随意
//        //下面子弹数据自己改
//        float cont = 60;//扩散角度，1/2值，60 = 120
//        float bRange = range;//范围
//        shootType = new BulletType(){{
//            damage = 100;
//            lifetime = 120;
//            speed = 0;
//            keepVelocity = false;
//            despawnEffect = hitEffect = none;
//
//            hittable = absorbable = reflectable = false;
//        }
//
//            @Override
//            public void update(Bullet b) {
//                //super.update(b);
//                Seq<Healthc> seq = new Seq<>();
//                float r = bRange * (1 - b.foutpow());
//                Vars.indexer.allBuildings(b.x, b.y, r, bd -> {
//                    if(bd.team != b.team && Angles.within(b.rotation(), b.angleTo(bd), cont)) seq.addUnique(bd);
//                });
//                Units.nearbyEnemies(b.team, b.x - r, b.y - r, r * 2, r * 2, u -> {
//                    if(u.type != null && u.type.targetable && b.within(u, r) && Angles.within(b.rotation(), b.angleTo(u), cont)) seq.addUnique(u);
//                });
//                for(int i = 0; i < seq.size; i++){
//                    Healthc hc = seq.get(i);
//                    if(hc != null && !hc.dead()) {
//                        if(!b.hasCollided(hc.id())) {
//                            //伤害的方式在这里改
//
//                            //普攻
//                            hc.damage(damage);
//
//                            //穿甲
//                            //hc.damagePierce(damage);
//
//                            //真伤
//                            //if(hc.health() <= damage) hc.kill();
//                            //else hc.health(hc.health() - damage);
//                            b.collided.add(hc.id());
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void draw(Bullet b) {
//                super.draw(b);
//                float pin = (1 - b.foutpow());
//                Lines.stroke(5 * pin, Pal.bulletYellowBack);
//
//                for(float i = b.rotation() - cont; i < b.rotation() + cont; i++){
//                    float lx = EUGet.dx(b.x, bRange * pin, i);
//                    float ly = EUGet.dy(b.y, bRange * pin, i);
//                    Lines.lineAngle(lx, ly, i - 90, bRange/(cont * 2) * pin);
//                    Lines.lineAngle(lx, ly, i + 90, bRange/(cont * 2) * pin);
//                }
//            }
//        };
//    }};
}
