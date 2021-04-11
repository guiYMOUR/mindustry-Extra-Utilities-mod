const drill = extendContent(Drill, "drill", {});
drill.buildType = prov(() => {
    var work = false;
    return new JavaAdapter(Drill.DrillBuild, {
        updateTile(){
            this.super$updateTile();
            if(this.liquids.get(this.liquids.current()) / this.block.liquidCapacity >= 0.08 && !work) work = true;
            if(this.liquids.get(this.liquids.current()) / this.block.liquidCapacity < 0.08 && work){
                this.liquids.clear();
                work = false;
            }
        },
        efficiency(){
            if(!this.enabled) return 0;
            return this.power.status / (this.liquids.current().temperature * 2.05) * Mathf.num(work);
        },
        write(write) {
            this.super$write(write);
            write.bool(work);
        },
        read(read, revision) {
            this.super$read(read, revision);
            work = read.bool();
        },
    }, drill);
});
drill.requirements = ItemStack.with(
    Items.copper, 180,
    Items.graphite, 165,
    Items.silicon, 155,
    Items.titanium, 150,
    Items.thorium, 90,
    Items.plastanium, 155
);
drill.buildVisibility = BuildVisibility.shown;
drill.category = Category.production;
drill.drillTime = 210;
drill.size = 4;
drill.drawRim = true;
drill.hasPower = true;
drill.tier = 8;
drill.updateEffect = Fx.pulverizeRed;
drill.updateEffectChance = 0.05;
drill.drillEffect = Fx.mineHuge;
drill.rotateSpeed = 7;
drill.warmupSpeed = 0.01;
drill.liquidBoostIntensity = 1.8;
drill.consumes.power(4);
drill.consumes.add(new ConsumeLiquidFilter(boolf(liquid => liquid.temperature <= 0.5 && liquid.flammability < 0.1), 0.1)).update(true);
drill.buildCostMultiplier = 0.6;
exports.drill = drill;