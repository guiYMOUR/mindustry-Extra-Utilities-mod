package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.net.EUCall;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Scaled;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.blocks.ControlBlock;

/** 真的真的真的看不惯猫的那套单位制导技术*/
public class CtrlMissile extends BulletType {
    public String sprite;
    public float width, height;
    //public float x, y;
    public boolean autoHoming = false;
    public boolean low = false;
    //public boolean flipSprite = false;
    public Sound loopSound = Sounds.missileTrail;
    public float loopSoundVolume = 0.1f;
    //public Seq<DrawPart> parts = new Seq<>(DrawPart.class);

    public CtrlMissile(String sprite, float width, float height) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        homingPower = 2.5f;
        homingRange = 8 * 8;
        trailWidth = 3;
        trailLength = 7;
        lifetime = 60 * 1.7f;
        buildingDamageMultiplier = 0.8f;
        hitSound = despawnSound = Sounds.bang;
        absorbable = false;
        keepVelocity = false;
        //必要的
        reflectable = false;
    }

    public void lookAt(float angle, Bullet b) {
        b.rotation(Angles.moveToward(b.rotation(), angle, homingPower * Time.delta));
    }

    public void lookAt(Position pos, Bullet b) {
        lookAt(b.angleTo(pos), b);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        //updateSound();
        if (!Vars.headless && loopSound != Sounds.none) {
            Vars.control.sound.loop(loopSound, b, loopSoundVolume);
        }
    }

    @Override
    public void updateHoming(Bullet b) {
        //autoHoming时候优先寻找目标，无目标时依然会跟随瞄准方向
        if (homingPower > 0.0001f && b.time >= homingDelay) {
            float realAimX = b.aimX < 0 ? b.data instanceof Position ? ((Position) b.data).getX() : b.x : b.aimX;
            float realAimY = b.aimY < 0 ? b.data instanceof Position ? ((Position) b.data).getY() : b.y : b.aimY;

            Teamc target;
            if (heals()) {
                target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                        e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                        t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                );
            } else {
                if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                    target = b.aimTile.build;
                } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, homingRange, e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id), t -> collidesGround && !b.hasCollided(t.id));
                }
            }

            if(reflectable) return;
            if (target != null && autoHoming) {
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta));
            } else {
                @Nullable Unit shooter = null;
                if(b.owner instanceof Unit) shooter = (Unit)b.owner;
                if(b.owner instanceof ControlBlock) shooter = ((ControlBlock)b.owner).unit();
                if (shooter != null) {
                    //if(!Vars.net.client() || shooter.isPlayer()) lookAt(EUGet.pos(shooter.aimX, shooter.aimY), b);
                    if(shooter.isPlayer()) lookAt(EUGet.pos(shooter.aimX, shooter.aimY), b);
                    else {
                        if (b.data instanceof Position)
                            lookAt((Position) b.data, b);
                        else
                            lookAt(EUGet.pos(realAimX, realAimY), b);
                    }
                }
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        Draw.z(low ? Layer.flyingUnitLow : Layer.flyingUnit);
        if (width > 0 && height > 0) Draw.rect(Core.atlas.find(sprite), b.x, b.y, width, height, b.rotation() - 90);
        else Draw.rect(Core.atlas.find(sprite), b.x, b.y, b.rotation() - 90);

        //555,明明我先来的（
//        if(parts.size > 0){
//            for(int i = 0; i < parts.size; i++){
//                DrawPart part = parts.get(i);
//                DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, b.x, b.y, b.rotation());
//
//                DrawPart.params.life = b.fin();
//
//                part.draw(DrawPart.params);
//            }
//        }
        Draw.reset();
    }
}
