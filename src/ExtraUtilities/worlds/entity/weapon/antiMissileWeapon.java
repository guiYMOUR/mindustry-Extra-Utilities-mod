package ExtraUtilities.worlds.entity.weapon;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.unit.MissileUnitType;

public class antiMissileWeapon extends BoostWeapon{
    public float loadSpeed;

    public antiMissileWeapon(String name){
        this.name  = name;
        predictTarget = false;
        controllable = false;
        autoTarget = true;
        useAmmo = true;
        rotate = true;
        rotateSpeed = 30;
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);
        t.row();
        t.add(Core.bundle.format("stat.extra-utilities-antiWeapon", bullet.range/ Vars.tilesize - 1));
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
        Teamc target;
        target = Units.closestEnemy(unit.team, x, y, range, u -> u.type instanceof MissileUnitType);
        if(target != null) return target;

        return Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != unit.team && (b.type() != null && (b.type() instanceof MissileBulletType || b.type().homingPower > 0)), b -> b.dst2(x, y));
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
        return !(target.within(unit, range) && target.team() != unit.team && ((target instanceof Bullet && ((Bullet)target).type != null) || (target instanceof Unit && ((Unit)target).type != null)));
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        if(mount.target == null){
            mount.rotation += Time.delta * loadSpeed;
        }
    }
}
