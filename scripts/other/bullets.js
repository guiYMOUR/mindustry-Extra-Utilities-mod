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