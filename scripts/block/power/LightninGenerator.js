const items = require("game/items");
const range = 36;
const pullPower = 22*60;
const effectChance = 0.005;

/*drawer*/function circlePercent(x, y, rad, percent, angle){
    //if(p < 0.001) return;
    var p = Mathf.clamp(percent);
    var sides = Lines.circleVertices(rad);
    var space = 360 / sides;
    var len = 2 * rad * Mathf.sinDeg(space / 2);
    var hstep = Lines.getStroke() / 2 / Mathf.cosDeg(space / 2);
    var r1 = rad - hstep;
    var r2 = rad + hstep;
    var i;
    for(i = 0; i < sides * p - 1; ++i){
        var a = space * i + angle;
        var cos = Mathf.cosDeg(a);
        var sin = Mathf.sinDeg(a);
        var cos2 = Mathf.cosDeg(a + space);
        var sin2 = Mathf.sinDeg(a + space);
        Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2, y + r1 * sin2, x + r2 * cos2, y + r2 * sin2, x + r2 * cos, y + r2 * sin);
    }
    var a = space * i + angle;
    var cos = Mathf.cosDeg(a);
    var sin = Mathf.sinDeg(a);
    var cos2 = Mathf.cosDeg(a + space);
    var sin2 = Mathf.sinDeg(a + space);
    var f = sides * p - i;
    var vec = new Vec2();
    vec.trns(a, 0, len * (f - 1));
    Fill.quad(x + r1 * cos, y + r1 * sin, x + r1 * cos2 + vec.x, y + r1 * sin2 + vec.y, x + r2 * cos2 + vec.x, y + r2 * sin2 + vec.y, x + r2 * cos, y + r2 * sin);
}
const Start = new Effect(30, cons(e => {
    Draw.color(items.lightninAlloy.color);
    Lines.stroke(3 * e.fout());
    Lines.circle(e.x, e.y, range * e.fout());
}));

const effectBullet = extend(BasicBulletType, {});
Object.assign(effectBullet, {
    collidesAir : true,
    lifetime : 6,
    speed : 1,
    splashDamageRadius : 56,
    instantDisappear : true,
    splashDamage : 10,
    hitShake : 3,
    lightningColor : items.lightninAlloy.color,
    lightningDamage : 3,
    lightning : 5,
    lightningLength : 12,
    hitSound : Sounds.release,
});
effectBullet.hitEffect = new Effect(60, cons(e => {
    Draw.color(items.lightninAlloy.color);
    Lines.stroke(e.fout() * 2);
    Lines.circle(e.x, e.y, 4 + e.finpow() * effectBullet.splashDamageRadius);
    Draw.color(items.lightninAlloy.color);
    for(var i = 0; i < 4; i++){
        Drawf.tri(e.x, e.y, 6, 36 * e.fout(), (i - e.fin()) * 90);
    }
    Draw.color();
    for(var i = 0; i < 4; i++){
        Drawf.tri(e.x, e.y, 3, 16 * e.fout(), (i - e.fin()) * 90);
    }
}));

const LG = extendContent(NuclearReactor, "lightnin-generator", {});
LG.buildType = prov(() => {
    var working = false;
    var consumeTimer = 0;
    var light = 0;
    return new JavaAdapter(NuclearReactor.NuclearReactorBuild, {
        updateTile(){
            //this.super$updateTile();
            if(this.items.total() == this.block.itemCapacity && !working){
                Start.at(this);
                Sounds.lasercharge2.at(this.x, this.y, 1.5);
                Units.nearby(null, this.x, this.y, range*2, cons(unit => {
                    unit.impulse(Tmp.v3.set(unit).sub(this.x, this.y).nor().scl(-pullPower));
                }));
                working = true;
            }
            if(this.items.total() < 1 && working){
                working = false;
                consumeTimer = 0;
            }
            var cliquid = this.block.consumes.get(ConsumeType.liquid);
            var item = this.block.consumes.getItem().items[0].item;

            var fuel = this.items.get(item);
            var fullness = fuel / this.block.itemCapacity;
            this.productionEfficiency = fullness;
            if(fuel > 0 && this.enabled && working){
                this.heat += fullness * this.block.heating * Math.min(this.delta(), 4);
                consumeTimer += this.getProgressIncrease(this.block.itemDuration);
                if(/*this.timer.get(this.block.timerFuel, this.block.itemDuration / this.timeScale)*/consumeTimer >= 1){
                    this.consume();
                    effectBullet.create(this, this.team, this.x + Mathf.range(this.block.size * 4), this.y + Mathf.range(this.block.size * 4), 0);
                    consumeTimer %= 1;
                }
            }else{
                this.productionEfficiency = 0;
            }
            var liquid = cliquid.liquid;
            if(this.heat > 0){
                var maxUsed = Math.min(this.liquids.get(liquid), this.heat / this.block.coolantPower);
                this.heat -= maxUsed * this.block.coolantPower;
                this.liquids.remove(liquid, maxUsed);
            }
            if(this.heat > this.block.smokeThreshold){
                var smoke = 1 + (this.heat - this.block.smokeThreshold) / (1 - this.block.smokeThreshold); //ranges from 1.0 to 2.0
                if(Mathf.chance(smoke / 20 * this.delta())){
                    Fx.reactorsmoke.at(this.x + Mathf.range(this.block.size * Vars.tilesize / 2), this.y + Mathf.range(this.block.size * Vars.tilesize / 2));
                }
            }
            this.heat = Mathf.clamp(this.heat);
            if(this.heat >= 0.999){
                Events.fire(Trigger.thoriumReactorOverheat);
                this.kill();
            }
            light = Mathf.lerpDelta(light, working ? 1 : 0, 0.05);
        },
        draw(){
            this.super$draw();
            Draw.color(items.lightninAlloy.color);
            Draw.alpha(this.items.total() > 0 ? 1 : 0);
            Draw.z(Layer.effect);
            Lines.stroke(3);
            if(!working){
                circlePercent(this.x, this.y, range, this.items.total()/this.block.itemCapacity, 135);
            }
            Draw.alpha(light);
            Draw.rect(Core.atlas.find("btm-lightnin-generator-lights"), this.x,this.y);
        },
        write(write) {
            this.super$write(write);
            write.bool(working);
            write.f(consumeTimer);
            write.f(light);
        },
        read(read, revision) {
            this.super$read(read, revision);
            working = read.bool();
            consumeTimer = read.f();
            light = read.f();
        },
    }, LG);
});
Object.assign(LG, {
    size : 6,
    itemCapacity : 30,
    health : 2400,
    itemDuration : 186,
    powerProduction : 15900/60,
    hasItems : true,
    hasLiquids : true,
    heating : 0.04,
    explosionRadius : 75,
    explosionDamage : 5000,
    coolantPower : 0.1,
});
LG.consumes.item(items.lightninAlloy);
LG.liquidCapacity = 60;
LG.consumes.liquid(Liquids.cryofluid, 0.04 / 0.1).update = false;
LG.requirements = ItemStack.with(
    Items.metaglass, 600,
    Items.graphite, 550,
    Items.silicon, 470,
    Items.surgeAlloy, 550,
    items.lightninAlloy, 270
);
LG.buildVisibility = BuildVisibility.shown;
LG.category = Category.power;
exports.LG = LG;