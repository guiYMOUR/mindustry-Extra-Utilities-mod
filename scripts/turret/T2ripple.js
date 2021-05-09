//
const lib = require("blib");
//const tr = new Vec2();
const T2rip = extendContent(ItemTurret, "T2-ripple", {
    setStats(){
		this.super$setStats();
		
		this.stats.remove(Stat.reload);
		this.stats.add(Stat.reload, 7.93, StatUnit.none);
	},
});
lib.setBuildingSimple(T2rip, ItemTurret.ItemTurretBuild, {
    //_tr: new Vec2(),
    _shotCounter: 0,
    shoot(type){
        this.super$shoot(type);
        
        var i = (this._shotCounter % this.block.shots) - (this.block.shots - 1)/2;

        //this._tr.trns(this.rotation - 90, this.block.spread * i + Mathf.range(0), this.block.size * 8 / 2);
        for(var i = 0; i < 3; i++){
            this.bullet(type, this.rotation + Mathf.range(this.block.inaccuracy))
        }
        this._shotCounter ++;
    }
});
T2rip.reloadTime = 30;
T2rip.shots = 2;
T2rip.targetAir = false;
T2rip.spread = 8;
T2rip.alternate = true;
T2rip.inaccuracy = 10;
//T2rip.xRand = 4;
T2rip.size = 3;
T2rip.ammoEjectBack = 5;
T2rip.ammoUseEffect = Fx.casing3Double;
T2rip.ammoPerShot = 2;
T2rip.cooldown = 0.03;
T2rip.velocityInaccuracy = 0.2;
T2rip.restitution = 0.02;
T2rip.recoilAmount = 6;
T2rip.shootShake = 2;
T2rip.range = 320;
T2rip.minRange = 50;
T2rip.health = 180 * 3 * 3;
T2rip.shootSound = Sounds.artillery;

T2rip.ammo(
    Items.graphite, Bullets.artilleryDense,
            Items.silicon, Bullets.artilleryHoming,
            Items.pyratite, Bullets.artilleryIncendiary,
            Items.blastCompound, Bullets.artilleryExplosive,
            Items.plastanium, Bullets.artilleryPlastic
);
T2rip.requirements = ItemStack.with(
    Items.copper, 200,
    Items.graphite, 150,
    Items.titanium, 70,
    Items.silicon, 30
);
T2rip.buildVisibility = BuildVisibility.shown;
T2rip.category = Category.turret;

exports.T2rip = T2rip;