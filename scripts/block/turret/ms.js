const cor = Color.valueOf("e5f3fe");
const liC = Color.valueOf("bf92f9");
var lib = require("blib");
//var plasmaRegions = [];

const MS = extend(BasicBulletType,{
    update(b){
        const target = Units.closestTarget(b.team, b.x,b.y,100)
        if(target != null) {
            b.vel.setAngle(Mathf.slerpDelta(b.vel.angle(), b.angleTo(target), 0.425));
        }
        if(b.timer.get(1,4)){
            for(var i = 0; i < 3; i++){
                Lightning.create(b.team, cor, 16, b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(8,20));
            }
            for(var i = 0; i < 5; i++){
                Lightning.create(b.team, liC, 12, b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(5,10));
            }
            for(var i = 0; i < 7; i++){
                Lightning.create(b.team, cor, 18, b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(3,7));
            }
            if(Mathf.chance(Time.delta * 0.075)){
                var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, liC, 12, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(14));
            }
        }
    },
    draw(b){
        const plasmas = 6;
        for(var i = 0; i < plasmas; i++){
            var r = 29 + Mathf.absin(Time.time, 2 + i * 1, 5 - i * 0.5);
            Draw.color(liC, Color.valueOf("a7d8fe"), i / 6);
            Draw.alpha((0.3 + Mathf.absin(Time.time, 2 + i * 2, 0.3 + i * 0.05)) * 1);
            Draw.blend(Blending.additive);
            Draw.rect(Core.atlas.find("btm-plasma-" + i), b.x, b.y, 40, 40, Time.time * (12 + i * 6) * 1);
            Draw.blend();
        }
    },
    despawned(b){
        this.despawnEffect.at(b.x, b.y, b.rotation());
        for(var i = 0; i < 12; i++){
            Lightning.create(b.team, cor, 32, b.x , b.y , Mathf.random(360), Mathf.random(25,40));  
            var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, liC, 32, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(14));
        }
    }
})
MS.speed = 1.5,
MS.damage = 0,
MS.knockback = 0.2,
MS.splashDamageRadius = 56,
MS.splashDamage = 200,
MS.width = 3,
MS.height = 3,
MS.drag = 0,
MS.collidesTiles = false,
MS.hitTiles = false;
MS.pierce = true,
MS.hitSize = 0,
MS.collides = false,
MS.collidesAir = true,
MS.lifetime = 300;
MS.status = StatusEffects.shocked;
MS.despawnEffect = lib.newEffect(60, e => {
    Draw.color(liC, cor, e.fin());
    Fill.circle(e.x, e.y, e.fout() * 40);
    Lines.stroke(e.fout() * 4.5);
    Lines.circle(e.x, e.y, e.fin() * 60);
    Lines.stroke(e.fout() * 2.75);
    Lines.circle(e.x, e.y, e.fin() * 30);
    const d = new Floatc2({get(x, y){
        Lines.stroke(e.fout() * 2);
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 1);
    }})
    Angles.randLenVectors(e.id, 45, 1 + 65 * e.fin(), e.rotation, 360,d);
    const c = new Floatc2({get(x, y){
        Fill.circle(e.x + x, e.y + y, e.fout() * 10);
    }})
    Angles.randLenVectors(e.id, 85, 1 + 160 * e.fin(),  Time.time * 4, 360,c);
});

const storm = extendContent(PowerTurret, 'MAGNETIC-STORM', {
    /*load(){
        this.super$load();
        for(var i = 0; i < 6; i++){
            plasmaRegions[i] = "btm-plasma-"+i;
        }
    },*/
});

lib.setBuildingSimple(storm, PowerTurret.PowerTurretBuild, {});
storm.powerUse = 19;
storm.shootType = MS;
storm.shots = 1;
storm.shootShake = 5;
storm.reloadTime = 258;
storm.recoilAmount = 6;
storm.restitution = 0.02;
storm.range = 300;
storm.shootCone = 15;
storm.ammoUseEffect = Fx.none;
storm.health = 3320;
storm.inaccuracy = 0;
storm.rotateSpeed = 3;
storm.coolantUsage = 0.65;
storm.coolantMultiplier = 0.7;
storm.size = 4;
storm.shootSound = Sounds.spark;
storm.absorbLasers = true;

storm.requirements = ItemStack.with(
    Items.lead, 780,
    Items.silicon, 600,
    //Items.titanium, 385,
    Items.plastanium, 350,
    Items.thorium, 350,
    Items.surgeAlloy, 295
);
storm.buildVisibility = BuildVisibility.shown;
storm.category = Category.turret;

exports.ms = storm;