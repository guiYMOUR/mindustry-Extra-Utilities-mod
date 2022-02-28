const items = require("game/items");

const { suzerain } = require("unit/suzerain");
const { nebula } = require("unit/nebula");
const { asphyxia } = require("unit/asphyxia");
const { apocalypse } = require("unit/apocalypse");
const { tera } = require("unit/tera");
const { nihilo } = require("unit/nihilo");
const { narwhal } = require("unit/narwhal");
var unitType = (unitFrom, unitTo) => {
    var a = java.util.Arrays.copyOf(Blocks.tetrativeReconstructor.upgrades.get(0), 2);
    a[0] = unitFrom;
    a[1] = unitTo;
    return a;
}
exports.unitType = unitType;

const unitF = extendContent(Reconstructor, "imaginary-reconstructor", {});
unitF.size = 11;
unitF.consumes.power(30);
unitF.consumes.items(ItemStack.with(
    Items.silicon, 1200,
    Items.titanium, 800,
    Items.plastanium, 600,
    Items.phaseFabric, 400,
    items.lightninAlloy, 150
));
unitF.consumes.liquid(Liquids.cryofluid, 4);
unitF.constructTime = 60 * 60 * 5;
unitF.liquidCapacity = 180;

unitF.upgrades.addAll(
    unitType(UnitTypes.reign, suzerain),
    unitType(UnitTypes.corvus, nebula),
    unitType(UnitTypes.toxopid, asphyxia),
    unitType(UnitTypes.eclipse, apocalypse),
    unitType(UnitTypes.oct, tera),
    unitType(UnitTypes.omura, nihilo),
    unitType(UnitTypes.navanax, narwhal),
);
unitF.requirements = ItemStack.with(
    Items.silicon, 4000,
    Items.graphite, 2500,
    Items.titanium, 2000,
    Items.thorium, 1200,
    Items.plastanium, 800,
    Items.phaseFabric, 500,
    items.lightninAlloy, 200
);
unitF.buildCostMultiplier = 0.8;
unitF.buildVisibility = BuildVisibility.shown;
unitF.category = Category.units;
exports.unitF = unitF;
