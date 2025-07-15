const lib = require("blib");

const mh = extend(BasicBulletType,{});
mh.lifetime = 33;
mh.speed = 5;
mh.damage = 35;
mh.width = 10;
mh.height = 16;
mh.shrinkY = 0;
mh.drag = -0.01;
mh.homingPower = 0.08;
mh.reloadMultiplier = 1.2;
mh.ammoMultiplier = 4;
const mc = extend(BasicBulletType,{});
mc.lifetime = 33;
mc.speed = 5;
mc.damage = 19;
mc.width = 10;
mc.height = 16;
mc.shrinkY = 0;
mc.drag = -0.01;
const mt = extend(BasicBulletType,{});
mt.lifetime = 33;
mt.speed = 5;
mt.damage = 37;
mt.width = 15;
mt.height = 22;
mt.shrinkY = 0;
mt.drag = -0.01;
const mg = extend(BasicBulletType,{});
mg.lifetime = 33;
mg.speed = 5;
mg.damage = 25;
mg.width = 13;
mg.height = 20;
mg.shrinkY = 0;
mg.drag = -0.01;
const mp = extend(BasicBulletType,{});
mp.lifetime = 33;
mp.speed = 5;
mp.damage = 22;
mp.width = 10;
mp.height = 16;
mp.shrinkY = 0;
mp.drag = -0.01;
mp.frontColor = Pal.lightishOrange;
mp.backColor = Pal.lightOrange;
mp.status = StatusEffects.burning;

const shotgun = extend(ItemTurret, "shotgun", {});
shotgun.reload = 72;
shotgun.shoot.shots = 7;
//shotgun.burstSpacing = 4;
shotgun.inaccuracy = 11;
shotgun.range = 190;
//shotgun.xRand = 4;
shotgun.size = 2;
shotgun.shootSound = Sounds.shootBig;
shotgun.health = 200 * 2 * 2;
shotgun.ammoPerShot = 4;
lib.Coolant(shotgun, 0.2);
// shotgun.limitRange();
shotgun.ammo(
    Items.copper, mc,
    Items.graphite, mg,
    Items.phaseFabric, mh,
    Items.pyratite, mp,
    Items.thorium, mt
);
shotgun.requirements = ItemStack.with(
    Items.copper, 60,
    Items.lead, 70,
    Items.graphite, 60,
    Items.titanium, 40,
);
shotgun.buildVisibility = BuildVisibility.shown;
shotgun.category = Category.turret;

exports.shotgun = shotgun;