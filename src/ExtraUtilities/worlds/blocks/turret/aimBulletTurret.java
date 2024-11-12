package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUGet;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.logic.LAccess;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.logic.LogicBlock;

public class aimBulletTurret extends ItemTurret {
    public BulletType aimBullet;
    public boolean logicCtrlAble = false;

    public aimBulletTurret(String name) {
        super(name);
    }

    public class aimBulletBuild extends ItemTurretBuild{
        @Override
        protected void shoot(BulletType type) {
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

            if(shoot.firstShotDelay > 0){
                chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(bulletX, bulletY, rotation);
                float tx = within(targetPos, range) ? targetPos.x : EUGet.dx(x, range, angleTo(targetPos)),
                        ty = within(targetPos, range) ? targetPos.y : EUGet.dy(y, range, angleTo(targetPos));
                aimBullet.create(this, team, tx, ty, 0);
            } else return;

            shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets++;
                if(delay > 0f){
                    Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
                }else{
                    bullet(type, xOffset, yOffset, angle, mover);
                }
            }, () -> barrelCounter++);

            if(consumeAmmoOnce){
                useAmmo();
            }

        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if(logicCtrlAble) super.control(type,p1, p2, p3, p4);
            if (type == LAccess.enabled) {
                enabled = !Mathf.zero((float)p1);
            }
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4) {
            if(logicCtrlAble) super.control(type,p1, p2, p3, p4);
            if (type == LAccess.config && block.logicConfigurable && !(p1 instanceof LogicBlock.LogicBuild)) {
                configured(null, p1);
            }
        }
    }
}
