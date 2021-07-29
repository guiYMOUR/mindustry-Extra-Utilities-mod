/*
* @author <guiY>
* @readme <My first attempt was to integrate the stack conveyor and the item bridge, but it did not achieve the effect I wanted, so now I'm writing bullets to get what I want.Unfortunately, Plan B, which I also chose to give up temporarily for design and time reasons, is a failure.I'm sorry, but I'll keep trying in future versions.>
*/
//const stackRegion = Core.atlas.find("btm-ppc-stack");
const stackBridge = extendContent(BufferedItemBridge, "stackBridge", {});
stackBridge.buildType = prov(() => {
    var lastItem = null;
    var amount = 0;
    return new JavaAdapter(BufferedItemBridge.BufferedItemBridgeBuild, {
        setLastItem(v){
            lastItem = v;
        },
        getLastItem(){
            return lastItem;
        },
        setAmount(v){
            amount = v;
        },
        getAmount(){
            return amount;
        },
        updateTile(){
            if(this.getLastItem() == null || !this.items.has(this.getLastItem())){
                this.setLastItem(this.items.first());
            }
            this.super$updateTile();
        },
        updateTransport(other){
            if(this.items.total() >= this.block.itemCapacity && other != null && other.items.total() < this.block.itemCapacity){
                other.setAmount(this.items.total());
                other.items.add(lastItem, other.getAmount());
                Fx.plasticburn.at(this);
                this.items.clear();
            }
        },
        doDump(){
            for(var i = 0; i < 3; i++){
                this.dumpAccumulate();
            }
        },
        draw(){
            this.super$draw();
            var other = Vars.world.build(this.link);
            if(other == null || this.getLastItem() == null || this.link == -1) return;
            var angle = this.angleTo(other);
            //Draw.rect(stackRegion, this.x, this.y, angle);
            var size = Vars.itemSize * Mathf.lerp(Math.min(this.items.total() / this.block.itemCapacity, 1), 1, 0.4);
            Drawf.shadow(Tmp.v1.x, Tmp.v1.y, size * 1.2);
            Draw.rect(this.getLastItem().icon(Cicon.medium), this.x, this.y, size, size, 0);
        },
        acceptItem(source, item){
            if(this == source && this.items.total() < this.block.itemCapacity) return true;
            var other = Vars.world.tile(this.link);
            return (!((this.items.any() && !this.items.has(item)) || (this.items.total() >= this.getMaximumAccepted(item)))) && other != null && this.block.linkValid(this.tile, other);
        },
    }, stackBridge);
});
stackBridge.itemCapacity = 20;
stackBridge.size = 1;
stackBridge.range = 8;
stackBridge.requirements = ItemStack.with(
    Items.lead, 15,
    Items.silicon, 12,
    Items.titanium, 15,
    Items.plastanium, 10
);
stackBridge.buildVisibility = BuildVisibility.shown;
stackBridge.category = Category.distribution;

exports.stackBridge = stackBridge;