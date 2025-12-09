//const bullets = require("other/bullets");
const items = require("game/items");
const lib = require("blib");
const bullets = lib.getClass("ExtraUtilities.content.EUBulletTypes");

// function bulletSet(speed, damage, splashDamage, splashDamageRadius, status, ammoMultiplier){
//     var b = new ArtilleryBulletType(speed, damage, "shell");
//     b.width = b.height = 15;
//     b.lifetime = 80;
//     b.collidesTiles = false;
//     b.splashDamage = splashDamage;
//     b.splashDamageRadius = splashDamageRadius;
//     b.status = status;
//     b.ammoMultiplier = ammoMultiplier;
//     return b;
// }
// const b1 = bulletSet(3, 22, 45, 2.6*8, StatusEffects.none, 2);
// b1.hitEffect = Fx.flakExplosion;
// b1.knockback = 0.8;
// const b2 = bulletSet(2.5, 30, 40, 5*8, StatusEffects.freezing, 4);
// b2.backColor = Pal.lancerLaser;
// b2.frontColor = items.crispSteel.color;
// b2.hitEffect = Fx.flakExplosion;
// b2.knockback = 1;
// b2.reloadMultiplier = 1.2;
// const b3 = bulletSet(2.5, 35, 60, 3*8, StatusEffects.burning, 2);
// b3.backColor = Pal.lightOrange;
// b3.frontColor = Pal.lightishOrange;
// b3.hitEffect = Fx.blastExplosion;
// b3.knockback = 0.8;
// const b4 = bulletSet(2.5, 70, 120, 3*8, StatusEffects.shocked, 1);
// b4.backColor = items.lightninAlloy.color;
// b4.frontColor = Pal.surge;
// b4.hitEffect = Fx.blastExplosion;
// b4.knockback = 0.8;
// b4.lightningDamage = 12;
// b4.lightning = 4;
// b4.lightningLength = 9;

const IM = new ItemTurret("IM");
IM.ammo(
    Items.graphite, bullets.b1,
    items.crispSteel, bullets.b2,
    Items.pyratite, bullets.b3,
    Items.surgeAlloy, bullets.b4,
);
Object.assign(IM, {
    size : 2,
    recoil : 2.5,
    reload : 60,
    //coolantMultiplier : 6,
    range : 33*8,
    shootCone : 15,
    targetAir : false,
    ammoUseEffect : Fx.none,
    health : 300 * 2 * 2,
    shootSound : Sounds.unitExplode2,
});
IM.requirements = ItemStack.with(
    Items.copper, 100,
    Items.graphite, 70,
    items.crispSteel, 60
);
IM.coolant = IM.consumeCoolant(0.15);
IM.buildVisibility = BuildVisibility.shown;
IM.category = Category.turret;

exports.IM = IM;