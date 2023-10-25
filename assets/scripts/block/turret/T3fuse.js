//
const lib = require("blib");
const hardMod = Core.settings.getBool("eu-hard-mode");

const T3fuse = extend(ItemTurret, "T3-fuse", {});
T3fuse.reload = 35;
T3fuse.shake = 4;
T3fuse.range = 120;
T3fuse.recoil = 5;
T3fuse.shootCone = 30;
T3fuse.size = 4;
T3fuse.health = 280 * 3 * 3;
T3fuse.shoot = ShootSpread(9, 9);
lib.Coolant(T3fuse, 0.6);
T3fuse.coolantMultiplier = 2;
T3fuse.shootSound = Sounds.shotgun;
T3fuse.requirements = ItemStack.with(
    Items.copper, 550,
    Items.graphite, 320,
    Items.titanium, 270,
    Items.thorium, 150,
    Items.silicon, 150 + (hardMod ? 30 : 0),
    Items.surgeAlloy, 140 + (hardMod ? 10 : 0)
);
T3fuse.buildVisibility = BuildVisibility.shown;
T3fuse.category = Category.turret;

exports.T3fuse = T3fuse;