const poison = new StatusEffect("poison");
poison.color = Color.valueOf("#CBD97F");
poison.speedMultiplier = 0.4;
poison.damage = 0.5;
poison.healthMultiplier = 0.7;
poison.effect = Fx.oily;
exports.poison = poison;

const speedUp = new StatusEffect("speedUp");
speedUp.speedMultiplier = 1.3;
speedUp.reloadMultiplier = 2;
speedUp.effect = Fx.none;
exports.speedUp = speedUp;

const speedDown = new StatusEffect("speedDown");
speedDown.speedMultiplier = 0.4;
speedDown.reloadMultiplier = 0.5;
speedDown.effect = Fx.none;
exports.speedDown = speedDown;