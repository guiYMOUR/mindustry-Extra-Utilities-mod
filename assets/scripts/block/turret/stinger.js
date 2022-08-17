//
const lib = require('blib');
const status = require("other/status");

const chargeTime = 40;
const chargeMaxDelay = 30;
const chargeEffects = 8;
const chargeEffect = Fx.lancerLaserCharge;
//const chargeSound = Sounds.none;
const chargeBeginEffect = lib.newEffect(chargeTime * 1.5, e => {
        Draw.color(Color.valueOf("ffffff"), Color.valueOf("c6d676"), e.fin());
        Fill.circle(e.x, e.y, e.fin() * 10);

        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 6);
});
const eff1 = lib.newEffect(10, (e) => {
        Draw.color(Color.white, Color.valueOf("#C6D676"), e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
});

const stingerLaser = extend(LaserBulletType, {});
stingerLaser.damage = 286;
stingerLaser.sideAngle = 2;
stingerLaser.sideWidth = 2;
stingerLaser.sideLength = 40;
stingerLaser.collidesAir = false
stingerLaser.length = 230;
stingerLaser.width = 22;
stingerLaser.hitSize = 6;
stingerLaser.lifetime = 48;
stingerLaser.drawSize = 400;
stingerLaser.status = status.poison;
stingerLaser.lightningSpacing = 35;
stingerLaser.lightningLength = 5;
stingerLaser.lightningDelay = 1.1;
stingerLaser.lightningLengthRand = 10;
stingerLaser.lightningDamage = 2;
stingerLaser.lightningAngleRand = 30;
stingerLaser.lightColor = Color.valueOf("#C6D676");
stingerLaser.lightningColor = Color.valueOf("#C6D676");
stingerLaser.colors =[Color.valueOf("#C6D676"), Color.valueOf("#C6D676"), Color.white];
stingerLaser.shootEffect = eff1;


const stinger = extend(PowerTurret, 'stinger', {});

lib.setBuildingSimple(stinger, PowerTurret.PowerTurretBuild, {
    shouldTurn(){
        return true;
    }
});

stinger.chargeTime = chargeTime;
stinger.chargeMaxDelay = chargeMaxDelay;
stinger.chargeEffects = chargeEffects;
stinger.chargeEffect = chargeEffect;
stinger.chargeBeginEffect = chargeBeginEffect;
//stinger.chargeSound = chargeSound;

stinger.consumePower(11);
stinger.shootType = stingerLaser;
stinger.shots = 1;
stinger.shootShake = 3;
stinger.reload = chargeTime * 2.5;
stinger.range = 200;
stinger.shootCone = 15;
stinger.ammoUseEffect = Fx.none;
stinger.health = 2960;
stinger.inaccuracy = 0;
stinger.recoilAmount = 2.5;
stinger.rotateSpeed = 4;
stinger.size = 3;
stinger.targetAir = false;
stinger.shootSound = Sounds.laser;
lib.Coolant(stinger, 0.32);
stinger.requirements = ItemStack.with(
    Items.copper, 200,
    Items.silicon, 150,
    Items.graphite, 125,
    Items.titanium, 100,
    Items.thorium, 100,
    Items.plastanium, 80,
    Items.surgeAlloy, 10
);
stinger.buildVisibility = BuildVisibility.shown;
stinger.category = Category.turret;

exports.stinger = stinger;
