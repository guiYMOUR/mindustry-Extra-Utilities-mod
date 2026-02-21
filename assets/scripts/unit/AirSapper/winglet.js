const sapper = new SapBulletType();
sapper.sapStrength = 1;
sapper.length = 72;
sapper.damage = 11;
sapper.shootEffect = Fx.shootSmall;
sapper.hitColor = Color.valueOf("bf92f9");
sapper.color = Color.valueOf("bf92f9");
sapper.despawnEffect = Fx.none;
sapper.width = 1;
sapper.lifetime = 30;
sapper.knockback = -1;

const winglet = new UnitType("winglet");
winglet.constructor = prov(() => extend(UnitTypes.zenith.constructor.get().class, {}));
winglet.weapons.add(
    (() =>{
        const w = new Weapon("");
        w.x = 0;
        w.y = 3;
        w.mirror = false;
        w.reload = 21;
        w.rotate = false;
        w.bullet = sapper;
        w.shootSound = Sounds.shootSap;
        return w;
    })()
);
winglet.flying = true;
winglet.hitSize = 9;
winglet.speed = 3;
winglet.accel = 0.04;
winglet.drag = 0.02;
winglet.health = 130;
winglet.mineSpeed = 2.5;
winglet.mineTier = 1;
winglet.buildSpeed = 0.5;
winglet.itemCapacity = 50;
winglet.engineOffset = 5.8;
winglet.engineSize = 2.1;
winglet.rotateShooting = true;
winglet.ammoType = new PowerAmmoType(500);
winglet.commandLimit = 5;
exports.winglet = winglet;