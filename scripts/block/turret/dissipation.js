/**
 * readme <Inspiration comes from Rusted Warfare>
 */
//能量槽
const maxShot = 60 * 40;
//范围
const range = 220;
//没什么用的激光颜色
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
    
    var block = dissipation;
    var timeScale = 0;
    var liquids = null;
    var x = 0, y = 0;
    var baseReloadSpeed = 0;
    
    return new JavaAdapter(PointDefenseTurret.PointDefenseBuild, {
        getShot(){
            return shot;
        },
        getCool(){
            return cooldown;
        },
        updateTile(){
            timeScale = this.timeScale;
            liquids = this.liquids;
            x = this.x;
            y = this.y;
            baseReloadSpeed = this.baseReloadSpeed()
            if(shot <= 0 && !cooldown) cooldown = true;
            if(shot >= maxShot && cooldown) cooldown = false;
            if(cooldown){
                var maxUsed = block.consumes.get(ConsumeType.liquid).amount;
                var liquid = liquids.current();
                var used = Math.min(liquids.get(liquid), maxUsed * Time.delta) * baseReloadSpeed;
                shot = liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 2 * (1 + (liquid.heatCapacity - 0.4) * 0.9) * block.coolantMultiplier * this.baseReloadSpeed() * 0.28 * timeScale) : Math.min(maxShot, shot + 2 * baseReloadSpeed * timeScale);
                liquids.remove(liquid, used);
                if(Mathf.chance(0.06 * used)){
                    block.coolEffect.at(x + Mathf.range(block.size * Vars.tilesize / 2), y + Mathf.range(block.size * Vars.tilesize / 2));
                }
            } else {
                //cooldown = false;
                var maxUsed = block.consumes.get(ConsumeType.liquid).amount;
                var liquid = liquids.current();
                var used = Math.min(liquids.get(liquid), maxUsed * Time.delta) * baseReloadSpeed;
                shot = liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 5 * (1 + (liquid.heatCapacity - 0.4) * 0.9) * block.coolantMultiplier * this.baseReloadSpeed() * 0.28 * timeScale) : Math.min(maxShot, shot + 5 * baseReloadSpeed * timeScale);
                this.target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b => b.team != this.team && b.type.hittable, b => b.dst2(this));
                if(this.target != null && !this.target.isAdded()){
                    this.target = null;
                }
                if(block.acceptCoolant && this.target != null){
                    liquids.remove(liquid, used);

                    if(Mathf.chance(0.06 * used)){
                        block.coolEffect.at(x + Mathf.range(block.size * Vars.tilesize / 2), y + Mathf.range(block.size * Vars.tilesize / 2));
                    }
                }
                if(this.target != null && this.target.within(this, range) && this.target.team != this.team && this.target.type != null && this.target.type.hittable){
                    var dest = this.angleTo(this.target);
                    this.rotation = Angles.moveToward(this.rotation, dest, this.block.rotateSpeed * this.edelta());
                    if(Angles.within(this.rotation, dest, block.shootCone)){
                        this.target.remove();
                        shot = Math.max(0, shot - 30);
                        Tmp.v1.trns(this.rotation, block.shootLength);

                        block.beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, this.rotation, color, new Vec2().set(this.target));
                        block.shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, this.rotation, color);
                        block.hitEffect.at(this.target.x, this.target.y, color);
                        block.shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9, 1.1));
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
dissipation.consumes.powerCond(12, boolf(e => e.getCool() || e.target != null));
dissipation.requirements = ItemStack.with(
    Items.silicon, 250,
    Items.titanium, 200,
    Items.surgeAlloy, 100,
    Items.phaseFabric, 120
);
dissipation.buildVisibility = BuildVisibility.shown;
dissipation.category = Category.turret;
exports.dissipation = dissipation;
