const pl = new PayloadLaunchPad("pl");
Object.assign(pl, {
    hasPower : true,
    launchTime : 60 * 10,
    size : 5,
    //alwaysUnlocked : true,
});
pl.consumes.power(5);
pl.requirements = ItemStack.with(
    Items.copper, 80
);
pl.buildVisibility = BuildVisibility.debugOnly;
pl.category = Category.effect;