package ExtraUtilities.worlds.entity.ability;

import ExtraUtilities.content.EUFx;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.Floor;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class propeller extends Ability {
    public float px, py, length, speed;
    public String sprite;

    float rot = 0;

    public propeller(float px, float py, String sprite, float length, float speed){
        this.px = px;
        this.py = py;
        this.length = length;
        this.speed = speed;
        this.sprite = sprite;
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.extra-utilities-propeller", px, py);
    }

    @Override
    public void update(Unit unit) {
        if(
                unit.type != null && unit.type.naval &&
                        !unit.floorOn().isLiquid
        ){
            unit.elevation = 1;
        }
        float realSpeed = unit.elevation * speed * Time.delta;
        rot += realSpeed;
        float out = unit.elevation * length;
        float x = unit.x + Angles.trnsx(unit.rotation, px, py) + Angles.trnsx(unit.rotation, 0, out);
        float y = unit.y + Angles.trnsy(unit.rotation, px, py) + Angles.trnsy(unit.rotation, 0, out);
        if(!unit.moving() && unit.isFlying()){
            Floor floor = Vars.world.floorWorld(x, y);
            if(floor != null) EUFx.wind.at(x + Mathf.range(8), y + Mathf.range(8), floor.mapColor);
        }
    }

    @Override
    public void draw(Unit unit) {
        Draw.mixcol(Color.white, unit.hitTime);
        Draw.z(Math.max(Layer.groundUnit - 1, unit.elevation * Layer.flyingUnitLow));
        float out = unit.elevation * length;
        float x = unit.x + Angles.trnsx(unit.rotation, px, py) + Angles.trnsx(unit.rotation, 0, out);
        float y = unit.y + Angles.trnsy(unit.rotation, px, py) + Angles.trnsy(unit.rotation, 0, out);
        Draw.rect(Core.atlas.find(name("wing-s")),x, y, unit.rotation + rot * 2);//why not Time.time ? I Don't Know. ha~
        Draw.rect(Core.atlas.find(sprite),x, y, unit.rotation - 90);
        Draw.mixcol();
        Draw.z(Math.min(Layer.darkness, Layer.groundUnit - 1));
        if(unit.isFlying()){
            Draw.color(Pal.shadow);
            float e = Math.max(unit.elevation, unit.type.shadowElevation);
            Draw.rect(Core.atlas.find(sprite), x + UnitType.shadowTX * e, y + UnitType.shadowTY * e, unit.rotation - 90);
            Draw.color();
        }
    }
}
