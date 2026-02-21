const ability = require("other/ability");
const bullets = require("other/bullets");
const weapon = require("other/weapon");

const lineBullet = bullets.lineBullet({
    damageUp : 1.2,
});

const asl = extend(ContinuousLaserBulletType, {
    update(b){
        this.super$update(b);
        //new SoundLoop(Sounds.beam, 1).update(b.x, b.y, b != null);
        if(Mathf.chance(0.5)){
            Lightning.create(b.team, Pal.sapBullet, this.damage/2, b.x, b.y, b.rotation() + (4 - Mathf.range(8)), this.length/6);
        }
    },
});
asl.damage = 80;
asl.length = 240;
asl.hitEffect = Fx.sapExplosion;
asl.drawSize = 420;
asl.lifetime = 160;
asl.despawnEffect = Fx.smokeCloud;
asl.smokeEffect = Fx.none;
asl.shootEffect = new Effect(40, 100, cons(e => {
    Draw.color(Pal.sapBullet);
    Lines.stroke(e.fin() * 2);
    Lines.circle(e.x, e.y, e.fout() * 50);
}));
asl.status = StatusEffects.wet;
asl.incendChance = 0.075;
asl.incendSpread = 5;
asl.incendAmount = 1;
asl.colors = [Pal.sapBullet, Pal.sapBullet, Pal.sapBulletBack];

const sapper = new SapBulletType();
sapper.sapStrength = 1.5;
sapper.length = 88;
sapper.damage = 40;
sapper.shootEffect = Fx.shootSmall;
sapper.hitColor = Color.valueOf("bf92f9");
sapper.color = Color.valueOf("bf92f9");
sapper.despawnEffect = Fx.none;
sapper.width = 0.55;
sapper.lifetime = 30;
sapper.knockback = -1;

const asphyxia = new UnitType("asphyxia");
asphyxia.constructor = prov(() => extend(UnitTypes.toxopid.constructor.get().class, {}));

asphyxia.weapons.add(
    (() =>{
        function setStat(W){
            W.add("[accent]" + lineBullet.maxFindValue() + " [lightgray]max link");
        }
        const w = weapon.statWeapon(setStat, "btm-asphyxia-l");
        w.x = 14;
        w.y = -5;
        w.reload = 60;
        w.rotate = true;
        w.bullet = lineBullet;
        w.shootSound = Sounds.shootReign;
        return w;
    })()
);
asphyxia.weapons.add(
    (() => {
        const w = new Weapon("btm-asphyxia-main");
        w.shake = 4;
        w.shootY = 13;
        w.reload = 150;
        w.cooldownTime = 200;
        w.shootSound = Sounds.beamMeltdown;
        w.bullet = asl;
        w.mirror = false;
        w.continuous = true;
        w.rotate = true;
        w.rotateSpeed = 3;
        w.x = 0;
        w.y = -12;
        w.chargeSound = Sounds.chargeVela;
        w.firstShotDelay = 49;
        w.recoil = 3;
        return w;
    })()
);
asphyxia.weapons.add(
    (() =>{
        const w = new Weapon("btm-asphyxia-f");
        w.x = 9;
        w.y = 8;
        w.reload = 9;
        w.rotate = true;
        w.autoTarget = true;
        w.controllable = false;
        w.bullet = sapper;
        w.shootSound = Sounds.shootSap;
        return w;
    })()
);
asphyxia.weapons.add(
    (() =>{
        const w = new Weapon("btm-asphyxia-f");
        w.x = 14;
        w.y = 6;
        w.reload = 14;
        w.rotate = true;
        w.autoTarget = true;
        w.controllable = false;
        w.bullet = sapper;
        w.shootSound = Sounds.shootSap;
        return w;
    })()
);

asphyxia.armor = 15;
asphyxia.flying = false;
asphyxia.speed = 0.4;
asphyxia.hitSize = 33;
asphyxia.lightRadius = 160;
asphyxia.rotateSpeed = 1.8;
asphyxia.landShake = 1.5;
asphyxia.health = 60000;
asphyxia.buildSpeed = 1.5;
asphyxia.itemCapacity = 300;
asphyxia.rotateShooting = true;
asphyxia.legCount = 8;
asphyxia.legLength = 78;
asphyxia.legBaseOffset = 8;
asphyxia.legMoveSpace = 0.8;
asphyxia.legTrns = 0.58;
asphyxia.legPairOffset = 3;
asphyxia.legExtension = -20;
asphyxia.legSpeed = 0.1;
asphyxia.legLengthScl = 0.93;
asphyxia.rippleScale = 3;
asphyxia.legSpeed = 0.19;
asphyxia.hovering = true;
asphyxia.visualElevation = 0.95;
asphyxia.allowLegStep = true;
asphyxia.groundLayer = Layer.legUnit;
asphyxia.commandLimit = 8;
asphyxia.ammoType = new PowerAmmoType(3000);
asphyxia.legSplashDamage = 90;
asphyxia.legSplashRange = 60;
exports.asphyxia = asphyxia;