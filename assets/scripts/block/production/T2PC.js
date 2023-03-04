const lib = require("blib");
const items = require("game/items");
const T2PC = extend(GenericCrafter, "T2-PC", {});

T2PC.hasItems = true;
T2PC.liquidCapacity = 90;
T2PC.itemCapacity = 20;
T2PC.craftTime = 90;
T2PC.outputItem = new ItemStack(Items.plastanium, 5);
T2PC.size = 3;
T2PC.health = 420;
T2PC.hasPower = true;
T2PC.hasLiquids = true;
T2PC.craftEffect = Fx.formsmoke;
T2PC.updateEffect = Fx.plasticburn;
T2PC.drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawLiquidRegion(), new DrawFrames(), new DrawFade());

T2PC.consumeLiquid(Liquids.oil, 0.5);
T2PC.consumePower(6.5);
T2PC.consumeItem(Items.titanium, 8);
T2PC.requirements = ItemStack.with(
    Items.silicon, 105,
    Items.lead, 155,
    Items.graphite, 60,
    items.crispSteel, 95,
    Items.plastanium, 50
);
T2PC.buildVisibility = BuildVisibility.shown;
T2PC.category = Category.crafting;

exports.T2PC = T2PC;