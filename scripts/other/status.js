const poison = extend(StatusEffect, "poison", {
    init(){
        this.affinity(StatusEffects.melting, ((unit, time, newTime, result) => {
            unit.damagePierce(this.transitionDamage);
            Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2));
            result.set(poison, time);
        }));
    },
    setStats(){
        this.stats.add(Stat.abilities, Core.bundle.format("status.btm-poison.ability",""));
        this.super$setStats();
    },
});
poison.transitionDamage = 30;
poison.color = Color.valueOf("#CBD97F");
poison.speedMultiplier = 0.4;
poison.damage = 1.8;
poison.healthMultiplier = 0.7;
poison.effect = Fx.oily;
//poison.reactive = true;
exports.poison = poison;

const speedUp = extend(StatusEffect, "speedUp", {
    /*init(){
        this.opposite(speedDown);
    },*/
});
speedUp.buildSpeedMultiplier = 2;
speedUp.speedMultiplier = 1.3;
speedUp.reloadMultiplier = 2;
speedUp.effect = Fx.none;
exports.speedUp = speedUp;

const speedDown = extend(StatusEffect, "speedDown", {
    /*init(){
        this.opposite(speedUp);
    },*/
});
speedDown.speedMultiplier = 0.4;
speedDown.reloadMultiplier = 0.5;
speedDown.effect = Fx.none;
exports.speedDown = speedDown;

const weakness = extend(StatusEffect, "weakness", {});
weakness.color = Color.valueOf("#EC7458");
weakness.speedMultiplier = 0.4;
weakness.healthMultiplier = 0.8;
weakness.effect = Fx.sapped;
exports.weakness = weakness;