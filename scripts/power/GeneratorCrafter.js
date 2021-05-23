const craftTime = 126;
const itemDuration = 90;
const input = Items.coal;
const output = Items.pyratite;
const inputLiquid = Liquids.water;
const amount = 0.055;
const GC = extendContent(SingleTypeGenerator, "GC", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.productionTime, craftTime / 60, StatUnit.seconds);
        this.stats.add(Stat.output, output);
    },
    outputsItems(){
        return true;
    },
});
GC.buildType = prov(() => {
    var p = 0;
    var gp = 0;
    var full = false;
    function use(entity){
        return Math.min(amount * entity.edelta(), entity.block.liquidCapacity);
    }
    function update(entity){
        entity.liquids.remove(inputLiquid, Math.min(use(entity), entity.liquids.get(inputLiquid)));
    }
    return new JavaAdapter(ItemLiquidGenerator.ItemLiquidGeneratorBuild, {
        updateTile(){
            var cons = this.consValid();
            full = this.items.get(output) >= this.block.itemCapacity;
            if(cons && !full){
                p += this.getProgressIncrease(craftTime);
                gp += this.getProgressIncrease(itemDuration);
                update(this);
            }
            if(p > 1 && !full){
                this.items.add(output, 1);
                p %= 1;
            }
            if(gp > 1 && !full){
                this.consume();
                gp %= 1;
                this.block.generateEffect.at(this.x + Mathf.range(3), this.y + Mathf.range(3));
            }
            this.productionEfficiency = Mathf.num(cons) * Mathf.num(!full);
            this.dump(output);
            this.heat = Mathf.lerpDelta(this.heat, cons && !full ? 1 : 0, 0.05)
        },
        getPowerProduction(){
            return Mathf.num(this.consValid()) * this.block.powerProduction * Mathf.num(!full);
        },
        drawStatus(){
            if(this.block.enableDrawStatus && this.block.consumes.any()){
                var multiplier = 1;
                var brcx = this.x + (this.block.size * Vars.tilesize / 2) - (Vars.tilesize * multiplier / 2);
                var brcy = this.y - (this.block.size * Vars.tilesize / 2) + (Vars.tilesize * multiplier / 2);

                Draw.z(Layer.power + 1);
                Draw.color(Pal.gray);
                Fill.square(brcx, brcy, 2.5 * multiplier, 45);
                Draw.color(this.consValid() && !full ? Color.green : Color.red);
                Fill.square(brcx, brcy, 1.5 * multiplier, 45);
                Draw.color();
            }
        },
        write(write){
            this.super$write(write);
            write.f(p);
            write.f(gp);
        },
        read(read, revision){
            this.super$read(read, revision);
            p = read.f();
            gp = read.f();
        },
    },GC);
});
GC.requirements = ItemStack.with(
    Items.copper, 65,
    Items.lead, 50,
    Items.graphite, 55,
    Items.silicon, 50
);
GC.buildVisibility = BuildVisibility.shown;
GC.category = Category.crafting;
GC.itemCapacity = 10;
GC.powerProduction = 3.8;
GC.itemDuration = itemDuration;
GC.hasLiquids = true;
GC.hasItems = true;
GC.size = 2;
GC.ambientSound = Sounds.steam;
GC.ambientSoundVolume = 0.02;
GC.consumes.item(input).optional(false, false);
GC.consumes.liquid(inputLiquid, amount);
exports.GC = GC;