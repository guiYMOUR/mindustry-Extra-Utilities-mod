package ExtraUtilities.ai;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.DefenderAI;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;

public class DefenderHealAI extends DefenderAI {
    public boolean targetUnit, targetBuilding;
    
    public DefenderHealAI(boolean targetUnit, boolean targetBuilding){
        this.targetUnit = targetUnit;
        this.targetBuilding = targetBuilding;
    }

    public DefenderHealAI() {
        this(true, true);
    }

    @Override
    public void updateMovement() {
        if(target != null){
            boolean shoot = false;
            if(target.within(unit, unit.type.range) && (target instanceof Healthc hc && hc.health() < hc.maxHealth() - 0.001f) && target.team() == unit.team && ((targetUnit && target instanceof Unit) || (targetBuilding && target instanceof Building))){
                unit.aim(target);
                shoot = true;
            }
            unit.controlWeapons(shoot);
        } else {
            unit.controlWeapons(false);
        }
        if(target != null && unit.type != null){//move to reach the target
            if(unit.type.flying){
                if(!target.within(unit, unit.type.range * 0.7f)){
                    moveTo(target, unit.type.range * 0.7f);
                }
                unit.lookAt(target);
            } else {
                if(!target.within(unit, unit.type.range * 0.7f)){
                    moveTo(target, unit.type.range * 0.7f);
                    if(!target.within(unit, unit.type.range * 1.2f)){
                        if(unit.type.canBoost || (unit.pathType() == Pathfinder.costNaval && !unit.floorOn().isLiquid)){
                            unit.elevation = Mathf.approachDelta(unit.elevation, 1, unit.type.riseSpeed);
                        }
                    }
                }
                if(target.within(unit, unit.type.range * 1.2f)){
                    if(unit.type.canBoost && unit.elevation > 0.001 && !unit.onSolid() && !(unit.pathType() == Pathfinder.costNaval && !unit.floorOn().isLiquid)){
                        unit.elevation = Mathf.approachDelta(unit.elevation, 0, unit.type.riseSpeed);
                    }
                }
                unit.lookAt(target);
            }
        }
        if(!targetUnit && (target == null || target instanceof Unit || target.team() != unit.team) && unit.type != null){//auto attack
            boolean shootA = false;
            boolean air = unit.type.targetAir;
            boolean ground = unit.type.targetGround;
            Teamc enemy = Units.closestTarget(unit.team, unit.x, unit.y, unit.type.maxRange, u -> u.checkTarget(air, ground), t -> ground);
            if(enemy != null){
                unit.aim(enemy);
                shootA = true;
            }
            unit.controlWeapons(shootA);
        }
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        Unit result = Units.closest(this.unit.team, x, y, Math.max(range, 400), u -> !u.dead && u.type != this.unit.type && u.damaged());
        if(result != null) return result;
        if(targetBuilding){
            Building build = Units.findAllyTile(this.unit.team, x, y, Math.max(range, 400), Building::damaged);
            if(build != null) return build;
        }
        Unit strong = Units.closest(this.unit.team, x, y, Math.max(range, 400), u -> !u.dead && u.type != this.unit.type, (u, tx, ty) -> -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400);
        if(strong != null) return strong;
        CoreBlock.CoreBuild core = this.unit.closestCore();
        if(core != null) return core;
        if(Vars.state.rules.waves && this.unit.team == Vars.state.rules.waveTeam){
            return this.unit.closestEnemyCore();
        }
        return null;
    }
}
