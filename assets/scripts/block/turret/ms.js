const cor = Color.valueOf("e5f3fe");
const liC = Color.valueOf("bf92f9");
const lib = require("blib");
const items = require("game/items");
const MagneticStormBulletType = lib.getClass("ExtraUtilities.worlds.entity.bullet.MagneticStormBulletType");

const MS = new MagneticStormBulletType(cor, liC);
MS.homingRange = 100;
MS.homingPower = 0.4;
MS.speed = 1.5;
MS.damage = 0;
MS.knockback = 0.2;
MS.splashDamageRadius = 56;
MS.splashDamage = 200;
MS.width = 40;
MS.height = 40;
MS.drag = 0;
MS.collidesTiles = false;
MS.pierce = true;
MS.hitSize = 0;
MS.collides = false;
MS.collidesAir = true;
MS.lifetime = 300;
MS.status = StatusEffects.shocked;

const storm = extend(PowerTurret, 'MAGNETIC-STORM', {});
storm.consumePower(32);
storm.shootType = MS;
//storm.shots = 1;
storm.shootShake = 5;
storm.reload = 282;
storm.recoil = 6;
//storm.restitution = 0.02;
storm.range = 300;
storm.shootCone = 15;
storm.ammoUseEffect = Fx.none;
storm.health = 3320;
storm.inaccuracy = 0;
storm.rotateSpeed = 1.5;
// storm.coolantUsage = 0.65;
// storm.coolantMultiplier = 0.7;
storm.size = 4;
storm.shootSound = Sounds.spark;
storm.absorbLasers = true;
lib.Coolant(storm, 0.5);
storm.requirements = ItemStack.with(
    Items.lead, 780,
    Items.silicon, 600,
    //Items.titanium, 385,
    Items.plastanium, 350,
    Items.thorium, 350,
    items.lightninAlloy, 190
);
storm.buildVisibility = BuildVisibility.shown;
storm.category = Category.turret;

exports.ms = storm;