//引用部分，类似import，对应的是exports导出
const items = require("game/items");
const lib = require("blib");
//写一个有地板加成的液体工厂
const T2CM = extend(AttributeCrafter, "T2-CM", {});
T2CM.buildType = prov(() => {
    
    const block = T2CM;
    var x = 0, y =0;
    return new JavaAdapter(AttributeCrafter.AttributeCrafterBuild, {
        draw(){
            x = this.x;
            y = this.y;
            Draw.rect(Core.atlas.find(lib.aModName + "-T2-CM-bottom"),x,y);
            Draw.color(Liquids.water.color);
            Draw.alpha(this.liquids.get(Liquids.water) / block.liquidCapacity);
            Draw.rect(Core.atlas.find(lib.aModName + "-T2-CM-liquid2"), x, y);
            Draw.color(block.outputLiquid.liquid.color);
            Draw.alpha(this.liquids.get(block.outputLiquid.liquid) / block.liquidCapacity);
            Draw.rect(Core.atlas.find(lib.aModName + "-T2-CM-liquid"), x, y);
            Draw.color();
            Draw.rect(Core.atlas.find(lib.aModName + "-T2-CM-s"), x, y, 30 + 90 * Math.sin(this.totalProgress/10));
            Draw.rect(Core.atlas.find(lib.aModName + "-T2-CM-a"),x,y);
        },
    }, T2CM);
});
T2CM.outputLiquid = new LiquidStack(Liquids.cryofluid, 0.6);
T2CM.craftTime = 120;
T2CM.size = 3;
T2CM.hasPower = true;
T2CM.hasItems = true;
T2CM.hasLiquids = true;
T2CM.rotate = false;
T2CM.solid = true;
T2CM.outputsLiquid = true;
T2CM.liquidCapacity = 54;
T2CM.attribute = Attribute.water;
T2CM.boostScale = 0.6;
T2CM.consumePower(3);
T2CM.consumeItem(Items.titanium);
T2CM.consumeLiquid(Liquids.water, 0.6);
T2CM.requirements = ItemStack.with(
    Items.lead, 90,
    Items.graphite, 50,
    Items.silicon, 70,
    items.crispSteel, 60
);
T2CM.itemCapacity = 14;
T2CM.buildVisibility = BuildVisibility.shown;
T2CM.category = Category.crafting;

exports.T2CM = T2CM;