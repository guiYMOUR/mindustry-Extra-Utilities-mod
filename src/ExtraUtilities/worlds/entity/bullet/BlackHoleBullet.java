package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.graphics.MainRenderer;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.Version;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.type.unit.MissileUnitType;

public class BlackHoleBullet extends BulletType {
    public float inRad, outRad, rotateSpeed;
    public int minLength = 13, midLength = 18, maxLength = 25;
    public float minWidth = 0.9f, maxWidth = 2.2f;
    public float minSpeed = 0.5f, midSpeed = 0.7f, maxSpeed = 1.8f;

    public float impulse = 6.67f * 1e-1f;

    //吸积盘颜色
    public Color accColor = Color.valueOf("B778FF");
    //吸积盘基数
    public int amount = 2;

    public BlackHoleBullet(){
        speed = 0f;
        damage = 0;
        lifetime = 300;
        keepVelocity = collides = collidesGround = collidesAir = absorbable = hittable = false;

        despawnEffect = healEffect = Fx.none;
    }

    @Override
    public void draw(Bullet b) {
        float in = b.time <= b.lifetime - 72 ?
                Math.min(b.time/60f, 1) :
                (b.lifetime - b.time)/72f;
        in = Interp.fastSlow.apply(in);

//        if(Version.build != 146) {
//            Draw.color(Tmp.c2.set(Color.black).a(Math.min(1, in + 0.1f)));
//            Fill.circle(b.x, b.y, inRad * in * 1.5f);
//            Draw.color();
//        } else 
        MainRenderer.addBlackHole(b.x, b.y, inRad * in, outRad * in, Math.min(1, in + 0.1f));

        super.draw(b);
    }

    @Override
    public void drawLight(Bullet b) {
        //no light
    }

    @Override
    public void update(Bullet b) {
        float in = b.time <= b.lifetime - 72 ?
                Math.min(b.time/60f, 1) :
                (b.lifetime - b.time)/72f;
        in = Interp.fastSlow.apply(in);

        float finalIn = in;
        Units.nearbyEnemies(b.team, b.x, b.y, outRad * 2, e -> {
            if(e != null && e.targetable(b.team)) {
                float p = F(b.x, b.y, e.x, e.y, impulse * Math.abs(e.mass() + 1f), outRad * 2);
                if(e.type instanceof MissileUnitType) p = F(b.x, b.y, e.x, e.y, impulse * 2000, outRad * 2);
                e.impulseNet(Tmp.v3.set(e).sub(b).nor().scl(-p * Time.delta * finalIn));

                e.vel.limit(5);
            }
        });

        Groups.bullet.intersect(b.x - outRad * 2, b.y - outRad * 2, outRad * 4, outRad * 4, bullet -> {
            if(bullet != null && bullet.within(b, outRad * 2) && bullet.team != b.team && bullet.type != null && (bullet.type.absorbable || bullet.type.hittable)){
                float p = F(b.x, b.y, bullet.x, bullet.y, impulse * 10, outRad * 2);
                Vec2 v = Tmp.v4.set(bullet).sub(b).nor().scl(-p * Time.delta * finalIn);
                bullet.vel.add(v.x, v.y);
                bullet.vel.limit(5);
                if(bullet.within(b, inRad)) {
                    bullet.damage = 0;
                    bullet.remove();
                }
            }
        });

        if(!Vars.headless && (Core.settings != null && Core.settings.getBool("eu-show-hole-acc-disk")) && b.time <= b.lifetime - 72) for(int i = 0; i < amount; i++){
            var data = Pools.obtain(EUFx.ateData.class, EUFx.ateData::new);
            float outRDI = i % 2 == 0 ? outRad * 1.2f : outRad;
            data.width = Mathf.random(minWidth, maxWidth) * in;
            data.inRad = inRad * 0.9f * in;
            data.outRad = Math.max(data.inRad, Mathf.random(inRad * 1.1f, outRDI) * in);
            data.speed = data.outRad > inRad * 1.5f ? Mathf.random(minSpeed, midSpeed) : Mathf.random(midSpeed * 2f, maxSpeed);
            data.length = data.speed < midSpeed ? Mathf.random(midLength, maxLength) : Mathf.random(minLength, midLength);
            data.owner = b;
            if(i % 2 == 0) data.out = true;
            EUFx.AccretionDiskEffect.at(
                    b.x,
                    b.y,
                    0, accColor, data);
        }
    }

    public float F(float x, float y, float tx, float ty, float G, float r) {
        float dst = Mathf.dst(x, y, tx, ty);
        float ptr = 1 - dst/r;
        return G * ptr;
    }
}
