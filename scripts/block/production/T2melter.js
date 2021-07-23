const lib = require("blib");

const T2melter = extendContent(AttributeSmelter, "T2melter", {});
lib.setBuildingSimple(T2melter, AttributeSmelter.AttributeSmelterBuild, {
    draw(){
        this.super$draw();
        Draw.color(this.liquids.current().color);
        Draw.alpha(this.liquids.total() / this.block.liquidCapacity);
        Draw.rect(Core.atlas.find("btm-T2melter-liquid"), this.x, this.y);
        Draw.color();
    },
});
T2melter.outputLiquid = new LiquidStack(Liquids.slag, 5.5);
T2melter.craftTime = 10;
T2melter.size = 2;
T2melter.health = 200*2*2;
T2melter.hasPower = true;
T2melter.hasLiquids = true;
T2melter.consumes.power(1.5);
T2melter.consumes.item(Items.scrap, 2);
T2melter.requirements = ItemStack.with(
    Items.lead, 60,
    Items.titanium, 40,
    Items.silicon, 30,
    Items.graphite, 60
);
T2melter.itemCapacity = 10;
T2melter.boostScale = 0.55;
T2melter.buildVisibility = BuildVisibility.shown;
T2melter.category = Category.crafting;

exports.T2melter = T2melter;