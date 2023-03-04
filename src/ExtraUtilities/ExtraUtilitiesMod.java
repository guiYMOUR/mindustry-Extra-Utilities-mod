package ExtraUtilities;

import ExtraUtilities.content.*;
import ExtraUtilities.net.EUCall;
import arc.*;
import arc.files.Fi;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.units.UnitFactory;

import javax.swing.*;
import javax.swing.text.Style;

public class ExtraUtilitiesMod extends Mod{
    public static String ModName = "extra-utilities";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public static void addToTable(Block block, Table t){
        t.image(block.uiIcon).pad(3f).row();
        t.add(Core.bundle.format("block."+block.name + ".name")).row();
        t.add(Core.bundle.format("block."+block.name + ".description")).row();
    }
    public static String toText(String str){
        return Core.bundle.format(str);
    }

    private static boolean show = false;

    public static Mods.LoadedMod EU;

    public static void log(){
        if(show) return;
        show = true;
        if(Core.settings.getBool("eu-first-load")){
            BaseDialog dialog = new BaseDialog("ExtraUtilities"){
                private float leave = 5f * 60;
                private boolean canClose = false;
                {
                    update(() -> {
                        leave -= Time.delta;
                        if(leave < 0 && !canClose) {
                            canClose = true;
                        }
                    });
                    cont.add("ExtraUtilities").row();
                    cont.add(toText("eu-log-attention")).row();
                    cont.add(toText("eu-log-open")).row();
                    cont.add("朋友朋友，[red]看这里[]").row();
                    cont.add("Extra Utilities，更多实用设备，作者：guiY，[red]中国人[]我的朋友").row();
                    cont.add("bilibili找guiY归某人，QQ群：588379643").row();
                    cont.add("[blue]第一次模组英文？看下面操作").row();
                    cont.add("[green]打开设置>找到语言>设置成English>退出重进>打开Settings>设置成简体中文>退出重进").row();
                    cont.image(Core.atlas.find(name("LOGO"))).pad(3f).height(150).width(400).row();
                    cont.add(Core.bundle.format("tips.name")).row();
                    cont.add(Core.bundle.format("tips.description")).row();
                    cont.pane(t -> {
                        addToTable(EUBlocks.communicatingValve, t);
                        addToTable(EUBlocks.finalF, t);
                        addToTable(EUBlocks.ADC, t);
                        addToTable(EUBlocks.heatDistributor, t);
                        addToTable(EUBlocks.liquidConsumeGenerator, t);
                        addToTable(EUBlocks.onyxBlaster, t);
                        addToTable(EUBlocks.celebration, t);
                        addToTable(EUBlocks.celebrationMk2, t);
                        addToTable(EUBlocks.guiY, t);
                    }).grow().center().maxWidth(960f);
                    buttons.check(toText("eu-log-not-show-next"), !Core.settings.getBool("eu-first-load"), b -> {
                        Core.settings.put("eu-first-load", !b);
                    }).center();
                    buttons.button("",this::hide).update(b -> {
                        b.setDisabled(!canClose);
                        b.setText(canClose ? toText("eu-log-understand"):toText("eu-log-pls-read") + "[accent]" + Math.floor(leave/60) + "[]s");
                    }).size(140f, 50f).center();
                }
            };
            dialog.show();
        }
    }

    public ExtraUtilitiesMod() {
        Log.info("Loaded ExtraUtilities Mod constructor.");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, ExtraUtilitiesMod::log));
    }

    @Override
    public void init() {
        EUCall.registerPackets();
        EUOverride.overrideBuilder();
        //EUOverride.overrideBlockAll();
        Vars.ui.settings.game.checkPref("eu-first-load", true);
    }

    @Override
    public void loadContent(){
        EUUnitTypes.load();
        EUOverride.overrideUnit1();
        EUBlocks.load();
        EUOverride.overrideBlock1();

        TDPlanet.load();
        TDSectorPresets.load();

        EUTechTree.load();
    }

}
