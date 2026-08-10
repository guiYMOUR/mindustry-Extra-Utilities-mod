package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.graphics.Pal;

public class diffBullet extends BulletType {
    public float cont;

    public int damType;

    public Color color = Pal.accent;

    public boolean pfin = true;

    public diffBullet(float cont, int damType){
        this.cont = cont/2;
        this.damType = damType;

        collides = collidesAir = collidesGround = collidesTiles = absorbable = hittable = keepVelocity = false;
        despawnEffect = hitEffect = Fx.none;

        speed = 0;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if(b instanceof diffEnt d) {
            float r = splashDamageRadius * (1 - b.foutpow());
            Vars.indexer.allBuildings(b.x, b.y, r, bd -> {
                if (bd.team != b.team && bd.block != null && bd.block.targetable && Angles.within(b.rotation(), b.angleTo(bd), cont))
                    d.seq.addUnique(bd);
            });
            Units.nearbyEnemies(b.team, b.x - r, b.y - r, r * 2, r * 2, u -> {
                if (u.type != null && u.type.targetable && b.within(u, r) && Angles.within(b.rotation(), b.angleTo(u), cont))
                    d.seq.addUnique(u);
            });
            for (int i = 0; i < d.seq.size; i++) {
                Healthc hc = d.seq.get(i);
                if (hc != null && !hc.dead()) {
                    if (!b.hasCollided(hc.id())) {

                        switch (damType) {
                            case 1:
                                hc.damage(damage);
                                break;
                            case 2:
                                hc.damagePierce(damage);
                                break;
                            case 3:
                                if (hc.health() <= damage) hc.kill();
                                else hc.health(hc.health() - damage);
                                break;
                        }
                        EUFx.diffHit.at(hc.getX(), hc.getY(), 0, color, hc);
                        b.collided.add(hc.id());
                    }
                }
            }
        }
    }
    @Override
    public void draw(Bullet b) {
        super.draw(b);
        float pin = (1 - b.foutpow());
        Lines.stroke(5 * (pfin ? pin : b.foutpow()), color);

        for(float i = b.rotation() - cont; i < b.rotation() + cont; i++){
            float lx = EUGet.dx(b.x, splashDamageRadius * pin, i);
            float ly = EUGet.dy(b.y, splashDamageRadius * pin, i);
            Lines.lineAngle(lx, ly, i - 90, splashDamageRadius/(cont * 2) * pin);
            Lines.lineAngle(lx, ly, i + 90, splashDamageRadius/(cont * 2) * pin);
        }
    }

    @Override
    public @Nullable
    Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ){
        diffEnt bullet = diffEnt.create();
        if(bullet.seq.size > 0) bullet.seq.clear();
        return EUGet.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
    }

    public static class diffEnt extends Bullet{
        public Seq<Healthc> seq = new Seq<>();

        public static diffEnt create(){
            return Pools.obtain(diffEnt.class, diffEnt::new);
        }
    }
}
