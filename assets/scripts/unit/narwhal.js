var ability = require("other/ability");
const bullets = require("other/bullets");
const weapon = require("other/weapon");
const AI = require("other/unitAI");

const narwhal = new UnitType("narwhal");
narwhal.constructor = prov(() => extend(UnitTypes.navanax.constructor.get().class, {
    kill(){
        if(this.dead) return;
        for(var i = 0; i < 8; i++){
            var a = this.rotation + Mathf.range(180);
            Lightning.create(this.team, Pal.heal, 35, this.x, this.y, a, Mathf.random(20, 40));
        }
        Damage.damage(this.team, this.x, this.y, 200, 1500, true, true);
        this.super$kill();
    },
}));
narwhal.defaultController = prov(() => AI.DefenderHealAI(true, true, true));
for(var x of [-18, 18]){
    narwhal.weapons.add(
        (() =>{
            const w = weapon.healProjectorWeapon({
                name : "btm-narwhal-heal",
                x : x,
                y : 7,
                bullet : bullets.unitHealCone({
                    lifetime : 240,
                    healPercent : 10,
                }),
            });
            return w;
        })()
    );
}
narwhal.weapons.add(
    (() =>{
        const w = weapon.healProjectorWeapon({
            name : "",
            flyShoot : true,
            x : 0,
            y : 18,
            autoTarget : true,
            controllable : false,
            //rotate : false,
            shootY : 1,
            bullet : bullets.unitHealCone({
                lifetime : 240,
                healPercent : 9,
                findAngle : 70,
                findRange : 152,
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
var
p1 = [22, 15, 0, 15, 6],
p2 = [-25, 20, 1, 15, -6],
p3 = [22, -15, 2, -15, -6],
p4 = [-25, -20, 3, -15, 6];
var pos = [p1, p2, p3, p4];
for(var p of pos){
    narwhal.abilities.add(ability.propeller(p[0], p[1], "btm-wing" + p[2], p[3], p[4]));
}

narwhal.armor = 21;
narwhal.drag = 0.2;
narwhal.flying = false;
narwhal.canBoost = true;
narwhal.boostMultiplier = 1.2;
narwhal.riseSpeed = 0.02;
narwhal.lowAltitude = true;
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