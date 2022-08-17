//引用部分，类似import，对应的是exports导出
const items = require("game/items");

const crispSteelSmelter = extend(GenericCrafter, "crisp-steel-smelter", {});
crispSteelSmelter.craftEffect = Fx.smeltsmoke;
crispSteelSmelter.outputItem = new ItemStack(items.crispSteel, 2);
crispSteelSmelter.craftTime = 60;
crispSteelSmelter.size = 2;
crispSteelSmelter.hasPower = true;
crispSteelSmelter.drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("e0ecff")));
crispSteelSmelter.consumePower(2.5);
crispSteelSmelter.consumeItems(ItemStack.with(
    Items.metaglass, 1,
    Items.titanium, 2,
));
crispSteelSmelter.requirements = ItemStack.with(
    Items.silicon, 90,
    Items.lead, 100,
    Items.titanium, 95,
);
crispSteelSmelter.itemCapacity = 10;
crispSteelSmelter.buildVisibility = BuildVisibility.shown;
crispSteelSmelter.category = Category.crafting;

exports.crispSteelSmelter = crispSteelSmelter;