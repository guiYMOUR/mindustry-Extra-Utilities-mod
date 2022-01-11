const T2scatter = extendContent(ItemTurret, "T2-scatter", {});
T2scatter.size = 2;
T2scatter.xRand = 3;
T2scatter.shots = 3;
T2scatter.burstSpacing = 3;
T2scatter.reloadTime = 9;
T2scatter.recoilAmount = 2;
T2scatter.range = 220;
T2scatter.shootCone = 35;
T2scatter.health = 210 * 2 * 2;
T2scatter.inaccuracy = 10;
T2scatter.rotateSpeed = 15;
T2scatter.targetGround = false;
T2scatter.shootSound = Sounds.shootSnap;
T2scatter.ammo(
    Items.scrap, Bullets.flakScrap,
    Items.lead, Bullets.flakLead,
    Items.metaglass, Bullets.flakGlass
);
T2scatter.limitRange(2);
T2scatter.requirements = ItemStack.with(
    Items.copper, 90,
    Items.lead, 70,
    Items.graphite, 25
);
T2scatter.buildVisibility = BuildVisibility.shown;
T2scatter.category = Category.turret;

exports.T2scatter = T2scatter;