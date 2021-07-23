const ability = require("other/ability");

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
sapper.sapStrength = 0.85;
sapper.length = 88;
sapper.damage = 40;
sapper.shootEffect = Fx.shootSmall;
sapper.hitColor = Color.valueOf("bf92f9");
sapper.color = Color.valueOf("bf92f9");
sapper.despawnEffect = Fx.none;
sapper.width = 0.55;
sapper.lifetime = 30;
sapper.knockback = -1;

const lb = extend(BasicBulletType, {
    update(b){
        this.super$update(b);
        var range = this.homingRange;
        if(b.timer.get(1, 5)){
            Units.nearbyEnemies(b.team, b.x - range, b.y - range, range * 2, range * 2, cons(other => {
                if(other.within(b, range)){
                    other.damage(this.damage/12);
                    other.apply(this.status, 5);
                    Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, other);
                    Fx.hitLaserBlast.at(other.x, other.y, b.angleTo(other), Pal.sapBulletBack);
                }
            }));
        }
    },
    hit(b){
        var range2 = this.splashDamageRadius
        Units.nearbyEnemies(b.team, b.x - range2, b.y - range2, range2 * 2, range2 * 2, cons(other => {
            if(other.within(b, range2)){
                other.damage(this.splashDamage);
                other.apply(this.status, 60);
                Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, other);
                Fx.hitLaserBlast.at(other.x, other.y, b.angleTo(other), Pal.sapBulletBack);
            }
        }));
        Vars.indexer.allBuildings(b.x, b.y, range2, cons(other =>{
            if(other.block != null && other.team != b.team){
                if(other.block.group == BlockGroup.power || other.block.group == BlockGroup.turrets || other.block.group == BlockGroup.transportation){
                    if(other.block instanceof PowerNode){
                        for(var i = 0; i < other.power.links.size; i++){
                            var link = Vars.world.build(other.power.links.get(i));
                            if(link == null || !other.block.linkValid(other, link)) return;
                            other.power.graph.removeList(link);
                            other.power.graph.remove(link);
                            link.power.graph.removeList(other);
                            link.power.graph.remove(other);
                            other.power.links.removeValue(link.pos());
                            link.power.links.removeValue(other.pos());
                        }
                    //other.kill();
                    } else {
                        other.damage(this.splashDamage * 1.5);
                        Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, other);
                        Fx.hitLaserBlast.at(other.x, other.y, b.angleTo(other), Pal.sapBulletBack);
                    }
                } else {
                    other.damage(this.splashDamage);
                }
            }
        }));
        new Effect(18, cons(e => {
            Draw.color(Pal.sapBulletBack);
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * range2);
        })).at(b);
        Sounds.spark.at(b);
    },
});
lb.width = 28;
lb.height = 18;
lb.frontColor = Pal.sapBullet;
lb.backColor = Pal.sapBulletBack;
lb.lifetime = 150;
lb.speed = 2;
lb.trailLength = 18;
lb.trailWidth = 8;
lb.trailColor = Pal.sapBulletBack;
lb.trailInterval = 3;
lb.damage = 120;
lb.splashDamage = 100;
lb.splashDamageRadius = 100;
lb.hitShake = 4;
lb.collidesTiles = false;
lb.trailRotation = true;
lb.status = StatusEffects.sapped;
lb.trailEffect = new Effect(16, cons(e => {
    Draw.color(Pal.sapBulletBack);
    for(var s in Mathf.signs){
        Drawf.tri(e.x, e.y, 4, 30 * e.fslope(), e.rotation + 90*s);
    }
}));
lb.despawnEffect = Fx.none;
//lb.despawnSound = Sounds.spark;
lb.homingPower = 0.08;
lb.homingRange = 256;
lb.homingDelay = 30;

const asphyxia = extendContent(UnitType, 'asphyxia', {});
asphyxia.constructor = prov(() => extend(UnitTypes.toxopid.constructor.get().class, {}));
asphyxia.weapons.add(
    (() =>{
        const w = new Weapon("btm-asphyxia-l");
        w.x = 14;
        w.y = -6;
        w.reload = 60;
        w.rotate = true;
        w.bullet = lb;
        w.shootSound = Sounds.bang;
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
        w.shootSound = Sounds.beam;
        w.bullet = asl;
        w.mirror = false;
        w.continuous = true;
        w.rotate = true;
        w.rotateSpeed = 3;
        w.x = 0;
        w.y = -10;
        w.chargeSound = Sounds.lasercharge2;
        w.firstShotDelay = 49;
        w.recoil = 3;
        return w;
    })()
);
asphyxia.weapons.add(
    (() =>{
        const w = new Weapon("btm-asphyxia-f");
        w.x = 10;
        w.y = 11;
        w.reload = 9;
        w.rotate = true;
        w.bullet = sapper;
        w.shootSound = Sounds.sap;
        return w;
    })()
);
asphyxia.weapons.add(
    (() =>{
        const w = new Weapon("btm-asphyxia-b");
        w.x = 15;
        w.y = 7;
        w.reload = 14;
        w.rotate = true;
        w.bullet = sapper;
        w.shootSound = Sounds.sap;
        return w;
    })()
);

asphyxia.armor = 15;
asphyxia.flying = false;
asphyxia.speed = 0.4;
asphyxia.hitSize = 32;
asphyxia.lightRadius = 160;
asphyxia.rotateSpeed = 1.8;
asphyxia.landshake = 1.1;
asphyxia.health = 59000;
asphyxia.buildSpeed = 1.5;
asphyxia.itemCapacity = 300;
asphyxia.rotateShooting = true;
asphyxia.drawShields =false;
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
asphyxia.ammoType = AmmoTypes.powerHigh;
asphyxia.legSplashDamage = 90;
asphyxia.legSplashRange = 60;
exports.asphyxia = asphyxia;