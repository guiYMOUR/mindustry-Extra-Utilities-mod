package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.ExtraUtilitiesMod;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.worlds.meta.TurretManager;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static ExtraUtilities.ExtraUtilitiesMod.toText;
import static mindustry.Vars.*;
import static mindustry.Vars.state;

public class Cobweb extends TractorBeamTurret {
    public int webAmount = 8;
    public float webRange = 10 * 8f;
    public TurretManager manager = new TurretManager();
    public float coolantMulti = 1f;

    public Cobweb(String name) {
        super(name);
    }

    public void setStats() {
        super.setStats();
        if (this.damage > 0.0F) {
            stats.remove(Stat.damage);
            stats.add(Stat.damage, Strings.autoFixed(damage * 60, 2) + "/s " + toText("bullet.armorpierce") + "[] "
                    +Strings.autoFixed(damage/2f * 60, 2) + "/s " + toText("bullet.armorpierce") + "[]");
        }

    }

    public class CobwebBuild extends TractorBeamBuild{
        public Seq<Unit> webs = new Seq<>();
        public float rotTimer = 0;
        public float cod = 1f;

        @Override
        public void updateTile(){
            float eff = efficiency * cod, edelta = eff * delta();
            rotTimer += edelta;

            //float realRange = range * power.status;

            //retarget
            if(timer(timerTarget, retargetTime/(power.status + 1e-4f))){
                //target = Units.closestEnemy(team, x, y, range, u -> u.checkTarget(targetAir, targetGround));
                Units.nearbyEnemies(team, x, y, range, u -> {
                    if(u.targetable(team) && !u.inFogTo(team)) manager.addEnemy(u);
                });
                manager.addTurret(this);
                manager.update();
            }

            //consume coolant
            if(target != null && coolant != null){
                float maxUsed = coolant.amount;

                Liquid liquid = liquids.current();

                float used = Math.min(Math.min(liquids.get(liquid), maxUsed * Time.delta), Math.max(0, (1f / coolantMultiplier) / liquid.heatCapacity));

                liquids.remove(liquid, used);

                if(Mathf.chance(0.06 * used)){
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                }

                cod = 1f + (used * liquid.heatCapacity * coolantMulti);
            }

            any = false;

            //look at target
            if(target != null && target.within(this, range + target.hitSize/2f) && target.team() != team && target.checkTarget(targetAir, targetGround) && efficiency > 0.02f){
                if(!headless){
                    control.sound.loop(shootSound, this, shootSoundVolume);
                }

                float dest = angleTo(target);
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta);
                lastX = target.x;
                lastY = target.y;
                strength = Mathf.lerpDelta(strength, 1f, 0.1f);

                //shoot when possible
                if(Angles.within(rotation, dest, shootCone)){
                    if(damage > 0){
                        target.damageContinuousPierce(damage * eff * state.rules.blockDamage(team));
                    }

                    if(status != StatusEffects.none){
                        target.apply(status, statusDuration);
                    }

                    any = true;
                    target.vel.limit(0.5f/Math.max(1e-4f, edelta));

                    web();
                }
            }else{
                strength = Mathf.lerpDelta(strength, 0, 0.1f);
            }
        }

        public void web(){
            float eff = efficiency * cod, edelta = eff * delta();
            //float realRange = range * power.status;
            webs.clear();

            Units.nearbyEnemies(team, target.x, target.y, webRange, u -> {
                if(u != target && u.targetable(team) && !u.inFogTo(team) && webs.size < webAmount)
                    webs.add(u);
            });

            webs.removeAll(u -> !target.within(u, webRange));
            webs.sort(u -> u.dst(target));

            if(webs.size > 0){
                for(Unit u : webs) {
                    if (u != null && target != null) {
                        if(damage > 0){
                            u.damageContinuousPierce(damage/2f * eff * state.rules.blockDamage(team));
                        }

                        if(status != StatusEffects.none){
                            u.apply(status, statusDuration);
                        }
                        u.vel.limit(0.5f/Math.max(1e-4f, edelta));
                        u.impulseNet(Tmp.v1.set(target).sub(u).limit((force + (1f - u.dst(target) / range) * scaledForce) * edelta));
                    }
                }
            }
        }

        public void updateTarget(Seq<Unit> enemies, Seq<Unit> lockedEnemies) {
            float realRange = range * power.status;
            target = null;
            for (Unit enemy : enemies) {
                if (within(enemy, realRange) && !lockedEnemies.contains(enemy)) {
                    target = enemy;
                    break;
                }
            }
        }

//        @Override
//        public float range() {
//            return range * power.status;
//        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            //draw laser if applicable
            if(any){
                Draw.z(Layer.bullet);
                float ang = angleTo(lastX, lastY);

                Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

                Drawf.laser(laser, laserStart, laserEnd,
                        x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
                        lastX, lastY, strength * efficiency * laserWidth);

                if(target != null) {
                    for (int i = 0; i < webAmount; i++) {
                        float a = 360f / webAmount * i + rotTimer * 1.5f;
                        float lx = EUGet.dx(target.x, webRange * strength, a),
                                ly = EUGet.dy(target.y, webRange * strength, a);
                        if(i <= webs.size - 1){
                            Unit u = webs.get(i);
                            if (u != null) {
                                Drawf.laser(laser, laserStart, laserEnd,
                                        target.x, target.y,
                                        u.x, u.y, strength * efficiency * laserWidth * 0.5f);
                            }
                        } else {
                            Drawf.laser(laser, laserStart, laserEnd,
                                    target.x, target.y,
                                    lx, ly, strength * efficiency * laserWidth * 0.5f);
                        }
                    }
                }

                Draw.mixcol();
            }
        }
    }
}
