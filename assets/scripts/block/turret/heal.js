/**
 * @author guiY
 * @readme <The player can control it or use the logic processor.>
 */
//范围
const findRange = 184;
//角度
const findAngle = 40;
//射击间隔
const shootDuration = 240;
//治疗比
const healP = 13;
//加成(相位物)
const boostM = 2;

//128.1can but 128cannot
/*function pos(x, y, length, angle){
    var a = (Math.PI * angle)/180;
    var sin = Math.sin(a);
    var cos = Math.cos(a);
    var posValue = [];
    var px = x + length * cos;
    var py = y + length * sin;
    posValue[0] = px;
    posValue[1] = py;
    return posValue;
}*/
function posx(x, y, length, angle){
    var a = (Math.PI * angle)/180;
    var cos = Math.cos(a);
    var px = x + length * cos;
    return px;
}
function posy(x, y, length, angle){
    var a = (Math.PI * angle)/180;
    var sin = Math.sin(a);
    var py = y + length * sin;
    return py;
}

const hb = extend(BasicBulletType,{
    update(b){
        const ratio = (60 * 100) / b.owner.getBoost();//REPAIR PER SECOND
        Units.nearby(b.team, b.x, b.y, findRange, cons(unit =>{
            if(unit.damaged() && Angles.within(b.rotation(), b.angleTo(unit), findAngle/2)){
                unit.heal(unit.maxHealth * (this.healPercent/ratio));
            }
        }));
        if(b.timer.get(1, 30)) {
            Vars.indexer.eachBlock(b, findRange, boolf(other => other.damaged() && Angles.within(b.rotation(), b.angleTo(other), findAngle/2)), cons(other => {
                other.heal((this.healPercent / ratio) * other.maxHealth * 20);
                Fx.healBlockFull.at(other.x, other.y, !(other instanceof PayloadSource.PayloadSourceBuild) ? other.block.size : 5, Pal.heal);
            }));
        }
    },
    draw(b){
        const range = findRange * b.owner.getHeat();
        const angle = findAngle;
        Draw.color(Pal.heal);
        Draw.z(Layer.buildBeam);
        Draw.alpha(0.8);
        Fill.circle(b.x, b.y, 4);
        for(var i = b.rotation() - angle/2; i < b.rotation() + angle/2; i+=2){
            //The same thing.
            /*var px1 = pos(b.x, b.y, range, i)[0];
            var py1 = pos(b.x, b.y, range, i)[1];
            var px2 = pos(b.x, b.y, range, i+2)[0];
            var py2 = pos(b.x, b.y, range, i+2)[1];*/
            var px1 = posx(b.x, b.y, range, i);
            var py1 = posy(b.x, b.y, range, i);
            var px2 = posx(b.x, b.y, range, i+2);
            var py2 = posy(b.x, b.y, range, i+2);
            Fill.tri(b.x, b.y, px1, py1, px2, py2);
        }
        Draw.alpha(1);
        Draw.z();
    },
});
Object.assign(hb, {
    speed : 0,
    lifetime : 10,
    damage : 0,
    collides: false,
    collidesAir: false,
    collidesGround: false,
    absorbable: false,
    hittable: false,
    keepVelocity: false,
    despawnEffect : Fx.none,
    shootEffect : Fx.none,
    smokeEffect : Fx.none,
    healPercent : healP,
});

const fadeEffect = new Effect(15, cons(e =>{
    const range = findRange * e.fout();
    const angle = findAngle;
    Draw.color(Pal.heal);
    Draw.z(Layer.buildBeam);
    Draw.alpha(0.8);
    for(var i = e.rotation - angle/2; i < e.rotation + angle/2; i+=2){
        /*var px1 = pos(e.x, e.y, range, i)[0];
        var py1 = pos(e.x, e.y, range, i)[1];
        var px2 = pos(e.x, e.y, range, i+2)[0];
        var py2 = pos(e.x, e.y, range, i+2)[1];*/
        var px1 = posx(e.x, e.y, range, i);
        var py1 = posy(e.x, e.y, range, i);
        var px2 = posx(e.x, e.y, range, i+2);
        var py2 = posy(e.x, e.y, range, i+2);
        Fill.tri(e.x, e.y, px1, py1, px2, py2);
    }
}));

const heal = extend(PowerTurret, "heal", {
    setStats(){
       this.super$setStats();
       var healTime = Math.floor(100/healP);
       var reloadNeed = Math.floor(healTime/((shootDuration -60)/60)) - 1;
       var reloadRest = Math.floor(reloadNeed * this.reload/60);
       this.stats.add(Stat.repairTime, 2 * (healTime + reloadRest), StatUnit.seconds);
       this.stats.add(Stat.boostEffect, boostM, StatUnit.timesSpeed);
    },
});
heal.buildType = prov(() => {
    const timerConsume = 180;
    var bullet = null;
    var bulletLife = 0;
    var bulletHeat = 0;
    var boost = 1;
    var timer = 0;
    var tr = new Vec2();
    
    var block = heal;
    var x = 0, y = 0;
    var rotation = 0;
    var target = null;
    var unit = null;
    var items = null;
    var health = 0;
    
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        getHeat(){
            return bulletHeat;
        },
        getBoost(){
            return boost;
        },
        updateConsume(){
            if(bulletLife > 0 && this.items.get(Items.phaseFabric) > 0) timer += Time.delta;
            if(timer > timerConsume){
                this.consume();
                timer = 0;
            }
        },
        updateTile() {
            x = this.x;
            y = this.y;
            rotation = this.rotation;
            target = this.target;
            unit = this.unit;
            health = this.health;
            items = this.items;
            boost = items.get(Items.phaseFabric) > 0 ? boostM : 1;
            this.updateConsume();
            this.wasShooting = false;
            this.curRecoil = Mathf.lerpDelta(this.curRecoil, 0, 1 / block.recoilTime);
            this.recoilOffset.trns(rotation, -Mathf.pow(this.curRecoil, block.recoilPow) * block.recoil);
            if(unit != null){
                this.unit.health = health;
                this.unit.rotation = rotation;
                this.unit.team = this.team;
                this.unit.set(x, y);
            }
            if(this.logicControlTime > 0){
                this.logicControlTime -= Time.delta;
            }
            if(this.hasAmmo()){
                if(this.timer.get(block.timerTarget, block.targetInterval)){
                    if(target != null && (target.health >= target.maxHealth || !(target.within(this, findRange + 8) && Angles.within(rotation, this.angleTo(target), findAngle/2)) || this.isControlled() || this.logicControlled())){
                        this.target = null;
                    }
                    this.findTarget();
                }
                var canShoot = true;
                if(this.isControlled()){ //player behavior
                    this.targetPos.set(unit.aimX, unit.aimY);
                    canShoot = unit.isShooting;
                } else if(this.logicControlled()){ //logic behavior
                    canShoot = this.logicShooting;
                } else { //default AI behavior
                    if(Number.isNaN(rotation)){
                        this.rotation = 0;
                    }
                }
                if(target == null && !(this.isControlled() || this.logicControlled())){
                    bulletLife = 0;
                    return;
                }
                this.targetPosition(target);
                var targetRot = this.angleTo(this.targetPos);
                this.turnToTarget(targetRot);

                if(Angles.angleDist(rotation, targetRot) < block.shootCone && canShoot){
                    this.wasShooting = true;
                    this.updateShooting();
                }
            }
            this.updateReload();

            if (bulletLife > 0 && bullet != null){
                this.wasShooting = true;
                tr.trns(rotation, block.shootLength, 0);
                bullet.rotation(rotation);
                bullet.set(x + tr.x, y + tr.y);
                bullet.time = 0;
                //this.heat = 1;
                this.curRecoil = 1;
                bulletLife -= Time.delta / Math.max(this.efficiency(), 0.00001);
                bulletHeat = Math.min(Mathf.lerpDelta(bulletHeat, 2, 0.035), 1);
                if (bulletLife <= 0) {
                    fadeEffect.at(bullet, bullet.rotation());
                    this.target = null;
                    bullet = null;
                    bulletHeat = 0;
                }
            }
        },
        findTarget(){
            if(this.target != null || this.isControlled() || this.logicControlled()) return;
            Vars.indexer.eachBlock(this, findRange + 8, boolf(other => other.damaged()), cons(other => {
                if(other != this){
                    this.target = other;
                }
            }));
            Units.nearby(this.team, this.x, this.y, findRange + 8, cons(unit =>{
                if(unit.damaged()){
                    this.target = unit;
                }
            }));
        },
        updateShooting(){
            if(bulletLife > 0 && bullet != null){
                return;
            }
            this.reload += this.edelta();
            if(this.reload >= block.reloadTime && (this.consValid() || this.cheating())){
                var type = hb;//this.peekAmmo();
                this.shoot(type);
            
                this.reload = 0;
            }
        },
        bullet(type, angle){
            bulletLife = shootDuration;
            tr.trns(this.rotation, block.shootLength, 0);
            bullet = type.create(this.tile.build, this.team, this.x + tr.x, this.y + tr.y, angle);
        },
        shouldActiveSound(){
            return bulletLife > 0 && bullet != null;
        },
    }, heal);
});
Object.assign(heal, {
    health : 180*3*3,
    shootType : hb,
    range : findRange + 8,
    reload : 90,
    size : 3,
    firingMoveFract : 0.8,
    shootDuration : shootDuration,
    acceptCoolant : false,
    shootSound : Sounds.none,
    loopSound : Sounds.loopPulse,
});
heal.consumeItem(Items.phaseFabric).boost();
heal.consumePower(7);
heal.requirements = ItemStack.with(
    Items.graphite, 200,
    Items.silicon, 240,
    Items.titanium, 180,
    Items.thorium, 150,
    Items.surgeAlloy, 100
);
heal.buildVisibility = BuildVisibility.shown;
heal.category = Category.turret;

exports.heal = heal;