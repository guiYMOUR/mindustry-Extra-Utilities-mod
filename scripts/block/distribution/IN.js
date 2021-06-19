const range = 20;
const lib = require("blib");

const IN = extendContent(ExtendingItemBridge, "i-node", {
    drawPlace(x, y, rotation, valid){
		Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, (range + 1) * Vars.tilesize, Pal.accent);
    },
    linkValid(tile, other, checkDouble){
		if(other == null || tile == null || other == tile) return false;
		if(Math.pow(other.x - tile.x, 2) + Math.pow(other.y - tile.y, 2) > Math.pow(range + 0.5, 2)) return false;
		return ((other.block() == tile.block() && tile.block() == this) || (!(tile.block() instanceof ItemBridge) && other.block() == this))
            && (other.team == tile.team || tile.block() != this)
            && (!checkDouble || other.build.link != tile.pos());
	},
});
lib.setBuildingSimple(IN, ExtendingItemBridge.ExtendingItemBridgeBuild, {
	drawConfigure() {
		const sin = Mathf.absin(Time.time, 6, 1);

		Draw.color(Pal.accent);
		Lines.stroke(1);
		Drawf.circles(this.x, this.y, (this.block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.accent);
		const other = Vars.world.build(this.link);
		if(other != null){
			Drawf.circles(other.x, other.y, (this.block.size / 3 + 1) * Vars.tilesize + sin - 2, Pal.place);
			Drawf.arrow(this.x, this.y, other.x, other.y, this.block.size * Vars.tilesize + sin, 4 + sin, Pal.accent);
		}
		Drawf.dashCircle(this.x, this.y, range * Vars.tilesize, Pal.accent);
	},
	draw(){
	    //this.super$draw();
        Draw.rect(Core.atlas.find("btm-i-node"),this.x,this.y);
        Draw.z(Layer.power);
        var bridgeRegion = Core.atlas.find("btm-i-node-bridge");
        var endRegion = Core.atlas.find("btm-i-node-end");
        var other = Vars.world.build(this.link);
        if(other == null) return;
        var op = Core.settings.getInt("bridgeopacity") / 100;
        if(Mathf.zero(op)) return;

        Draw.color(Color.white);
        Draw.alpha(Math.max(this.power.status, 0.25) * op);

        Draw.rect(endRegion, this.x, this.y);
        Draw.rect(endRegion, other.x, other.y);

        Lines.stroke(8);

        Tmp.v1.set(this.x, this.y).sub(other.x, other.y).setLength(Vars.tilesize/2).scl(-1);

        Lines.line(bridgeRegion,
            this.x,
            this.y,
            other.x,
            other.y, false);
        Draw.reset();
    },
});
IN.hasPower = true;
IN.consumes.power(1.5);
IN.size = 1;
IN.requirements = ItemStack.with(
    Items.copper, 150,
    Items.lead, 80,
    Items.silicon, 110,
    Items.graphite, 85,
    Items.titanium, 45,
    Items.thorium, 40,
    Items.phaseFabric, 25
);
IN.buildVisibility = BuildVisibility.shown;
IN.category = Category.distribution;

exports.IN = IN;