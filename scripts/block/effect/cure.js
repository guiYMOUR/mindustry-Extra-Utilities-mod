const unitRange = 90;//单位治疗范围
const cor = Color.valueOf("84f491");
const cure = extendContent(MendProjector , "cure", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.circles(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, unitRange, Pal.accent);
    },
    setStats(){
        this.super$setStats();
        this.stats.remove(Stat.range);
        this.stats.add(Stat.range, unitRange / Vars.tilesize, StatUnit.blocks);
        this.stats.add(Stat.range, this.range / Vars.tilesize, StatUnit.blocks);
    },
});
cure.buildType = prov(() => {
    var wasHealed = false;
    var timeR = 0;
    var heatR = 0;
    var phaseHeatR = 0;
    
    const block = cure;
    var x = 0, y = 0;
    
    return new JavaAdapter(MendProjector.MendBuild, {
        updateTile(){
            x = this.x;
            y = this.y;
            this.super$updateTile();
            phaseHeatR = Mathf.lerpDelta(phaseHeatR, Mathf.num(this.cons.optionalValid()), 0.1);
            var realRange = unitRange + phaseHeatR * block.phaseRangeBoost * 0.7;
            heatR = Mathf.lerpDelta(heatR, this.consValid() || this.cheating() ? 1 : 0, 0.08);

            timeR += heatR * this.delta();
            if(timeR > (block.reload * 1.2)){
                wasHealed = false;
                Units.nearby(this.team, x, y, realRange, cons(other => {
                    if(other.damaged()){
                        Fx.heal.at(other);
                        wasHealed = true;
                    }
                    var hm = other.maxHealth;
                    other.heal(hm < 10000 ? (hm <= 1500 ? 150 + 120 * phaseHeatR : hm * 0.06 + hm * 0.05) : hm * 0.03 + hm * 0.02);
                }));
                if(wasHealed){
                    Fx.healWaveDynamic.at(x, y, realRange);
                }
                timeR = 0;
            }
        },
        drawSelect(){
            this.super$drawSelect();
            var realRange = unitRange + phaseHeatR * block.phaseRangeBoost * 0.7;
            Drawf.circles(x, y, realRange, cor);
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
    Items.lead, 200,
    Items.graphite, 100,
    Items.silicon, 160,
    Items.titanium, 90,
    Items.thorium, 50,
    Items.plastanium, 60,
    Items.surgeAlloy, 120
);
cure.buildVisibility = BuildVisibility.shown;
cure.category = Category.effect;
cure.consumes.power(4);
cure.buildCostMultiplier = 0.9;
cure.size = 3;
cure.reload = 200;
cure.range = 120;
cure.healPercent = 12;
cure.phaseBoost = 15;
cure.health = 720;
cure.consumes.item(Items.phaseFabric).boost();
exports.cure = cure;
