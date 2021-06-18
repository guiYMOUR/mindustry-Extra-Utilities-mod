const mh = extend(BasicBulletType,{});
mh.speed = 5;
mh.damage = 18;
mh.width = 10;
mh.height = 16;
mh.shrinkY = 0;
mh.drag = -0.01;
mh.homingPower = 0.08;
mh.reloadMultiplier = 1.2;
const mc = extend(BasicBulletType,{});
mc.speed = 5;
mc.damage = 18;
mc.width = 10;
mc.height = 16;
mc.shrinkY = 0;
mc.drag = -0.01;
const mt = extend(BasicBulletType,{});
mt.speed = 5;
mt.damage = 38;
mt.width = 15;
mt.height = 22;
mt.shrinkY = 0;
mt.drag = -0.01;
const mg = extend(BasicBulletType,{});
mg.speed = 5;
mg.damage = 27;
mg.width = 13;
mg.height = 20;
mg.shrinkY = 0;
mg.drag = -0.01;
const mp = extend(BasicBulletType,{});
mp.speed = 5;
mp.damage = 19;
mp.width = 10;
mp.height = 16;
mp.shrinkY = 0;
mp.drag = -0.01;
mp.frontColor = Pal.lightishOrange;
mp.backColor = Pal.lightOrange;
mp.status = StatusEffects.burning;

const shotgun = extendContent(ItemTurret, "shotgun", {});
shotgun.reloadTime = 72;
shotgun.shots = 8;
//shotgun.burstSpacing = 4;
shotgun.inaccuracy = 9;
shotgun.range = 190;
//shotgun.xRand = 4;
shotgun.size = 2;
shotgun.shootSound = Sounds.shootBig;
shotgun.health = 200 * 2 * 2;
shotgun.ammoPerShot = 2;
shotgun.ammo(
    Items.copper, mc,
    Items.graphite, mg,
    Items.silicon, mh,
    Items.pyratite, mp,
    Items.thorium, mt
);
shotgun.requirements = ItemStack.with(
    Items.copper, 60,
    Items.lead, 65,
    Items.graphite, 50
);
shotgun.buildVisibility = BuildVisibility.shown;
shotgun.category = Category.turret;

exports.shotgun = shotgun;