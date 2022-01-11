//
const lib = require('blib');
const items = require("game/items");
const effect = require("block/turret/blackhole");

const chargeTime = 40;
const chargeDelay = 30;
const chargeEffects = 3;
const chargeEffect = lib.newEffect(20, (e) => {
        Draw.color(Pal.surge);
        //Lines.stroke(e.fout() * 2 + 0.2);
        Lines.circle(e.x, e.y, e.fout() * 54);
        Draw.reset();
});
const chargeSound = Sounds.lasercharge2;
const chargeBeginEffect = lib.newEffect(chargeTime * 1.2, e => {
        Draw.color(Pal.surge, Color.valueOf("e5f3fe"), e.fout());
        Fill.circle(e.x, e.y, e.fout() * 21 + 7);

        Draw.color();
        Fill.circle(e.x, e.y, e.fout() * 3);
});

const RGT = extend(BasicBulletType, {});
RGT.sprite = "btm-gt";
RGT.width = 40;
RGT.height = 20;
RGT.damage = 180;
RGT.splashDamageRadius = 28;
RGT.splashDamage = 130;
RGT.buildingDamageMultiplier = 0.15;
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
RGS.width = 40;
RGS.height = 20;
RGS.damage = 290;
RGS.splashDamageRadius = 36;
RGS.splashDamage = 260;
RGS.buildingDamageMultiplier = 0.1;
RGS.lifetime = 72;
RGS.speed = 8;
RGS.pierceCap = 2;
RGS.pierceBuilding = true;
RGS.shrinkY = 0;
RGS.backColor = Pal.surge;
RGS.frontColor = Pal.surge;
RGS.status = StatusEffects.shocked;
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

const bigShotEff = effect.aimShoot(Pal.surge, 1.2, chargeTime + 5, 14);

const RG = extendContent(ItemTurret, 'RG', {});

RG.buildType = prov(()=>{ 
var x=0,y=0;
var rotation = 0;
const block = RG;
return new JavaAdapter(ItemTurret.ItemTurretBuild, {
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
        x = this.x;
        y = this.y;
        rotation = this.rotation;
        var vec = new Vec2();
        vec.trns(rotation, block.size * 8 / 2);
        chargeBeginEffect.at(x + vec.x, y + vec.y, rotation);
        //effect.aimShoot(Pal.surge, this.block.range, 1.2, chargeTime + 5, 14).at(this.x + vec.x, this.y + vec.y, this.rotation);
        bigShotEff.at(x + vec.x, y + vec.y, rotation, {length:RG.range});
        chargeSound.at(x + vec.x, y + vec.y, 1);
            
        for(var i = 0; i < chargeEffects; i++){
            Time.run((chargeDelay / chargeEffects) * i, () => {
                if(!this.isValid()) return;
                vec.trns(rotation, block.size * 8 / 2);
                chargeEffect.at(x + vec.x, y + vec.y, rotation);
            });
        }
        this.charging = true;
        Time.run(chargeTime, () => {
            if(!this.isValid() || !this.hasAmmo()) return;
            this.block.tr.trns(rotation, block.size * 8 / 2);
            this.heat = 1;
            for(var i = 0; i < block.shots; i++){
                Time.run(8 * i, () => {
                    //this.peekAmmo().create(this, this.team, this.x + vec.x, this.y + vec.y, this.rotation, 1 ,1);
                    this.bullet(type, rotation);
                    //this.recoil = this.block.recoilAmount;
                    this.effects();
                })
            }
            Time.run(8*block.shots,()=>{
                this.charging = false;
            });
            //this.charging = false;
        });
        this.useAmmo();
        this.reload = 0;
    },
    efficiency(){
        if(!this.enabled || this.liquids.get(this.liquids.current()) / this.block.liquidCapacity < 0.036) return 0;
        return this.power.status;
    },
    /*effects(){
        var tr = new Vec2();
        var fshootEffect = block.shootEffect;
        var fsmokeEffect = block.smokeEffect;
        tr.trns(this.rotation, block.size * 8 / 2);
        fshootEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
        fsmokeEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
        block.shootSound.at(this.x + tr.x, this.y + tr.y, Mathf.random(0.9, 1.1));

        if(block.shootShake > 0){
            Effect.shake(block.shootShake, block.shootShake, this);
        }
        this.recoil = block.recoilAmount;
    },//Wrote this was intended to change the shooting effects, may be changed behind.*/
},RG);});
RG.shootSound = Sounds.laser;
RG.reloadTime = 60 * 5;
RG.shots = 5;
RG.inaccuracy = 0;
RG.size = 5;
RG.ammoUseEffect = Fx.none;
RG.restitution = 0.05;
RG.recoilAmount = 6;
RG.shootShake = 5;
RG.ammoPerShot = 2;
RG.range = 8 * 60;
RG.liquidCapacity = 12;
RG.coolantMultiplier = 1.1;
RG.coolantUsage = 0.45;
RG.health = 180 * 5 * 5;
//RG.canOverdrive = false;
RG.shootEffect = Fx.none;
RG.smokeEffect = Fx.none;
RG.ammo(
    Items.titanium, RGT,
    Items.surgeAlloy, RGS
);
RG.requirements = ItemStack.with(
    //Items.copper, 500,
    Items.lead, 750,
    Items.silicon, 700,
    Items.graphite, 585,
    Items.titanium, 345,
    Items.plastanium, 220,
    items.lightninAlloy, 180
);
RG.consumes.powerCond(15, boolf(b => b.isActive()));
RG.buildVisibility = BuildVisibility.shown;
RG.category = Category.turret;

exports.RG = RG;
