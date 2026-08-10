package ExtraUtilities.worlds.entity.unit;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUStatusEffects;
import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.ui.OSLog;
import ExtraUtilities.worlds.entity.ability.bossUnitAbi;
import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.*;

public class bossEntity extends UnitEntity{
    private boolean b1 = false;
    private boolean r1 = false;

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

        if(timer.get(2, 30) && !hasEffect(EUStatusEffects.breakage)){
            Groups.unit.each(u -> {
                if(u.team != team && (u.type != null && u.type != this.type && u.targetable(team))){
                    if(u.type.hitSize >= 50 || u.type.armor >= 50 || u.maxHealth >= 50000){
                        EUFx.rgX.at(u.x, u.y, 0, EUGet.MIKU, u.hitSize);
                        if (!Vars.net.client()) {
                            Unit ut = u.type.create(team);
                            ut.health(u.health);
                            ut.rotation(u.rotation());
                            ut.set(u);
                            ut.add();
                            u.remove();
                            Groups.unit.remove(u);
                        }
                    }
                    for(var m : u.mounts){
                        if(within(m.aimX, m.aimY, 160) || (m.target == this)){
                            EUFx.rgX.at(u.x, u.y, 0, EUGet.MIKU, u.hitSize);
                            if (!Vars.net.client()) {
                                Unit ut = u.type.create(team);
                                ut.health(u.health);
                                ut.rotation(u.rotation());
                                ut.set(u);
                                ut.add();
                                u.remove();
                                Groups.unit.remove(u);
                            }
                            break;
                        }
                    }
                }
            });
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
    public void rawDamage(float amount) {
        if(reSp()) return;
        float d = amount * 0.1f;
        if(Vars.player != null) {
            var core = Vars.player.closestCore();
            if (core != null && d > 1200) {
                EUFx.rgX.at(core.x, core.y, 0, EUGet.MIKU, hitSize);
                if (!Vars.net.client()) {
                    Unit ut = type.create(team);
                    ut.health(health);
                    ut.rotation(rotation());
                    ut.set(core);
                    ut.add();
                    remove();
                    Groups.unit.remove(this);
                }
            }
        }
        super.rawDamage(Math.min(800, d));
    }

    @Override
    public void kill() {
        if(!(type instanceof bossType) || abilities.length == 0) return;
        if(r1) return;
        if(health > 1000 && !Vars.net.active() && Vars.player != null && team != Vars.player.team()){
            r1 = true;
            OSLog.show("star-nullTarget", OSLog.getPcUsername());
            Vars.ui.paused.runExitSave(false);
            //return;
        }
        for(Ability ab : abilities){
            if(ab instanceof bossUnitAbi ba) {
                if (!ba.isS1()) {
                    ba.setD1(true);
                } else super.kill();
            }
        }
    }

    @Override
    public void collision(Hitboxc other, float x, float y) {
        if (other instanceof Bullet bullet) {
            this.controller.hit(bullet);

            if(hasEffect(EUStatusEffects.breakage)) return;
            var owner = bullet.owner;
            int i = 0;
            while (owner instanceof Bullet b){
                owner = b.owner;
                if(i > 6) break;
                i++;
            }

//            if(!Vars.net.active() && Vars.player != null && team != Vars.player.team() && (owner == null || owner instanceof Bullet)) {
//                OSLog.show("star-nullTarget", OSLog.getPcUsername());
//                Vars.ui.paused.runExitSave(false);
//            }

            if(owner instanceof Unit u && u.team != team && (u.type != null && u.type != this.type && u.targetable(team))){
                EUFx.rgX.at(u.x, u.y, 0, EUGet.MIKU, u.hitSize);
                if (!Vars.net.client()) {
                    Unit ut = u.type.create(team);
                    ut.health(u.health);
                    ut.rotation(u.rotation());
                    ut.set(u);
                    ut.add();
                    u.remove();
                    Groups.unit.remove(u);
                }
            }
        }
    }

    @Override
    public void remove(){
        if(r1) return;
        if(health > 1000 && !Vars.net.active() && Vars.player != null && team != Vars.player.team()){
            r1 = true;
            OSLog.show("star-nullTarget", OSLog.getPcUsername());
            Vars.ui.paused.runExitSave(false);
        }
        super.remove();
    }

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
