package ExtraUtilities.worlds.entity.weapon;

import arc.math.Angles;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;

public class ReRotRepairBeamWeapon extends RepairBeamWeapon {
    public float reRotateTime = 180f;

    public ReRotRepairBeamWeapon(String name){
        super(name);
        mountType = ReRotRepairBeamMount::new;
    }

    @Override
    public void update(Unit unit, WeaponMount m) {
        super.update(unit, m);
        float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
        ReRotRepairBeamMount mount = (ReRotRepairBeamMount) m;
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

    public static class ReRotRepairBeamMount extends HealBeamMount{
        public float reRotate;

        public ReRotRepairBeamMount(Weapon weapon) {
            super(weapon);
        }
    }
}
