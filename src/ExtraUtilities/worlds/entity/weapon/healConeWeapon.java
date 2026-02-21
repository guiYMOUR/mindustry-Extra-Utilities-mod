package ExtraUtilities.worlds.entity.weapon;

import arc.math.Angles;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class healConeWeapon extends BoostWeapon {
    public boolean targetBuildings = true;

    public healConeWeapon(String name){
        this.name = name;
        shootSound = Sounds.loopPulse;
        cooldownTime = 180;
        shoot.firstShotDelay = 60;
        continuous = true;
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);
        t.row();
        int healTime = (int) (100/bullet.healPercent);
        int reloadNeed = (int) (healTime/(bullet.lifetime/60));
        int reloadRest = (int) (reloadNeed * reload/60);
        t.add("[lightgray]" + Stat.repairTime.localized() + ": " + "[white]" + (1.5f * (healTime + reloadRest) / (mirror ? 2 : 1)) + " " + StatUnit.seconds.localized());
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        Unit out = Units.closest(unit.team, x, y, range, u -> u != unit && u.damaged());
        if(out != null || !targetBuildings) return out;
        return Units.findAllyTile(unit.team, x, y, range, Building::damaged);
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        return !(target.within(unit, range + unit.hitSize/2) && target.team() == unit.team && (target instanceof Healthc && ((Healthc)target).damaged() && ((Healthc)target).isValid()));
    }
}
