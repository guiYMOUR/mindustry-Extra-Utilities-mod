const T2kiln = extendContent(AttributeCrafter, "T2kiln", {});
T2kiln.craftEffect = Fx.smeltsmoke;
T2kiln.outputItem = new ItemStack(Items.metaglass, 8);
T2kiln.craftTime = 36;
T2kiln.size = 3;
T2kiln.hasPower = true;
T2kiln.drawer = new DrawSmelter(Color.valueOf("ffef99"));
T2kiln.flameColor = Color.valueOf("ffc099");
T2kiln.consumes.power(1);
T2kiln.consumes.items(ItemStack.with(
    Items.lead, 4,
    Items.sand, 4,
    Items.pyratite, 1
));
T2kiln.requirements = ItemStack.with(
    Items.silicon, 55,
    Items.copper, 100,
    Items.lead, 80,
    Items.graphite, 50,
    Items.titanium, 90
);
T2kiln.itemCapacity = 10;
T2kiln.boostScale = 0.4;
T2kiln.buildVisibility = BuildVisibility.shown;
T2kiln.category = Category.crafting;

exports.T2kiln = T2kiln;
