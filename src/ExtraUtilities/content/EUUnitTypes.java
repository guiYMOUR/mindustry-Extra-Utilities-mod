package ExtraUtilities.content;

import ExtraUtilities.ai.DefenderHealAI;
import ExtraUtilities.ai.MinerPointAI;
import ExtraUtilities.worlds.drawer.*;
import ExtraUtilities.worlds.entity.ability.*;
import ExtraUtilities.worlds.entity.bullet.*;
import ExtraUtilities.worlds.entity.weapon.*;
import ExtraUtilities.worlds.entity.weapon.mounts.reRotMount;
import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.FlyingFollowAI;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootHelix;
import mindustry.entities.pattern.ShootSpread;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.unit.ErekirUnitType;
import mindustry.type.unit.TankUnitType;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.meta.Env;

import static ExtraUtilities.ExtraUtilitiesMod.hardMod;
import static ExtraUtilities.ExtraUtilitiesMod.name;
import static arc.graphics.g2d.Draw.color;
import static mindustry.content.Fx.rand;

public class EUUnitTypes {
    static {
        //one day, someone asks me : why not use xxxUnit::new? ha, I say : I don't know...
        EntityMapping.nameMap.put(name("miner"), EntityMapping.idMap[36]);
        EntityMapping.nameMap.put(name("T2miner"), EntityMapping.idMap[36]);

        EntityMapping.nameMap.put(name("suzerain"), EntityMapping.idMap[4]);
        EntityMapping.nameMap.put(name("nebula"), EntityMapping.idMap[24]);
        EntityMapping.nameMap.put(name("asphyxia"), EntityMapping.idMap[33]);
        EntityMapping.nameMap.put(name("apocalypse"), EntityMapping.idMap[3]);
        EntityMapping.nameMap.put(name("Tera"), EntityMapping.idMap[5]);
        EntityMapping.nameMap.put(name("nihilo"), EntityMapping.idMap[20]);
        EntityMapping.nameMap.put(name("narwhal"), EntityMapping.idMap[20]);

        EntityMapping.nameMap.put(name("napoleon"), EntityMapping.idMap[43]);
        EntityMapping.nameMap.put(name("havoc"), EntityMapping.idMap[5]);
        EntityMapping.nameMap.put(name("arcana"), EntityMapping.idMap[24]);


        EntityMapping.nameMap.put(name("winglet"), EntityMapping.idMap[3]);
    }

    public static UnitType
        miner, T2miner,
        //T6
        suzerain, nebula, asphyxia, apocalypse, Tera, nihilo, narwhal,
        //E-T6
        napoleon, havoc, arcana,
        //air sapper
        winglet;
    public static void load(){
        miner = new ErekirUnitType("miner"){{
            defaultCommand = UnitCommand.mineCommand;
            controller = u -> new MinerPointAI();

            flying = true;
            drag = 0.06f;
            accel = 0.12f;
            speed = 1.5f;
            health = 100;
            engineSize = 1.8f;
            engineOffset = 5.7f;
            range = 50f;
            hitSize = 12f;
            itemCapacity = 20;
            isEnemy = false;
            payloadCapacity = 0;

            mineTier = 10;//据点决定tier
            mineSpeed = 1.6f;
            mineWalls = true;
            mineFloor = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
            hidden = true;
            targetable = false;
            hittable = false;
            targetPriority = -2;

            //alwaysUnlocked = true;

            setEnginesMirror(
                    new UnitEngine(24 / 4f, -24 / 4f, 2.3f, 315f)
            );
        }};

        T2miner = new ErekirUnitType("T2miner"){{
            defaultCommand = UnitCommand.mineCommand;
            controller = u -> new MinerPointAI();

            flying = true;
            drag = 0.06f;
            accel = 0.12f;
            speed = 1.5f;
            health = 100;
            engineSize = 2.6f;
            engineOffset = 9.8f;
            range = 50f;
            mineRange = 100f;
            hitSize = 16f;
            itemCapacity = 50;
            isEnemy = false;
            payloadCapacity = 0;

            mineTier = 10;
            mineSpeed = 3.2f;
            mineWalls = true;
            mineFloor = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
            hidden = true;
            targetable = false;
            hittable = false;
            targetPriority = -2;

            //alwaysUnlocked = true;

            setEnginesMirror(
                    new UnitEngine(40 / 4f, -40 / 4f, 3f, 315f)
            );
        }};

        //T6
        suzerain = new UnitType("suzerain"){{
            armor = 40;
            speed = 0.3f;
            canBoost = true;
            engineOffset = 20;
            engineSize = 10;
            hitSize = 40;
            rotateSpeed = 1.8f;
            canDrown = false;
            mechStepParticles = true;
            stepShake = 1;
            mechFrontSway = 1.9f;
            mechSideSway = 0.6f;
            singleTarget = true;
            health = 63000 - (hardMod ? 3000 : 0);
            itemCapacity = 240;
            ammoType = new ItemAmmoType(Items.thorium);

            immunities = ObjectSet.with(EUStatusEffects.speedDown, EUStatusEffects.poison, StatusEffects.sapped);
            immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> !immunities.contains(s) && s.reloadMultiplier >= 1));

            abilities.add(new TerritoryFieldAbility(20 * 8, 90, 210){{
                open = true;
            }}, new ShieldRegenFieldAbility(100, 1000, 60 * 4, 20 * 8), new preventCheatingAbility());
            weapons.add(
                    new Weapon(name("suzerain-weapon")){{
                        shake = 5;
                        shootY = 13;
                        shoot.shots = 5;
                        inaccuracy = 1;
                        shoot.shotDelay = 3;
                        ejectEffect = Fx.casing4;
                        //bullet = ((ItemTurret)Blocks.spectre).ammoTypes.get(Items.thorium);
                        bullet = new BasicBulletType(13f, 80){{
                            pierce = true;
                            pierceCap = 10;
                            width = 14f;
                            height = 33f;
                            lifetime = 24f;
                            shootEffect = Fx.shootBig;
                            fragVelocityMin = 0.4f;

                            hitEffect = Fx.blastExplosion;
                            splashDamage = 18f;
                            splashDamageRadius = 13f;

                            fragBullets = 3;
                            fragLifeMin = 0f;
                            fragRandomSpread = 30f;

                            fragBullet = new BasicBulletType(9f, 21){{
                                width = 10f;
                                height = 10f;
                                pierce = true;
                                pierceBuilding = true;
                                pierceCap = 3;

                                lifetime = 20f;
                                hitEffect = Fx.flakExplosion;
                                splashDamage = 15f;
                                splashDamageRadius = 10f;
                            }};
                        }};
                        x = 25.5f;
                        y = 0;
                        shootCone = 80f;
                        shootSound = Sounds.bang;
                        reload = 24 + (hardMod ? 6 : 0);
                        recoil = 5;

                        rotate = true;
                        rotateSpeed = 0.6f;
                        rotationLimit = 20f;
                    }},
                    new Weapon(name("suzerain-weapon2")){{
                        shake = 4;
                        shootY = 7;
                        shoot = new ShootSpread(2, 10f);
                        bullet = new FlakBulletType(8f, 45f - (hardMod ? 10 : 0)){{
                            sprite = "missile-large";

                            lifetime = 40f;
                            width = 12f;
                            height = 22f;

                            hitSize = 7f;
                            shootEffect = Fx.shootSmokeSquareBig;
                            smokeEffect = Fx.shootSmokeDisperse;
                            ammoMultiplier = 1;
                            hitColor = backColor = trailColor = lightningColor = Pal.surge;
                            frontColor = Color.white;
                            trailWidth = 3f;
                            trailLength = 12;
                            hitEffect = despawnEffect = Fx.hitBulletColor;
                            buildingDamageMultiplier = 0.5f;

                            trailEffect = Fx.colorSpark;
                            trailRotation = true;
                            trailInterval = 3f;
                            lightning = 1;
                            lightningCone = 15f;
                            lightningLength = 20;
                            lightningLengthRand = 30;
                            lightningDamage = 20f;

                            homingPower = 0.17f;
                            homingDelay = 10f;
                            homingRange = 160f;

                            explodeRange = 0f;
                            explodeDelay = 0f;

                            flakInterval = 20f;
                            despawnShake = 3f;

                            fragBullet = new LaserBulletType(45f - (hardMod ? 10 : 0)){{
                                colors = new Color[]{Pal.surge.cpy().a(0.4f), Pal.surge, Color.white};
                                buildingDamageMultiplier = 0.4f;
                                width = 19f;
                                hitEffect = Fx.hitLancer;
                                sideAngle = 175f;
                                sideWidth = 1f;
                                sideLength = 40f;
                                lifetime = 22f;
                                drawSize = 400f;
                                length = 180f;
                                pierceCap = 2;
                            }};

                            fragSpread = fragRandomSpread = 0f;

                            splashDamage = 0f;
                            hitEffect = Fx.hitSquaresColor;
                            collidesGround = true;
                        }};
                        rotate = true;
                        rotateSpeed = 2;
                        x = 12;
                        y = 2;
                        shootSound = Sounds.railgun;
                        reload = 27;
                        recoil = 4;
                    }}
            );
        }};
        
        nebula = new UnitType("nebula"){{
            armor = 32;
            flying = false;
            speed = 0.25f;
            hitSize = 41;
            rotateSpeed = 1.8f;
            health = 59000;
            mineSpeed = 7;
            mineTier = 3;
            buildSpeed = 3;
            itemCapacity = 300;
            drawShields = false;
            legCount = 6;
            legLength = 24;
            legBaseOffset = 3;
            legMoveSpace = 1.5f;
            legForwardScl = 0.58f;
            stepShake = 1.8f;
            hovering = true;
            allowLegStep = true;
            groundLayer = Layer.legUnit;
            ammoType = new PowerAmmoType(3800);

            immunities = ObjectSet.with(EUStatusEffects.speedDown, EUStatusEffects.poison, StatusEffects.sapped, StatusEffects.wet, StatusEffects.electrified);

            abilities.add(new EnergyFieldAbility(60f, 90f, 200f){{
                maxTargets = 25;
                healPercent = 10f;
                hitUnits = false;
                y = - 20f;
            }});

            BulletType r = new RailBulletType(){{
                shootEffect = new Effect(24, e ->{
                    e.scaled(10, b -> {
                        Draw.color(Color.white, Pal.heal, b.fin());
                        Lines.stroke(b.fout() * 3 + 0.2f);
                        Lines.circle(b.x, b.y, b.fin() * 50);
                    });
                    Draw.color(Pal.heal);
                    for(int i : Mathf.signs){
                        Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + 90 * i);
                    }
                });
                length = 500;
                pointEffectSpace = 30;
                pierceEffect = Fx.railHit;
                pointEffect = new Effect(16, e -> {
                    Draw.color(Pal.heal);
                    for(int i : Mathf.signs){
                        Drawf.tri(e.x, e.y, 22 * e.fout(), 25, e.rotation + 90 + 90 * i);
                    }
                });
                hitEffect = Fx.massiveExplosion;
                smokeEffect = Fx.shootBig2;
                damage = 1550;
            }};

            weapons.add(
                    new Weapon(name("nebula-weapon")){{
                        shake = 4;
                        shootY = 13;
                        reload = 180;
                        cooldownTime = 90;
                        bullet = r;
                        mirror = false;
                        top = false;
                        x = 0;
                        y = 0;
                        shootSound = Sounds.railgun;
                        recoil = 0;
                    }}
            );
            for(float wx : new Float[]{-20f, 20f}) {
                weapons.add(
                        new ReRotPointDefenseWeapon(name("nebula-defense")){{
                            top = false;
                            x = wx;
                            y = -18;
                            reload = 8;
                            targetInterval = 8;
                            targetSwitchInterval = 8;
                            mirror = false;
                            shootSound = Sounds.lasershoot;
                            bullet = new BulletType(){{
                                shootEffect = Fx.sparkShoot;
                                hitEffect = Fx.pointHit;
                                maxRange = 320;
                                damage = 40;
                            }};
                        }}
                );
            }
            for(float wx : new Float[]{-10.5f, 10.5f}) {
                weapons.add(
                        new ReRotRepairBeamWeapon(name("nebula-defense")){{
                            top = false;
                            x = wx;
                            y = -4.5f;
                            beamWidth = 0.8f;
                            repairSpeed = 2.5f;
                            mirror = false;
                            bullet = new BulletType(){{
                                drag = 1;
                                maxRange = 200;
                            }};
                        }}
                );
            }
        }};

        asphyxia = new UnitType("asphyxia"){{
            armor = 38;
            flying = false;
            speed = 0.4f;
            hitSize = 33;
            lightRadius = 160;
            rotateSpeed = 1.8f;
            stepShake = 1.5f;
            health = 54000;
            buildSpeed = 1.5f;
            itemCapacity = 180;

            legCount = 8;
            legLength = 80;
            legBaseOffset = 8;
            legMoveSpace = 0.8f;
            legPairOffset = 3;
            legExtension = -22;
            legLengthScl = 0.93f;
            rippleScale = 3.4f;
            legSpeed = 0.18f;

            canDrown = false;
            hovering = true;
            allowLegStep = true;
            groundLayer = Layer.legUnit;
            ammoType = new PowerAmmoType(3000);
            legSplashDamage = 100;
            legSplashRange = 64;

            immunities = ObjectSet.with(StatusEffects.wet, StatusEffects.sapped);
            
            BulletType sapper = new SapBulletType(){{
                sapStrength = 1.5f;
                length = 96;
                damage = 50;
                shootEffect = Fx.shootSmall;
                hitColor = Color.valueOf("bf92f9");
                color = Color.valueOf("bf92f9");
                despawnEffect = Fx.none;
                width = 0.55f;
                lifetime = 30;
                knockback = -1;
            }};
            
            BulletType asl = new liContinuousLaserBullet(){{
                damage = 96 - (hardMod ? 10 : 0);
                length = 240;
                width = 7;
                hitEffect = Fx.sapExplosion;
                drawSize = 420;
                lifetime = 160;
                despawnEffect = Fx.smokeCloud;
                smokeEffect = Fx.none;
                chargeEffect = new Effect(40, 100, e -> {
                        Draw.color(Pal.sapBullet);
                    Lines.stroke(e.fin() * 2);
                    Lines.circle(e.x, e.y, e.fout() * 50);
                });
                status = StatusEffects.wet;
                incendChance = 0.075f;
                incendSpread = 5;
                incendAmount = 1;
                colors = new Color[]{Pal.sapBullet, Pal.sapBullet, Pal.sapBulletBack};
                chain = new ChainLightningFade(lifetime, -1, 2.5f, Pal.sapBullet, damage/3, hitEffect);
                lTime = 8;
            }};

            SCSBullet lineBullet = new SCSBullet(){{
                damage = 150 - (hardMod ? 30 : 0);
            }};

            weapons.add(
                    new Weapon(name("asphyxia-main")){{
                        top = false;
                        shake = 4;
                        shootY = 13;
                        reload = 150;
                        cooldownTime = 200;
                        shootSound = Sounds.beam;
                        bullet = asl;
                        mirror = false;
                        continuous = true;
                        rotate = true;
                        rotateSpeed = 3;
                        rotationLimit = 70f;

                        x = 0;
                        y = -12;
                        chargeSound = Sounds.lasercharge2;
                        shoot.firstShotDelay = 49;
                        recoil = 3;
                        parentizeEffects = true;
                    }},

                    new Weapon(name("asphyxia-l")){{
                        top = false;
                        x = 14;
                        y = -5;
                        reload = 60;
                        rotate = true;
                        bullet = lineBullet;
                        shootSound = Sounds.bang;
                    }

                        @Override
                        public void addStats(UnitType u, Table t) {
                            super.addStats(u, t);
                            t.row();
                            t.add(Core.bundle.format("unit.extra-utilities-asphyxia.weaponStat1", lineBullet.maxFind));
                        }
                    },
                    
                    new Weapon(name("asphyxia-f")){{
                        top = false;
                        x = 9;
                        y = 8;
                        reload = 9;
                        rotate = true;
                        targetInterval = targetSwitchInterval = 12;

                        autoTarget = true;
                        controllable = false;
                        mountType = reRotMount::new;

                        bullet = sapper;
                        shootSound = Sounds.sap;
                    }
                        @Override
                        public void update(Unit unit, WeaponMount m) {
                            super.update(unit, m);
                            float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                                    mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
                            reRotMount mount = (reRotMount) m;
                            if(mount.target != null) {
                                mount.reRotate = 180;
                            } else {
                                mount.reRotate = Math.max(mount.reRotate - Time.delta, 0f);
                            }

                            if(mount.target == null && !mount.shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && mount.reRotate <= 0){
                                mount.rotate = true;
                                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                                mount.aimX = mountX + Tmp.v1.x;
                                mount.aimY = mountY + Tmp.v1.y;
                            }
                        }
                    },
                    new Weapon(name("asphyxia-f")){{
                        top = false;
                        x = 14;
                        y = 6;
                        reload = 9;
                        rotate = true;
                        targetInterval = targetSwitchInterval = 12;

                        autoTarget = true;
                        controllable = false;
                        mountType = reRotMount::new;

                        bullet = sapper;
                        shootSound = Sounds.sap;
                    }
                        @Override
                        public void update(Unit unit, WeaponMount m) {
                            super.update(unit, m);
                            float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                                    mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
                            reRotMount mount = (reRotMount) m;
                            if(mount.target != null) {
                                mount.reRotate = 180;
                            } else {
                                mount.reRotate = Math.max(mount.reRotate - Time.delta, 0f);
                            }

                            if(mount.target == null && !mount.shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && mount.reRotate <= 0){
                                mount.rotate = true;
                                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                                mount.aimX = mountX + Tmp.v1.x;
                                mount.aimY = mountY + Tmp.v1.y;
                            }
                        }
                    }
            );
        }};
        apocalypse = new UnitType("apocalypse"){{
            armor = 45;
            flying = true;
            speed = 0.51f;
            hitSize = 62;
            accel = 0.04f;
            rotateSpeed = 1;
            baseRotateSpeed = 20;
            drag = 0.018f;
            health = 60000;
            lowAltitude = true;
            itemCapacity = 320;
            engineOffset = 41;
            engineSize = 11;
            targetFlags = UnitTypes.eclipse.targetFlags;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting, StatusEffects.wet);
            ammoType = new ItemAmmoType(Items.pyratite);

            abilities.add(new UnitSpawnAbility(UnitTypes.crawler, 60*10, 17, -27.5f), new UnitSpawnAbility(UnitTypes.crawler, 60*10, -17, -27.5f));
            abilities.add(new EnergyFieldAbility(180f, 90f, 192f){{
                color = Color.valueOf("FFA665");
                status = StatusEffects.unmoving;
                statusDuration = 60f;
                maxTargets = 30;
                healPercent = 0.8f;
            }});

            PercentDamage bubble = new PercentDamage(){{
                damage = 13;
                status = StatusEffects.sapped;
            }};

            weapons.add(
                    new Weapon(name("apocalypse-m1")){{
                        top = false;
                        shake = 3;
                        shootY = 2;
                        bullet = new MissileBulletType(3.7f, 36){{
                            width = 8f;
                            height = 8f;
                            shrinkY = 0f;
                            splashDamageRadius = 30f;
                            splashDamage = 72;
                            hitEffect = Fx.blastExplosion;
                            despawnEffect = Fx.blastExplosion;
                            status = StatusEffects.blasted;
                            statusDuration = 60f;
                        }};
                        rotate = true;
                        rotateSpeed = 4;
                        x = 35;
                        y = 23;
                        shoot.shotDelay = 3;
                        xRand = 2;
                        shoot.shots = 3;
                        inaccuracy = 3;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.missile;
                        reload = 30;
                        recoil = 2;
                    }},
                    new Weapon(name("apocalypse-m1")){{
                        top = false;
                        shake = 3;
                        shootY = 2;
                        bullet = new MissileBulletType(3.7f, 36){{
                            width = 8f;
                            height = 8f;
                            shrinkY = 0f;
                            splashDamageRadius = 30f;
                            splashDamage = 72;
                            hitEffect = Fx.blastExplosion;
                            despawnEffect = Fx.blastExplosion;
                            status = StatusEffects.blasted;
                            statusDuration = 60f;
                        }};
                        rotate = true;
                        rotateSpeed = 4;
                        x = 30;
                        y = -27;
                        shoot.shotDelay = 3;
                        xRand = 2;
                        shoot.shots = 3;
                        inaccuracy = 3;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.missile;
                        reload = 30;
                        recoil = 2;
                    }},
                    new Weapon(name("apocalypse-bubble")){{
                        top = false;
                        shootY = 6;
                        bullet = bubble;
                        rotate = true;
                        rotateSpeed = 8;
                        x = 14;
                        y = 29;
                        shoot.shotDelay = 3;
                        xRand = 2;
                        shoot.shots = 5;
                        inaccuracy = 1;
                        ejectEffect = Fx.none;
                        reload = 6;
                        recoil = 1;
                    }

                        @Override
                        public void addStats(UnitType u, Table t) {
                            super.addStats(u, t);
                            t.row();
                            t.add(Core.bundle.format("stat." + name("percentDamage"), bubble.percent * 100));
                        }
                    },
                    new Weapon(name("apocalypse-weapon")){{
                        top = false;
                        shake = 4;
                        shootY = 16;
                        reload = 90;
                        bullet = new RailBulletType(){{
                            shootEffect = Fx.railShoot;
                            length = 400;
                            pierceEffect = Fx.railHit;
                            hitEffect = Fx.massiveExplosion;
                            smokeEffect = Fx.shootBig2;
                            damage = 986;
                            pierceDamageFactor = 0.5f;
                            pointEffectSpace = 60f;
                            pointEffect = Fx.railTrail;
                        }};
                        rotate = true;
                        rotateSpeed = 2;
                        shootSound = Sounds.railgun;
                        x = 28;
                        y = -3;
                    }}
            );
        }};

        Tera = new UnitType("Tera"){{
            armor = 41;
            flying = true;
            speed = 0.7f;
            hitSize = 66;
            accel = 0.04f;
            rotateSpeed = 1;
            drag = 0.018f;
            health = 61000;
            mineSpeed = 7;
            mineTier = 10;
            buildSpeed = 8;
            itemCapacity = 600;
            engineOffset = 28.4f;
            engineSize = 9;
            drawShields = false;
            lowAltitude = true;
            payloadCapacity = Mathf.sqr(7.5f) * Vars.tilePayload;
            ammoType = new PowerAmmoType(2500);

            immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s == StatusEffects.none || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1));

            abilities.add(
                    new DeathBullet(new diffBullet(360, 1){{
                        damage = splashDamage = 2500;
                        splashDamageRadius = 30 * 8;
                        lifetime = 240;
                        color = Pal.heal;
                        pfin = false;
                    }

                        @Override
                        public void draw(Bullet b) {
                            super.draw(b);
                            float pin = (1 - b.foutpow());
                            rand.setSeed(b.id);
                            for(int i = 0; i < 5; i++){
                                float a = rand.random(180);
                                float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                Draw.color(Pal.heal);
                                Drawf.tri(lx, ly, 25 * b.foutpow(), (150 + rand.random(-40, 40)) * b.foutpow(), a + 180);
                            }
                            for(int i = 0; i < 5; i++){
                                float a = 180 + rand.random(180);
                                float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                Draw.color(Pal.heal);
                                Drawf.tri(lx, ly, 25 * b.foutpow(), (150 + rand.random(-40, 40)) * b.foutpow(), a + 180);
                            }

                            if(b.timer.get(3, lifetime/10)) Effect.shake(5, 5, b);
                        }
                    }, null)
            );

            float spawnTime = 30 * 60;
            abilities.add(
                    new UnitSpawnAbility(UnitTypes.poly, spawnTime, 30, -27.5f),
                    new UnitSpawnAbility(UnitTypes.poly, spawnTime, -30, -27.5f)
            );

            float rd = 22 * 8f;
            abilities.add(
                    new RepairField(){{
                        range = rd;
                        blockHealPercent = 10;
                        status = EUStatusEffects.regenBoost;
                        unitHeal = 100;
                    }

                        @Override
                        public void death(Unit unit) {
                            Units.nearby(unit.team, unit.x, unit.y, range, u -> {
                                u.apply(StatusEffects.shielded, 8 * 60);
                                u.apply(EUStatusEffects.speedUp, 8 * 60);
                            });
                        }
                    },
                    new PcShieldArcAbility(){{
                        width = 18;
                        radius = rd - width + 8;
                        regen = 4;
                        max = 9000;
                        cooldown = 8 * 60;
                        whenShooting = false;
                        angle = 300;
                    }}
            );

            BulletType l = new liLaserBullet(){{
                damage = 200;
                healPercent = 12;
                collidesTeam = true;
                colors = new Color[]{Pal.heal.cpy().a(0.4f), Pal.heal.cpy().a(0.7f), Pal.heal, Color.white};
                color = colors[2];
                length = 21.5f * 8;
                width = 40;
                largeHit = true;
                sideAngle = 90;
                sideLength = 24;
                lifetime = 24;
                status = StatusEffects.electrified;
                statusDuration = 2 * 60;
                chain = new ChainLightningFade(lifetime, -1, 2.5f, color, 50, hitEffect){{
                    healPercent = 10;
                    collidesTeam = true;
                }};
                spacing = 6;
            }};

            weapons.add(
                    new Weapon(name("Tera-weapon")){{
                        top = false;
                        shake = 4;
                        shootY = 9;
                        reload = 60;
                        bullet = l;
                        rotate = true;
                        recoil = 3;
                        rotateSpeed = 2;
                        shadow = 20;
                        shootSound = Sounds.laser;
                        x = 25;
                        y = 3;

                    }}
            );

            setEnginesMirror(
                    new UnitEngine(25.5f, -37.5f, 6, 315f),
                    new UnitEngine(17f, -22.5f, 9, 315f)
            );
        }};

        nihilo = new UnitType("nihilo"){{
            float spawnTime = 60 * 14;
            trailLength = 70;
            waveTrailX = 25f;
            waveTrailY = -32f;
            trailScl = 3.5f;
            abilities.add(new TerritoryFieldAbility(220, -1, 150){{
                active = false;
            }});
            abilities.add(new ShieldRegenFieldAbility(100, 600, 60 * 4, 200));
            abilities.add(new UnitSpawnAbility(UnitTypes.flare, spawnTime, 9.5f, -35.5f), new UnitSpawnAbility(UnitTypes.flare, spawnTime, -9.5f, -35.5f), new UnitSpawnAbility(UnitTypes.zenith, spawnTime * 5, 29, -25), new UnitSpawnAbility(UnitTypes.zenith, spawnTime * 5, -29, -25));
            armor = 46;
            drag = 0.2f;
            flying = false;
            speed = 0.6f;
            accel = 0.2f;
            hitSize = 60;
            rotateSpeed = 0.9f;
            health = 63000;
            itemCapacity = 350;
            ammoType = new ItemAmmoType(Items.surgeAlloy);

            immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s.reloadMultiplier >= 1));

            BulletType air = new PointBulletType(){{
                trailEffect = Fx.railTrail;
                splashDamage = 520;
                splashDamageRadius = 88;
                shootEffect = new Effect(10, e -> {
                    Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
                    Lines.stroke(e.fout() * 2 + 0.2f);
                    Lines.circle(e.x, e.y, e.fin() * 28);
                });
                hitEffect = despawnEffect = new Effect(15, e -> {
                    Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
                    Lines.stroke(e.fout() * 2 + 0.2f);
                    Lines.circle(e.x, e.y, e.fin() * splashDamageRadius);
                });
                trailSpacing = 40;
                damage = 100;
                collidesGround = false;
                lifetime = 1;
                speed = 320;
                status = StatusEffects.shocked;
                hitShake = 6;
            }};
            BulletType r = new RailBulletType(){{
                shootEffect = Fx.railShoot;
                length = 500;
                pierceEffect = Fx.railHit;
                hitEffect = Fx.massiveExplosion;
                smokeEffect = Fx.shootBig2;
                damage = 1350 - (hardMod ? 150 : 0);
                pointEffectSpace = 30f;
                pointEffect = Fx.railTrail;
                pierceDamageFactor = 0.6f;
            }};
            weapons.add(
                    new ReRotPointDefenseWeapon(name("nihilo-defense")){{
                        top = false;
                        x = 0;
                        y = -17;
                        reload = 6;
                        targetInterval = 8;
                        targetSwitchInterval = 8;
                        mirror = false;
                        shootSound = Sounds.lasershoot;
                        bullet = new BulletType(){{
                            shootEffect = Fx.sparkShoot;
                            hitEffect = Fx.pointHit;
                            maxRange = 288;
                            damage = 40;
                        }};
                    }},
                    new Weapon(name("nihilo-m")){{
                        top = false;
                        shake = 3;
                        shootY = 2;
                        bullet = new MissileBulletType(3.7f, 30){{
                            lifetime = 60;
                            width = 8f;
                            height = 8f;
                            shrinkY = 0f;
                            splashDamageRadius = 32f;
                            splashDamage = 30f * 1.4f;
                            hitEffect = Fx.blastExplosion;
                            despawnEffect = Fx.blastExplosion;
                            lightningDamage = 12;
                            lightning = 3;
                            lightningLength = 10;
                        }};
                        rotate = true;
                        rotateSpeed = 4;
                        x = 24;
                        y = 1;
                        shoot.shotDelay = 1;
                        xRand = 10;
                        shoot.shots = 6;
                        inaccuracy = 4;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.missile;
                        reload = 36;
                        recoil = 2;
                    }},
                    new Weapon(name("nihilo-a")){{
                        shake = 4;
                        shootY = 6;
                        top = false;
                        shoot.shots = 4;
                        inaccuracy = 12;
                        velocityRnd = 0.2f;
                        bullet = new ArtilleryBulletType(2.8f, 30){{
                            hitEffect = Fx.blastExplosion;
                            knockback = 0.8f;
                            lifetime = 100;
                            width = 14;
                            height = 14;
                            collidesTiles = false;
                            ammoMultiplier = 3;
                            splashDamageRadius = 28;
                            splashDamage = 38;
                            frontColor = Pal.surge;
                            lightningDamage = 12;
                            lightning = 2;
                            lightningLength = 8;
                        }};
                        rotate = true;
                        rotateSpeed = 4;
                        x = 14.5f;
                        y = -10;
                        shootSound = Sounds.artillery;
                        reload = 60;
                        recoil = 4;
                    }},
                    new Weapon(name("nihilo-air")){{
                        shake = 5;
                        shootY = 9;
                        bullet = air;
                        mirror = false;
                        top = false;
                        rotate = true;
                        rotateSpeed = 8;

                        predictTarget = false;
                        controllable = false;
                        autoTarget = true;
                        mountType = reRotMount::new;

                        x = 0;
                        y = 38;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.railgun;
                        reload = 150;
                        recoil = 4;
                    }
                        @Override
                        public void update(Unit unit, WeaponMount m) {
                            super.update(unit, m);
                            float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                                    mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
                            reRotMount mount = (reRotMount) m;
                            if(mount.target != null) {
                                mount.reRotate = 180;
                            } else {
                                mount.reRotate = Math.max(mount.reRotate - Time.delta, 0f);
                            }

                            if(mount.target == null && !mount.shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && mount.reRotate <= 0){
                                mount.rotate = true;
                                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                                mount.aimX = mountX + Tmp.v1.x;
                                mount.aimY = mountY + Tmp.v1.y;
                            }
                        }
                    }
            );
            weapons.add(
                    new Weapon(name("nihilo-main")){{
                        shake = 6;
                        shootY = 23;
                        bullet = r;
                        mirror = false;
                        top = false;
                        rotate = true;
                        rotateSpeed = 2;
                        x = 0;
                        y = 5;
                        ejectEffect = Fx.none;
                        shootSound = Sounds.railgun;
                        reload = 110;
                        cooldownTime = 90;
                        recoil = 5;
                    }}
            );
        }};

        narwhal = new UnitType("narwhal"){{
            UnitCommand c = new UnitCommand("EUAssist", "defense", u -> new DefenderHealAI());
            defaultCommand = c;
            commands = new UnitCommand[]{UnitCommand.moveCommand, UnitCommand.assistCommand, UnitCommand.rebuildCommand, c, UnitCommand.boostCommand};
            armor = 48;
            drag = 0.2f;
            flying = false;
            canBoost = true;
            boostMultiplier = 1.2f;
            riseSpeed = 0.02f;
            lowAltitude = true;
            engineOffset = 45;
            engineSize = 8;
            speed = 0.7f;
            accel = 0.2f;
            hitSize = 60;
            rotateSpeed = 1;
            drawShields = false;
            health = 62500;
            itemCapacity = 800;
            ammoType = new PowerAmmoType(1800);

            buildSpeed = 10;

            immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s == StatusEffects.none || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1));

            abilities.add(new UnitSpawnAbility(UnitTypes.mega, 32 * 60, 0, 27));
            abilities.add(new BatteryAbility(80000, 120, 120, 0, -15));
            abilities.add(new RepairFieldAbility(400, 60 * 3, 120));

            deathExplosionEffect = Fx.none;
            deathSound = Sounds.none;

            int[]
            p1 = new int[]{22, 15, 0, 15, 6},
            p2 = new int[]{-25, 20, 1, 15, -6},
            p3 = new int[]{22, -15, 2, -15, -6},
            p4 = new int[]{-25, -20, 3, -15, 6};
            int[][] pos = new int[][]{p1, p2, p3, p4};
            for(int[] p : pos){
                abilities.add(new propeller(p[0], p[1], name("wing") + p[2], p[3], p[4]));
            }

            for(int xx : new int[]{18, -18}) {
                weapons.add(
                        new healConeWeapon(name("narwhal-heal")) {{
                            x = xx;
                            y = 7;
                            mirror = false;
                            bullet = new HealCone(){{
                                lifetime = 240;
                                healPercent = 10;
                            }};
                            reload = 150;
                            rotate = true;
                            rotateSpeed = 4;
                            alternate = false;
                            useAmmo = true;
                            continuous = true;
                            cooldownTime = 150;
                            shootY = 8;
                            recoil = 0;
                            top = false;
                        }}
                );
            }

            weapons.add(
                    new antiMissileWeapon(name("narwhal-defense")){{
                        bullet = new antiMissile( 30 * 8, name("anti")){{
                            y = -37;
                            x = 0;
                            loadSpeed = -1.5f;
                        }};
                        xRand = 8;
                        reload = 6;
                        mirror = false;
                        shootY = 3;
                        recoil = 1.5f;
                        targetInterval = targetSwitchInterval = 0;
                        inaccuracy = 0;
                        top = false;
                    }}
            );
        }};


        napoleon = new TankUnitType("napoleon"){{
            hitSize = 57;

            speed = 0.39f;
            health = 60000;
            armor = 52;
            crushDamage = 10;
            rotateSpeed = 0.8f;
            treadRects = new Rect[]{
                    new Rect(-113, -133 + 167, 70, 100),
                    new Rect(-113, -133, 70, 90)
            };

            immunities.addAll(StatusEffects.unmoving, StatusEffects.burning, StatusEffects.sapped);
            ammoType = new ItemAmmoType(Items.blastCompound);

            abilities.add(new PcShieldArcAbility(){{
                whenShooting = false;
                radius = 8 * 8;
                max = 8000;
                regen = 3;
                cooldown = 360;
                angle = 160;
                width = 10f;
            }});

            Color esc = Color.valueOf("feb380");
            Mover mover = bullet -> {
                if(bullet.type == null) return;
                float fout = Math.max((60 - bullet.time)/60, 0);
                if(bullet.time < 70) bullet.initVel(bullet.rotation(), bullet.type.speed * fout);
            };

            weapons.add(
                    new Weapon(name("napoleon-wf")){{
                        layerOffset = 0.1f;
                        rotate = true;
                        rotateSpeed = 2;
                        shoot = new ShootSpread(3, 120);
                        shoot.shotDelay = 8;
                        shootSound = Sounds.malignShoot;
                        x = 18;
                        y = 19;
                        shootY = -5;
                        reload = 60;
                        bullet = new CtrlMissile("", -1, -1){{
                            damage = 110;
                            speed = 15;
                            lifetime = 24;
                            homingRange = speed * lifetime;
                            homingDelay = 3;
                            homingPower = 11;
                            trailLength = 5;
                            trailWidth = 4;
                            trailColor = esc;
                            trailEffect = Fx.missileTrail;
                            trailInterval = 1;
                            despawnEffect = hitEffect = EUFx.gone(esc);
                            despawnSound = hitSound = Sounds.artillery;
                        }

                            @Override
                            public void update(Bullet b) {
                                b.lifetime = lifetime * 2.5f;
                                if(b.time > b.lifetime/2){
                                    b.remove();
                                    return;
                                }
                                super.update(b);
                                b.initVel(b.rotation(), speed * b.finpow() * 1.8f);
                            }

                            @Override
                            public void draw(Bullet b) {
                                drawTrail(b);
                                drawParts(b);
                                Draw.color(esc);
                                Drawf.tri(b.x, b.y, 6f, 9f, b.rotation());
                            }
                        };
                    }},
                    new Weapon(name("napoleon-wm")){{
                        mirror = false;
                        rotate = true;
                        layerOffset = 0.1f;
                        rotateSpeed = 0.9f;
                        shootSound = Sounds.release;
                        reload = 180;
                        recoil = 5.5f;
                        shake = 5;
                        x = 0;
                        y = -1f;
                        minWarmup = 0.9f;

                        bullet = new ScarletDevil(esc){{
                            hitSound = despawnSound = Sounds.explosionbig;
                            damage = 410;
                            splashDamage = 390;
                            splashDamageRadius = 12 * 8f;
                            buildingDamageMultiplier = 0.8f;
                            hitEffect = despawnEffect = new ExplosionEffect(){{
                                lifetime = 30f;
                                waveStroke = 5f;
                                waveLife = 10f;
                                waveRad = splashDamageRadius;
                                waveColor = esc;
                                smokes = 7;
                                smokeSize = 13;
                                smokeColor = esc;
                                smokeRad = splashDamageRadius;
                                sparkColor = esc;
                                sparks = 14;
                                sparkRad = splashDamageRadius;
                                sparkLen = 6f;
                                sparkStroke = 2f;
                            }};

                            pierce = true;
                            pierceCap = 2;
                            pierceBuilding = true;

                            speed = 10;
                            trailWidth = 7;
                            trailLength = 12;
                            trailColor = esc;
                            fragBullet = null;
                            fragBullets= 0;
                            healPercent = -1;
                            ff.speed = 1.5f;
                            ff.fragBullets = fragBullets;
                            ff.buildingDamageMultiplier = fb.buildingDamageMultiplier = 0.6f;
                            fb.healPercent = -1;
                            fb.damage = 55f;
                            intervalBullet = fb;
                            intervalBullets = 2;
                            bulletInterval = 6;
                            intervalDelay = 6;
                            intervalSpread = 180;
                        }

                            @Override
                            public void updateBulletInterval(Bullet b) {
                                if (ff != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
                                    float ang = b.rotation();

                                    for(int i = 0; i < intervalBullets; i++) {
                                        ff.create(b, b.team, b.x, b.y, ang - 90 + i * intervalSpread, 1, 1, mover);
                                    }
                                }

                            }
                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(esc);
                                Drawf.tri(b.x, b.y, 13f, 12f, b.rotation());
                            }
                        };
                        parts.add(
                                new PartBow(){{
                                    color = esc;
                                    turretTk = 6;
                                    bowFY = -4;
                                    bowMoveY = -33 - bowFY;
                                    bowTk = 6;
                                    bowWidth = 28;
                                    bowHeight = 12f;
                                }},
                                new BowHalo(){{
                                    color = esc;
                                    stroke = 3;
                                    radius = 9;
                                    w1 = 2.8f;
                                    h1 = 6;
                                    w2 = 4;
                                    h2 = 13;
                                    y = -21;
                                    sinWave = false;
                                }}
                        );

                        parts.add(
                                new RegionPart("-glow") {{
                                    color = esc;
                                    blending = Blending.additive;
                                    outline = mirror = false;
                                }},
                                new ShapePart() {{
                                    progress = PartProgress.warmup.delay(0.5f);
                                    color = esc;
                                    circle = true;
                                    hollow = true;
                                    stroke = 0;
                                    strokeTo = 2;
                                    radius = 14;
                                    layer = Layer.effect;
                                    y = -21;
                                }},
//                                new HaloPart() {{
//                                        progress = PartProgress.warmup.delay(0.5f);
//                                        color = esc;
//                                        layer = Layer.effect;
//                                        y = -21;
//                                        haloRotateSpeed = 2;
//                                        shapes = 4;
//                                        triLength = 0;
//                                        triLengthTo = 4.2f;
//                                        haloRadius = 13;
//                                        tri = true;
//                                        radius = 13;
//                                }},
                                new AimPart(){{
                                    layer = Layer.effect;
                                    y = 15;
                                    width = 0.9f;
                                    length = 10 * 8;
                                    spacing = 10;
                                    color = esc;
                                }}
                        );
                    }},
                    new Weapon(){{
                        mirror = false;
                        shootSound = Sounds.none;
                        rotate = false;
                        shootCone = 360f;
                        x = 0;
                        y = 0;
                        shootY = -20;
                        reload = 210;
                        controllable = false;
                        autoTarget = true;

                        BulletType eb = new BulletType(){{
                            keepVelocity = false;
                            hitEffect = despawnEffect = Fx.none;
                            damage = 0;
                            speed = 4.1f;
                            lifetime = 30;
                            collides = collidesGround = collidesAir = collidesTiles = absorbable = hittable = false;
                        }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                if(!(b.data instanceof Float)) return;
                                float ier = (Float) b.data;
                                b.lifetime = lifetime + ier;
                                if(b.time < ier){
                                    b.initVel(b.rotation(), 0);
                                } else {
                                    b.initVel(b.rotation(), speed * Math.min(1 - (b.time - (Float)b.data)/lifetime, 1));
                                }
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                float t = b.lifetime/2;
                                Draw.color(esc);
                                if(b.time < t) {
                                    float fin = b.fin() * 2;
                                    Drawf.tri(b.x, b.y, 10 * fin, 21 * fin, b.rotation() - 90);
                                    Drawf.tri(b.x, b.y, 10 * fin, 21 * fin, b.rotation() + 90);
                                } else {
                                    float fout = b.fout() * 2;
                                    Drawf.tri(b.x, b.y, 10 * fout, 21 * fout, b.rotation() - 90);
                                    Drawf.tri(b.x, b.y, 10 * fout, 21 * fout, b.rotation() + 90);
                                }
                            }
                        };

                        bullet = new BulletType(){{
                            hittable = absorbable = collides = collidesTiles = false;

                            damage = 0;
                            speed = 1;
                            lifetime = 46 * 8;
                            despawnSound = hitSound = Sounds.cannon;
                            despawnShake = hitShake = 4;
                            despawnEffect = hitEffect = new MultiEffect(new Effect(60, 160, (e) -> {
                                Draw.color(esc);
                                Lines.stroke(e.fout() * 5);
                                float circleRad = 6 + e.finpow() * 60;
                                Lines.circle(e.x, e.y, circleRad);
                                rand.setSeed(e.id);

                                for(int i = 0; i < 16; ++i) {
                                    float angle = rand.random(360);
                                    float lenRand = rand.random(0.5f, 1);
                                    Tmp.v1.trns(angle, circleRad);
                                    int[] signs = Mathf.signs;

                                    for (int s : signs) {
                                        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 40, e.fout() * 30 * lenRand + 6, angle + 90 + (float) s * 90);
                                    }
                                }

                            }), Fx.casing3Double);
                            trailEffect = new Effect(38, e -> {
                                Draw.color(esc);
                                Angles.randLenVectors(e.id, 1, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> Fill.circle(e.x + x, e.y + y, e.foutpow() * 6));
                            });
                            trailInterval = 5;
                            fragBullets = 4;

                            fragBullet = new BulletType(){{
                                damage = splashDamage = 140;
                                splashDamageRadius = 8 * 8;
                                keepVelocity = false;
                                homingRange = 45 * 8;
                                homingPower = 0.3f;
                                homingDelay = 10;
                                speed = 8;
                                lifetime = 48;
                                trailColor = Color.valueOf("feb380");
                                trailWidth = 7;
                                trailLength = 7;
                                hitEffect = despawnEffect = new MultiEffect(new Effect(30, e -> {
                                    Lines.stroke(10 * e.foutpow(), esc);
                                    Lines.circle(e.x, e.y, splashDamageRadius * e.finpow());
                                }), EUFx.expFtEffect(7, 11, 8, 17, 0), new ExplosionEffect(){{
                                    lifetime = 30f;
                                    waveStroke = 0;
                                    waveLife = 0;
                                    smokes = 7;
                                    smokeSizeBase = 0;
                                    smokeSize = 11;
                                    smokeRad = splashDamageRadius;
                                    sparkColor = esc;
                                    sparks = 9;
                                    sparkRad = splashDamageRadius;
                                    sparkLen = 6f;
                                    sparkStroke = 2f;
                                }});
                                hitSound = despawnSound = Sounds.explosion;
                            }

                                @Override
                                public void draw(Bullet b) {
                                    super.draw(b);
                                    Draw.color(esc);
                                    Drawf.tri(b.x, b.y, 9f, 11f, b.rotation());
                                }

                                @Override
                                public void createFrags(Bullet b, float x, float y) {
                                    super.createFrags(b, x, y);
                                    float a = Mathf.random(360);
                                    for(int i = 0; i < 4; i++){
                                        eb.create(b, b.team, x, y, a + i * (360/8f), -1, 1,  1, i * 3f);
                                        eb.create(b, b.team, x, y, a + i * (360/8f) + 180, -1, 1,  1, i * 5f);
                                    }
                                }
                            };
                        }


                            @Override
                            public void createFrags(Bullet b, float x, float y) {
                                if (fragBullet != null && (fragOnAbsorb || !b.absorbed)) {
                                    for(int i = 0; i < fragBullets; ++i) {
                                        fragBullet.create(b, b.x, b.y, b.rotation() - 90 + i * 60, 1, 1);
                                    }
                                }
                            }

                            @Override
                            public void update(Bullet b) {
                                if(b.time > 90) {
                                    b.remove();
                                    return;
                                }
                                super.update(b);
                                float f = b.time/90;
                                b.initVel(b.rotation(), speed * f * f);
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(esc);
                                float sin = Mathf.sin(Time.time, 4, 2);
                                Fill.circle(b.x, b.y, 12);
                                Draw.color(Color.black);
                                Fill.circle(b.x, b.y, 7 + sin);
                            }
                        };
                        parts.add(
                                new AimPart(){{
                                    layer = Layer.effect;
                                    x = 45;
                                    color = esc;
                                    spacing = 5;
                                    length = 15;
                                    drawLine = false;
                                    rt = 90;
                                }},
                                new AimPart(){{
                                    layer = Layer.effect;
                                    x = -45;
                                    color = esc;
                                    spacing = 5;
                                    length = 15;
                                    drawLine = false;
                                    rt = -90;
                                }}
                        );
                    }}
            );

            parts.add(new RegionPart("-glow") {{
                    color = Color.red;
                    blending = Blending.additive;
                    layer = -1;
                    outline = false;
                }
            });
        }};

        havoc = new ErekirUnitType("havoc"){{
            aiController = FlyingFollowAI::new;
            envDisabled = 0;
            lowAltitude = false;
            flying = true;
            drag = 0.08f;
            speed = 0.89f;
            rotateSpeed = 1.8f;
            accel = 0.1f;
            health = 40000;
            armor = 27;
            hitSize = 48;
            payloadCapacity = Mathf.sqr(7.2f) * 64;
            engineSize = 6;
            engineOffset = 22.3f;

            immunities.addAll(StatusEffects.wet, StatusEffects.freezing, StatusEffects.sapped, StatusEffects.disarmed, StatusEffects.electrified, EUStatusEffects.speedDown);
            
            abilities.add(
                    new SuppressionFieldAbility() {{
                            orbRadius = 8;
                            particleSize = 9;
                            y = -3.2f;
                            particles = 9;
                    }}
            );
            for(float xx : new float[]{14.2f, -14.2f}){
                abilities.add(
                        new SuppressionFieldAbility() {{
                            orbRadius = 5;
                            particleSize = 9;
                            y = -12.4f;
                            x = xx;
                            particles = 6;
                        }}
                );
            }

            weapons.add(
                    new Weapon(){{
                        shootSound = Sounds.missileLarge;
                        baseRotation = 90;
                        alternate = false;
                        shoot.shots = 3;
                        shoot.shotDelay = 8;
                        xRand = 20;
                        reload = 90;
                        shootCone = 180;
                        x = 12;
                        float spr = 9 * 8f;
                        Effect de = new ExplosionEffect(){{
                            lifetime = 30f;
                            waveStroke = 5f;
                            waveLife = 8f;
                            waveColor = Pal.sap;
                            sparkColor = smokeColor = Pal.suppress;
                            waveRad = spr;
                            smokeSize = 5f;
                            smokes = 7;
                            smokeSizeBase = 0f;
                            sparks = 9;
                            sparkRad = spr;
                            sparkLen = 6f;
                            sparkStroke = 2f;
                        }};
                        BulletType ms = new CtrlMissile(name("havoc-missile"), 14, 20){{
                            damage = splashDamage = 160;
                            splashDamageRadius = spr;
                            despawnEffect = hitEffect = de;
                            speed = 6;
                            lifetime = 90;
                            homingPower = 8;
                            trailEffect = Fx.artilleryTrail;
                            trailInterval = 1;
                            trailWidth = 4;
                            trailLength = 8;
                            trailColor = Pal.suppress;

                            autoHoming = true;

                            buildingDamageMultiplier = 0.7f;
                        }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * Math.min(b.finpow() * 2, 1));
                            }
                        };
                        bullet = new BulletType(){{
                            damage = splashDamage = 160;
                            splashDamageRadius = spr;
                            hitEffect = despawnEffect = Fx.casing4;
                            speed = 10;
                            lifetime = 40;
                            collides = collidesTiles = absorbable = hittable = false;
                            buildingDamageMultiplier = 0.7f;
                            fragOnHit = false;
                        }

                            @Override
                            public void hitEntity(Bullet b, Hitboxc entity, float health) { }

                            @Override
                            public void hit(Bullet b, float x, float y) { }

                            @Override
                            public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) { }

                            @Override
                            public void createFrags(Bullet b, float x, float y) {
                                super.createFrags(b, x, y);
                                if(!(b.owner instanceof Unit owner)) return;
                                if(!b.absorbed) ms.create(owner, owner.team, b.x, b.y, owner.rotation(), -1, 1, 1, EUGet.pos(owner.mounts[0].aimX, owner.mounts[0].aimY));
                            }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * Math.max(b.foutpow(), 0));
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                if(!(b.owner instanceof Unit owner)) return;
                                float n = Math.max(b.foutpow(), 0.4f);
                                Draw.z(Layer.flyingUnitLow);
                                Draw.rect(Core.atlas.find(name("havoc-missile")), b.x, b.y, 35 * n, 50 * n, owner.rotation - 90);
                                Tmp.v1.set(0, -8).rotate(owner.rotation - 90);
                                Draw.color(Pal.suppress);
                                Draw.z(Layer.bullet);
                                Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, 8 * b.foutpow() + 2);
                                Draw.reset();
                            }
                        };
                    }},
                    new Weapon(name("havoc-w")){{
                        shootSound = Sounds.malignShoot;
                        rotate = true;
                        rotateSpeed = 3;
                        rotationLimit = 80;
                        x = 26;
                        y = -10;
                        reload = 15;
                        recoil = 2;
                        layerOffset = -0.1f;
                        shootY = 10;
                        bullet = new BulletType(){{
                            damage = 120;
                            speed = 10;
                            lifetime = 55 * 8f / 10f;
                            homingPower = 0.3f;
                            homingRange = 64;
                            trailColor = Pal.suppress;
                            trailWidth = 4;
                            trailLength = 7;
                            shootEffect = new Effect(24, e -> {
                                Draw.color(Pal.suppress);
                                Angles.randLenVectors(e.id, 4, 20 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 10 * e.foutpow()));
                            });
                            hitEffect = despawnEffect = new MultiEffect(shootEffect, new ExplosionEffect(){{
                                lifetime = 24f;
                                waveStroke = 5f;
                                waveLife = 8f;
                                waveColor = Pal.sap;
                                sparkColor = Pal.suppress;
                                waveRad = 20;
                                smokes = 0;
                                sparks = 8;
                                sparkRad = 20;
                                sparkLen = 6f;
                                sparkStroke = 2f;
                            }});
                        }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(Pal.suppress);
                                Drawf.tri(b.x, b.y, 8, 10, b.rotation());
                            }
                        };
                    }}
            );

            weapons.add(
                    new Weapon(){{
                        shootSound = Sounds.cannon;
                        reload = 300;
                        autoTarget = true;
                        controllable = false;
                        mirror = false;
                        x = 0;
                        y = 0;
                        shootCone = 360;
                        BulletType cdd = new diffBullet(360, 3){{
                            damage = splashDamage = 500;
                            splashDamageRadius = 10f * 8;
                            lifetime = 90;
                            color = Pal.suppress;
                            pfin = false;
                        }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                float pin = (1 - b.foutpow());
                                rand.setSeed(b.id);
                                for(int i = 0; i < 5; i++){
                                    float a = rand.random(180);
                                    float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                    float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                    Draw.color(Pal.suppress);
                                    Drawf.tri(lx, ly, 25 * b.foutpow(), (70 + rand.random(-10, 10)) * b.foutpow(), a + 180);
                                }
                                for(int i = 0; i < 5; i++){
                                    float a = 180 + rand.random(180);
                                    float lx = EUGet.dx(b.x, splashDamageRadius * pin, a);
                                    float ly = EUGet.dy(b.y, splashDamageRadius * pin, a);
                                    Draw.color(Pal.suppress);
                                    Drawf.tri(lx, ly, 25 * b.foutpow(), (70 + rand.random(-10, 10)) * b.foutpow(), a + 180);
                                }

                                Effect.shake(2, 2, b);
                            }
                        };
                        BulletType cff = new fBullet(cdd, 15){
                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Lines.stroke(6 * b.fout(), Pal.suppress);
                                Lines.circle(b.x, b.y, 18 * b.fout());
                            }
                            {
                                despawnSound = Sounds.explosionbig;
                            }
                        };
                        BulletType cll = new fBullet(cff, 90){{
                            trailEffect = new Effect(38, e -> {
                                Draw.color(Pal.suppress);
                                Angles.randLenVectors(e.id, 1, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> Fill.circle(e.x + x, e.y + y, e.foutpow() * 6));
                            });
                            trailInterval = 1;
                        }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(Pal.suppress);
                                Fill.circle(b.x, b.y, 18 * b.finpow());
                                float sin = Mathf.sin(Time.time, 6, 2);
                                Draw.color(Color.black);
                                Fill.circle(b.x, b.y, (9 + sin) * b.finpow());
                            }
                        };
                        bullet = new BulletType(){{
                            speed = 1;
                            lifetime = 24 * 8;
                            damage = splashDamage = cdd.splashDamage;
                            splashDamageRadius = cdd.splashDamageRadius;
                            hittable = absorbable = collides = collidesTiles = false;
                            smokeEffect = shootEffect = Fx.none;
                            despawnEffect = hitEffect = Fx.none;
                            fragOnHit = false;
                        }

                            @Override
                            public void hit(Bullet b, float x, float y) { }

                            @Override
                            public void hitEntity(Bullet b, Hitboxc entity, float health) { }

                            @Override
                            public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) { }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), 0);
                                if(!(b.owner instanceof Unit owner)) return;
                                Seq<Building> ms = new Seq<>();
                                Seq<Building> bs = new Seq<>();
                                Units.nearbyBuildings(owner.x, owner.y, (speed + 0.1f) * lifetime, building -> {
                                    if(building.team != b.team && building.block != null && building.block.targetable){
                                        if(building instanceof MendProjector.MendBuild || building instanceof RegenProjector.RegenProjectorBuild)
                                            ms.addUnique(building);
                                        else
                                            bs.addUnique(building);
                                    }
                                });
                                ms.removeAll(building -> building == null || building.dead);
                                bs.removeAll(building -> building == null || building.dead);
                                ms.sort(building -> building.dst(owner));
                                bs.sort(building -> building.dst(owner));
                                Building building = ms.size > 0 ? ms.get(0) : bs.size > 0 ? bs.get(0) : null;
                                if(building != null){
                                    cll.create(b, building.x, building.y, 0);
                                }
                                b.remove();
                            }
                        };

                        parts.add(
                                new DrawPart(){
                                    final PartProgress progress = PartProgress.warmup;
                                    final PartProgress reload = PartProgress.reload;

                                    @Override
                                    public void draw(PartParams params) {
                                        float warmup = progress.getClamp(params);
                                        float rd = 1 - reload.getClamp(params);
                                        Draw.z(Layer.effect);
                                        Lines.stroke(7 * warmup, Pal.suppress);
                                        DrawFunc.circlePercent(params.x, params.y, 72, rd, params.rotation + 180);
                                    }

                                    @Override
                                    public void load(String s) {

                                    }
                                }
                        );
                    }},
                    new Weapon(){{
                        x = y = 0;
                        mirror = false;
                        reload = 180;
                        shoot.shots = 4;
                        shoot.shotDelay = 8;
                        shootSound = Sounds.laser;
                        bullet = new mixBoom(Pal.suppress){{
                            damage = 0;
                            splashDamage = 180;
                            splashDamageRadius = 8 * 8;
                            lifetime = 25;
                            speed = 16;
                            trailInterval = 0;
                            collidesGround = collidesAir = true;
                            despawnEffect = new ExplosionEffect(){{
                                smokes = sparks = 6;
                                waveLife = 6;
                                waveRad = splashDamageRadius;
                                waveStroke = 6;
                                lifetime = 12;
                                sparkRad = splashDamageRadius;
                                sparkLen = 3f;
                                sparkStroke = 2f;
                                waveColor = sparkColor = smokeColor = Pal.suppress;
                                smokeSize = 4;
                                smokeSizeBase = 0;
                            }};
                            fragBullet = new mixExps(){{
                                circle = false;
                                lifetime = 20;
                                speed = 3f;
                                trailColor = color = Pal.suppress;
                                damage = 0;
                                splashDamage = 32;
                                splashDamageRadius = 5.4f * 8;
                                trailWidth = 4;
                                trailLength = 8;
                                hitEffect = despawnEffect = new ExplosionEffect(){{
                                    lifetime = 20f;
                                    waveLife = 6;
                                    waveRad = splashDamageRadius;
                                    waveColor = sparkColor = smokeColor = Pal.suppress;
                                    smokeSize = 3;
                                    smokeSizeBase = 0;
                                    smokes = 4;
                                    sparks = 5;
                                    sparkRad = splashDamageRadius;
                                    sparkLen = 2f;
                                    sparkStroke = 1.5f;
                                }};
                            }

                                @Override
                                public void draw(Bullet b) {
                                    drawTrail(b);
                                    drawParts(b);
                                    Draw.color(Pal.suppress);
                                    Drawf.tri(b.x, b.y, 6, 11 * b.fout(), b.rotation());
                                }
                            };
                            fragBullets = 6;
                            fragAngle = 30;
                            fragSpread = 60;
                            fragRandomSpread = 0;
                        }

                            @Override
                            public void draw(Bullet b) {
                                drawTrail(b);
                                drawParts(b);
                                Draw.color(Pal.suppress);
                                Drawf.tri(b.x, b.y, 10, 30, b.rotation());
                                Drawf.tri(b.x, b.y, 10, 40, b.rotation() - 180);
                            }
                        };

                        parts.add(
                                new AimPart(){{
                                    layer = Layer.effect;
                                    x = 10;
                                    y = 10;
                                    color = Pal.suppress;
                                    spacing = 12;
                                    length = 48;
                                    drawLine = false;
                                    rt = -90;
                                }},
                                new AimPart(){{
                                    layer = Layer.effect;
                                    x = -10;
                                    y = 10;
                                    color = Pal.suppress;
                                    spacing = 12;
                                    length = 48;
                                    drawLine = false;
                                    rt = 90;
                                }}
                        );
                    }}
            );

            parts.add(
                    new BowHalo(){{
                        progress = PartProgress.constant(1);
                        y = -4;
                        radius = 19;
                        w1 = 6;
                        h1 = 16;
                        w2 = 8;
                        h2 = 38;
                        color = Pal.suppress;
                    }},
                    new DrawPart(){
                        final PartProgress progress = PartProgress.warmup;
                        final PartProgress reload = PartProgress.reload;

                        @Override
                        public void draw(PartParams params) {
                            float warmup = progress.getClamp(params);
                            float rd = 1 - reload.getClamp(params);
                            Draw.z(Layer.effect);
                            Lines.stroke(5 * warmup, Pal.suppress);
                            DrawFunc.circlePercent(params.x, params.y, 55, rd, params.rotation);
                            for(int i = 0; i < 4; i++){
                                float r = i * 90 + Time.time * 2;
                                float dx = EUGet.dx(params.x, 68, r), dy = EUGet.dy(params.y, 68, r);
                                Drawf.tri(dx, dy, 14 * warmup, 13 * warmup, r + 180);
                            }
                        }

                        @Override
                        public void load(String s) {

                        }
                    }
            );

            setEnginesMirror(new UnitEngine(14, -25f, 6f, 315f));
            setEnginesMirror(new UnitEngine(28.9f, -24.3f, 3f, 315f));
        }};

        arcana = new ErekirUnitType("arcana"){{
            drag = 0.12f;
            speed = 0.9f;
            hitSize = 50f;
            health = 51000;
            armor = 30f;
            rotateSpeed = 1.1f;
            lockLegBase = true;
            legContinuousMove = true;
            legStraightness = 0.4f;
            baseLegStraightness = 1.2f;

            legCount = 8;
            legLength = 40f;
            legForwardScl = 2.4f;
            legMoveSpace = 1.1f;
            rippleScale = 1.2f;
            stepShake = 0.5f;
            legGroupSize = 2;
            legExtension = 2f;
            legBaseOffset = 12f;
            legStraightLength = 1.1f;
            legMaxLength = 1.2f;

            ammoType = new PowerAmmoType(3500);

            legSplashDamage = 90;
            legSplashRange = 40;
            drownTimeMultiplier = 2f;

            hovering = true;
            shadowElevation = 0.8f;
            groundLayer = Layer.legUnit;

            alwaysShootWhenMoving = true;
            maxRange = 50 * 8f;

            immunities.addAll(StatusEffects.wet, StatusEffects.unmoving, StatusEffects.disarmed, StatusEffects.slow);

            weapons.add(
                    new Weapon(name("arcana-wm")){{
                        shootSound = Sounds.shotgun;
                        reload = 120;
                        recoil = 4;
                        shake = 5;
                        x = 14.3f;
                        y = -10;
                        rotate = true;
                        rotateSpeed = 2;
                        shootY = 10;

                        BulletType crack = new LaserBulletType(){{
                            damage= 130;
                            length = 90;
                            lifetime = 20;
                            laserAbsorb = false;
                        }

                            @Override
                            public void draw(Bullet b) {
                                Draw.z(Layer.bullet);
                                Draw.color(Pal.techBlue);
                                float in = Mathf.curve(b.fin(), 0f, 0.5f);
                                Drawf.tri(b.x, b.y, 25 * b.fout(), length * in, b.rotation());
                                Draw.z(Layer.effect + 1);
                                Draw.color(Color.black);
                                Drawf.tri(b.x, b.y, 18 * b.fout(), (length/1.3f) * in, b.rotation());
                            }
                        };
                        BulletType crackFrag = new BulletType(){{
                            keepVelocity = absorbable = hittable = collides = collidesTiles = collidesGround = collidesAir = false;
                            damage = speed = 0;
                            lifetime = 60;
                            hitEffect = despawnEffect = Fx.none;
                        }

                            @Override
                            public void update(Bullet b) {
                                if(b.timer.get(lifetime/5) && b.time < lifetime - lifetime/5){
                                    float r = Mathf.random(360);
                                    float xx = b.x + Mathf.random(-12, 12);
                                    float yy = b.y + Mathf.random(-12, 12);
                                    crack.create(b, xx, yy, b.rotation() + 90 + r);
                                    crack.create(b, xx, yy, b.rotation() - 90 + r);
                                    Sounds.laserblast.at(xx, yy);
                                    Effect.shake(5, 5, xx, yy);
                                }
                            }
                        };
                        BulletType fff = new fBullet(crackFrag, 10);

                        bullet = new BulletType(){{
                            lifetime = 22;
                            speed = 20;
                            splashDamage = 315;
                            splashDamageRadius = 8 * 8;
                            trailLength = 9;
                            trailWidth = 5;
                            trailColor = Pal.techBlue;
                            trailRotation = true;
                            trailEffect = new Effect(30, e ->{
                                color(e.color);
                                for(int x : new int[]{-8, 8}){
                                    Tmp.v1.set(x, -3).rotate(e.rotation - 90);
                                    Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 6 * e.foutpow());
                                }
                            });
                            trailInterval = 0.1f;
                            scaleLife = true;
                            keepVelocity = false;
                            collides = collidesTiles = absorbable = hittable = false;

                            Effect e1 = new Effect(36, e -> {
                                rand.setSeed(e.id);
                                for(int i = 0; i < 6; i++){
                                    Draw.color(Pal.techBlue);
                                    float r = rand.random(360);
                                    float rx = EUGet.dx(e.x, 25 * e.finpow(), r), ry = EUGet.dy(e.y, 25 * e.finpow(), r);

                                    Drawf.tri(rx, ry, 24 * e.foutpow(), 36, r);
                                    Drawf.tri(rx, ry, 24 * e.foutpow(), 18, r - 180);
                                }
                            });
                            Effect e2 = new Effect(15, e -> {
                                rand.setSeed(e.id);
                                for(int i = 0; i < 10; i++){
                                    Draw.color(Pal.techBlue);
                                    float r = rand.random(360);
                                    float rx = EUGet.dx(e.x, splashDamageRadius * e.finpow(), r), ry = EUGet.dy(e.y, splashDamageRadius * e.finpow(), r);

                                    Drawf.tri(rx, ry, 30 * e.foutpow(), 30 * e.finpow(), r + 90);
                                    Drawf.tri(rx, ry, 30 * e.foutpow(), 30 * e.finpow(), r - 90);
                                }
                            });
                            Effect e3 = new Effect(54, e -> {
                                Draw.color(Pal.techBlue);
                                Angles.randLenVectors(e.id, 5, splashDamageRadius / 2f * e.finpow(), e.rotation, 360, (x, y) -> Fill.square(e.x + x, e.y + y, 20 * e.foutpow()));
                            });
                            hitEffect = despawnEffect = new MultiEffect(e1, e2, e3, new ExplosionEffect(){{
                                lifetime = 24f;
                                waveRad = 0;
                                smokeColor = Pal.techBlue;
                                smokes = 6;
                                smokeSize = 6;smokeRad = splashDamageRadius;
                                sparks = 0;
                            }});
                            shootEffect = e3;

                            status = StatusEffects.slow;
                            statusDuration = 60;

                            fragBullet = crack;
                            fragBullets = 4;
                        }

                            @Override
                            public void init(Bullet b) {
                                super.init(b);
                                float ex, ey;
                                ex = b.x + Angles.trnsx(b.rotation(), speed * b.lifetime);
                                ey = b.y + Angles.trnsy(b.rotation(), speed * b.lifetime);
                                EUFx.chainLightningFade(b.lifetime * 2).at(b.x, b.y, b.lifetime * 4, trailColor, EUGet.pos(ex, ey));
                                EUFx.chainLightningFade(b.lifetime * 2).at(b.x, b.y, b.lifetime * 4, trailColor, EUGet.pos(ex, ey));
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(Pal.techBlue);
                                Drawf.tri(b.x, b.y, 25, 20, b.rotation());
                            }

                            @Override
                            public void createFrags(Bullet b, float x, float y) {
                                fff.create(b, b.x, b.y, 0);
                            }
                        };

                        parts.add(
                                new JavelinWing(){{
                                    y = -4;
                                    w1 = 2;
                                    h1 = 7;
                                    w2 = 3;
                                    h2 = 4;
                                    rd = 10;
                                    layer = Layer.bullet;
                                    color = Pal.techBlue;
                                    ap = 0.8f;
                                }},
                                new BowHalo(){{
                                    y = -11;
                                    color = Pal.techBlue;
                                    w1 = h1 = 0;
                                    w2 = 3;
                                    h2 = 9;
                                    stroke = 2;
                                    radius = 3;
                                }}
                        );
                    }},
                    new Weapon(){{
                        shootSound = Sounds.bioLoop;
                        shootY = 16;
                        reload = 240;
                        mirror = false;
                        x = y = 0;
                        continuous = true;
                        cooldownTime = 150;
                        bullet = new liContinuousLaserBullet(){{
                            damage = 1100/12f;
                            lifetime = 150;
                            colors = new Color[]{Pal.techBlue, Pal.lancerLaser, Color.white};
                            incendAmount = 0;
                            incendChance = -1;
                            status = StatusEffects.shocked;
                            width = 6;
                            length = 30 * 8;
                            pierceCap = 4;
                            hitEffect = new Effect(18, e -> {
                                Draw.color(Pal.techBlue);
                                Angles.randLenVectors(e.id, 3, 16 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 9 * e.foutpow()));
                            });
                            shootEffect = despawnEffect = new Effect(24, e -> {
                                Draw.color(Pal.techBlue);
                                Angles.randLenVectors(e.id, 5, 40 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 17f * e.foutpow()));
                            });
                            chain = new ChainLightningFade(lifetime, -1, 2.5f, Pal.techBlue, damage/3, hitEffect);
                        }};

                        parts.add(
                                new AimPart(){{
                                    layer = Layer.bullet;
                                    color = Pal.techBlue;
                                    x = -27;
                                    y = -7;
                                    rt = -55;
                                    drawLine = false;
                                    length = 32;
                                    spacing = 8;
                                }},
                                new AimPart(){{
                                    layer = Layer.bullet;
                                    color = Pal.techBlue;
                                    x = 27;
                                    y = -7;
                                    rt = 55;
                                    drawLine = false;
                                    length = 32;
                                    spacing = 8;
                                }}
                        );
                        parts.add(
                                new DrawPart() {
                                    final PartProgress r = PartProgress.reload;
                                    //final PartProgress w = PartProgress.warmup;
                                    final float rad = 7;
                                    @Override
                                    public void draw(PartParams params) {
                                        float z = Draw.z();
                                        float reload = 1 - r.getClamp(params);
                                        Tmp.v1.set(0, 13).rotate(params.rotation - 90);
                                        float px = params.x + Tmp.v1.x, py = params.y + Tmp.v1.y;

                                        Draw.z(Layer.bullet);
                                        Draw.color(Pal.techBlue);
                                        Fill.circle(px, py, rad * reload);

                                        for(int i = 0; i < 3; i++){
                                            float sin = Mathf.absin(Time.time + 120 * i, 15, 6 * reload);
                                            float sin_m = Mathf.absin(Time.time + 120 * i, 30, 6 * reload);
                                            float r = 360/3f * i + Time.time;
                                            float r2 = r + 190;
                                            Drawf.tri(px + Angles.trnsx(r, (rad - 1) * reload), py + Angles.trnsy(r, (rad - 1) * reload), 6 * reload, sin, r);
                                            Drawf.tri(px + Angles.trnsx(r2, (rad - 1) * reload), py + Angles.trnsy(r2, (rad - 1) * reload), 6 * reload, sin_m, r2);
                                        }

                                        Draw.reset();
                                        Draw.z(z);
                                    }

                                    @Override
                                    public void load(String name) {

                                    }
                                }
                        );
                    }}
            );


            weapons.add(
                    new Weapon(name("arcana-wp")){{
                        shootSound = Sounds.blaster;
                        x = 11.2f;
                        y = 25f;
                        recoil = 2;
                        reload = 45;
                        rotate = true;
                        rotateSpeed = 4;
                        shoot = new ShootHelix();
                        shoot.shots = 2;
                        ((ShootHelix)shoot).mag = 2.8f;
                        predictTarget = false;

                        autoTarget = true;
                        controllable = false;
                        targetInterval = targetSwitchInterval = 12;
                        mountType = reRotMount::new;

                        bullet = new BasicBulletType(6, 55){{
                            width = 10;
                            height = 15;
                            shootEffect = Fx.lancerLaserShoot;
                            smokeEffect = Fx.shootBigSmoke;
                            lifetime = 24f;
                            pierce = true;
                            pierceBuilding = true;
                            absorbable = false;
                            trailLength = 7;
                            trailWidth = 3;
                            trailInterval = 1;
                            trailEffect = Fx.artilleryTrail;
                            backColor = frontColor = trailColor = Pal.techBlue;
                            hitEffect = despawnEffect = EUFx.gone(Pal.techBlue);
                        }};
                    }

                        @Override
                        public void update(Unit unit, WeaponMount m) {
                            super.update(unit, m);
                            float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                                    mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
                            reRotMount mount = (reRotMount) m;
                            if(mount.target != null) {
                                mount.reRotate = 180;
                            } else {
                                mount.reRotate = Math.max(mount.reRotate - Time.delta, 0f);
                            }

                            if(mount.target == null && !mount.shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && mount.reRotate <= 0){
                                mount.rotate = true;
                                Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
                                mount.aimX = mountX + Tmp.v1.x;
                                mount.aimY = mountY + Tmp.v1.y;
                            }
                        }
                    }
            );

            for(float xx : new float[]{14.7f, -14.7f}){
                weapons.add(
                        new TractorBeamWeapon(name("arcana-wp")){{
                            mirror = false;
                            x = xx;
                            y = 14.5f;
                            trueDamage = true;
                            force = 20;
                            scaledForce = 10;
                            bullet = new BulletType(){{
                                damage = 4;
                                maxRange = 30 * 8;
                                collidesGround = false;
                            }};
                        }}
                );
            }
            for(float xx : new float[]{22.2f, -22.2f}){
                weapons.add(
                        new ReRotPointDefenseWeapon(name("arcana-wp")){{
                            mirror = false;
                            x = xx;
                            y = 5.3f;
                            reload = 12;
                            targetInterval = targetSwitchInterval = 5;
                            recoil = 1;
                            bullet = new BulletType(){{
                                damage = 80;
                                maxRange = 24 * 8;
                            }};
                        }}
                );
            }
        }};


        winglet = new UnitType("winglet"){{
            armor = 1;
            flying = true;
            hitSize = 12;
            speed = 3;
            accel = 0.04f;
            drag = 0.02f;
            health = 130;
            if(!hardMod) {
                mineSpeed = 1f;
                mineTier = 1;
                buildSpeed = 0.2f;
            }
            itemCapacity = 50;
            engineOffset = 5.8f;
            engineSize = 2.1f;
            ammoType = new PowerAmmoType(500);

            BulletType sapper = new SapBulletType(){{
                sapStrength = 1f;
                length = 72;
                damage = 11;
                shootEffect = Fx.shootSmall;
                hitColor = Color.valueOf("bf92f9");
                color = Color.valueOf("bf92f9");
                despawnEffect = Fx.none;
                width = 1f;
                lifetime = 30;
                knockback = -1;
            }};

            weapons.add(
                    new Weapon(""){{
                        x = 0;
                        y = 3;
                        mirror = false;
                        reload = 21;
                        rotate = false;
                        bullet = sapper;
                        shootSound = Sounds.sap;
                    }}
            );

            setEnginesMirror(new UnitEngine(6, -5.8f, 1.6f, 315f));
        }};
    }
}
