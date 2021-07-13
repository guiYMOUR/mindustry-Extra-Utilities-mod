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
                this.dump(this.items.has(this.dominantItem) ? this.dominantItem : null);
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
drill.consumes.add(new ConsumeLiquidFilter(boolf(liquid => liquid.temperature <= 0.5 && liquid.flammability < 0.1), 0.1));
drill.buildCostMultiplier = 0.6;
exports.drill = drill;

const shovel = extendContent(Drill, "shovel", {});
shovel.buildType = prov(() => {
    return new JavaAdapter(Drill.DrillBuild, {
        draw(){
            Draw.color(Color.valueOf("ffd06b"));
            Draw.alpha(this.warmup);
            Draw.rect(Core.atlas.find("btm-shovel-rotator"), this.x, this.y, this.timeDrilled * 6);
            Draw.alpha(1);
            Draw.color();
            Draw.rect(Core.atlas.find("btm-shovel"), this.x, this.y);
            Draw.color(Pal.surge);
            Draw.alpha(this.warmup * 0.6 * (1 - 0.3 + Mathf.absin(Time.time, 3, 0.3)));
            Draw.blend(Blending.additive);
            Draw.rect(Core.atlas.find("btm-shovel-rim"), this.x, this.y);
            Draw.blend();
            Draw.color();
        },
    }, shovel);
});
shovel.requirements = ItemStack.with(
    Items.metaglass, 45,
    Items.silicon, 60,
    Items.titanium, 75
);
shovel.buildVisibility = BuildVisibility.shown;
shovel.category = Category.production;
shovel.drillTime = 30;
shovel.size = 3;
//shovel.drawRim = true;
shovel.hasPower = true;
shovel.tier = 0;
shovel.updateEffect = Fx.mineBig;
shovel.updateEffectChance = 0.05;
shovel.drillEffect = Fx.none;
shovel.warmupSpeed = 10;
shovel.hasLiquids = false;
shovel.liquidBoostIntensity = 1;
shovel.consumes.power(2);
shovel.buildCostMultiplier = 0.8;
exports.shovel = shovel;

const boof = 2;
const testDrill = extendContent(BeamDrill, "beam-drill", {
    drawPlace(x, y, rotation, valid){
        this.drawPotentialLinks(x, y);
        this.super$drawPlace(x, y, rotation, valid);
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.boostEffect, boof, StatUnit.timesSpeed);
    },
});
testDrill.buildType = prov(() => {
    return new JavaAdapter(BeamDrill.BeamDrillBuild, {
        delta(){
            var boost = this.liquids.total() > 1 ? boof : 1;
            return Time.delta * this.timeScale * boost;
        },
    }, testDrill);
});
testDrill.drillTime = 150;
testDrill.tier = 5;
testDrill.size = 2;
testDrill.range = 5;
testDrill.hasPower = true;
testDrill.drawArrow = true;
testDrill.consumes.power(1);
testDrill.consumes.liquid(Liquids.water, 0.03).boost();
testDrill.requirements = ItemStack.with(
    Items.copper, 85,
    Items.graphite, 55,
    Items.silicon, 55
);
testDrill.buildVisibility = BuildVisibility.shown;
testDrill.category = Category.production;
exports.testDrill = testDrill;

const slagE = extendContent(SolidPump, "slag-extractor", {});
slagE.result = Liquids.slag;
slagE.pumpAmount = 0.1;
slagE.size = 2;
slagE.liquidCapacity = 30;
slagE.rotateSpeed = 1.4;
slagE.baseEfficiency = 1;
slagE.attribute = Attribute.heat;
slagE.consumes.power(2);
slagE.requirements = ItemStack.with(
    Items.metaglass, 40,
    Items.graphite, 35,
    Items.silicon, 25,
    Items.titanium, 25
);
slagE.buildVisibility = BuildVisibility.shown;
slagE.category = Category.production;
exports.slagE = slagE;

//6.0, 7.0 not this type.
const T2CU = extendContent(AttributeCrafter, "T2CU", {});
T2CU.outputItem = new ItemStack(Items.sporePod, 2);
T2CU.craftTime = 120;
T2CU.size = 3;
T2CU.hasLiquids = true;
T2CU.hasPower = true;
T2CU.hasItems = true;
T2CU.consumes.power(1.5);
T2CU.consumes.liquid(Liquids.water, 20/60);
T2CU.drawer = new DrawCultivator();
T2CU.requirements = ItemStack.with(
    Items.copper, 40,
    Items.graphite, 35,
    Items.silicon, 25,
    Items.titanium, 30
);
T2CU.buildVisibility = BuildVisibility.shown;
T2CU.category = Category.production;
T2CU.envRequired |= Env.spores;
T2CU.attribute = Attribute.spores;
T2CU.legacyReadWarmup = true;
T2CU.maxBoost = 3;

exports.T2CU = T2CU;
