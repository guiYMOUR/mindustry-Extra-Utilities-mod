/*
The removed part is that at the beginning I considered that it could be started without liquid, but later my group friends and I all thought it was unbalanced, so I removed it.
去掉的部分是一开始我考虑不用液体也可以启动，后来我的群友和我都认为这样太强了，所以才去掉的
*/
const drill = extendContent(Drill, "drill", {});
drill.buildType = prov(() => {
    
    return new JavaAdapter(Drill.DrillBuild, {
        efficiency(){
            if(!this.enabled) return 0;
            return /*this.liquids.get(this.liquids.current()) / this.block.liquidCapacity > 0.06 ? */this.power.status / (this.liquids.current().temperature * 2.05)/* : this.power.status*/;
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
drill.consumes.add(new ConsumeLiquidFilter(boolf(liquid => liquid.temperature <= 0.5 && liquid.flammability < 0.1), 0.1)).update(true)/*.boost()*/;
drill.buildCostMultiplier = 0.6;
exports.drill = drill;