package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.storage.CoreBlock;

public class SCSBullet extends BasicBulletType {
    public int maxFind = 12;
    //public float damageUp = 1f;

    Seq<Unit> unitTarget = new Seq<>();
    
    public SCSBullet(){
        lifetime = 150;
        damage = 120;
        speed = 3;
        status = StatusEffects.sapped;
        statusDuration = 60 * 2f;
        width = 28;
        height = 18;
        backColor = Pal.sapBulletBack;
        frontColor = Pal.sapBullet;
        trailLength = 18;
        trailWidth = 8;
        trailColor = Pal.sapBulletBack;
        trailInterval = 3;
        trailRotation = true;
        splashDamage = 150;
        splashDamageRadius = 100;
        hitShake = 4;
        trailEffect = new Effect(16, e -> {
            Draw.color(Pal.sapBulletBack);
            for(int s : Mathf.signs){
                Drawf.tri(e.x, e.y, 4, 30 * e.fslope(), e.rotation + 90*s);
            }
        });
        despawnHit = true;
        despawnEffect = new Effect(18, e -> {
            Draw.color(backColor);
            Lines.stroke(e.fout() * 2 + 0.2f);
            Lines.circle(e.x, e.y, e.fin() * splashDamageRadius);
        });
        despawnSound = Sounds.shootArc;
        homingPower = 0.08f;
        homingRange = 256;
        homingDelay = 30;
        keepVelocity = false;
        shrinkY = 0;

        collides = false;
//        collidesAir = false;
//        collidesGround = false;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        Units.nearbyEnemies(b.team, b.x, b.y, homingRange, u -> {
            if(u.targetable(b.team) && !u.inFogTo(b.team)) unitTarget.add(u);
        });
        unitTarget.removeAll(unit -> unit.dead || !unit.within(b, homingRange));
        unitTarget.sort(unit -> unit.dst2(b));
        int find = Math.min(maxFind, unitTarget.size);
        if(b.timer.get(3, 5)){
            for(int a = 0; a < find; a++){
                Unit other = unitTarget.get(a);
                if(other == null) continue;
                other.damage(damage/6);
                other.apply(status, statusDuration);
                Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, other);
                Fx.hitLaserBlast.at(other.x, other.y, b.angleTo(other), Pal.sapBulletBack);
            }
        }
        unitTarget.clear();
    }

    @Override
    public void hit(Bullet b) {
        Units.nearbyEnemies(b.team, b.x, b.y, splashDamageRadius, unit -> {
            if(unit.targetable(b.team)){
                unit.damage(splashDamage);
                unit.apply(status, statusDuration);
                Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, unit);
                Fx.hitLaserBlast.at(unit.x, unit.y, b.angleTo(unit), Pal.sapBulletBack);
            }
        });
        //优先强行断开节点
        Vars.indexer.allBuildings(b.x, b.y, splashDamageRadius, building -> {
            if(building.block != null && building.team != b.team) {
                if (building.block instanceof PowerNode && building.power.links.size > 0) {
                    building.configureAny(null);
                }
            }
        });
//        Vars.indexer.allBuildings(b.x, b.y, splashDamageRadius, building -> {
//            if(building.block != null && building.team != b.team){
//                if(building.block instanceof PowerBlock || building.block instanceof BaseTurret || building.block instanceof CoreBlock){
//                    building.damage(splashDamage * damageUp);
//                    Fx.chainLightning.at(b.x, b.y, 0, Pal.sapBulletBack, building);
//                    Fx.hitLaserBlast.at(building.x, building.y, b.angleTo(building), Pal.sapBulletBack);
//                } else {
//                    building.damage(splashDamage);
//                }
//            }
//        });
        Damage.damage(b.team, b.x, b.y, splashDamageRadius, splashDamage, false);
    }
}
