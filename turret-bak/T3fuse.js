//
const lib = require("blib");
const { T2fuse } = require('block/turret/T2fuse');
//钛
const fuse1 = extend(ShrapnelBulletType, {});
fuse1.length = 120;
fuse1.damage = 72;
fuse1.width = 17;
fuse1.reloadMultiplier = 1.3;
fuse1.ammoMultiplier = 2;
fuse1.shootEffect = Fx.lightningShoot;
fuse1.smokeEffect = Fx.lightningShoot;
//钍
const fuse2 = extend(ShrapnelBulletType, {});
fuse2.length = 120;
fuse2.damage = 113;
fuse2.toColor = Color.valueOf("ffc3ff");
fuse2.ammoMultiplier = 3;
fuse2.shootEffect = Fx.thoriumShoot;
fuse2.smokeEffect = Fx.thoriumShoot;

const T3fuse = extendContent(ItemTurret, "T3-fuse", {
    canPlaceOn(tile, team){
        return tile.block() == T2fuse && tile.team() == team && lib.placeRule(this);
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
lib.setBuildingSimple(T3fuse, ItemTurret.ItemTurretBuild, {});
T3fuse.reloadTime = 35;
T3fuse.shootShake = 4;
T3fuse.range = 110;
T3fuse.recoilAmount = 5;
T3fuse.shots = 9;
T3fuse.spread = 9;
T3fuse.restitution = 0.1;
T3fuse.shootCone = 30;
T3fuse.size = 3;
T3fuse.health = 280 * 3 * 3;
T3fuse.shootSound = Sounds.shotgun;


T3fuse.ammo(
    Items.titanium, fuse1,
    Items.thorium, fuse2
);
T3fuse.requirements = ItemStack.with(
    Items.copper, 90,
    Items.graphite, 50,
    Items.titanium, 40,
    Items.thorium, 30,
    Items.silicon, 150,
    Items.surgeAlloy, 100
);
T3fuse.buildVisibility = BuildVisibility.shown;
T3fuse.category = Category.turret;

T3fuse.replaceable = false;

exports.T3fuse = T3fuse;