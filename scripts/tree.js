const lib = require('blib');

const { T2duo } = require('turret/T2duo');
const { T3duo } = require('turret/T3duo');
const { hurricane } = require('turret/hurricane');
const { T2lan } = require('turret/T2lancer');
const { stinger } = require('turret/stinger');
const { swT2 } = require('turret/T2swarmer');
const { swMin } = require('turret/miniswarmer');
const { T2rip } = require('turret/T2ripple');
const { T3rip } = require('turret/T3ripple');
const { T2fuse } = require('turret/T2fuse');
const { T3fuse } = require('turret/T3fuse');
const { DCF } = require('block/he');

lib.addToResearch(T2duo, { parent: 'duo', });
lib.addToResearch(T3duo, { parent: T2duo.name, });
lib.addToResearch(hurricane, { parent: 'arc', });
lib.addToResearch(T2lan, { parent: 'lancer', });
lib.addToResearch(stinger, { parent: T2lan.name, });
lib.addToResearch(swT2, { parent: 'swarmer', });
lib.addToResearch(swMin, { parent: 'swarmer', });
lib.addToResearch(T2rip, { parent: 'ripple', });
lib.addToResearch(T3rip, { parent: T2rip.name, });
lib.addToResearch(T2fuse, { parent: 'fuse', });
lib.addToResearch(T3fuse, { parent: T2fuse.name, });
lib.addToResearch(DCF, { parent: 'force-projector', });
