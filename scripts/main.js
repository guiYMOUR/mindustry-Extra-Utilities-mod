const lib = require("blib");

//require("blib");
require("block/turret/T2duo");
require("block/turret/T3duo");
require("block/turret/T2scatter");
require("block/turret/T2scorch");
require("block/turret/shotgun");
require("block/turret/sakura");
require("block/turret/rainbow");
require("block/turret/hurricane");
require("block/turret/ms");
require("block/turret/sunburst");
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
require("block/turret/prism");
require("block/turret/blackhole");
require("block/turret/RG");
require("block/turret/sucker");
require("block/turret/dissipation");
require("block/turret/heal");

require("block/effect/DCF");
require("block/power/png");
require("block/power/T2steam");
require("block/power/T2ther");
require("block/power/th2");
require("block/power/LightninGenerator");

require("block/production/drill");
require("block/production/T2kiln");
require("block/production/T2melter");
require("block/production/T2PC");
require("block/production/T2PF");
require("block/production/T2CM");
require("block/production/T2SA");
require("block/power/GeneratorCrafter");
require("block/production/T2pulverize");
require("block/production/LA");
require("block/production/AdjustableIncinerator");

require("block/distribution/T2IB");
require("block/distribution/stackBridge");
require("block/distribution/IN");
require("block/distribution/LB");
require("block/distribution/driver");
require("block/effect/core");
require("block/effect/und");
require("block/effect/LiquidUnloader");
require("block/effect/speeder");
require("block/effect/cure");
require("block/effect/unitA");
require("block/effect/lighthouse");
require("block/defence/wall");

require("unit/UF");
require("unit/suzerain");
require("unit/nebula");
require("unit/asphyxia");
require("unit/apocalypse");
require("unit/tera");
require("unit/nihilo");

require("game/items");
require("other/status");

//re-coming soon...
require("game/challengeMap/cmain");
require("game/TD/tmain");

require("tree");

lib.mod.meta.displayName = lib.getMessage('mod', 'displayName');
lib.mod.meta.description = lib.getMessage('mod', 'description');

Events.on(EventType.ClientLoadEvent, cons(e => {
    var dialog = new BaseDialog("Extra Utilities 2.4.50");

    dialog.buttons.defaults().size(210, 64);
    dialog.buttons.button("@close", run(() => {
        dialog.hide();
    })).size(210, 64);

    dialog.cont.pane((() => {

        var table = new Table();
        table.add(Core.bundle.format("mod.btm.displayName")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-logo", Core.atlas.find("clear"))).left().fillX().height(200).width(620).pad(3);
        table.row();
        table.add(Core.bundle.format("item.btm-lightnin-alloy.name") + "\n" + Core.bundle.format("item.btm-lightnin-alloy.description")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-lightnin-alloy", Core.atlas.find("clear"))).fillX().height(96).width(96).pad(3);
        table.row();
        table.add(Core.bundle.format("block.btm-LA.name") + "\n" + Core.bundle.format("block.btm-LA.description")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-LA", Core.atlas.find("clear"))).fillX().height(128).width(128).pad(3);
        table.row();
        table.add(Core.bundle.format("block.btm-lightnin-generator.name") + "\n" + Core.bundle.format("block.btm-lightnin-generator.description")).left().growX().wrap().width(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-lightnin-generator", Core.atlas.find("clear"))).fillX().height(160).width(160).pad(3);
        table.row();
        table.add(Core.bundle.format("block.btm-rws.name") + "\n" + Core.bundle.format("block.btm-rws.description")).left().growX().wrap().width(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-rws", Core.atlas.find("clear"))).fillX().height(32).width(32).pad(3);
        table.row();
        table.add(Core.bundle.format("block.btm-rwl.name") + "\n" + Core.bundle.format("block.btm-rwl.description")).left().growX().wrap().width(620).pad(4).labelAlign(Align.left);
        table.row();
        table.image(Core.atlas.find("btm-rwl", Core.atlas.find("clear"))).fillX().height(64).width(64).pad(3);
        table.row();
        table.add(Core.bundle.format("mod.btm.mapMaker")).left().growX().wrap().width(620).maxWidth(620).pad(4).labelAlign(Align.left);
        return table;
    })()).grow().center().maxWidth(620);
    dialog.show();
}));

require("other/override");

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

require("block/effect/pl");
