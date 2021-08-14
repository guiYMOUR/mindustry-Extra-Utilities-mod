/*
* @author <guiY>
* @readme <bulletTypes>
*/

const items = require("game/items");

exports.artillerySurge = new ArtilleryBulletType(2.8, 20, "shell");
Object.assign(exports.artillerySurge, {
    hitEffect : Fx.blastExplosion,
    knockback : 0.8,
    lifetime : 100,
    width : 14,
    height : 14,
    collidesTiles : false,
    ammoMultiplier : 3,
    splashDamageRadius : 30 * 0.75,
    splashDamage : 38,
    frontColor : Pal.surge,
    lightningDamage : 12,
    lightning : 2,
    lightningLength : 8,
});

const shieldBullet = (() => {
    return (object) => {
        const options = Object.assign({
            lifetime : 60,
            splashDamageRadius : 80,
            shootEffect : Fx.none,
            hitEffect : Fx.none,
            smokeEffect : Fx.none,
            trailEffect : Fx.none,
            despawnEffect : Fx.none,
            damage : 0,
            speed : 0,
            collides : false,
            collidesAir : false,
            collidesGround : false,
            absorbable : false,
            hittable : false,
            keepVelocity : false,
            reflectable : false,
        }, object);
        const eff1 = new Effect(35, cons(e => {
            Draw.color(items.lightninAlloy.color);
                Lines.stroke(e.fout() * 4); 
                Lines.poly(e.x, e.y, 6, options.splashDamageRadius * 0.525 + 75 * e.fin());
}));
        const shieldDefense = new Effect(20, cons(e => {
                Draw.color(items.lightninAlloy.color);
                Lines.stroke(e.fslope() * 2.5);
                Lines.poly(e.x, e.y, 6, 3 * e.fout() + 9);
                const d = new Floatc2({get(x, y){
                    Lines.poly(e.x + x, e.y + y, 6, 2 * e.fout() + 2);
                }})
                Angles.randLenVectors(e.id, 2, 32 * e.fin(), 0, 360,d);
}));
        const shield = new JavaAdapter(BasicBulletType, {
            update(b){
                const realRange = this.splashDamageRadius * b.fout();
                Groups.bullet.intersect(b.x - realRange, b.y - realRange, realRange * 2, realRange * 2, cons(trait =>{
                    if(trait.type.absorbable && trait.team != b.team && Intersector.isInsideHexagon(trait.getX(), trait.getY(), realRange, b.x, b.y) ){
                        trait.absorb();
                        shieldDefense.at(trait);
                    }
                }));
            },
            init(b){
                if(b == null) return;
                eff1.at(b.x, b.y, b.fout(), items.lightninAlloy.color);
            },
            draw(b){
                Draw.color(items.lightninAlloy.color);
                var fout = Math.min(b.fout(), 0.5) *2;
                Lines.stroke(fout * 3);
                Lines.poly(b.x, b.y, 6, (this.splashDamageRadius * 0.525) * fout * fout);
                Draw.alpha(fout * fout * 0.15);
                Fill.poly(b.x, b.y, 6, (this.splashDamageRadius * 0.525) * fout * fout);
            },
        });
        shield.lifetime = options.lifetime;
        shield.splashDamageRadius = options.splashDamageRadius;
        shield.shootEffect = options.shootEffect;
        shield.hitEffect = options.hitEffect;
        shield.smokeEffect = options.smokeEffect;
        shield.trailEffect = options.trailEffect;
        shield.despawnEffect = options.despawnEffect;
        shield.damage = options.damage;
        shield.speed = options.speed;
        shield.collides = options.collides;
        shield.collidesAir = options.collidesAir;
        shield.collidesGround = options.collidesGround;
        shield.absorbable = options.absorbable;
        shield.hittable = options.hittable;
        shield.keepVelocity = options.keepVelocity;
        shield.reflectable = options.reflectable;
        return shield;
    }
})();
exports.shieldBullet = shieldBullet;

function flameShoot(colorBegin, colorTo, colorEnd, length, cone, number, lifetime){
    return new Effect(lifetime, 80, cons(e => {
        Draw.color(colorBegin, colorTo, colorEnd, e.fin());
        Angles.randLenVectors(e.id, number, e.finpow() * length, e.rotation, cone, (x, y) => {
            Fill.circle(e.x + x, e.y + y, 0.65 + e.fout() * 1.5);
        });
    }));
}
//flameBullet
const flame = (() => {
    return (object) => {
        const options = Object.assign({
            //not in bullet
            flameLength : 88,//real flame range
            flameCone : 10,
            particleNumber : 72,
            //flameColors▼
            colorBegin : Pal.lightFlame,
            colorTo : Pal.darkFlame,
            colorEnd : Color.gray,
            //in bullet
            ammoMultiplier : 3,
            lifetime : 22,
            hitEffect : Fx.none,
            smokeEffect : Fx.none,
            trailEffect : Fx.none,
            despawnEffect : Fx.none,
            damage : 20,
            speed : 0,
            pierce : true,
            collidesAir : false,
            absorbable : false,
            hittable : false,
            keepVelocity : false,
            status : StatusEffects.burning,
            statusDuration : 60 * 4,
            buildingDamageMultiplier : 0.4,
        }, object);
        options.shootEffect = flameShoot(options.colorBegin, options.colorTo, options.colorEnd, options.flameLength/0.75, options.flameCone, options.particleNumber, options.lifetime + 10);
        //Define a bullet▼
        const f = extend(BulletType, {
            //draw hitsize
            hit(b){
                if(this.absorbable && b.absorbed) return;
                //let's step by step
                //unit▼
                Units.nearbyEnemies(b.team, b.x, b.y, options.flameLength, cons(unit =>{
                    if(Angles.within(b.rotation(), b.angleTo(unit), options.flameCone) && unit.checkTarget(this.collidesAir, this.collidesGround)){
                        Fx.hitFlameSmall.at(unit);
                        unit.damage(this.damage * this.ammoMultiplier);
                        unit.apply(this.status, this.statusDuration);
                    }
                }));
                //block▼
                Vars.indexer.allBuildings(b.x, b.y, options.flameLength, cons(other => {
                    if(other.team != b.team && Angles.within(b.rotation(), b.angleTo(other), options.flameCone)){
                        Fx.hitFlameSmall.at(other);
                        other.damage(this.damage * options.buildingDamageMultiplier * this.ammoMultiplier);
                    }
                }));
            },
        });
        f.ammoMultiplier = options.ammoMultiplier;
        f.lifetime = options.lifetime;
        f.shootEffect = options.shootEffect;
        f.hitEffect = options.hitEffect;
        f.smokeEffect = options.smokeEffect;
        f.trailEffect = options.trailEffect;
        f.despawnEffect = options.despawnEffect;
        f.damage = options.damage;
        f.speed = options.speed;
        f.pierce = options.pierce;
        f.collidesAir = options.collidesAir;
        f.absorbable = options.absorbable;
        f.hittable = options.hittable;
        f.keepVelocity = options.keepVelocity;
        f.status = options.status;
        f.statusDuration = options.statusDuration;
        f.despawnHit = true;
        return f;
    }
})();
exports.flame = flame;

const unitHealCone =(() => {
    function posx(x, y, length, angle){
        var a = (Math.PI * angle)/180;
        var cos = Math.cos(a);
        var px = x + length * cos;
        return px;
    }
    function posy(x, y, length, angle){
        var a = (Math.PI * angle)/180;
        var sin = Math.sin(a);
        var py = y + length * sin;
        return py;
    }
    return (object) => {
        const options = Object.assign({
            lifetime : 180,
            healPercent : 12,
            findRange : 176,
            findAngle : 46,
        }, object);
        const hb = extend(BasicBulletType,{
            range(){
                return options.findRange;
            },
            update(b){
                const ratio = 60 * 100;
                Units.nearby(b.team, b.x, b.y, options.findRange, cons(unit =>{
                    if(unit.damaged() && Angles.within(b.rotation(), b.angleTo(unit), options.findAngle/2)){
                        unit.heal((unit.maxHealth < 1000 ? 1000 : unit.maxHealth) * (this.healPercent/ratio));
                    }
                }));
                Vars.indexer.eachBlock(b, options.findRange, boolf(other => other.damaged() && Angles.within(b.rotation(), b.angleTo(other), options.findAngle/2)), cons(other => {
                    other.heal((this.healPercent/ratio) * other.maxHealth);
                    Fx.healBlockFull.at(other.x, other.y, !(other instanceof PayloadSource.PayloadSourceBuild) ? other.block.size : 5, Pal.heal);
                }));
            },
            draw(b){
                const range = options.findRange;
                const angle = options.findAngle;
                Draw.color(Pal.heal);
                Draw.z(Layer.buildBeam);
                Draw.alpha(0.8);
                Fill.circle(b.x, b.y, 4);
                for(var i = b.rotation() - angle/2; i < b.rotation() + angle/2; i+=2){
                    var px1 = posx(b.x, b.y, range, i);
                    var py1 = posy(b.x, b.y, range, i);
                    var px2 = posx(b.x, b.y, range, i+2);
                    var py2 = posy(b.x, b.y, range, i+2);
                    Fill.tri(b.x, b.y, px1, py1, px2, py2);
                }
                Draw.alpha(1);
                Draw.z();
            },
        });
        const fadeEffect = new Effect(15, cons(e =>{
            const range = options.findRange * e.fout();
            const angle = options.findAngle;
            Draw.color(Pal.heal);
            Draw.z(Layer.buildBeam);
            Draw.alpha(0.8);
            for(var i = e.rotation - angle/2; i < e.rotation + angle/2; i+=2){
                var px1 = posx(e.x, e.y, range, i);
                var py1 = posy(e.x, e.y, range, i);
                var px2 = posx(e.x, e.y, range, i+2);
                var py2 = posy(e.x, e.y, range, i+2);
                Fill.tri(e.x, e.y, px1, py1, px2, py2);
            }
        }));
        Object.assign(hb, {
            speed : 0,
            lifetime : options.lifetime,
            damage : 0,
            collides: false,
            collidesAir: false,
            collidesGround: false,
            absorbable: false,
            hittable: false,
            keepVelocity: false,
            despawnEffect : fadeEffect,
            shootEffect : Fx.none,
            smokeEffect : Fx.none,
            healPercent : options.healPercent,
        });
        return hb;
    }
})();
exports.unitHealCone = unitHealCone;

const eff1 = new Effect(10, cons(e => {
        Draw.color(Color.white, Pal.heal, e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 13);
}));
const antiMissileBullet = extend(BasicBulletType, {
    range(){
        return this.homingRange * 2.5;
    },
    update(b){
        var target;
        target = Groups.bullet.intersect(b.x - this.homingRange, b.y - this.homingRange, this.homingRange*2, this.homingRange*2).min(other => other.team != b.team && (other.type.homingPower > 0 || other instanceof MissileBulletType), other => other.dst2(b.x, b.y));
        if(target != null){
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), 30));
            if(b.within(target.x, target.y, target.type.homingRange)){
                target.vel.setAngle(Angles.moveToward(target.rotation(), target.angleTo(b), target.type.homingPower * Time.delta * 50));
            }
            if(target.within(b.x, b.y, this.splashDamageRadius * target.type.speed)){
                target.remove();
                b.remove();
            }
        }
        if(Mathf.chanceDelta(this.trailChance)){
            this.trailEffect.at(b.x, b.y, this.trailParam, this.trailColor);
        }
    },
});
Object.assign(antiMissileBullet, {
    sprite : "btm-anti",
    shrinkY : 0,
    width : 8,
    height: 9,
    trailChance : 0.8,
    trailColor : Color.valueOf("6f6f6f"),
    damage : 0,
    speed : 15,
    hitEffect : Fx.blastExplosion,
    shootEffect : eff1,
    smokeEffect : Fx.none,
    splashDamage : 0,
    splashDamageRadius : 8,
    lifetime : Math.ceil((48*12)/15),
    homingRange : 48*6,
    absorbable : false,
    hittable : false,
    collides : false,
    collidesTiles : false,
    collidesAir : false,
    collidesGround : false,
});
exports.antiMissileBullet = antiMissileBullet;