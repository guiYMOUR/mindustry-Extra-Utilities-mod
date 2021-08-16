var ability = require("other/ability");
const bullets = require("other/bullets");
const weapon = require("other/weapon");
const AI = require("other/unitAI");

const narwhal = new UnitType("narwhal");
narwhal.constructor = prov(() => extend(UnitTypes.navanax.constructor.get().class, {
    kill(){
        this.super$kill();
        for(var i = 0; i < 8; i++){
            var a = this.rotation + Mathf.range(180);
            Lightning.create(this.team, Pal.heal, 35, this.x, this.y, a, Mathf.random(20, 40));
        }
        Damage.damage(this.team, this.x, this.y, 200, 1500, true, true);
    },
}));
narwhal.defaultController = prov(() => AI.DefenderHealAI(true, true, true));
narwhal.weapons.add(
    (() =>{
        const w = weapon.healProjectorWeapon({
            name : "btm-narwhal-heal",
            //mirror : true,
            x : 18,
            y : 7,
            bullet : bullets.unitHealCone({
                lifetime : 240,
                healPercent : 10,
            }),
        });
        return w;
    })()
);
narwhal.weapons.add(
    (() =>{
        const w = weapon.healProjectorWeapon({
            name : "btm-narwhal-heal",
            //mirror : true,
            x : -18,
            y : 7,
            bullet : bullets.unitHealCone({
                lifetime : 240,
                healPercent : 10,
            }),
        });
        return w;
    })()
);
narwhal.weapons.add(
    (() => {
        const w = weapon.antiMissileWeapon({
            name : "btm-narwhal-defense",
            y : -37,
            loadSpeed : -1.5,
        });
        w.xRand = 8;
        w.reload = 6;
        return w;
    })()
);

narwhal.trailLength = 70;
narwhal.trailX = 24;
narwhal.trailY = -32;
narwhal.trailScl = 3.5;
narwhal.abilities.add(new UnitSpawnAbility(UnitTypes.mega, 32*60, 0, 27));
narwhal.abilities.add(ability.BatteryAbility(72000, 120, 120, 0, -15));
narwhal.abilities.add(new RepairFieldAbility(400, 60 * 3, 120));
narwhal.armor = 21;
narwhal.drag = 0.2;
narwhal.flying = false;
narwhal.canBoost = true;
narwhal.boostMultiplier = 1.5;
narwhal.riseSpeed = 0.05;
narwhal.engineOffset = 45;
narwhal.engineSize = 8;
narwhal.speed = 0.7;
narwhal.accel = 0.2;
narwhal.hitSize = 60;
narwhal.rotateSpeed = 1;
narwhal.rotateShooting = false;
narwhal.buildSpeed = 10;
narwhal.drawShields = false;
narwhal.health = 65000;
narwhal.itemCapacity = 800;
narwhal.commandLimit = 10;
narwhal.ammoType = new PowerAmmoType(1800);
exports.narwhal = narwhal;