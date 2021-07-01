const ability = require("other/ability");

var railTrail = new Effect(16, cons(e => {
    Draw.color(Pal.heal);
    for(var i of Mathf.signs){
        Drawf.tri(e.x, e.y, 10 * e.fout(), 24, e.rotation + 90 + 90 * i);
    }
}));
const nebulaShoot = new Effect(24, cons(e => {
    e.scaled(10, cons(b => {
        Draw.color(Color.white, Pal.heal, b.fin());
        Lines.stroke(b.fout() * 3 + 0.2);
        Lines.circle(b.x, b.y, b.fin() * 50);
    }));
    Draw.color(Pal.heal);
    for(var i of Mathf.signs){
        Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + 90 * i);
    }
}));

const r = extend(RailBulletType, {});
r.shootEffect = nebulaShoot;
r.length = 500;
r.updateEffectSeg = 30;
r.pierceEffect = Fx.railHit;
r.updateEffect = railTrail;
r.hitEffect = Fx.massiveExplosion;
r.smokeEffect = Fx.shootBig2;
r.damage = 1550;
r.pierceDamageFactor = 0.5;

const nebula = extendContent(UnitType, 'nebula', {});
nebula.constructor = prov(() => extend(UnitTypes.corvus.constructor.get().class, {}));
nebula.weapons.add(
    (() => {
        const w2 = new Weapon("btm-nebula-weapon");
        w2.shake = 4;
        w2.shootY = 13;
        w2.reload = 180;
        w2.cooldownTime = 90;
        w2.bullet = r;
        w2.mirror = false;
        w2.top = false;
        w2.x = 0;
        w2.y = 0;
        w2.shootSound = Sounds.railgun;
        w2.recoil = 0;
        return w2;
    })()
);
nebula.weapons.add(
    (() =>{
        const w = new PointDefenseWeapon("btm-nebula-defense");
        w.x = 25;
        w.y = -8;
        w.reload = 8;
        w.targetInterval = 8;
        w.targetSwitchInterval = 8;
        w.mirror = false;
        w.bullet = (() => {
            const b = new BulletType();
            b.shootEffect = Fx.sparkShoot;
            b.hitEffect = Fx.pointHit;
            b.maxRange = 320;
            b.damage = 40;
            return b;
        })()
        return w;
    })()
);
nebula.weapons.add(
    (() =>{
        const w = new PointDefenseWeapon("btm-nebula-defense");
        w.x = -25;
        w.y = -8;
        w.reload = 8;
        w.targetInterval = 8;
        w.targetSwitchInterval = 8;
        w.mirror = false;
        w.bullet = (() => {
            const b = new BulletType();
            b.shootEffect = Fx.sparkShoot;
            b.hitEffect = Fx.pointHit;
            b.maxRange = 320;
            b.damage = 40;
            return b;
        })()
        return w;
    })()
);
nebula.abilities.add(ability.MendFieldAbility(180, 210, 10));
//nebula.abilities.add(ability.pointDefenseAbility(25, -8, 10, 320, 40, "nebula-defense"), ability.pointDefenseAbility(-25, -8, 10, 320, 40, "nebula-defense"));
nebula.armor = 15;
nebula.flying = false;
nebula.speed = 0.25;
nebula.hitSize = 35;
nebula.rotateSpeed = 1.8;
nebula.shake = 3;
nebula.health = 54000;
nebula.mineSpeed = 7;
nebula.mineTier = 3;
nebula.buildSpeed = 3;
nebula.itemCapacity = 300;
nebula.rotateShooting = true;
nebula.drawShields =false;
nebula.legCount = 6;
nebula.legLength = 22;
nebula.legBaseOffset = 11;
nebula.legMoveSpace = 1.5;
nebula.legTrns = 0.58;
nebula.hovering = true;
nebula.visualElevation = 0.2;
nebula.allowLegStep = true;
nebula.groundLayer = Layer.legUnit;
nebula.commandLimit = 8;
nebula.ammoType = AmmoTypes.powerHigh;
exports.nebula = nebula;