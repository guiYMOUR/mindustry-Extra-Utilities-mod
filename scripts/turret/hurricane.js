//
const lib = require('blib');
const liC = Color.valueOf("bf92f9");
const hur = extend(BasicBulletType, {
    update(b){
        if(b.timer.get(6)){
            for(var i = 0; i < 13; i++){
                var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, liC, 3, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(15));
                
            }
        }
    }
    
});
hur.width = 1;
hur.height = 1;
hur.damage = 26;
hur.lifetime = 60;
hur.speed = 3;
hur.despawnEffect = Fx.shockwave;
hur.pierceCap = 2
hur.pierceBuilding = true

const hurricane = extendContent(PowerTurret, 'hurricane', {});

lib.setBuildingSimple(hurricane, PowerTurret.PowerTurretBuild, {});
hurricane.powerUse = 6;
hurricane.shootType = hur;
hurricane.shots = 1;
hurricane.shootShake = 0.5;
hurricane.reloadTime = 60;
hurricane.restitution = 0.02;
hurricane.range = 160;
hurricane.shootCone = 15;
hurricane.ammoUseEffect = Fx.none;
hurricane.health = 1650;
hurricane.inaccuracy = 0;
hurricane.rotateSpeed = 10;
hurricane.size = 2;
hurricane.shootSound = Sounds.spark;
hurricane.requirements = ItemStack.with(
    Items.copper, 100,
    Items.lead, 60,
    Items.silicon, 30,
    //Items.graphite, 30,
    Items.titanium, 20
);
hurricane.buildVisibility = BuildVisibility.shown;
hurricane.category = Category.turret;

exports.hurricane = hurricane;
