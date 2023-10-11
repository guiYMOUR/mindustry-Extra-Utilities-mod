//
const lib = require("blib");

const swMin = extend(ItemTurret, "mini-swarmer", {});

swMin.reload = 35;
swMin.shoot = new ShootBarrel();
swMin.shoot.barrels = [
    -4.5, -1.2, 0,
    0, 0, 0,
    4.5, -1.2, 0
];
swMin.shoot.shots = 3;
swMin.shoot.shotDelay = 6;
swMin.inaccuracy = 10;
swMin.range = 26 * 8;
swMin.size = 2;
swMin.health = 250 * 2 * 2;
swMin.shootSound = Sounds.missile;
swMin.consumeAmmoOnce = false;

lib.Coolant(swMin, 0.2, false);
swMin.requirements = ItemStack.with(
    Items.graphite, 45,
    Items.titanium, 40,
    Items.silicon, 30
);
swMin.buildVisibility = BuildVisibility.shown;
swMin.category = Category.turret;

exports.swMin = swMin;