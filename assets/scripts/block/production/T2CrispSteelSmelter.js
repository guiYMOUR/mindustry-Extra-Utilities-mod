//引用物品(类似import
const items = require("game/items");

//更改原版的塑钢效果颜色变为脆钢色
const crispSteelSmoke = new Effect(40, cons(e => {
    Angles.randLenVectors(e.id, 6, 5 + e.fin() * 8, new Floatc2({
        get: (x, y) => {
            Draw.color(items.crispSteel.color, Color.gray, e.fin());
            Fill.square(e.x + x, e.y + y, 0.2 + e.fout() * 2, 45);
        }
    }));
}));
const crispSteelBurn = new Effect(40, cons(e => {
    Angles.randLenVectors(e.id, 5, 3 + e.fin() * 5, new Floatc2({
        get: (x, y) => {
            Draw.color(items.crispSteel.color, Color.gray, e.fin());
            Fill.circle(e.x + x, e.y + y, e.fout());
        }
    }));
}));

const T2CSm = extend(GenericCrafter, "T2CSm", {});
T2CSm.hasItems = true;
T2CSm.liquidCapacity = 30;
T2CSm.itemCapacity = 20;
T2CSm.craftTime = 60;
T2CSm.outputItem = new ItemStack(items.crispSteel, 5);
T2CSm.size = 3;
T2CSm.health = 420;
T2CSm.hasPower = true;
T2CSm.hasLiquids = true;
T2CSm.craftEffect = crispSteelSmoke;
T2CSm.updateEffect = crispSteelBurn;
T2CSm.drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion(), new DrawRegion("-top"));

T2CSm.consumeLiquid(Liquids.water, 0.05);
T2CSm.consumePower(6);
T2CSm.consumeItems(ItemStack.with(
    Items.metaglass, 2,
    Items.titanium, 3,
));
T2CSm.requirements = ItemStack.with(
    Items.silicon, 55,
    Items.lead, 105,
    Items.graphite, 60,
    items.crispSteel, 40,
);
T2CSm.buildVisibility = BuildVisibility.shown;
T2CSm.category = Category.crafting;

//导出，为上科技树准备
exports.T2CSm = T2CSm;