//
const lib = require('blib');

const chargeTime = 40;
const chargeDelay = 30;
const chargeEffects = 3;
const chargeEffect = lib.newEffect(20, (e) => {
        Draw.color(Pal.surge);
        //Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fout() * 54);
        Draw.reset();
});
const chargeSound = Sounds.none;
const chargeBeginEffect = lib.newEffect(chargeTime * 1.2, e => {
        Draw.color(Pal.surge, Color.valueOf("e5f3fe"), e.fout());
        Fill.circle(e.x, e.y, e.fout() * 18 + 6);

        Draw.color();
        Fill.circle(e.x, e.y, e.fout() * 3);
});

const RGT = extend(BasicBulletType, {});
RGT.sprite = "btm-gt";
RGT.width = 36;
RGT.height = 18;
RGT.damage = 175;
RGT.splashDamageRadius = 28,
RGT.splashDamage = 125,
RGT.lifetime = 60;
RGT.speed = 8;
RGT.reloadMultiplier = 1.2;
RGT.pierceCap = 2;
RGT.pierceBuilding = true;
RGT.shrinkY = 0;
RGT.backColor = Pal.lancerLaser;
RGT.frontColor = Pal.lancerLaser;
RGT.despawnEffect = lib.newEffect(20,(e) => {
	Draw.color(Pal.lancerLaser);
	Lines.stroke(e.fout() * 3);
	Lines.circle(e.x, e.y, e.fin() * 60);
	Lines.stroke(e.fout() * 1.75);
	Lines.circle(e.x, e.y, e.fin() * 45);
	Draw.color(Pal.lancerLaser);
	Fill.circle(e.x, e.y, e.fout() * 20);
	Draw.color(Pal.lancerLaser);
	Fill.circle(e.x, e.y, e.fout() * 14);
});
const RGS = extend(BasicBulletType, {
    update(b){
        if(b.timer.get(10)){
            for(var i = 0; i < 5; i++){
                var len = Mathf.random(1, 7);
                var a = b.rotation() + Mathf.range(this.fragCone/2) + this.fragAngle;
                Lightning.create(b.team, Pal.surge, 5, b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, 0.5 + Mathf.random(15));
                
            }
        }
    }
});
RGS.sprite = "btm-gs";
RGS.width = 36;
RGS.height = 18;
RGS.damage = 285;
RGS.splashDamageRadius = 36,
RGS.splashDamage = 255,
RGS.lifetime = 72;
RGS.speed = 8;
RGS.pierceCap = 2;
RGS.pierceBuilding = true;
RGS.shrinkY = 0;
RGS.backColor = Pal.surge;
RGS.frontColor = Pal.surge;

RGS.despawnEffect = lib.newEffect(20,(e) => {
	Draw.color(Pal.surge);
	Lines.stroke(e.fout() * 3);
	Lines.circle(e.x, e.y, e.fin() * 60);
	Lines.stroke(e.fout() * 1.75);
	Lines.circle(e.x, e.y, e.fin() * 45);
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fout() * 20);
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fout() * 14);
});

const RG = extendContent(ItemTurret, 'RG', {});

lib.setBuildingSimple(RG, ItemTurret.ItemTurretBuild, {
    updateShooting(){
        if(this.reload >= this.block.reloadTime){
            var type = this.peekAmmo();
            this.shoot(type);
            //this.reload = 0;
        }else{
            this.reload = this.efficiency() == 0 ? 0 : this.reload + this.delta() * this.peekAmmo().reloadMultiplier * this.efficiency();
        }
    },
    shoot(type){
        //
        var vec = new Vec2();
        vec.trns(this.rotation, this.block.size * 8 / 2);
        chargeBeginEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
        chargeSound.at(this.x + vec.x, this.y + vec.y, 1);
            
        for(var i = 0; i < chargeEffects; i++){
            Time.run((chargeDelay / chargeEffects) * i, () => {
                if(!this.isValid()) return;
                vec.trns(this.rotation, this.block.size * 8 / 2);
                chargeEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
            });
        }
        

        Time.run(chargeTime, () => {
            if(!this.isValid() || !this.hasAmmo()) return;
            vec.trns(this.rotation, this.block.size * 8 / 2);
            this.recoil = this.block.recoilAmount;
            this.heat = 1;
            for(var i = 0; i < this.block.shots; i++){
                Time.run(8 * i, () => {
                    this.peekAmmo().create(this, this.team, this.x + vec.x, this.y + vec.y, this.rotation, 1 ,1)
                    this.effects();
                })
            }
        });
        this.useAmmo();
        this.reload = 0;
    },
    efficiency(){
        if(!this.enabled || this.liquids.get(this.liquids.current()) / this.block.liquidCapacity < 0.036) return 0;
        return this.power.status;
    },
    effects(){
        var tr = new Vec2;
        var fshootEffect = this.block.shootEffect == Fx.none ? this.peekAmmo().shootEffect : this.block.shootEffect;
        var fsmokeEffect = this.block.smokeEffect == Fx.none ? this.peekAmmo().smokeEffect : this.block.smokeEffect;
        tr.trns(this.rotation, this.block.size * 8 / 2);
        fshootEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
        fsmokeEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
        this.block.shootSound.at(this.x + tr.x, this.y + tr.y, Mathf.random(0.9, 1.1));

        if(this.block.shootShake > 0){
            Effect.shake(this.block.shootShake, this.block.shootShake, this);
        }
        this.recoil = this.block.recoilAmount;
    },//Wrote this was intended to change the shooting effects, may be changed behind.
});
RG.shootSound = Sounds.laser;
RG.reloadTime = 60 * 5;
RG.shots = 5;
RG.inaccuracy = 0;
RG.size = 4;
RG.ammoUseEffect = Fx.none;
RG.restitution = 0.05;
RG.recoilAmount = 6;
RG.shootShake = 5;
RG.ammoPerShot = 2;
RG.range = 8 * 59.5;
RG.liquidCapacity = 12;
RG.coolantMultiplier = 1.1;
RG.coolantUsage = 0.45;
RG.health = 180 * 4 * 4;
//RG.canOverdrive = false;
RG.shootEffect = Fx.none;
RG.smokeEffect = Fx.none;
RG.ammo(
    Items.titanium, RGT,
    Items.surgeAlloy, RGS
);
RG.requirements = ItemStack.with(
    //Items.copper, 500,
    Items.lead, 600,
    Items.silicon, 550,
    Items.graphite, 355,
    Items.titanium, 325,
    Items.thorium, 285,
    Items.plastanium, 220,
    Items.surgeAlloy, 150
);
RG.consumes.powerCond(9, boolf(b => b.isActive()));
RG.buildVisibility = BuildVisibility.shown;
RG.category = Category.turret;

exports.RG = RG;
