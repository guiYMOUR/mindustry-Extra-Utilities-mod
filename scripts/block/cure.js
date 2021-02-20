const unitRange = 90;
const cor = Color.valueOf("84f491");
const cure = extendContent(MendProjector , "cure", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.circles(x * Vars.tilesize, y * Vars.tilesize, unitRange, Pal.accent);
    },
});
cure.buildType = prov(() => {
    var wasHealed = false;
    var timeR = 0;
    var heatR = 0;
    var phaseHeatR = 0;
    return new JavaAdapter(MendProjector.MendBuild, {
        updateTile(){
            this.super$updateTile();
            phaseHeatR = Mathf.lerpDelta(phaseHeatR, Mathf.num(this.cons.optionalValid()), 0.1);
            var realRange = unitRange + phaseHeatR * this.block.phaseRangeBoost * 0.7;
            heatR = Mathf.lerpDelta(heatR, this.consValid() || this.cheating() ? 1 : 0, 0.08);

            timeR += heatR * this.delta();
            if(timeR > (this.block.reload * 0.9)){
                wasHealed = false;
                Units.nearby(this.team, this.x, this.y, realRange, cons(other => {
                    if(other.damaged()){
                        Fx.heal.at(other);
                        wasHealed = true;
                    }
                    other.heal(other.maxHealth * (0.05 + phaseHeatR * 0.05));
                }));
                if(wasHealed){
                    Fx.healWaveDynamic.at(this.x, this.y, realRange);
                }
                timeR = 0;
            }
        },
        drawSelect(){
            this.super$drawSelect();
            var realRange = unitRange + phaseHeatR * this.block.phaseRangeBoost * 0.7;
            Drawf.circles(this.x, this.y, realRange, cor);
        },
        write(write) {
            this.super$write(write);
            write.f(heatR);
            write.f(phaseHeatR);
        },
        read(read, revision) {
            this.super$read(read, revision);
            heatR = read.f();
            phaseHeatR = read.f();
        },
    }, cure);
});
cure.requirements = ItemStack.with(
    Items.lead, 180,
    Items.graphite, 105,
    Items.silicon, 160,
    Items.titanium, 90,
    Items.thorium, 50,
    Items.plastanium, 60,
    Items.surgeAlloy, 80
);
cure.buildVisibility = BuildVisibility.shown;
cure.category = Category.effect;
cure.consumes.power(2.5);
cure.buildCostMultiplier = 0.9;
cure.size = 3;
cure.reload = 240;
cure.range = 120;
cure.healPercent = 14;
cure.phaseBoost = 15;
cure.health = 720;
cure.consumes.item(Items.phaseFabric).boost();
exports.cure = cure;