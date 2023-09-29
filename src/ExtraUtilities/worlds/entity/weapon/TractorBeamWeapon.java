package ExtraUtilities.worlds.entity.weapon;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.worlds.entity.weapon.mounts.reRotMount;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class TractorBeamWeapon extends Weapon {
    public Color color = Color.white;

    public float shootSoundVolume = 0.8f;

    public float force = 18, scaledForce = 6, laserWidth = 0.45f;

    public boolean trueDamage = false;

    public float reRotateTime = 180;

    public TractorBeamWeapon(String name){
        super(name);
        shootSound = Sounds.tractorbeam;
        predictTarget = false;
        autoTarget = true;
        controllable = false;
        rotate = true;
        rotateSpeed = 10;
        useAmmo = false;
        useAttackRange = false;
        targetInterval = 5;
        targetSwitchInterval = 5;
        mountType = TractorBeamWeaponMount::new;
    }

    public TractorBeamWeapon(){
        super(null);
    }

    @Override
    public void addStats(UnitType u, Table t) {
        if(bullet.damage > 0){
            t.row();
            t.add(Stat.damage.localized() + "[accent]" + bullet.damage * 60 + "[]" + StatUnit.perSecond.localized());
        }
        t.row();
        t.add(Stat.range.localized() + "[accent]" + bullet.range/8 + "[]" + StatUnit.blocks.localized());
    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground){
        return Units.closestEnemy(unit.team, x, y, range, u -> u.checkTarget(air, ground));
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        return !(target instanceof Unit && ((Unit) target).type != null && target.within(EUGet.pos(x, y), range + ((Unit) target).hitSize / 2f) && target.team() != unit.team && !((Unit) target).dead);
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {

    }

    @Override
    public void update(Unit unit, WeaponMount m) {
        super.update(unit, m);

        float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

        TractorBeamWeaponMount mount = (TractorBeamWeaponMount) m;

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

        mount.any = false;

        if(mount.shoot && mount.target != null){
            if(!headless){
                control.sound.loop(shootSound, EUGet.pos(mountX, mountY), shootSoundVolume);
            }
            Unit target = (Unit) mount.target;
            mount.lastX = target.x;
            mount.lastY = target.y;
            mount.strength = Mathf.lerpDelta(mount.strength, 1f, 0.1f);

            if(bullet.damage > 0){
                if(trueDamage) target.damageContinuousPierce(bullet.damage * state.rules.unitDamage(unit.team));
                else target.damageContinuous(bullet.damage * state.rules.unitDamage(unit.team));
            }

            if(bullet.status != StatusEffects.none){
                target.apply(bullet.status, bullet.statusDuration);
            }

            mount.any = true;
            target.impulseNet(Tmp.v1.set(EUGet.pos(mountX, mountY)).sub(target).limit((force + (1f - target.dst(EUGet.pos(mountX, mountY)) / bullet.range) * scaledForce)));
        }else{
            mount.strength = Mathf.lerpDelta(mount.strength, 0, 0.1f);
        }
        
    }

    @Override
    public void draw(Unit unit, WeaponMount m) {
        super.draw(unit, m);
        TractorBeamWeaponMount mount = (TractorBeamWeaponMount) m;
        float  mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);
        float z = Draw.z();
        if(mount.any){
            Draw.z(Layer.bullet);
            float ang = EUGet.pos(mountX, mountY).angleTo(mount.lastX, mount.lastY);

            Draw.mixcol(color, Mathf.absin(4f, 0.6f));

            TractorBeamTurret t = ((TractorBeamTurret) Blocks.parallax);
            Drawf.laser(t.laser, t.laserStart, t.laserEnd,
                    mountX + Angles.trnsx(ang, shootY), mountY + Angles.trnsy(ang, shootY),
                    mount.lastX, mount.lastY, mount.strength * laserWidth);

            Draw.mixcol();
        }
        Draw.z(z);
        Draw.reset();
    }

    public static class TractorBeamWeaponMount extends reRotMount {
        public boolean any;
        public float lastX, lastY, strength;

        public TractorBeamWeaponMount(Weapon weapon) {
            super(weapon);
        }
    }
}
