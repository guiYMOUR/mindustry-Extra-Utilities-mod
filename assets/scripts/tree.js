//The single quotation mark is used because it looks comfortable.
//科技树部分
const lib = require('blib');
const items = require('game/items');


const { T2duo } = require('block/turret/T2duo');
const { T3duo } = require('block/turret/T3duo');
const { T2scatter } = require('block/turret/T2scatter');
const { T2scorch } = require('block/turret/T2scorch');
const { IM } = require('block/turret/IceCookie');
const { shotgun } = require('block/turret/shotgun');
const { sakura } = require('block/turret/sakura');
const { rainbow } = require('block/turret/rainbow');
const { hurricane } = require('block/turret/hurricane');
const { ms } = require('block/turret/ms');
// const { sunburst } =require('block/turret/sunburst');
const { T2lan } = require('block/turret/T2lancer');
const { stinger } = require('block/turret/stinger');
const { swT2 } = require('block/turret/T2swarmer');
const { swMin } = require('block/turret/miniswarmer');
const { T2rip } = require('block/turret/T2ripple');
const { T3rip } = require('block/turret/T3ripple');
const { T2fuse } = require('block/turret/T2fuse');
const { T3fuse } = require('block/turret/T3fuse');
const { minisp } = require('block/turret/minisp');
// const { antiaircraft } = require('block/turret/antiaircraft');
// const { sam } = require('block/turret/sam');
const { RG } = require('block/turret/RG');
const { prism } = require('block/turret/prism');
// const { blackhole } = require('block/turret/blackhole');
// const { sucker } = require('block/turret/sucker');
// const { dissipation } = require('block/turret/dissipation');
// const { heal } = require('block/turret/heal');
// const { TRS } = require('block/turret/TurretResupplyPoint');
//
const { arNode, png } = require('block/power/png');
const { T2ST } = require('block/power/T2steam');
// const { T2ther } = require('block/power/T2ther');
const { th2 } = require('block/power/th2');
// const { LG } = require('block/power/LightninGenerator');
//
// const { LB } = require('block/distribution/LB');
const { driver } = require('block/distribution/driver');
const { alloyDuct, ppc, T2IB, invertedJunction, TJ, TIJ, TR, T2LB, conduit } = require('block/distribution/T2IB');
//const { stackBridge } = require('block/distribution/stackBridge');
//const { IN } = require('block/distribution/IN');
const { T2kiln } = require('block/production/T2kiln');
const { T2melter } = require('block/production/T2melter');
const { T2PC } = require('block/production/T2PC');
const { T2PF } = require('block/production/T2PF');
const { T2SA } = require('block/production/T2SA');
const { T2CM } = require('block/production/T2CM');
const { GC } = require("block/power/GeneratorCrafter");
const { pu } = require("block/production/T2pulverize");
//const { crusher } = require("block/production/crusher");
const { crispSteelSmelter } = require('block/production/CrispSteelSmelter');
const { T2CSm } = require("block/production/T2CrispSteelSmelter");
//const { LA } = require('block/production/LA');
const { ai } = require('block/production/AdjustableIncinerator');
//const { DCF } = require('block/effect/DCF');
const { tiDrill, /*drill, */shovel, slagE, T2WE, T2CU, blastOilExtractor, dustExtractor } = require('block/production/drill');
const { chest, cargo } = require('block/effect/core');
const { und } = require('block/effect/und');
const { lu } = require('block/effect/LiquidUnloader');
const { speeder } = require('block/effect/speeder');
/*const { cure } = require('block/effect/cure');
const { unitA } = require('block/effect/unitA');
const { lighthouse } = require('block/effect/lighthouse');*/
//const { clWall, clWallL, aws, awl, rws, rwl } = require('block/defence/wall');
const { clWall, clWallL, rws, rwl } = require('block/defence/wall');

/*const { unitF } = require('unit/UF');
const { suzerain } = require('unit/suzerain');
const { asphyxia } = require('unit/asphyxia');
const { apocalypse } = require('unit/apocalypse');
const { tera } = require('unit/tera');
const { nebula } = require('unit/nebula');
const { nihilo } = require('unit/nihilo');
const { narwhal } = require('unit/narwhal');

const { winglet } = require('unit/AirSapper/winglet');
const { moth } = require('unit/AirSapper/moth');
const { vb } = require('unit/AirSapper/VenomBee');
const { phantom } = require('unit/AirSapper/phantom');
const { skyline } = require('unit/AirSapper/skyline');*/


//const { start, sporeArea, rail, RadiationIslands, Colosseum, Darkness, GlacialValley } = require('game/challengeMap/cmain');
//const { pd, TD1, TD2, TD3, TD4, TD5, TD6, TD7, TD8, TD9, TD10, TD11, TD12, TD13 } = require('game/TD/tmain');
/*-----------------------------------------------------------------------*/
lib.addToResearch(T2duo, { parent: 'duo', });
lib.addToResearch(T3duo, { parent: T2duo.name, });
lib.addToResearch(T2scatter, { parent: 'scatter', });
lib.addToResearch(T2scorch, { parent: 'scorch', });
lib.addToResearch(IM, { parent: 'hail', });
lib.addToResearch(shotgun, { parent: 'duo',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(sakura, { parent: shotgun.name, });
lib.addToResearch(rainbow, { parent: shotgun.name, });
lib.addToResearch(hurricane, { parent: 'arc', });
lib.addToResearch(ms, { parent: hurricane.name, });
// lib.addToResearch(sunburst, { parent: hurricane.name, });
lib.addToResearch(T2lan, { parent: 'lancer', });
lib.addToResearch(stinger, { parent: T2lan.name, });
lib.addToResearch(swT2, { parent: 'swarmer', });
lib.addToResearch(swMin, { parent: 'duo', });
lib.addToResearch(T2rip, { parent: 'ripple', });
lib.addToResearch(T3rip, { parent: T2rip.name, });
lib.addToResearch(T2fuse, { parent: 'fuse', });
lib.addToResearch(T3fuse, { parent: T2fuse.name, });
lib.addToResearch(minisp, { parent: 'swarmer', });
/*lib.addToResearch(antiaircraft, { parent: 'scatter',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.overgrowth),
    )
});
lib.addToResearch(sam, { parent: 'scatter',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});*/
lib.addToResearch(RG, { parent: 'foreshadow',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});
lib.addToResearch(prism, { parent: rainbow.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});
/*lib.addToResearch(blackhole, { parent: sam.name, });
lib.addToResearch(sucker, { parent: 'parallax', });
lib.addToResearch(dissipation, { parent: 'segment', });
lib.addToResearch(heal, { parent: 'mend-projector', objectives: Seq.with(new Objectives.Research(Blocks.repairTurret)) });
lib.addToResearch(TRS, { parent: 'duo',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.stainedMountains),
    )
});*/
lib.addToResearch(arNode, { parent: 'power-node', });
lib.addToResearch(png, { parent: 'power-node-large', });
lib.addToResearch(T2ST, { parent: 'steam-generator', });
// lib.addToResearch(T2ther, { parent: 'thermal-generator', });
lib.addToResearch(th2, { parent: 'thorium-reactor', });
// lib.addToResearch(LG, { parent: 'impact-reactor', });
//
// lib.addToResearch(conduit, { parent: 'plated-conduit', });
// lib.addToResearch(LB, { parent: 'phase-conduit', });
lib.addToResearch(driver, { parent: 'phase-conduit', });
lib.addToResearch(T2IB, { parent: 'bridge-conveyor',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(alloyDuct, { parent: 'armored-conveyor', });
//lib.addToResearch(stackBridge, { parent: 'plastanium-conveyor', });
lib.addToResearch(ppc, { parent: 'plastanium-conveyor', });
lib.addToResearch(invertedJunction, { parent: 'junction', });
lib.addToResearch(TJ, { parent: 'junction',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(TIJ, { parent: TJ.name, });
lib.addToResearch(TR, { parent: 'router',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(T2LB, { parent: 'bridge-conduit',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(conduit, { parent: 'pulse-conduit', });
/*lib.addToResearch(TLR, { parent: 'liquid-router',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(IN, { parent: T2IB.name, });*/
lib.addToResearch(T2kiln, { parent: 'kiln', });
lib.addToResearch(T2melter, { parent: 'melter', });
lib.addToResearch(T2PC, { parent: 'plastanium-compressor', });
lib.addToResearch(T2PF, { parent: 'phase-weaver', });
lib.addToResearch(T2SA, { parent: 'surge-smelter', });
lib.addToResearch(T2CM, { parent: 'cryofluid-mixer', });
lib.addToResearch(GC, { parent: 'pyratite-mixer', });
lib.addToResearch(pu, { parent: 'pulverizer', });
//lib.addToResearch(crusher, { parent: 'pulverizer', });
lib.addToResearch(crispSteelSmelter, { parent: 'kiln', });
lib.addToResearch(T2CSm, { parent: crispSteelSmelter.name, });
//lib.addToResearch(LA, { parent: T2SA.name, });
lib.addToResearch(ai, { parent: 'incinerator', });
/*lib.addToResearch(DCF, { parent: 'force-projector',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});*/
lib.addToResearch(tiDrill, { parent: 'pneumatic-drill', });
//lib.addToResearch(drill, { parent: 'blast-drill', });
lib.addToResearch(shovel, { parent: 'pneumatic-drill', });
//lib.addToResearch(testDrill, { parent: 'laser-drill', });
lib.addToResearch(slagE, { parent: 'water-extractor', });
lib.addToResearch(T2WE, { parent: 'water-extractor', });
lib.addToResearch(T2CU, { parent: 'cultivator', });
lib.addToResearch(blastOilExtractor, { parent: 'oil-extractor', });
lib.addToResearch(dustExtractor, { parent: 'pneumatic-drill', });

/*lib.addToResearch(core, { parent: 'core-shard',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.extractionOutpost),
    )
});*/
lib.addToResearch(chest, { parent: 'router', });
lib.addToResearch(cargo, { parent: 'vault', });
lib.addToResearch(und, { parent: 'unloader', });
lib.addToResearch(lu, { parent: 'liquid-tank', });
lib.addToResearch(speeder, { parent: 'force-projector', });
/*lib.addToResearch(cure, { parent: 'mend-projector', });
lib.addToResearch(unitA, { parent: 'overdrive-projector', });
lib.addToResearch(lighthouse, { parent: 'illuminator', });*/
lib.addToResearch(clWall, { parent: 'copper-wall-large', });
lib.addToResearch(clWallL, { parent: clWall.name, });
// lib.addToResearch(aws, { parent: 'surge-wall-large', });
// lib.addToResearch(awl, { parent: aws.name, });
// lib.addToResearch(rws, { parent: aws.name, });
//lib.addToResearch(rws, { parent: 'surge-wall-large', });
//lib.addToResearch(rwl, { parent: rws.name, });

/*lib.addToResearch(unitF, { parent: 'tetrative-reconstructor',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.planetaryTerminal),
    )
});
lib.addToResearch(suzerain, { parent: 'reign',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(asphyxia, { parent: 'toxopid',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(apocalypse, { parent: 'eclipse',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(tera, { parent: 'oct',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(nebula, { parent: 'corvus',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(nihilo, { parent: 'omura',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(narwhal, { parent: 'navanax',objectives: Seq.with( new Objectives.Research(unitF) ) });

lib.addToResearch(winglet, { parent: 'flare', });
lib.addToResearch(moth, { parent: winglet.name,objectives: Seq.with( new Objectives.Research(Blocks.additiveReconstructor) ) });
lib.addToResearch(vb, { parent: moth.name,objectives: Seq.with( new Objectives.Research(Blocks.multiplicativeReconstructor) ) });
lib.addToResearch(phantom, { parent: vb.name,objectives: Seq.with( new Objectives.Research(Blocks.exponentialReconstructor) ) });
lib.addToResearch(skyline, { parent: phantom.name,objectives: Seq.with( new Objectives.Research(Blocks.tetrativeReconstructor) ) });

lib.addToResearch(items.crispSteel, { parent: 'titanium', });
lib.addToResearch(items.lightninAlloy, { parent: 'surge-alloy', });

/*lib.addToResearch(start, {
    parent: SectorPresets.planetaryTerminal.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.planetaryTerminal)
    )
});
lib.addToResearch(sporeArea, {
    parent: start.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(start)
    )
});
lib.addToResearch(rail, {
    parent: start.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(start)
    )
});
lib.addToResearch(RadiationIslands, {
    parent: start.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(start)
    )
});
lib.addToResearch(Darkness, {
    parent: start.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(start)
    )
});
lib.addToResearch(Colosseum, {
    parent: Darkness.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(Darkness)
    )
});
lib.addToResearch(GlacialValley, {
    parent: sporeArea.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(sporeArea)
    )
});*/

/*lib.addToResearch(pd, {
    parent: 'core-shard', 
});
lib.addToResearch(TD1, { 
    parent: pd.name, 
    objectives: Seq.with(
        new Objectives.SectorComplete(pd)
    )
});
lib.addToResearch(TD2, {
    parent: TD1.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD1)
    )
});
lib.addToResearch(TD3, {
    parent: TD1.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD2)
    )
});
lib.addToResearch(TD4, {
    parent: TD2.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD2)
    )
});
lib.addToResearch(TD5, {
    parent: TD4.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD4)
    )
});
lib.addToResearch(TD6, {
    parent: TD5.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD5)
    )
});
lib.addToResearch(TD7, {
    parent: TD6.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD6)
    )
});
lib.addToResearch(TD8, {
    parent: TD6.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD3)
    )
});
lib.addToResearch(TD9, {
    parent: TD8.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD8)
    )
});
lib.addToResearch(TD10, {
    parent: TD7.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD7)
    )
});
lib.addToResearch(TD11, {
    parent: TD9.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD9)
    )
});
lib.addToResearch(TD12, {
    parent: TD1.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD1)
    )
});
lib.addToResearch(TD13, {
    parent: TD12.name,
    objectives: Seq.with(
        new Objectives.SectorComplete(TD12)
    )
});*/