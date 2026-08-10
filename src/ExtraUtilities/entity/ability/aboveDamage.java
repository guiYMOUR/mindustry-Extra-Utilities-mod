package ExtraUtilities.worlds.entity.ability;

import arc.func.Cons;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class aboveDamage extends Ability {
    public float range = 100 * 8;

    public aboveDamage(){
        display = false;
    }

    private static float realRad;
    private static Unit paramUnit;
    private static final Cons<Bullet> consumer = b -> {
        if(b.team != paramUnit.team && b.within(paramUnit, realRad/1.8f)){
            if(b.owner instanceof Teamc tc){
                if(!tc.within(paramUnit, realRad)){
                    b.damage = 0;
                    b.remove();
                }
            }
            if(b.owner instanceof Bullet bt){
                if(!bt.within(paramUnit, realRad)){
                    b.damage = 0;
                    b.remove();
                    bt.remove();
                }
            }
        }
    };

    @Override
    public void update(Unit unit) {
        realRad = range;
        paramUnit = unit;

        Groups.bullet.intersect(unit.x - realRad, unit.y - realRad, realRad * 2f, realRad * 2f, consumer);
    }
}
