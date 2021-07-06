//
const lib = require("blib");
const { T2duo } = require('block/turret/T2duo');
const T3duo = extendContent(ItemTurret, "T3-duo", {
    canPlaceOn(tile, team){
        return tile.block() == T2duo && tile.team() == team && lib.placeRule(this);
    },
    canReplace(other){
        if(other.alwaysReplace) return true;
        return (other != this || this.rotate) && this.group != BlockGroup.none && other.group == this.group &&
            (this.size == other.size || (this.size >= other.size && ((this.subclass != null && this.subclass == other.subclass) || this.group.anyReplace)));
    },
    drawPlace(x, y, rotation, valid){
        if(Vars.world.tile(x, y) == null) return;
        this.drawPlaceText(Core.bundle.get(
            this.canReplace(Vars.world.tile(x, y).block()) && this.canPlaceOn(Vars.world.tile(x, y), Vars.player.team()) ?
            "bar.btm-can" :
            lib.placeRule(this) ? "bar.btm-cannot-block" : "bar.btm-cannot-item"
        ), x, y, valid);
    },
});
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
T3duo.range = 130;
T3duo.shootCone = 15;
T3duo.ammoUseEffect = Fx.casing1;
T3duo.health = 400;
T3duo.inaccuracy = 2;
T3duo.rotateSpeed = 10;
T3duo.ammo(
    Items.copper, Bullets.standardCopper,
    Items.graphite, Bullets.standardDense,
    Items.pyratite, Bullets.standardIncendiary,
    Items.silicon, Bullets.standardHoming
);
T3duo.requirements = ItemStack.with(
    Items.copper, 15,
    Items.graphite, 25
);
T3duo.buildVisibility = BuildVisibility.shown;
T3duo.category = Category.turret;

T3duo.replaceable = false;

exports.T3duo = T3duo;