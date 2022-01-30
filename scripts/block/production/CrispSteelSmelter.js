const items = require("game/items");

const crispSteelSmelter = extendContent(GenericCrafter, "crisp-steel-smelter", {});
crispSteelSmelter.craftEffect = Fx.smeltsmoke;
crispSteelSmelter.outputItem = new ItemStack(items.crispSteel, 2);
crispSteelSmelter.craftTime = 60;
crispSteelSmelter.size = 2;
crispSteelSmelter.hasPower = true;
crispSteelSmelter.drawer = new DrawSmelter(Color.valueOf("e0ecff"));
crispSteelSmelter.consumes.power(2.5);
crispSteelSmelter.consumes.items(ItemStack.with(
    Items.metaglass, 3,
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