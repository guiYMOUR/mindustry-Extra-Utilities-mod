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

public class ExtraUtilitiesMod extends Mod{
    public static String ModName = "extra-utilities";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public void addToTable(String name, Table t){
        t.image(Core.atlas.find(name(name))).pad(3f).row();
        t.add(Core.bundle.format("block."+name(name + ".name"))).row();
        t.add(Core.bundle.format("block."+name(name + ".description"))).row();
    }

    public ExtraUtilitiesMod(){
        Log.info("Loaded ExtraUtilities Mod constructor.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("ExtraUtilities");
                dialog.cont.add("ExtraUtilities").row();
                dialog.cont.image(Core.atlas.find(name("LOGO"))).pad(3f).row();
                dialog.cont.add(Core.bundle.format("tips.name")).row();
                dialog.cont.add(Core.bundle.format("tips.description")).row();
                dialog.cont.pane(t -> {
                    t.image(Core.atlas.find(name("arkycite-extractor-preview"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("arkycite-extractor.name"))).row();
                    t.add(Core.bundle.format("block."+name("arkycite-extractor.description"))).row();
                    addToTable("miner-point", t);
                    addToTable("miner-center", t);
                    t.image(Core.atlas.find(name("heat-driver-preview-all"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("heat-driver.name"))).row();
                    t.add(Core.bundle.format("block."+name("heat-driver.description"))).row();
                    addToTable("heat-transfer", t);
                    addToTable("thermal-heater", t);
                    t.image(Core.atlas.find(name("T2oxide-preview"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("T2oxide.name"))).row();
                    t.add(Core.bundle.format("block."+name("T2oxide.description"))).row();
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
