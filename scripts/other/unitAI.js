/*
* @author <guiY>
* @DefAI
*/
const DefenderHealAI = (shotUnit, findBuild, hasPower) => {
    return extend(DefenderAI, {
        updateMovement(){
            if(this.target != null){
                var shoot = false;
                if(this.target.within(this.unit, this.unit.type.range) && this.target.damaged() && ((shotUnit && this.target instanceof Unit) || (findBuild && this.target instanceof Building))){
                    this.unit.aim(this.target);
                    shoot = true;
                }
                this.unit.controlWeapons(shoot);
            } else if(this.target == null){
                this.unit.controlWeapons(false);
            }
            if(this.target != null){
                if(!this.target.within(this.unit, this.unit.type.range * 0.65) && this.target.team == this.unit.team){
                    this.moveTo(this.target, this.unit.type.range * 0.65);
                }
                this.unit.lookAt(this.target);
            }
            if(!shotUnit && (this.target == null || this.target instanceof Unit)){
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
                return Units.closest(this.unit.team, x, y, Math.max(range, 400), boolf(u => !u.dead && u.type != this.unit.type), (u, tx, ty) => -u.maxHealth + Mathf.dst2(u.x, u.y, tx, ty) / 6400);
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