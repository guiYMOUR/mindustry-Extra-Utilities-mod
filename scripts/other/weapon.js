/*
* @author <guiY>为海辅献上心脏!!!(某种原因，武器要单加，太困了，一天没睡，就先这样吧(for some reason, weapons need to be added separately, too sleepy, a day did not sleep, temporarily so, ahhh~
* @weapon(s)
*/
const bullets = require("other/bullets");

const healProjectorWeapon = (() => {
    return (object) => {
        const options = Object.assign({
            targetBuildings : true,
            flyShoot : false,
            
            name : "",
            reload : 90,
            mirror : false,
            predictTarget : false,
            autoTarget : false,
            controllable : true,
            rotate : true,
            rotateSpeed : 4,
            alternate : false,
            useAmmo : true,
            shootY : 8,
            recoil : 0,
            bullet : Bullets.standardThoriumBig,
            x : 0,
            y : 0,
        }, object)
        const h = extend(Weapon, {
            addStats(u, w){
                this.super$addStats(u, w);
                w.row();
                var healTime = Math.floor(100/options.bullet.healPercent);
                var reloadNeed = Math.floor(healTime/(options.bullet.lifetime/60));
                var reloadRest = Math.floor(reloadNeed * options.reload/60);
                w.add("[lightgray]" + Stat.repairTime.localized() + ": " + (options.mirror ? "1/2x " : "") + "[white]" + (3 * (healTime + reloadRest)) + " " + StatUnit.seconds.localized());
                if(options.flyShoot){
                    w.row();
                    var func = options.autoTarget && !options.controllable ? "auto" : "operation";
                    w.add("fly only & " + func);
                }
            },
            findTarget(unit, x, y, range, air, ground){
                var out = Units.closest(unit.team, x, y, range, boolf(u => u != unit && u.damaged()));
                if(out != null || !options.targetBuildings) return out;
                return Units.findAllyTile(unit.team, x, y, range, boolf(other => other.damaged()));
            },
            checkTarget(unit, target, x, y, range){
                return !(target.within(unit, range + unit.hitSize/2) && target.team == unit.team && target.damaged() && target.isValid());
            },
            update(unit, mount){//for narwhal(Heart for narwhal!!!)为海辅(鲸歌)献上心脏!!!
                /*if(!options.flyShoot){
                    this.super$update(unit, mount);
                } else {*/
                    var can = options.flyShoot ? (!unit.disarmed) && unit.isFlying() : unit.canShoot();
                mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
                    var
                    weaponRotation = unit.rotation - 90 + (this.rotate ? mount.rotation : 0),
                    mountX = unit.x + Angles.trnsx(unit.rotation - 90, this.x, this.y),
                    mountY = unit.y + Angles.trnsy(unit.rotation - 90, this.x, this.y),
                    bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                    bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                    shootAngle = this.rotate ? weaponRotation + 90 : Angles.angle(bulletX, bulletY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
                    if(!this.controllable && this.autoTarget){
                        if((mount.retarget -= Time.delta) <= 0){
                            mount.target = this.findTarget(unit, mountX, mountY, this.bullet.range(), this.bullet.collidesAir, this.bullet.collidesGround);
                            mount.retarget = mount.target == null ? this.targetInterval : this.targetSwitchInterval;
                        }
                        if(mount.target != null && this.checkTarget(unit, mount.target, mountX, mountY, this.bullet.range())){
                        mount.target = null;
                        }
                        var shoot = false;
                        if(mount.target != null){
                            shoot = mount.target.within(mountX, mountY, this.bullet.range() + Math.abs(this.shootY) + (mount.target instanceof Sized ? mount.target.hitSize/2 : 0)) && can;
                            if(this.predictTarget){
                                var to = Predict.intercept(unit, mount.target, this.bullet.speed);
                                mount.aimX = to.x;
                                mount.aimY = to.y;
                            }else{
                                mount.aimX = mount.target.x;
                                mount.aimY = mount.target.y;
                            }
                        }
                        mount.shoot = mount.rotate = shoot;
                    }
                    if(this.continuous && mount.bullet != null){
                        if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != this.bullet){
                            mount.bullet = null;
                        }else{
                            mount.bullet.rotation(weaponRotation + 90);
                            mount.bullet.set(bulletX, bulletY);
                            mount.reload = this.reload;
                            unit.vel.add(Tmp.v1.trns(unit.rotation + 180, mount.bullet.type.recoil));
                            if(this.shootSound != Sounds.none && !Vars.headless){
                                if(mount.sound == null) mount.sound = new SoundLoop(this.shootSound, 1);
                                mount.sound.update(bulletX, bulletY, true);
                            }
                        }
                    } else {
                        mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / mount.weapon.cooldownTime, 0);
                        if(mount.sound != null){
                            mount.sound.update(bulletX, bulletY, false);
                        }
                    }
                    if(this.otherSide != -1 && this.alternate && mount.side == this.flipSprite &&
                    mount.reload + Time.delta * unit.reloadMultiplier > this.reload/2 && mount.reload <= this.reload/2){
                        unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
                        mount.side = !mount.side;
                    }
                    if(this.rotate && (mount.rotate || mount.shoot) && can){
                        var axisX = unit.x + Angles.trnsx(unit.rotation - 90,  this.x, this.y),
                        axisY = unit.y + Angles.trnsy(unit.rotation - 90,  this.x, this.y);
                        mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
                        mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, this.rotateSpeed * Time.delta);
                    }else if(!this.rotate){
                        mount.rotation = 0;
                        mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
                    }
                    if(mount.shoot &&
                    can &&
                    (!this.useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&
                    (!this.alternate || mount.side == this.flipSprite) &&
                    unit.vel.len() >= mount.weapon.minShootVelocity &&
                    mount.reload <= 0.0001 &&
                    Angles.within(this.rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone)
                    ){
                        this.shoot(unit, mount, bulletX, bulletY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(this.x));
                        mount.reload = this.reload;
                        if(this.useAmmo){
                            unit.ammo--;
                            if(unit.ammo < 0) unit.ammo = 0;
                        }
                    //}
                }
            },
        });
        h.name = options.name;
        h.reload = options.reload;
        h.predictTarget = options.predictTarget;
        h.autoTarget = options.autoTarget;
        h.controllable = options.controllable;
        h.rotate = options.rotate;
        h.alternate = options.alternate,
        h.useAmmo = options.useAmmo;
        h.recoil = options.recoil;
        h.x = options.x;
        h.y = options.y;
        h.bullet = options.bullet;
        h.shootY = options.shootY;
        h.cooldownTime = 180;
        h.shootSound = Sounds.pulse;
        h.mirror = options.mirror;
        h.continuous = true;
        h.rotateSpeed = options.rotateSpeed;
        h.firstShotDelay = 60;
        return h;
    }
})();
exports.healProjectorWeapon = healProjectorWeapon;

const antiMissileWeapon = (() => {
    return (object) => {
        const options = Object.assign({
            name : "",
            reload : 8,
            mirror : false,
            predictTarget : false,
            autoTarget : true,
            controllable : false,
            rotate : true,
            rotateSpeed : 30,
            useAmmo : true,
            shootY : 3,
            recoil : 1.5,
            bullet : bullets.antiMissileBullet,
            loadSpeed : 0,
            x : 0,
            y : 0,
        }, object)
        const at = extend(Weapon, {
            addStats(u, w){
                this.super$addStats(u, w);
                w.row();
                w.add(Core.bundle.format("stat.btm-antiWeapon", options.bullet.range()/Vars.tilesize - 1));
            },
            findTarget(unit, x, y, range, air, ground){
                return Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b => b.team != unit.team && (b.type.homingPower > 0 || b instanceof MissileBulletType), b => b.dst2(x, y));
            },
            checkTarget(unit, target, x, y, range){
                return !(target.within(unit, range) && target.team != unit.team && target instanceof Bullet && target.type != null);
            },
            update(unit, mount){//can shoot during the boost
                var can = !unit.disarmed;//only
                mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
                var
                weaponRotation = unit.rotation - 90 + (this.rotate ? mount.rotation : 0),
                mountX = unit.x + Angles.trnsx(unit.rotation - 90, this.x, this.y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, this.x, this.y),
                bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
                bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
                shootAngle = this.rotate ? weaponRotation + 90 : Angles.angle(bulletX, bulletY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));
                if(!this.controllable && this.autoTarget){
                    if((mount.retarget -= Time.delta) <= 0){
                        mount.target = this.findTarget(unit, mountX, mountY, this.bullet.range(), this.bullet.collidesAir, this.bullet.collidesGround);
                        mount.retarget = mount.target == null ? this.targetInterval : this.targetSwitchInterval;
                    }
                    if(mount.target != null && this.checkTarget(unit, mount.target, mountX, mountY, this.bullet.range())){
                        mount.target = null;
                    }
                    var shoot = false;
                    if(mount.target != null){
                        shoot = mount.target.within(mountX, mountY, this.bullet.range() + Math.abs(this.shootY) + (mount.target instanceof Sized ? mount.target.hitSize/2 : 0)) && can;
                        if(this.predictTarget){
                            var to = Predict.intercept(unit, mount.target, this.bullet.speed);
                            mount.aimX = to.x;
                            mount.aimY = to.y;
                        }else{
                            mount.aimX = mount.target.x;
                            mount.aimY = mount.target.y;
                        }
                    }
                    mount.shoot = mount.rotate = shoot;
                }
                mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / mount.weapon.cooldownTime, 0);
                if(mount.sound != null){
                    mount.sound.update(bulletX, bulletY, false);
                }
                if(this.otherSide != -1 && this.alternate && mount.side == this.flipSprite &&
                mount.reload + Time.delta * unit.reloadMultiplier > this.reload/2 && mount.reload <= this.reload/2){
                    unit.mounts[this.otherSide].side = !unit.mounts[this.otherSide].side;
                    mount.side = !mount.side;
                }
                if(this.rotate && (mount.rotate || mount.shoot) && can){
                    var axisX = unit.x + Angles.trnsx(unit.rotation - 90,  this.x, this.y),
                    axisY = unit.y + Angles.trnsy(unit.rotation - 90,  this.x, this.y);
                    mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
                    mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, this.rotateSpeed * Time.delta);
                }else if(!this.rotate){
                    mount.rotation = 0;
                    mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
                }
                if(mount.shoot &&
                can &&
                (!this.useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&
                (!this.alternate || mount.side == this.flipSprite) &&
                unit.vel.len() >= mount.weapon.minShootVelocity &&
                mount.reload <= 0.0001 &&
                Angles.within(this.rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone)
                ){
                    this.shoot(unit, mount, bulletX, bulletY, mount.aimX, mount.aimY, mountX, mountY, shootAngle, Mathf.sign(this.x));
                    mount.reload = this.reload;
                    if(this.useAmmo){
                        unit.ammo--;
                        if(unit.ammo < 0) unit.ammo = 0;
                    }
                }
                if(mount.target == null){
                    mount.rotation += Time.delta * options.loadSpeed;
                }
            },
        });
        at.name = options.name;
        at.reload = options.reload;
        at.predictTarget = options.predictTarget;
        at.autoTarget = options.autoTarget;
        at.controllable = options.controllable;
        at.rotate = options.rotate;
        at.useAmmo = options.useAmmo;
        at.recoil = options.recoil;
        at.x = options.x;
        at.y = options.y;
        at.bullet = options.bullet;
        at.shootY = options.shootY;
        at.shootSound = Sounds.missile;
        at.mirror = options.mirror;
        at.rotateSpeed = options.rotateSpeed;
        at.targetInterval = 0;
        at.targetSwitchInterval = 0;
        return at;
    }
})();
exports.antiMissileWeapon = antiMissileWeapon;