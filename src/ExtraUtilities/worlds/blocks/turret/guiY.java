package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

import static ExtraUtilities.content.EUGet.sandBox;

/**unfinished(总之隐藏了先awa)*/

public class guiY extends ItemTurret {

    public guiY(String name) {
        super(name);
    }

    public class Author extends ItemTurretBuild{

        @Override
        protected void updateShooting(){
            if(sandBox) {
                if (reloadCounter >= 6 && !charging() && shootWarmup >= minWarmup) {
                    int j = Mathf.random(EUGet.bulletTypes().size - 1);
                    BulletType type = EUGet.bulletTypes().get(j);
                    shoot(type);
                    reloadCounter %= 6;
                }
            } else super.updateShooting();
        }

        @Override
        public BulletType useAmmo() {
            return null;
        }
    }
}
