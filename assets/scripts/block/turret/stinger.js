//
const lib = require('blib');
const status = require("other/status");
const Stinger = lib.getClass("ExtraUtilities.worlds.blocks.turret.Stinger");
const DeathLaser = lib.getClass("ExtraUtilities.worlds.entity.bullet.DeathLaser");
const hardMod = Core.settings.getBool("eu-hard-mode");

const chargeTime = 40;
const color = Color.valueOf("#C6D676");
//const chargeSound = Sounds.none;

//const stingerLaser = extend(LaserBulletType, {});
const stingerLaser = new DeathLaser();

stingerLaser.chargeEffect = new MultiEffect(lib.Fx.lancerLaserCharge(color), lib.Fx.chargeBeginEffect(chargeTime, color));

stingerLaser.damage = 320 - (hardMod ? 20 : 0);
stingerLaser.sideAngle = 2;
stingerLaser.sideWidth = 2;
stingerLaser.sideLength = 40;
stingerLaser.collidesAir = false
stingerLaser.length = 230;
stingerLaser.width = 26;
stingerLaser.hitSize = 6;
stingerLaser.lifetime = 48;
stingerLaser.drawSize = 400;
stingerLaser.status = status.poison;
stingerLaser.buildingDamageMultiplier = 0.25;
stingerLaser.lightningSpacing = 35;
stingerLaser.lightningLength = 5;
stingerLaser.lightningDelay = 1.1;
stingerLaser.lightningLengthRand = 10;
stingerLaser.lightningDamage = 5;
stingerLaser.lightningAngleRand = 30;
stingerLaser.lightColor = color;
stingerLaser.lightningColor = color;
stingerLaser.colors =[color, color, Color.white];
stingerLaser.shootEffect = lib.Fx.stingerShoot(color);


//const stinger = extend(PowerTurret, 'stinger', {});
const stinger = new Stinger("stinger");
stinger.shoot.firstShotDelay = 40;
stinger.accurateDelay = false;
if(hardMod) stinger.delay = 60 * 7;
stinger.moveWhileCharging = false;
stinger.consumePower(11);
stinger.shootType = stingerLaser;
stinger.shake = 3.5;
stinger.reload = chargeTime * 2.5;
stinger.range = 200;
stinger.shootCone = 15;
stinger.ammoUseEffect = Fx.none;
stinger.health = 2960;
stinger.inaccuracy = 0;
stinger.cooldownTime = 48;
stinger.recoil = 2.5;
stinger.rotateSpeed = 2;
stinger.size = 3;
stinger.targetAir = false;
stinger.shootSound = Sounds.laser;
lib.Coolant(stinger, 0.4, 2);
stinger.drawer = (() => {
    const d = new DrawTurret();
    d.parts.add(
        (() => {
            const p = new RegionPart("-head");
            p.progress = DrawPart.PartProgress.recoil;
            p.moveX = 0;
            p.moveY = -2;
            p.mirror = false;
            return p;
        })(),
        (() => {
            const p = new RegionPart("-pigu");
            p.progress = DrawPart.PartProgress.recoil;
            p.moveX = 0;
            p.moveY = -0;
            p.mirror = false;
            p.under = true;
            return p;
        })()
    );
    return d;
})();
stinger.requirements = ItemStack.with(
    Items.silicon, 250,
    Items.titanium, 100,
    Items.thorium, 100,
    Items.plastanium, 80 + (hardMod ? 80 : 0),
    Items.surgeAlloy, 100 + (hardMod ? 20 : 0)
);
stinger.buildVisibility = BuildVisibility.shown;
stinger.category = Category.turret;

exports.stinger = stinger;
