const cor = Color.valueOf("e5f3fe");
const liC = Color.valueOf("bf92f9");
var lib = require("blib");
const MS = extend(BasicBulletType,{
	update(b){
		const target = Units.closestTarget(b.team, b.x,b.y,100)
		if(target != null) {
			b.vel.setAngle(Mathf.slerpDelta(b.vel.angle(), b.angleTo(target), 0.425));
		}
		if(b.timer.get(1,4)){
			for(var i = 0; i < 3; i++){
				Lightning.create(b.team, cor, 18, b.x + Mathf.random(-60,60), b.y + Mathf.random(-60,60), Mathf.random(360), Mathf.random(9,25));
			}
			for(var i = 0; i < 5; i++){
				Lightning.create(b.team, cor, 16, b.x + Mathf.random(-60,60), b.y + Mathf.random(-60,60), Mathf.random(360), Mathf.random(6,12));
			}
			for(var i = 0; i < 7; i++){
				Lightning.create(b.team, cor, 18, b.x + Mathf.random(-60,60), b.y + Mathf.random(-60,60), Mathf.random(360), Mathf.random(3,7));
			}
			if(Mathf.chance(Time.delta * 0.075)){
				var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, liC, 12, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(15));
			}
		}
	},
	draw(b){
		const plasmas = 6;
		var plasmaRegions = new Array();
		for(var i = 0; i < 6; i++){
			plasmaRegions[i] = "btm-plasma-"+i;
		}
		for(var i = 0; i < plasmas; i++){
			var r = 29 + Mathf.absin(Time.time, 2 + i * 1, 5 - i * 0.5);
			Draw.color(Color.valueOf("a7d8fe"), liC, i / 6);
			Draw.alpha((0.37128 + Mathf.absin(Time.time, 2 + i * 2, 0.3 + i * 0.05)) * 1);
			Draw.rect(Core.atlas.find(plasmaRegions[i]), b.x, b.y,Time.time * (12 + i * 6) * 1);
		}
	},
	despawned(b){
		this.despawnEffect.at(b.x, b.y, b.rotation());
		for(var i = 0; i < 12; i++){
			Lightning.create(b.team, cor, 32, b.x , b.y , Mathf.random(360), Mathf.random(30,45));  
			var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, liC, 32, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(15));
		}
	}
})
MS.speed = 1.5,
MS.damage = 0,
MS.knockback = 0.2,
MS.splashDamageRadius = 40,
MS.splashDamage = 250,
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
MS.despawnEffect = lib.newEffect(60, e => {
	Draw.color(liC, cor, e.fin());
	Fill.circle(e.x, e.y, e.fout() * 50);
	Lines.stroke(e.fout() * 4.5);
	Lines.circle(e.x, e.y, e.fin() * 70);
	Lines.stroke(e.fout() * 2.75);
	Lines.circle(e.x, e.y, e.fin() * 40);
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
    setStats(){
		this.super$setStats();
		
		this.stats.remove(Stat.damage);
		this.stats.add(Stat.damage, "250+/s");
	},
});

lib.setBuildingSimple(storm, PowerTurret.PowerTurretBuild, {});
storm.powerUse = 11;
storm.shootType = MS;
storm.shots = 1;
storm.shootShake = 0.5;
storm.reloadTime = 258;
storm.recoilAmount = 3;
storm.restitution = 0.02;
storm.range = 288;
storm.shootCone = 15;
storm.ammoUseEffect = Fx.none;
storm.health = 3320;
storm.inaccuracy = 0;
storm.rotateSpeed = 10;
storm.size = 4;
storm.shootSound = Sounds.spark;

storm.requirements = ItemStack.with(
    Items.copper, 680,
    Items.lead, 500,
    Items.silicon, 460,
    Items.titanium, 350,
    Items.plastanium, 300,
    Items.thorium, 320,
    Items.surgeAlloy, 150
);
storm.buildVisibility = BuildVisibility.shown;
storm.category = Category.turret;

exports.ms = storm;