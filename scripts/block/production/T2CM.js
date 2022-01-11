const T2CM = extendContent(AttributeSmelter, "T2-CM", {});
T2CM.buildType = prov(() => {
    var totalProgress = 0;
    
    const block = T2CM;
    var x = 0, y =0;
    
    function getTotalProgress(){ return totalProgress; }
    function setTotalProgress(v){ totalProgress = v; }
    return new JavaAdapter(AttributeSmelter.AttributeSmelterBuild, {
        limitProgress(){
            totalProgress = totalProgress < Math.PI ? totalProgress + this.delta() * 0.025 : 0;
        },
        updateTile(){
            //this.super$updateTile();
            if(this.consValid()){
                var cl = this.block.consumes.get(ConsumeType.liquid);
                var use = Math.min(cl.amount * this.edelta(), block.liquidCapacity - this.liquids.get(block.outputLiquid.liquid));
                this.limitProgress();
                this.progress += use / cl.amount;
                this.liquids.add(block.outputLiquid.liquid, use);
                if(this.progress >= block.craftTime){
                    this.consume();
                    this.progress %= block.craftTime;
                }
            }

            this.dumpLiquid(block.outputLiquid.liquid);
        },
        draw(){
            x = this.x;
            y = this.y;
            Draw.rect(Core.atlas.find("btm-T2-CM-bottom"),x,y);
            Draw.color(Liquids.water.color);
            Draw.alpha(this.liquids.get(Liquids.water) / block.liquidCapacity);
            Draw.rect(Core.atlas.find("btm-T2-CM-liquid2"), x, y);
            Draw.color(block.outputLiquid.liquid.color);
            Draw.alpha(this.liquids.get(block.outputLiquid.liquid) / block.liquidCapacity);
            Draw.rect(Core.atlas.find("btm-T2-CM-liquid"), x, y);
            Draw.color();
            Draw.rect(Core.atlas.find("btm-T2-CM-s"), x, y, 30 + 90 * Math.sin(totalProgress));
            Draw.rect(Core.atlas.find("btm-T2-CM-a"),x,y);
        },
        write(write) {
            this.super$write(write);
            write.f(totalProgress);
        },
        read(read, revision) {
            this.super$read(read, revision);
            totalProgress = read.f();
        },
    }, T2CM);
});
T2CM.outputLiquid = new LiquidStack(Liquids.cryofluid, 72);
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
T2CM.consumes.power(3);
T2CM.consumes.item(Items.titanium);
T2CM.consumes.liquid(Liquids.water, 0.6);
T2CM.requirements = ItemStack.with(
    Items.lead, 90,
    Items.graphite, 50,
    Items.silicon, 60,
    Items.titanium, 80
);
T2CM.itemCapacity = 14;
T2CM.buildVisibility = BuildVisibility.shown;
T2CM.category = Category.crafting;

exports.T2CM = T2CM;
