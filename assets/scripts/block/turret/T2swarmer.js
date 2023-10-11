const lib = require("blib");
const swT2 = extend(ItemTurret, "T2-swarmer", {});

swT2.reload = 30;

swT2.shoot = new ShootBarrel();
swT2.shoot.barrels = [
    -8.5, -1.5, 0,
    -4.2, -0.7, 0,
    0, 0, 0,
    4.2, -0.7, 0,
    8.5, -1.5, 0
];
swT2.shoot.shots = 7;
swT2.shoot.shotDelay = 3;
swT2.inaccuracy = 10;
swT2.range = 34.5 * 8;
swT2.size = 3;
swT2.health = 300 * 3 * 3;
swT2.shootSound = Sounds.missile;

lib.Coolant(swT2, 0.3);
swT2.requirements = ItemStack.with(
    Items.graphite, 155,
    Items.titanium, 180,
    Items.plastanium, 80,
    Items.silicon, 120,
    Items.surgeAlloy, 60
);
swT2.buildVisibility = BuildVisibility.shown;
swT2.category = Category.turret;

exports.swT2 = swT2;

