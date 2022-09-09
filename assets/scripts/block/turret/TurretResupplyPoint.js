/*
* @author <guiY>
*/
//补给范围
const range = 64;

const TRS = extend(Unloader, "turret-resupply-point", {
    drawPlace(x, y, rotation, valid){
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range, Pal.accent);
    },
    outputsItems(){
        return false;
    },
    setStats(){
       this.super$setStats();
       this.stats.remove(Stat.speed);
       this.stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks);
    },
});
TRS.buildType = prov(() => {
    var x = 0, y = 0;
    var power = 0;
    var sortItem = null;
    var items = null;

    return new JavaAdapter(Unloader.UnloaderBuild, {
        updateTile(){
            x = this.x;
            y = this.y;
            power = this.power.status;
            sortItem = this.sortItem;
            items = this.items;
            Vars.indexer.eachBlock(this, range, boolf(other => other.block instanceof ItemTurret), cons(other => {
                if(power > 0.999 && sortItem != null && items.get(sortItem) > 0 && other.acceptItem(this, sortItem)){
                    other.handleItem(this, sortItem);
                    Fx.itemTransfer.at(x, y, 2, sortItem.color, other);
                    this.items.clear();
                }
            }));
        },
        acceptItem(source, item){
            return item == this.sortItem && this.items.get(item) < 1;
        },
        drawConfigure(){
            this.super$drawConfigure();
            Vars.indexer.eachBlock(this, range, boolf(other => other.block instanceof ItemTurret), cons(other => {
                if(this.sortItem != null && other.block.ammoTypes.get(this.sortItem) != null) Drawf.square(other.x, other.y, other.block.size * Vars.tilesize / 2 + 1, this.sortItem.color);
            }));
            Drawf.dashCircle(this.x, this.y, range, Pal.accent);
        },
        status(){
            return this.power.status > 0.999 ? BlockStatus.active : BlockStatus.noInput;
        },
    }, TRS);
});
Object.assign(TRS, {
    itemCapacity : 1,
    size : 2,
    acceptsItems : true,
    hasPower : true,
});
TRS.consumePower(1);
TRS.requirements = ItemStack.with(
    Items.graphite, 100,
    Items.silicon, 185,
    Items.thorium, 65
    
);
TRS.buildVisibility = BuildVisibility.shown;
TRS.category = Category.turret;

exports.TRS = TRS;