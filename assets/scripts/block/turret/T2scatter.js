const lib = require("blib");
var defShoot = extend(ShootBarrel,{});
defShoot.barrels = [
    0, 1, 0,
    2, 0, 0,
    -2, 0, 0
];
defShoot.shots = 2;
defShoot.shotDelay = 6;

const T2scatter = extend(ItemTurret, "T2-scatter", {});
T2scatter.size = 2;
T2scatter.shoot = defShoot;
T2scatter.burstSpacing = 3;
T2scatter.reload = 12;
T2scatter.recoilAmount = 2;
T2scatter.range = 220;
T2scatter.shootCone = 35;
T2scatter.health = 210 * 2 * 2;
T2scatter.inaccuracy = 10;
T2scatter.rotateSpeed = 15;
T2scatter.targetGround = false;
T2scatter.shootSound = Sounds.shootSnap;
T2scatter.ammoTypes = Blocks.scatter.ammoTypes;
T2scatter.limitRange(2);
T2scatter.scaledHealth = 105;
T2scatter.coolant = T2scatter.consumeCoolant(0.22);
T2scatter.requirements = ItemStack.with(
    Items.copper, 90,
    Items.lead, 70,
    Items.graphite, 25
);
T2scatter.buildVisibility = BuildVisibility.shown;
T2scatter.category = Category.turret;

exports.T2scatter = T2scatter;