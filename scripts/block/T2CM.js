const T2CM = extendContent(AttributeSmelter, "T2-CM", {});
T2CM.buildType = prov(() => {
    var totalProgress = 0;
    function getTotalProgress(){ return totalProgress; }
    function setTotalProgress(v){ totalProgress = v; }
    return new JavaAdapter(AttributeSmelter.AttributeSmelterBuild, {
        updateTile(){
            //this.super$updateTile();
            if(this.consValid()){
                var cl = this.block.consumes.get(ConsumeType.liquid);
                var use = Math.min(cl.amount * this.edelta(), this.block.liquidCapacity - this.liquids.get(this.block.outputLiquid.liquid));
                totalProgress += this.delta();
                this.progress += use / cl.amount;
                this.liquids.add(this.block.outputLiquid.liquid, use);
                if(this.progress >= this.block.craftTime){
                    this.consume();
                    this.progress %= this.block.craftTime;
                }
            }

            this.dumpLiquid(this.block.outputLiquid.liquid);
        },
        draw(){
            Draw.rect(Core.atlas.find("btm-T2-CM-bottom"),this.x,this.y);
            Draw.color(Liquids.water.color);
            Draw.alpha(this.liquids.get(Liquids.water) / this.block.liquidCapacity);
            Draw.rect(Core.atlas.find("btm-T2-CM-liquid2"), this.x, this.y);
            Draw.color(this.block.outputLiquid.liquid.color);
            Draw.alpha(this.liquids.get(this.block.outputLiquid.liquid) / this.block.liquidCapacity);
            Draw.rect(Core.atlas.find("btm-T2-CM-liquid"), this.x, this.y);
            Draw.color();
            Draw.rect(Core.atlas.find("btm-T2-CM-s"), this.x, this.y, 0 + totalProgress * 2);
            Draw.rect(Core.atlas.find("btm-T2-CM-a"),this.x,this.y);
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
T2CM.outputLiquid = new LiquidStack(Liquids.cryofluid, 48);
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
T2CM.consumes.liquid(Liquids.water, 0.4);
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
