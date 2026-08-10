package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.worlds.blocks.liquid.LiquidMassDriver.*;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class LiquidMassDriverBolt extends BulletType {
    public LiquidMassDriverBolt(){
        super(1f, 75);
        collidesTiles = false;
        lifetime = 1f;
        despawnEffect = Fx.smeltsmoke;
        hitEffect = Fx.hitBulletBig;
    }

    @Override
    public void draw(Bullet b) {
        if(!(b.data() instanceof LiquidBulletData data)) return;

        float mid = 100;//阈值

        float p = Math.min((int)(data.amount/mid) + 1, 3);

        float w = 4 * p, h = 5 * p;

        Draw.color(data.liquid.color);
        Draw.rect("shell-back", b.x, b.y, w, h, b.rotation() + 90);
        Draw.rect("shell", b.x, b.y, w, h, b.rotation() + 90);
        Draw.reset();
    }

    @Override
    public void update(Bullet b) {
        if(!(b.data() instanceof LiquidBulletData data)){
            hit(b);
            return;
        }

        float hitDst = 7f;

        if(data.to.dead()){
            return;
        }

        float baseDst = data.from.dst(data.to);
        float dst1 = b.dst(data.from);
        float dst2 = b.dst(data.to);

        boolean intersect = false;

        if(dst1 > baseDst){
            float angleTo = b.angleTo(data.to);
            float baseAngle = data.to.angleTo(data.from);

            if(Angles.near(angleTo, baseAngle, 2f)){
                intersect = true;

                b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
            }
        }

        if(Math.abs(dst1 + dst2 - baseDst) < 4f && dst2 <= hitDst){
            intersect = true;
        }

        if(intersect){
            data.to.handlePayload(b, data);
        }
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        if(!(b.data() instanceof LiquidBulletData data)) return;
        Fx.hitLiquid.at(b.x, b.y, b.rotation(), data.liquid.color);
    }

    @Override
    public void hit(Bullet b, float x, float y, boolean createFrags) {
        super.hit(b, x, y, createFrags);
        despawned(b);
    }
}

