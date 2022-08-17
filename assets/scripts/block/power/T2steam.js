const lib = require("blib");

const T2ST = extend(BurnerGenerator, "T2-steam-generator", {});
lib.setBuildingSimple(T2ST, BurnerGenerator.BurnerGeneratorBuild, {});
T2ST.powerProduction = 13;
T2ST.itemDuration = 60;
T2ST.consumes.liquid(Liquids.water, 0.15);
T2ST.hasLiquids = true;
T2ST.size = 3;

T2ST.ambientSound = Sounds.smelter;
T2ST.ambientSoundVolume = 0.06;
T2ST.requirements = ItemStack.with(
    Items.copper, 65,
    Items.graphite, 50,
    Items.lead, 60,
    Items.titanium, 55,
    Items.silicon, 50
);
T2ST.buildVisibility = BuildVisibility.shown;
T2ST.category = Category.power;

exports.T2ST = T2ST;