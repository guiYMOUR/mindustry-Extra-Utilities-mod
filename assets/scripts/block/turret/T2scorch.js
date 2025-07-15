/*
* @author <guiY>
* @readme <The bullet are in "other/bullets">
*/
const lib = require("blib");
//const bullets = require("other/bullets");
const items = require("game/items");
const FlameBulletType = lib.getClass("ExtraUtilities.worlds.entity.bullet.FlameBulletType");
const bullets = lib.getClass("ExtraUtilities.content.EUBulletTypes");
const value = lib.getClass("ExtraUtilities.worlds.meta.EUStatValues");

//const T2scorch = new ItemTurret("T2scorch");
const T2scorch = extend(ItemTurret, "T2scorch", {
    setStats(){
        this.super$setStats();
        if(this.ammoTypes == null) return;

        this.stats.add(Stat.ammo, value.ammoString(this.ammoTypes));
    },
});
T2scorch.ammo(
    Items.coal, new FlameBulletType(Pal.lightFlame, Pal.darkFlame, Color.gray, T2scorch.range + 8, 14, 60, 22),
    Items.pyratite, (() => {
        const b = new FlameBulletType(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, T2scorch.range + 8, 20, 72, 22);
        b.damage = 99;
        b.statusDuration = 60 * 6;
        return b;
    })(),
    Items.sporePod, (() => {
        const b = new FlameBulletType(Pal.lightFlame, Pal.sapBulletBack, Color.gray, T2scorch.range + 8, 25, 60, 25);
        b.damage = 35;
        b.status = StatusEffects.sapped;
        return b;
    })(),
    Items.blastCompound, bullets.expFlame(T2scorch.range, 52),
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
lib.Coolant(T2scorch, 0.2, 2);
T2scorch.requirements = ItemStack.with(
    Items.copper, 70,
    Items.graphite, 50,
    items.crispSteel, 30,
    Items.titanium, 40
);
T2scorch.buildVisibility = BuildVisibility.shown;
T2scorch.category = Category.turret;

exports.T2scorch = T2scorch;