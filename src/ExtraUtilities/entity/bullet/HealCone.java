package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.worlds.blocks.turret.MendTurret;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static ExtraUtilities.content.EUGet.*;

public class HealCone extends BulletType {
    public float findRange;
    public float findAngle;
    public boolean percentHeal;
    public float healAmount = 200;

    public HealCone(float findAngle, float findRange){
        this(findAngle, findRange, true);
    }

    public HealCone(float findAngle, float findRange, boolean percentHeal){
        this.findAngle = findAngle;
        this.findRange = findRange;
        this.percentHeal = percentHeal;
        speed = 0;
        damage = 0;
        collides = false;
        collidesAir = false;
        collidesGround = false;
        absorbable = false;
        hittable = false;
        keepVelocity = false;
        //despawnEffect = EUFx.coneFade(findRange, findAngle);
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
        healPercent = 12;
        drawSize = findRange;
        range = findRange;
    }

    public HealCone(){
        this(45, 160);
    }

    @Override
    protected float calculateRange() {
        return findRange;
    }

    @Override
    public void update(Bullet b) {
        float ratio = 60 * 100;

        float in = b.time < b.lifetime - 10 ? Math.min(1, b.time/10) : (b.lifetime - b.time)/10;
        in = Interp.fastSlow.apply(in);

        float amountMt = b.owner instanceof MendTurret.MendTurretBuild mt ? mt.amountMti() : 1;
        float angleMt = (b.owner instanceof MendTurret.MendTurretBuild mt ? mt.angleMti() : 1) * in;
        Units.nearby(b.team, b.x, b.y, findRange * in, unit -> {
            if(unit.damaged() && Angles.within(b.rotation(), b.angleTo(unit), (findAngle * angleMt)/2) && unit != b.owner){
                if(percentHeal) unit.heal((unit.maxHealth < 1000 ? 1000 : unit.maxHealth) * ((healPercent * amountMt)/ratio) * Time.delta);
                else unit.heal((healAmount * amountMt)/60 * Time.delta);
            }
        });
        Vars.indexer.eachBlock(b, findRange * in,
                other -> other.health < other.maxHealth - 0.001f && Angles.within(b.rotation(), b.angleTo(other), (findAngle * angleMt)/2),
                other -> {
                    if(percentHeal) other.heal((healPercent/ratio) * other.maxHealth * Time.delta);
                    else other.heal((healAmount * amountMt)/60 * Time.delta);
                });
        //maybe can Mmm...Combine these two code and heal every second
        if(b.timer.get(30)){
            Vars.indexer.eachBlock(b, findRange * in,
                    other -> other.health < other.maxHealth - 0.001f && Angles.within(b.rotation(), b.angleTo(other), (findAngle * angleMt)/2),
                    other -> {
                        if(other.block != null) Fx.healBlockFull.at(other.x, other.y, 0, Pal.heal, other.block);
                    });
        }
    }

    @Override
    public void draw(Bullet b) {
        float in = b.time < b.lifetime - 10 ? Math.min(1, b.time/10) : (b.lifetime - b.time)/10;
        in = Interp.fastSlow.apply(in);
        float angleMt = b.data instanceof Float f ? f : 1;
        float range = findRange * in;
        float angle = findAngle * angleMt * in;
        Draw.color(Pal.heal);
        Draw.z(Layer.buildBeam);
        Draw.alpha(0.8f);
        Fill.circle(b.x, b.y, 4 * in);
        Fill.arc(b.x, b.y, range, angle/360, b.rotation() - angle/2);
        Draw.alpha(1);
    }
}
