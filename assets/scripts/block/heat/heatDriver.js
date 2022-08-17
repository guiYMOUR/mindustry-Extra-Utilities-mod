const lib = require("blib");
const Drawer = require("drawer/DrawHeatDriver");

const range = 45 * 8;//÷8格
const arrowSpacing = 4, arrowOffset = 2, arrowPeriod = 0.4, arrowTimeScl = 6.2;

const heatDriver = extend(HeatConductor, "heat-driver", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range, Pal.accent);
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.shootRange, range, StatUnit.blocks);
    },
    drawPlanRegion(plan, list){
        this.super$drawPlanRegion(plan, list);
        Draw.rect(Core.atlas.find("btm-heat-driver-outline"), plan.drawx(), plan.drawy());
    },
    icons(){
        return [Core.atlas.find("btm-heat-driver"), Core.atlas.find("btm-heat-driver-outline")];
    },
});
heatDriver.size = 3;
heatDriver.configurable = true;
heatDriver.hasPower = true;
heatDriver.consumePower(4);
heatDriver.visualMaxHeat = 30;
heatDriver.drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"), Drawer.DrawHeatDriver(4, 2, 0.4, 6.2));
heatDriver.requirements = ItemStack.with(
    Items.tungsten, 150,
    Items.carbide, 50,
    Items.oxide, 125
);
heatDriver.buildVisibility = BuildVisibility.shown;
heatDriver.category = Category.crafting;
// heatDriver.config(Point2, lib.cons2((tile, point) => {
//     tile.setLink2(Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
// }));
heatDriver.config(java.lang.Integer, lib.cons2((tile, int) => {
    tile.setOneLink(int);
}));


heatDriver.buildType = prov(() => {
    const block = heatDriver;
    var link = -1;
    var owner = new Seq();
    var rotation = 90;
    var progress = 0;
    var resProgress = 0;
    
    return new JavaAdapter(HeatConductor.HeatConductorBuild, {
        setOneLink(v){
            var int = new java.lang.Integer(v);
            link = int;
        },
        setLink2(v){
            link = v;
        },
        setOwner(build){
            owner.add(build);
        },
        removeOwner(build){
            owner.remove(build);
        },
        getOwner(){ return owner; },
        getLink(){
            return link;
        },
        getRotation(){
            return rotation;
        },
        getProgress(){
            return progress;
        },
        getResProgress(){
            return resProgress;
        },
        linkValid(){
            if(link == -1) return false;
            var other = Vars.world.build(link);
            if(other == null){
                link = -1;
                return false;
            }
            return other.block == block && other.team == this.team && this.within(other, range);
        },
        setHeat(v){
            this.heat = v;
        },
        updateTile(){
            this.checkOwner();
            if(owner.size == 0 && link == -1) this.heat = 0;
            var other = Vars.world.build(link);
            var hasLink = this.linkValid();
            if(hasLink){
                if(other.checkOneOwner(this)) other.setOwner(this);
                var torotation = this.angleTo(other);
                rotation = Mathf.slerpDelta(rotation, torotation, 0.02 * this.power.status);
                if(Angles.near(rotation, torotation, 2)){
                    this.updateTransfer();
                    other.updateTransfer();
                    progress = Mathf.slerpDelta(progress, 1, 0.02 * this.power.status);
                } else {
                    progress = Mathf.slerpDelta(progress, 0, 0.04);
                }
            } else {
                progress = Mathf.slerpDelta(progress, 0, 0.04);
            }
            var p = Math.min((this.heat/block.visualMaxHeat), 1);
            if(owner.size > 0 && p > 0){
                resProgress = Mathf.slerpDelta(resProgress, 1, 0.02 * p);
            } else {
                resProgress = Mathf.slerpDelta(resProgress, 0, 0.05);
            }
        },
        updateTransfer(){
            if(owner.size > 0){
                var heat = 0;
                for(var i = 0; i < owner.size; i++){
                    var owners = owner.get(i);
                    var dst = this.dst(owners)/range;
                    if(Angles.near(owners.getRotation(), owners.angleTo(this), 2)){
                        heat += owners.heat*(1 - dst * 0.15);
                        heat *= owners.power.status;
                    }
                }
                this.heat = heat;
            } else {
                this.super$updateTile();
            }
        },
        heat(){
            return owner.size > 0 && link == -1 ? this.heat : 0;
        },
        drawConfigure(){
            const sin = Mathf.absin(Time.time, 6, 1);
            Draw.color(Pal.accent);
            Lines.stroke(1);
            Drawf.circles(this.x, this.y, (block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.accent);
            if(this.linkValid()){
                var other = Vars.world.build(link);
                Drawf.circles(other.x, other.y, (block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.place);
                Drawf.arrow(this.x, this.y, other.x, other.y, block.size * Vars.tilesize + sin, 4 + sin);
            }
            for(var i = 0; owner.size > 0 && i < owner.size; i++){
                Drawf.circles(owner.get(i).x, owner.get(i).y, (block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.place);
                Drawf.arrow(owner.get(i).x, owner.get(i).y, this.x, this.y, block.size * Vars.tilesize + sin, 4 + sin, Pal.place);
            }
            Drawf.dashCircle(this.x, this.y, range, Pal.accent);
        },
        onConfigureBuildTapped(other){
            if(this == other){
                if(link == -1) this.deselect();
                this.configure(-1);
                return false;
            }

            if(link == other.pos()){
                other.removeOwner(this);
                this.configure(-1);
                return false;
            }else if(other.block == this.block && this.dst(other) <= range && other.team == this.team && this.checkOneOwner(other)){
                var lastLink = Vars.world.build(link);
                if(lastLink != null) lastLink.removeOwner(this);
                other.setOwner(this);
                this.configure(other.pos());
                return false;
            }

            return true;
        },
        configure(config){
            link = config;
        },
        checkOwner(){
            for(var i = 0; i < owner.size; i++){
                var pos = owner.get(i).pos();
                var build = Vars.world.build(pos);
                if(build == null || build.block != block || build.getLink() != this.pos()) owner.remove(i);
            }
        },
        checkOneOwner(other){
            if(owner.size == 0) return true;
            var i = 0;
            for(; i < owner.size; i++){
                var pos = owner.get(i).pos();
                if(Vars.world.build(pos) == other) break;
            }
            if(i == owner.size) return true;
            return false;
        },
        // config(){
        //     if(this.tile == null) return null;
        //     return new Packages.arc.math.geom.Point2.unpack(link).sub(this.tile.x, this.tile.y);
        // },
        write(write) {
            this.super$write(write);
            write.i(link);
            write.f(rotation);
            write.f(progress);
        },
        read(read, revision) {
            this.super$read(read, revision);
            link = read.i();
            rotation = read.f();
            progress = read.f();
        },
    }, heatDriver);
});

