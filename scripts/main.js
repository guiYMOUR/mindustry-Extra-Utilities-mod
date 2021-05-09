const lib = require("blib");

require("turret/T2duo");
require("turret/T3duo");
require("turret/T2scatter");
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
require("turret/antiaircraft");
require("turret/RG");
require("turret/prism");

require("block/he");
require("power/png");
require("power/T2steam");
require("power/T2ther");
require("power/th2");
require("block/drill");
require("block/T2kiln");
require("block/T2melter");
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
require("block/wall");

require("unit/UF");
require("unit/suzerain");
require("unit/tera");
require("unit/nebula");

require("other/status");

//coming soon...
//require("game/challengeMap/cmain");
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

Events.on(EventType.ClientLoadEvent, cons(e => {

    var dialog = new JavaAdapter(BaseDialog, {}, "暂时告别 Farewell for a while");
    var icon =new Packages.arc.scene.style.TextureRegionDrawable(Core.atlas.find("btm-speedUp", Core.atlas.find("clear")));
    dialog.shown(run(() => {
        dialog.cont.table(Tex.button, cons(t => {
            t.defaults().size(250, 45).left();
            t.button("返回", icon, Styles.cleart, run(() => {
                dialog.hide();
            }));
        t.add("由于作者guiY和贴图师\nPlastaniumX Carrot\n要高考原因\n此mod可能会停更一段时间\n到6/10\n这个版本将会可能是\n高考结束前最后一个版本\n且玩切珍惜\n感谢游玩\n\nDue to the author guiY\nand PlastaniumX Carrot\nWill be the college\nentrance examination,\nthis MOD may stop\nmore time to June 10,\nthis version will might be\nthe last version\n before the end of\nthe college entrance\nexamination.\nThank you for playing.")
        }));
    }));
    dialog.show();
}));