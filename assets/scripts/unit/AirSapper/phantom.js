const weapon = require("other/weapon");
const bullets = require("other/bullets");

/*const sEffect = new Effect(38, cons(e => {
    
}));*/

const lineBullet = bullets.lineBullet({
    maxFind : 10,
    width : 24,
    height : 15,
    trailLength : 15,
    trailWidth : 6,
    damage : 90,
    splashDamage : 90,
    splashDamageRadius : 88,
});
//lineBullet.shootEffect = sEffect;

const phantom = new UnitType("phantom");
phantom.constructor = prov(() => extend(UnitTypes.zenith.constructor.get().class, {}));
phantom.weapons.add(
    (() =>{
        function setStat(W){
            W.add("[accent]" + lineBullet.maxFindValue() + " [lightgray]max link");
        }
        const w = weapon.statWeapon(setStat, "btm-phantom-main");
        w.x = 0;
        w.y = 0;
        w.shootY = 30;
        w.mirror = false;
        w.reload = 72;
        w.rotate = false;
        w.bullet = lineBullet;
        w.shootSound = Sounds.shootSap;
        return w;
    })()
);
const cl = new BulletType();
cl.maxRange = 8*23;
cl.damage = 150;
cl.drag = 1;
const wea1 = weapon.continueLaser({
    name : "btm-phantom-l",
    x : 15,
    y : -6,
    bullet : cl,
});
const wea2 = weapon.continueLaser({
    name : "btm-phantom-l",
    x : -15,
    y : -6,
    bullet : cl,
});
phantom.weapons.add(wea1);
phantom.weapons.add(wea2);

phantom.armor = 8;
phantom.flying = true;
phantom.lowAltitude = true;
phantom.hitSize = 36;
phantom.speed = 1.5;
phantom.accel = 0.04;
phantom.drag = 0.016;
phantom.health = 5900;
//phantom.buildSpeed = 0.5;
phantom.itemCapacity = 120;
phantom.engineOffset = 24;
phantom.engineSize = 7;
phantom.rotateShooting = true;
phantom.ammoType = new PowerAmmoType(1200);
phantom.commandLimit = 6;
exports.phantom = phantom;