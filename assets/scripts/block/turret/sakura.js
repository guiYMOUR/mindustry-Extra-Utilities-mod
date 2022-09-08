const lib = require("blib");

const c1 = Pal.thoriumPink;
const disappear = lib.Fx.gone(c1);

const rankB = extend(BasicBulletType, {
    update(b){
        b.vel.trns(b.rotation(), this.speed * Math.max(0, b.fout() - 0.5));
    },
});
rankB.speed = 2;
rankB.lifetime = 216;
rankB.damage = 38;
rankB.buildingDamageMultiplier = 0.5;
rankB.width = 10;
rankB.height = 7;
rankB.shrinkY = 0;
rankB.despawnEffect = disappear;
rankB.backColor = c1;
rankB.frontColor = c1;

const sakura = extend(ItemTurret, 'sakura', {});

sakura.ammo(
    Items.thorium, (() => {
        const lifetime = 120;
        const fragBullets = 5;
        const rankS = new JavaAdapter(BasicBulletType, {
            update(b){
                if(b.timer.get(lifetime/4)){
                    for(var i = 0; i < this.fragBullets; i++){
                        rankB.create(b, b.x, b.y, b.rotation() + (360/fragBullets) * (Math.floor(fragBullets/2) - i), 1, 1);
                    }
                }
            },
            despawned(b){
                for(var i = 0; i < fragBullets; i++){
                    rankB.create(b, b.x, b.y, b.rotation() + (360/fragBullets) * (Math.floor(fragBullets/2) - i), 1, 1);
                }
            },
        });
        rankS.speed = 2;
        rankS.lifetime = lifetime;
        rankS.damage = 61;
        rankS.buildingDamageMultiplier = 0.5;
        rankS.width = 32;
        rankS.height = 18;
        rankS.shrinkY = 0;
        rankS.pierce = true;
        rankS.pierceBuilding = true;
        rankS.backColor = c1;
        rankS.frontColor = c1;
        rankS.fragBullets = fragBullets;
        return rankS;
    })()
);

sakura.reload = 90;
sakura.size = 3;
sakura.rotateSpeed = 5;
sakura.inaccuracy = 0;
sakura.range = 240;
// sakura.shootCone = 80;
sakura.shootSound = Sounds.shootBig;
sakura.shootEffect = Fx.thoriumShoot;
sakura.smokeEffect = Fx.thoriumShoot;
lib.Coolant(sakura, 0.3);
sakura.requirements = ItemStack.with(
    Items.lead, 180,
    Items.silicon, 120,
    Items.titanium, 100,
    Items.thorium, 85
);
sakura.buildVisibility = BuildVisibility.shown;
sakura.category = Category.turret;
exports.sakura = sakura;
