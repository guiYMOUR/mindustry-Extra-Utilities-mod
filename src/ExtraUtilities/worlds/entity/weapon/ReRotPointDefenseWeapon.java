package ExtraUtilities.worlds.entity.weapon;

import ExtraUtilities.worlds.entity.weapon.mounts.reRotMount;
import arc.math.Angles;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.weapons.PointDefenseWeapon;

public class ReRotPointDefenseWeapon extends PointDefenseWeapon {
    public float reRotateTime = 180;

    public ReRotPointDefenseWeapon(String name){
        super(name);
        mountType = reRotMount::new;
    }

    @Override
    public void update(Unit unit, WeaponMount m) {
        super.update(unit, m);
        float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
        reRotMount mount = (reRotMount) m;
        if(mount.target != null) {
            mount.reRotate = reRotateTime;
        } else {
            mount.reRotate = Math.max(mount.reRotate - Time.delta, 0f);
        }

        if(mount.target == null && !mount.shoot && !Angles.within(mount.rotation, mount.weapon.baseRotation, 0.01f) && mount.reRotate <= 0){
            mount.rotate = true;
            Tmp.v1.trns(unit.rotation + mount.weapon.baseRotation, 5f);
            mount.aimX = mountX + Tmp.v1.x;
            mount.aimY = mountY + Tmp.v1.y;
        }
    }
}
