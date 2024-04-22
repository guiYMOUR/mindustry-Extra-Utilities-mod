//引用部分，类似import，对应的是exports导出
const lib = require("blib");
const items = require("game/items");
const Prism = lib.getClass("ExtraUtilities.worlds.blocks.turret.Prism");
const DrawRainbow = lib.getClass("ExtraUtilities.worlds.drawer.DrawRainbow");
const prism = new Prism("prism");

const hardMod = Core.settings.getBool("eu-hard-mode");

prism.shootEffect = Fx.shootBigSmoke2;
prism.shootCone = 40;
prism.recoil = 4;
prism.shootX = 1;
prism.recoilPow = 0.9;
prism.recoilTime = 60;
prism.size = 5;
prism.shake = 2;
prism.range = 36 * 8;
prism.consumePower(40);
prism.drawer = new DrawMulti(new DrawTurret("reinforced-"), new DrawRainbow(2, 8));

prism.rainbowDamage = (300 - (hardMod ? 96 : 0))/12;
prism.health = 220 * 5 * 5;
prism.coolantMultiplier = 1;
prism.squareSprite = false;
prism.requirements = ItemStack.with(
    Items.lead, 800,
    Items.silicon, 650,
    Items.metaglass, 650,
    Items.thorium, 500,
    items.lightninAlloy, 350
);
prism.buildVisibility = BuildVisibility.shown;
prism.category = Category.turret;
exports.prism = prism;