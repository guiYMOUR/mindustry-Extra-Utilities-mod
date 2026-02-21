//
const lib = require('blib');
const items = require("game/items");

const chargeTime = 40;
const chargeEffect = lib.newEffect(20, (e) => {
        Draw.color(Pal.surge);
        //Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fout() * 54);
        Draw.reset();
});
const chargeEffectT = lib.newEffect(20, (e) => {
    Draw.color(Pal.lancerLaser);
    //Lines.stroke(e.fout() * 2 + 0.2);
    Lines.circle(e.x, e.y, e.fout() * 54);
    Draw.reset();
});
const chargeBeginEffect = lib.newEffect(chargeTime * 1.2, e => {
    Draw.color(Pal.surge, Color.valueOf("e5f3fe"), e.fout());
    Fill.circle(e.x, e.y, e.fout() * 19 + 7);

    Draw.color();
    Fill.circle(e.x, e.y, e.fout() * 3);
});
const chargeBeginEffectT = lib.newEffect(chargeTime * 1.2, e => {
    Draw.color(Pal.lancerLaser, Color.valueOf("e5f3fe"), e.fout());
    Fill.circle(e.x, e.y, e.fout() * 19 + 7);

    Draw.color();
    Fill.circle(e.x, e.y, e.fout() * 3);
});
const chargeSound = Sounds.chargeVela;

const bigShotEffS = lib.Fx.aimEffect(chargeTime + 5, Pal.surge, 1.2, 60 * 8, 12);
const bigShotEffT = lib.Fx.aimEffect(chargeTime + 5, Pal.lancerLaser, 1.2, 60 * 8, 12);

const RGT = extend(BasicBulletType, {});
RGT.sprite = "extra-utilities-gt";
RGT.width = 40;
RGT.height = 20;
RGT.damage = 310;
RGT.splashDamageRadius = 28;
RGT.splashDamage = 300;
RGT.buildingDamageMultiplier = 0.1;
RGT.lifetime = 54;
RGT.speed = 8;
RGT.reloadMultiplier = 1.2;
RGT.pierceCap = 2;
RGT.pierceBuilding = true;
RGT.shrinkY = 0;
RGT.backColor = Pal.lancerLaser;
RGT.frontColor = Pal.lancerLaser;
RGT.despawnEffect = RGT.hitEffect = lib.newEffect(20,(e) => {
    Draw.color(Pal.lancerLaser);
    Lines.stroke(e.fout() * 3);
    Lines.circle(e.x, e.y, e.fin() * 60);
    Lines.stroke(e.fout() * 1.75);
    Lines.circle(e.x, e.y, e.fin() * 45);
    Draw.color(Pal.lancerLaser);
    Fill.circle(e.x, e.y, e.fout() * 20);
    Draw.color(Pal.lancerLaser);
    Fill.circle(e.x, e.y, e.fout() * 14);
});
RGT.chargeEffect = new MultiEffect(bigShotEffT, chargeEffectT, chargeBeginEffectT);

const RGS = extend(BasicBulletType, {});
RGS.sprite = "extra-utilities-gs";
RGS.width = 40;
RGS.height = 20;
RGS.damage = 490;
RGS.splashDamageRadius = 36;
RGS.splashDamage = 460;
RGS.buildingDamageMultiplier = 0.1;
RGS.lifetime = 54;
RGS.speed = 8;
RGS.pierceCap = 2;
RGS.pierceBuilding = true;
RGS.shrinkY = 0;
RGS.backColor = Pal.surge;
RGS.frontColor = Pal.surge;
RGS.status = StatusEffects.shocked;
RGS.despawnEffect = RGS.hitEffect = lib.newEffect(20,(e) => {
    Draw.color(Pal.surge);
    Lines.stroke(e.fout() * 3);
    Lines.circle(e.x, e.y, e.fin() * 60);
    Lines.stroke(e.fout() * 1.75);
    Lines.circle(e.x, e.y, e.fin() * 45);
    Draw.color(Pal.surge);
    Fill.circle(e.x, e.y, e.fout() * 20);
    Draw.color(Pal.surge);
    Fill.circle(e.x, e.y, e.fout() * 14);
});
RGS.chargeEffect = new MultiEffect(bigShotEffS, chargeEffect, chargeBeginEffect);
RGS.intervalBullet = (() => {
    const l = new LightningBulletType();
    l.damage = 15;
    l.ammoMultiplier = 1;
    l.lightningColor = Pal.surge;
    l.lightningLength = 3;
    l.lightningLengthRand = 4;
    l.buildingDamageMultiplier = 0.1;
    return l;
})();
RGS.bulletInterval = 5;
RGS.intervalBullets = 6;

const RG = extend(ItemTurret, 'RG', {});

RG.shootSound = Sounds.shootLancer;
RG.chargeSound = chargeSound;
RG.reload = 60 * 3.5;
RG.shoot.shots = 5;
RG.shoot.shotDelay = 8;
RG.shoot.firstShotDelay = chargeTime;
RG.moveWhileCharging = false;
RG.accurateDelay = false;
RG.inaccuracy = 0;
RG.size = 5;
RG.ammoUseEffect = Fx.none;
RG.recoil = 6;
RG.recoilTime = 30;
RG.shake = 5;
RG.ammoPerShot = 5;
RG.maxAmmo = 20;
RG.range = 8 * 60;
RG.liquidCapacity = 20;
RG.coolant = RG.consumeCoolant(1.2);
RG.coolantMultiplier = 1;
RG.health = 180 * 5 * 5;
//RG.canOverdrive = false;
RG.shootEffect = Fx.none;
RG.smokeEffect = Fx.none;
RG.drawer = new DrawTurret("reinforced-");
RG.squareSprite = false;
RG.ammo(
    Items.titanium, RGT,
    Items.surgeAlloy, RGS
);
RG.requirements = ItemStack.with(
    //Items.copper, 500,
    Items.lead, 900,
    Items.silicon, 800,
    Items.graphite, 600,
    Items.titanium, 400,
    Items.plastanium, 320,
    items.lightninAlloy, 270
);
RG.consumePower(15);
RG.buildVisibility = BuildVisibility.shown;
RG.category = Category.turret;

exports.RG = RG;
