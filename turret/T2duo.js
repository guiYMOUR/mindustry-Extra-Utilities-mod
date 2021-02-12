//
const lib = require("blib");
const T2duo = extendContent(ItemTurret, "T2-duo", {});
lib.setBuildingSimple(T2duo, ItemTurret.ItemTurretBuild, {});
T2duo.spread = 2;
T2duo.shots = 3;
T2duo.alternate = true;
T2duo.reloadTime = 13;
T2duo.restitution = 0.03;
T2duo.range = 120;
T2duo.shootCone = 15;
T2duo.ammoUseEffect = Fx.casing1;
T2duo.health = 320;
T2duo.inaccuracy = 2;
T2duo.rotateSpeed = 10;
T2duo.ammo(
    Items.copper, Bullets.standardCopper,
            Items.graphite, Bullets.standardDense,
            Items.pyratite, Bullets.standardIncendiary,
            Items.silicon, Bullets.standardHoming
);
T2duo.requirements = ItemStack.with(
    Items.copper, 55
);
T2duo.buildVisibility = BuildVisibility.shown;
T2duo.category = Category.turret;

exports.T2duo = T2duo;