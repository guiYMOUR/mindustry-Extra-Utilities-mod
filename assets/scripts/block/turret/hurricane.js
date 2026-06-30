//
const lib = require('blib');
const items = require("game/items");
const liC = Color.valueOf("bf92f9");
const dec = Color.valueOf("ffffff");
const dec2 = Color.valueOf("ffffff");
const bullets = lib.getClass("ExtraUtilities.content.EUBulletTypes");
const hardMod = lib.hardMod;

const hur = extend(BasicBulletType, {});
hur.bulletInterval = 6;
hur.intervalBullets = 10;
hur.intervalBullet = (() => {
    const l = new LightningBulletType();
    l.damage = 5;
    l.ammoMultiplier = 1;
    l.lightningColor = liC;
    l.lightningLength = 3;
    l.lightningLengthRand = 4;
    l.buildingDamageMultiplier = 0.5;
    return l;
})();
hur.width = 1;
hur.height = 1;
hur.damage = 26;
hur.lifetime = 60;
hur.speed = 3;
hur.status = StatusEffects.shocked;
hur.despawnEffect =lib.Fx.elDsp(dec, dec2);
hur.hitEffect = Fx.hitLancer;
hur.pierceCap = 2;
hur.pierceBuilding = true;

const hurricane = extend(PowerTurret, 'hurricane', {});

if(hardMod) {
    hurricane.consumePower(420/60);
    hurricane.shootType = hur;
    hurricane.reload = 60;
    hurricane.range = 182;
    hurricane.inaccuracy = 3;
    hurricane.requirements = ItemStack.with(
        Items.copper, 100,
        Items.lead, 77,
        Items.graphite, 50,
        items.crispSteel, 70
    );
    lib.Coolant(hurricane, 0.2);
} else {
    hurricane.consumePower(600/60);
    hurricane.shootType = bullets.auroraMain;
    hurricane.reload = 120;
    hurricane.range = 176;
    hurricane.inaccuracy = 30;
    hurricane.requirements = ItemStack.with(
        Items.copper, 100,
        Items.lead, 140,
        Items.silicon, 110,
        //Items.graphite, 30,
        items.crispSteel, 110
    );
}
//hurricane.shots = 1;
hurricane.shootShake = 0.5;
//hurricane.restitution = 0.02;
hurricane.shootCone = 15;
hurricane.ammoUseEffect = Fx.none;
hurricane.health = 1850;

hurricane.rotateSpeed = 3;
hurricane.size = 2;
hurricane.shootSound = Sounds.shootSap;

hurricane.buildVisibility = BuildVisibility.shown;
hurricane.category = Category.turret;

exports.hurricane = hurricane;
