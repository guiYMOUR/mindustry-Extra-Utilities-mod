//
const lib = require("blib");
const shots = 2;

const T2rip = extend(ItemTurret, "T2-ripple", {
    setStats(){
        this.super$setStats();
        
        this.stats.remove(Stat.reload);
        this.stats.add(Stat.reload, 7.93, StatUnit.none);
    },
});
const block = T2rip;
T2rip.buildType = prov(() => {
    //var shootC = 0;
    return new JavaAdapter(ItemTurret.ItemTurretBuild, {
        shoot(type){
            this.super$shoot(type);
            var ent = this;
            //shootC++;
            var
                bulletX = ent.x + Angles.trnsx(ent.rotation - 90, block.shootX, block.shootY),
                bulletY = ent.y + Angles.trnsy(ent.rotation - 90, block.shootX, block.shootY);
            var lif = Mathf.clamp(Mathf.dst(bulletX, bulletY, ent.targetPos.x, ent.targetPos.y) / type.range, block.minRange / type.range, block.range / type.range)
            for(var i = 0; i < 3; i++){
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
            Draw.rect(Core.atlas.find("btm-T2-ripple-heat-" + i), ent.x + ent.recoilOffset.x, ent.y + ent.recoilOffset.y, ent.rotation - 90);
            Draw.blend();
            Draw.color();
        },
    }, T2rip);
});
T2rip.reload = 30;
T2rip.shoot = lib.moreShootAlternate(8, shots);
T2rip.targetAir = false;
T2rip.inaccuracy = 10;
//T2rip.xRand = 4;
T2rip.size = 3;
T2rip.ammoEjectBack = 5;
T2rip.ammoUseEffect = Fx.casing3Double;
T2rip.ammoPerShot = 2;
T2rip.cooldown = 0.03;
T2rip.velocityRnd = 0.2;
//T2rip.restitution = 0.02;
T2rip.recoilAmount = 6;
T2rip.shake = 2;
T2rip.range = 320;
T2rip.minRange = 50;
T2rip.health = 180 * 3 * 3;
T2rip.shootSound = Sounds.artillery;
lib.Coolant(T2rip, 0.3, 0.3);
T2rip.ammoTypes = Blocks.ripple.ammoTypes;
T2rip.requirements = ItemStack.with(
    Items.copper, 200,
    Items.graphite, 150,
    Items.titanium, 70,
    Items.silicon, 30
);
T2rip.buildVisibility = BuildVisibility.shown;
T2rip.category = Category.turret;

exports.T2rip = T2rip;