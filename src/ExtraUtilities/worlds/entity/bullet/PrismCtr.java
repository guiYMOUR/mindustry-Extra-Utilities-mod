package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;

public class PrismCtr extends BulletType {
    public float angleShiftStrength = 5;
    public float shiftAngel = 30;
    public int las = 6;
    public float checkRange = 39 * 8;
    public boolean tgAuto = true;

    public Color dc1 = Color.valueOf("87CEEB");

    //public BulletType laser = new PrismLaser(40, 12, checkRange);
    public BulletType laser = new liContinuousLaserBullet(){{
        damage = 40;
        buildingDamageMultiplier = 0.5f;
        lifetime = 15f;
        chain = new ChainLightningFade(12, -1, 5, Color.valueOf("87CEEB"), 30, Fx.hitLancer);
        colors = new Color[]{Color.valueOf("87CEEB").a(0.4f), Color.valueOf("87CEEB").a(0.7f), Color.valueOf("87CEEB")};
        width = 4;
        length = checkRange + 24;
        lTime = 18;
        spacing = 6;
        incendChance = -1;
        incendAmount = 0;
        status = StatusEffects.shocked;
        hitEffect = despawnEffect = Fx.none;
        pierceArmor = true;
    }};

    public PrismCtr(){
        speed = damage = 0;
        lifetime = 180;
        hitEffect = despawnEffect = Fx.none;
        collides = collidesTiles = absorbable = hittable = keepVelocity = false;
    }

    @Override
    public void init() {
        super.init();
        if(laser instanceof PrismLaser pl) pl.plan = 3;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        Teamc tc = Units.closestTarget(b.team, b.x, b.y, checkRange, u -> u.checkTarget(collidesAir, collidesGround) && u.targetable(b.team));
        if(b.owner instanceof Unit u){
            b.rotation(tc != null ? b.angleTo(tc) : b.angleTo(u.mounts[0].aimX, u.mounts[0].aimY));
        }

        if(b instanceof ctr c){
            for(int i = 0; i < las; i++){
                Bullet be = laser.create(b, b.team, b.x, b.y, 0);
                be.data = 360/las * i;
                c.bs.add(be);
            }
        }
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if(tgAuto){
            Teamc tc = Units.closestTarget(b.team, b.x, b.y, checkRange, u -> u.checkTarget(collidesAir, collidesGround) && u.targetable(b.team));
            if(b.owner instanceof Unit u){
                float acg = tc != null ? b.angleTo(tc) : b.angleTo(u.mounts[0].aimX, u.mounts[0].aimY);
                b.rotation(Angles.moveToward(b.rotation(), acg, 0.8f * Time.delta));
            }
        }

        float fin = Mathf.curve(b.fin(), 0, 0.6f);
        if(b instanceof ctr c){
            for(int i = 0; i < c.bs.size; i++){
                if(c.bs.get(i) != null){
                    int data = (i * (360 / c.bs.size));
                    float sine = Mathf.sinDeg(data + (b.time * (angleShiftStrength * (1 - Mathf.pow(b.fout(), 3)))));
                    c.vec.set(sine * 2f, 0).rotate(b.rotation() - 90);
                    c.bs.get(i).rotation(b.rotation() + ((sine * shiftAngel) * (1 - fin)));
                    c.bs.get(i).set(b.x + c.vec.x, b.y + c.vec.y);
                    c.bs.get(i).time = 0;
                }
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        Draw.color(dc1);
        Fill.circle(b.x, b.y, 12);
        for(int i : Mathf.zeroOne){
            Drawf.tri(b.x, b.y, 12, 30, b.rotation() - 90 + 180 * i);
        }
        Draw.color(Color.black);
        Fill.circle(b.x, b.y, 8);
    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        Bullet bullet = ctr.create();
        return EUGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class ctr extends Bullet{
        public Seq<Bullet> bs = new Seq<>();
        public Vec2 vec = new Vec2();

        public static ctr create() {
            return Pools.obtain(ctr.class, ctr::new);
        }

        @Override
        public void remove() {
            super.remove();
            bs.clear();
        }
    }
}
