const vb = new UnitType("venom-bee");
vb.constructor = prov(() => extend(UnitTypes.mono.constructor.get().class, {}));
vb.defaultController = prov(() => new MinerAI());
vb.weapons.add(
    (() =>{
        const w = new Weapon("btm-venom-bee-cannon");
        w.x = 0;
        w.y = -12.5;
        w.shootY = 5;
        w.reload = 42;
        w.rotate = true;
        w.mirror = false;
        w.bullet = (() =>{
            const b = new ArtilleryBulletType(2, 14);
            Object.assign(b, {
                hitEffect : Fx.sapExplosion,
                knockback : 0.8,
                lifetime : 90,
                width : 19,
                height : 19,
                collidesTiles : true,
                ammoMultiplier : 4,
                splashDamageRadius : 70,
                splashDamage : 65,
                backColor : Pal.sapBulletBack,
                frontColor : Pal.sapBullet,
                lightningColor : Pal.sapBullet,
                lightning : 3,
                lightningLength : 10,
                smokeEffect : Fx.shootBigSmoke2,
                hitShake : 5,
                status : StatusEffects.sapped,
                statusDuration : 60 * 10,
            });
            return b;
        })();
        w.shootSound = Sounds.artillery;
        return w;
    })()
);
vb.armor = 4;
vb.flying = true;
vb.lowAltitude = true;
vb.miningRange = 96;
vb.hitSize = 18;
vb.speed = 2;
vb.accel = 0.06;
vb.drag = 0.017;
vb.health = 880;
vb.mineSpeed = 5;
vb.mineTier = 6;
vb.buildSpeed = 1;
vb.itemCapacity = 60;
vb.engineOffset = 19;
vb.engineSize = 5.4;
vb.rotateShooting = true;
vb.ammoType = new PowerAmmoType(800);
vb.commandLimit = 5;
exports.vb = vb;