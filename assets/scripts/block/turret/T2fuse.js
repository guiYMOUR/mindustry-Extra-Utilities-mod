//
const lib = require("blib");
const items = require("game/items");
//钛
const fuse1 = extend(ShrapnelBulletType, {});
fuse1.length = 110;
fuse1.damage = 70;
fuse1.width = 17;
fuse1.reloadMultiplier = 1.3;
fuse1.ammoMultiplier = 3;
fuse1.shootEffect = Fx.lightningShoot;
fuse1.smokeEffect = Fx.lightningShoot;
//钍
const fuse2 = extend(ShrapnelBulletType, {});
fuse2.length = 110;
fuse2.damage = 110;
fuse2.toColor = Color.valueOf("ffc3ff");
fuse2.ammoMultiplier = 4;
fuse2.shootEffect = Fx.thoriumShoot;
fuse2.smokeEffect = Fx.thoriumShoot;

const T2fuse = extend(ItemTurret, "T2-fuse", {});
lib.setBuildingSimple(T2fuse, ItemTurret.ItemTurretBuild, {});
T2fuse.reload = 35;
T2fuse.shake = 4;
T2fuse.range = 100;
T2fuse.recoilAmount = 5;
//T2fuse.shots = 5;
//T2fuse.spread = 12;
//T2fuse.restitution = 0.1;
T2fuse.shootCone = 30;
T2fuse.size = 3;
T2fuse.health = 240 * 3 * 3;
T2fuse.shootSound = Sounds.shotgun;
T2fuse.envEnabled |= Env.space;
T2fuse.shoot = ShootSpread(5, 12);
lib.Coolant(T2fuse, 0.35);
T2fuse.ammo(
    Items.titanium, fuse1,
    Items.thorium, fuse2
);
T2fuse.requirements = ItemStack.with(
    Items.copper, 260,
    Items.graphite, 235,
    items.crispSteel, 175,
    Items.thorium, 120
);
T2fuse.buildVisibility = BuildVisibility.shown;
T2fuse.category = Category.turret;

exports.T2fuse = T2fuse;