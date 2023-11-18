const items = require("game/items");

const T2kiln = extend(AttributeCrafter, "T2kiln", {});
T2kiln.craftEffect = Fx.smeltsmoke;
T2kiln.outputItem = new ItemStack(Items.metaglass, 9);
T2kiln.craftTime = 60;
T2kiln.size = 3;
T2kiln.hasPower = true;
T2kiln.drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc099")));
T2kiln.flameColor = Color.valueOf("ffc099");
T2kiln.consumePower(2.5);
T2kiln.consumeItems(ItemStack.with(
    Items.lead, 4,
    Items.sand, 5,
    Items.pyratite, 1
));
T2kiln.requirements = ItemStack.with(
    Items.silicon, 60,
    Items.copper, 120,
    Items.lead, 100,
    Items.graphite, 80,
    items.crispSteel, 60
);
T2kiln.itemCapacity = 18;
T2kiln.boostScale = 0.4;
T2kiln.buildVisibility = BuildVisibility.shown;
T2kiln.category = Category.crafting;

exports.T2kiln = T2kiln;
