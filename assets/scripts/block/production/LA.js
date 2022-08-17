const lib = require("blib");
const items = require("game/items");
const craft = new Effect(42, cons(e => {
    Draw.color(Pal.surge, items.lightninAlloy.color, e.fin());
    Draw.alpha(0.9);
    if(e.time < e.lifetime/2){
        Lines.stroke(28 * Math.min(e.fin(), 0.5));
        Lines.circle(e.x, e.y, 14 * e.fout());
    } else {
        Fill.circle(e.x, e.y, 14 * e.fout(0.5));
    }
}));

const LA = extend(GenericCrafter, "LA", {});

const block = LA;

lib.setBuildingSimple(LA, GenericCrafter.GenericCrafterBuild, {
    updateTile(){
        this.super$updateTile();
        var x = this.x;
        var y = this.y;
        if(Mathf.chanceDelta(block.updateEffectChance/4) && this.consValid()){
            var rx = Mathf.range(block.size * 4);
            var ry = Mathf.range(block.size * 4);
            for(var i = 0; i < 3; i++){
                var a = Mathf.range(180);
                Lightning.create(this.team, items.lightninAlloy.color, 2, x + rx, y + ry, a, Mathf.random(5, 8));
            }
            Sounds.spark.at(x + rx, y + ry, Mathf.random(0.5, 0.7));
        }
    },
    consValid(){
        return this.efficiency > 0;
    },
    draw(){
        this.super$draw();
        Draw.color(Liquids.cryofluid.color);
        Draw.alpha(this.liquids.get(Liquids.cryofluid) / block.liquidCapacity);
        Draw.rect(Core.atlas.find(lib.aModName + "-LA-liquid"), this.x, this.y);
        Draw.color();
    },
});
LA.craftEffect = craft;
LA.outputItem = new ItemStack(items.lightninAlloy, 2);
LA.craftTime = 120;
LA.size = 4;
LA.hasPower = true;
LA.drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
LA.consumePower(7);
LA.consumeItems(ItemStack.with(
    Items.surgeAlloy, 3,
    Items.phaseFabric, 2,
    Items.blastCompound, 3
));
LA.consumeLiquid(Liquids.cryofluid, 0.1);
LA.requirements = ItemStack.with(
    Items.silicon, 135,
    Items.lead, 200,
    Items.titanium, 120,
    Items.thorium, 100,
    Items.surgeAlloy, 55
);
LA.itemCapacity = 12;
LA.buildVisibility = BuildVisibility.shown;
LA.category = Category.crafting;

exports.LA = LA;