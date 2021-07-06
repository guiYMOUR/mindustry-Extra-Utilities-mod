//
const lib = require("blib");
//钛
const fuse1 = extend(ShrapnelBulletType, {});
fuse1.length = 110;
fuse1.damage = 70;
fuse1.width = 17;
fuse1.reloadMultiplier = 1.3;
fuse1.ammoMultiplier = 3;
fuse1.shootEffect = Fx.lightningShoot;
fuse1.smokeEffect = Fx.lightningShoot;
//钍
const fuse2 = extend(ShrapnelBulletType, {});
fuse2.length = 110;
fuse2.damage = 110;
fuse2.toColor = Color.valueOf("ffc3ff");
fuse2.ammoMultiplier = 4;
fuse2.shootEffect = Fx.thoriumShoot;
fuse2.smokeEffect = Fx.thoriumShoot;

const T2fuse = extendContent(ItemTurret, "T2-fuse", {
    canPlaceOn(tile, team){
        return tile.block() == Blocks.fuse && tile.team() == team && lib.placeRule(this);
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
lib.setBuildingSimple(T2fuse, ItemTurret.ItemTurretBuild, {});
T2fuse.reloadTime = 35;
T2fuse.shootShake = 4;
T2fuse.range = 100;
T2fuse.recoilAmount = 5;
T2fuse.shots = 5;
T2fuse.spread = 12;
T2fuse.restitution = 0.1;
T2fuse.shootCone = 30;
T2fuse.size = 3;
T2fuse.health = 240 * 3 * 3;
T2fuse.shootSound = Sounds.shotgun;


T2fuse.ammo(
    Items.titanium, fuse1,
    Items.thorium, fuse2
);
T2fuse.requirements = ItemStack.with(
    Items.copper, 35,
    Items.graphite, 35,
    Items.titanium, 225,
    Items.thorium, 20
);
T2fuse.buildVisibility = BuildVisibility.shown;
T2fuse.category = Category.turret;

T2fuse.replaceable = false;

exports.T2fuse = T2fuse;