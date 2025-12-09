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
sakura.shootSound = Sounds.shootSpectre;
sakura.shootEffect = Fx.thoriumShoot;
sakura.smokeEffect = Fx.thoriumShoot;
lib.Coolant(sakura, 0.4, 2.5);
sakura.requirements = ItemStack.with(
    Items.lead, 200,
    Items.silicon, 200,
    Items.titanium, 150,
    Items.thorium, 120
);
sakura.buildVisibility = BuildVisibility.shown;
sakura.category = Category.turret;
exports.sakura = sakura;
