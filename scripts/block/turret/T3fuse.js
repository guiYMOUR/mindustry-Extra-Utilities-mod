//
const lib = require("blib");
//钛
const fuse1 = extend(ShrapnelBulletType, {});
fuse1.length = 120;
fuse1.damage = 72;
fuse1.width = 17;
fuse1.reloadMultiplier = 1.3;
fuse1.ammoMultiplier = 2;
fuse1.shootEffect = Fx.lightningShoot;
fuse1.smokeEffect = Fx.lightningShoot;
//钍
const fuse2 = extend(ShrapnelBulletType, {});
fuse2.length = 120;
fuse2.damage = 113;
fuse2.toColor = Color.valueOf("ffc3ff");
fuse2.ammoMultiplier = 3;
fuse2.shootEffect = Fx.thoriumShoot;
fuse2.smokeEffect = Fx.thoriumShoot;

const T3fuse = extendContent(ItemTurret, "T3-fuse", {});
lib.setBuildingSimple(T3fuse, ItemTurret.ItemTurretBuild, {});
T3fuse.reloadTime = 35;
T3fuse.shootShake = 4;
T3fuse.range = 110;
T3fuse.recoilAmount = 5;
T3fuse.shots = 9;
T3fuse.spread = 9;
T3fuse.restitution = 0.1;
T3fuse.shootCone = 30;
T3fuse.size = 3;
T3fuse.health = 280 * 3 * 3;
T3fuse.shootSound = Sounds.shotgun;


T3fuse.ammo(
    Items.titanium, fuse1,
    Items.thorium, fuse2
);
T3fuse.requirements = ItemStack.with(
    Items.copper, 350,
    Items.graphite, 320,
    Items.titanium, 270,
    Items.thorium, 150,
    Items.silicon, 150,
    Items.surgeAlloy, 140
);
T3fuse.buildVisibility = BuildVisibility.shown;
T3fuse.category = Category.turret;

exports.T3fuse = T3fuse;