/**
 * @author guiY<guiYMOUR>
 * @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
 */
//引用部分，类似import，对应的是exports导出
const items = require("game/items");

//速度，一般性不用改
const speed = 2;

//液体卸货器就是用卸货器的码改液体源
const lu = extendContent(LiquidSource, "liquid-unloader", {
    drawRequestConfig(req, list){
        this.drawRequestConfigCenter(req, req.config, Core.atlas.find("btm-liquid-unloader-centre"), true);
    },
});
lu.buildType = prov(() => {
    var dumpingTo = null;
    var offset = 0;
    var liquidBegin = null;
    
    const block = lu;
    var source = null;
    var proximity = null;
    
    return new JavaAdapter(LiquidSource.LiquidSourceBuild, {
        updateTile(){
            source = this.source;
            proximity = this.proximity;
            if(liquidBegin != source){
                this.liquids.clear();
                liquidBegin = source;
            }
            for(var i = 0; i < proximity.size; i++){
                var pos = (offset + i) % proximity.size;
                var other = proximity.get(pos);

                if(other.interactable(this.team) && other.block.hasLiquids && !(other instanceof LiquidBlock.LiquidBuild && other.block.size == 1) && source != null && other.liquids.get(source) > 0){
                    dumpingTo = other;
                    if(this.liquids.total() < block.liquidCapacity){
                        var amount = Math.min(speed, other.liquids.get(source));
                        this.liquids.add(source, amount);
                        other.liquids.remove(source, amount);
                    }
                }
            }
            if(proximity.size > 0){
                offset ++;
                offset %= proximity.size;
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
    Items.metaglass, 18,
    Items.silicon, 18,
    Items.titanium, 12
);
lu.buildVisibility = BuildVisibility.shown;
lu.category = Category.effect;
exports.lu = lu;
