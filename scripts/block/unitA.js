const unitRange = 160;
const baseColor = Pal.shield;
const phaseColor = Color.valueOf("ffd59e");
const damApply = new Effect(11, cons(e => {
        Draw.color(Color.valueOf("ff0000"));
        Lines.stroke(e.fout() * 2);
        Lines.circle(e.x, e.y, 2 + e.finpow() * 7);
}));
const status = require("other/status");

const unitA = extendContent(MendProjector , "unitA", {
    drawPlace(x, y, rotation, valid) {
        
        Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, unitRange * 0.6, Pal.accent);
        Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, unitRange, Color.valueOf("ff0000"));
    },
    setStats(){
        this.super$setStats();
        this.stats.remove(Stat.repairTime);
    },
});
unitA.buildType = prov(() => {
    var applied = false;
    var dam = false;
    var timer = 0;
    var timeR = 0;
    var heatU = 0;
    var phaseHeatU = 0;
    return new JavaAdapter(MendProjector.MendBuild, {
        updateTile(){
            
            phaseHeatU = Mathf.lerpDelta(phaseHeatU, Mathf.num(this.cons.optionalValid()), 0.1);
            var realRange = unitRange * 0.6 + phaseHeatU * this.block.phaseRangeBoost * 0.6;
            Units.nearby(this.team, this.x, this.y, realRange, cons(other => {
                other.apply(status.speedUp, 30);
            }));
            heatU = Mathf.lerpDelta(heatU, this.consValid() || this.cheating() ? 1 : 0, 0.08);
            if(this.cons.optionalValid() && this.timer.get(unitA.timerUse, unitA.useTime) && this.efficiency() > 0){
                this.consume();
            }
            timeR += heatU * this.delta();
            if(timeR > (this.block.reload * 0.8)){
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
            var realR = unitRange + phaseHeatU * this.block.phaseRangeBoost;
            if(timer > (this.block.reload)){
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
            
            var realRange = unitRange * 0.6 + phaseHeatU * this.block.phaseRangeBoost * 0.6;
            Drawf.dashCircle(this.x, this.y, realRange, Pal.shield);
            var realR = unitRange + phaseHeatU * this.block.phaseRangeBoost;
            Drawf.dashCircle(this.x, this.y, realR, Color.valueOf("ff0000"));
        },
        draw(){
            this.super$draw();
            var topRegion = Core.atlas.find("btm-unitA-top");
            var f = 1 - (Time.time / 100) % 1;

            Draw.color(baseColor, phaseColor, phaseHeatU);
            Draw.alpha(heatU * Mathf.absin(Time.time, 10, 1) * 0.5);
            Draw.rect(topRegion, this.x, this.y);
            Draw.alpha(1);
            Lines.stroke((2 * f + 0.2) * heatU);
            Lines.square(this.x, this.y, Math.min(1 + (1 - f) * this.block.size * Vars.tilesize / 2, this.block.size * Vars.tilesize/2));

            Draw.reset();
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
    Items.copper, 210,
    Items.lead, 200,
    Items.graphite, 135,
    Items.silicon, 160,
    Items.titanium, 100,
    Items.thorium, 80,
    Items.plastanium, 80,
    Items.surgeAlloy, 180
);
unitA.buildVisibility = BuildVisibility.shown;
unitA.category = Category.units;
unitA.consumes.power(3);
unitA.buildCostMultiplier = 0.85;
unitA.size = 3;
unitA.reload = 240;
unitA.range = unitRange;
unitA.phaseBoost = 15;
unitA.health = 700;
unitA.consumes.item(Items.phaseFabric).boost();
exports.unitA = unitA;