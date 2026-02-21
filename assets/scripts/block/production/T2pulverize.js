const lib = require("blib");

const pu = extend(GenericCrafter, "pu", {});
pu.buildType = prov(() => {
    var x = 0, y = 0;
    var totalProgress = 0;
    return new JavaAdapter(GenericCrafter.GenericCrafterBuild, {
        draw(){
            x = this.x;
            y = this.y;
            totalProgress = this.totalProgress;
            Draw.rect(Core.atlas.find(lib.aModName + "-pu-b"), x, y);
            Draw.rect(Core.atlas.find(lib.aModName + "-pu-1"), x, y, 90 + totalProgress * 1.5)
            Draw.rect(Core.atlas.find(lib.aModName + "-pu-2"), x, y, 90 - totalProgress * 3);
            Draw.rect(Core.atlas.find(lib.aModName + "-pu-top"),x, y);
        },
    }, pu);
});
pu.size = 2;
pu.requirements = ItemStack.with(
    Items.copper, 25,
    Items.graphite, 15,
    Items.silicon, 15,
    Items.titanium, 20,
);
pu.buildVisibility = BuildVisibility.shown;
pu.category = Category.crafting;
pu.outputItem = new ItemStack(Items.sand, 3);
pu.craftEffect = Fx.pulverize;
pu.craftTime = 36;
pu.updateEffect = Fx.pulverizeSmall;
pu.hasItems = true;
pu.hasPower = true;
pu.ambientSound = Sounds.loopGrind;
pu.ambientSoundVolume = 0.025;
pu.consumeItem(Items.scrap, 2);
pu.consumePower(1);

exports.pu = pu;