//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;

public class SurgeVolt extends BulletType {
    public float size;
    public int maxTarget = 10;
    public float targetRange = 40;
    public float voltDamageMp = 0.5f;
    public float voltTimer = 12;
    public Effect chainEffect;
    public Effect chainHit;
    public boolean useChainBullet;
    public ChainLightningFade chainBullet;
    public Color color;

    public boolean chainTile = false;

    public SurgeVolt(float damage, float size) {
        this.chainEffect = Fx.none;
        this.chainHit = Fx.none;
        this.useChainBullet = false;
        this.color = Pal.remove;
        this.damage = damage;
        this.size = size;
        this.lifetime = 90;
        this.speed = 5;
        this.pierce = true;
        this.chainEffect = EUFx.chainLightningFade(60, 8);
        this.chainHit = Fx.hitFuse;
    }

    public void update(Bullet b) {
        if (b instanceof volt v) {
            this.update(v);
        }

        super.update(b);
    }

    public void update(volt v) {
        v.targets.clear();
        Units.nearbyEnemies(v.team, v.x, v.y, this.targetRange, (u) -> {
            if (v.targets.size < this.maxTarget) {
                v.targets.add(u);
            }

        });
        Units.nearbyBuildings(v.x, v.y, this.targetRange, (b) -> {
            if (b.team != v.team && v.targets.size < this.maxTarget) {
                v.targets.add(b);
            }

        });
        if (v.timer.get(3, this.voltTimer)) {
            v.targets.each((t) -> {
                if (t instanceof Unit u) {
                    if (!u.dead && u.targetable(v.team) && !u.inFogTo(v.team)) {
                        if (this.useChainBullet && this.chainBullet != null) {
                            this.chainBullet.create(v, v.team, v.x, v.y, 0, -1, 1, 1, u);
                        } else {
                            if (this.status != StatusEffects.none) {
                                u.apply(this.status, this.statusDuration);
                            }

                            this.chainEffect.at(v.x, v.y, 0, this.color, u);
                            this.chainHit.at(u);
                            if (this.pierceArmor) {
                                u.damagePierce(this.damage * this.voltDamageMp);
                            } else {
                                u.damage(this.damage * this.voltDamageMp);
                            }
                        }
                    }
                }

                if (t instanceof Building b) {
                    if (!b.dead && b.block != null && b.block.targetable && !b.inFogTo(v.team)) {
                        if (this.useChainBullet && this.chainBullet != null) {
                            this.chainBullet.create(v, v.team, v.x, v.y, 0, -1, 1, 1, b);
                        } else {
                            this.chainEffect.at(v.x, v.y, 0, this.color, b);
                            this.chainHit.at(b);
                            if (this.pierceArmor) {
                                b.damagePierce(this.damage * this.voltDamageMp * this.buildingDamageMultiplier);
                            } else {
                                b.damage(this.damage * this.voltDamageMp * this.buildingDamageMultiplier);
                            }
                        }
                    }
                }
            });

            if(chainTile && chainBullet != null){
                for(int i = 0; i < 3; i++){
                    float rx = Mathf.random(-32, 32);
                    float ry = Mathf.random(-32, 32);
                    chainBullet.create(v, v.team, v.x + rx, v.y + ry, 0, -1, 1, 1, v);
                }
            }
        }

    }

    public void draw(Bullet b) {
        super.draw(b);
        float z = Draw.z();
        Draw.z(102);
        Draw.color(this.color);
        Fill.circle(b.x, b.y, this.size);
        Draw.z(z);
    }

    @Nullable
    public Bullet create(@Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target) {
        volt bullet = SurgeVolt.volt.create();
        if (bullet.targets.size > 0) {
            bullet.targets.clear();
        }

        return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public static class volt extends Bullet {
        public Seq<Teamc> targets = new Seq<>();

        public static volt create() {
            return (volt)Pools.obtain(volt.class, volt::new);
        }
    }
}
