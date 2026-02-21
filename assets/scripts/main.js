const lib = require("blib");
//main是模组的捆绳，把所有要用到的文件一并读入
//require("blib");
const onlyPlugIn = Core.settings.getBool("eu-plug-in-mode");
if(!onlyPlugIn) {
    require("block/turret/T2duo");
    require("block/turret/T3duo");
    require("block/turret/T2scatter");
    require("block/turret/T2scorch");
    require("block/turret/IceCookie");
    require("block/turret/shotgun");
    require("block/turret/sakura");
    require("block/turret/rainbow");
    require("block/turret/hurricane");
    require("block/turret/ms");
// require("block/turret/sunburst");
    require("block/turret/T2lancer");
    require("block/turret/stinger");
    require("block/turret/miniswarmer");
    require("block/turret/T2swarmer");
    require("block/turret/T2ripple");
    require("block/turret/T3ripple");
    require("block/turret/T2fuse");
    require("block/turret/T3fuse");
    require("block/turret/minisp");
// require("block/turret/antiaircraft");
// require("block/turret/sam");
    require("block/turret/prism");
// require("block/turret/blackhole");
    require("block/turret/RG");
// require("block/turret/sucker");
// require("block/turret/dissipation");
// require("block/turret/heal");
//require("block/turret/TurretResupplyPoint");

//require("block/effect/DCF");
    require("block/power/png");
    require("block/power/T2steam");
//require("block/power/T2ther");
    require("block/power/th2");
//require("block/power/LightninGenerator");

    require("block/production/drill");
    require("block/production/T2kiln");
    require("block/production/T2melter");
    require("block/production/T2PC");
    require("block/production/T2PF");
    require("block/production/T2CM");
    require("block/production/T2SA");
    require("block/production/CrispSteelSmelter");
    require("block/production/T2CrispSteelSmelter");
    require("block/production/T2pulverize");
    require("block/power/GeneratorCrafter");
//require("block/production/crusher");
//require("block/production/LA");
    require("block/production/AdjustableIncinerator");

    require("block/distribution/T2IB");
    /*require("block/distribution/stackBridge");
    require("block/distribution/IN");
    require("block/distribution/LB");*/
    require("block/distribution/driver");
    require("block/effect/core");
    require("block/effect/und");
    require("block/effect/LiquidUnloader");
    require("block/effect/speeder");
    /*require("block/effect/cure");
    require("block/effect/unitA");
    require("block/effect/lighthouse");*/
    require("block/defence/wall");

    /*require("unit/UF");
    require("unit/suzerain");
    require("unit/nebula");
    require("unit/asphyxia");
    require("unit/apocalypse");
    require("unit/tera");
    require("unit/nihilo");
    require("unit/narwhal");
    //air sapper
    require("unit/AirSapper/winglet");
    require("unit/AirSapper/moth");
    require("unit/AirSapper/VenomBee");
    require("unit/AirSapper/phantom");
    require("unit/AirSapper/skyline");

    require("game/items");
    require("other/status");

    //re-coming soon...
    require("game/challengeMap/cmain");*/
    require("game/TD/tmain");

    require("tree");
}
//根据游戏设置的语言改模组介绍和名字
lib.mod.meta.displayName = lib.getMessage('mod', 'displayName');
lib.mod.meta.description = lib.getMessage('mod', 'description');