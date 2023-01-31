package ExtraUtilities.content;

import ExtraUtilities.worlds.blocks.fireWork;
import ExtraUtilities.worlds.blocks.heat.*;
import ExtraUtilities.worlds.blocks.liquid.SortLiquidRouter;
import ExtraUtilities.worlds.blocks.power.LightenGenerator;
import ExtraUtilities.worlds.blocks.production.*;
//import ExtraUtilities.worlds.blocks.turret.MultiBulletTurret;
import ExtraUtilities.worlds.blocks.turret.MultiBulletTurret;
import ExtraUtilities.worlds.blocks.turret.TurretResupplyPoint;
import ExtraUtilities.worlds.blocks.turret.guiY;
import ExtraUtilities.worlds.drawer.*;
import ExtraUtilities.worlds.entity.bullet.CtrlMissile;
import ExtraUtilities.worlds.entity.bullet.FireWorkBullet;
import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
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
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mindustry.type.ItemStack.*;
import static ExtraUtilities.ExtraUtilitiesMod.*;
import static ExtraUtilities.worlds.entity.bullet.FireWorkBullet.*;

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
            guiY, onyxBlaster, celebration, celebrationMk2, turretResupplyPoint,
        //unit
            imaginaryReconstructor,
        //other&sandbox
            randomer, fireWork;
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
//                    Items.silicon, new MissileBulletType(0.5f, 0){
//                        {
//                            frontColor = backColor = Pal.gray;
//                            width = 10f;
//                            height = 12f;
//                            shrinkY = 0f;
//                            ammoMultiplier = 1f;
//                            hitSound = Sounds.none;
//                            shootEffect = despawnEffect = hitEffect = Fx.none;
//                            trailEffect = EUFx.missileTrailSmokeSmall;
//                            trailWidth = 2.2f;
//                            trailLength = 11;
//                            trailColor = Pal.lightTrail;
//                            lifetime = 20f;
//                            homingPower = 0;
//                            fragRandomSpread = 0;
//                            fragAngle = 0;
//                            fragBullets = 1;
//                            fragVelocityMin = 1f;
//                            fragBullet = new MissileBulletType(3.7f, 0) {{
//                                frontColor = backColor = Pal.gray;
//                                width = 10f;
//                                height = 12f;
//                                shrinkY = 0f;
//                                homingPower = 0.08f;
//                                homingRange = 26.25f * 8;
//                                splashDamageRadius = 32f;
//                                splashDamage = 88f;
//                                ammoMultiplier = 4f;
//                                hitEffect = Fx.blastExplosion;
//                                trailWidth = 2.2f;
//                                trailLength = 11;
//                                trailColor = Pal.lightTrail;
//                                buildingDamageMultiplier = 0.3f;
//                            }};
//                        }}
                    Items.silicon, new CtrlMissile("quell-missile", -1, -1){{
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootBigSmoke2;
                        speed = 4.3f;
                        keepVelocity = false;
                        maxRange = 6f;
                        lifetime = 60f;
                        damage = 120;
                        splashDamage = 160;
                        splashDamageRadius = 32;
                        buildingDamageMultiplier = 0.8f;
                        hitEffect = despawnEffect = Fx.massiveExplosion;
                        trailColor = Pal.sapBulletBack;
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
//        onyxBlaster = new MultiBulletTurret("onyx-blaster"){{
//            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
//            size = 4;
//            //ammo(Items.surgeAlloy, );
//        }};
        celebration = new MultiBulletTurret("celebration"){{
            requirements(Category.turret, with(Items.silicon, 120, Items.titanium, 125, Items.thorium, 70, EUItems.crispSteel, 60));
            drawer = new DrawTurret("reinforced-");
            shoot = new ShootSpread(2, 4);
            inaccuracy = 3;
            scaledHealth = 150;
            size = 3;
            range = 27f * 8;
            shake = 2f;
            recoil = 1f;
            reload = 60f;
            shootY = 12f;
            rotateSpeed = 3.1f;
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
            ammo(Items.blastCompound, bullets1, Items.plastanium, bullets2);
        }};

        celebrationMk2 = new MultiBulletTurret("celebration-mk2"){{
            size = 5;
            drawer = new DrawMulti(new DrawTurret("reinforced-"), new DrawMk2());
            requirements(Category.turret, with(Items.silicon, 410, Items.graphite, 330, Items.thorium, 280, EUItems.lightninAlloy, 200));
            inaccuracy = 3;
            shootEffect = EUFx.Mk2Shoot(90);
            smokeEffect = Fx.none;
            scaledHealth = 150;
            range = 32 * 8;
            shake = 2f;
            recoil = 1.5f;
            reload = 10;
            shootY = 20;
            rotateSpeed = 2f;
            coolant = consumeCoolant(0.8f);
            coolantMultiplier = 1.5f;
            shootSound = Sounds.missile;
            canOverdrive = false;

            //红
            BulletType f1 = new FireWorkBullet(120, 5, name("mb-mk2"), Color.valueOf("FF1A44"), 6 * 8){{
                outline = true;
                trailInterval = 0;
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
            BulletType ff5 = new FireWorkBullet(110, 6, name("mb-mk2"), Color.valueOf("2BBCFF"), 8 * 8){{
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
                fire = new colorFire(true, 4, 60);
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
                }};
            }};

            BulletType[] bullets = new BulletType[]{f1, f2, f3, f4, f5, f6, f7};
            ammo(Items.thorium, bullets);
        }};

        turretResupplyPoint = new TurretResupplyPoint("turret-resupply-point"){{
            requirements(Category.turret, with(Items.graphite, 90, Items.silicon, 180, Items.thorium, 70));
            size = 2;
            hasPower = true;
            consumePower(1);
        }};

        imaginaryReconstructor = new Reconstructor("imaginary-reconstructor"){{
            requirements(Category.units, with(Items.silicon, 6000, Items.graphite, 3500, Items.titanium, 1000, Items.thorium, 800, Items.plastanium, 600, Items.phaseFabric, 350, EUItems.lightninAlloy, 200));
            size = 11;
            upgrades.addAll(
                    new UnitType[]{UnitTypes.reign, EUUnitTypes.suzerain},
                    new UnitType[]{UnitTypes.toxopid, EUUnitTypes.asphyxia}
            );
            researchCostMultiplier = 0.4f;
            buildCostMultiplier = 0.7f;
            constructTime = 60 * 60 * 4.2f;

            consumePower(3f);
            consumeItems(with(Items.silicon, 900, Items.titanium, 750, Items.plastanium, 450, Items.phaseFabric, 250, EUItems.lightninAlloy, 210));
            consumeLiquid(Liquids.cryofluid, 3.2f);
        }};

        randomer = new Randomer("randomer"){{
            requirements(Category.distribution, with(Items.silicon, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};

        fireWork = new fireWork("fireWork"){{
            requirements(Category.effect, with(Items.silicon, 10));
            size = 2;
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.editorOnly;
        }};
        //override
    }
}
