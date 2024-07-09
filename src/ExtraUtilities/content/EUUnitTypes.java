package ExtraUtilities.content;

import ExtraUtilities.ai.DefenderHealAI;
import ExtraUtilities.ai.MinerPointAI;
import ExtraUtilities.worlds.drawer.*;
import ExtraUtilities.worlds.entity.ability.*;
import ExtraUtilities.worlds.entity.bullet.*;
import ExtraUtilities.worlds.entity.unit.bossEntity;
import ExtraUtilities.worlds.entity.unit.bossType;
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
import arc.scene.style.TextureRegionDrawable;
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
import mindustry.entities.UnitSorts;
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
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;

import static ExtraUtilities.ExtraUtilitiesMod.hardMod;
import static ExtraUtilities.ExtraUtilitiesMod.name;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.ui;
import static mindustry.content.Fx.rand;

public class EUUnitTypes {
    public static int bossId;
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

        bossId = EntityMapping.register(name("regency"), bossEntity::new);
    }

    public static UnitType
        miner, T2miner,
        //T6
        suzerain, nebula, asphyxia, apocalypse, Tera, nihilo, narwhal,
        //E-T6
        napoleon, havoc, arcana,
        //air sapper
        winglet,
        //BOSS
        regency;

    public static void loadBoss(){
        regency = new bossType("regency"){{
            speed = 0.35f;
            accel = 0.06f;
            drag = 0.02f;
            createScorch = false;
            faceTarget = false;
            flying = true;
            lowAltitude = true;
            health = 60000;
            hitSize = 160f;
            hideDetails = false;//use to test...

            rotateSpeed = 0.2f;

            engineOffset = 110;
            engineSize = 40;

            buildSpeed = 20;

            deathExplosionEffect = Fx.none;
            deathSound = Sounds.titanExplosion;
            targetFlags = new BlockFlag[]{BlockFlag.turret, BlockFlag.core, null};

            setEnginesMirror(
                    new UnitEngine(40, -112, 20, 315f),
                    new UnitEngine(88, -103, 14, 315f)
            );

            abilities.add(new bossUnitAbi(){
                @Override
                public void addStats(Table t) {
                    super.addStats(t);
                    if(locked() || hideDetails) {
                        t.add("[red]Unknown...[]" + Iconc.lock + Core.bundle.get("unlock.incampaign"));
                        return;
                    }
                    t.add(Core.bundle.get("ability.stat.boss"));
                    t.row();
                    t.add("SUMMON: ");
                    t.row();
                    t.button(new TextureRegionDrawable(havoc.uiIcon), () -> ui.content.show(havoc)).padTop(2f).padBottom(2f).size(50);
                    t.row();
                }
            });

            Color eccl = Color.valueOf("87CEEB");
            Color eccb = Color.valueOf("6D90BC");

            BulletType efb = new BulletType(){{
                damage = speed = 0;
                keepVelocity = absorbable = hittable = collides = collidesTiles = collidesGround = collidesAir = false;
                hitEffect = despawnEffect = Fx.none;
            }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    for(int i = 0; i < 4; i ++){
                        float ang = 90 * i;
                        Draw.color(eccl);
                        Drawf.tri(b.x, b.y, 16 * b.fout(), 55 * b.fin(), ang);
                    }
                }
            };

            abilities.add(new DeathBullet(new fBullet(new BulletType(){{
                lifetime = 200;
                damage = speed = 0;

                keepVelocity = absorbable = hittable = collides = collidesTiles = collidesGround = collidesAir = false;
            }

                @Override
                public void update(Bullet b) {
                    if(b.timer.get(1 + 19 * b.fout())){
                        efb.create(b, b.x + Mathf.random(-30 * 8, 30 * 8), b.y + + Mathf.random(-30 * 8, 30 * 8), 0);
                    }
                }
            }, 1){{
                hitEffect = despawnEffect = new MultiEffect(
                        new Effect(200, e -> {
                            rand.setSeed(e.id);
                            float r = 45 * 8;
                            float pin = (1 - e.foutpow());
                            Lines.stroke(6 * e.foutpow(), eccl);
                            Lines.circle(e.x, e.y, r * pin);
                            for(int i = 0; i < 5; i++){
                                float a = rand.random(180);
                                float lx = EUGet.dx(e.x, r * pin, a);
                                float ly = EUGet.dy(e.y, r * pin, a);
                                Drawf.tri(lx, ly, 50 * e.foutpow(), (150 + rand.random(-50, 50)) * e.foutpow(), a + 180);
                            }
                            for(int i = 0; i < 5; i++){
                                float a = 180 + rand.random(180);
                                float lx = EUGet.dx(e.x, r * pin, a);
                                float ly = EUGet.dy(e.y, r * pin, a);
                                Drawf.tri(lx, ly, 50 * e.foutpow(), (150 + rand.random(-50, 50)) * e.foutpow(), a + 180);
                            }

                            if(!Vars.state.isPaused()) Effect.shake(5, 5, e.x, e.y);
                        }),
                        new ExplosionEffect(){{
                            lifetime = 180f;
                            waveStroke = 0f;
                            waveLife = 0f;
                            sparkColor = eccb;
                            smokeColor = Pal.gray;
                            smokes = 10;
                            smokeSize = 56;
                            sparks = 16;
                            smokeRad = sparkRad = 45 * 8;
                            sparkLen = 21f;
                            sparkStroke = 9f;
                        }}
                );
            }}, null){{
                display = false;
            }});

            abilities.add(new aboveDamage());

            PrismCtr btL = new PrismCtr();

            float[] ags = {-10, 10, -30, 30};
            for(float a : ags){
                weapons.add(new Weapon(){{
                    shootCone = 30;
                    inaccuracy = 15f;
                    reload = 240;
                    baseRotation = a;
                    rotate = true;
                    mirror = false;
                    x = y = 0;
                    shootSound = Sounds.cannon;

                    bullet = new BulletType(){{
                        speed = 5;
                        lifetime = 48;
                        rangeOverride = 59 * 8;
                        collides = collidesTiles = hittable = absorbable = keepVelocity = false;
                        splashDamage = 300;
                        splashDamageRadius = 10 * 8;
                        despawnEffect = new ExplosionEffect(){{
                            lifetime = 50f;
                            waveStroke = 10f;
                            waveLife = 12f;
                            waveColor = eccb;
                            sparkColor = smokeColor = eccl;
                            waveRad = splashDamageRadius;
                            smokeSize = 4f;
                            smokes = 7;
                            smokeSizeBase = 0f;
                            sparks = 8;
                            sparkRad = splashDamageRadius;
                            sparkLen = 7f;
                            sparkStroke = 2.4f;
                        }};
                        hitEffect = Fx.none;
                        fragOnHit = false;

                        fragBullets = 6;
                        fragBullet = btL.laser;
                    }


                        @Override
                        public void createFrags(Bullet b, float x, float y) {
                            btL.create(b, x, y, 0);
                        }

                        @Override
                        public void init(Bullet b) {
                            super.init(b);
                            float ex, ey;
                            ex = b.x + Angles.trnsx(b.rotation(), speed * b.lifetime);
                            ey = b.y + Angles.trnsy(b.rotation(), speed * b.lifetime);
                            new ChainLightningFade(b.lifetime + 30, 4 * b.lifetime, 5, eccl, 50, Fx.hitLancer).create(b, b.team, b.x, b.y, 0, -1, 1, 1, EUGet.pos(ex, ey));
                            new ChainLightningFade(b.lifetime + 30, 4 * b.lifetime, 5, eccl, 50, Fx.hitLancer).create(b, b.team, b.x, b.y, 0, -1, 1, 1, EUGet.pos(ex, ey));
                        }
                    };
                }

                    @Override
                    public void update(Unit unit, WeaponMount mount) {
                        if(a == -30 || a == 30){
                            if(unit instanceof bossEntity be && be.abilities.length > 0){
                                for(Ability ab : be.abilities){
                                    if(ab instanceof bossUnitAbi ba){
                                        if(!ba.isS1()) return;
                                    }
                                }
                            }
                        }
                        super.update(unit, mount);
                    }

                    @Override
                    public void addStats(UnitType u, Table t) {
                        super.addStats(u, t);
                        if(a == -30 || a == 30){
                            t.row();
                            t.add(Core.bundle.get("second-only"));
                            t.row();
                        }
                    }
                });
            }

            weapons.add(
                    new Weapon(){{
                        x = 160;
                        y = -56;
                        rotate = true;
                        rotateSpeed = 10;
                        shootCone = 360;
                        targetInterval = 6;
                        targetSwitchInterval = 6;
                        shootSound = Sounds.malignShoot;
                        float desTime = 30;
                        float desLife = 60;
                        float runTime = 20;
                        BulletType hole = new BulletType(){{
                            damage = 0;
                            speed = 6;
                            hittable = absorbable = collides = collidesTiles = keepVelocity = false;
                            lifetime = desLife + desTime;
                            despawnEffect = hitEffect = Fx.none;
                            bulletInterval = 6;
                            intervalDelay = runTime;
                            intervalRandomSpread = 150;
                            rangeOverride = 80 * 8;
                            intervalBullet = new BulletType(){{
                                homingPower = 0.4f;
                                damage = 70;
                                hitEffect = despawnEffect = new Effect(24, e -> {
                                    Draw.color(eccl);
                                    Angles.randLenVectors(e.id, 4, 13 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 8 * e.foutpow()));
                                });
                                speed = 10;
                                lifetime = 82;
                                trailLength = 15;
                                trailWidth = 2;
                                trailColor = eccl;
                                trailEffect = new Effect(24, e -> {
                                    Draw.color(e.color);
                                    Drawf.tri(
                                            e.x + Mathf.randomSeed(e.id, -3, 3),
                                            e.y + Mathf.randomSeed(e.id, -3, 3),
                                            5.8f * e.fout(),
                                            5.8f/2 * Mathf.sqrt3 * e.fout(),
                                            Mathf.randomSeed(e.id, 360) * e.fin()
                                    );
                                });
                                trailInterval = 1;
                                pierceArmor = true;
                                hitShake = despawnShake = 4;
                                hitSound = despawnSound = Sounds.shootAlt;
                                lightning = 1;
                                lightningColor = eccl;
                                lightningDamage = 15;
                                lightningLength = 10;
                            }

                                @Override
                                public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                    if(entity instanceof Unit u && !(entity instanceof bossEntity)){
                                        u.health -= damage * Math.max(3, u.armor);
                                    }
                                    super.hitEntity(b, entity, health);
                                }

                                @Override
                                public void init(Bullet b) {
                                    super.init(b);
                                    b.data = Units.closestTarget(b.team, b.x, b.y, lifetime * speed, ut -> ut.checkTarget(collidesAir, collidesGround) && ut.targetable(b.team));
                                    int i = 0;
                                    while(b.owner instanceof Bullet bu){
                                        b.owner = bu.owner;
                                        i++;
                                        if(i > 6) return;
                                    }
                                }

                                @Override
                                public void updateHoming(Bullet b) {
                                    if(homingPower > 0.0001f && b.time >= homingDelay){
                                        float realAimX = b.aimX < 0 ? b.x : b.aimX;
                                        float realAimY = b.aimY < 0 ? b.y : b.aimY;

                                        Teamc target;
                                        //home in on allies if possible
                                        if(heals()){
                                            target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                                                    e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                                                    t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                                            );
                                        }else{
                                            if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                                                target = b.aimTile.build;
                                            }else{
                                                target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                                                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                                                        t -> t != null && collidesGround && !b.hasCollided(t.id));
                                            }
                                        }

                                        if(target != null){
                                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
                                        } else {
                                            if(!(b.owner instanceof Unit u)) return;
                                            b.vel.setAngle(Angles.moveToward(b.rotation(), b.data instanceof Teamc tc ? b.angleTo(tc) : b.angleTo(u.mounts[4].aimX, u.mounts[4].aimY), homingPower * Time.delta * 50f));
                                        }
                                    }
                                }

                                @Override
                                public void draw(Bullet b) {
                                    super.draw(b);
                                    Draw.color(eccb);
                                    Drawf.tri(b.x, b.y, 6, 9, b.rotation());
                                }
                            };
                        }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * Math.max(0, 1 - b.time/runTime));
                            }

                            @Override
                            public void updateBulletInterval(Bullet b) {
                                if(b.time > b.lifetime - desTime + 6) return;
                                if(intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)){
                                    float ang;
                                    Teamc tc = Units.closestTarget(b.team, b.x, b.y, rangeOverride, ut -> ut.checkTarget(collidesAir, collidesGround) && ut.targetable(b.team));
                                    ang = tc != null ? b.angleTo(tc) : b.owner instanceof Unit u ? b.angleTo(u.mounts[4].aimX, u.mounts[4].aimY) : b.rotation();
                                    for(int i = 0; i < intervalBullets; i++){
                                        intervalBullet.create(b, b.x, b.y, ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f)/2f) * intervalSpread));
                                    }
                                }
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                float fin = b.time/(b.lifetime - desTime);
                                float fout = 1 - (b.time - desLife)/(b.lifetime - desLife);
                                float finOver = b.time < desLife ? Math.min(1, fin * 5) : fout;
                                Draw.color(Pal.techBlue);
                                Fill.circle(b.x, b.y, 12 * finOver);
                                for(int i : Mathf.zeroOne){
                                    Drawf.tri(b.x, b.y, 8 * finOver, 20, (b.rotation() - 90 + i * 180) * finOver);
                                }

                                Draw.color(eccl);
                                Fill.circle(b.x, b.y, 9 * finOver);
                            }
                        };

                        bullet = new BulletType(){{
                            damage = 0;
                            speed = 2;
                            hittable = absorbable = collides = collidesTiles = keepVelocity = false;
                            shootEffect = EUFx.PlanetaryArray(72, 4, 64, eccl, 18, 6, 0.6f);
                            lifetime = 60;
                            intervalBullet = hole;
                            bulletInterval = 15;
                            intervalRandomSpread = 270;
                            hitEffect = despawnEffect = Fx.none;
                            rangeOverride = 8 * 100;
                        }};
                        reload = 60;
                    }}
            );

            weapons.add(
                    new Weapon(name("regency-w3")){{
                        x = 67;
                        rotate = true;
                        rotateSpeed = 2f;
                        reload = 60;
                        shootSound = Sounds.missileLarge;
                        xRand = 4;
                        shoot.shots = 3;
                        shoot.shotDelay = 6;
                        bullet = new BulletType(){{
                            damage = 400;
                            pierceArmor = true;
                            trailWidth = 7.5f;
                            trailLength = 10;
                            trailColor = eccl;
                            speed = 40;
                            lifetime = 8 * 8;
                            rangeOverride = 100 * 8;
                            shootEffect = new Effect(30, e -> {
                                Draw.color(eccl);
                                Angles.randLenVectors(e.id, 5, 32 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 20 * e.foutpow()));
                            });
                            hitShake = despawnShake = 7;
                            hitSound = despawnSound = Sounds.bang;
                            hitEffect = despawnEffect = new MultiEffect(
                                    new Effect(90, e -> {
                                        Draw.color(trailColor);
                                        rand.setSeed(e.id);
                                        for(int i = 0; i < 5; i++){
                                            Drawf.tri(e.x, e.y, 19, 120 * e.foutpow(), e.rotation + rand.random(-60, 60));
                                        }
                                    }),
                                    shootEffect
                            );
                        }

                            @Override
                            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                if(entity instanceof Unit u && !(entity instanceof bossEntity)){
                                    u.health -= damage * Math.max(2, u.armor);
                                }
                                super.hitEntity(b, entity, health);
                            }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * b.fin() * b.fin());
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(trailColor);
                                float x1, x2, y1, y2;
                                Tmp.v1.set(0, 20).rotate(b.rotation() - 90);
                                x1 = b.x + Tmp.v1.x;
                                y1 = b.y + Tmp.v1.y;
                                for(int i : Mathf.signs){
                                    Tmp.v1.set(14 * i, -13).rotate(b.rotation() - 90);
                                    x2 = b.x + Tmp.v1.x;
                                    y2 = b.y + Tmp.v1.y;
                                    Fill.tri(x1, y1, x2, y2, b.x, b.y);
                                }
                            }
                        };
                    }}
            );

            weapons.add(
                    new ReRotPointDefenseWeapon(name("regency-w2")){{
                        x = 50;
                        y = 52;
                        reload = 5;
                        targetInterval = targetSwitchInterval = 6;
                        beamEffect = EUFx.chainLightningFade(20, 2.5f, 8);//Fx.chainLightning
                        color = eccl;
                        bullet = new BulletType(){{
                            rangeOverride = 60 * 8;
                            damage = 80;
                        }};
                        shootSound = Sounds.spark;
                    }},
                    new ReRotPointDefenseWeapon(name("regency-w2")){{
                        x = 55;
                        y = 34;
                        reload = 2;
                        targetInterval = targetSwitchInterval = 6;
                        beamEffect = EUFx.chainLightningFade(20, 2.5f, 8);
                        color = eccl;
                        bullet = new BulletType(){{
                            rangeOverride = 60 * 8;
                            damage = 80;
                        }};
                        shootSound = Sounds.spark;
                    }

                        @Override
                        protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
                            if(unit instanceof bossEntity be && be.abilities.length > 0){
                                for(Ability ab : be.abilities){
                                    if(ab instanceof bossUnitAbi ba){
                                        if(!ba.isS1()) return null;
                                    }
                                }
                            }
                            return super.findTarget(unit, x, y, range, air, ground);
                        }

                        @Override
                        public void addStats(UnitType u, Table t) {
                            super.addStats(u, t);
                            t.row();
                            t.add(Core.bundle.get("second-only"));
                            t.row();
                        }
                    }
            );

            weapons.add(new Weapon(name("regency-w1")){{
                x = 55;
                y = -60;
                reload = 30;
                rotate = true;
                rotateSpeed = 5;
                autoTarget = true;
                controllable = false;
                shootSound = Sounds.shotgun;
                targetInterval = targetSwitchInterval = 10;
                bullet = new BulletType(){{
                    damage = 600;
                    collidesTiles = hittable = absorbable = false;
                    speed = 50;
                    lifetime = 200 * 8 / speed;
                    trailEffect = new Effect(40, e -> {
                        Draw.color(e.color);
                        rand.setSeed(e.id);
                        float fin = 1 - Mathf.curve(e.fout(), 0, 0.85f);
                        Tmp.v1.set((rand.chance(0.5f) ? 20 : -20) * (rand.chance(0.2f) ? 0 : fin), 0).rotate(e.rotation - 90);
                        float ex = e.x + Tmp.v1.x;
                        float ey = e.y + Tmp.v1.y;
                        Draw.rect(name("aim-shoot"), ex, ey, 80 * e.fout(), 80 * e.fout(), e.rotation - 90);
                    });
                    trailInterval = 0.2f;
                    trailColor = eccl;
                    trailRotation = true;
                    trailWidth = 8;
                    trailLength = 12;
                    hitEffect = despawnEffect = new Effect(90, e -> {
                        float fin = Mathf.curve(e.fin(), 0, 0.09f);
                        float fout = Mathf.curve(e.fout(), 0, 0.95f);
                        for(int i = 0; i < 4; i ++){
                            float ang = 90 * i;
                            Draw.z(Layer.effect);
                            Draw.color(eccb);
                            Drawf.tri(e.x, e.y, 19 * fout, 90 * fin, ang);
                            Drawf.tri(e.x, e.y, 15 * fout, 72 * fin, 45 + ang);

                            Draw.color(Color.black);
                            Draw.z(Layer.effect + 1);
                            Drawf.tri(e.x, e.y, 15 * fout, 82 * fin, ang);
                            Drawf.tri(e.x, e.y, 11 * fout, 66 * fin, 45 + ang);
                        }
                    });
                    hitSound = Sounds.laser;
                    hitShake = despawnShake = 10;
                }

                    @Override
                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                        if(entity instanceof Unit u && !(entity instanceof bossEntity) && u.type != null){
                            u.health -= (damage * (u.type.hitSize/10f + 1) * ((u.type.armor)/10f + 1) + u.maxHealth * 0.2f);
                        }
                        super.hitEntity(b, entity, health);
                    }

                    @Override
                    public void updateHoming(Bullet b) {
                        if(b.time >= homingDelay){
                            float realAimX = b.aimX < 0 ? b.x : b.aimX;
                            float realAimY = b.aimY < 0 ? b.y : b.aimY;

                            Teamc target;
                            //home in on allies if possible
                            if(heals()){
                                target = Units.closestTarget(null, realAimX, realAimY, speed * lifetime,
                                        e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                                        t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                                );
                            }else{
                                if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                                    target = b.aimTile.build;
                                }else{
                                    target = Units.closestTarget(b.team, realAimX, realAimY, speed * lifetime,
                                            e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                                            t -> t != null && collidesGround && !b.hasCollided(t.id));
                                }
                            }

                            if(target != null){
                                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), 5 * Time.delta));
                            }
                        }
                    }

                    @Override
                    public void draw(Bullet b) {
                        super.draw(b);
                        Draw.color(eccl);
                        Draw.z(Layer.bullet);
                        Drawf.tri(b.x, b.y, 30, 40, b.rotation());
                    }
                };

                mountType = reRotMount::new;
            }

                @Override
                protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
                    return Units.bestEnemy(unit.team, x, y, range + Math.abs(shootY), u -> u.checkTarget(air, ground) && !(u instanceof bossEntity) && u.type != null && (u.type.hitSize >= 50 || u.type.armor >= 50 || u.maxHealth >= 60000), UnitSorts.strongest);
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
            });

            weapons.add(
                    new Weapon(){{
                        x = 0;
                        y = 30;
                        mirror = false;
                        shootCone = 20;
                        chargeSound = Sounds.lasercharge;
                        reload = 120;
                        shootSound = Sounds.missileLaunch;
                        float chargeTime = 80;
                        bullet = new BulletType(){{
                            damage = splashDamage = 600;
                            splashDamageRadius = 24 * 8;
                            pierce = true;
                            pierceArmor = true;
                            pierceBuilding = false;
                            hitSize = 40;
                            speed = 80;
                            lifetime = 5f * 8;
                            rangeOverride = 120 * 8;
                            trailLength = 9;
                            trailWidth = 16;
                            trailColor = eccl;
                            trailEffect = EUFx.ellipse(64, 5, 24, 24, eccl);
                            trailInterval = 6;
                            trailRotation = true;
                            chargeEffect = new Effect(chargeTime, 100f, e -> {
                                color(eccl);
                                stroke(e.fin() * 4f);
                                Lines.circle(e.x, e.y, 10f + e.fout() * 150f);

                                Fill.circle(e.x, e.y, e.fin() * 26);
                                Drawf.tri(e.x, e.y, 40 * e.fin(), 50 * e.fin(), e.rotation);

                                randLenVectors(e.id, 20, 60f * e.fout(), (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, e.fin() * 12f);
                                    Drawf.light(e.x + x, e.y + y, e.fin() * 20f, Pal.heal, 0.7f);
                                });

                                color();

                                Fill.circle(e.x, e.y, e.fin() * 15);
                                Drawf.light(e.x, e.y, e.fin() * 30f, Pal.heal, 0.7f);
                            }).followParent(true).rotWithParent(true);
                            hitEffect = new Effect(30, e -> {
                                Draw.color(eccl);
                                Angles.randLenVectors(e.id, 7, 55 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 33 * e.foutpow()));
                            });
                            despawnEffect = new MultiEffect(
                                    new ExplosionEffect(){{
                                        lifetime = 100f;
                                        waveStroke = 8f;
                                        waveLife = 20f;
                                        sparkColor = eccb;
                                        smokes = 8;
                                        smokeSize = 10;
                                        smokeRad = splashDamageRadius;
                                        sparks = 8;
                                        sparkRad = splashDamageRadius;
                                        sparkLen = 12f;
                                        sparkStroke = 3f;
                                    }},
                                    EUFx.airAsh(100, splashDamageRadius, 24, eccb, 5, 36),
                                    EUFx.diffEffect(100, 6, splashDamageRadius * 0.8f, 10, 140, 40, 40, eccl, -1)
                            );

                            hitShake = despawnShake = 14;
                            hitSound = despawnSound = Sounds.explosionbig;
                        }
                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * b.fin() * b.fin());
                            }

                            @Override
                            public void draw(Bullet b) {
                                super.draw(b);
                                Draw.color(trailColor);
                                float x1, x2, y1, y2;
                                Tmp.v1.set(0, 50).rotate(b.rotation() - 90);
                                x1 = b.x + Tmp.v1.x;
                                y1 = b.y + Tmp.v1.y;
                                for(int i : Mathf.signs){
                                    Tmp.v1.set(30 * i, -30).rotate(b.rotation() - 90);
                                    x2 = b.x + Tmp.v1.x;
                                    y2 = b.y + Tmp.v1.y;
                                    Fill.tri(x1, y1, x2, y2, b.x, b.y);
                                }
                            }

                            @Override
                            public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
                                despawnEffect.at(b);
                                super.hitTile(b, build, x, y, initialHealth, direct);
                            }

                            @Override
                            public void hitEntity(Bullet b, Hitboxc entity, float health) {
                                if(entity instanceof Unit u && !(entity instanceof bossEntity)) u.health -= damage * Math.max(2f, u.armor);
                                super.hitEntity(b, entity, health);
                            }
                        };
                        shoot.firstShotDelay = chargeTime;
                    }}
            );

            //alwaysUnlocked = true;

            parts.add(
                    new BowHalo(){{
                        x = y = 0;
                        radius = 25 * 8;
                        w1 = 18;
                        h1 = 20;
                        w2 = 0;
                        h2 = 0;
                        color = eccb;
                        progress = PartProgress.constant(1);
                        rotAb = sinWave = false;
                    }

                        @Override
                        public void draw(PartParams params) {
                            super.draw(params);
                            float rot = params.rotation - 90;
                            float bx = params.x, by = params.y;
                            Draw.z(layer);
                            Draw.color(color);

                            for(int k = 0; k < 6; k++){
                                Tmp.v1.set(x, y + (2 - k)).rotate(rot);
                                float px = bx + Tmp.v1.x, py = by + Tmp.v1.y;
                                float sin = Mathf.absin(Time.time + 60/2f * k, 15, 48);
                                for(int i = 0; i < 2; i++){
                                    float angle = i* 360f / 2;
                                    Drawf.tri(px + Angles.trnsx(angle, radius), py + Angles.trnsy(angle, radius), 18, (sin) * 8, angle);
                                }
                            }
                        }
                    },
                    new DrawPart(){
                        @Override
                        public void draw(PartParams params) {
                            Draw.z(Layer.effect);
                            Draw.color(eccl);
                            for(int i = 0; i < 4; i++){
                                float r = i * 90 + Time.time * 2;
                                float dx = EUGet.dx(params.x, 25 * 8, r), dy = EUGet.dy(params.y, 25 * 8, r);
                                Drawf.tri(dx, dy, 20, 18, r + 180);
                            }
                            for(int i = 0; i < 4; i++){
                                float r = i * 90 - Time.time * 2;
                                float dx = EUGet.dx(params.x, 60 * 8, r), dy = EUGet.dy(params.y, 60 * 8, r);
                                Drawf.tri(dx, dy, 26, 20, r + 180);
                            }
                        }

                        @Override
                        public void load(String s) {

                        }
                    }
            );
        }};
    }

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

            immunities = ObjectSet.with(EUStatusEffects.speedDown, EUStatusEffects.poison, StatusEffects.sapped, EUStatusEffects.awsl);

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

            immunities = ObjectSet.with(EUStatusEffects.speedDown, EUStatusEffects.poison, StatusEffects.sapped, StatusEffects.wet, StatusEffects.electrified, EUStatusEffects.awsl);

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

            immunities = ObjectSet.with(StatusEffects.wet, StatusEffects.sapped, EUStatusEffects.awsl);
            
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
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting, StatusEffects.wet, EUStatusEffects.awsl);
            ammoType = new ItemAmmoType(Items.pyratite);

            abilities.add(new UnitSpawnAbility(UnitTypes.crawler, 60*10, 17, -27.5f), new UnitSpawnAbility(UnitTypes.crawler, 60*10, -17, -27.5f));
            abilities.add(new EnergyFieldAbility(220f, 90f, 192f){{
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
                            damage = 980;
                            pointEffectSpace = 60f;
                            pointEffect = Fx.railTrail;
                            pierce = true;
                            pierceBuilding = true;
                            pierceCap = 6;
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

            buildSpeed = 12;

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
                        bullet = new antiMissile( 30 * 8, name("anti"));
                        y = -37;
                        x = 0;
                        loadSpeed = -1.5f;
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

            immunities.addAll(StatusEffects.unmoving, StatusEffects.burning, StatusEffects.sapped, EUStatusEffects.awsl);
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
            Color escAlpha = esc.cpy().a(0.8f);
            Color escDark = esc.cpy().mul(Pal.lightishGray);
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
                            damage = 100;
                            speed = 15;
                            lifetime = 24;
                            homingRange = speed * lifetime;
                            homingDelay = 3;
                            homingPower = 11;
                            trailLength = 5;
                            trailWidth = 4;
                            trailColor = esc;
                            trailRotation = true;
                            trailEffect =  new Effect(24, e -> {
                                Draw.color(escAlpha);
                                Angles.randLenVectors(e.id, 1, 6f * e.foutpow(), e.rotation, 180, (x, y) -> {
                                    Drawf.tri(e.x + x, e.y + y, 4.5f * e.foutpow(), 16, e.rotation);
                                    Drawf.tri(e.x + x, e.y + y, 4.5f * e.foutpow(), 40 * e.foutpow(), e.rotation + 180);
                                });
                            });
                            trailInterval = 1f;
                            despawnEffect = new Effect(30, e -> {
                                Draw.color(escAlpha);
                                Angles.randLenVectors(e.id, 5, 20 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 10 * e.foutpow()));
                            });
                            hitEffect = new Effect(72, e -> {
                                Draw.color(esc);
                                rand.setSeed(e.id);
                                for(int i = 0; i < 5; i++){
                                    Drawf.tri(e.x, e.y, 12, 72 * e.foutpow(), e.rotation + rand.random(-45, 45));
                                }
                            });
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
                            damage = 310;
                            splashDamage = 390;
                            splashDamageRadius = 13 * 8f;
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
                            fb.damage = 22.5f;
                            intervalBullet = fb;
                            intervalBullets = 4;
                            bulletInterval = 6;
                            intervalDelay = 6;
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
//                            trailColor = escDark;
//                            trailLength = 8;
//                            trailWidth = 12;
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

                            @Override
                            public void removed(Bullet b) {

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
                                damage = splashDamage = 130;
                                splashDamageRadius = 9f * 8;
                                keepVelocity = false;
                                homingRange = 45 * 8;
                                homingPower = 0.3f;
                                homingDelay = 10;
                                speed = 8;
                                lifetime = 48;
                                trailColor = Color.valueOf("feb380");
                                trailWidth = 5f;
                                trailLength = 9;
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
                                }}, EUFx.airAsh(72, splashDamageRadius, splashDamageRadius/5, escDark, 2, 30));
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
                                    for(int i = 0; i < 3; i++){
                                        eb.create(b, b.team, x, y, a + i * (360/9f), -1, 1,  1, i * 5f);
                                        eb.create(b, b.team, x, y, a + i * (360/9f) + 120, -1, 1,  1, i * 5f);
                                        eb.create(b, b.team, x, y, a + i * (360/9f) + 240, -1, 1,  1, i * 5f);
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

            immunities.addAll(StatusEffects.wet, StatusEffects.freezing, StatusEffects.sapped, StatusEffects.disarmed, StatusEffects.electrified, EUStatusEffects.speedDown, EUStatusEffects.awsl);
            
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
                            damage = splashDamage = 130;
                            splashDamageRadius = spr;
                            despawnEffect = hitEffect = de;
                            speed = 6;
                            lifetime = 90;
                            homingPower = 8;
                            trailEffect = new Effect(18, e -> {
                                if(!(e.data instanceof Float time)) return;
                                Draw.color(Pal.suppress);
                                float sin = Mathf.sin(time, 5, 5.5f);
                                float ex = EUGet.dx(e.x, sin, e.rotation + 90),
                                        ey = EUGet.dy(e.y, sin, e.rotation + 90);
                                Fill.circle(ex, ey, 3.4f * e.foutpow());
                                ex = EUGet.dx(e.x, sin, e.rotation - 90);
                                ey = EUGet.dy(e.y, sin, e.rotation - 90);
                                Fill.circle(ex, ey, 3.4f * e.foutpow());
                            }).layer(Layer.bullet - 0.0001f);
                            trailRotation = true;
                            trailInterval = 0.1f;
                            trailWidth = 4;
                            trailLength = 13;
                            trailColor = Pal.suppress;

                            autoHoming = true;

                            buildingDamageMultiplier = 0.7f;
                        }

                            @Override
                            public void update(Bullet b) {
                                super.update(b);
                                b.initVel(b.rotation(), speed * Math.min(b.finpow() * 2, 1));
                            }

                            @Override
                            public void updateTrailEffects(Bullet b) {
                                if(b.timer(0, trailInterval)){
                                    trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor, b.time);
                                }
                            }
                        };
                        bullet = new BulletType(){{
                            damage = splashDamage = 130;
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
                            damage = 100;
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
                            damage = splashDamage = 450;
                            splashDamageRadius = 11f * 8;
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

                                if(!Vars.state.isPaused()) Effect.shake(2, 2, b);
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
                            splashDamage = 150;
                            splashDamageRadius = 9 * 8;
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
                                splashDamage = 30;
                                splashDamageRadius = 5.5f * 8;
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

            immunities.addAll(StatusEffects.wet, StatusEffects.unmoving, StatusEffects.disarmed, StatusEffects.slow, EUStatusEffects.awsl);

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
                            damage = 90;
                            length = 100;
                            lifetime = 20;
                            laserAbsorb = false;
                            pierceArmor = true;
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
                                    Sounds.laserblast.at(xx, yy, 0.5f, 0.25f);
                                    Effect.shake(5, 5, xx, yy);
                                }
                            }
                        };
                        BulletType fff = new fBullet(crackFrag, 10);

                        bullet = new BulletType(){{
                            lifetime = 22;
                            speed = 20;
                            splashDamage = 250;
                            splashDamageRadius = 8.5f * 8;
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
                            damage = 900/12f;
                            lifetime = 150;
                            colors = new Color[]{Pal.techBlue, Pal.lancerLaser, Color.white};
                            incendAmount = 0;
                            incendChance = -1;
                            status = StatusEffects.shocked;
                            width = 6;
                            length = 30 * 8;
                            pierceCap = 4;
                            pierceArmor = true;
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
                                new arcanaPart()
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

                        bullet = new BasicBulletType(6, 50){{
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
