//
const lib = require("blib");
//const tr = new Vec2();
const T3rip = extendContent(ItemTurret, "T3-ripple", {
    setStats(){
		this.super$setStats();
		
		this.stats.remove(Stat.reload);
		this.stats.add(Stat.reload, 14.25, StatUnit.none);
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
    Items.copper, 300,
    Items.graphite, 220,
    Items.titanium, 155,
    Items.thorium, 100,
    Items.silicon, 80,
    Items.surgeAlloy, 66
);
T3rip.buildVisibility = BuildVisibility.shown;
T3rip.category = Category.turret;

exports.T3rip = T3rip;