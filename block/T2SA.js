const lib = require("blib");

const T2SA = extendContent(GenericSmelter, "T2-SA", {});
lib.setBuildingSimple(T2SA, GenericSmelter.SmelterBuild, {});
T2SA.craftEffect = Fx.smeltsmoke;
T2SA.outputItem = new ItemStack(Items.surgeAlloy, 2);
T2SA.craftTime = 66;
T2SA.size = 4;
T2SA.hasPower = true;

T2SA.consumes.power(6);
T2SA.consumes.items(ItemStack.with(
    Items.copper, 5,
    Items.lead, 7,
    Items.titanium, 4,
    Items.silicon, 4
));
T2SA.requirements = ItemStack.with(
    Items.silicon, 100,
    Items.lead, 120,
    Items.titanium, 90,
    Items.thorium, 80
);
T2SA.itemCapacity = 14;
T2SA.buildVisibility = BuildVisibility.shown;
T2SA.category = Category.crafting;

exports.T2SA = T2SA;