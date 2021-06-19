const lib = require("blib");

require("block/turret/T2duo");
require("block/turret/T3duo");
require("block/turret/T2scatter");
require("block/turret/shotgun");
require("block/turret/sakura");
require("block/turret/rainbow");
require("block/turret/hurricane");
require("block/turret/ms");
require("block/turret/T2lancer");
require("block/turret/stinger");
require("block/turret/miniswarmer");
require("block/turret/T2swarmer");
require("block/turret/T2ripple");
require("block/turret/T3ripple");
require("block/turret/T2fuse");
require("block/turret/T3fuse");
require("block/turret/minisp");
require("block/turret/antiaircraft");
require("block/turret/sam");
require("block/turret/RG");
require("block/turret/prism");

require("block/effect/he");
require("block/power/png");
require("block/power/T2steam");
require("block/power/T2ther");
require("block/power/th2");
require("block/production/drill");
require("block/production/T2kiln");
require("block/production/T2melter");
require("block/production/T2PC");
require("block/production/T2PF");
require("block/production/T2CM");
require("block/production/T2SA");
require("block/power/GeneratorCrafter");
require("block/production/T2pulverize");
require("block/distribution/T2IB");
require("block/distribution/stackBridge");
require("block/distribution/IN");
require("block/distribution/LB");
require("block/distribution/driver");
require("block/effect/core");
require("block/effect/und");
require("block/effect/LiquidUnloader");
require("block/effect/cure");
require("block/effect/unitA");
require("block/defence/wall");

require("unit/UF");
require("unit/suzerain");
require("unit/tera");
require("unit/nebula");
require("unit/nihilo");

require("other/status");

//coming soon...
require("game/challengeMap/cmain");
//require("game/TD/tmain");

require("tree");

lib.mod.meta.displayName = lib.getMessage('mod', 'displayName');
lib.mod.meta.description = lib.getMessage('mod', 'description');

const CoreSchematics = [
"bXNjaAF4nCWLWwqAMBAD05eI/ngRD1XXBQt9yFpEb6+lCQRmIHAYNGz2iTHxU8WvVIQx73yRhLOGkgGDJTEdPgfycd0lxIiRSr75LYJxq6m/AIce9VdD6X9sY9OlafIDyOMXfA=="
];
(() => {
    for (var schematic of CoreSchematics) {
        let read = Schematics.readBase64(schematic);
        if (read.hasCore()) {
            Vars.schematics.getLoadouts(Blocks.coreShard).add(read);
        }
    }
})();
