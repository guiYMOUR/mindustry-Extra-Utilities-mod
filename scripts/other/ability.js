/*
* @author <guiY>
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
                    timer = 0;
                    other.heal((healP/100) * other.maxHealth);
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, Tmp.c1.set(baseColor).lerp(phaseColor, 0.3));
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

//In 7.0 I'll remove it.
/*const pointDefenseAbility = (px, py, reloadTime, range, bulletDamage, sprite) => {
    const color = Color.white;
    var target = null;
    var reload = 0;
    var rotation = 90;
    var timer = 90;
    var ability = new JavaAdapter(Ability, {
        localized() {
            return Core.bundle.get("ability.btm-pointDefenseAbility");
        },
        update(unit) {
            var x = unit.x + Angles.trnsx(unit.rotation, py, px);
            var y = unit.y + Angles.trnsy(unit.rotation, py, px);
            target = Groups.bullet.intersect(unit.x - range, unit.y - range, range*2, range*2).min(b => b.team != unit.team && b.type.hittable, b => b.dst2(unit));

            if(target != null && !target.isAdded()){
                target = null;
            }
            if(target == null){
                if(timer >= 90){
                    rotation = Angles.moveToward(rotation, unit.rotation, 3);
                }else{
                    timer += Time.delta;
                }
            }
            if(target != null && target.within(unit, range) && target.team != unit.team && target.type != null && target.type.hittable){
                timer = 0;
                reload += Time.delta;
                //var dest = unit.angleTo(target);
                var dest = target.angleTo(x, y) - 180;
                rotation = Angles.moveToward(rotation, dest, 20);
                if(Angles.within(rotation, dest, 3) && reload >= reloadTime){
                    if(target.damage > bulletDamage){
                        target.damage = target.damage - bulletDamage;
                    }else{
                        target.remove();
                    }
                    Tmp.v1.trns(rotation, 6);
                    Fx.pointBeam.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
                    Fx.sparkShoot.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                    Fx.pointHit.at(target.x, target.y, color);
                    Sounds.lasershoot.at(x, y, Mathf.random(0.9, 1.1));
                    reload = 0;
                }
            }
        },
        copy() {
            return pointDefenseAbility(px, py, reloadTime, range, bulletDamage, sprite);
        },
        draw(unit){
            var x = unit.x + Angles.trnsx(unit.rotation, py, px);
            var y = unit.y + Angles.trnsy(unit.rotation, py, px);
            var region = Core.atlas.find("btm-" + sprite);
            Draw.rect(region, x, y, rotation - 90);
        },
    });
    return ability;
};
exports.pointDefenseAbility = pointDefenseAbility;*/

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
const LightningFieldAbility = (damage, reload, range, color) => {
    var x = 0;
    var y = 0;
    var timer = 0;
    var curStroke = 0;
    var find = false;
    const chargeTime = 20;
    var ability = new JavaAdapter(Ability, {
        localized() {
            return Core.bundle.get("ability.btm-LightningFieldAbility");
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
                Units.nearbyEnemies(unit.team, unit.x - range, unit.y - range, range * 2, range * 2, cons(other => {
                    if(other.within(unit.x, unit.y, range)){
                        find = true;
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
                            Fx.chainLightning.at(unit.x, unit.y, 0, color, other);
                            other.apply(StatusEffects.unmoving, 30);
                            for(var i = 0; i < 4; i++){
                                Lightning.create(unit.team, color, damage/4, other.x, other.y, Mathf.range(180), 10);
                            }
                        });
                        timer = 0;
                    }
                }));
                //timer = 0;
            }
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
            return LightningFieldAbility(damage, reload, range, color);
        },
    });
    return ability;
};
exports.LightningFieldAbility = LightningFieldAbility;