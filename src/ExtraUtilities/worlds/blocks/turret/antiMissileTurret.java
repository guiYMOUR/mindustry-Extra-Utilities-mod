package ExtraUtilities.worlds.blocks.turret;

import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.logic.LAccess;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.Stat;

public class antiMissileTurret extends LoadTurret {

    public antiMissileTurret(String name) {
        super(name);
        predictTarget = false;
        targetInterval = 2;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.ammo);
        stats.remove(Stat.targetsAir);
        stats.remove(Stat.targetsGround);
    }

    public class antiMissileBuild extends LoadTurretBuild {
        @Override
        protected void findTarget() {
            float range = range();

            Teamc ta;

            ta = Units.closestEnemy(team, x, y, range, u -> u.type instanceof MissileUnitType);

            if(ta == null)
                ta = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != team && (b.type() != null && (b.type() instanceof MissileBulletType || b.type().homingPower > 0)), b -> b.dst2(x, y));


            target = ta;
        }
    }
}
