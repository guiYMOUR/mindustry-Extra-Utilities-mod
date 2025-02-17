package ExtraUtilities.worlds.blocks.turret;

import arc.math.Angles;
import arc.util.Interval;
import mindustry.entities.bullet.BulletType;

public class LingSha extends BuffUpTurret {


    public LingSha(String name) {
        super(name);
    }

    public class LingShaBuild extends BuffUpBuild{
        int shootAt = 0;
        Interval timerTik = new Interval(6);
        int[] shootAng = {-1, 1, 0};

        @Override
        public void updateTile() {
            if(shootAt > 0){
                wasShooting = true;
                updateShooting();
            }
            super.updateTile();
        }

        @Override
        protected void updateShooting() {
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                BulletType type = peekAmmo();

                if(shootAt > 2) {
                    reloadCounter %= reload;
                    shootAt = 0;
                }
                if (shootAt >= 0 && timerTik.get(30/((timeScale * baseReloadSpeed()) + 1e-4f))){
                    rotation = angleTo(targetPos) + 18 * shootAng[shootAt%3];
                    shoot(type);
                    shootAt ++;
                }
            }
        }
    }
}
