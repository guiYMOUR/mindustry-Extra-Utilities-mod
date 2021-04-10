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
