const lib = require("blib");
const T2PC = extendContent(GenericCrafter, "T2-PC", {});
lib.setBuildingSimple(T2PC, GenericCrafter.GenericCrafterBuild, {});
T2PC.hasItems = true;
T2PC.liquidCapacity = 90;
T2PC.craftTime = 54;
T2PC.outputItem = new ItemStack(Items.plastanium, 3);
T2PC.size = 3;
T2PC.health = 420;
T2PC.hasPower = true;
T2PC.hasLiquids = true;
T2PC.craftEffect = Fx.formsmoke;
T2PC.updateEffect = Fx.plasticburn;
T2PC.drawer = new DrawAnimation();

T2PC.consumes.liquid(Liquids.oil, 0.35);
T2PC.consumes.power(6.5);
T2PC.consumes.item(Items.titanium, 4);
T2PC.requirements = ItemStack.with(
    Items.silicon, 105,
    Items.lead, 155,
    Items.graphite, 85,
    Items.titanium, 100,
    Items.plastanium, 10
);
T2PC.buildVisibility = BuildVisibility.shown;
T2PC.category = Category.crafting;

exports.T2PC = T2PC;