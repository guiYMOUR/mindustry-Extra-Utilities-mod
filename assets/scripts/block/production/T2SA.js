const lib = require("blib");
const items = require("game/items");
const drawer = require("drawer/DrawPower");

const T2SA = extend(AttributeCrafter, "T2-SA", {});
T2SA.craftEffect = Fx.smeltsmoke;
T2SA.outputItem = new ItemStack(Items.surgeAlloy, 5);
T2SA.craftTime = 90;
T2SA.size = 4;
T2SA.hasPower = true;
T2SA.drawer = new DrawMulti(new DrawRegion("-bottom"), drawer.DrawPower(Color.valueOf("feb380")), new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
T2SA.consumePower(6);
T2SA.consumeItems(ItemStack.with(
    Items.copper, 5,
    items.crispSteel, 5,
    Items.silicon, 4,
    Items.pyratite, 1
));
T2SA.requirements = ItemStack.with(
    Items.silicon, 100,
    Items.lead, 120,
    items.crispSteel, 60,
    Items.thorium, 80,
    Items.surgeAlloy, 8
);
T2SA.itemCapacity = 20;
T2SA.boostScale = 0.35;
T2SA.buildVisibility = BuildVisibility.shown;
T2SA.category = Category.crafting;

exports.T2SA = T2SA;