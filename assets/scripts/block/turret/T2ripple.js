//
const lib = require("blib");
const shots = 2;
const MultiShootTurret = lib.getClass("ExtraUtilities.worlds.blocks.turret.MultiShootTurret");
const hardMod = lib.hardMod;

const T2rip = new MultiShootTurret("T2-ripple");
T2rip.reload = 30;
T2rip.shoot = lib.moreShootAlternate(8, shots);
T2rip.shots = shots;
T2rip.targetAir = false;
T2rip.inaccuracy = 10;
//T2rip.xRand = 4;
T2rip.size = 3;
T2rip.ammoEjectBack = 5;
T2rip.ammoUseEffect = Fx.casing3Double;
T2rip.ammoPerShot = 2;
T2rip.cooldownTime = 60;
T2rip.velocityRnd = 0.2;
T2rip.recoilTime = 60;
T2rip.recoil = 6;
T2rip.shake = 2;
T2rip.range = 320;
T2rip.minRange = 50;
T2rip.health = 180 * 3 * 3;
T2rip.shootSound = Sounds.shootArtillery;
lib.Coolant(T2rip, 0.3, 2.5);
T2rip.ammoTypes = Blocks.ripple.ammoTypes;
T2rip.requirements = ItemStack.with(
    Items.copper, 200,
    Items.graphite, 150,
    Items.titanium, 70,
    Items.silicon, 40 + (hardMod ? 30 : 0)
);
T2rip.buildVisibility = BuildVisibility.shown;
T2rip.category = Category.turret;

exports.T2rip = T2rip;