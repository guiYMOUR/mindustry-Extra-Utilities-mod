package ExtraUtilities.worlds.blocks.turret;

import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class BuffUpTurret extends ItemTurret {
    public StatusEffect buff = StatusEffects.none;
    public float upTo = 0.2f;

    public BuffUpTurret(String name) {
        super(name);
    }

    public class BuffUpBuild extends ItemTurretBuild{
        int buffUnit;


        @Override
        protected float baseReloadSpeed() {
            buffUnit = 0;
            if(buff != StatusEffects.none) {
                Units.nearbyEnemies(team, x, y, range(), u -> {
                    if (u.hasEffect(buff)) buffUnit++;
                });
            }

            return efficiency * (1 + buffUnit * upTo);
        }
    }
}
