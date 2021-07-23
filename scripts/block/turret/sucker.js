//const shots = 5;
/*
*readme <Why don't I use this limit? A: I'm lazy.>
*/
const force = 20;
const scaledForce = 8;
const sucker = extendContent(TractorBeamTurret, "sucker", {});
sucker.buildType = prov(() => {
    return new JavaAdapter(TractorBeamTurret.TractorBeamBuild, {
        updateTile(){
            this.super$updateTile();
            Units.nearbyEnemies(this.team, this.x, this.y, this.block.range, cons(unit =>{
                if(unit.checkTarget(this.block.targetAir, this.block.targetGround) && Angles.within(this.rotation, this.angleTo(unit), this.block.shootCone)){
                    if(this.block.damage > 0){
                        unit.damageContinuous(this.block.damage * this.efficiency());
                    }
                    unit.impulseNet(Tmp.v1.set(this).sub(unit).limit((force + (1 - unit.dst(this) / this.block.range) * scaledForce) * this.edelta() * this.timeScale));
                }
            }));
        },
        draw(){
            Draw.rect(this.block.baseRegion, this.x, this.y);
            Drawf.shadow(this.block.region, this.x - (this.block.size / 2), this.y - (this.block.size / 2), this.rotation - 90);
            Draw.rect(this.block.region, this.x, this.y, this.rotation - 90);
            Draw.z(Layer.bullet);
            Units.nearbyEnemies(this.team, this.x, this.y, this.block.range, cons(unit =>{
                if(unit.checkTarget(this.block.targetAir, this.block.targetGround) && Angles.within(this.rotation, this.angleTo(unit), this.block.shootCone)){
                    Draw.mixcol();
                    Draw.mixcol(this.block.laserColor, Mathf.absin(4, 0.6));
                    Tmp.v1.trns(this.rotation, this.block.shootLength).add(this.x, this.y);
                    Drawf.laser(
                        this.team,
                        Core.atlas.find("parallax-laser"),
                        Core.atlas.find("parallax-laser-end"),
                        Tmp.v1.x,
                        Tmp.v1.y,
                        unit.x,
                        unit.y,
                        this.efficiency() * this.block.laserWidth
                    );
                }
            }));
        },
    }, sucker);
});
Object.assign(sucker, {
    hasPower : true,
    force : 0,
    scaledForce : 0,
    size : 3,
    damage : 0.6,
    range : 300,
    shootCone : 24,
    rotateSpeed : 8,
    laserWidth : 0.8,
    health : 160 * 3 * 3,
});
sucker.consumes.powerCond(4, boolf(e => e.target != null));
sucker.requirements = ItemStack.with(
    Items.graphite, 120,
    Items.silicon, 180,
    Items.titanium, 60,
    Items.plastanium, 35
);
sucker.buildVisibility = BuildVisibility.shown;
sucker.category = Category.turret;
exports.sucker = sucker;