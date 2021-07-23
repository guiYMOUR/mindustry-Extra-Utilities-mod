const { suzerain } = require("unit/suzerain");
const { nebula } = require("unit/nebula");
const { asphyxia } = require("unit/asphyxia");
const { apocalypse } = require("unit/apocalypse");
const { tera } = require("unit/tera");
const { nihilo } = require("unit/nihilo");
var unitType = (unitFrom, unitTo) => {
    var a = java.util.Arrays.copyOf(Blocks.tetrativeReconstructor.upgrades.get(0), 2);
    a[0] = unitFrom;
    a[1] = unitTo;
    return a;
}

const unitF = extendContent(Reconstructor, "imaginary-reconstructor", {});
unitF.size = 11;
unitF.consumes.power(30);
unitF.consumes.items(ItemStack.with(
    Items.silicon, 800,
    Items.titanium, 650,
    Items.plastanium, 350,
    Items.surgeAlloy, 300,
    Items.phaseFabric, 200
));
unitF.consumes.liquid(Liquids.cryofluid, 4);
unitF.constructTime = 60 * 60 * 3.8;
unitF.liquidCapacity = 180;

unitF.upgrades.addAll(
    unitType(UnitTypes.reign, suzerain),
    unitType(UnitTypes.corvus, nebula),
    unitType(UnitTypes.toxopid, asphyxia),
    unitType(UnitTypes.eclipse, apocalypse),
    unitType(UnitTypes.oct, tera),
    unitType(UnitTypes.omura, nihilo),
);
unitF.requirements = ItemStack.with(
    Items.silicon, 1200,
    Items.graphite, 850,
    Items.titanium, 600,
    Items.thorium, 500,
    Items.plastanium, 600,
    Items.surgeAlloy, 500,
    Items.phaseFabric, 350
);
unitF.buildCostMultiplier = 0.8;
unitF.buildVisibility = BuildVisibility.shown;
unitF.category = Category.units;
exports.unitF = unitF;