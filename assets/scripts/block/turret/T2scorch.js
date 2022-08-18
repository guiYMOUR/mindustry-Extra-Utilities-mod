/*
* @author <guiY>
* @readme <The bullet are in "other/bullets">
*/
const lib = require("blib");
//const bullets = require("other/bullets");
const items = require("game/items");
const FlameBulletType = lib.getClass("ExtraUtilities.worlds.entity.bullet.FlameBulletType");

const T2scorch = new ItemTurret("T2scorch");
T2scorch.ammo(
    Items.coal, new FlameBulletType(Pal.lightFlame, Pal.darkFlame, Color.gray, T2scorch.range+8, 14, 60, 22),
    Items.pyratite, (() => {
        const b = new FlameBulletType(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, T2scorch.range+8, 20, 72, 22);
        b.damage = 41;
        b.damageBoost = 5;
        b.statusDuration = 60 * 6;
        return b;
    })()
);
Object.assign(T2scorch, {
    size : 2,
    recoil : 0,
    reload : 8,
    //coolantMultiplier : 1.5,
    range : 88,
    shootCone : 50,
    targetAir : false,
    ammoUseEffect : Fx.none,
    health : 400 * 2 * 2,
    shootSound : Sounds.flame,
});
T2scorch.coolant = T2scorch.consumeCoolant(0.2);
T2scorch.requirements = ItemStack.with(
    Items.copper, 60,
    Items.graphite, 40,
    items.crispSteel, 20
);
T2scorch.buildVisibility = BuildVisibility.shown;
T2scorch.category = Category.turret;

exports.T2scorch = T2scorch;