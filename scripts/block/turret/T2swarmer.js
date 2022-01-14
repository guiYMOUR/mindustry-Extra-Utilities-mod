//guiY
const lib = require("blib");
/*const laser = extend(LaserBulletType, {});
laser.damage = 90;
laser.sideAngle = 20;
laser.sideWidth = 1.5;
laser.sideLength = 80;
laser.width = 10;
laser.length = 200;
laser.shootEffect = Fx.shockwave;
laser.colors = [Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white];
const bomb = extend(BombBulletType, {});
bomb.damage = 50;
bomb.splashDamage = 50;
bomb.splashDamageRadius = 50;
bomb.width = 10;
bomb.height = 14;
bomb.hitEffect = Fx.flakExplosion;
bomb.shootEffect = Fx.none;
bomb.smokeEffect = Fx.none;
bomb.status = StatusEffects.blasted;
bomb.statusDuration = 60;

const f1E = extend(BasicBulletType, {
    update(b){
        b.vel.setAngle(Mathf.slerpDelta(b.rotation() + 45, b.angleTo(b.owner), 1));
        if(b.timer.get(60)){
            if(Math.abs(b.owner.x - b.x) > 15 && Math.abs(b.owner.y - b.y) > 15){
                laser.create(b, b.x, b.y, b.rotation(), 1, 1);
            }
        }
        if(Math.abs(b.owner.x - b.x) <= 8 && Math.abs(b.owner.y - b.y) <= 8){
            Fx.absorb.at(b.x, b.y);
            b.remove();
        }
    },
    despawned(b){
        
    }
});
f1E.damage = 1;
f1E.speed = 2.5;
f1E.width = 8;
f1E.height = 8;
f1E.sprite = "btm-f1";
f1E.lifetime = 3000;
f1E.shrinkY = 0;
f1E.collidesTiles = false;
f1E.collides = false;
const f1 = extend(BasicBulletType, {
    update(b){
        var target = Units.closestTarget(b.team, b.x, b.y, this.homingRange,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
        if (this.homingPower > 0.0001 && (b.time > 50 || target != null)) {
            var targetSet = Units.closestTarget(b.team, b.x, b.y, this.homingRange * 100,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
            if(targetSet != null){
            if(b.timer.get(40)){
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(targetSet), this.homingPower));
            }
            var targetS = Units.closestTarget(b.team, b.x, b.y, this.homingRange*1.5,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
                if(targetS != null){
                    if(b.timer.get(30)){
                laser.create(b, b.x, b.y, b.rotation(), 1, 1);
                    }
                }
            }
        }
    },
    despawned(b){
        
        this.fragBullet.create(b, b.x, b.y, b.rotation(), 1 ,1);
    }
});
f1.damage = 0;
f1.speed = 2.5;
f1.homingRange = 50;
f1.homingPower = 1;
f1.sprite = "btm-f1";
f1.hitEffect = Fx.none;
f1.knockback = 0.8;
f1.lifetime = 360;
f1.width = 8;
f1.height = 8;
f1.pierce = true;
f1.backColor = Color.valueOf("ff0000");
f1.frontColor = Color.white;
f1.spin = 0;
f1.shrinkY = 0;
f1.collidesTiles = false;
f1.collides = false;
f1.fragBullet = f1E;

const f2E = extend(BasicBulletType, {
    update(b){
        b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(b.owner), 1));
        if(b.timer.get(20)){
            if(Math.abs(b.owner.x - b.x) > 15 && Math.abs(b.owner.y - b.y) > 15){
                bomb.create(b, b.x, b.y, b.rotation(), 1, 1);
            }
        }
        if(Math.abs(b.owner.x - b.x) <= 8 && Math.abs(b.owner.y - b.y) <= 8){
            Fx.absorb.at(b.x, b.y);
            b.remove();
        }
    },
    despawned(b){
        
    }
});
f2E.damage = 1;
f2E.speed = 2.5;
f2E.width = 8;
f2E.height = 8;
f2E.sprite = "btm-f3";
f2E.lifetime = 3000;
f2E.shrinkY = 0;
f2E.collidesTiles = false;
f2E.collides = false;
const f2 = extend(BasicBulletType, {
    update(b){
        var target = Units.closestTarget(b.team, b.x, b.y, this.homingRange,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
        if (this.homingPower > 0.0001 && (b.time > 50 || target != null)) {
            var targetSet = Units.closestTarget(b.team, b.x, b.y, this.homingRange * 100,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
            if(targetSet != null){
            if(b.timer.get(40)){
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(targetSet), this.homingPower));
            }
            var targetS = Units.closestTarget(b.team, b.x, b.y, this.homingRange/8,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
                if(targetS != null){
                    if(b.timer.get(5)){
                bomb.create(b, b.x, b.y, b.rotation(), 1, 1);
                    }
                }
            }
        }
    },
    despawned(b){
        
        this.fragBullet.create(b, b.x, b.y, b.rotation(), 1 ,1);
    }
});
f2.damage = 0;
f2.speed = 2.5;
f2.homingRange = 50;
f2.homingPower = 1;
f2.sprite = "btm-f3";
f2.hitEffect = Fx.none;
f2.knockback = 0.8;
f2.lifetime = 360;
f2.width = 8;
f2.height = 8;
f2.pierce = true;
f2.backColor = Color.valueOf("ff0000");
f2.frontColor = Color.white;
f2.spin = 0;
f2.shrinkY = 0;
f2.collidesTiles = false;
f2.collides = false;
f2.fragBullet = f2E;

const f3E = extend(BasicBulletType, {
    update(b){
        b.vel.setAngle(Mathf.slerpDelta(b.rotation() + 45, b.angleTo(b.owner), 1));
        if(b.timer.get(30)){
            if(Math.abs(b.owner.x - b.x) > 15 && Math.abs(b.owner.y - b.y) > 15){
                Bullets.standardThorium.create(b, b.x, b.y, b.rotation(), 1, 1);
            }
        }
        if(Math.abs(b.owner.x - b.x) <= 8 && Math.abs(b.owner.y - b.y) <= 8){
            Fx.absorb.at(b.x, b.y);
            b.remove();
        }
    },
    despawned(b){
        
    }
});
f3E.damage = 1;
f3E.speed = 2.5;
f3E.width = 8;
f3E.height = 8;
f3E.sprite = "btm-f2";
f3E.lifetime = 3000;
f3E.shrinkY = 0;
f3E.collidesTiles = false;
f3E.collides = false;
const f3 = extend(BasicBulletType, {
    update(b){
        var target = Units.closestTarget(b.team, b.x, b.y, this.homingRange,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
        if (this.homingPower > 0.0001 && (b.time > 50 || target != null)) {
            var targetSet = Units.closestTarget(b.team, b.x, b.y, this.homingRange * 100,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
            if(targetSet != null){
            if(b.timer.get(40)){
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(targetSet), this.homingPower));
            }
            var targetS = Units.closestTarget(b.team, b.x, b.y, this.homingRange * 1.2,
            boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
            boolf(t => this.collidesGround)
                    );
                if(targetS != null){
                    if(b.timer.get(5)){
                Bullets.standardThorium.create(b, b.x, b.y, b.rotation(), 1, 1);
                    }
                }
            }
        }
    },
    despawned(b){
        
        this.fragBullet.create(b, b.x, b.y, b.rotation(), 1 ,1);
    }
    //collides(bullet, tile){ this.super$collides(bullet, tile);return false; },
});
f3.damage = 0;
f3.speed = 2.5;
f3.homingRange = 50;
f3.homingPower = 1;
f3.sprite = "btm-f2";
f3.hitEffect = Fx.none;
f3.knockback = 0.8;
f3.lifetime = 360;
f3.width = 8;
f3.height = 8;
f3.pierce = true;
f3.backColor = Color.valueOf("ff0000");
f3.frontColor = Color.white;
f3.spin = 0;
f3.shrinkY = 0;
f3.collidesTiles = false;
f3.collides = false;
f3.fragBullet = f3E;

var bt = [f1, f2, f3];*/

const swT2 = extendContent(ItemTurret, "T2-swarmer", {});
lib.setBuildingSimple(swT2, ItemTurret.ItemTurretBuild, {
    /*shoot(type){
        this.super$shoot(type);
        if(Mathf.chance(0.06)){
            for(var i = 0; i < 2; i ++){
                var a = Math.floor(Math.random() * (bt.length));
                bt[a].create(this, this.team, this.x, this.y, this.rotation + 45 * (i - 0.5), 1 ,1);
            }
        }
    }*/
});
swT2.reloadTime = 30;
swT2.shots = 7;
swT2.burstSpacing = 5;
swT2.inaccuracy = 10;
swT2.range = 34.5 *8;
swT2.xRand = 10;
swT2.size = 3;
swT2.health = 300 * 3 * 3;
swT2.shootSound = Sounds.missile;
swT2.ammo(
    Items.blastCompound, Bullets.missileExplosive,
            Items.pyratite, Bullets.missileIncendiary,
            Items.surgeAlloy, Bullets.missileSurge
            //Items.copper, f2
);
swT2.limitRange();
swT2.requirements = ItemStack.with(
    Items.graphite, 65,
    Items.titanium, 50,
    Items.plastanium, 55,
    Items.silicon, 90,
    Items.surgeAlloy, 25
);
swT2.buildVisibility = BuildVisibility.shown;
swT2.category = Category.turret;

//exports.f = bt;
exports.swT2 = swT2;

