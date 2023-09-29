package ExtraUtilities.worlds.entity.ability;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.meta.StatValues;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class DeathBullet extends Ability {
    public BulletType db;
    public Object data;

    protected UnitType statUnit;

    public DeathBullet(BulletType db, Object data){
        this.db = db;
        this.data = data;
    }

    @Override
    public void init(UnitType type) {
        statUnit = type;
    }

    @Override
    public void death(Unit unit) {
        if(db == null) return;
        db.create(unit, unit.team, unit.x, unit.y, 0, -1, 1, 1, data);
    }

    @Override
    public void addStats(Table t) {
        StatValues.ammo(ObjectMap.of(statUnit, db)).display(t);
    }

    @Override
    public String localized() {
        return Core.bundle.get("ability." + name("DeathBullet"));
    }
}
