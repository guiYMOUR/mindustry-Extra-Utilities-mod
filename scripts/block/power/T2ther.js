var lib = require("blib");
const plasma1 = Color.valueOf("ffd06b");
const plasma2 = Color.valueOf("ff361b");
const T2ther = extendContent(ThermalGenerator, "T2ther", {
    setBars(){
        this.super$setBars();
        this.bars.add("load", func(entity => {
            var bar = new Bar(prov( () => "loading"), prov( () => Color.valueOf("ffd06d")), floatp( () => entity.getPowerProduction() / (entity.block.powerProduction * entity.productionEfficiency)));
            return bar;
        }));
    },
});
T2ther.buildType = prov(() => {
    var st = 0;
    return new JavaAdapter(ThermalGenerator.ThermalGeneratorBuild, {
        getPowerProduction(){
            st = Math.min(Mathf.lerpDelta(st, 2, 0.0002), 1);
            return this.block.powerProduction * this.productionEfficiency * st;
        },
        draw(){
            var x = this.x;
            var y = this.y;
            Draw.rect(Core.atlas.find("btm-T2ther-b"),x,y);
            const plasmas = 4;
            var plasmaRegions = new Array();
            for(var i = 0; i < plasmas; i++){
                plasmaRegions[i] = "btm-T2ther-plasma-"+i;
            }
            for(var i = 0; i < plasmas; i++){
                var r = 29 + Mathf.absin(Time.time, 2 + i * 1, 5 - i * 0.5);
                Draw.color(plasma2, plasma1, st);
                Draw.alpha(st);
                Draw.rect(Core.atlas.find(plasmaRegions[i]), x, y,Time.time * (12 + i * 6) * 1);
            }
            Draw.color();
            Draw.alpha(1);
            Draw.rect(Core.atlas.find("btm-T2ther-top"),x,y);
        },
        write(write) {
            this.super$write(write);
            write.f(st);
        },
        read(read, revision) {
            this.super$read(read, revision);
            st = read.f();
        },
    }, T2ther);
});
T2ther.requirements = ItemStack.with(
    Items.copper, 300,
    Items.graphite, 100,
    Items.lead, 350,
    Items.silicon, 125,
    Items.titanium, 75,
    Items.thorium, 50,
    Items.metaglass, 80
);
T2ther.buildVisibility = BuildVisibility.shown;
T2ther.category = Category.power;
T2ther.powerProduction = 276/60;
T2ther.generateEffect = Fx.none;
T2ther.size = 3;
T2ther.floating = true;
T2ther.ambientSound = Sounds.hum;
T2ther.ambientSoundVolume = 0.06;
exports.T2ther = T2ther;
