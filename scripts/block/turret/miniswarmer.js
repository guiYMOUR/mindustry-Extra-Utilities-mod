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

const swMin = extendContent(ItemTurret, "mini-swarmer", {});
lib.setBuildingSimple(swMin, ItemTurret.ItemTurretBuild, {});
swMin.reloadTime = 35;
swMin.shots = 3;
swMin.burstSpacing = 4;
swMin.inaccuracy = 10;
swMin.range = 26 *8;
swMin.xRand = 4;
swMin.size = 2;
swMin.health = 200 * 2 * 2;
swMin.ammo(
    Items.graphite, m,
    Items.blastCompound, Bullets.missileExplosive,
            Items.pyratite, Bullets.missileIncendiary,
            Items.surgeAlloy, Bullets.missileSurge
);
swMin.limitRange();
swMin.requirements = ItemStack.with(
    Items.graphite, 45,
    Items.titanium, 40,
    Items.silicon, 30
);
swMin.buildVisibility = BuildVisibility.shown;
swMin.category = Category.turret;

exports.swMin = swMin;