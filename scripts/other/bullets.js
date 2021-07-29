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