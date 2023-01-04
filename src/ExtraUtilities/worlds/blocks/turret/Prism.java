package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUSounds;
import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import java.util.Arrays;

import static mindustry.Vars.*;

public class Prism extends PowerTurret {
    //三棱镜光线聚集的强度
    public float angleShiftStrength = 5;
    //角度
    public float shiftAngel = 30;
    //初始散射和最后的交替散射
    public float sideOffsetFirst = 4;
    public float sideOffset = 2.5f;
    //激光粗度和交替变化速度，<1
    public float fade = 0.001f;
    //纯交替速度（越大越慢
    public int upTime = 3;
    //射击间隔
    public float shootDuration = 60*10;
    //烎移动炮台速度
    public float firingMoveFract = 0.25f;
    //reload还要注释?
    public float reloadTime = 240;

    public Prism(String name) {
        super(name);
        canOverdrive = false;
        coolantMultiplier = 1f;
        shootSound = Sounds.laser;//EUSounds.prismShoot;
        loopSound = Sounds.pulse;//EUSounds.prismLoop;
        loopSoundVolume = 2f;
    }

    @Override
    public void init() {
        super.init();
        if (this.coolant == null) {
            this.coolant = (ConsumeLiquidBase)this.findConsumer((c) -> {
                return c instanceof ConsumeLiquidBase;
            });
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.reload);
        this.stats.add(Stat.reload, 60f / this.reloadTime + " * 6", StatUnit.perSecond);
        this.stats.remove(Stat.booster);
        this.stats.add(Stat.input, StatValues.boosters(reloadTime, coolant.amount, coolantMultiplier, false, this::consumesLiquid));
        this.stats.remove(Stat.ammo);
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));
    }

    public class PrismBuild extends PowerTurretBuild{
        public Bullet[] bullets = new Bullet[]{null, null, null, null, null, null};
        //public Seq<Bullet> bullets = new Seq<>(6);

        public float bulletLife = 0f;
        public float bulletTime = 0f;
        public float bulletHeat = 0f;
        public float faded = fade;
        public float _reloadTime = reloadTime;

        public Vec2 tr = new Vec2();

        @Override
        protected void updateCooling() {
        }

        @Override
        public boolean shouldConsume() {
            return bulletLife>0 || this.isActive() || this.isShooting();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            float used;
            if(bulletLife > 0 && bullets[0] != null){
                wasShooting = true;
                faded = Math.min(faded + fade * Time.delta, 1f);
                for(int n = 0; n < bullets.length; n++){
                    int data = (n * (360 / bullets.length));
                    float sine = Mathf.sinDeg(data + (bulletTime * (angleShiftStrength * (1.6f-bulletHeat))));
                    //float sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength * faded));
                    tr.trns(rotation, (block.size * tilesize / 2f) - recoil + shootX, sine * (sideOffsetFirst * bulletHeat + sideOffset));
                    bullets[n].rotation(rotation + ((sine * shiftAngel) * bulletHeat));
                    bullets[n].set(x + tr.x, y + tr.y);
                    bullets[n].time = 0;
                }
                curRecoil = 1f;
                heat = 1f;
                bulletTime += Time.delta;
                bulletLife -= Time.delta / Math.max(power.status, 0.00001f);
                if (bulletLife <= 0) {
                    Arrays.fill(bullets, null);
                    bulletTime = 0;
                    bulletHeat = 0;
                    faded = fade;
                }
            } else if(_reloadTime > 0){
                wasShooting = true;
                if (coolant != null) {
                    Liquid liquid = liquids.current();
                    float maxUsed = coolant.amount;
                    used = (cheating() ? maxUsed : Math.min(liquids.get(liquid), maxUsed)) * delta();
                    _reloadTime -= used * liquid.heatCapacity * coolantMultiplier;
                    liquids.remove(liquid, used);
                    if (Mathf.chance(0.06d * (double)used)) {
                        coolEffect.at(x + Mathf.range((float)(size * 8) / 2f), y + Mathf.range((float)(size * 8) / 2f));
                    }
                } else {
                    _reloadTime -= this.edelta();
                }
            }
            bulletHeat = Mathf.lerpDelta(bulletHeat, 0, (float) Math.pow(faded, upTime));
            //bulletHeat = Mathf.lerpDelta(bulletHeat, 0, fade);
        }

        @Override
        protected void updateReload() {
        }

        @Override
        protected void updateShooting() {
            if(bulletLife > 0 || bullets[0] != null){
                return;
            }
            if(_reloadTime <= 0 && (power.status > 0 || cheating())){
                BulletType type = peekAmmo();
                shoot(type);
                _reloadTime = reloadTime;
            }
        }

        @Override
        protected void shoot(BulletType type) {
            tr.trns(rotation, shootX, 0);
            bullet(type, rotation);
        }

        protected void bullet(BulletType type, float angle){
            bulletTime = 0;
            bulletLife = shootDuration;
            for(int s = 0; s < 6; s++){
                int data = (s * (360 / 6));
                float sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength));
                tr.trns(angle, block.size * tilesize / 2f, sine * sideOffset);
                bullets[s] = type.create(tile.build, team, x + tr.x, y + tr.y, angle + (sine * shiftAngel));
                bullets[s].data = data;
            }
            bulletHeat = 1;
        }

        public float progress() {
            return 1f - Mathf.clamp(_reloadTime / reloadTime);
        }

        protected void turnToTarget(float targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, power.status * Prism.this.rotateSpeed * this.delta() * (bulletLife > 0 ? Prism.this.firingMoveFract : 1.0F));
        }

        public float activeSoundVolume() {
            return 1f;
        }

        public boolean shouldActiveSound() {
            return bullets[0] != null;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(_reloadTime);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            _reloadTime = read.f();
        }
    }
}
