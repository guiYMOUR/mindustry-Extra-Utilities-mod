package ExtraUtilities.worlds.blocks.turret;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.*;

public class MultiShootTurret extends ItemTurret {
    /** 事实上这个是增加的子弹数 */
    public int perShoot = 3;
    public int shots = 1;
    public TextureRegion[] heats;

    public MultiShootTurret(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.reload);
        this.stats.add(Stat.reload, 60f / (reload) * (shoot.shots + perShoot), StatUnit.none);
    }

    public class MultiShootTurretBuild extends ItemTurretBuild {
        float[] perHeat = new float[shots];

//        @Override
//        protected void shoot(BulletType type) {
//            super.shoot(type);
//            float
//                    bulletX = x + Angles.trnsx(rotation - 90f, shootX, shootY),
//                    bulletY = y + Angles.trnsy(rotation - 90f, shootX, shootY);
//            float lif = Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range / type.range);
//            for(int i = 0; i < perShoot; i++)
//                type.create(this, team, bulletX, bulletY, rotation + Mathf.range(inaccuracy), 1 + Mathf.range(velocityRnd), lif);
//        }

        @Override
        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
            super.bullet(type, xOffset, yOffset, angleOffset, mover);
            float
                    bulletX = x + Angles.trnsx(rotation - 90f, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90f, shootX, shootY);
            for(int i = 0; i < perShoot; i++) {
                float shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);
                float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;
                handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            for (int i = 0; i < shots; i++)
                perHeat[i] = Mathf.approachDelta(perHeat[i], 0, 1 / cooldownTime);
        }

        @Override
        public void draw() {
            super.draw();
            float i = totalShots % shots;
            perHeat[(int) i] = heat;
            for (int c = 0; c < shots; c++) {
                if (perHeat[c] <= 0.00001) continue;
                Draw.color(heatColor, perHeat[c]);
                Draw.blend(Blending.additive);
                //TODO use heats[c]
                Draw.rect(Core.atlas.find(name + "-heat-" + c), x + recoilOffset.x, y + recoilOffset.y, rotation - 90);
            }
            Draw.blend();
            Draw.color();
        }
    }
}
