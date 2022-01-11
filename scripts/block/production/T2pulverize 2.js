const pu = extendContent(GenericCrafter, "pu", {});
pu.buildType = prov(() => {
    return new JavaAdapter(GenericCrafter.GenericCrafterBuild, {
        draw(){
            Draw.rect(Core.atlas.find("btm-pu-b"), this.x, this.y);
            Draw.rect(Core.atlas.find("btm-pu-1"), this.x, this.y, 90 + this.totalProgress * 1.5)
            Draw.rect(Core.atlas.find("btm-pu-2"), this.x, this.y, 90 - this.totalProgress * 3);
            Draw.rect(Core.atlas.find("btm-pu-top"),this.x, this.y);
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
pu.ambientSound = Sounds.grinding;
pu.ambientSoundVolume = 0.025;
pu.consumes.item(Items.scrap, 2);
pu.consumes.power(1);

exports.pu = pu;