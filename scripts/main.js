const lib = require("blib");

require("turret/T2duo");
require("turret/T3duo");
require("turret/hurricane");
require("turret/ms");
require("turret/T2lancer");
require("turret/stinger");
require("turret/miniswarmer");
require("turret/T2swarmer");
require("turret/T2ripple");
require("turret/T3ripple");
require("turret/T2fuse");
require("turret/T3fuse");
require("turret/RG");

require("block/he");
require("power/png");
require("power/T2steam");
require("power/T2ther");
require("power/th2");
require("block/drill");
require("block/T2kiln");
require("block/T2PC");
require("block/T2PF");
require("block/T2CM");
require("block/T2SA");
require("power/GeneratorCrafter");
require("block/T2pulverize");
require("block/T2IB");
require("block/IN");
require("block/LB");
require("block/core");
require("block/cure");
require("block/unitA");

require("unit/UF");
require("unit/suzerain");
require("unit/tera");
require("unit/nebula");

require("other/status");

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
