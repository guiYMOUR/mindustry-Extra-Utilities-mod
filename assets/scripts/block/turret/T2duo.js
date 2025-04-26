//
const lib = require("blib");
const hardMod = lib.hardMod;
const T2duo = extend(ItemTurret, "T2-duo", {});
lib.setBuildingSimple(T2duo, ItemTurret.ItemTurretBuild, {});
T2duo.shoot = lib.moreShootAlternate(3.6, 2);
T2duo.reload = hardMod ? 36 : 15;
T2duo.restitution = 0.03;
T2duo.range = 125;
T2duo.shootCone = 15;
T2duo.ammoUseEffect = Fx.casing1;
T2duo.health = hardMod ? 640 : 320;
T2duo.inaccuracy = 2;
T2duo.rotateSpeed = 10;
T2duo.limitRange();
T2duo.coolant = T2duo.consume(new ConsumeCoolant(0.1));
T2duo.ammoTypes = Blocks.duo.ammoTypes;
T2duo.requirements = ItemStack.with(
    Items.copper, 70
);
T2duo.recoils = 2;
T2duo.buildVisibility = BuildVisibility.shown;
T2duo.category = Category.turret;
T2duo.researchCostMultiplier = 0.1;

exports.T2duo = T2duo;