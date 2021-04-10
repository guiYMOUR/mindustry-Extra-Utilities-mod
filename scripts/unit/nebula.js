const c1 = Color.valueOf("84f491");
const chargeEffect = new Effect(60, cons(e => {
        Draw.color(c1, Color.valueOf("e5f3fe"), e.fout());
        Fill.circle(e.x, e.y, e.fout() * 20 + 8);

        Draw.color();
        Fill.circle(e.x, e.y, e.fout() * 3);
}));
const mw = extend(BasicBulletType, {
    update(b){
        if(b.timer.get(10)){
            for(var i = 0; i < 3; i++){
                var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, c1, 5, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(15));
                
            }
        }
    }
});
mw.sprite = "btm-gs";
mw.width = 40;
mw.height = 20;
mw.damage = 225;
mw.splashDamageRadius = 36,
mw.splashDamage = 225,
mw.lifetime = 54;
mw.speed = 8;
mw.pierceCap = 2;
mw.pierceBuilding = false;
mw.shrinkY = 0;
mw.healPercent = 18;
mw.backColor = c1;
mw.frontColor = c1;
mw.shootEffect = chargeEffect;
mw.despawnEffect = new Effect(20,cons(e => {
	Draw.color(c1);
	Lines.stroke(e.fout() * 3);
	Lines.circle(e.x, e.y, e.fin() * 60);
	Lines.stroke(e.fout() * 1.75);
	Lines.circle(e.x, e.y, e.fin() * 45);
	Draw.color(c1);
	Fill.circle(e.x, e.y, e.fout() * 20);
	Draw.color(c1);
	Fill.circle(e.x, e.y, e.fout() * 14);
}));
const l1 = extend(LaserBulletType, {});
l1.length = 230;
l1.damage = 155;
l1.width = 30;
l1.lifetime = 30;
l1.lightningSpacing = 35;
l1.lightningLength = 5;
l1.lightningDelay = 1.1;
l1.lightningLengthRand = 15;
l1.lightningDamage = 20;
l1.lightningAngleRand = 40;
l1.largeHit = true;
l1.lightColor = c1;
l1.lightningColor = c1;
l1.shootEffect = Fx.none;
l1.healPercent = 18;
l1.collidesTeam = true;
l1.sideAngle = 2;
l1.sideWidth = 5;
l1.sideLength = 30;
l1.colors = [c1, c1, Color.white];

const nebula = extendContent(UnitType, 'nebula', {});
nebula.constructor = prov(() => extend(UnitTypes.corvus.constructor.get().class, {}));
nebula.weapons.add(
    (() => {
        const w = new Weapon("btm-nebula-weapon");
        w.shake = 4;
        w.shootY = 10
        w.bullet = l1;
        w.rotate = true;
        w.rotateSpeed = 1.5;
        w.x = 25;
        w.y = -6;
        w.shootSound = Sounds.laserblast;
        w.soundPitchMin = 1;
        w.reload = 120;
        w.recoil = 0;
        return w;
    })()
);
nebula.weapons.add(
    (() => {
        const w2 = new Weapon();
        w2.shake = 4;
        w2.shootY = 13;
        w2.reload = 300;
        w2.bullet = mw;
        w2.shots = 4;
        w2.shotDelay = 9;
        w2.mirror = false;
        w2.top = false;
        w2.x = 0;
        w2.y = 0;
        w2.firstShotDelay = 60-1;
        w2.recoil = 0;
        w2.chargeSound = Sounds.lasercharge2;
        w2.shootSound = Sounds.laser;
        w2.shootStatusDuration = 60 + 24
        w2.shootStatus = StatusEffects.unmoving;
        w2.cooldownTime = 180;
        return w2;
    })()
);
nebula.armor = 16;
nebula.flying = false;
nebula.speed = 0.25;
nebula.hitSize = 35;
nebula.rotateSpeed = 1.8;
nebula.drag = 0.018;
nebula.mass = 31210;
nebula.shake = 3;
nebula.health = 54000;
nebula.mineSpeed = 7;
nebula.mineTier = 3;
nebula.buildSpeed = 2;
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