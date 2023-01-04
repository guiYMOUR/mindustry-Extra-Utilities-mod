//const cor = Color.valueOf("e5f3fe");
const cor = Color.valueOf("bf92f9");
const liC = Color.valueOf("98cce8");
const lib = require("blib");
const items = require("game/items");
const MagneticStormBulletType = lib.getClass("ExtraUtilities.worlds.entity.bullet.MagneticStormBulletType");
const DrawElectric = lib.getClass("ExtraUtilities.worlds.drawer.DrawElectric");
const DrawBall = lib.getClass("ExtraUtilities.worlds.drawer.DrawBall");
const DrawArrow = lib.getClass("ExtraUtilities.worlds.drawer.DrawArrow");
const DrawTri = lib.getClass("ExtraUtilities.worlds.drawer.DrawTri");

const MS = new MagneticStormBulletType(cor, liC);
MS.homingRange = 100;
MS.homingPower = 0.4;
MS.speed = 1.5;
MS.damage = 0;
MS.knockback = 0.2;
MS.splashDamageRadius = 64;
MS.splashDamage = 200;
MS.width = 40;
MS.height = 40;
MS.drag = 0;
MS.collidesTiles = false;
MS.pierce = true;
MS.hitSize = 0;
MS.collides = false;
MS.collidesAir = true;
MS.lifetime = 300;

//Anuke: for visual stats only.
MS.buildingDamageMultiplier = 0.25;

MS.status = StatusEffects.shocked;

const storm = extend(PowerTurret, 'magstorm', {});
storm.consumePower(32);
storm.shootType = MS;
//storm.shots = 1;
storm.shootShake = 5;
storm.reload = 282;
storm.recoil = 6;
//storm.restitution = 0.02;
storm.range = 300;
storm.shootCone = 15;
storm.ammoUseEffect = Fx.none;
storm.health = 3920;
storm.inaccuracy = 0;
storm.rotateSpeed = 1.5;
// storm.coolantUsage = 0.65;
// storm.coolantMultiplier = 0.7;
storm.size = 5;
storm.shootSound = Sounds.spark;
storm.absorbLasers = true;
//lib.Coolant(storm, 0.5);
storm.coolant = storm.consume(new ConsumeLiquid(Liquids.water, 15 / 60));

storm.minWarmup = 0.98;
storm.shootWarmupSpeed = 0.04;
storm.envEnabled |= Env.space;

storm.drawer = (() => {
    const d = new DrawTurret("reinforced-");
    d.parts.add(
        (() => {
            const p = new RegionPart("-back");
            p.progress = DrawPart.PartProgress.warmup;
            p.moveX = 3.2;
            p.moveY = 0;
            p.heatColor = cor;
            p.heatProgress = DrawPart.PartProgress.warmup;
            p.mirror = true;
            return p;
        })(),
        (() => {
            const p = new RegionPart("-front");
            p.progress = DrawPart.PartProgress.warmup;
            p.moveX = 5.7;
            p.moveY = 0;
            p.heatColor = liC;
            p.heatProgress = DrawPart.PartProgress.warmup;
            p.mirror = true;
            return p;
        })(),
        (() => {
            const p = new RegionPart("-mid");
            p.progress = DrawPart.PartProgress.recoil;
            p.moveX = 0;
            p.moveY = 0;
            p.heatColor = liC;
            p.heatProgress = DrawPart.PartProgress.warmup;
            p.mirror = false;
            p.under = true;
            return p;
        })(),
    );
    for(var a = 0; a < 3; a ++) {
        d.parts.add(
            (() => {
                const p = new RegionPart("-spine");
                p.progress = DrawPart.PartProgress.warmup.delay(a / 4);
                p.turretHeatLayer = Layer.effect - 0.2;
                p.heatProgress = DrawPart.PartProgress.warmup.delay(0.8);
                p.heatColor = liC;
                p.under = true;
                p.mirror = true;
                p.moveY = -15 / 3 - a * 3;
                p.moveX = 42 / 3 - a + 2;
                p.moveRot = -a * 30;
                p.color = liC;
                p.moves.add(new DrawPart.PartMove(DrawPart.PartProgress.recoil.delay(a / 4), 0, 0, 30));
                return p;
            })()
        );
    }
    for(var i = 0; i < 2; i++){
        d.parts.add(
            (() => {
                const p = new DrawBall();
                p.progress = DrawPart.PartProgress.warmup.delay(i/4);
                p.id = i;
                p.bColor = liC;
                p.mirror = false;
                p.y = 19;
                p.rotate = (360 / 2) * i;
                p.layer = Layer.effect;
                return p;
            })()
        )
    }
    d.parts.add(
        (() => {
            const p = new DrawArrow();
            p.color = liC;
            p.layer = Layer.effect;
            return p;
        })(),
        (() => {
            const p = new DrawTri();
            p.color = liC;
            p.cColor = cor;
            p.length = 40;
            p.get = -15;
            p.y = (19 - p.get);
            p.back = true;
            p.layer = Layer.effect;
            return p;
        })(),
        (() => {
            const p = new DrawElectric();
            p.bColor = liC;
            p.lColor = cor;
            p.mirror = false;
            p.y = -15;
            p.radius = 8;
            p.point = 3;
            p.colorful = false;
            p.square = false;
            p.layer = Layer.effect;
            return p;
        })(),
        (() => {
            const p = new DrawElectric();
            p.progress = DrawPart.PartProgress.warmup.delay(0.9);
            p.bColor = liC;
            p.lColor = cor;
            p.radius = 10;
            p.mirror = false;
            //p.square = false;
            p.y = 19;
            p.layer = Layer.effect;
            return p;
        })()
    );
    return d;
})();

storm.requirements = ItemStack.with(
    Items.graphite, 780,
    Items.silicon, 600,
    Items.thorium, 350,
    Items.surgeAlloy, 250,
    items.lightninAlloy, 190
);
storm.buildVisibility = BuildVisibility.shown;
storm.category = Category.turret;

exports.ms = storm;