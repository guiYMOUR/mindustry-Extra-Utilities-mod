const lib = require("blib");
const emptyLightColor = Color.valueOf("f8c266");//f7bd5d
const fullLightColor = Color.valueOf("fb9567");

const png = extendContent(PowerNode, "power-node-giant", {});
lib.setBuildingSimple(png, PowerNode.PowerNodeBuild, {
    draw(){
        this.super$draw();
        Draw.z(Layer.power - 1);
        Draw.color(emptyLightColor, fullLightColor, this.power.status);
        Draw.rect(Core.atlas.find("btm-png-capacity"),this.x,this.y);
    },
});
png.size = 3;
png.maxNodes = 30;
png.laserRange = 19;
png.outputsPower = true;
png.consumesPower = true;
png.consumes.powerBuffered(30000);
png.requirements = ItemStack.with(
    Items.titanium, 40,
    Items.lead, 55,
    Items.graphite, 30,
    Items.silicon, 45
);
png.buildVisibility = BuildVisibility.shown;
png.category = Category.power;

exports.png = png;