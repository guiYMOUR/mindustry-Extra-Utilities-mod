package ExtraUtilities.worlds.entity.ability;

import ExtraUtilities.content.EUFx;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class PcShieldArcAbility extends ShieldArcAbility {
    private static Unit paramUnit;
    private static PcShieldArcAbility paramField;
    private static final Vec2 paramPos = new Vec2();
    private static final Cons<Bullet> shieldConsumer = b -> {
        if(b.team != paramUnit.team && b.type.absorbable && paramField.data > 0 &&
                !b.within(paramPos, paramField.radius - paramField.width/2f) &&
                Tmp.v1.set(b).add(b.vel).within(paramPos, paramField.radius + paramField.width/2f) &&
                Angles.within(paramPos.angleTo(b), paramUnit.rotation + paramField.angleOffset, paramField.angle / 2f)){

            b.absorb();
            Fx.absorb.at(b);

            //break shield
            if(paramField.data <= b.damage()){
                paramField.data = 0;
                paramField.data -= paramField.cooldown * paramField.regen;

                EUFx.arcShieldBreak.at(paramPos.x, paramPos.y, 0, paramUnit.team.color, paramUnit);
            } else {
                paramField.data -= b.damage();
            }


            paramField.alpha = 1f;
        }
    };


    protected float widthScale, alpha;

    @Override
    public void update(Unit unit){

        if(data < max){
            data += Time.delta * regen;
        }

        boolean active = data > 0 && (unit.isShooting || !whenShooting);
        alpha = Math.max(alpha - Time.delta/10f, 0f);

        if(active){
            widthScale = Mathf.lerpDelta(widthScale, 1f, 0.06f);
            paramUnit = unit;
            paramField = this;
            paramPos.set(x, y).rotate(unit.rotation - 90f).add(unit);

            Groups.bullet.intersect(unit.x - radius, unit.y - radius, radius * 2f, radius * 2f, shieldConsumer);
        }else{
            widthScale = Mathf.lerpDelta(widthScale, 0f, 0.11f);
        }
    }

    @Override
    public void draw(Unit unit){
        var pos = paramPos.set(x, y).rotate(unit.rotation - 90f).add(unit);

        if(widthScale > 0.001f){
            Draw.z(Layer.shields);

            Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));


            if(!Vars.renderer.animateShields){
                Draw.alpha(0.4f);
            }

            if(region != null){
                Vec2 rp = offsetRegion ? pos : Tmp.v1.set(unit);
                Draw.yscl = widthScale;
                Draw.rect(region, rp.x, rp.y, unit.rotation - 90);
                Draw.yscl = 1f;
            }

            if(drawArc){
                Lines.stroke(width * widthScale);
                Lines.arc(pos.x, pos.y, radius, angle / 360f, unit.rotation + angleOffset - angle / 2f);
            }
        } else {
            Draw.z(Layer.flyingUnitLow);
            Lines.stroke(6, unit.team.color);
            Lines.arc(pos.x, pos.y, radius, ((1 - (-data / (cooldown * regen))) * angle) / 360f, unit.rotation - angle / 2f);
        }
        Draw.reset();
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability." + name("EUArcFieldAbility"));
    }
}
