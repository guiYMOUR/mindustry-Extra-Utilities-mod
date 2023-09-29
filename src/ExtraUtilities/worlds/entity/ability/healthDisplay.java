package ExtraUtilities.worlds.entity.ability;

import arc.Core;
import arc.graphics.g2d.*;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;

public class healthDisplay extends Ability {
    public float y, width, height;
    public healthDisplay(float y, float width, float height){
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String localized() {
        return Core.bundle.get("ability.extra-utilities-healthDisplay");
    }

    @Override
    public void draw(Unit unit) {
        float drawy = unit.y + y;
        float realWidth = width * (unit.health / unit.maxHealth);
        Draw.color(Pal.health);
        Fill.rect(unit.x, drawy, realWidth, height);
        Draw.reset();
        Draw.color();
    }
}
