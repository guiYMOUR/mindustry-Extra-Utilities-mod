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

function eff1(color, fout, rad){
    return new Effect(12, cons(e => {
        Draw.color(color);
        Lines.stroke(3 * e.fout());
        if(fout){
            Lines.circle(e.x, e.y, rad * e.fout());
        } else {
            Lines.circle(e.x, e.y, rad * e.fin());
        }
    }));
};

function filter(liquid){
    return liquid.temperature <= 0.5 && liquid.flammability < 0.1;
};
function boosters(speed, maxUsed, multiplier, baseTime){
    return new JavaAdapter(StatValue, {
        display(table){
            table.row();
            table.table(cons(c => {
                for(var i = 0; i < Vars.content.liquids().size; i++){
                    var liquid = Vars.content.liquids().get(i);
                    if(!filter(liquid)) continue;

                    c.image(liquid.uiIcon).size(3 * 8).padRight(4).right().top();
                    c.add(liquid.localizedName).padRight(10).left().top();
                    c.table(Tex.underline, cons(bt => {
                        bt.left().defaults().padRight(3).left();

                        var speedRate = (baseTime ? 1 : 0) + maxUsed * multiplier * liquid.heatCapacity;
                        var standardSpeed = baseTime ? speed : speed / (maxUsed * multiplier * 0.4);
                        var result = standardSpeed / (speed / speedRate);
                        bt.add(Core.bundle.format("stat.btm-drillSpeed", Strings.autoFixed(result, 2)));
                    })).left().padTop(-9);
                    c.row();
                }
            })).colspan(table.getColumns());
            table.row();
        }
    });
}

const drill = extendContent(Drill, "drill", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.booster, boosters(this.drillTime, this.consumes.get(ConsumeType.liquid).amount, this.liquidBoostIntensity, false));
        this.stats.remove(Stat.boostEffect);
    },
});

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
                speed *= (this.efficiency() * (1 + (this.liquids.current().heatCapacity - 0.4) * 0.9) * this.block.liquidBoostIntensity * 0.3087 * Mathf.num(work));
                //limit display
                this.lastDrillSpeed = Math.min((speed * this.dominantItems * this.warmup) / (this.block.drillTime + this.block.hardnessDrillMultiplier * this.dominantItem.hardness), 60);
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
                var eff = eff1(this.dominantItem.color, true, 10);
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
drill.drillTime = 80;
drill.size = 4;
drill.drawRim = true;
drill.hasPower = true;
drill.tier = 10;
drill.hardnessDrillMultiplier = 0;
drill.updateEffect = Fx.pulverizeRed;
drill.updateEffectChance = 0.01;
drill.drillEffect = Fx.none;
drill.rotateSpeed = 7;
drill.warmupSpeed = 0.01;
drill.liquidBoostIntensity = 1.8;
drill.consumes.power(4);
drill.consumes.add(new ConsumeLiquidFilter(boolf(liquid => filter(liquid)), 0.1));
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

const blastOilExtractor = extendContent(Fracker, "blast-oil-extractor", {});
blastOilExtractor.buildType = prov(() => {
    return new JavaAdapter(Fracker.FrackerBuild, {
        updateTile(){
            this.super$updateTile();
            if(Mathf.chance(this.delta() * this.block.updateEffectChance) && this.cons.valid() && this.typeLiquid() < this.block.liquidCapacity - 0.001){
                var range = Mathf.range(this.block.size * 2);
                var range2 = Mathf.range(this.block.size * 2);
                eff1(Items.blastCompound.color, false, 12).at(this.getX() + range, this.getY() + range2);
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
blastOilExtractor.consumes.item(Items.blastCompound);
blastOilExtractor.consumes.power(6);
blastOilExtractor.consumes.liquid(Liquids.water, 0.4);
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

const absorbEffect = new Effect(38, cons(e => {
    Draw.color(Items.sand.color);
    Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({
        get: (x, y) => {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3 + 1);
            Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 2);
        }
    }));
}));

const dustExtractor = extendContent(GenericCrafter, "dust-extractor", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.boostEffect, boof, StatUnit.timesSpeed);
    },
});
dustExtractor.buildType = prov(() => {
    return new JavaAdapter(GenericCrafter.GenericCrafterBuild, {
        updateTile(){
            var boost = this.liquids.total() > 1 ? boof : 1;
            if(this.consValid()){
                this.progress += this.getProgressIncrease(this.block.craftTime/boost);
                this.totalProgress += this.delta();
                this.warmup = Mathf.approachDelta(this.warmup, 1, this.block.warmupSpeed);
                if(Mathf.chanceDelta(this.block.updateEffectChance)){
                    this.block.updateEffect.at(this.getX() + Mathf.range(this.block.size * 4), this.getY() + Mathf.range(this.block.size * 4));
                }
            }else{
                this.warmup = Mathf.approachDelta(this.warmup, 0, this.block.warmupSpeed);
            }
            if(this.progress >= 1){
                this.consume();
                if(this.block.outputItem != null){
                    for(var i = 0; i < this.block.outputItem.amount; i++){
                        this.offload(this.block.outputItem.item);
                    }
                }
                for(var i = 0; i < 3; i++){
                    this.block.craftEffect.at(this.x, this.y);
                }
                this.progress %= 1;
            }
            if(this.block.outputItem != null && this.timer.get(this.block.timerDump, this.block.dumpTime / this.timeScale)){
                this.dump(this.block.outputItem.item);
            }
        },
    }, dustExtractor);
});
Object.assign(dustExtractor, {
    craftTime : 1.6 * 60,
    updateEffect : eff1(Items.sand.color, true, 4),
    craftEffect : absorbEffect,
    updateEffectChance : 0.02,
    size : 2,
    outputItem : new ItemStack(Items.sand, 1),
});
dustExtractor.consumes.power(1.2);
dustExtractor.requirements = ItemStack.with(
    Items.copper, 72,
    Items.lead, 50,
    Items.graphite, 40
);
dustExtractor.consumes.liquid(Liquids.water, 0.04).boost();
dustExtractor.buildVisibility = BuildVisibility.shown;
dustExtractor.category = Category.production;
exports.dustExtractor = dustExtractor;
