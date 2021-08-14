/*
* @author <guiY>
* @weapon(s)
*/
const bullets = require("other/bullets");

const healProjectorWeapon = (() => {
    return (object) => {
        const options = Object.assign({
            targetBuildings : true,
            
            name : "",
            reload : 90,
            mirror : true,
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
            },
            findTarget(unit, x, y, range, air, ground){
                var out = Units.closest(unit.team, x, y, range, boolf(u => u != unit && u.damaged()));
                if(out != null || !options.targetBuildings) return out;
                return Units.findAllyTile(unit.team, x, y, range, boolf(other => other.damaged()));
            },
            checkTarget(unit, target, x, y, range){
                return !(target.within(unit, range + unit.hitSize/2) && target.team == unit.team && target.damaged() && target.isValid());
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
            update(unit, mount){
                this.super$update(unit, mount);
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