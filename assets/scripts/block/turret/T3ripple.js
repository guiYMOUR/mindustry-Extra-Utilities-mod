//
const lib = require("blib");
const bullets = require("other/bullets");
const shots = 3;

const T3rip = extend(ItemTurret, "T3-ripple", {
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
        var ent = this;
        var
            bulletX = ent.x + Angles.trnsx(ent.rotation - 90, block.shootX, block.shootY),
            bulletY = ent.y + Angles.trnsy(ent.rotation - 90, block.shootX, block.shootY);
        var lif = Mathf.clamp(Mathf.dst(bulletX, bulletY, ent.targetPos.x, ent.targetPos.y) / type.range, block.minRange / type.range, block.range / type.range)
        for(var i = 0; i < 4; i++){
            type.create(ent, ent.team, bulletX, bulletY, ent.rotation + Mathf.range(block.inaccuracy), 1 + Mathf.range(block.velocityRnd), lif);
        }
    },
    draw(){
        this.super$draw();
        var ent = this;
        var i = ent.totalShots % shots;
        if(ent.heat <= 0.00001) return;
        Draw.color(block.heatColor, ent.heat);
        Draw.blend(Blending.additive);
        Draw.rect(Core.atlas.find("btm-T3-ripple-heat-" + i), ent.x + ent.recoilOffset.x, ent.y + ent.recoilOffset.y, ent.rotation - 90);
        Draw.blend();
        Draw.color();
    },
});
T3rip.reload = 20;
T3rip.shoot = lib.moreShootAlternate(6, shots);
T3rip.targetAir = false;
T3rip.inaccuracy = 10;
//T3rip.xRand = 4;
T3rip.size = 4;
T3rip.ammoEjectBack = 5;
T3rip.ammoUseEffect = Fx.casing3Double;
T3rip.ammoPerShot = 2;
T3rip.cooldown = 0.03;
T3rip.velocityRad = 0.2;
//T3rip.restitution = 0.02;
T3rip.recoilAmount = 6;
T3rip.shake = 2;
T3rip.range = 370;
T3rip.minRange = 50;
T3rip.health = 250 * 3 * 3;
T3rip.shootSound = Sounds.artillery;
lib.Coolant(T3rip, 0.4, 0.3);
T3rip.ammo(
    Items.graphite, lib.toBullet(Blocks.ripple, Items.graphite),
    Items.silicon, lib.toBullet(Blocks.ripple, Items.silicon),
    Items.pyratite, lib.toBullet(Blocks.ripple, Items.pyratite),
    Items.blastCompound, lib.toBullet(Blocks.ripple, Items.blastCompound),
    Items.plastanium, lib.toBullet(Blocks.ripple, Items.plastanium),
    Items.surgeAlloy, bullets.artillerySurge
);
T3rip.requirements = ItemStack.with(
    Items.copper, 300,
    Items.graphite, 220,
    Items.titanium, 155,
    Items.thorium, 100,
    Items.silicon, 85,
    Items.surgeAlloy, 80
);
T3rip.buildVisibility = BuildVisibility.shown;
T3rip.category = Category.turret;

exports.T3rip = T3rip;