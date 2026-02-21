var ability = require("other/ability");
const bullets = require("other/bullets");

var spawnTime = 60 * 14;

const air = extend(PointBulletType, {
    despawned(b){
        this.super$despawned(b);
        new Effect(15, cons(e => {
            Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * this.splashDamageRadius);
        })).at(b.x, b.y);
    },
});
air.shootEffect = new Effect(10, cons(e => {
        Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
}));
air.hitEffect = Fx.none;
air.trailEffect = Fx.railTrail;
air.despawnEffect = Fx.none;
air.trailSpacing = 40;
air.damage = 100;
air.collidesGround = false;
air.lifetime = 1;
air.speed = 320;
air.reloadMultiplier = 1.2;
air.status = StatusEffects.shocked;
air.splashDamage = 520;
air.splashDamageRadius = 88;
air.hitShake = 6;
const r = extend(RailBulletType, {});
r.shootEffect = Fx.railShoot;
r.length = 500;
r.updateEffectSeg = 30;
r.pierceEffect = Fx.railHit;
r.updateEffect = Fx.railTrail;
r.hitEffect = Fx.massiveExplosion;
r.smokeEffect = Fx.shootBig2;
r.damage = 1350;
r.pierceDamageFactor = 0.6;

const nihilo = new UnitType("nihilo");
nihilo.constructor = prov(() => extend(UnitTypes.omura.constructor.get().class, {}));
nihilo.weapons.add(
    (() =>{
        const w = new PointDefenseWeapon("btm-nihilo-defense");
        w.x = 0;
        w.y = -17;
        w.reload = 6;
        w.targetInterval = 8;
        w.targetSwitchInterval = 8;
        w.mirror = false;
        w.shootSound = Sounds.shootForeshadow;
        w.bullet = (() => {
            const b = new BulletType();
            b.shootEffect = Fx.sparkShoot;
            b.hitEffect = Fx.pointHit;
            b.maxRange = 288;
            b.damage = 40;
            return b;
        })()
        return w;
    })()
);
nihilo.weapons.add(
    (() => {
        const w = new Weapon("btm-nihilo-m");
        w.shake = 3;
        w.shootY = 2;
        w.bullet = Bullets.missileSurge;
        w.rotate = true;
        w.rotateSpeed = 4;
        w.x = 24;
        w.y = 1;
        w.shotDelay = 1;
        w.xRand = 8;
        w.shots = 6;
        w.spacing = 5;
        w.inaccuracy = 4;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.shootMissile;
        w.reload = 36;
        w.recoil = 2;
        return w;
    })()
);
nihilo.weapons.add(
    (() => {
        const w = new Weapon("btm-nihilo-a");
        w.shake = 4;
        w.shootY = 6;
        w.top = false;
        w.shots = 4;
        w.inaccuracy = 12;
        w.velocityRnd = 0.2;
        w.bullet = bullets.artillerySurge;
        w.rotate = true;
        w.rotateSpeed = 4;
        w.x = 14.5;
        w.y = -10;
        w.shootSound = Sounds.shootArtillery;
        w.reload = 60;
        w.recoil = 4;
        return w;
    })()
);
nihilo.weapons.add(
    (() => {
        const w = new Weapon("btm-nihilo-air");
        w.shake = 5;
        w.shootY = 9;
        w.bullet = air;
        w.mirror = false;
        w.top = false;
        w.rotate = true;
        w.rotateSpeed = 8;
        w.controllable = false;
        w.autoTarget = true;
        w.x = 0;
        w.y = 38;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.shootForeshadow;
        w.reload = 180;
        w.recoil = 4;
        return w;
    })()
);
nihilo.weapons.add(
    (() => {
        const w = new Weapon("btm-nihilo-main");
        w.shake = 6;
        w.shootY = 23;
        w.bullet = r;
        w.mirror = false;
        w.top = false;
        w.rotate = true;
        w.rotateSpeed = 2;
        w.x = 0;
        w.y = 5;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.shootForeshadow;
        w.reload = 110;
        w.cooldownTime = 90;
        w.recoil = 5;
        return w;
    })()
);
nihilo.trailLength = 70;
nihilo.trailX = 23;
nihilo.trailY = -32;
nihilo.trailScl = 3.5;
nihilo.abilities.add(ability.TerritoryFieldAbility(120, 60 * 5, 220));
nihilo.abilities.add(new ShieldRegenFieldAbility(100, 600, 60 * 6, 200));
//nihilo.abilities.add(ability.pointDefenseAbility(0, -17, 8, 300, 40, "nihilo-defense"));
nihilo.abilities.add(new UnitSpawnAbility(UnitTypes.flare, spawnTime, 9.5, -35.5), new UnitSpawnAbility(UnitTypes.flare, spawnTime, -9.5, -35.5), new UnitSpawnAbility(UnitTypes.zenith, spawnTime * 5, 29, -25), new UnitSpawnAbility(UnitTypes.zenith, spawnTime * 5, -29, -25));
nihilo.armor = 19;
nihilo.drag = 0.2;
nihilo.flying = false;
nihilo.speed = 0.6;
nihilo.accel = 0.2;
nihilo.hitSize = 60;
nihilo.rotateSpeed = 0.9;
nihilo.rotateShooting = false;
//nihilo.canDrown = false;
nihilo.health = 61000;
nihilo.itemCapacity = 350;
nihilo.commandLimit = 8;
nihilo.ammoType = new ItemAmmoType(Items.surgeAlloy);
exports.nihilo = nihilo;