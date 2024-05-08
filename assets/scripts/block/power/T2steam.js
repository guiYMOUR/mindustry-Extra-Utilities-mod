const T2ST = extend(ConsumeGenerator, "T2-steam-generator", {});
T2ST.powerProduction = 690/60;
T2ST.itemDuration = 60;
T2ST.consumeLiquid(Liquids.water, 0.15);
T2ST.hasLiquids = true;
T2ST.size = 3;
T2ST.consume(new ConsumeItemFlammable());
T2ST.consume(new ConsumeItemExplode());
T2ST.ambientSound = Sounds.smelter;
T2ST.ambientSoundVolume = 0.06;
T2ST.drawer = new DrawMulti(
    new DrawDefault(),
    new DrawWarmupRegion(),
    (() => {
        const d = new DrawRegion("-turbine")
        d.rotateSpeed = 2;
        return d;
    })(),
    (() => {
        const d = new DrawRegion("-turbine")
        d.rotateSpeed = -2;
        d.rotation = 45;
        return d;
    })(),
    new DrawRegion("-cap"),
    new DrawLiquidRegion()
)
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