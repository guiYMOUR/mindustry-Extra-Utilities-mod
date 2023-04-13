package ExtraUtilities.worlds.blocks.turret.wall;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUItems;
import ExtraUtilities.net.EUCall;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.scene.ui.layout.Table;
import arc.struct.Bits;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;
import mindustry.ctype.Content;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.BuildPlan;
import mindustry.entities.units.UnitController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;

import java.nio.FloatBuffer;

import static mindustry.Vars.net;

public class ReleaseShieldWall extends Wall {
    public float chargeChance = 0.8f;
    public float maxHandle = 180;
    public float lifetime = 150;

    public ReleaseShieldWall(String name) {
        super(name);
        update = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("charge", (ReleaseShieldWallBuild entity) ->
             new Bar(() ->
                     Core.bundle.get("bar.extra-utilities-charge"),
                     () -> EUItems.lightninAlloy.color,
                     entity::getCharge
             )
        );
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.add(Stat.abilities, Core.bundle.format("stat.extra-utilities-charge", maxHandle, chargeChance * 100));
    }

    public static void setDamage(Tile tile, float damage){
        if(tile == null || !(tile.build instanceof  ReleaseShieldWallBuild)) return;
        ((ReleaseShieldWallBuild) tile.build).setDamage(damage);
    }

    public class ReleaseShieldWallBuild extends WallBuild{
        public float totalDamage = 0;
        public float clientDamage = 0;
        public float shieldLife = 0;
        public Bullet shieldBullet = null;
        public boolean acceptDamage = true;
        public float rePacketTimer = 0;

        public float getCharge(){
            return (net.client() ? clientDamage : totalDamage) / maxHandle;
        }

        @Override
        public void updateTile() {
            rePacketTimer = Math.min(rePacketTimer + Time.delta, 60);
            //用于逻辑显示屏显示，显示接口为timeScale
            timeScale = getCharge();
            if(totalDamage > maxHandle){
                EUCall.ReleaseShieldWallBuildSync(tile, totalDamage);
                shieldBullet = new ShieldBullet(size * 64).create(this.tile.build, this.team, this.x, this.y, 0);
                shieldLife = lifetime;
                acceptDamage = false;
                totalDamage = 0;
                clientDamage = 0;
            }
            if(shieldLife > 0){
                if(shieldBullet != null){
                    shieldBullet.set(this.x, this.y);
                    shieldBullet.time = 0;
                }
                shieldLife -= Time.delta;
            } else {
                shieldBullet = null;
                acceptDamage = true;
            }
        }

        @Override
        public void damage(float damage) {
            super.damage(damage);
            if(acceptDamage){
                if(!net.client()) {
                    if (Mathf.chance(chargeChance)) totalDamage += damage;
//                    if(rePacketTimer >= 60){//最多每秒发一次同步包
//                        EUCall.ReleaseShieldWallBuildSync(tile, totalDamage);
//                        rePacketTimer = 0;
//                    }
                } else {
                    if (Mathf.chance(chargeChance)) clientDamage += damage;
                }
            }
        }

        public void setDamage(float v){
            if(net.client()) {
                totalDamage = v;
                //clientDamage = v;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(totalDamage);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            totalDamage = read.f();
        }
    }
    public static class ShieldBullet extends BulletType{
        public float range;
        public Effect openEffect;

        public ShieldBullet(float range){
            this.range = range;
            openEffect = new Effect(35, e -> {
                Draw.color(e.color);
                Lines.stroke(e.fout() * 4);
                Lines.poly(e.x, e.y, 6, range * 0.525f + 75 * e.fin());
            });
            hittable = false;
            absorbable = false;
            hitEffect = despawnEffect = Fx.none;
            lifetime = 60;
            speed = damage = 0;
            collides = false;
            collidesAir = false;
            collidesGround = false;
            keepVelocity = false;
            reflectable = false;
        }

        @Override
        public void update(Bullet b) {
            float realRange = range * b.fout();
            Groups.bullet.intersect(b.x - realRange, b.y - realRange, realRange * 2, realRange * 2, trait ->{
                if(trait.type.absorbable && trait.team != b.team && Intersector.isInsideHexagon(trait.getX(), trait.getY(), realRange, b.x, b.y) ){
                    trait.absorb();
                    EUFx.shieldDefense.at(trait);
                }
            });
        }

        @Override
        public void init(Bullet b) {
            if(b == null) return;
            openEffect.at(b.x, b.y, b.fout(), EUItems.lightninAlloy.color);
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(EUItems.lightninAlloy.color);
            float fout = Math.min(b.fout(), 0.5f) *2;
            Lines.stroke(fout * 3);
            Lines.poly(b.x, b.y, 6, (range * 0.525f) * fout * fout);
//            Draw.alpha(fout * fout * 0.15f);
//            Fill.poly(b.x, b.y, 6, (this.splashDamageRadius * 0.525f) * fout * fout);
        }
    }
}
