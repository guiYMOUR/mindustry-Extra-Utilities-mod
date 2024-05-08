package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.worlds.entity.bullet.HealCone;
import arc.func.Boolf;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.gen.Healthc;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class MendTurret extends ContinuousTurret {
    public float amountBoost = 0.5f;
    public float angleBoost = 0.25f;

    public float consTime = 240f;

    public MendTurret(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        stats.timePeriod = consTime;
        super.setStats();
        stats.remove(Stat.ammo);
        stats.remove(Stat.targetsGround);
        stats.remove(Stat.targetsAir);
        if(shootType instanceof HealCone hc){
            stats.add(Stat.repairSpeed, hc.percentHeal ? hc.healPercent : hc.healAmount, hc.percentHeal ? StatUnit.percent : StatUnit.perSecond);
            stats.add(Stat.repairSpeed, t -> t.add(Strings.autoFixed(hc.findAngle, 1) + "°"));

            if(findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems cons){
                stats.remove(Stat.booster);
                stats.add(Stat.booster, table -> {
                    table.row();
                    Boolf<Item> cs = this::consumesItem;
                    table.table(c -> {
                        for(Item item : content.items()){
                            if(!cs.get(item)) continue;

                            c.table(Styles.grayPanel, b -> {
                                for(ItemStack stack : cons.items){
                                    if(stats.timePeriod < 0){
                                        b.add(new ItemDisplay(stack.item, stack.amount, true)).pad(20f).left();
                                    }else{
                                        b.add(new ItemDisplay(stack.item, stack.amount, stats.timePeriod, true)).pad(20f).left();
                                    }
                                    if(cons.items.length > 1) b.row();
                                }

                                b.table(bt -> {
                                    bt.right().defaults().padRight(3).left();
                                    if(angleBoost != 0) bt.add("[lightgray]+ [stat]" + Strings.autoFixed(angleBoost * hc.findAngle, 2) + "[lightgray]°").row();
                                    if(amountBoost != 0) {
                                        if(hc.percentHeal) bt.add("[lightgray]+ [stat]" + Strings.autoFixed(amountBoost * hc.healPercent, 2) + "[lightgray]" + StatUnit.percent.localized() + Stat.repairSpeed.localized());
                                        else bt.add("[lightgray]+ [stat]" + Strings.autoFixed(amountBoost * hc.healAmount, 2) + " [lightgray]" + Stat.repairSpeed.localized());
                                    }
                                }).right().grow().pad(10f).padRight(15f);
                            }).growX().pad(5).padBottom(-5).row();
                        }
                    }).growX().colspan(table.getColumns());
                    table.row();
                });
            }
        }
    }

    public class MendTurretBuild extends ContinuousTurretBuild{
        public float timerCons = 0;
        public float boostWarmup = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            if(items.any()) {
                boostWarmup = Mathf.lerpDelta(boostWarmup, 1, 0.04f);
                if (canConsume() && shouldConsume()) {
                    timerCons += edelta();
                }
            } else {
                boostWarmup = Mathf.lerpDelta(boostWarmup, 0, 0.08f);
            }

            if(timerCons >= consTime){
                consume();
                timerCons = 0;
            }
        }

        @Override
        protected void findTarget() {
            float range = range();

            target = Units.findAllyTile(team, x, y, range, b -> b.damaged() && b != this);
            if(target != null) return;
            target = Units.closest(team, x, y, range, Healthc::damaged);
        }

        @Override
        protected boolean validateTarget() {
            return target != null || isControlled() || logicControlled();
        }

        @Override
        protected void updateBullet(BulletEntry entry) {
            super.updateBullet(entry);
            if(isShooting() && hasAmmo()) entry.bullet.data = angleMti();
        }

        public float angleMti(){
            return efficiency * (1 + angleBoost * boostWarmup);
        }

        public float amountMti(){
            float boost = amountBoost * boostWarmup;
            float scale = timeScale > 1 ? boost + timeScale - 1 : boost * timeScale;
            return efficiency * (1 + scale);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(timerCons);
            write.f(boostWarmup);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            timerCons = read.f();
            boostWarmup = read.f();
        }
    }
}
