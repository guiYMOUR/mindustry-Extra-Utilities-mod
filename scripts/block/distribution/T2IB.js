/*all the advanced transport equipment is here*/
const lib = require("blib");

const ppc = extendContent(StackConveyor, "ppc", {});
ppc.health = 100;
ppc.speed = 8 / 60;
ppc.itemCapacity = 10;
ppc.requirements = ItemStack.with(
    Items.silicon, 1,
    Items.thorium, 1,
    Items.phaseFabric, 1
);
ppc.buildVisibility = BuildVisibility.shown;
ppc.category = Category.distribution;
exports.ppc = ppc;

const T2IB = extendContent(BufferedItemBridge, "T2IB", {});
lib.setBuildingSimple(T2IB, BufferedItemBridge.BufferedItemBridgeBuild, {/**/});
T2IB.size = 1;
T2IB.requirements = ItemStack.with(
    Items.copper, 15,
    Items.lead, 10,
    Items.graphite, 8,
    Items.titanium, 5
);
T2IB.range = 6;
T2IB.speed = 12;
T2IB.health = 85;
T2IB.bufferCapacity = 25;
T2IB.buildVisibility = BuildVisibility.shown;
T2IB.category = Category.distribution;
exports.T2IB = T2IB;

const capacity = 6;
const invertedJunction = extendContent(Junction, "inverted-junction", {});
invertedJunction.buildType = prov(() => {
    var loc = 1;
    return new JavaAdapter(Junction.JunctionBuild, {
        updateTile(){
            for(var i = 0; i < 4; i++){
                var p = (i + loc) % 4;
                if(this.buffer.indexes[i] > 0){
                    if(this.buffer.indexes[i] > capacity) this.buffer.indexes[i] = capacity;
                    var l = this.buffer.buffers[i][0];
                    var time = BufferItem.time(l);
                    if(Time.time >= time + this.block.speed / this.timeScale || Time.time < time){
                        var item = Vars.content.item(BufferItem.item(l));
                        var dest = this.nearby(p);
                        if(item == null || dest == null || !dest.acceptItem(this, item) || dest.team != this.team){
                            continue;
                        }
                        dest.handleItem(this, item);
                        java.lang.System.arraycopy(this.buffer.buffers[i], 1, this.buffer.buffers[i], 0, this.buffer.indexes[i] - 1);
                        this.buffer.indexes[i] --;
                    }
                }
            }
        },
        acceptItem(source, item){
            var relative = source.relativeTo(this.tile);

            if(relative == -1 || !this.buffer.accepts(relative)) return false;
            var to = this.nearby((relative + loc) % 4);
            return to != null && to.team == this.team;
        },
        buildConfiguration(table) {
            table.button(new Packages.arc.scene.style.TextureRegionDrawable(Core.atlas.find("btm-flip", Core.atlas.find("clear"))), Styles.clearTransi, run(() => { this.switchf() })).size(36).tooltip("switch");
        },
        switchf(){
            loc = loc == 1 ? 3 : 1;
            this.deselect();
        },
        write(write) {
            this.super$write(write);
            write.f(loc);
        },
        read(read, revision) {
            this.super$read(read, revision);
            loc = read.f();
        },
    }, invertedJunction);
});
invertedJunction.requirements = ItemStack.with(
    Items.copper, 2
);
invertedJunction.buildVisibility = BuildVisibility.shown;
invertedJunction.category = Category.distribution;
invertedJunction.speed = 26;
invertedJunction.capacity = capacity;
invertedJunction.health = 30;
invertedJunction.configurable = true;
invertedJunction.buildCostMultiplier = 5;
exports.invertedJunction = invertedJunction;

const TJ = extendContent(Junction, "TJ", {});
TJ.requirements = ItemStack.with(
    Items.copper, 5,
    Items.lead, 2,
    Items.titanium, 3
);
TJ.buildVisibility = BuildVisibility.shown;
TJ.category = Category.distribution;
TJ.speed = (26 + 2) / 4;
TJ.capacity = 14;
TJ.health = 80;
TJ.buildCostMultiplier = 5;
exports.TJ = TJ;

const TR = extendContent(Router, "TR", {});
TR.requirements = ItemStack.with(
    Items.copper, 3,
    Items.lead, 3,
    Items.titanium, 2
);
TR.buildVisibility = BuildVisibility.shown;
TR.category = Category.distribution;
TR.speed = 8 / 4;
TR.health = 80;
TR.buildCostMultiplier = 4;
exports.TR = TR;

const T2LB = extendContent(LiquidExtendingBridge , "TLB",{});
T2LB.requirements = ItemStack.with(
    Items.graphite, 8,
    Items.metaglass, 9,
    Items.titanium, 8
);
T2LB.buildVisibility = BuildVisibility.shown;
T2LB.category = Category.liquid;
T2LB.health = 85;
T2LB.range = 6;
T2LB.hasPower = false;
exports.T2LB = T2LB;

const TLR = extendContent(LiquidRouter, "TLR", {});
TLR.liquidCapacity = 20;
TLR.liquidPressure = 1.1;
TLR.health = 90;
TLR.requirements = ItemStack.with(
    Items.graphite, 6,
    Items.metaglass, 4,
    Items.titanium, 5
);
TLR.buildVisibility = BuildVisibility.shown;
TLR.category = Category.liquid;
exports.TLR = TLR;
