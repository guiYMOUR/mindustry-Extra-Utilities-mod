/*
* readme <Inspiration comes from Rusted Warfare>
*/
const maxShot = 60 * 40;
const range = 220;
const color = Color.white;
const dissipation = extendContent(PointDefenseTurret, "dissipation", {
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.reload);
    },
    setBars() {
        this.super$setBars();
        this.bars.add("amount", func(entity => {
            var bar = new Bar(prov( () => Core.bundle.format("bar.btm-shotAmount")), prov(() => entity.getCool() ? Pal.gray : Color.blue), floatp(() => entity.getShot() / maxShot));
            return bar;
        }));
    },
});
dissipation.buildType = prov(() => {
    var shot = maxShot;
    var cooldown = false;
    return new JavaAdapter(PointDefenseTurret.PointDefenseBuild, {
        getShot(){
            return shot;
        },
        getCool(){
            return cooldown;
        },
        updateTile(){
            if(shot <= 0 && !cooldown) cooldown = true;
            if(shot >= maxShot && cooldown) cooldown = false;
            if(cooldown){
                var maxUsed = this.block.consumes.get(ConsumeType.liquid).amount;
                var liquid = this.liquids.current();
                var used = Math.min(this.liquids.get(liquid), maxUsed * Time.delta) * this.baseReloadSpeed();
                shot = this.liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 2 * (1 + (liquid.heatCapacity - 0.4) * 0.9) * this.block.coolantMultiplier * this.baseReloadSpeed() * 0.28 * this.timeScale) : Math.min(maxShot, shot + 2 * this.baseReloadSpeed() * this.timeScale);
                this.liquids.remove(liquid, used);
                if(Mathf.chance(0.06 * used)){
                    this.block.coolEffect.at(this.x + Mathf.range(this.block.size * Vars.tilesize / 2), this.y + Mathf.range(this.block.size * Vars.tilesize / 2));
                }
            } else {
                //cooldown = false;
                var maxUsed = this.block.consumes.get(ConsumeType.liquid).amount;
                var liquid = this.liquids.current();
                var used = Math.min(this.liquids.get(liquid), maxUsed * Time.delta) * this.baseReloadSpeed();
                shot = this.liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 5 * (1 + (liquid.heatCapacity - 0.4) * 0.9) * this.block.coolantMultiplier * this.baseReloadSpeed() * 0.28 * this.timeScale) : Math.min(maxShot, shot + 5 * this.baseReloadSpeed() * this.timeScale);
                this.target = Groups.bullet.intersect(this.x - range, this.y - range, range*2, range*2).min(b => b.team != this.team && b.type.hittable, b => b.dst2(this));
                if(this.target != null && !this.target.isAdded()){
                    this.target = null;
                }
                if(this.block.acceptCoolant && this.target != null){
                    this.liquids.remove(liquid, used);

                    if(Mathf.chance(0.06 * used)){
                        this.block.coolEffect.at(this.x + Mathf.range(this.block.size * Vars.tilesize / 2), this.y + Mathf.range(this.block.size * Vars.tilesize / 2));
                    }
                }
                if(this.target != null && this.target.within(this, range) && this.target.team != this.team && this.target.type != null && this.target.type.hittable){
                    var dest = this.angleTo(this.target);
                    this.rotation = Angles.moveToward(this.rotation, dest, this.block.rotateSpeed * this.edelta());
                    if(Angles.within(this.rotation, dest, this.block.shootCone)){
                        this.target.remove();
                        shot = Math.max(0, shot - 30);
                        Tmp.v1.trns(this.rotation, this.block.shootLength);

                        this.block.beamEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation, color, new Vec2().set(this.target));
                        this.block.shootEffect.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, this.rotation, color);
                        this.block.hitEffect.at(this.target.x, this.target.y, color);
                        this.block.shootSound.at(this.x + Tmp.v1.x, this.y + Tmp.v1.y, Mathf.random(0.9, 1.1));
                    }
                }
            }
        },
        draw(){
            this.super$draw();
            Draw.color();
            Draw.alpha(cooldown ? 1 : 0);
            Draw.rect(Core.atlas.find("btm-dissipation-low"), this.x, this.y, this.rotation - 90);
        },
        write(write) {
            this.super$write(write);
            write.bool(cooldown);
            write.f(shot);
        },
        read(read, revision) {
            this.super$read(read, revision);
            cooldown = read.bool();
            shot = read.f();
        },
    }, dissipation);
});
Object.assign(dissipation, {
    hasPower : true,
    size : 3,
    range : range,
    shootCone : 36,
    rotateSpeed : 12,
    shootLength : 8,
    health : 250 * 3 * 3,
    acceptCoolant : true,
    coolantMultiplier : 5,
});
dissipation.consumes.powerCond(10, boolf(e => e.getCool() || e.target != null));
dissipation.requirements = ItemStack.with(
    Items.silicon, 180,
    Items.thorium, 100,
    Items.surgeAlloy, 70,
    Items.phaseFabric, 55
);
dissipation.buildVisibility = BuildVisibility.shown;
dissipation.category = Category.turret;
exports.dissipation = dissipation;