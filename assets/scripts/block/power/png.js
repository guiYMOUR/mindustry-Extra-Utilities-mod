const lib = require("blib");

const arNode = extend(PowerNode, "ar-node", {});
arNode.maxNodes = 6;
arNode.laserRange = 9;
arNode.health = 220;
arNode.placeableLiquid = true;
arNode.requirements = ItemStack.with(
    Items.lead, 5,
    Items.metaglass, 2,
    Items.plastanium, 1
);
arNode.buildVisibility = BuildVisibility.shown;
arNode.category = Category.power;
exports.arNode = arNode;

const BatteryNode = lib.getClass("ExtraUtilities.worlds.blocks.power.BatteryNode");

const png = new BatteryNode("power-node-giant");
png.size = 3;
png.maxNodes = 30;
png.laserRange = 22;
png.health = 450;
png.requirements = ItemStack.with(
    Items.titanium, 50,
    Items.lead, 60,
    Items.graphite, 40,
    Items.silicon, 55
);
png.buildVisibility = BuildVisibility.shown;
png.category = Category.power;

exports.png = png;