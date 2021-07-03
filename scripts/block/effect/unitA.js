const unitRange = 240;
const baseColor = Pal.shield;
const phaseColor = Color.valueOf("ffd59e");
const damApply = new Effect(11, cons(e => {
        Draw.color(Color.valueOf("ff0000"));
        Lines.stroke(e.fout() * 2);
        Lines.circle(e.x, e.y, 2 + e.finpow() * 7);
}));
const status = require("other/status");

const unitA = extendContent(OverdriveProjector , "unitA", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, unitRange * 0.6, Pal.accent);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, unitRange, Color.valueOf("ff0000"));
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.damage, "120(+100phase)");
    },
});
unitA.buildType = prov(() => {
    var applied = false;
    var dam = false;
    var timer = 0;
    var timeR = 0;
    var heatU = 0;
    var phaseHeatU = 0;
    return new JavaAdapter(OverdriveProjector.OverdriveBuild, {
        updateTile(){
            this.super$updateTile();
            phaseHeatU = Mathf.lerpDelta(phaseHeatU, Mathf.num(this.cons.optionalValid()), 0.1);
            var realRange = unitRange * 0.6 + phaseHeatU * this.block.phaseRangeBoost * 0.6;
            Units.nearby(this.team, this.x, this.y, realRange, cons(other => {
                other.apply(status.speedUp, 30);
            }));
            heatU = Mathf.lerpDelta(heatU, this.consValid() || this.cheating() ? 1 : 0, 0.08);
            /*if(this.cons.optionalValid() && this.timer.get(unitA.timerUse, unitA.useTime) && this.efficiency() > 0){
                this.consume();
            }*/
            timeR += heatU * this.delta();
            if(timeR > (this.block.reload * 0.8 * 4)){
                applied = false;
                Units.nearby(this.team, this.x, this.y, realRange, cons(other => {
                    var max = other.maxHealth * 0.08;
                    if(other.shield < max){
                        other.shield = Math.min((max <= 56 ? other.shield + max/5 : other.shield + max/15) + (max/12) * phaseHeatU, max);
                        other.shieldAlpha = 1;
                        Fx.shieldApply.at(other);
                        applied = true;
                    }
                }));
                if(applied){
                    new Effect(22, cons(e => {
                        Draw.color(Pal.shield);
                        Lines.stroke(e.fout() * 2);
                        Lines.circle(e.x, e.y, 2 + e.finpow() * realRange);
                    })).at(this.x, this.y);
                }
                timeR = 0;
            }
            timer += heatU * this.delta();
            var realR = unitRange + phaseHeatU * this.block.phaseRangeBoost * 1.2;
            if(timer > (this.block.reload * 4)){
                dam = false;
                Units.nearbyEnemies(this.team, this.x, this.y, realR*2, realR*2, cons(other => {
                    if(other != null && other.within(this.x, this.y, realR)){
                        other.damage(120 + phaseHeatU * 100);
                        other.apply(status.speedDown, 120);
                        damApply.at(other.x, other.y);
                        dam = true;
                    }
                }));
                if(dam){
                    new Effect(22, cons(e => {
                        Draw.color(Color.valueOf("ff0000"));
                        Lines.stroke(e.fout() * 2);
                        Lines.circle(e.x, e.y, 2 + e.finpow() * realR);
                    })).at(this.x, this.y);
                }
                timer = 0;
            }
        },
        drawSelect(){
            this.super$drawSelect();
            var realRange = unitRange * 0.6 + phaseHeatU * this.block.phaseRangeBoost * 0.6;
            Drawf.dashCircle(this.x, this.y, realRange, Pal.shield);
            var realR = unitRange + phaseHeatU * this.block.phaseRangeBoost * 1.2;
            Drawf.dashCircle(this.x, this.y, realR, Color.valueOf("ff0000"));
        },
        write(write) {
            this.super$write(write);
            write.f(heatU);
            write.f(phaseHeatU);
        },
        read(read, revision) {
            this.super$read(read, revision);
            heatU = read.f();
            phaseHeatU = read.f();
        },
    }, unitA);
});
unitA.requirements = ItemStack.with(
    Items.copper, 230,
    Items.lead, 200,
    Items.silicon, 180,
    Items.titanium, 100,
    Items.thorium, 80,
    Items.plastanium, 80,
    Items.surgeAlloy, 180
);
unitA.buildVisibility = BuildVisibility.shown;
unitA.category = Category.effect;
unitA.consumes.power(10);
unitA.buildCostMultiplier = 0.85;
unitA.useTime = 330;
unitA.size = 3;
unitA.reload = 60;
unitA.range = unitRange * 0.8;
unitA.phaseRangeBoost = 56;
unitA.speedBoost = 1.8;
unitA.speedBoostPhase = 0.65;
unitA.health = 700;
unitA.consumes.item(Items.phaseFabric).boost();
exports.unitA = unitA;