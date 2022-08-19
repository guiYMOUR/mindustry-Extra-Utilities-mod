const lib = require("blib");

const tiDrill = extend(Drill, "tiDrill", {});
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
tiDrill.consumeLiquid(Liquids.water, 0.06).boost();
exports.tiDrill = tiDrill;

const defaultData = {color:Items.sand.color, fout:false, rad:4};
const eff1 = new Effect(12, cons(e => {
    let data = e.data ? e.data: defaultData;
    Draw.color(data.color);
    Lines.stroke(3 * e.fout());
    if(data.fout){
        Lines.circle(e.x, e.y, data.rad * e.fout());
    } else {
        Lines.circle(e.x, e.y, data.rad * e.fin());
    }
}));
// function filter(liquid){
//     return liquid.temperature <= 0.5 && liquid.flammability < 0.1;
// }
// function boosters(speed, maxUsed, multiplier, baseTime){
//     return new JavaAdapter(StatValue, {
//         display(table){
//             table.row();
//             table.table(cons(c => {
//                 for(var i = 0; i < Vars.content.liquids().size; i++){
//                     var liquid = Vars.content.liquids().get(i);
//                     if(!filter(liquid)) continue;
//
//                     c.image(liquid.uiIcon).size(3 * 8).padRight(4).right().top();
//                     c.add(liquid.localizedName).padRight(10).left().top();
//                     c.table(Tex.underline, cons(bt => {
//                         bt.left().defaults().padRight(3).left();
//
//                         var speedRate = (baseTime ? 1 : 0) + maxUsed * multiplier * liquid.heatCapacity;
//                         var standardSpeed = baseTime ? speed : speed / (maxUsed * multiplier * 0.4);
//                         var result = standardSpeed / (speed / speedRate);
//                         bt.add(Core.bundle.format("stat.btm-drillSpeed", Strings.autoFixed(result, 2)));
//                     })).left().padTop(-9);
//                     c.row();
//                 }
//             })).colspan(table.getColumns());
//             table.row();
//         }
//     });
// }
//
// const drill = extend(Drill, "drill", {
//     setStats(){
//         this.super$setStats();
//         this.stats.add(Stat.booster, boosters(this.drillTime, this.consume.amount, this.liquidBoostIntensity, false));
//         this.stats.remove(Stat.boostEffect);
//     },
// });
//
// //by EB Wilson & gm, Optimizations Provided
// drill.buildType = prov(() => {
//     let work = false;
//     let warmup = 0;
//     let progress = 0;
//
//     let dominantItem = null;
//     let delta = 0;
//     let block = drill;
//     let size = 0;
//     let itemCapacity = 0;
//     let liquidCapacity = 0;
//     let drillTime = 0;
//     let hardnessDrillMultiplier = 0;
//     let liquidBoostIntensity = 0;
//     let warmupSpeed = 0;
//     let dumpTime = 0;
//     let timerDump = 0;
//     let x = 0, y = 0;
//     let liquids = null;
//     let items = null;
//     let dominantItems = 0;
//
//     let dmHardness = null;
//     let dmColor = null;
//     let current = null;
//
//     return new JavaAdapter(Drill.DrillBuild, {
//         create(blockParam, team){
//           this.super$create(blockParam, team)
//           size = block.size;
//           itemCapacity = block.itemCapacity;
//           liquidCapacity = block.liquidCapacity;
//           drillTime = block.drillTime;
//           hardnessDrillMultiplier = block.hardnessDrillMultiplier;
//           liquidBoostIntensity = block.liquidBoostIntensity;
//           warmupSpeed = block.warmupSpeed;
//           dumpTime = block.dumpTime;
//           timerDump = block.timerDump;
//           return this;
//         },
//
//         updateTile(){
//             x = this.x;
//             y = this.y;
//             this.warmup = warmup;
//             progress = this.progress;
//             delta = this.delta();
//             liquids = this.liquids;
//             items = this.items;
//             dominantItems = this.dominantItems;
//
//             dominantItem = this.dominantItem;
//             dmHardness = dominantItem.hardness;
//             dmColor = dominantItem.color;
//             current = liquids.current();
//
//             this.super$updateTile();
//             if(dominantItem == null){
//                 return;
//             }
//             if(this.timer.get(timerDump, dumpTime)){
//                 this.dump(items.has(dominantItem) ? dominantItem : null);
//             }
//             this.timeDrilled += warmup * delta;
//             if(items.total() < itemCapacity && dominantItems > 0 && this.consValid()){
//                 let speed = 1;
//                 if(this.cons.optionalValid()){
//                     speed = liquidBoostIntensity;
//                 }
//                 speed *= (this.efficiency() * (1 + (current.heatCapacity - 0.4) * 0.9) * liquidBoostIntensity * 0.3087 * Mathf.num(work));
//                 //limit display
//                 this.lastDrillSpeed = Math.min((speed * dominantItems * warmup) / (drillTime + hardnessDrillMultiplier * dmHardness), 60);
//                 warmup = Mathf.lerpDelta(warmup, speed, warmupSpeed);
//                 progress += delta * dominantItems * speed * warmup;
//                 if(Mathf.chanceDelta(block.updateEffectChance * warmup))
//                     block.updateEffect.at(x + Mathf.range(size * 2), y + Mathf.range(size * 2));
//             }else{
//                 this.lastDrillSpeed = 0;
//                 warmup = Mathf.lerpDelta(warmup, 0, warmupSpeed);
//                 return;
//             }
//             let delay = drillTime + hardnessDrillMultiplier * dmHardness;
//             if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
//                 this.offload(dominantItem);
//                 this.index ++;
//                 progress %= delay;
//
//                 if(Mathf.chance(0.2)){
//                     eff1.at(x + Mathf.range(size * 2), y + Mathf.range(size * 2), 0, {color:dmColor, fout:true, rad:10});
//                     if(Mathf.chance(0.5)){
//                         Fx.mineHuge.at(x + Mathf.range(size), y + Mathf.range(size), dmColor);
//                     }
//                 }
//             }
//
//             if(liquids.get(current) / liquidCapacity >= 0.08 && !work) work = true;
//             if(liquids.get(current) / liquidCapacity < 0.08 && work){
//                 liquids.clear();
//                 work = false;
//             }
//
//             //this.warmup = warmup;
//             this.progress = progress;
//         },
//         write(write) {
//             this.super$write(write);
//             write.bool(work);
//             write.f(warmup);
//         },
//         read(read, revision) {
//             this.super$read(read, revision);
//             work = read.bool();
//             warmup = read.f();
//         },
//     }, drill);
// });
// drill.requirements = ItemStack.with(
//     Items.copper, 180,
//     Items.graphite, 165,
//     Items.silicon, 155,
//     Items.titanium, 150,
//     Items.thorium, 90,
//     Items.plastanium, 155
// );
// drill.buildVisibility = BuildVisibility.shown;
// drill.category = Category.production;
// drill.itemCapacity = 20;
// drill.drillTime = 80;
// drill.size = 4;
// drill.drawRim = true;
// drill.hasPower = true;
// drill.tier = 10;
// drill.hardnessDrillMultiplier = 0;
// drill.updateEffect = Fx.pulverizeRed;
// drill.updateEffectChance = 0.01;
// drill.drillEffect = Fx.none;
// drill.rotateSpeed = 7;
// drill.warmupSpeed = 0.01;
// drill.liquidBoostIntensity = 1.8;
// drill.consumePower(4);
// drill.consume(new ConsumeLiquidFilter(boolf(liquid => filter(liquid)), 0.1));
// drill.buildCostMultiplier = 0.6;
// exports.drill = drill;

const shovel = extend(Drill, "shovel", {});
shovel.buildType = prov(() => {
    return new JavaAdapter(Drill.DrillBuild, {
        draw(){
            Draw.color(Color.valueOf("ffd06b"));
            Draw.alpha(this.warmup);
            Draw.rect(Core.atlas.find(lib.aModName + "-shovel-rotator"), this.x, this.y, this.timeDrilled * 6);
            Draw.alpha(1);
            Draw.color();
            Draw.rect(Core.atlas.find(lib.aModName + "-shovel"), this.x, this.y);
            Draw.color(Pal.surge);
            Draw.alpha(this.warmup * 0.6 * (1 - 0.3 + Mathf.absin(Time.time, 3, 0.3)));
            Draw.blend(Blending.additive);
            Draw.rect(Core.atlas.find(lib.aModName + "-shovel-rim"), this.x, this.y);
            Draw.blend();
            Draw.color();
        },
    }, shovel);
});
shovel.requirements = ItemStack.with(
    Items.metaglass, 55,
    Items.silicon, 105,
    Items.titanium, 80,
);
shovel.buildVisibility = BuildVisibility.shown;
shovel.category = Category.production;
shovel.drillTime = 36;
shovel.size = 3;
//shovel.drawRim = true;
shovel.hasPower = true;
shovel.tier = 0;
shovel.updateEffect = Fx.mineBig;
shovel.updateEffectChance = 0.05;
shovel.drillEffect = Fx.none;
shovel.warmupSpeed = 0.02;
shovel.hasLiquids = false;
shovel.liquidBoostIntensity = 1;
shovel.consumePower(2);
shovel.buildCostMultiplier = 0.8;
exports.shovel = shovel;

const boof = 2;
// const testDrill = extend(BeamDrill, "beam-drill", {
//     drawPlace(x, y, rotation, valid){
//         this.drawPotentialLinks(x, y);
//         this.super$drawPlace(x, y, rotation, valid);
//     },
//     setStats(){
//         this.super$setStats();
//         this.stats.add(Stat.boostEffect, boof, StatUnit.timesSpeed);
//     },
// });
// testDrill.buildType = prov(() => {
//     return new JavaAdapter(BeamDrill.BeamDrillBuild, {
//         delta(){
//             var boost = this.liquids.total() > 1 ? boof : 1;
//             return Time.delta * this.timeScale * boost;
//         },
//     }, testDrill);
// });
// testDrill.drillTime = 150;
// testDrill.tier = 5;
// testDrill.size = 2;
// testDrill.range = 5;
// testDrill.hasPower = true;
// testDrill.drawArrow = true;
// testDrill.consumes.power(1);
// testDrill.consumes.liquid(Liquids.water, 0.03).boost();
// testDrill.requirements = ItemStack.with(
//     Items.copper, 85,
//     Items.graphite, 55,
//     Items.silicon, 55
// );
// testDrill.buildVisibility = BuildVisibility.shown;
// testDrill.category = Category.production;
// exports.testDrill = testDrill;

var weBoost = 1.5;
var weItem = Items.graphite;
const weUseTime = 120;
const T2WE = extend(SolidPump, "T2-WE", {
    setStats(){
        this.stats.timePeriod = weUseTime;
        this.super$setStats();
        this.stats.add(Stat.boostEffect, weBoost, StatUnit.timesSpeed);
    },
});
T2WE.buildType = prov(() => {
    var timer = 0;
    return new JavaAdapter(SolidPump.SolidPumpBuild, {
        updateTile(){
            this.efficiency *= this.items.get(weItem) > 0 ? weBoost : 1;
            this.super$updateTile();
            var entity = this;
            if(entity.items.get(weItem) && entity.liquids.get(entity.liquids.current()) < T2WE.liquidCapacity && entity.power.status > 0.0001){
                timer += entity.power.status * entity.delta();
            }
            if(timer >= weUseTime){
                entity.consume();
                timer -= weUseTime;
            }
        },
        // efficiency(){
        //     if(!this.enabled) return 0;
        //     return this.items.get(weItem) > 0 ? weBoost : 1;
        // },
    }, T2WE);
});
T2WE.result = Liquids.water;
T2WE.pumpAmount = 0.28;
T2WE.size = 3;
T2WE.liquidCapacity = 60;
T2WE.rotateSpeed = 2;
//T2WE.baseEfficiency = 1;
T2WE.attribute = Attribute.water;
//T2WE.envRequired |= Env.groundWater;
T2WE.consumePower(5);
T2WE.consumeItem(weItem).boost();
T2WE.requirements = ItemStack.with(
    Items.metaglass, 50,
    Items.lead, 85,
    Items.graphite, 75,
    Items.silicon, 75,
    Items.titanium, 70
);
T2WE.buildVisibility = BuildVisibility.shown;
T2WE.category = Category.production;
exports.T2WE = T2WE;

const slagE = extend(SolidPump, "slag-extractor", {});
slagE.result = Liquids.slag;
slagE.pumpAmount = 0.1;
slagE.size = 2;
slagE.liquidCapacity = 30;
slagE.rotateSpeed = 1.4;
slagE.baseEfficiency = 1;
slagE.attribute = Attribute.heat;
slagE.consumePower(2);
slagE.requirements = ItemStack.with(
    Items.metaglass, 40,
    Items.graphite, 35,
    Items.silicon, 25,
    Items.titanium, 25
);
slagE.buildVisibility = BuildVisibility.shown;
slagE.category = Category.production;
exports.slagE = slagE;

const T2CU = extend(AttributeCrafter, "T2CU", {});
T2CU.outputItem = new ItemStack(Items.sporePod, 3);
T2CU.craftTime = 120;
T2CU.size = 3;
T2CU.hasLiquids = true;
T2CU.hasPower = true;
T2CU.hasItems = true;
T2CU.consumePower(1.5);
T2CU.consumeLiquid(Liquids.water, 24/60);
T2CU.drawer = new DrawMulti(
    new DrawRegion("-bottom"),
    new DrawLiquidTile(Liquids.water),
    new DrawDefault(),
    new DrawCultivator(),
    new DrawRegion("-top")
);
T2CU.requirements = ItemStack.with(
    Items.copper, 40,
    Items.graphite, 35,
    Items.silicon, 28,
    Items.titanium, 30
);
T2CU.buildVisibility = BuildVisibility.shown;
T2CU.category = Category.production;
T2CU.envRequired |= Env.spores;
T2CU.attribute = Attribute.spores;
T2CU.legacyReadWarmup = true;
T2CU.maxBoost = 3;
exports.T2CU = T2CU;

const blastOilExtractor = extend(Fracker, "blast-oil-extractor", {});
blastOilExtractor.buildType = prov(() => {
    return new JavaAdapter(Fracker.FrackerBuild, {
        updateTile(){
            this.super$updateTile();
            if(Mathf.chance(this.delta() * this.block.updateEffectChance) && this.efficiency > 0 && this.typeLiquid() < this.block.liquidCapacity - 0.001){
                var range = Mathf.range(this.block.size * 2);
                var range2 = Mathf.range(this.block.size * 2);
                eff1.at(this.getX() + range, this.getY() + range2, 0, {color:Items.blastCompound.color, fout:false, rad:12});
                Sounds.explosion.at(this.getX() + range, this.getY() + range2, Mathf.random(0.7, 1.2));
            }
        },
    }, blastOilExtractor);
});
Object.assign(blastOilExtractor, {
    result : Liquids.oil,
    updateEffect : Fx.pulverize,
    updateEffectChance : 0.04,
    //rotateSpeed : 2,
    liquidCapacity : 100,
    pumpAmount : 0.8,
    size : 4,
    attribute : Attribute.oil,
    baseEfficiency : 0.2,
    itemUseTime : 60,
    buildCostMultiplier : 0.8,
});
blastOilExtractor.consumeItem(Items.blastCompound);
blastOilExtractor.consumePower(6);
blastOilExtractor.consumeLiquid(Liquids.water, 0.4);
blastOilExtractor.requirements = ItemStack.with(
    Items.copper, 220,
    Items.lead, 220,
    Items.thorium, 135,
    Items.silicon, 155,
    Items.plastanium, 125,
    Items.surgeAlloy, 55
);
blastOilExtractor.buildVisibility = BuildVisibility.shown;
blastOilExtractor.category = Category.production;
exports.blastOilExtractor = blastOilExtractor;

const dustExtractor = extend(GenericCrafter, "dust-extractor", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.boostEffect, boof, StatUnit.timesSpeed);
    },
});
dustExtractor.buildType = prov(() => {
    const block = dustExtractor;
    return new JavaAdapter(GenericCrafter.GenericCrafterBuild, {
        updateTile(){
            var boost = this.liquids.get(this.liquids.current()) > 1 ? boof : 1;
            if(this.efficiency > 0){
                this.progress += this.getProgressIncrease(block.craftTime/boost);
                this.totalProgress += this.delta();
                this.warmup = Mathf.approachDelta(this.warmup, 1, block.warmupSpeed);
                if(Mathf.chanceDelta(block.updateEffectChance)){
                    this.block.updateEffect.at(this.getX() + Mathf.range(block.size * 4), this.getY() + Mathf.range(block.size * 4));
                }
            }else{
                this.warmup = Mathf.approachDelta(this.warmup, 0, block.warmupSpeed);
            }
            if(this.progress >= 1){
                this.consume();
                if(block.outputItem != null){
                    for(var i = 0; i < block.outputItem.amount; i++){
                        this.offload(block.outputItem.item);
                    }
                }
                for(var i = 0; i < 3; i++){
                    block.craftEffect.at(this.x, this.y);
                }
                this.progress %= 1;
            }
            if(block.outputItem != null && this.timer.get(block.timerDump, block.dumpTime / this.timeScale)){
                this.dump(block.outputItem.item);
            }
        },
    }, dustExtractor);
});
Object.assign(dustExtractor, {
    craftTime : 1.6 * 60,
    updateEffect : eff1,
    craftEffect : lib.Fx.absorbEffect,
    updateEffectChance : 0.02,
    size : 2,
    outputItem : new ItemStack(Items.sand, 1),
});
dustExtractor.consumePower(1.2);
dustExtractor.requirements = ItemStack.with(
    Items.copper, 72,
    Items.lead, 50,
    Items.graphite, 40
);
dustExtractor.consumeLiquid(Liquids.water, 0.04).boost();
dustExtractor.buildVisibility = BuildVisibility.shown;
dustExtractor.category = Category.production;
exports.dustExtractor = dustExtractor;
