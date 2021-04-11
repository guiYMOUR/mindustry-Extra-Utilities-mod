var ability = require("other/ability");

const suzerain = extendContent(UnitType, 'suzerain', {});
suzerain.constructor = prov(() => extend(UnitTypes.reign.constructor.get().class, {}));
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon");
        w.shake = 4;
        w.shootY = 11;
        w.top = false;
        w.shots = 5;
        w.inaccuracy = 1;
        w.shotDelay = 3;
        w.bullet = Bullets.standardThoriumBig;
        w.rotate = false;
        w.x = 22;
        w.y = 1;
        w.shootSound = Sounds.bang
        w.soundPitchMin = 1;
        w.reload = 24;
        w.recoil = 5;
        return w;
    })()
);

suzerain.abilities.add(ability.TerritoryFieldAbility(150, 60 * 4, 200));
suzerain.abilities.add(new ShieldRegenFieldAbility(200, 800, 60 * 6, 200));
suzerain.armor = 17;
suzerain.flying = false;
suzerain.speed = 0.3;
suzerain.hitSize = 26;
suzerain.rotateSpeed = 1.8;
suzerain.canDrown = false;
suzerain.mechStepParticles = true;
suzerain.mechStepShake = 1;
suzerain.mechFrontSway = 1.9;
suzerain.mechSideSway = 0.6;
suzerain.health = 61000;
suzerain.itemCapacity = 300;
suzerain.rotateShooting = true;
suzerain.commandLimit = 8;
exports.suzerain = suzerain;