package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.liquid.SortLiquidRouter;
import ExtraUtilities.worlds.blocks.power.LightenGenerator;
import ExtraUtilities.worlds.blocks.production.*;
//import ExtraUtilities.worlds.blocks.turret.MultiBulletTurret;
import ExtraUtilities.worlds.blocks.turret.TurretResupplyPoint;
import ExtraUtilities.worlds.blocks.turret.guiY;
import ExtraUtilities.worlds.drawer.*;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.*;
import static ExtraUtilities.ExtraUtilitiesMod.*;

public class EUBlocks {
    public static Block
        //drill?
            arkyciteExtractor, quantumExplosion, minerPoint, minerCenter,
        //liquid
            liquidSorter, liquidValve, liquidIncinerator,
        //transport
            ekMessDriver,
        //production
            T2oxide,
        /** 光束合金到此一游*/
            LA, ELA,
        //heat
            thermalHeater, heatTransfer, heatDriver,
        //power
            LG,
        //turret
            guiY, onyxBlaster, turretResupplyPoint,
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
        quantumExplosion = new ExplodeDrill("quantum-explosion"){{
            requirements(Category.production, with(Items.thorium, 999, Items.silicon, 300, Items.phaseFabric, 100, Items.surgeAlloy, 200));
            drillTime = 60f * 3f;
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

            alwaysUnlocked = true;
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
        liquidIncinerator = new LiquidIncinerator("liquid-incinerator"){{
            requirements(Category.crafting, with(Items.silicon, 16, Items.carbide, 6));
            consumePower(0.9f);
            hasLiquids = true;
            size = 1;
        }};


        ekMessDriver = new MassDriver("Ek-md"){{
            requirements(Category.distribution, with(Items.silicon, 75, Items.tungsten, 100, Items.thorium, 55, Items.carbide, 45));
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
            craftTime = 60f * 5f;
            liquidCapacity = 50f;
            itemCapacity = 80;
            heatOutput = 25f;
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


        guiY = new ItemTurret("guiY"){{
            requirements(Category.turret, with(Items.beryllium, 65, Items.graphite, 90, Items.silicon, 66));
            size = 2;
            ammo(
                    Items.silicon, new MissileBulletType(0.5f, 0){
                        {
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
                            fragBullet = new MissileBulletType(3.7f, 50) {{
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
//        onyxBlaster = new MultiBulletTurret("onyx-blaster"){{
//            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
//            size = 4;
//            //ammo(Items.surgeAlloy, );
//        }};
        turretResupplyPoint = new TurretResupplyPoint("turret-resupply-point"){{
            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
            size = 2;
            hasPower = true;
            consumePower(1);
        }};

        randomer = new Randomer("randomer"){{
            requirements(Category.distribution, with(Items.silicon, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
    }
}
