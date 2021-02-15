const lib = require('blib');

const { T2duo } = require('turret/T2duo');
const { T3duo } = require('turret/T3duo');
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
const { RG } = require('turret/RG');

const { png } = require('power/png');
const { T2ST } = require('power/T2steam');

const { LB } = require('block/LB');
const { T2IB, TJ, TR } = require('block/T2IB');
const { IN } = require('block/IN');
const { T2PC } = require('block/T2PC');
const { T2PF } = require('block/T2PF');
const { T2SA } = require('block/T2SA');
const { T2CM } = require('block/T2CM');
const { DCF } = require('block/he');

lib.addToResearch(T2duo, { parent: 'duo', });
lib.addToResearch(T3duo, { parent: T2duo.name, });
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
lib.addToResearch(RG, { parent: 'salvo', });

lib.addToResearch(png, { parent: 'power-node-large', });
lib.addToResearch(T2ST, { parent: 'steam-generator', });

lib.addToResearch(LB, { parent: 'phase-conduit', });
lib.addToResearch(T2IB, { parent: 'bridge-conveyor', });
lib.addToResearch(TJ, { parent: 'junction', });
lib.addToResearch(TR, { parent: 'router', });
lib.addToResearch(IN, { parent: T2IB.name, });
lib.addToResearch(T2PC, { parent: 'plastanium-compressor', });
lib.addToResearch(T2PF, { parent: 'phase-weaver', });
lib.addToResearch(T2SA, { parent: 'alloy-smelter', });
lib.addToResearch(T2CM, { parent: 'cryofluid-mixer', });
lib.addToResearch(DCF, { parent: 'force-projector', });
