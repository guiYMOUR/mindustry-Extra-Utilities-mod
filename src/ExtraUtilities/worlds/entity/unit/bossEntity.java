package ExtraUtilities.worlds.entity.unit;

import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.worlds.entity.ability.bossUnitAbi;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.UnitEntity;

public class bossEntity extends UnitEntity{
    private boolean b1 = false;

    public Seq<Building> bs = new Seq<>();
    public Seq<bossType.pickedBlock> pb = new Seq<>();

    public transient Interval timer = new Interval(6);

    @Override
    public int classId() {
        return EUUnitTypes.bossId;
    }

    @Override
    public void update() {
        super.update();
        if(!b1){
            apply(StatusEffects.boss);
            b1 = true;
        }
    }

    public boolean reSp(){
        if(abilities.length == 0) return false;
        for (Ability ab : abilities){
            if(ab instanceof bossUnitAbi ba) return ba.targetable();
        }
        return false;
    }

    @Override
    public boolean hittable() {
        return !reSp() && super.hittable();
    }

    @Override
    public boolean targetable(Team targeter) {
        return !reSp() && super.targetable(targeter);
    }

    @Override
    public void damage(float amount) {
        if(reSp()) return;
        super.damage(Math.min(800, amount * 0.1f));
    }

    @Override
    public void damagePierce(float amount) {
        if(reSp()) return;
        super.damagePierce(Math.min(800, amount * 0.15f));
    }

    @Override
    public void damageContinuous(float amount) {
        if(reSp()) return;
        super.damageContinuous(Math.min(800, amount * 0.1f));
    }

    @Override
    public void damageContinuousPierce(float amount) {
        if(reSp()) return;
        super.damageContinuousPierce(Math.min(800, amount * 0.15f));
    }

    @Override
    public void kill() {
        if(!(type instanceof bossType) || abilities.length == 0) return;
        for(Ability ab : abilities){
            if(ab instanceof bossUnitAbi ba) {
                if (!ba.isS1()) {
                    ba.setD1(true);
                } else super.kill();
            }
        }
    }

//    @Override
//    public void remove() {
//        if(!(type instanceof bossType) || abilities.length == 0) return;
//        for(Ability ab : abilities){
//            if(ab instanceof bossUnitAbi ba) {
//                if (ba.isS1()) {
//                    super.remove();
//                }
//            }
//        }
//    }

    @Override
    public void writeSync(Writes write) {
        super.writeSync(write);

        if(!(type instanceof bossType) || abilities.length == 0) return;
        for(Ability ab : abilities){
            if(ab instanceof bossUnitAbi ba) {
                write.bool(ba.isD1());
                write.bool(ba.isS1());
            }
        }
    }

    @Override
    public void readSync(Reads read) {
        super.readSync(read);

        if(!(type instanceof bossType) || abilities.length == 0) return;
        for(Ability ab : abilities){
            if(ab instanceof bossUnitAbi ba) {
                ba.setD1(read.bool());
                ba.setS1(read.bool());
            }
        }
    }
}
