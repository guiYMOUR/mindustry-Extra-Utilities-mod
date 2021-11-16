/*
* @author <guiY>
* @DefAI
*/

const DefenderHealAI = (shotUnit, findBuild, hasPower) => {
    return extend(DefenderAI, {
        updateMovement(){
            if(this.target != null){
                var shoot = false;
                if(this.target.within(this.unit, this.unit.type.range) && this.target.damaged() && this.target.team == this.unit.team && ((shotUnit && this.target instanceof Unit) || (findBuild && this.target instanceof Building))){
                    this.unit.aim(this.target);
                    shoot = true;
                }
                this.unit.controlWeapons(shoot);
            } else if(this.target == null){
                this.unit.controlWeapons(false);
            }
            if(this.target != null){//move to reach the target
                if(this.unit.type.flying){
                    if(!this.target.within(this.unit, this.unit.type.range * 0.7)){
                        this.moveTo(this.target, this.unit.type.range * 0.7);
                    }
                    this.unit.lookAt(this.target);
                } else {
                    if(!this.target.within(this.unit, this.unit.type.range * 0.7)){
                        this.moveTo(this.target, this.unit.type.range * 0.7);
                        if(!this.target.within(this.unit, this.unit.type.range * 1.2)){
                            if(this.unit.type.canBoost || (this.unit.pathType() == Pathfinder.costNaval && !this.unit.floorOn().isLiquid)){
                                this.unit.elevation = Mathf.approachDelta(this.unit.elevation, 1, this.unit.type.riseSpeed);
                            }
                        }
                    }
                    if(this.target.within(this.unit, this.unit.type.range * 1.2)){
                        if(this.unit.type.canBoost && this.unit.elevation > 0.001 && !this.unit.onSolid() && !(this.unit.pathType() == Pathfinder.costNaval && !this.unit.floorOn().isLiquid)){
                            this.unit.elevation = Mathf.approachDelta(this.unit.elevation, 0, this.unit.type.riseSpeed);
                        }
                    }
                    this.unit.lookAt(this.target);
                }
            }
            if(!shotUnit && (this.target == null || this.target instanceof Unit || this.target.team != this.unit.team)){//auto attack
                var shootA = false;
                var air = this.unit.type.targetAir;
                var ground = this.unit.type.targetGround;
                var enemy = Units.closestTarget(this.unit.team, this.unit.x, this.unit.y, this.unit.type.maxRange, boolf(u => u.checkTarget(air, ground)), boolf(t => ground));
                if(enemy != null){
                    this.unit.aim(enemy);
                    shootA = true;
                }
                this.unit.controlWeapons(shootA);
            }
        },
        findTarget(x, y, range, air, ground){
            if(this.command() != UnitCommand.rally){
                var result = Units.closest(this.unit.team, x, y, Math.max(range, 400), boolf(u => !u.dead && u.type != this.unit.type && u.damaged()));
                if(result != null) return result;
                if(findBuild){
                    var build = Units.findAllyTile(this.unit.team, x, y, Math.max(range, 400), boolf(other => other.damaged()));
                    if(build != null) return build;
                }
                var strong = Units.closest(this.unit.team, x, y, Math.max(range, 400), boolf(u => !u.dead && u.type != this.unit.type), (u, tx, ty) => -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400);
                if(strong != null) return strong;
            }
            var block = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);
            if(block != null) return block;
            var core = this.unit.closestCore();
            if(core != null) return core;
            if(Vars.state.rules.waves && this.unit.team == Vars.state.rules.waveTeam){
                return this.unit.closestEnemyCore();
            }
            return null;
        },
    });
}
exports.DefenderHealAI = DefenderHealAI;

const Firefighter = (retreatDst, fleeRange, retreatDelay) => {
    var avoid = null;
    var retreatTimer = 0;
    return extend(AIController, {
        updateTargeting(){
            var result = null;
            var realRange = Math.max(this.unit.type.range, 800);
            result = Groups.bullet.intersect(this.unit.x - realRange, this.unit.y - realRange, realRange*2, realRange*2).min(b => b.type == Bullets.fireball, b => b.dst2(this.unit.x, this.unit.y));
            if(result != null){
                var enemyBuild = null;
                Vars.indexer.allBuildings(result.x, result.y, 64, cons(other =>{
                    if(other.team != this.unit.team){
                        enemyBuild = other;
                    }
                }));
                if(enemyBuild == null) this.target = result;
            } else {
                this.super$updateTargeting();
            }
        },
        updateMovement(){
            if(this.target != null){
                var shoot = false;
                if(this.target.within(this.unit, Math.max(this.unit.type.range, 80))){
                    this.unit.aim(this.target);
                    shoot = true;
                }
                this.unit.controlWeapons(shoot);
            } else if(this.target == null){
                this.unit.controlWeapons(false);
            }
            if(this.target != null){
                if(!this.target.within(this.unit, this.unit.type.range)){
                    this.moveTo(this.target, this.unit.type.range);
                }
                this.unit.lookAt(this.target);
            }
            if(this.target == null){
                if(this.timer.get(this.timerTarget4, 40)){
                    avoid = Units.closestTarget(this.unit.team, this.unit.x, this.unit.y, fleeRange, boolf(u => u.checkTarget(true, true)), boolf(t => true));
                }
                if((retreatTimer += Time.delta) >= retreatDelay){
                    if(avoid != null){
                        var core = this.unit.closestCore();
                        if(core != null && !this.unit.within(core, retreatDst)){
                            this.moveTo(core, retreatDst);
                        }
                    }
                }
            }else{
                retreatTimer = 0;
            }
        },
    });
}
exports.Firefighter = Firefighter;

const TDFlyingAI = () => {
    return extend(GroundAI, {
        updateMovement(){
            this.super$updateMovement();
            if(this.target == null || !this.unit.within(this.target, this.unit.type.range * 2)){
                this.unit.elevation = 1;
            }
        },
    });
}
exports.TDFlyingAI = TDFlyingAI;