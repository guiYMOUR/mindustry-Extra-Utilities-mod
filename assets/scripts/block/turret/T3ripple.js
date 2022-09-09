//
const lib = require("blib");
const bullets = require("other/bullets");
const MultiShootTurret = lib.getClass("ExtraUtilities.worlds.blocks.turret.MultiShootTurret");
const shots = 3;

const T3rip = new MultiShootTurret("T3-ripple");
T3rip.reload = 20;
T3rip.shots = shots;
T3rip.perShoot = 4;
T3rip.shoot = lib.moreShootAlternate(6, shots);
T3rip.targetAir = false;
T3rip.inaccuracy = 10;
//T3rip.xRand = 4;
T3rip.size = 4;
T3rip.ammoEjectBack = 5;
T3rip.ammoUseEffect = Fx.casing3Double;
T3rip.ammoPerShot = 2;
T3rip.cooldownTime = 60;
T3rip.velocityRnd = 0.2;
T3rip.recoilTime = 60;
T3rip.recoil = 6;
T3rip.shake = 2;
T3rip.range = 370;
T3rip.minRange = 50;
T3rip.health = 250 * 3 * 3;
T3rip.shootSound = Sounds.artillery;
lib.Coolant(T3rip, 0.4, 1.5);
T3rip.ammo(
    Items.graphite, lib.toBullet(Blocks.ripple, Items.graphite),
    Items.silicon, lib.toBullet(Blocks.ripple, Items.silicon),
    Items.pyratite, lib.toBullet(Blocks.ripple, Items.pyratite),
    Items.blastCompound, lib.toBullet(Blocks.ripple, Items.blastCompound),
    Items.plastanium, lib.toBullet(Blocks.ripple, Items.plastanium),
    Items.surgeAlloy, bullets.artillerySurge
);
T3rip.requirements = ItemStack.with(
    Items.copper, 300,
    Items.graphite, 220,
    Items.titanium, 155,
    Items.thorium, 100,
    Items.silicon, 85,
    Items.surgeAlloy, 80
);
T3rip.buildVisibility = BuildVisibility.shown;
T3rip.category = Category.turret;

exports.T3rip = T3rip;