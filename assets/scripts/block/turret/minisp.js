const items = require("game/items");
const lib = require("blib");

const minisp = extend(ItemTurret, "minisp", {});
minisp.shoot = lib.moreShootAlternate(5, 2);
minisp.shootSound = Sounds.shootBig;
minisp.size = 3;
minisp.shake = 1;
minisp.recoilAmount = 2;
minisp.reloadTime = 8;
minisp.restitution = 0.09;
minisp.range = 30 *8;
minisp.shootCone = 22;
minisp.ammoUseEffect = Fx.casing3;
minisp.health = 160*3*3;
minisp.inaccuracy = 3;
// minisp.coolantMultiplier = 0.5;
// minisp.coolantUsage = 0.8;
minisp.ammoTypes = Blocks.spectre.ammoTypes;
minisp.limitRange();
lib.Coolant(minisp, 0.5);
minisp.requirements = ItemStack.with(
    Items.copper, 500,
    Items.graphite, 150,
    Items.surgeAlloy, 150,
    Items.silicon, 175,
    items.crispSteel, 150
);
minisp.buildVisibility = BuildVisibility.shown;
minisp.category = Category.turret;

exports.minisp = minisp;