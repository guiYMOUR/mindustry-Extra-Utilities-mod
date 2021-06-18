const tiDrill = extendContent(Drill, "tiDrill", {});
tiDrill.requirements = ItemStack.with(
    Items.copper, 20,
    Items.graphite, 18,
    Items.titanium, 15,
);
tiDrill.buildVisibility = BuildVisibility.shown;
tiDrill.category = Category.production;
tiDrill.drillTime = 350;
tiDrill.size = 2;
tiDrill.tier = 4;
tiDrill.consumes.liquid(Liquids.water, 0.06).boost()
exports.tiDrill = tiDrill;

function eff1(color){
    return new Effect(12, cons(e => {
        Draw.color(color);
        Lines.stroke(3 * e.fout());
        Lines.circle(e.x, e.y, 10 * e.fout());
    }));
};
const drill = extendContent(Drill, "drill", {});
drill.buildType = prov(() => {
    var work = false;
    return new JavaAdapter(Drill.DrillBuild, {
        updateTile(){
            //this.super$updateTile();
            if(this.dominantItem == null){
                return;
            }
            if(this.timer.get(this.block.timerDump, this.block.dumpTime)){
                this.dump(this.dominantItem);
            }
            this.timeDrilled += this.warmup * this.delta();
            if(this.items.total() < this.block.itemCapacity && this.dominantItems > 0 && this.consValid()){
                var speed = 1;
                if(this.cons.optionalValid()){
                    speed = this.block.liquidBoostIntensity;
                }
                speed *= this.efficiency();
                this.lastDrillSpeed = (speed * this.dominantItems * this.warmup) / (this.block.drillTime + this.block.hardnessDrillMultiplier * this.dominantItem.hardness);
                this.warmup = Mathf.lerpDelta(this.warmup, speed, this.block.warmupSpeed);
                this.progress += this.delta() * this.dominantItems * speed * this.warmup;
                if(Mathf.chanceDelta(this.block.updateEffectChance * this.warmup))
                    this.block.updateEffect.at(this.x + Mathf.range(this.block.size * 2), this.y + Mathf.range(this.block.size * 2));
            }else{
                this.lastDrillSpeed = 0;
                this.warmup = Mathf.lerpDelta(this.warmup, 0, this.block.warmupSpeed);
                return;
            }
            var delay = this.block.drillTime + this.block.hardnessDrillMultiplier * this.dominantItem.hardness;
            if(this.dominantItems > 0 && this.progress >= delay && this.items.total() < this.block.itemCapacity){
                this.offload(this.dominantItem);
                this.index ++;
                this.progress %= delay;
                var eff = eff1(this.dominantItem.color);
                if(Mathf.chance(0.2)){
                    eff.at(this.x + Mathf.range(this.block.size * 2), this.y + Mathf.range(this.block.size * 2));
                    if(Mathf.chance(0.5)){
                        Fx.mineHuge.at(this.x + Mathf.range(this.block.size), this.y + Mathf.range(this.block.size), this.dominantItem.color);
                    }
                }
            }
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
drill.updateEffectChance = 0.01;
drill.drillEffect = Fx.none;
drill.rotateSpeed = 5;
drill.warmupSpeed = 0.01;
drill.liquidBoostIntensity = 1.8;
drill.consumes.power(4);
drill.consumes.add(new ConsumeLiquidFilter(boolf(liquid => liquid.temperature <= 0.5 && liquid.flammability < 0.1), 0.1)).update(true);
drill.buildCostMultiplier = 0.6;
exports.drill = drill;
