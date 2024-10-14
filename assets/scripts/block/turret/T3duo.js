//
const lib = require("blib");
const hardMod = lib.hardMod;
const T3duo = extend(ItemTurret, "T3-duo", {});
lib.setBuildingSimple(T3duo, ItemTurret.ItemTurretBuild, {
    shoot(type){
        this.super$shoot(type);
        if(hardMod){
            if(Mathf.chance(0.5)){
                lib.toBullet(Blocks.duo, Items.graphite).create(this, this.team, this.x, this.y, this.rotation, 1 ,1);
                Sounds.pew.at(this.x, this.y);
            }
        } else {
            if(Mathf.chance(0.1)){
                lib.toBullet(Blocks.spectre, Items.thorium).create(this, this.team, this.x, this.y, this.rotation, 1 ,1);
                Sounds.shootBig.at(this.x, this.y);
            }
        }
    }
});
T3duo.shoot = lib.moreShootAlternate(3.6, 2);
T3duo.reload = hardMod ? 24 : 12;
T3duo.restitution = 0.03;
T3duo.range = 140;
T3duo.shootCone = 15;
T3duo.ammoUseEffect = Fx.casing1;
T3duo.health = hardMod ? 720 : 400;
T3duo.inaccuracy = 2;
T3duo.rotateSpeed = 10;
T3duo.limitRange();
T3duo.ammoTypes = Blocks.duo.ammoTypes;
T3duo.coolant = T3duo.consume(new ConsumeCoolant(0.1));
T3duo.requirements = ItemStack.with(
    Items.copper, 85,
    Items.graphite, 35
);
T3duo.buildVisibility = BuildVisibility.shown;
T3duo.category = Category.turret;

exports.T3duo = T3duo;