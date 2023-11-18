const lib = require("blib");
const bullets = lib.getClass("ExtraUtilities.content.EUBulletTypes");

const sakura = extend(ItemTurret, 'sakura', {});

sakura.ammo(
    Items.thorium, bullets.suk
);

sakura.reload = 90;
sakura.size = 3;
sakura.rotateSpeed = 5;
sakura.inaccuracy = 0;
sakura.range = 240;
sakura.shootSound = Sounds.shootBig;
sakura.shootEffect = Fx.thoriumShoot;
sakura.smokeEffect = Fx.thoriumShoot;
lib.Coolant(sakura, 0.3);
sakura.requirements = ItemStack.with(
    Items.lead, 180,
    Items.silicon, 120,
    Items.titanium, 100,
    Items.thorium, 85
);
sakura.buildVisibility = BuildVisibility.shown;
sakura.category = Category.turret;
exports.sakura = sakura;
