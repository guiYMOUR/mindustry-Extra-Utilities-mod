const items = require("game/items");
const item = items.lightninAlloy;
const COLOR = item.color;

const m = new FlakBulletType(2.8, 42);
Object.assign(m, {
    sprite : "missile-large",
    collidesGround : true,
    collidesAir : true,
    explodeRange : 40,
    width : 12,
    height : 12,
    shrinkY : 0,
    drag : -0.003,
    homingPower : 0.08,
    homingRange : 60,
    keepVelocity : false,
    lightRadius : 60,
    lightOpacity : 0.7,
    lightColor : COLOR,
    hitSound : Sounds.splash,

    splashDamageRadius : 30,
    splashDamage : 50,

    lifetime : 80,
    backColor : COLOR,
    frontColor : Color.white,

    weaveScale : 8,
    weaveMag : 1,

    trailColor : COLOR,
    trailWidth : 4.5,
    trailLength : 29,

    fragBullets : 7,
    fragVelocityMin : 0.3,
});
m.hitEffect = (() => {
    const e = new ExplosionEffect();
    Object.assign(e, {
        lifetime : 28,
        waveStroke : 6,
        waveLife : 10,
        waveRadBase : 7,
        waveColor : COLOR,
        waveRad : 30,
        smokes : 6,
        smokeColor : Color.white,
        sparkColor : COLOR,
        sparks : 6,
        sparkRad : 35,
        sparkStroke : 1.5,
        sparkLen : 4,
    });
    return e;
})();
m.fragBullet = (() => {
    const mf = new MissileBulletType(3.9, 34);
    Object.assign(mf, {
        homingPower : 0.2,
        weaveMag : 4,
        weaveScale : 4,
        lifetime : 60,
        shootEffect : Fx.shootHeal,
        smokeEffect : Fx.hitLaser,
        splashDamage : 38,
        splashDamageRadius : 20,
        frontColor : Color.white,
        hitSound : Sounds.none,

        lightColor : COLOR,
        lightRadius : 40,
        lightOpacity : 0.7,

        trailColor : COLOR,
        trailWidth : 2.5,
        trailLength : 20,
        trailChance : -1,

        backColor : COLOR,

        despawnEffect : Fx.none,
    });
    mf.hitEffect = (() => {
        const e = new ExplosionEffect();
        Object.assign(e, {
            lifetime : 20,
            waveStroke : 2,
            waveColor : COLOR,
            waveRad : 12,
            smokeSize : 0,
            smokeSizeBase : 0,
            sparkColor : COLOR,
            sparks : 9,
            sparkRad : 35,
            sparkLen : 4,
            sparkStroke : 1.5,
        });
        return e;
    })();
    return mf;
})();


const sunburst = extendContent(PowerTurret, "sunburst", {});
Object.assign(sunburst, {
    size : 3,
    powerUse : 6,
    shootType : m,
    shots : 2,
    shootShake : 1,
    reloadTime : 60,
    restitution : 0.02,
    recoilAmount : 4,
    range : 33*8,
    spread : 0,
    shootCone : 15,
    ammoUseEffect : Fx.none,
    health : 230*3*3,
    inaccuracy : 0,
    rotateSpeed : 6,
    shootSound : Sounds.missile,
    placeableLiquid : true,
});
sunburst.requirements = ItemStack.with(
    Items.lead, 250,
    Items.silicon, 180,
    item, 40
);
sunburst.buildVisibility = BuildVisibility.shown;
sunburst.category = Category.turret;

exports.sunburst = sunburst;
