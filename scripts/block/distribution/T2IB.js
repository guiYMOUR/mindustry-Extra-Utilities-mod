const lib = require("blib");

const T2IB = extendContent(BufferedItemBridge, "T2IB", {});
lib.setBuildingSimple(T2IB, BufferedItemBridge.BufferedItemBridgeBuild, {/**/});
T2IB.size = 1;
T2IB.requirements = ItemStack.with(
    Items.copper, 15,
    Items.lead, 10,
    Items.graphite, 8,
    Items.titanium, 5
);
T2IB.range = 6;
T2IB.speed = 12;
T2IB.health = 85;
T2IB.bufferCapacity = 25;
T2IB.buildVisibility = BuildVisibility.shown;
T2IB.category = Category.distribution;
exports.T2IB = T2IB;

const TJ = extendContent(Junction, "TJ", {});
TJ.requirements = ItemStack.with(
    Items.copper, 5,
    Items.lead, 2,
    Items.titanium, 3
);
TJ.buildVisibility = BuildVisibility.shown;
TJ.category = Category.distribution;
TJ.speed = (26 + 2) / 4;
TJ.capacity = 14;
TJ.health = 80;
TJ.buildCostMultiplier = 5;
exports.TJ = TJ;

const TR = extendContent(Router, "TR", {});
TR.requirements = ItemStack.with(
    Items.copper, 3,
    Items.lead, 3,
    Items.titanium, 2
);
TR.buildVisibility = BuildVisibility.shown;
TR.category = Category.distribution;
TR.speed = 8 / 4;
TR.health = 80;
TR.buildCostMultiplier = 4;
exports.TR = TR;

const T2LB = extendContent(LiquidExtendingBridge , "TLB",{});
T2LB.requirements = ItemStack.with(
    Items.graphite, 8,
    Items.metaglass, 9,
    Items.titanium, 8
);
T2LB.buildVisibility = BuildVisibility.shown;
T2LB.category = Category.liquid;
T2LB.health = 85;
T2LB.range = 6;
T2LB.hasPower = false;
exports.T2LB = T2LB;

const TLR = extendContent(LiquidRouter, "TLR", {});
TLR.liquidCapacity = 20;
TLR.liquidPressure = 1.1;
TLR.health = 90;
TLR.requirements = ItemStack.with(
    Items.graphite, 6,
    Items.metaglass, 4,
    Items.titanium, 5
);
TLR.buildVisibility = BuildVisibility.shown;
TLR.category = Category.liquid;
exports.TLR = TLR;