//
const lib = require("blib");
const T2duo = extendContent(ItemTurret, "T2-duo", {
    canPlaceOn(tile, team){
        return tile.block() == Blocks.duo && tile.team() == team && lib.placeRule(this);
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
lib.setBuildingSimple(T2duo, ItemTurret.ItemTurretBuild, {});
T2duo.spread = 2;
T2duo.shots = 3;
T2duo.alternate = true;
T2duo.reloadTime = 13;
T2duo.restitution = 0.03;
T2duo.range = 120;
T2duo.shootCone = 15;
T2duo.ammoUseEffect = Fx.casing1;
T2duo.health = 320;
T2duo.inaccuracy = 2;
T2duo.rotateSpeed = 10;
T2duo.ammo(
    Items.copper, Bullets.standardCopper,
            Items.graphite, Bullets.standardDense,
            Items.pyratite, Bullets.standardIncendiary,
            Items.silicon, Bullets.standardHoming
);
T2duo.requirements = ItemStack.with(
    Items.copper, 20
);
T2duo.buildVisibility = BuildVisibility.shown;
T2duo.category = Category.turret;

T2duo.replaceable = false;

exports.T2duo = T2duo;