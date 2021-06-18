/*
* @author guiY
* @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
*/
const speed = 2;

const lu = extendContent(LiquidSource, "liquid-unloader", {
    drawRequestConfig(req, list){
        this.drawRequestConfigCenter(req, req.config, Core.atlas.find("btm-liquid-unloader-centre"), true);
    },
});
lu.buildType = prov(() => {
    var dumpingTo = null;
    var offset = 0;
    var liquidBegin = null;
    return new JavaAdapter(LiquidSource.LiquidSourceBuild, {
        updateTile(){
            if(liquidBegin != this.source){
                this.liquids.clear();
                liquidBegin = this.source;
            }
            for(var i = 0; i < this.proximity.size; i++){
                var pos = (offset + i) % this.proximity.size;
                var other = this.proximity.get(pos);

                if(other.interactable(this.team) && other.block.hasLiquids && this.source != null && other.liquids.get(this.source) > 0){
                    dumpingTo = other;
                    if(this.liquids.total() < this.block.liquidCapacity){
                        var amount = Math.min(speed, other.liquids.get(this.source));
                        this.liquids.add(this.source, amount);
                        other.liquids.remove(this.source, amount);
                    }
                }
            }
            if(this.proximity.size > 0){
                offset ++;
                offset %= this.proximity.size;
            }
            this.dumpLiquid(this.liquids.current());
        },
        canDumpLiquid(to, liquid){
            return to != dumpingTo;
        },
        draw(){
            Draw.rect(Core.atlas.find("btm-liquid-unloader"), this.x, this.y);
            if(this.source == null){
                Draw.rect("cross", this.x, this.y);
            }else{
                Draw.color(this.source.color);
                Draw.rect(Core.atlas.find("btm-liquid-unloader-centre"), this.x, this.y);
                Draw.color();
            }
        },
    }, lu);
});
lu.health = 70;
lu.liquidCapacity = 10;
lu.requirements = ItemStack.with(
    Items.metaglass, 10,
    Items.silicon, 20,
    Items.titanium, 15
);
lu.buildVisibility = BuildVisibility.shown;
lu.category = Category.effect;
exports.lu = lu;