/*all the advanced transport equipment is here*/
//引用部分，类似import，对应的是exports导出
const lib = require("blib");
//const build = require("block/distribution/invertedJunction");
const InvertedJunction = lib.getClass("ExtraUtilities.worlds.blocks.distribution.InvertedJunction");

const alloyDuct = new Duct("alloy-duct");
alloyDuct.requirements = ItemStack.with(
    Items.graphite, 3,
    Items.surgeAlloy, 1,
    Items.phaseFabric, 1
);
alloyDuct.health = 240;
alloyDuct.buildVisibility = BuildVisibility.shown;
alloyDuct.category = Category.distribution;
alloyDuct.speed = 2;
alloyDuct.itemCapacity = 2;
alloyDuct.placeableLiquid = true;
exports.alloyDuct = alloyDuct;

//Blocks.plastaniumConveyor.recharge = 1;
const ppc = extend(StackConveyor, "ppc", {});
ppc.health = 150;
ppc.speed = 8 / 60;
ppc.recharge = 1;
ppc.itemCapacity = 10;
ppc.requirements = ItemStack.with(
    Items.silicon, 1,
    Items.thorium, 1,
    Items.phaseFabric, 1
);
ppc.buildVisibility = BuildVisibility.shown;
ppc.category = Category.distribution;
exports.ppc = ppc;

const T2IB = extend(BufferedItemBridge, "T2IB", {});
T2IB.size = 1;
T2IB.requirements = ItemStack.with(
    Items.copper, 15,
    Items.lead, 10,
    Items.graphite, 8,
    Items.titanium, 5
);
T2IB.range = 6;
T2IB.speed = 20;
T2IB.health = 85;
T2IB.bufferCapacity = 25;
T2IB.buildVisibility = BuildVisibility.shown;
T2IB.category = Category.distribution;
exports.T2IB = T2IB;

const invertedJunction = InvertedJunction("inverted-junction");
invertedJunction.requirements = ItemStack.with(
    Items.copper, 2
);
invertedJunction.placeSprite = "junction";
invertedJunction.sync = true;
invertedJunction.buildVisibility = BuildVisibility.shown;
invertedJunction.category = Category.distribution;
invertedJunction.speed = 26;
invertedJunction.capacity = 6;
invertedJunction.health = 30;
invertedJunction.configurable = true;
invertedJunction.buildCostMultiplier = 5;
exports.invertedJunction = invertedJunction;

const TJ = extend(Junction, "TJ", {});
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

const TIJ = new InvertedJunction("titanium-inverted-junction");
TIJ.placeSprite = lib.aModName + "-TJ";
TIJ.sync = true;
TIJ.requirements = TJ.requirements;
TIJ.buildVisibility = BuildVisibility.shown;
TIJ.category = Category.distribution;
TIJ.speed = TJ.speed;
TIJ.capacity = TJ.capacity;
TIJ.health = TJ.health;
TIJ.configurable = true;
TIJ.buildCostMultiplier = 5;
exports.TIJ = TIJ;

const TR = extend(Router, "TR", {});
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

const conduit = extend(ArmoredConduit, "conduit", {});
conduit.requirements = ItemStack.with(
    Items.silicon, 2,
    Items.phaseFabric, 1
);
conduit.health = 220;
conduit.buildVisibility = BuildVisibility.shown;
conduit.category = Category.liquid;
conduit.liquidCapacity = 24;
conduit.liquidPressure = 1.08;
conduit.placeableLiquid = true;
exports.conduit = conduit;

const T2LB = extend(LiquidBridge , "TLB",{});
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

// const TLR = extend(LiquidRouter, "TLR", {});
// TLR.liquidCapacity = 30;
// TLR.liquidPressure = 1.1;
// TLR.health = 90;
// TLR.requirements = ItemStack.with(
//     Items.graphite, 6,
//     Items.metaglass, 4,
//     Items.titanium, 5
// );
// TLR.buildVisibility = BuildVisibility.shown;
// TLR.category = Category.liquid;
// exports.TLR = TLR;

