package ExtraUtilities.content;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.geom.Position;
import arc.scene.style.Drawable;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.storage.CoreBlock;

import static arc.Core.*;
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
