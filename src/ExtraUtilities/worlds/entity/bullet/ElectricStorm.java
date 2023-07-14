package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class ElectricStorm extends BulletType {
    public Color color;
    public int maxTarget;
    public Sound sound = Sounds.spark;

    public ElectricStorm(float damage, Color color, int maxTarget){
        this.damage = damage;
        this.splashDamage = damage * 10;
        this.color = color;
        this.maxTarget = maxTarget;
        keepVelocity = absorbable = hittable = collides = collidesAir = collidesGround = false;
        despawnEffect = hitEffect = Fx.none;
        despawnSound = Sounds.explosionbig;
        speed = 0;
    }

    @Override
    public void update(Bullet b) {
        if(b.time >= b.lifetime - EUFx.chainLightningFade.lifetime) return;
        float baseRange = splashDamageRadius * 0.1f + splashDamageRadius * 0.9f * b.finpow();
        if(b.timer.get(lifetime/15f)) {
            Seq<Healthc> t = new Seq<>();
            Vars.indexer.allBuildings(b.x, b.y, baseRange, building -> {
                if (building.team != b.team && Mathf.chance(0.5f) && t.size < maxTarget) t.addUnique(building);
            });
            Units.nearbyEnemies(b.team, b.x, b.y, baseRange, unit -> {
                if (Mathf.chance(0.5f) && t.size < maxTarget) t.addUnique(unit);
            });
            t.removeAll(hc -> hc == null || hc.dead());
            if(t.size > 0) sound.at(b);
            for(int i = 0; i < t.size; i++){
                Healthc hc = t.get(i);
                if(hc != null){
                    EUFx.chainLightningFade.at(b.x, b.y, 4, color.cpy().a(0.3f), hc);
                    Fx.randLifeSpark.at(hc.getX(), hc.getY(), b.angleTo(hc), color);
                    if(hc instanceof Building){
                        ((Building) hc).applySlowdown(0, 300);
                    }
                    if(hc instanceof Unit){
                        ((Unit)hc).apply(StatusEffects.disarmed, 300);
                    }
                    hc.damage(damage * Vars.state.rules.unitCrashDamage(b.team));
                }
            }
        }
        Effect.shake(2, 2, b);
    }

    @Override
    public void draw(Bullet b) {
        float baseRange = splashDamageRadius * 0.1f + splashDamageRadius * 0.9f * b.finpow();

        Draw.color(color);
        Draw.z(Layer.flyingUnitLow - 0.1f);
        Draw.alpha(0.3f);
        Fill.circle(b.x, b.y, baseRange);

        Draw.z(Layer.bullet);
        Lines.stroke(splashDamageRadius/12 * b.finpow(), color);
        Lines.circle(b.x, b.y, baseRange);

        Draw.color(color);
        for(int i = 0; i < 2; i++){
            Drawf.tri(b.x, b.y, 12 * b.finpow(), baseRange * 1.5f, i * 180);
            Drawf.tri(b.x, b.y, 6 * b.finpow(), baseRange/3, i * 180 + 90);
        }

        if(b.timer.get(1, 27 * b.foutpow() + 3)){
            if(b.time >= b.lifetime - EUFx.chainLightningFade.lifetime) return;
            for(int i = 0; i < 3; i++) {
                float a = Mathf.random(360);
                float x = EUGet.dx(b.x, baseRange, a);
                float y = EUGet.dy(b.y, baseRange, a);
                EUFx.chainLightningFade.at(x, y, 8, color, b);
            }
        }
    }
}
