var ability = require("other/ability");
const powerShot = extend(PointBulletType, {
    despawned(b){
        this.super$despawned(b);
        new Effect(15, cons(e => {
            Draw.color(Color.white, Pal.surge, e.fin());
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * this.splashDamageRadius);
        })).at(b.x, b.y);
    },
});
powerShot.shootEffect = new Effect(10, cons(e => {
        Draw.color(Color.white, Pal.surge, e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
}));
powerShot.hitEffect = Fx.none;
powerShot.trailEffect = Fx.railTrail;
powerShot.despawnEffect = Fx.none;
powerShot.trailSpacing = 20;
powerShot.damage = 400;
powerShot.tileDamageMultiplier = 0.3;
powerShot.speed = 46 * 8;
powerShot.lifetime = 1;
powerShot.splashDamage = 300;
powerShot.splashDamageRadius = 60;
powerShot.hitShake = 6;

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
        w.shootSound = Sounds.bang;
        w.reload = 24;
        w.recoil = 5;
        return w;
    })()
);
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon2");
        w.shake = 4;
        w.shootY = 10;
        w.bullet = powerShot;
        w.rotate = true;
        w.rotateSpeed = 1.5;
        w.x = 9;
        w.y = -2;
        w.shootSound = Sounds.railgun;
        w.reload = 300;
        w.recoil = 6;
        return w;
    })()
);

suzerain.abilities.add(ability.TerritoryFieldAbility(150, 60 * 4, 200));
suzerain.abilities.add(new ShieldRegenFieldAbility(200, 800, 60 * 6, 200));
suzerain.armor = 14;
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