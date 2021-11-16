/*
* @author <guiY>
*/

const range = 64;

const TRS = extendContent(Unloader, "turret-resupply-point", {
    drawPlace(x, y, rotation, valid){
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
    return new JavaAdapter(Unloader.UnloaderBuild, {
        updateTile(){
            Vars.indexer.eachBlock(this, range, boolf(other => other.block instanceof ItemTurret), cons(other => {
                if(this.power.status > 0.999 && this.sortItem != null && this.items.get(this.sortItem) > 0 && other.acceptItem(this, this.sortItem)){
                    other.handleItem(this, this.sortItem);
                    Fx.itemTransfer.at(this.x, this.y, 2, this.sortItem.color, other);
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
TRS.consumes.power(1);
TRS.requirements = ItemStack.with(
    Items.graphite, 100,
    Items.titanium, 95,
    Items.silicon, 115,
    Items.thorium, 75
    
);
TRS.buildVisibility = BuildVisibility.shown;
TRS.category = Category.turret;

exports.TRS = TRS;