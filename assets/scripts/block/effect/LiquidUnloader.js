/**
 * @author guiY<guiYMOUR>
 * @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
 */
//引用部分，类似import，对应的是exports导出
const items = require("game/items");
const lib = require("blib");
const LiquidUnloader = lib.getClass("ExtraUtilities.worlds.blocks.liquid.LiquidUnloader");

//use Java
const lu = new LiquidUnloader("liquid-unloader");
lu.speed = 6;
lu.health = 70;
lu.liquidCapacity = 10;
lu.requirements = ItemStack.with(
    Items.metaglass, 10,
    Items.silicon, 20,
    items.crispSteel, 10
);
lu.buildVisibility = BuildVisibility.shown;
lu.category = Category.effect;
exports.lu = lu;