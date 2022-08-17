const th2 = extend(NuclearReactor, "Th2", {});
th2.ambientSound = Sounds.hum;
th2.ambientSoundVolume = 0.24;
th2.size = 4;
th2.itemCapacity = 40;
th2.health = 2400;
th2.itemDuration = 300;
th2.powerProduction = 2400/60;
th2.consumes.item(Items.thorium);
th2.heating = 0.024;
th2.explosionRadius = 50;
th2.explosionDamage = 2400;
th2.consumes.liquid(Liquids.cryofluid, 0.024 / 0.5).update = false;//js的在consume里面update等的7.0写法为赋值
th2.requirements = ItemStack.with(
    Items.lead, 400,
    Items.silicon, 300,
    Items.graphite, 250,
    Items.thorium, 300,
    Items.metaglass, 80,
    Items.phaseFabric, 18
    
);
th2.buildCostMultiplier = 0.85;
th2.buildVisibility = BuildVisibility.shown;
th2.category = Category.power;

exports.th2 = th2;