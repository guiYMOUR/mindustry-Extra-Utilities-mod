const heatC = new HeatConductor("heat-transfer");
heatC.researchCostMultiplier = 8;
heatC.size = 2;
heatC.drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
heatC.regionRotated1 = 1;
heatC.requirements = ItemStack.with(
    Items.tungsten, 10,
    Items.graphite, 8,
    Items.oxide, 5
);
heatC.buildVisibility = BuildVisibility.shown;
heatC.category = Category.crafting;

exports.heatC = heatC;