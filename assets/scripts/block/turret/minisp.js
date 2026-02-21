const items = require("game/items");
const lib = require("blib");

const minisp = extend(ItemTurret, "minisp", {});
minisp.shootSound = Sounds.shootSpectre;
minisp.size = 3;
minisp.shake = 1;
minisp.recoil = 2;
minisp.recoilTime = 18;
minisp.reload = 18;
minisp.range = 30 * 8;
minisp.shootCone = 22;
minisp.ammoUseEffect = Fx.casing3;
minisp.health = 160 * 3 * 3;
minisp.inaccuracy = 5;
minisp.ammoTypes = Blocks.spectre.ammoTypes;
minisp.limitRange();
lib.Coolant(minisp, 0.5, 1);
minisp.shoot = new ShootAlternate(5);
minisp.requirements = ItemStack.with(
    Items.copper, 300,
    Items.graphite, 175,
    Items.silicon, 175,
    Items.titanium, 150,
    items.crispSteel, 140
);
minisp.buildVisibility = BuildVisibility.shown;
minisp.category = Category.turret;

exports.minisp = minisp;