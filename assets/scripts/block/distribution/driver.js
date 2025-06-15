/**
 * @author guiY<guiYMOUR>
 * @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
 */
const lib = require("blib");
const LiquidMassDriver = lib.getClass("ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver");

const driver = new LiquidMassDriver("ld");
driver.range = 45 * 8;
driver.reload = 150;
driver.knockback = 3;
driver.hasPower = true;
driver.consumePower(1.8);
driver.size = 2;
driver.liquidCapacity = 500;
driver.requirements = ItemStack.with(
    Items.metaglass, 85,
    Items.silicon, 80,
    Items.titanium, 80,
    Items.thorium, 55
);
driver.buildVisibility = BuildVisibility.shown;
driver.category = Category.liquid;

exports.driver = driver;