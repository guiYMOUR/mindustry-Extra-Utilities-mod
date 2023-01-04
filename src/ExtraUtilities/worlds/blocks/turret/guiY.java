package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUGet;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

/**unfinished(总之隐藏了先awa)*/

public class guiY extends ItemTurret {
    public guiY(String name) {
        super(name);
    }
//    public void ammo(Object... objects){
//        ammoTypes = ObjectMap.of(objects);
//    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.ammo, StatValues.ammo(ammoTypes));
    }
    public class Author extends ItemTurretBuild{
        @Override
        protected void updateShooting(){
            if(reloadCounter >= 6 && !charging() && shootWarmup >= minWarmup){
                //for(int i = 0; i < 4; i++) {
                    int j = Mathf.random(EUGet.bulletTypes().size - 1);
                    BulletType type = EUGet.bulletTypes().get(j);
                    shoot(type);
                //}
                reloadCounter %= 6;
            }
        }

        @Override
        public BulletType useAmmo() {
            return null;
        }
    }
}
