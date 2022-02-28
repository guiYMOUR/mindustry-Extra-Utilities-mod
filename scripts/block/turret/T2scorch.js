/*
* @author <guiY>
* @readme <The bullet are in "other/bullets">
*/
const bullets = require("other/bullets");
const items = require("game/items");

const T2scorch = new ItemTurret("T2scorch");
T2scorch.ammo(
    Items.coal, bullets.flame({
        particleNumber : 60
    }),
    Items.pyratite, bullets.flame({
        damage : 72,
        flameCone : 16,
        colorBegin : Pal.lightPyraFlame,
        colorTo : Pal.darkPyraFlame,
        ammoMultiplier : 6,
        statusDuration : 60 * 4
    })
);
Object.assign(T2scorch, {
    size : 2,
    recoilAmount : 0,
    reloadTime : 8,
    coolantMultiplier : 1.5,
    range : 88,
    shootCone : 50,
    targetAir : false,
    ammoUseEffect : Fx.none,
    health : 350 * 2 * 2,
    shootSound : Sounds.flame,
});
T2scorch.requirements = ItemStack.with(
    Items.copper, 80,
    Items.graphite, 50,
    Items.titanium, 50,
    items.crispSteel, 30
);
T2scorch.buildVisibility = BuildVisibility.shown;
T2scorch.category = Category.turret;

exports.T2scorch = T2scorch;
