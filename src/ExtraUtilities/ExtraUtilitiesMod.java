package ExtraUtilities;

import ExtraUtilities.content.EUBlocks;
import ExtraUtilities.content.EUTechTree;
import arc.*;
import arc.graphics.g2d.TextureRegion;
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

    public ExtraUtilitiesMod(){
        Log.info("Loaded ExtraUtilities.");

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("ExtraUtilities");
                dialog.cont.add("ExtraUtilities").row();
                dialog.cont.image(Core.atlas.find(name("LOGO"))).pad(3f).row();
                dialog.cont.add(Core.bundle.format("tips.name")).row();
                dialog.cont.add(Core.bundle.format("tips.description")).row();
                dialog.cont.pane(t -> {
                    t.image(Core.atlas.find(name("heat-driver-preview-all"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("heat-driver.name"))).row();
                    t.add(Core.bundle.format("block."+name("heat-driver.description"))).row();
                    t.image(Core.atlas.find(name("heat-transfer"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("heat-transfer.name"))).row();
                    t.add(Core.bundle.format("block."+name("heat-transfer.description"))).row();
                    t.image(Core.atlas.find(name("thermal-heater"))).pad(3f).row();
                    t.add(Core.bundle.format("block."+name("thermal-heater.name"))).row();
                    t.add(Core.bundle.format("block."+name("thermal-heater.description"))).row();
                }).grow().center().maxWidth(960f);
                dialog.buttons.button("@close", dialog::hide).size(100f, 50f).center();
                dialog.show();
            });
        });
    }

    @Override
    public void loadContent(){
        EUBlocks.load();
        EUTechTree.load();
    }

}
