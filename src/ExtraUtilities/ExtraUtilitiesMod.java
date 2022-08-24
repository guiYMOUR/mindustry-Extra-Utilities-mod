package ExtraUtilities;

import ExtraUtilities.content.EUBlocks;
import ExtraUtilities.content.EUTechTree;
import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.net.EUCall;
import arc.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;

public class ExtraUtilitiesMod extends Mod{
    public static String ModName = "extra-utilities";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public void addToTable(Block block, Table t){
        t.image(block.uiIcon).pad(3f).row();
        t.add(Core.bundle.format("block."+block.name + ".name")).row();
        t.add(Core.bundle.format("block."+block.name + ".description")).row();
    }

    public ExtraUtilitiesMod(){
        Log.info("Loaded ExtraUtilities Mod constructor.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("ExtraUtilities");
                dialog.cont.add("ExtraUtilities").row();
                dialog.cont.image(Core.atlas.find(name("LOGO"))).pad(3f).height(150).width(400).row();
                dialog.cont.add(Core.bundle.format("tips.name")).row();
                dialog.cont.add(Core.bundle.format("tips.description")).row();
                dialog.cont.pane(t -> {
                    addToTable(EUBlocks.liquidSorter, t);
                    addToTable(EUBlocks.liquidValve, t);
                    addToTable(EUBlocks.arkyciteExtractor, t);
                    addToTable(EUBlocks.minerPoint, t);
                    addToTable(EUBlocks.minerCenter, t);
                    addToTable(EUBlocks.heatDriver, t);
                    addToTable(EUBlocks.heatTransfer, t);
                    addToTable(EUBlocks.thermalHeater, t);
                    addToTable(EUBlocks.T2oxide, t);
                }).grow().center().maxWidth(960f);
                dialog.buttons.button("@close", dialog::hide).size(100f, 50f).center();
                dialog.show();
            });
        });
    }

    @Override
    public void init() {
        EUCall.registerPackets();
    }

    @Override
    public void loadContent(){
        EUUnitTypes.load();
        EUBlocks.load();
        EUTechTree.load();
    }

}
