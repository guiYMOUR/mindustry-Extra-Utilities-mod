//
const lib = require("blib");
//const tr = new Vec2();
const { T2rip } = require("block/turret/T2ripple");

const T3rip = extendContent(ItemTurret, "T3-ripple", {
    setStats(){
        this.super$setStats();
        
        this.stats.remove(Stat.reload);
        this.stats.add(Stat.reload, 14.25, StatUnit.none);
    },
    canPlaceOn(tile, team){
        return tile.block() == T2rip && tile.team() == team && lib.placeRule(this);
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
lib.setBuildingSimple(T3rip, ItemTurret.ItemTurretBuild, {
    _shotCounter: 0,
    shoot(type){
        this.super$shoot(type);
        
        var i = (this._shotCounter % this.block.shots) - (this.block.shots - 1)/2;

        //tr.trns(this.rotation - 90, this.block.spread * i + Mathf.range(0), this.block.size * 8 / 2);
        for(var i = 0; i < 4; i++){
            this.bullet(type, this.rotation + Mathf.range(this.block.inaccuracy))
        }
        this._shotCounter ++;
    },
});
T3rip.reloadTime = 20;
T3rip.shots = 3;
T3rip.targetAir = false;
T3rip.spread = 4;
T3rip.alternate = true;
T3rip.inaccuracy = 10;
//T3rip.xRand = 4;
T3rip.size = 3;
T3rip.ammoEjectBack = 5;
T3rip.ammoUseEffect = Fx.casing3Double;
T3rip.ammoPerShot = 2;
T3rip.cooldown = 0.03;
T3rip.velocityInaccuracy = 0.2;
T3rip.restitution = 0.02;
T3rip.recoilAmount = 6;
T3rip.shootShake = 2;
T3rip.range = 370;
T3rip.minRange = 50;
T3rip.health = 250 * 3 * 3;
T3rip.shootSound = Sounds.artillery;

T3rip.ammo(
    Items.graphite, Bullets.artilleryDense,
            Items.silicon, Bullets.artilleryHoming,
            Items.pyratite, Bullets.artilleryIncendiary,
            Items.blastCompound, Bullets.artilleryExplosive,
            Items.plastanium, Bullets.artilleryPlastic
);
T3rip.requirements = ItemStack.with(
    Items.copper, 100,
    Items.graphite, 50,
    Items.titanium, 55,
    Items.thorium, 100,
    Items.silicon, 20,
    Items.surgeAlloy, 65
);
T3rip.buildVisibility = BuildVisibility.shown;
T3rip.category = Category.turret;

T3rip.replaceable = false;

exports.T3rip = T3rip;