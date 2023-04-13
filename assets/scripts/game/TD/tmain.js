//Will be added in version 1.4 or 1.3.30 onwards
const lib = require("blib");
//const ability = require("other/ability");
const healthDisplay = lib.getClass("ExtraUtilities.worlds.entity.ability.healthDisplay");
//const planet = require("game/challengeMap/cmain");

const walkFloor = extend(Floor, "walkFloor", {});
walkFloor.solid = false;
walkFloor.hasShadow = false;
walkFloor.variants = 0;
walkFloor.placeableOn = false;

const walkFloorWater = new Floor("walkfloor-water");
Object.assign(walkFloorWater, {
    speedMultiplier : 0.2,
    variants : 0,
    isLiquid : true,
    status : StatusEffects.wet,
    statusDuration : 90,
    drownTime : 120,
    cacheLayer : CacheLayer.water,
    albedo : 0.5,
    placeableOn : false,
});

const landWaterCross = new Floor("land-water-cross");
Object.assign(landWaterCross, {
    speedMultiplier : 1.2,
    variants : 0,
    isLiquid : true,
    status : StatusEffects.none,
    placeableOn : false,
});

const basicDamage = new BombBulletType(0, 0, "clear");
basicDamage.collidesAir = true;
basicDamage.killShooter = true;
basicDamage.lifetime = 10;
basicDamage.speed = 1;
basicDamage.splashDamageRadius = 58;
basicDamage.instantDisappear = true;
basicDamage.splashDamage = 190;
basicDamage.hitEffect = Fx.pulverize;

const midDamage = new BombBulletType(0, 0, "clear");
midDamage.collidesAir = true;
midDamage.killShooter = true;
midDamage.lifetime = 10;
midDamage.speed = 1;
midDamage.splashDamageRadius = 58;
midDamage.instantDisappear = true;
midDamage.splashDamage = 430;
midDamage.hitEffect = Fx.flakExplosion;

const hugeDamage = new BombBulletType(0, 0, "clear");
hugeDamage.collidesAir = true;
hugeDamage.killShooter = true;
hugeDamage.lifetime = 10;
hugeDamage.speed = 1;
hugeDamage.splashDamageRadius = 58;
hugeDamage.instantDisappear = true;
hugeDamage.splashDamage = 1400;
hugeDamage.hitEffect = new Effect(60, cons(e => {
    Draw.color(Pal.meltdownHit);
    Lines.stroke(e.fout() * 2);
    Lines.circle(e.x, e.y, 4 + e.finpow() * 65);
    Draw.color(Pal.meltdownHit);
    for(var i = 0; i < 4; i++){
        Drawf.tri(e.x, e.y, 6, 100 * e.fout(), i * 90);
    }
    Draw.color();
    for(var i = 0; i < 4; i++){
        Drawf.tri(e.x, e.y, 3, 35 * e.fout(), i * 90);
    }
}));

const enemy1 = new UnitType("enemy1");
enemy1.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy1.aiController = prov(() => new SuicideAI());
enemy1.targetAir = false;
enemy1.speed = 0.9;
enemy1.hitSize = 8;
enemy1.health = 350;
enemy1.mechSideSway = 0.25;
enemy1.range = 28;
enemy1.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = basicDamage;
        return w;
    })()
);
enemy1.armor = 1;
enemy1.abilities.add(new healthDisplay(10, 15, 2));
enemy1.alwaysUnlocked = true;

const enemy2 = new UnitType("enemy2");
enemy2.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy2.aiController = prov(() => new SuicideAI());
enemy2.targetAir = false;
enemy2.speed = 0.7;
enemy2.hitSize = 9;
enemy2.health = 210;
enemy2.mechSideSway = 0.25;
enemy2.range = 28;
enemy2.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = basicDamage;
        return w;
    })()
);
enemy2.armor = 6;
enemy2.abilities.add(new ShieldRegenFieldAbility(20, 160, 60 * 4, 60));
enemy2.abilities.add(new healthDisplay(12, 15, 2));
enemy2.abilities.add(new ForceFieldAbility(36, 1, 180, 60 * 10));
enemy2.alwaysUnlocked = true;

const enemy3 = new UnitType("enemy3");
enemy3.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy3.aiController = prov(() => new SuicideAI());
enemy3.targetAir = false;
enemy3.speed = 0.6;
enemy3.hitSize = 16;
enemy3.health = 1000;
enemy3.mechSideSway = 0.25;
enemy3.range = 28;
enemy3.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = basicDamage;
        return w;
    })()
);
enemy3.armor = 4;
enemy3.abilities.add(new RepairFieldAbility(60, 60 * 3, 64));
enemy3.abilities.add(new healthDisplay(14, 22, 3));
enemy3.alwaysUnlocked = true;

const boss1 = new UnitType("boss1");
boss1.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
boss1.aiController = prov(() => new SuicideAI());
boss1.targetAir = false;
boss1.speed = 0.3;
boss1.hitSize = 28;
boss1.health = 13000;
boss1.mechSideSway = 0.25;
boss1.range = 28;
boss1.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = midDamage;
        return w;
    })()
);
boss1.armor = 15;
boss1.abilities.add(new UnitSpawnAbility(enemy1, 60 *6, -9.5, -11.5), new UnitSpawnAbility(enemy2, 60 * 8, 9.5, -11.5));
boss1.abilities.add(new RepairFieldAbility(60, 60 * 3, 80));
boss1.abilities.add(new healthDisplay(18, 30, 4));
boss1.abilities.add(new ForceFieldAbility(80, 2.2, 600, 60 * 15));
boss1.alwaysUnlocked = true;

const boss2 = new UnitType("boss2");
boss2.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
boss2.aiController = prov(() => new SuicideAI());
boss2.targetAir = false;
boss2.speed = 0.35;
boss2.hitSize = 30;
boss2.health = 48000;
boss2.mechSideSway = 0.25;
boss2.range = 28;
boss2.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = hugeDamage;
        return w;
    })()
);
boss2.armor = 12;
boss2.abilities.add(new healthDisplay(20, 36, 5));
boss2.alwaysUnlocked = true;

const AI = require("other/unitAI");

const flyEnemy1 = new UnitType("flyEnemy1");
flyEnemy1.constructor = prov(() => extend(UnitTypes.quad.constructor.get().class, {}));
flyEnemy1.aiController = prov(() => AI.TDFlyingAI());
//flyEnemy1.drag = 0.018;
flyEnemy1.accel = 0.4;
flyEnemy1.targetAir = false;
flyEnemy1.boostMultiplier = 1.2;
flyEnemy1.canBoost = true;
flyEnemy1.speed = 0.3;
flyEnemy1.hitSize = 18;
flyEnemy1.health = 10000;
flyEnemy1.mechSideSway = 0.25;
flyEnemy1.range = 28;
flyEnemy1.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = midDamage;
        return w;
    })()
);
flyEnemy1.armor = 5;
flyEnemy1.abilities.add(new healthDisplay(30, 36, 5));
flyEnemy1.alwaysUnlocked = true;

const navalEnemy1 = new UnitType("navalEnemy1");
navalEnemy1.constructor = prov(() => extend(UnitTypes.risso.constructor.get().class, {}));
navalEnemy1.aiController = prov(() => new SuicideAI());
navalEnemy1.drag = 0.13;
navalEnemy1.accel = 0.4;
navalEnemy1.targetAir = false;
navalEnemy1.speed = 0.3;
navalEnemy1.hitSize = 18;
navalEnemy1.health = 9500;
navalEnemy1.range = 28;
navalEnemy1.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = midDamage;
        return w;
    })()
);
navalEnemy1.armor = 10;
navalEnemy1.abilities.add(new healthDisplay(30, 30, 4));
flyEnemy1.alwaysUnlocked = true;

const slime = new UnitType("slime");
slime.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
slime.aiController = prov(() => new SuicideAI());
slime.targetAir = false;
slime.speed = 0.4;
slime.hitSize = 20;
slime.health = 32000;
slime.mechSideSway = 0.25;
slime.range = 28;
slime.weapons.add(
    (() => {
        const w = new Weapon();
        w.reload = 12;
        w.shootCone = 180;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.explosion;
        w.bullet = midDamage;
        return w;
    })()
);
slime.armor = 9;
slime.abilities.add(new healthDisplay(20, 36, 5));
slime.abilities.add(new SpawnDeathAbility(enemy1, 4, 2), new SpawnDeathAbility(enemy2, 4, 2));
slime.alwaysUnlocked = true;

/*const TD = new JavaAdapter(Planet, {
    load(){
        this.meshLoader = prov(() => new SunMesh(TD, 4, 6, 2.8, 1.4, 1.8, 1.4, 1.1,
            Color.valueOf("5ecdc6"),
            Color.valueOf("5ebbcd"),
            Color.valueOf("5ecda6"),
            Color.valueOf("5ecd86"),
            Color.valueOf("5ecd65"),
            Color.valueOf("5e9acd")
        ));
        this.super$load();
    }
}, "TD", Planets.sun, 1);
lib.setPlanet(TD, 1);
TD.generator = new SerpuloPlanetGenerator();
TD.atmosphereColor = Color.valueOf("5ecd65");
TD.accessible = true;
TD.orbitRadius = 12;
TD.sectorApproxRadius = 0.5;
TD.atmosphereRadIn = 0.04;
TD.atmosphereRadOut = 0.3;
TD.startSector = 2;
TD.meshLoader = prov(() => new SunMesh(TD, 4, 6, 2.8, 1.4, 1.8, 1.4, 1.1,
    Color.valueOf("5ecdc6"),
    Color.valueOf("5ebbcd"),
    Color.valueOf("5ecda6"),
    Color.valueOf("5ecd86"),
    Color.valueOf("5ecd65"),
    Color.valueOf("5e9acd")
));
TD.alwaysUnlocked = true;

const pd = new SectorPreset("preparation", TD, 25);
pd.alwaysUnlocked = true;
pd.captureWave = 1;
pd.difficulty = 1;
exports.pd = pd;

const TD1 = new SectorPreset("TD1", TD, 2);
//TD1.alwaysUnlocked = true;
TD1.captureWave = 40;
TD1.difficulty = 6;
exports.TD1 = TD1;

const TD2 = new SectorPreset("TD2", TD, 22);
TD2.captureWave = 102;
TD2.difficulty = 9;
exports.TD2 = TD2;

const TD3 = new SectorPreset("TD3", TD, 23);
TD3.captureWave = 120;
TD3.difficulty = 6;
exports.TD3 = TD3;

const TD4 = new SectorPreset("TD4", TD, 10);
TD4.captureWave = 102;
TD4.difficulty = 9;
exports.TD4 = TD4;

const TD5 = new SectorPreset("TD5", TD, 20);
TD5.captureWave = 102;
TD5.difficulty = 9;
exports.TD5 = TD5;

const TD6 = new SectorPreset("TD6", TD, 17);
TD6.captureWave = 102;
TD6.difficulty = 9;
exports.TD6 = TD6;

const TD7 = new SectorPreset("TD7", TD, 18);
TD7.captureWave = 102;
TD7.difficulty = 10;
exports.TD7 = TD7;

const TD8 = new SectorPreset("TD8", TD, 7);
TD8.captureWave = 41;
TD8.difficulty = 6;
exports.TD8 = TD8;

const TD9 = new SectorPreset("TD9", TD, 11);
TD9.captureWave = 51;
TD9.difficulty = 6;
exports.TD9 = TD9;

const TD10 = new SectorPreset("TD10", TD, 16);
TD10.captureWave = 100;
TD10.difficulty = 10;
exports.TD10 = TD10;

const TD11 = new SectorPreset("TD11", TD, 0);
TD11.captureWave = 100;
TD11.difficulty = 9;
exports.TD11 = TD11;

const TD12 = new SectorPreset("TD12", TD, 6);
TD12.captureWave = 101;
TD12.difficulty = 7;
exports.TD12 = TD12;

const TD13 = new SectorPreset("TD13", TD, 24);
TD13.captureWave = 101;
TD13.difficulty = 8;
exports.TD13 = TD13;

const TD14 = new SectorPreset("TD14", TD, 26);
TD14.captureWave = 101;
TD14.difficulty = 8;
exports.TD14 = TD14;*/