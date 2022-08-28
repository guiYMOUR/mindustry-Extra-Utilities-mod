package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.liquid.SortLiquidRouter;
import ExtraUtilities.worlds.blocks.production.*;
import ExtraUtilities.worlds.blocks.turret.TurretResupplyPoint;
import ExtraUtilities.worlds.drawer.*;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;

public class EUBlocks {
    public static Block
        //drill?
            arkyciteExtractor, minerPoint, minerCenter,
        //liquid
            liquidSorter, liquidValve,
        //production
            T2oxide,
        //heat
            thermalHeater, heatTransfer, heatDriver,
        //turret
            guiY, turretResupplyPoint,
        //other&sandbox
            randomer;
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
        minerPoint = new MinerPoint("miner-point"){{
            requirements(Category.production, with(Items.beryllium, 120, Items.graphite, 120, Items.silicon, 85, Items.tungsten, 50));
            consumePower(2);
            consumeLiquid(Liquids.ozone, 6/60f);

            blockedItem = Items.titanium;
            droneConstructTime = 60 * 10f;
            tier = 3;
            //alwaysUnlocked = true;
        }};
        minerCenter = new MinerPoint("miner-center"){{
            requirements(Category.production, with(Items.tungsten, 360, Items.oxide, 125, Items.carbide, 120, Items.surgeAlloy, 130));
            consumePower(3);
            consumeLiquid(Liquids.cyanogen, 6/60f);

            range = 18;
            alwaysCons = true;
            blockedItem = Items.thorium;
            dronesCreated = 6;
            droneConstructTime = 60 * 7f;
            tier = 5;
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

        T2oxide = new HeatProducer("T2oxide"){{
            requirements(Category.crafting, with(Items.oxide, 50, Items.graphite, 300, Items.silicon, 300, Items.carbide, 110, Items.thorium, 100));
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

        guiY = new ItemTurret("guiY"){{
            requirements(Category.turret, with(Items.beryllium, 65, Items.graphite, 90, Items.silicon, 66));
            size = 2;
            ammo(
                    Items.silicon, new MissileBulletType(0.5f, 0){{
                        frontColor = backColor = Pal.gray;
                        width = 10f;
                        height = 12f;
                        shrinkY = 0f;
                        ammoMultiplier = 1f;
                        hitSound = Sounds.none;
                        shootEffect = despawnEffect = hitEffect = Fx.none;
                        trailEffect = EUFx.missileTrailSmokeSmall;
                        trailWidth = 2.2f;
                        trailLength = 11;
                        trailColor = Pal.lightTrail;
                        lifetime = 20f;
                        homingPower = 0;
                        fragRandomSpread = 0;
                        fragAngle = 0;
                        fragBullets = 1;
                        fragVelocityMin = 1f;
                        fragBullet = new MissileBulletType(3.7f, 50){{
                                frontColor = backColor = Pal.gray;
                                width = 10f;
                                height = 12f;
                                shrinkY = 0f;
                                homingPower = 0.08f;
                                homingRange = 26.25f * 8;
                                splashDamageRadius = 28f;
                                splashDamage = 66f;
                                ammoMultiplier = 4f;
                                hitEffect = Fx.blastExplosion;
                                trailWidth = 2.2f;
                                trailLength = 11;
                                trailColor = Pal.lightTrail;
                                buildingDamageMultiplier = 0.3f;
                            }};
                    }}
            );
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart(){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup;
                            heatColor = Color.red;
                            moveRot = -22f;
                            moveX = 0f;
                            moveY = -0.8f;
                            mirror = true;
                        }},
                        new RegionPart("-mid"){{
                            progress = PartProgress.recoil;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
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
        turretResupplyPoint = new TurretResupplyPoint("turret-resupply-point"){{
            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
            size = 2;
            hasPower = true;
            consumePower(1);
        }};

        randomer = new Randomer("randomer"){{
            requirements(Category.distribution, with(Items.copper, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
    }
}
