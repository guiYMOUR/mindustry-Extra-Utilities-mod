package ExtraUtilities.worlds.blocks.turret;

import arc.util.Time;
import mindustry.logic.LAccess;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class LoadTurret extends PowerTurret {
    public float loadSpeed = -1.5f;
    public float loadReload = 60;
    public boolean logicCtrlAble = false;

    public LoadTurret(String name) {
        super(name);
        playerControllable = false;
    }

    public class LoadTurretBuild extends PowerTurretBuild{
        public float reRot = loadReload;

        @Override
        public void updateTile() {
            super.updateTile();
            if(power.status < 0.01f) return;
            if(target != null || logicShooting || playerControllable) reRot = 0;
            if(target == null && (reRot += Time.delta) > loadReload) rotation += Time.delta * loadSpeed * power.status;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if(logicCtrlAble) super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4) {
            if(logicCtrlAble) super.control(type, p1, p2, p3, p4);
        }
    }
}
