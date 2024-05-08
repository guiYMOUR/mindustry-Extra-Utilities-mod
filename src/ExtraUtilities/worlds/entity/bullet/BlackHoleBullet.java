package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.graphics.MainRenderer;
import arc.Core;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class BlackHoleBullet extends BulletType {
    public float inRad, outRad, rotateSpeed;
    public int minLength = 13, midLength = 18, maxLength = 25;
    public float minWidth = 0.9f, maxWidth = 2.2f;
    public float minSpeed = 0.5f, midSpeed = 0.7f, maxSpeed = 1.8f;

    //引力
    public float impulse = 1200;

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
            if(e.targetable(b.team)) {
                if (e.within(b, outRad)) {
                    float p = percent(b.x, b.y, e.x, e.y, outRad);
                    e.impulseNet(Tmp.v3.set(e).sub(b.x, b.y).nor().scl(-(impulse * p * Time.delta) * finalIn));
                } else {
                    float p = percent(b.x, b.y, e.x, e.y, outRad * 2);
                    e.impulseNet(Tmp.v3.set(e).sub(b.x, b.y).nor().scl(-(impulse / 2 * p * Time.delta) * finalIn));
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
                    0, Color.valueOf("B778FF"), data);
        }
    }

    public float percent(float x, float y, float tx, float ty, float radius) {
        float dst = Mathf.dst(x, y, tx, ty);
        float falloff = 0.2f;
        return Mathf.lerp(1 - dst / radius, 1, falloff);
    }
}
