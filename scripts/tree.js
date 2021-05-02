//The single quotation mark is used because it looks comfortable.
const lib = require('blib');

const { T2duo } = require('turret/T2duo');
const { T3duo } = require('turret/T3duo');
const { T2scatter } = require('turret/T2scatter');
const { hurricane } = require('turret/hurricane');
const { ms } = require('turret/ms');
const { T2lan } = require('turret/T2lancer');
const { stinger } = require('turret/stinger');
const { swT2 } = require('turret/T2swarmer');
const { swMin } = require('turret/miniswarmer');
const { T2rip } = require('turret/T2ripple');
const { T3rip } = require('turret/T3ripple');
const { T2fuse } = require('turret/T2fuse');
const { T3fuse } = require('turret/T3fuse');
const { antiaircraft } = require('turret/antiaircraft');
const { RG } = require('turret/RG');
const { prism } = require('turret/prism');

const { png } = require('power/png');
const { T2ST } = require('power/T2steam');
const { T2ther } = require('power/T2ther');
const { th2 } = require('power/th2');

const { LB } = require('block/LB');
const { T2IB, TJ, TR, T2LB, TLR } = require('block/T2IB');
const { IN } = require('block/IN');
const { T2kiln } = require('block/T2kiln');
const { T2PC } = require('block/T2PC');
const { T2PF } = require('block/T2PF');
const { T2SA } = require('block/T2SA');
const { T2CM } = require('block/T2CM');
const { GC } = require("power/GeneratorCrafter");
const { pu } = require("block/T2pulverize");
const { DCF } = require('block/he');
const { drill } = require('block/drill');
const { core } = require('block/core');
const { cure } = require('block/cure');
const { unitA } = require('block/unitA');
const { clWall, clWallL, aws, awl } = require('block/wall');

const { unitF } = require('unit/UF');
const { suzerain } = require("unit/suzerain");
const { tera } = require('unit/tera');
const { nebula } = require('unit/nebula');
/*-----------------------------------------------------------------------*/
lib.addToResearch(T2duo, { parent: 'duo', });
lib.addToResearch(T3duo, { parent: T2duo.name, });
lib.addToResearch(T2scatter, { parent: 'scatter', });
lib.addToResearch(hurricane, { parent: 'arc', });
lib.addToResearch(ms, { parent: hurricane.name, });
lib.addToResearch(T2lan, { parent: 'lancer', });
lib.addToResearch(stinger, { parent: T2lan.name, });
lib.addToResearch(swT2, { parent: 'swarmer', });
lib.addToResearch(swMin, { parent: 'swarmer', });
lib.addToResearch(T2rip, { parent: 'ripple', });
lib.addToResearch(T3rip, { parent: T2rip.name, });
lib.addToResearch(T2fuse, { parent: 'fuse', });
lib.addToResearch(T3fuse, { parent: T2fuse.name, });
lib.addToResearch(antiaircraft, { parent: 'scatter',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.overgrowth),
    )
});
lib.addToResearch(RG, { parent: 'salvo',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});
lib.addToResearch(prism, { parent: 'meltdown',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});

lib.addToResearch(png, { parent: 'power-node-large', });
lib.addToResearch(T2ST, { parent: 'steam-generator', });
lib.addToResearch(T2ther, { parent: 'thermal-generator', });
lib.addToResearch(th2, { parent: 'thorium-reactor', });

lib.addToResearch(LB, { parent: 'phase-conduit', });
lib.addToResearch(T2IB, { parent: 'bridge-conveyor',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(TJ, { parent: 'junction',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
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
lib.addToResearch(TLR, { parent: 'liquid-router',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.craters),
    )
});
lib.addToResearch(IN, { parent: T2IB.name, });
lib.addToResearch(T2kiln, { parent: 'kiln', });
lib.addToResearch(T2PC, { parent: 'plastanium-compressor', });
lib.addToResearch(T2PF, { parent: 'phase-weaver', });
lib.addToResearch(T2SA, { parent: 'alloy-smelter', });
lib.addToResearch(T2CM, { parent: 'cryofluid-mixer', });
lib.addToResearch(GC, { parent: 'pyratite-mixer', });
lib.addToResearch(pu, { parent: 'pulverizer', });
lib.addToResearch(DCF, { parent: 'force-projector',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.nuclearComplex),
    )
});
lib.addToResearch(drill, { parent: 'blast-drill', });
lib.addToResearch(core, { parent: 'core-shard',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.extractionOutpost),
    )
});
lib.addToResearch(cure, { parent: 'mend-projector', });
lib.addToResearch(unitA, { parent: 'repair-point', });
lib.addToResearch(clWall, { parent: 'copper-wall-large', });
lib.addToResearch(clWallL, { parent: clWall.name, });
lib.addToResearch(aws, { parent: 'surge-wall-large', });
lib.addToResearch(awl, { parent: aws.name, });

lib.addToResearch(unitF, { parent: 'tetrative-reconstructor',
    objectives: Seq.with(
        new Objectives.SectorComplete(SectorPresets.planetaryTerminal),
    )
});
lib.addToResearch(suzerain, { parent: 'reign',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(tera, { parent: 'oct',objectives: Seq.with( new Objectives.Research(unitF) ) });
lib.addToResearch(nebula, { parent: 'corvus',objectives: Seq.with( new Objectives.Research(unitF) ) });