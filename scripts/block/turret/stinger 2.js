//
const lib = require('blib');
const status = require("other/status");

const chargeTime = 40;
const chargeMaxDelay = 30;
const chargeEffects = 8;
const chargeEffect = Fx.lancerLaserCharge;
const chargeSound = Sounds.none;
const chargeBeginEffect = lib.newEffect(chargeTime * 1.5, e => {
        Draw.color(Color.valueOf("ffffff"), Color.valueOf("c6d676"), e.fin());
        Fill.circle(e.x, e.y, e.fin() * 4);

        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 3);
});
const eff1 = lib.newEffect(10, (e) => {
        Draw.color(Color.white, Color.valueOf("#C6D676"), e.fin());
        Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fin() * 28);
});

const stingerLaser = extend(LaserBulletType, {});
stingerLaser.damage = 286;
stingerLaser.sideAngle = 2;
stingerLaser.sideWidth = 2;
stingerLaser.sideLength = 40;
stingerLaser.collidesAir = false
stingerLaser.length = 230;
stingerLaser.width = 22;
stingerLaser.hitSize = 6;
stingerLaser.lifetime = 25;
stingerLaser.drawSize = 400;
stingerLaser.status = status.poison;
stingerLaser.colors =[Color.valueOf("#C6D676"), Color.valueOf("#C6D676"), Color.white];
stingerLaser.shootEffect = eff1;


const stinger = extendContent(PowerTurret, 'stinger', {});

lib.setBuildingSimple(stinger, PowerTurret.PowerTurretBuild, {
    shoot(type){
        //this.super$shoot(type);
        this.useAmmo();
        var vec = new Vec2();
        vec.trns(this.rotation, this.block.size * 8 / 2);
        chargeBeginEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
        chargeSound.at(this.x + vec.x, this.y + vec.y, 1);
            
        for(var i = 0; i < chargeEffects; i++){
            Time.run(Mathf.random(chargeMaxDelay), () => {
                if(!this.isValid()) return;
                vec.trns(this.rotation, this.block.size * 8 / 2);
                chargeEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
            });
        }
        

        Time.run(chargeTime, () => {
            if(!this.isValid()) return;
            vec.trns(this.rotation, this.block.size * 8 / 2);
            this.recoil = this.block.recoilAmount;
            this.heat = 1;
            stingerLaser.create(this, this.team, this.x + vec.x, this.y + vec.y, this.rotation, 1 ,1)
            this.effects();
            
        });
    }
});
stinger.powerUse = 10;
stinger.shootType = stingerLaser;
stinger.shots = 1;
stinger.shootShake = 3;
stinger.reloadTime = chargeTime * 2;
stinger.range = 200;
stinger.shootCone = 15;
stinger.ammoUseEffect = Fx.none;
stinger.health = 2960;
stinger.inaccuracy = 0;
stinger.recoilAmount = 2.5;
stinger.rotateSpeed = 4;
stinger.size = 3;
stinger.targetAir = false;
stinger.shootSound = Sounds.laser;
stinger.requirements = ItemStack.with(
    Items.copper, 200,
    Items.silicon, 150,
    Items.graphite, 125,
    Items.titanium, 100,
    Items.thorium, 100,
    Items.plastanium, 80,
    Items.surgeAlloy, 10
);
stinger.buildVisibility = BuildVisibility.shown;
stinger.category = Category.turret;

exports.stinger = stinger;
