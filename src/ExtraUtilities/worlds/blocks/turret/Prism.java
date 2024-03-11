package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUStatusEffects;
import ExtraUtilities.worlds.entity.bullet.PrismLaser;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import java.util.Arrays;

import static mindustry.Vars.*;

/**
 * @author guiY
 * redo 2024 0307
 * 重新做了什么？持续发射持续消耗；子弹显示子弹效果子弹伤害
 * */

public class Prism extends Turret {
    //三棱镜光线聚集的强度
    public float angleShiftStrength = 5;
    //角度
    public float shiftAngel = 30;
    //初始散射和最后的交替散射
    public float sideOffsetFirst = 4;
    public float sideOffset = 3f;
    //激光粗度和交替变化速度，<1
    public float fade = 0.001f;
    //纯交替速度（越大越慢
    public int upTime = 3;
    //烎移动炮台速度
    public float firingMoveFract = 0.4f;
    //还原泰拉瑞亚棱镜的射击最高持续时间
    public float maxShootDr = 60 * 60f;

    public StatusEffect[] statuses = {
            StatusEffects.blasted,
            StatusEffects.shocked,
            EUStatusEffects.poison,
            StatusEffects.freezing,
            StatusEffects.wet,
            StatusEffects.sapped
    };
    public BulletType shootType;
    public BulletType[] shootTypes = {};
    public float rainbowDamage = 90;
    public float rainbowWidth = 11;

    public Prism(String name) {
        super(name);
        canOverdrive = false;
        coolantMultiplier = 1f;
        shootSound = Sounds.laser;//EUSounds.prismShoot;
        loopSound = Sounds.pulse;//EUSounds.prismLoop;
        loopSoundVolume = 2f;
        recoilTime = 30f;

        consumeLiquid(Liquids.cryofluid, 30/60f);
        liquidCapacity = 30f;
    }

    @Override
    public void init() {
        super.init();
        if(statuses.length > 0) {
            shootTypes = new BulletType[statuses.length];

            for (int i = 0; i < statuses.length; i++) {
                int finalI = i;
                shootTypes[i] = new PrismLaser(rainbowDamage, rainbowWidth, range){{
                    status = statuses[finalI];
                    statusDuration = 60f;

                    if(finalI < 3) pierceArmor = true;
                }};
            }
            shootType = shootTypes[0];
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.reload);
        this.stats.remove(Stat.inaccuracy);

        for(int i = 0; i < shootTypes.length; i++) {
            this.stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(new Object[]{statuses[i], shootTypes[i]})));
        }
    }

    public class PrismBuild extends TurretBuild{
        public Bullet[] bullets = new Bullet[statuses.length];

        public float bulletTime = 0f;
        public float bulletHeat = 0f;
        public float faded = fade;
        public float bulletLife = 0f;

        public Vec2 tr = new Vec2();

        public BulletType useAmmo() {
            return Prism.this.shootType;
        }

        public boolean hasAmmo() {
            return canConsume();
        }

        private boolean liquidAcc(){
            return !hasLiquids || liquids.get(liquids.current()) > liquidCapacity/6;
        }

        @Override
        public boolean canConsume() {
            return super.canConsume() && liquidAcc();
        }

        public BulletType peekAmmo() {
            return Prism.this.shootType;
        }

        @Override
        public boolean shouldConsume() {
            return isActive() || isShooting() && liquidAcc();
        }

        public boolean checkBullet(){
            if(shootTypes.length == 0 || bullets.length == 0) return false;
            for (Bullet b : bullets) {
                if (b == null) return false;
            }
            return true;
        }

        public void removeBullet(){
            for(int i = 0; i < bullets.length; i++){
                if(bullets[i] == null) continue;
                if(!bullets[i].isAdded() || bullets[i].type == null || bullets[i].owner != this) bullets[i] = null;
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            float ammoFact = this.efficiency;
            Consume cs = Prism.this.findConsumer((f) -> f instanceof ConsumeLiquidBase);
            if (cs instanceof ConsumeLiquid cl) {
                ammoFact = Math.min(ammoFact, this.liquids.get(cl.liquid) / Prism.this.liquidCapacity);
            }

            this.unit.ammo((float)this.unit.type().ammoCapacity * ammoFact);

            removeBullet();
            if(bulletLife < maxShootDr && checkBullet()){
                wasShooting = true;
                faded = Math.min(faded + fade * Time.delta, 1f);
                for(int n = 0; n < bullets.length; n++){
                    int data = (n * (360 / bullets.length));
                    float sin = Mathf.sinDeg(data + (bulletTime * (angleShiftStrength * (1.6f - bulletHeat))));
                    tr.trns(rotation, (block.size * tilesize / 2f) - recoil + shootX, sin * (sideOffsetFirst * bulletHeat + sideOffset));
                    bullets[n].rotation(rotation + ((sin * shiftAngel) * bulletHeat));
                    bullets[n].set(x + tr.x, y + tr.y);
                    //Mana Sickness
                    bullets[n].damage = rainbowDamage * (1 - (bulletLife/maxShootDr) * 0.75f);
                    //need half -> half
                    //bullets[n].damage = rainbowDamage * Math.max(1 - bulletLife/maxShootDr, 0.5f);
                    if(isShooting() && hasAmmo()) bullets[n].time = 0;
                }
                if(isShooting() && hasAmmo()) bulletLife += Time.delta;
                curRecoil = 1f;
                heat = 1f;
                bulletTime += Time.delta;
            }
            bulletHeat = Mathf.lerpDelta(bulletHeat, 0, (float) Math.pow(faded, upTime));
        }

        @Override
        protected void updateReload() {
        }

        @Override
        protected void updateShooting() {
            if(!checkBullet()) {
                if (canConsume() && !charging() && shootWarmup >= Prism.this.minWarmup) {
                    shoot();
                }
            }
        }

        @Override
        protected void updateCooling() {
        }

        protected void shoot() {
            tr.trns(rotation, shootX, 0);
            bullet(rotation);
        }

        protected void bullet(float angle){
            Arrays.fill(bullets, null);
            bulletTime = 0;
            bulletHeat = 0;
            bulletLife  = 0;
            faded = fade;
            for(int s = 0; s < shootTypes.length; s++){
                if(shootTypes[s] == null) return;
                int data = (s * (360 / shootTypes.length));
                float sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength));
                tr.trns(angle, block.size * tilesize / 2f, sine * sideOffset);
                bullets[s] = shootTypes[s].create(tile.build, team, x + tr.x, y + tr.y, angle + (sine * shiftAngel));
                bullets[s].data = data;
            }
            bulletHeat = 1;
        }

        protected void turnToTarget(float targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, power.status * Prism.this.rotateSpeed * this.delta() * (checkBullet() ? Prism.this.firingMoveFract : 1.0F));
        }

        public boolean shouldActiveSound() {
            return bullets[0] != null;
        }
    }
}
