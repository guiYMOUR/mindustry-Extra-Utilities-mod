const lib = require("blib");
const T2scatter = extendContent(ItemTurret, "T2-scatter", {
   canPlaceOn(tile, team){
        return tile.block() == Blocks.scatter && tile.team() == team && lib.placeRule(this);
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
T2scatter.size = 2;
T2scatter.xRand = 3;
T2scatter.shots = 3;
T2scatter.burstSpacing = 3;
T2scatter.reloadTime = 9;
T2scatter.recoilAmount = 2;
T2scatter.range = 220;
T2scatter.shootCone = 35;
T2scatter.health = 210 * 2 * 2;
T2scatter.inaccuracy = 10;
T2scatter.rotateSpeed = 15;
T2scatter.targetGround = false;
T2scatter.shootSound = Sounds.shootSnap;
T2scatter.ammo(
    Items.scrap, Bullets.flakScrap,
    Items.lead, Bullets.flakLead,
    Items.metaglass, Bullets.flakGlass
);
T2scatter.limitRange(2);
T2scatter.requirements = ItemStack.with(
    Items.copper, 5,
    Items.lead, 25,
    Items.graphite, 25
);
T2scatter.buildVisibility = BuildVisibility.shown;
T2scatter.category = Category.turret;

T2scatter.replaceable = false;

exports.T2scatter = T2scatter;