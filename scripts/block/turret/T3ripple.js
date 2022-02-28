//
const lib = require("blib");
const bullets = require("other/bullets");

const T3rip = extendContent(ItemTurret, "T3-ripple", {
    setStats(){
        this.super$setStats();
        
        this.stats.remove(Stat.reload);
        this.stats.add(Stat.reload, 14.25, StatUnit.none);
    },
});
const block = T3rip;
lib.setBuildingSimple(T3rip, ItemTurret.ItemTurretBuild, {
    shoot(type){
        this.super$shoot(type);
        for(var i = 0; i < 4; i++){
            this.bullet(type, this.rotation + Mathf.range(block.inaccuracy))
        }
    },
    draw(){
        this.super$draw();
        var i = this.shotCounter % block.shots;
        if(this.heat <= 0.00001) return;
        Draw.color(block.heatColor, this.heat);
        Draw.blend(Blending.additive);
        Draw.rect(Core.atlas.find("btm-T3-ripple-heat-" + i), this.x + block.tr2.x, this.y + block.tr2.y, this.rotation - 90);
        Draw.blend();
        Draw.color();
    },
});
T3rip.reloadTime = 20;
T3rip.shots = 3;
T3rip.targetAir = false;
T3rip.spread = 6;
T3rip.alternate = true;
T3rip.inaccuracy = 10;
//T3rip.xRand = 4;
T3rip.size = 4;
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
            Items.plastanium, Bullets.artilleryPlastic,
            Items.surgeAlloy, bullets.artillerySurge
);
T3rip.requirements = ItemStack.with(
    Items.copper, 800,
    Items.graphite, 400,
    Items.titanium, 250,
    Items.thorium, 150,
    Items.silicon, 150,
    Items.surgeAlloy, 80
);
T3rip.buildVisibility = BuildVisibility.shown;
T3rip.category = Category.turret;

exports.T3rip = T3rip;
