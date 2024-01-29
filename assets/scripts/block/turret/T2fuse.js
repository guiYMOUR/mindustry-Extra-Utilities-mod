//
const lib = require("blib");
const hardMod = Core.settings.getBool("eu-hard-mode");
const items = require("game/items");

const T2fuse = extend(ItemTurret, "T2-fuse", {});
T2fuse.reload = 35;
T2fuse.shake = 4;
T2fuse.range = 100;
T2fuse.recoil = 5;
T2fuse.shoot = new ShootSpread(5, 12);
T2fuse.shootCone = 30;
T2fuse.size = 3;
T2fuse.health = 240 * 3 * 3;
T2fuse.shootSound = Sounds.shotgun;
T2fuse.envEnabled |= Env.space;
T2fuse.shoot = ShootSpread(5, 12);
lib.Coolant(T2fuse, 0.4);
T2fuse.coolantMultiplier = 3;
T2fuse.requirements = ItemStack.with(
    Items.copper, 280,
    items.crispSteel, 175,
    Items.thorium, 150,
    Items.surgeAlloy, 60 + (hardMod ? 40 : 0),
);
T2fuse.buildVisibility = BuildVisibility.shown;
T2fuse.category = Category.turret;

exports.T2fuse = T2fuse;