const rail = extend(PointBulletType, {
    despawned(b){
        this.super$despawned(b);
        new Effect(15, cons(e => {
            Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * this.splashDamageRadius);
        })).at(b.x, b.y);
    },
});
rail.shootEffect = new Effect(10, cons(e => {
        Draw.color(Color.white, Color.valueOf("767a84"), e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
}));
rail.hitEffect = Fx.none;
rail.trailEffect = Fx.railTrail;
rail.despawnEffect = Fx.none;
rail.trailSpacing = 20;
rail.damage = 10;
rail.collidesGround = false;
rail.speed = 336;
rail.reloadMultiplier = 1.2;
rail.status = StatusEffects.shocked;
rail.splashDamage = 420;
rail.splashDamageRadius = 48;
rail.hitShake = 6;

const rail2 = extend(PointBulletType, {
    despawned(b){
        this.super$despawned(b);
        new Effect(15, cons(e => {
            Draw.color(Color.white, Color.valueOf("cbd97f"), e.fin());
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * this.splashDamageRadius);
        })).at(b.x, b.y);
    },
});
rail2.shootEffect = new Effect(10, cons(e => {
        Draw.color(Color.white, Color.valueOf("cbd97f"), e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
}));
rail2.hitEffect = Fx.none;
rail2.trailEffect = Fx.railTrail;
rail2.despawnEffect = Fx.none;
rail2.trailSpacing = 20;
rail2.damage = 400;
rail2.collidesGround = false;
rail2.speed = 336;
rail2.status = StatusEffects.shocked;
rail2.splashDamage = 600;
rail2.splashDamageRadius = 72;
rail2.hitShake = 6;

const antiaircraft = extendContent(ItemTurret, "antiaircraft", {});
antiaircraft.size = 3;
antiaircraft.spread = 9;
antiaircraft.shots = 2;
antiaircraft.alternate = true;
antiaircraft.reloadTime = 150;
antiaircraft.recoilAmount = 7;
antiaircraft.range = 336;
antiaircraft.shootCone = 15;
antiaircraft.targetGround = false;
antiaircraft.ammoUseEffect = Fx.none;
antiaircraft.ammoPerShot = 2;
antiaircraft.health = 1890;
antiaircraft.rotateSpeed = 10;
antiaircraft.shootSound = Sounds.railgun;
antiaircraft.ammo(
    Items.silicon, rail,
    Items.plastanium, rail2
);
antiaircraft.requirements = ItemStack.with(
    Items.copper, 250,
    Items.lead, 300,
    Items.silicon, 150,
    Items.graphite, 125,
    Items.titanium, 205,
    Items.surgeAlloy, 30
);
antiaircraft.consumes.powerCond(8, boolf(b => b.isActive()));
antiaircraft.buildVisibility = BuildVisibility.shown;
antiaircraft.category = Category.turret;

exports.antiaircraft = antiaircraft;