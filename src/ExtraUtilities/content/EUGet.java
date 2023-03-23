package ExtraUtilities.content;

import arc.math.geom.Position;
import arc.struct.Seq;
import mindustry.entities.bullet.BulletType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;

import static mindustry.Vars.*;

/**unfinished*/

public class EUGet {

    public static Position pos(float x, float y){
        return new Position() {
            @Override
            public float getX() {
                return x;
            }

            @Override
            public float getY() {
                return y;
            }
        };
    }

    public static float dx(float px, float r, float angel){
        return px + r * (float) Math.cos(angel * Math.PI/180);
    }

    public static float dy(float py, float r, float angel){
        return py + r * (float) Math.sin(angel * Math.PI/180);
    }

    public static Seq<Turret> turrets(){
        Seq<Turret> turretSeq = new Seq<>();
        int size = content.blocks().size;
        for(int i = 0; i < size; i++){
            Block b = content.block(i);
            if(b instanceof Turret){
                Turret t = (Turret) b;
                turretSeq.addUnique(t);
            }
        }
        return turretSeq;
    }

    /**turret and unit only, not use contents.bullets()*/
    public static Seq<BulletType> bulletTypes(){
        Seq<BulletType> bullets = new Seq<>();
        for(Turret t : turrets()){
            if(t instanceof ItemTurret){

            }
        }
//            if(b == null || b.killShooter || b instanceof MassDriverBolt) continue;
//            if(b instanceof ContinuousBulletType)
//                b.lifetime = Math.max(b.lifetime, 90);
        return bullets;
    }
}
