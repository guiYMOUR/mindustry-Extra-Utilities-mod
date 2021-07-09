//
const lib = require("blib");
const T3duo = extendContent(ItemTurret, "T3-duo", {});
lib.setBuildingSimple(T3duo, ItemTurret.ItemTurretBuild, {
    shoot(type){
        this.super$shoot(type);
        if(Mathf.chance(0.1)){
            Bullets.standardThoriumBig.create(this, this.team, this.x, this.y, this.rotation, 1 ,1);
            Sounds.shootBig.at(this.x, this.y);
        }
    }
});
T3duo.spread = 4;
T3duo.shots = 2;
T3duo.alternate = true;
T3duo.reloadTime = 13;
T3duo.restitution = 0.03;
T3duo.range = 140;
T3duo.shootCone = 15;
T3duo.ammoUseEffect = Fx.casing1;
T3duo.health = 400;
T3duo.inaccuracy = 2;
T3duo.rotateSpeed = 10;
T3duo.limitRange();
T3duo.ammo(
    Items.copper, Bullets.standardCopper,
    Items.graphite, Bullets.standardDense,
    Items.pyratite, Bullets.standardIncendiary,
    Items.silicon, Bullets.standardHoming
);
T3duo.requirements = ItemStack.with(
    Items.copper, 65,
    Items.graphite, 25
);
T3duo.buildVisibility = BuildVisibility.shown;
T3duo.category = Category.turret;

exports.T3duo = T3duo;