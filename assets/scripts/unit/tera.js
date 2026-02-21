const ability = require("other/ability");
const weapon = require("other/weapon");
const bullets = require("other/bullets");
const AI = require("other/unitAI");

const c1 = Color.valueOf("84f491");
var spawnTime = 60 * 24;
const la = extend(LaserBulletType, {});
la.length = 200;
la.width = 20;
la.lifetime = 20;
la.damage = 88;
la.healPercent = 10;
la.collidesTeam = true;
la.colors = [c1, c1, Color.white];

const tera = new UnitType("Tera");
tera.abilities.add(ability.MendFieldAbility(150, 150, 12));
tera.abilities.add(new UnitSpawnAbility(UnitTypes.poly, spawnTime, 30, -27.5), new UnitSpawnAbility(UnitTypes.poly, spawnTime, -30, -27.5));
tera.abilities.add(new ForceFieldAbility(140, 5, 8500, 60 * 8), new RepairFieldAbility(200, 60 * 2, 140));
tera.constructor = prov(() => extend(UnitTypes.oct.constructor.get().class, {}));
tera.defaultController = prov(() => AI.DefenderHealAI(false, true, false));
tera.weapons.add(
    (() => {
        const w = new Weapon("btm-Tera-weapon");
        w.shake = 4;
        w.shootY = 9;
        w.reload = 60;
        w.bullet = la;
        w.rotate = true;
        w.recoil = 3;
        w.rotateSpeed = 2;
        w.shadow = 20;
        w.shootSound = Sounds.shootLancer;
        w.x = 25;
        w.y = 3;
        return w;
    })()
);
tera.weapons.add(
    (() =>{
        const w = weapon.healProjectorWeapon({
            name : "",
            x : 0,
            y : 15,
            shootY : 1,
            autoTarget : true,
            controllable : false,
            flyShoot : true,
            bullet : bullets.unitHealCone({
                lifetime : 210,
                findRange : 100,
                findAngle : 100,
            }),
        });
        w.top = false;
        return w;
    })()
);
tera.armor = 20;
tera.flying = true;
tera.speed = 0.7;
tera.hitSize = 66;
tera.accel = 0.04;
tera.rotateSpeed = 1;
tera.drag = 0.018;
tera.health = 63000;
tera.mineSpeed = 7;
tera.mineTier = 10;
tera.buildSpeed = 8;
tera.itemCapacity = 600;
tera.engineOffset = 25;
tera.engineSize = 12;
tera.rotateShooting = false;
tera.drawShields = false;
tera.lowAltitude = true;
tera.payloadCapacity = (6.4 * 6.4) * Vars.tilePayload;
tera.ammoType = new PowerAmmoType(1500);
tera.commandLimit = 8;


exports.tera = tera;