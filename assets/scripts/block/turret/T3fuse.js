//
const lib = require("blib");
const hardMod = lib.hardMod;
const items = require("game/items");

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
T3fuse.shootSound = Sounds.shootFuse;
T3fuse.requirements = ItemStack.with(
    Items.copper, 500,
    Items.graphite, 500,
    Items.titanium, 300,
    Items.thorium, 200,
    Items.silicon, 270 + (hardMod ? 30 : 0),
    items.lightninAlloy, 150 + (hardMod ? 20 : 0)
);
T3fuse.buildVisibility = BuildVisibility.shown;
T3fuse.category = Category.turret;

exports.T3fuse = T3fuse;