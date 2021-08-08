/*
* @author <guiY>
* @readme <The bullet are in "other/bullets">
*/
const bullets = require("other/bullets");

const T2scorch = new ItemTurret("T2scorch");
T2scorch.ammo(
    Items.coal, bullets.flame({
        particleNumber : 60
    }),
    Items.pyratite, bullets.flame({
        damage : 32,
        flameCone : 16,
        colorBegin : Pal.lightPyraFlame,
        colorTo : Pal.darkPyraFlame,
        ammoMultiplier : 4,
        statusDuration : 60 * 6
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
    health : 400 * 2 * 2,
    shootSound : Sounds.flame,
});
T2scorch.requirements = ItemStack.with(
    Items.copper, 60,
    Items.graphite, 40,
    Items.titanium, 20
);
T2scorch.buildVisibility = BuildVisibility.shown;
T2scorch.category = Category.turret;

exports.T2scorch = T2scorch;