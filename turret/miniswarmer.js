//
const lib = require("blib");
const swMin = extendContent(ItemTurret, "mini-swarmer", {});
lib.setBuildingSimple(swMin, ItemTurret.ItemTurretBuild, {});
swMin.reloadTime = 35;
swMin.shots = 3;
swMin.burstSpacing = 4;
swMin.inaccuracy = 10;
swMin.range = 190;
swMin.xRand = 4;
swMin.size = 2;
swMin.health = 500 * 2 * 2;
swMin.ammo(
    Items.blastCompound, Bullets.missileExplosive,
            Items.pyratite, Bullets.missileIncendiary,
            Items.surgeAlloy, Bullets.missileSurge
            //Items.copper, f2
);
swMin.requirements = ItemStack.with(
    Items.graphite, 55,
    Items.titanium, 50,
    Items.silicon, 30
);
swMin.buildVisibility = BuildVisibility.shown;
swMin.category = Category.turret;

exports.swMin = swMin;