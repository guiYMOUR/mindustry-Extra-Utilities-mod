package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static ExtraUtilities.content.EUGet.*;

public class HealCone extends BulletType {
    public float findRange;
    public float findAngle;

    public HealCone(float findAngle, float findRange){
        this.findAngle = findAngle;
        this.findRange = findRange;
        speed = 0;
        damage = 0;
        collides = false;
        collidesAir = false;
        collidesGround = false;
        absorbable = false;
        hittable = false;
        keepVelocity = false;
        despawnEffect = EUFx.coneFade(findRange, findAngle);
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
        healPercent = 12;
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
        Units.nearby(b.team, b.x, b.y, findRange, unit -> {
            if(unit.damaged() && Angles.within(b.rotation(), b.angleTo(unit), findAngle/2) && unit != b.owner){
                unit.heal((unit.maxHealth < 1000 ? 1000 : unit.maxHealth) * (healPercent/ratio) * Time.delta);
            }
        });
        Vars.indexer.eachBlock(b, findRange,
                other -> other.damaged() && Angles.within(b.rotation(), b.angleTo(other), findAngle/2),
                other -> {
                    other.heal((healPercent/ratio) * other.maxHealth * Time.delta);
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, Pal.heal, other.block);
                });
    }

    @Override
    public void draw(Bullet b) {
        float range = findRange;
        float angle = findAngle;
        Draw.color(Pal.heal);
        Draw.z(Layer.buildBeam);
        Draw.alpha(0.8f);
        Fill.circle(b.x, b.y, 4);
        for(float i = b.rotation() - angle/2; i < b.rotation() + angle/2; i+=2){
            float px1 = posx(b.x, range, i);
            float py1 = posy(b.y, range, i);
            float px2 = posx(b.x, range, i+2);
            float py2 = posy(b.y, range, i+2);
            Fill.tri(b.x, b.y, px1, py1, px2, py2);
        }
        Draw.alpha(1);
    }
}
