const AI = require("other/unitAI");

const bubble = extend(LiquidBulletType, {});
bubble.liquid = Liquids.water;
bubble.status = StatusEffects.sapped;
bubble.lightColor = Pal.sap;
bubble.lightOpacity = Pal.sap;
bubble.damage = 1;
bubble.knockback = 1.2;
bubble.puddleSize = 9;
bubble.orbSize = 5;
bubble.lifetime = 18;
bubble.speed = 3.5;
bubble.shootEffect = Fx.shootLiquid;

const fireDefense = new BombBulletType(45, 25);
Object.assign(fireDefense, {
    backColor : Pal.sap,
    frontColor : Pal.sap,
    width : 16,
    height : 24,
    hitEffect : Fx.none,
    despawnEffect : Fx.none,
    shootEffect : Fx.none,
    smokeEffect : Fx.none,
    fragBullet : bubble,
    fragBullets : 30,
    collidesAir : true,
    status : StatusEffects.sapped,
});

const moth = new UnitType("moth");
moth.constructor = prov(() => extend(UnitTypes.zenith.constructor.get().class, {}));
moth.defaultController = prov(() => AI.Firefighter(160, 310, Time.toSeconds * 3));
moth.weapons.add(
    (() =>{
        const w = new Weapon("");
        w.x = 0;
        w.y = -1;
        w.shootY = 0;
        //w.mirror = true;
        w.reload = 24;
        w.rotate = true;
        w.bullet = fireDefense;
        w.shootSound = Sounds.shootSap;
        return w;
    })()
);
moth.armor = 2;
moth.flying = true;
moth.hitSize = 13;
moth.speed = 2;
moth.accel = 0.04;
moth.drag = 0.016;
moth.health = 420;
moth.mineSpeed = 3;
moth.mineTier = 2;
moth.buildSpeed = 0.5;
moth.itemCapacity = 60;
moth.engineOffset = 4;
moth.engineSize = 4.9;
moth.rotateShooting = false;
moth.ammoType = new PowerAmmoType(500);
moth.commandLimit = 6;
exports.moth = moth;