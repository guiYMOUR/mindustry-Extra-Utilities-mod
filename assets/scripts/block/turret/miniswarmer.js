//
const lib = require("blib");
const m = extend(MissileBulletType,{});
m.speed = 3.5;
m.damage = 10;
m.width = 7;
m.height = 8;
m.shrinkY = 0;
m.drag = -0.01;
m.homingPower = 0.08;
m.reloadMultiplier = 1.2;
m.splashDamageRadius = 25;
m.splashDamage = 16;
m.hitEffect = Fx.blastExplosion;

const swMin = extend(ItemTurret, "mini-swarmer", {});
lib.setBuildingSimple(swMin, ItemTurret.ItemTurretBuild, {});
swMin.reloadTime = 35;
swMin.shots = 3;
swMin.burstSpacing = 4;
swMin.inaccuracy = 10;
swMin.range = 26 *8;
swMin.xRand = 4;
swMin.size = 2;
swMin.health = 250 * 2 * 2;
swMin.shootSound = Sounds.missile;
//swMin.ammoTypes = Blocks.swarmer.ammoTypes;
swMin.ammo(
    Items.blastCompound, lib.toBullet(Blocks.swarmer, Items.blastCompound),
    Items.pyratite, lib.toBullet(Blocks.swarmer, Items.pyratite),
    Items.surgeAlloy, lib.toBullet(Blocks.swarmer, Items.surgeAlloy),
    Items.graphite, m
);
swMin.limitRange();
lib.Coolant(swMin, 0.2);
swMin.requirements = ItemStack.with(
    Items.graphite, 45,
    Items.titanium, 40,
    Items.silicon, 30
);
swMin.buildVisibility = BuildVisibility.shown;
swMin.category = Category.turret;

exports.swMin = swMin;