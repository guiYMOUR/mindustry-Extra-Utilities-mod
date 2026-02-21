const items = require("game/items");
const lib = require("blib");
const PowerUnloader = lib.getClass("ExtraUtilities.worlds.blocks.distribution.PowerUnloader");
const und = new PowerUnloader("und");
und.requirements = ItemStack.with(
    Items.lead, 30,
    Items.silicon, 30,
    Items.titanium, 30,
    Items.thorium, 15
);
und.buildVisibility = BuildVisibility.shown;
und.category = Category.distribution;
und.speed = 60/72;
und.health = 100;
und.hasPower = true;
und.consumePower(45/60);
exports.und = und;