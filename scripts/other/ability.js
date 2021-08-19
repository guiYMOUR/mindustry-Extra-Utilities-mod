/*
* @author <guiY>
* @abilities
*/

const status = require("other/status");

const TerritoryFieldAbility = (damage, reload, range) => {
    var dam = false;
    var timer = 0;
    var ability = new JavaAdapter(Ability, {
        localized() {
            return Core.bundle.get("ability.btm-TerritoryFieldAbility");
        },
        update(unit) {
            Units.nearby(unit.team, unit.x, unit.y, range, cons(other => {
                if(other != unit) other.apply(status.speedUp, 30);
            }));
            Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2, range * 2, cons(other => {
                if(other.within(unit.x, unit.y, range)) other.apply(status.speedDown, 30);
            }));
            timer += Time.delta;
            if(timer > reload){
                dam = false;
                Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2, range * 2, cons(other => {
                    if(other.within(unit.x, unit.y, range)){
                        other.damage(damage);
                        new Effect(11, cons(e => {
                            Draw.color(unit.team.color);
                            Lines.stroke(e.fout() * 2);
                            Lines.circle(e.x, e.y, 2 + e.finpow() * 7);
})).at(other.x, other.y);
                        dam = true;
                    }
                }));
                if(dam){
                    new Effect(22, cons(e => {
                        Draw.color(unit.team.color);
                        Lines.stroke(e.fout() * 2);
                        Lines.circle(e.x, e.y, 2 + e.finpow() * range);
                    })).at(unit.x, unit.y);
                }
                timer = 0;
            }
        },
        copy() {
            return TerritoryFieldAbility(damage, reload, range);
        },
        draw(unit) {
            Draw.color(unit.team.color);
            Lines.stroke(1.5);
            Draw.alpha(0.09);
            Fill.circle(unit.x, unit.y, range);
            Draw.alpha(0.5);
            Lines.circle(unit.x, unit.y, range);
        },
    });
    return ability;
};
exports.TerritoryFieldAbility = TerritoryFieldAbility;

const baseColor = Color.valueOf("84f491");
const phaseColor = Color.valueOf("ffd59e");
const MendFieldAbility = (range, reload, healP) => {
    var timer = 0;
    var ability = new JavaAdapter(Ability, {
        localized() {
            return Core.bundle.get("ability.btm-MendFieldAbility");
        },
        update(unit) {
            Vars.indexer.eachBlock(unit, range, boolf(other => other.damaged()), cons(other => {
                timer += Time.delta;
                if(timer >= reload){
                    other.heal((healP/100) * other.maxHealth);
                    Fx.healBlockFull.at(other.x, other.y, !(other instanceof PayloadSource.PayloadSourceBuild) ? other.block.size : 5, Tmp.c1.set(baseColor).lerp(phaseColor, 0.3));
                    timer = 0;
                }
            }));
        },
        copy() {
            return MendFieldAbility(range, reload, healP);
        },
        draw(unit) {
            Vars.indexer.eachBlock(unit, range, boolf(other => other.damaged()), cons(other => {
                var tmp = Tmp.c1.set(baseColor);
                tmp.a = Mathf.absin(4, 1);
                Drawf.selected(other, tmp);
                }));
        },
    });
    return ability;
};
exports.MendFieldAbility = MendFieldAbility;

//Prevent players from using cheat mod(only certain aspects)
const preventCheatingAbility = (open) => {
    var range = 80;
    var unitD;
    var consumer = cons(target => {
        if (target.team != unitD.team && target.type.absorbable && Intersector.isInsideHexagon(unitD.x, unitD.y, range * 2, target.x, target.y)){
            if(target.owner != null && (target.owner.health > unitD.maxHealth * 2 || target.damage > unitD.maxHealth/2)){
                target.owner.health -= (target.owner.maxHealth - 1);
                target.owner.kill();
                target.absorb();
            }
            if(target.damage > unitD.maxHealth/2){
                target.absorb();
            }
        }
    });
    var ability = new JavaAdapter(Ability, {
        localized() {
            return "";
        },
        update(unit) {
            unitD = unit;
            Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, consumer);
        },
        copy() {
            return preventCheatingAbility(open);
        },
    });
    return ability;
};
exports.preventCheatingAbility = preventCheatingAbility;

const healthDisplay = (y, width, height) => {
    var ability = new JavaAdapter(Ability, {
        localized() {
            return "";
        },
        draw(unit) {
            var drawy = unit.y + y;
            var realWidth = width * (unit.health / unit.maxHealth);
            Draw.color(Pal.health);
            //Draw.rect(unit.x, drawy, realWidth, height);
            Fill.rect(unit.x, drawy, realWidth, height);
            Draw.reset();
            Draw.color();
        },
        copy() {
            return healthDisplay(y, width, height);
        },
    });
    return ability;
};
exports.healthDisplay = healthDisplay;

//I also will remove it in 7.0(maybe not)
const sectors = 5;
const sectorRad = 0.14;
const blinkScl = 20;
const rotateSpeed = 0.5;
const effectRadius = 10;
const LightningFieldAbility = (damage, reload, range, color, maxFind) => {
    var x = 0;
    var y = 0;
    var timer = 0;
    var curStroke = 0;
    var find = false;
    var target = new Seq();
    //const maxFind = 18;
    const chargeTime = 20;
    var ability = new JavaAdapter(Ability, {
        localized() {
            return Core.bundle.format("ability.btm-LightningFieldAbility", damage, range/Vars.tilesize, maxFind);
        },
        draw(unit){
            Draw.z(Layer.bullet - 0.001);
            Draw.color(color);
            Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
            var rx = Tmp.v1.x;
            var ry = Tmp.v1.y;
            var orbRadius = effectRadius * (1 + Mathf.absin(blinkScl, 0.1));

            Fill.circle(rx, ry, orbRadius);
            Draw.color();
            Fill.circle(rx, ry, orbRadius / 2);

            Lines.stroke((0.7 + Mathf.absin(blinkScl, 0.7)), color);

            for(var i = 0; i < sectors; i++){
                var rot = unit.rotation + i * 360/sectors - Time.time * rotateSpeed;
                Lines.swirl(rx, ry, orbRadius + 3, sectorRad, rot);
            }

            Lines.stroke(Lines.getStroke() * curStroke);

            if(curStroke > 0){
                for(var i = 0; i < sectors; i++){
                   var rot = unit.rotation + i * 360/sectors + Time.time * rotateSpeed;
                   Lines.swirl(rx, ry, range, sectorRad, rot);
                }
            }
            Drawf.light(rx, ry, range * 1.5, color, 0.8);
            Draw.reset();
        },
        update(unit) {
            timer = Math.min(timer + Time.delta, reload);
            curStroke = Mathf.lerpDelta(curStroke, find ? 1 : 0, 0.09);
            //Lock multiple (group friend selection)
            if(timer >= reload){
                find = false;
                target.clear();
                Units.nearby(null, unit.x, unit.y, range, cons(other => {
                    if(other.team != unit.team){
                        target.add(other);
                    }
                }));
                target.sort(floatf(u => u.dst2(unit.x, unit.y)));
                var max = Math.min(maxFind, target.size);
                for(var a = 0; a < max; a++){
                    var other = target.get(a);
                    //if(other == null) continue;
                    find = true;
                    new Effect(12, cons(e => {
                            Draw.color(color);
                            Lines.circle(e.x, e.y, e.fin() * range);
                            Draw.reset();
                        })).at(unit);
                        Fx.chainLightning.at(unit.x, unit.y, 0, color, other);
                        other.apply(StatusEffects.unmoving, 30);
                        for(var i = 0; i < 4; i++){
                            Lightning.create(unit.team, color, damage/4, other.x, other.y, Mathf.range(180), 10);
                        }
                    timer = 0
                }
                //timer = 0;
            }
            //target.clear();
            //Lock single (my initial choice)
            /*Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2, range * 2, cons(other => {
                if(other.within(unit, range)){
                    if(timer >= reload){
                        //find = false;
                        new Effect(chargeTime, cons(e => {
                            Draw.color(color);
                            Lines.circle(e.x, e.y, e.fout() * 54);
                            Draw.reset();
                        })).at(unit);
                        Time.run(chargeTime, () => {
                            if(!unit.isValid()) return;
                            new Effect(12, cons(e => {
                                Draw.color(color);
                                Lines.circle(e.x, e.y, e.fin() * range);
                                Draw.reset();
                            })).at(unit);
                            Fx.pointBeam.at(unit.x, unit.y, unit.angleTo(other), color, new Vec2().set(other));
                            other.apply(StatusEffects.unmoving, 30);
                            for(var i = 0; i < 4; i++){
                                Lightning.create(unit.team, color, damage/4, other.x, other.y, Mathf.range(180), 10);
                            }
                        });
                        timer = 0;
                    }
                }
            }));*/
            if(Mathf.chance(0.05)){
                var a = unit.rotation + Mathf.range(180) + 0;
                Lightning.create(unit.team, color, damage, unit.x, unit.y, a, 4);
            }
        },
        copy() {
            return LightningFieldAbility(damage, reload, range, color, maxFind);
        },
    });
    return ability;
};
exports.LightningFieldAbility = LightningFieldAbility;

const BatteryAbility = (capacity, shieldRange, range, px, py) => {
    var amount = 0;
    var target = null;
    var timerRetarget = 0;
    var paramUnit;
    const absorb = new Effect(20, cons(e => {
            Draw.color(Pal.heal);
            Lines.stroke(e.fslope() * 2.5);
            Lines.poly(e.x, e.y, 6, 3 * e.fout() + 9);
            const d = new Floatc2({get(x, y){
                Lines.poly(e.x + x, e.y + y, 6, 2 * e.fout() + 2);
            }})
            Angles.randLenVectors(e.id, 2, 32 * e.fin(), 0, 360,d);
    }));
    var shieldConsumer = cons(trait => {
        if(trait.team != paramUnit.team && trait.type.absorbable && Intersector.isInsideHexagon(paramUnit.x, paramUnit.y, shieldRange * 2, trait.getX(), trait.getY()) && paramUnit.shield > 0){
            trait.absorb();
            absorb.at(trait);
            paramUnit.shield = Math.max(paramUnit.shield - trait.damage, 0);
        }
    });
    function setupColor(satisfaction){
        Draw.color(Color.white, Pal.powerLight, (1 - satisfaction) * 0.86 + Mathf.absin(3, 0.1));
        Draw.alpha(Renderer.laserOpacity);
    }
    var ability = new JavaAdapter(Ability, {
        findTarget(unit){
            if(target != null) return;
            Vars.indexer.allBuildings(unit.x, unit.y, range, cons(other =>{
                if(other.block != null && other.team == unit.team && other.block instanceof PowerNode){
                    target = other;
                }
            }));
        },
        updateTarget(unit){
            timerRetarget += Time.delta;
            if(timerRetarget > 5){
                target = null;
                this.findTarget(unit);
                timerRetarget = 0;
            }
        },
        localized() {
            return Core.bundle.format("ability.btm-BatteryAbility", capacity, range/8);
        },
        draw(unit){
            var x = unit.x + Angles.trnsx(unit.rotation, py, px);
            var y = unit.y + Angles.trnsy(unit.rotation, py, px);
            if(unit.shield > 0){
                Draw.color(Pal.heal);
                Draw.z(Layer.effect);
                Lines.stroke(1.5);
                Lines.poly(unit.x, unit.y, 6, shieldRange);
            }
            if(target == null || target.block == null) return;
            if(Mathf.zero(Renderer.laserOpacity)) return;
            Draw.z(Layer.power);
            setupColor(target.power.graph.getSatisfaction());
            target.block.drawLaser(unit.team, x, y, target.x, target.y, 2, target.block.size);
        },
        update(unit) {
            paramUnit = unit;
            this.updateTarget(unit);
            Groups.bullet.intersect(unit.x - shieldRange, unit.y - shieldRange, shieldRange * 2, shieldRange * 2, shieldConsumer);
            amount = unit.shield * 10;
            if(Vars.state.rules.unitAmmo && amount > 0){
                Units.nearby(unit.team, unit.x, unit.y, range, cons(other => {
                    if(other.type.ammoType instanceof PowerAmmoType){
                        var powerPerAmmo = other.type.ammoType.totalPower / other.type.ammoCapacity;
                        var ammoRequired = other.type.ammoCapacity - other.ammo;
                        var powerRequired = ammoRequired * powerPerAmmo;
                        var powerTaken = Math.min(amount, powerRequired);
                        if(powerTaken > 1){
                            unit.shield -= powerTaken / 10;
                            other.ammo += powerTaken / powerPerAmmo;
                            Fx.itemTransfer.at(unit.x, unit.y, Math.max(powerTaken / 100, 1), Pal.power, other);
                        }
                    }
                }));
            }
            if(target == null || target.block == null) return;
            var g = target.power.graph;
            if(g.getPowerBalance() > 0) amount = Math.min(amount + (g.getLastPowerProduced()), capacity);
            unit.shield = amount / 10;
        },
        displayBars(unit, bars){
            bars.add(new Bar(Core.bundle.format("bar.btm-unitBattery"), Pal.power, () => amount / capacity)).row();
        },
        copy() {
            return BatteryAbility(capacity, shieldRange, range, px, py);
        },
    });
    return ability;
};
exports.BatteryAbility = BatteryAbility;

const propeller = (px, py, sprite, length, speed) => {
    var rot = 0;
    var wind = new Effect(30, cons(e => {
        Draw.z(Layer.debris);
        Draw.color(e.color);
        Fill.circle(e.x, e.y, e.fout() * 6 + 0.3);
        Draw.z();
    }));
    var ability = new JavaAdapter(Ability, {
        localized() {
            return "";
        },
        update(unit){
            if(unit.pathType() == Pathfinder.costNaval && !unit.floorOn().isLiquid){
                unit.elevation = 1;
            }
            var realSpeed = unit.elevation * speed * Time.delta;
            rot += realSpeed;
            var out = unit.elevation * length;
            var x = unit.x + Angles.trnsx(unit.rotation, px, py) + Angles.trnsx(unit.rotation, 0, out);
            var y = unit.y + Angles.trnsy(unit.rotation, px, py) + Angles.trnsy(unit.rotation, 0, out);
            if(!unit.moving() && unit.isFlying()){
                var floor = Vars.world.floorWorld(x, y);
                if(floor != null) wind.at(x + Mathf.range(8), y + Mathf.range(8), floor.mapColor);
            }
        },
        draw(unit) {
            Draw.mixcol(Color.white, unit.hitTime);
            Draw.z(Math.max(Layer.groundUnit - 1, unit.elevation * Layer.flyingUnitLow));
            var out = unit.elevation * length;
            var x = unit.x + Angles.trnsx(unit.rotation, px, py) + Angles.trnsx(unit.rotation, 0, out);
            var y = unit.y + Angles.trnsy(unit.rotation, px, py) + Angles.trnsy(unit.rotation, 0, out);
            Draw.rect(Core.atlas.find("btm-wing-s"),x, y, unit.rotation + rot * 2);//why not Time.time ? I Don't Know. ha~
            Draw.rect(Core.atlas.find(sprite),x, y, unit.rotation - 90);
            Draw.mixcol();
            Draw.z(Math.min(Layer.darkness, Layer.groundUnit - 1));
            if(unit.isFlying()){
                Draw.color(Pal.shadow);
                var e = Math.max(unit.elevation, unit.type.visualElevation);
                Draw.rect(Core.atlas.find(sprite), x + unit.type.shadowTX * e, y + unit.type.shadowTY * e, unit.rotation - 90);
                Draw.color();
            }
            Draw.z();
        },
        copy() {
            return propeller(px, py, sprite, length, speed);
        },
    });
    return ability;
};
exports.propeller = propeller;