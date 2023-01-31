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
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;

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
                private float leave = 4f * 60;
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
                        addToTable(EUBlocks.liquidIncinerator, t);
                        addToTable(EUBlocks.ELA, t);
                        addToTable(EUBlocks.ekMessDriver, t);
                        addToTable(EUBlocks.liquidSorter, t);
                        addToTable(EUBlocks.liquidValve, t);
                        addToTable(EUBlocks.arkyciteExtractor, t);
                        addToTable(EUBlocks.minerPoint, t);
                        addToTable(EUBlocks.minerCenter, t);
                        addToTable(EUBlocks.heatDriver, t);
                        addToTable(EUBlocks.heatTransfer, t);
                        addToTable(EUBlocks.thermalHeater, t);
                        addToTable(EUBlocks.T2oxide, t);
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
    public static void override(){
        for(int i = 0; i < Vars.content.blocks().size; i ++){
            Block block = Vars.content.blocks().get(i);
            if(block instanceof Turret && block.size >= 5){
                boolean has = false;
                for(ItemStack stack : block.requirements){
                    if(stack.item == EUItems.lightninAlloy){
                        has = true;
                        break;
                    }
                }
                if(has) continue;
                ItemStack[] copy = new ItemStack[block.requirements.length + 1];
                System.arraycopy(block.requirements, 0, copy, 0, block.requirements.length);
                copy[block.requirements.length] = new ItemStack(EUItems.lightninAlloy, 50 + 50 * (block.size - 5));
                block.requirements = copy;
            }
        }
    }

    public ExtraUtilitiesMod() {
        Log.info("Loaded ExtraUtilities Mod constructor.");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, ExtraUtilitiesMod::log));
        Events.on(ClientLoadEvent.class, e -> Time.runTask(30f, ExtraUtilitiesMod::override));
    }

    @Override
    public void init() {
        EUCall.registerPackets();
        Vars.ui.settings.game.checkPref("eu-first-load", true);
    }

    @Override
    public void loadContent(){
        //EU = Vars.mods.getMod(getClass());
//        if(!Vars.headless)
//            EUSounds.load();

        EUUnitTypes.load();
        EUBlocks.load();
        EUTechTree.load();
    }

}
