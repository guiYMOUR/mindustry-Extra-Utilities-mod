const ability = require("other/ability");
//const bullets = require("other/bullets");
const status = require("other/status");
const weapon = require("other/weapon");

const skylineShoot = new Effect(24, cons(e => {
    Draw.color(Pal.sapBullet);
    for(var i of Mathf.signs){
        Drawf.tri(e.x, e.y, 13 * e.fout(), 40, e.rotation + 90 * i);
    }
}));
const bme = new Effect(42, cons(e => {
    Draw.color(Pal.sapBullet, Pal.sapBulletBack, e.fin());
    Lines.stroke(15);
    Lines.circle(e.x, e.y, 36 * e.fout());
    Draw.color(Color.black);
    Fill.circle(e.x, e.y, 27 * e.fout());
}));
const bfe = new Effect(42, cons(e => {
    Draw.color(Color.black);
    Lines.stroke(25);
    Lines.circle(e.x, e.y, 22 * e.fout());
    Draw.color(Color.white, Pal.sapBulletBack, e.fout());
    Fill.circle(e.x, e.y, 9 + Math.sin(12 * e.fin()));
}));
const sE = new Effect(46, cons(e => {
    Draw.color(Pal.sapBullet);
    var fout = Math.max(1-e.fin() * 1.8, 0);
    for(var r of [-1, 1]){
        Drawf.tri(e.x, e.y, 36, 56*(1-fout), e.rotation + 180 * fout * r);
    }
}));
const defData = {stroke : 6, x : 0, y : 0};
const lineE = new Effect(12, cons(e => {
    var data = e.data ? e.data: defData;
    Draw.z(Layer.effect -1);
    Draw.color(Pal.sapBullet);
    Lines.stroke(data.stroke + 1);
    Lines.line(e.x, e.y, data.x, data.y);
    Draw.color(Color.black);
    Lines.stroke(data.stroke);
    Lines.line(e.x, e.y, data.x, data.y);
    Drawf.light(null, e.x, e.y, data.x, data.y, 20, Color.white, 0.5 * e.fout());
}));

const bulletT = extend(BulletType, {
    draw(b){
        this.super$draw(b);
        Draw.color(Pal.sapBullet);
        Drawf.tri(b.x, b.y, 8, 16, b.rotation());
        Drawf.tri(b.x, b.y, 8, 30 * Math.min(1, b.time / this.speed * 0.8 + 0.2), b.rotation() - 180);
        Draw.reset();
    },
});
bulletT.homingPower = 0.1;
bulletT.homingRange = 100;
bulletT.speed = 4;
bulletT.lifetime = 81;
bulletT.shootEffect = skylineShoot;
bulletT.despawnEffect = bulletT.hitEffect = Fx.massiveExplosion;
bulletT.smokeEffect = Fx.shootBig2;
bulletT.damage = 80;
bulletT.keepVelocity = false;
bulletT.status = StatusEffects.sapped;
bulletT.trailLength = 11;
bulletT.trailWidth = 6;
bulletT.trailColor = Pal.sapBulletBack;
bulletT.trailEffect = Fx.none;

const Range = 32;
const bulletF = extend(BulletType, {
    despawned(b){
        Damage.damage(b.team, b.x, b.y, Range, b.damage, true, true, true);
        Effect.shake(5, 5, b);
        Sounds.plasmadrop.at(b);
        bfe.at(b);
    },
});
bulletF.lifetime = 42;
bulletF.damage = 0;
bulletF.collidesGround = bulletF.collidesAir = bulletF.collidesTiles = bulletF.collides = false;
bulletF.keepVelocity = false;

const bulletM = extend(BulletType, {
    hit(b){  },
    hitTile(b, build, initialHealth, direct){  },
    hitEntity(b, entity, health){  },
    update(b){
        var px = b.x + b.lifetime * b.vel.x,
            py = b.y + b.lifetime * b.vel.y,
            rot = b.rotation();
        b.time = b.lifetime;
        b.set(px, py);
        b.remove();
        b.vel.setZero();
    },
    despawned(b){
        lineE.at(b.x, b.y, 0, {stroke : 6, x : b.owner.x, y : b.owner.y});
        bme.at(b);
        bulletF.create(b.owner, b.team, b.x, b.y, 0, b.damage, 0, 1, null);
    },
});
bulletM.collides = false;
bulletM.scaleVelocity = true;
bulletM.damage = 200;
bulletM.lifetime = 1;
bulletM.speed = 45*8;
bulletM.shootEffect = sE;
bulletM.trailEffect = Fx.none;
bulletM.keepVelocity = false;

const skyline = new UnitType("skyline");
skyline.abilities.add(ability.healthRequireAbility(0.5, 1, status.defenseUp, false));
skyline.abilities.add(ability.healthRequireAbility(0.15, 0, status.weakness, false));
skyline.constructor = prov(() => extend(UnitTypes.eclipse.constructor.get().class, {}));
skyline.weapons.add(
    (() => {
        const w = weapon.healthDeWeapon("btm-skyline-missile");
        w.shake = 3;
        w.shootY = 10;
        w.bullet = bulletT;
        w.rotate = true;
        w.rotateSpeed = 2;
        w.x = 20;
        w.y = 23;
        w.shotDelay = 1;
        //w.xRand = 2;
        w.shots = 1;
        //w.spacing = 6;
        w.inaccuracy = 0;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.plasmaboom;
        w.reload = 36;
        w.recoil = 2;
        return w;
    })()
);
skyline.weapons.add(
    (() => {
        const w = weapon.healthDeWeapon("btm-skyline-missile");
        w.shake = 3;
        w.shootY = 10;
        w.bullet = bulletT;
        w.rotate = true;
        w.rotateSpeed = 3;
        w.x = 28;
        w.y = -15;
        w.shotDelay = 1;
        //w.xRand = 2;
        w.shots = 1;
        //w.spacing = 6;
        w.inaccuracy = 0;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.plasmaboom;
        w.reload = 48;
        w.recoil = 2;
        return w;
    })()
);
skyline.weapons.add(
    (() => {
        const w = weapon.healthDeWeapon("btm-skyline-main");
        w.shake = 6;
        w.shootY = 0;
        w.bullet = bulletM;
        w.mirror = false;
        w.top = false;
        w.rotate = true;
        w.rotateSpeed = 60;
        w.x = 0;
        w.y = 0;
        w.ejectEffect = Fx.none;
        w.shootSound = Sounds.release;
        w.reload = 180;
        w.cooldownTime = 90;
        w.firstShotDelay = 30;
        w.recoil = 5;
        return w;
    })()
);
skyline.armor = 13;
skyline.flying = true;
skyline.speed = 0.51;
skyline.hitSize = 62;
skyline.accel = 0.04;
skyline.rotateSpeed = 1;
skyline.baseRotateSpeed = 20;
skyline.drag = 0.018;
skyline.health = 20000;
skyline.lowAltitude = true;
skyline.itemCapacity = 220;
skyline.engineOffset = 41;
skyline.engineSize = 11;
skyline.rotateShooting = true;
skyline.commandLimit = 8;
skyline.destructibleWreck = false;
skyline.targetFlags = UnitTypes.eclipse.targetFlags;
skyline.immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.sapped);
skyline.ammoType = new ItemAmmoType(Items.sporePod);

exports.skyline = skyline;