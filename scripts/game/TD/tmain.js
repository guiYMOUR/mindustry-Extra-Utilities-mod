//Will be added in version 1.4 or 1.3.30 onwards
const ability = require("other/ability");
const planet = require("game/challengeMap/cmain");

const walkFloor = extendContent(Floor, "walkFloor", {});
walkFloor.size = 1;
walkFloor.variants = 0;
walkFloor.placeableOn = false;

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

const enemy1 = extendContent(UnitType, 'enemy1', {});
enemy1.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy1.defaultController = prov(() => new SuicideAI());
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
enemy1.abilities.add(ability.healthDisplay(10, 15, 2));

const enemy2 = extendContent(UnitType, 'enemy2', {});
enemy2.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy2.defaultController = prov(() => new SuicideAI());
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
enemy2.abilities.add(ability.healthDisplay(12, 15, 2));
enemy2.abilities.add(new ForceFieldAbility(36, 1, 180, 60 * 10));

const enemy3 = extendContent(UnitType, 'enemy3', {});
enemy3.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
enemy3.defaultController = prov(() => new SuicideAI());
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
enemy3.abilities.add(ability.healthDisplay(14, 22, 3));

const boss1 = extendContent(UnitType, 'boss1', {});
boss1.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
boss1.defaultController = prov(() => new SuicideAI());
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
boss1.abilities.add(ability.healthDisplay(18, 30, 4));
boss1.abilities.add(new ForceFieldAbility(80, 2.2, 600, 60 * 15));

const boss2 = extendContent(UnitType, 'boss2', {});
boss2.constructor = prov(() => extend(UnitTypes.crawler.constructor.get().class, {}));
boss2.defaultController = prov(() => new SuicideAI());
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
boss2.abilities.add(ability.healthDisplay(20, 36, 5));

const TD = new JavaAdapter(Planet, {
    load() {
        this.meshLoader = prov(() => new SunMesh(TD, 4, 6, 2.8, 1.4, 1.8, 1.4, 1.1,
        Color.valueOf("5ecdc6"),
        Color.valueOf("5ebbcd"),
        Color.valueOf("5ecda6"),
        Color.valueOf("5ecd86"),
        Color.valueOf("5ecd65"),
        Color.valueOf("5e9acd")
        ));
        this.super$load();
    },
}, "TD", planet.challenge, 1, 1);
TD.generator = new SerpuloPlanetGenerator();
TD.atmosphereColor = Color.valueOf("5ecd65");
TD.accessible = true;
TD.orbitRadius = 4;
TD.sectorApproxRadius = 0.5;
TD.atmosphereRadIn = 0.04;
TD.atmosphereRadOut = 0.3;
TD.startSector = 2;

const TD1 = new SectorPreset("TD1", TD, 2);
TD1.alwaysUnlocked = true;
TD1.captureWave = 50;
TD1.difficulty = 3;
exports.TD1 = TD1;

const TD2 = new SectorPreset("TD2", TD, 22);
TD2.captureWave = 80;
TD2.difficulty = 6;
exports.TD2 = TD2;