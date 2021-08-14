const ability = require("other/ability");
const status = require("other/status");

const bubble = extend(LiquidBulletType, {});
bubble.liquid = Liquids.slag;
bubble.status = status.weakness;
bubble.lightColor = Color.valueOf("#EC7458");
bubble.lightOpacity = Color.valueOf("#EC7458");
bubble.damage = 13;
bubble.knockback = 1.2;
bubble.puddleSize = 8;
bubble.orbSize = 4;
bubble.lifetime = 54;
bubble.speed = 4;
bubble.shootEffect = Fx.shootLiquid;

const r = extend(RailBulletType, {});
r.shootEffect = Fx.railShoot;
r.length = 400;
r.updateEffectSeg = 30;
r.pierceEffect = Fx.railHit;
r.updateEffect = Fx.railTrail;
r.hitEffect = Fx.massiveExplosion;
r.smokeEffect = Fx.shootBig2;
r.damage = 986;
r.pierceDamageFactor = 0.5;

const apocalypse = new UnitType("apocalypse");
var color = Color.valueOf("#FFA665");
apocalypse.abilities.add(new UnitSpawnAbility(UnitTypes.crawler, 60*10, 17, -27.5), new UnitSpawnAbility(UnitTypes.crawler, 60*10, -17, -27.5));
apocalypse.abilities.add(ability.LightningFieldAbility(54, 90, 192, color, 22));
apocalypse.constructor = prov(() => extend(UnitTypes.eclipse.constructor.get().class, {}));
apocalypse.weapons.add(
    (() => {
        const w = new Weapon("btm-apocalypse-m1");
        w.shake = 3;
        w.shootY = 2;
        w.bullet = Bullets.missileExplosive;
        w.rotate = true;
        w.rotateSpeed = 4;
        w.x = 35;
        w.y = 23;
        w.shotDelay = 1;
        w.xRand = 2;
        w.shots = 3;
        w.spacing = 6;
        w.inaccuracy = 3;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.missile;
        w.reload = 30;
        w.recoil = 2;
        return w;
    })()
);
apocalypse.weapons.add(
    (() => {
        const w = new Weapon("btm-apocalypse-m2");
        w.shake = 3;
        w.shootY = 2;
        w.bullet = Bullets.missileExplosive;
        w.rotate = true;
        w.rotateSpeed = 4;
        w.x = 30;
        w.y = -27;
        w.shotDelay = 1;
        w.xRand = 2;
        w.shots = 4;
        w.spacing = 5;
        w.inaccuracy = 1;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.missile;
        w.reload = 36;
        w.recoil = 2;
        return w;
    })()
);
apocalypse.weapons.add(
    (() => {
        const w = new Weapon("btm-apocalypse-bubble");
        w.shootY = 6;
        w.bullet = bubble;
        w.rotate = true;
        w.rotateSpeed = 8;
        w.x = 14;
        w.y = 29;
        w.shotDelay = 3;
        w.xRand = 2;
        w.shots = 5;
        w.spacing = 2;
        w.inaccuracy = 1;
        w.ejectEffect = Fx.none;
        w.reload = 6;
        w.recoil = 1;
        return w;
    })()
);
apocalypse.weapons.add(
    (() => {
        const w = new Weapon("btm-apocalypse-weapon");
        w.shake = 4;
        w.shootY = 16;
        w.reload = 90;
        w.bullet = r;
        w.rotate = true;
        w.rotateSpeed = 2;
        w.shootSound = Sounds.railgun;
        w.x = 28;
        w.y = -3;
        return w;
    })()
);
apocalypse.armor = 15;
apocalypse.flying = true;
apocalypse.speed = 0.51;
apocalypse.hitSize = 62;
apocalypse.accel = 0.04;
apocalypse.rotateSpeed = 1;
apocalypse.baseRotateSpeed = 20;
apocalypse.drag = 0.018;
apocalypse.health = 60000;
apocalypse.lowAltitude = true;
apocalypse.itemCapacity = 320;
apocalypse.engineOffset = 41;
apocalypse.engineSize = 11;
apocalypse.rotateShooting = true;
apocalypse.commandLimit = 8;
apocalypse.destructibleWreck = false;
apocalypse.targetFlags = UnitTypes.eclipse.targetFlags;
apocalypse.immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting);
apocalypse.ammoType = new ItemAmmoType(Items.pyratite);

exports.apocalypse = apocalypse;