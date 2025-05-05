package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUFx;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Interval;
import mindustry.content.Fx;
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
            if((reloadCounter >= reload && !charging() && shootWarmup >= minWarmup) || shootAt > 0){
                BulletType type = peekAmmo();

                if(shootAt > 2) {
                    reloadCounter %= reload;
                    shootAt = 0;
                }
                if (shootAt >= 0 && timerTik.get(30/((timeScale * baseReloadSpeed()) + 1e-4f))){
                    for(int i = 0; i < 5; i++) EUFx.casing4Double.at(
                            x - Angles.trnsx(rotation, ammoEjectBack),
                            y - Angles.trnsy(rotation, ammoEjectBack),
                            rotation
                    );
                    rotation = angleTo(targetPos) + 18 * shootAng[shootAt%3];
                    shoot(type);
                    shootAt ++;
                }
            }
        }
    }
}
