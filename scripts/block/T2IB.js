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
T2IB.speed = 85;
T2IB.health = 75;
T2IB.bufferCapacity = 18;
T2IB.buildVisibility = BuildVisibility.shown;
T2IB.category = Category.distribution;

exports.T2IB = T2IB;