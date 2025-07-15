package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUBulletTypes;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.worlds.meta.EUStatValues;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Bullet;
import mindustry.world.meta.Stat;

public class Penitent extends LoadTurret {
    public float damagePerShot = 0.02f;
    //绝境冷却
    public float impasseReload = 15 * 60f;
    //绝境持续
    public float impasseTime = 4 * 60f;
    //战斗续行持续
    public float endTime = 2 * 60f;

    public Penitent(String name) {
        super(name);
        targetInterval = 6;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.abilities, EUStatValues.ability(name, 3));
    }

    public class PenitentBuild extends LoadTurretBuild{
        public boolean impasse = false;
        public boolean inImp = false;
        public boolean impReload = false;
        public boolean end = false;
        public float impTimer = 0;
        public float impReloadTimer = 0;
        public Bullet[] effectBullets = {null, null};

        @Override
        public void updateTile() {
            super.updateTile();

            if(impasse){
                for(int i = 0; i < 2; i++){
                    if(effectBullets[i] == null) {
                        effectBullets[i] = EUBulletTypes.effectForPenitent.create(this, team, x, y, 0);
                        continue;
                    }
                    float fin = Math.min(1, impTimer/30);
                    fin = Interp.fastSlow.apply(fin);
                    float ex = EUGet.dx(x, size * 8 * fin, impTimer * 5 + 180 * i),
                            ey = EUGet.dy(y, size * 8 * fin, impTimer * 5 + 180 * i);
                    effectBullets[i].set(ex, ey);
                    effectBullets[i].rotation(impTimer * 5 + 180 * i + 90);
                    effectBullets[i].time = 0;
                }
            } else {
                for(int i = 0; i < 2; i++){
                    if(effectBullets[i] != null){
                        effectBullets[i].remove();
                        effectBullets[i] = null;
                    }
                }
            }

            //战斗续行
            if(end){
                impTimer += Time.delta;
                if(impTimer >= endTime){
                    for(int i = 0; i < 2; i++){
                        if(effectBullets[i] != null){
                            effectBullets[i].remove();
                            effectBullets[i] = null;
                        }
                    }
                    kill();
                }
                return;
            }

            if(reRot > loadReload){
                heal((maxHealth() * 0.02f) / 60f * Time.delta * power.status);
            }

            float stop = maxHealth() * 0.01f;
            if(health() <= stop + 0.01f && !inImp && !impReload){
                impasse = true;
                inImp = true;
            }
            //绝境效果计时器
            if(inImp){
                impTimer += Time.delta;
                if(impTimer >= impasseTime){
                    impasse = false;
                    inImp = false;
                    impReload = true;
                    impTimer = 0;
                }
            }
            //绝境效果重载CD
            if(impReload){
                impReloadTimer += Time.delta;
                if(impReloadTimer >= impasseReload){
                    impReload = false;
                    impReloadTimer = 0;
                }
            }
        }

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
            if(bullet != null){
                float lif = maxHealth() * damagePerShot;
                float stop = maxHealth() * 0.01f;
                if(health() - lif >= stop){
                    health -= lif;
                } else if(health > stop){
                    health = stop;
                }

                float pt = (1 + (maxHealth() - health() + stop)/maxHealth()) * (impasse ? 1.2f : 1f);
                bullet.damage *= pt;
            }
        }

        @Override
        public void draw() {
            super.draw();

            Lines.stroke(2, team.color);
//            if(impasse){
//                Lines.circle(x, y, size * 8);
//            }

            if(end){
                Lines.poly(x, y, 6, size * 8, -impTimer * 3);
            }
            Draw.reset();
        }

        @Override
        public boolean damaged() {
            return super.damaged() && !impasse;
        }

        @Override
        public void heal(float amount) {
            if(!impasse) super.heal(amount);
        }

        @Override
        public void damage(float damage) {
            if(!impasse) {
                float stop = maxHealth() * 0.01f;
                if(damage >= health()){
                    health(stop);
                    impasse = true;
                    end = true;
                    return;
                }

                super.damage(damage);
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(impasse);
            write.bool(inImp);
            write.bool(impReload);
            write.bool(end);
            write.f(impTimer);
            write.f(impReloadTimer);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            impasse = read.bool();
            inImp = read.bool();
            impReload = read.bool();
            end = read.bool();
            impTimer = read.f();
            impReloadTimer = read.f();
        }
    }
}
