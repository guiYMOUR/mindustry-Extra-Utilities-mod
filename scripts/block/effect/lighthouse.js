const range = 120;
const range2 = 280;
const lighthouse = extendContent(LightBlock, "lighthouse", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range, Pal.accent);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range2, Color.valueOf("ff5555"));
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks);
        this.stats.add(Stat.range, range2 / Vars.tilesize, StatUnit.blocks);
    },
});
lighthouse.buildType = prov(() => {
    return new JavaAdapter(LightBlock.LightBuild, {
        drawLight(){
            this.super$drawLight();
            Vars.indexer.allBuildings(this.x, this.y, range2, cons(other => {
                if(other.team == this.team){
                    if(other.within(this, range)){
                    Drawf.light(this.team, other.x, other.y, other.block.size * (range / 2) * Math.min(this.smoothTime, 2), Tmp.c1.set(this.color), this.block.brightness * this.efficiency());
                    }
                } else {
                    Drawf.light(this.team, other.x, other.y, other.block.size * (range / 4) * Math.min(this.smoothTime, 2), Tmp.c1.set(this.color), this.block.brightness * this.efficiency());
                }
            }));
            Units.nearbyEnemies(this.team, this.x, this.y, range2, cons(unit =>{
                Drawf.light(this.team, unit.x, unit.y, (unit.hitSize / 10) * (range / 4) * Math.min(this.smoothTime, 2), Tmp.c1.set(this.color), this.block.brightness * this.efficiency());
            }));
        },
        drawSelect(){
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, range, Tmp.c1.set(this.color));
            Drawf.dashCircle(this.x, this.y, range2, Color.valueOf("ff5555"));
        },
    }, lighthouse);
});
Object.assign(lighthouse, {
    brightness : 0.8,
    radius : range + 40,
    size : 2,
    alwaysUnlocked : true,
});
lighthouse.consumes.power(0.2);
lighthouse.requirements = ItemStack.with(
    Items.graphite, 24,
    Items.silicon, 12,
    Items.thorium, 10
);
lighthouse.buildVisibility = BuildVisibility.lightingOnly;
lighthouse.category = Category.effect;