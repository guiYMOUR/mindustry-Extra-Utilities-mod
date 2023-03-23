//
const lib = require('blib');
const items = require("game/items");

const laser = extend(LaserBulletType, {});
laser.damage = 120;
laser.sideAngle = 1;
laser.sideWidth = 3;
laser.sideLength = 35;
laser.collidesAir = false
laser.length = 190;
laser.hitSize = 4;
laser.lifetime = 16;
laser.drawSize = 400;
laser.lightningSpacing = 35;
laser.lightningLength = 2;
laser.lightningDelay = 1.1;
laser.lightningLengthRand = 10;
laser.lightningDamage = 1;
laser.lightningAngleRand = 30;
laser.lightColor = Pal.lancerLaser;
laser.lightningColor = Pal.lancerLaser;
laser.buildingDamageMultiplier = 0.2;


const T2lan = extend(PowerTurret, 'T2-lancer', {});

T2lan.consumePower(11);
T2lan.shootType = laser;
T2lan.shoot = lib.moreShootAlternate(5, 2);
T2lan.shake = 2;
T2lan.shootEffect = Fx.lancerLaserShoot;
T2lan.smokeEffect = Fx.none;
T2lan.alternate = true;
T2lan.reload = 40;
T2lan.recoil = 2;
T2lan.range = 180;
T2lan.shootCone = 15;
T2lan.ammoUseEffect = Fx.none;
T2lan.health = 1500;
T2lan.inaccuracy = 0;
T2lan.rotateSpeed = 10;
T2lan.size = 3;
T2lan.targetAir = false;
T2lan.shootSound = Sounds.laser;
lib.Coolant(T2lan, 0.3, 3);
T2lan.requirements = ItemStack.with(
    Items.lead, 120,
    Items.silicon, 105,
    Items.graphite, 85,
    Items.plastanium, 50,
    items.crispSteel, 95
);
T2lan.buildVisibility = BuildVisibility.shown;
T2lan.category = Category.turret;

exports.T2lan = T2lan;
