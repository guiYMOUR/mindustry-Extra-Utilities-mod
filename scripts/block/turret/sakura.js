const c1 = Pal.thoriumPink;
const disappear = new Effect(12, cons(e => {
    Draw.color(c1);
    Lines.stroke(2 * e.fout());
    Lines.circle(e.x, e.y, 5 * e.fout());
}));

const rankB = extend(BasicBulletType, {
    update(b){
        b.vel.trns(b.rotation(), this.speed * Math.max(0, b.fout() - 0.5));
    },
});
rankB.speed = 2;
rankB.lifetime = 216;
rankB.damage = 38;
rankB.width = 10;
rankB.height = 7;
rankB.shrinkY = 0;
rankB.despawnEffect = disappear;
rankB.backColor = c1;
rankB.frontColor = c1;

const sakura = extendContent(ItemTurret, 'sakura', {});

sakura.ammo(
    Items.thorium, (() => {
        const rankS = new JavaAdapter(BasicBulletType, {
            update(b){
                if(b.timer.get(this.lifetime/5)){
                    for(var i = 0; i < this.fragBullets; i++){
                        rankB.create(b, b.x, b.y, (360/this.fragBullets) * (Math.floor(this.fragBullets/2) - i), 1, 1);
                    }
                }
            },
            despawned(b){
                for(var i = 0; i < this.fragBullets; i++){
                    rankB.create(b, b.x, b.y, (360/this.fragBullets) * (Math.floor(this.fragBullets/2) - i), 1, 1);
                }
            },
        });
        rankS.speed = 2;
        rankS.lifetime = 120;
        rankS.damage = 61;
        rankS.width = 32;
        rankS.height = 18;
        rankS.shrinkY = 0;
        rankS.pierce = true;
        rankS.pierceBuilding = true;
        rankS.backColor = c1;
        rankS.frontColor = c1;
        rankS.fragBullets = 6;
        return rankS;
    })()
);

sakura.reloadTime = 60;
sakura.size = 3;
sakura.rotateSpeed = 5;
sakura.inaccuracy = 0;
sakura.range = 240;
sakura.shootCone = 80;
sakura.shootSound = Sounds.shootBig;
sakura.shootEffect = Fx.thoriumShoot;
sakura.smokeEffect = Fx.thoriumShoot;
sakura.requirements = ItemStack.with(
    Items.lead, 80,
    Items.silicon, 95,
    Items.titanium, 90,
    Items.thorium, 75
);
sakura.buildVisibility = BuildVisibility.shown;
sakura.category = Category.turret;
exports.sakura = sakura;
