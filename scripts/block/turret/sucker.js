/*
* @readme <All right, all right, I'll use it.>
*/
//索敌数
const shots = 8;
//强度
const force = 24;
const scaledForce = 8;

const sucker = extendContent(TractorBeamTurret, "sucker", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.shots, shots);
    },
});
sucker.buildType = prov(() => {
    var target = new Seq();
    
    const block = sucker;
    var x = 0, y = 0;
    var rotation = 0;
    
    return new JavaAdapter(TractorBeamTurret.TractorBeamBuild, {
        updateTile(){
            x = this.x;
            y = this.y;
            rotation = this.rotation;
            this.super$updateTile();
            target.clear();
            Units.nearbyEnemies(this.team, x, y, block.range, cons(unit =>{
                if(unit.checkTarget(block.targetAir, block.targetGround) && Angles.within(rotation, this.angleTo(unit), block.shootCone) && this.efficiency() > 0.02){
                    target.add(unit);
                }
            }));
            target.sort(floatf(u => u.dst(x, y)));
            var max = Math.min(shots, target.size);
            for(var a = 0; a < max; a++){
                var unit = target.get(a);
                if(unit != null){
                    if(block.damage > 0){
                            unit.damageContinuous(block.damage * this.efficiency());
                    }
                    unit.impulseNet(Tmp.v1.set(this).sub(unit).limit((force + (1 - unit.dst(this) / block.range) * scaledForce) * this.edelta() * this.timeScale));
                }
            }
        },
        draw(){
            rotation = this.rotation;
            Draw.rect(block.baseRegion, x, y);
            Drawf.shadow(block.region, x - (block.size / 2), y - (block.size / 2), rotation - 90);
            Draw.rect(block.region, x, y, rotation - 90);
            Draw.z(Layer.bullet);
            var max = Math.min(shots, target.size);
            for(var a = 0; a < max; a++){
                var unit = target.get(a);
                Draw.mixcol();
                Draw.mixcol(block.laserColor, Mathf.absin(4, 0.6));
                Tmp.v1.trns(rotation, block.shootLength).add(x, y);
                if(unit != null){
                    Drawf.laser(
                        this.team,
                        Core.atlas.find("parallax-laser"),
                        Core.atlas.find("parallax-laser-end"),
                        Tmp.v1.x,
                        Tmp.v1.y,
                        unit.x,
                        unit.y,
                        this.efficiency() * block.laserWidth
                    );
                }
            }
        },
    }, sucker);
});
Object.assign(sucker, {
    hasPower : true,
    force : 0,
    scaledForce : 0,
    size : 3,
    damage : 1.2,
    range : 300,
    shootCone : 50,
    shootLength : 11,
    rotateSpeed : 8,
    laserWidth : 0.8,
    health : 160 * 3 * 3,
});
sucker.consumes.powerCond(4, boolf(e => e.target != null));
sucker.requirements = ItemStack.with(
    Items.graphite, 200,
    Items.silicon, 240,
    Items.titanium, 100,
    Items.plastanium, 60
);
sucker.buildVisibility = BuildVisibility.shown;
sucker.category = Category.turret;
exports.sucker = sucker;
