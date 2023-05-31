package ExtraUtilities;

import ExtraUtilities.content.*;
import ExtraUtilities.net.EUCall;
import arc.*;
import arc.math.Mathf;
import arc.scene.ui.Button;
import arc.scene.ui.CheckBox;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;

import static arc.Core.settings;

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

    public static boolean hardMod = Core.settings.getBool("eu-hard-mode");

    private static boolean show = false;

    public static Mods.LoadedMod EU;

    public static void toShow(){
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
                cont.add("bilibili找guiY归某人，QQ群：613420467").row();
                cont.add("[blue]第一次模组英文？看下面操作").row();
                cont.add("[green]打开设置>找到语言>设置成English>退出重进>打开Settings>设置成简体中文>退出重进").row();
                cont.image(Core.atlas.find(name("LOGO"))).pad(3f).height(150).width(400).row();
                cont.add(Core.bundle.format("tips.name")).row();
                cont.add(Core.bundle.format("tips.description")).row();
                cont.pane(t -> {
                    addToTable(EUBlocks.quantumDomain, t);
                    addToTable(EUBlocks.nitrogenWell, t);
                    addToTable(EUBlocks.ekPump, t);
                    addToTable(EUBlocks.cyanogenPyrolysis, t);
                    addToTable(EUBlocks.heatPower, t);
                    addToTable(EUBlocks.windPower, t);
                    addToTable(EUBlocks.waterPower, t);
                    addToTable(EUBlocks.ADC, t);
                    addToTable(EUBlocks.guiY, t);
                    addToTable(EUBlocks.guiYsDomain, t);
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

    public static void log(){
        if(show) return;
        show = true;
        if(Core.settings.getBool("eu-first-load")){
            toShow();
        }
        settings.getBoolOnce("eu-open-hard", () -> {
            BaseDialog dialog = new BaseDialog("Hard Mode!");
            dialog.cont.add(toText("eu-hard-mode-open"));
            dialog.buttons.button("OK", dialog::hide).size(140, 50).center();
            dialog.show();
        });
    }

    public static void log2(){
        BaseDialog dialog = new BaseDialog("ExtraUtilities"){
            private int con = 0;
            private float bx, by;
            {
            cont.add("发现大量简介改动\n为了防止Mindustry崩溃，模组已被[red]全部禁用[]\n点击确定以继续");
            buttons.button("", this::hide).update(b ->{
                b.setText(con > 0 ? con == 5 ? "[red]愚人节快乐[]" : "再点我一下?" : "确定");
                if(con > 0) {
                    b.x = bx;
                    b.y = by;
                }
            }).size(140, 50).center();
        }

            @Override
            public void hide() {
                if(con >= 5) {
                    super.hide();
                    return;
                }
                con++;
                bx = Mathf.random(this.width * 0.8f);
                by = Mathf.random(this.height * 0.8f);
            }
        };
        dialog.show();
    }

    public ExtraUtilitiesMod() {
        Log.info("Loaded ExtraUtilities Mod constructor.");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, ExtraUtilitiesMod::log));
        //Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, ExtraUtilitiesMod::log2));
    }

    @Override
    public void init() {
        EUCall.registerPackets();
        EUOverride.overrideBuilder();
        EUOverride.overrideAmr();

        //EUOverride.ap4sOverride();

        settings.defaults("eu-hard-mode", false);

        if(hardMod){
            EUOverride.overrideBlockAll();
            Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
            mod.meta.displayName = mod.meta.displayName + " Hard!";
            mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-hard";
        }

        if(Vars.ui != null && Vars.ui.settings != null) {
            BaseDialog dialog = new BaseDialog("tips");
            Runnable exit = () -> {
                dialog.hide();
                Core.app.exit();
            };
            dialog.cont.add(toText("eu-reset-exit"));
            dialog.buttons.button("OK", exit).center().size(150, 50);

            Vars.ui.settings.addCategory(toText("EU-SET"), name("fireWork"), settingsTable -> {
                settingsTable.checkPref("eu-first-load", true);
                settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-me-now")) {
                    @Override
                    public void add(SettingsMenuDialog.SettingsTable table) {
                        table.button(name, ExtraUtilitiesMod::toShow).margin(14).width(200f).pad(6);
                        table.row();
                    }
                });
                settingsTable.pref(new SettingsMenuDialog.SettingsTable.CheckSetting("eu-hard-mode", false, null){
                    @Override
                    public void add(SettingsMenuDialog.SettingsTable table) {
                        CheckBox box = new CheckBox(title);

                        box.update(() -> box.setChecked(settings.getBool(name)));

                        box.changed(() -> {
                            settings.put(name, box.isChecked());
                            settings.put("eu-open-hard", hardMod);
                            dialog.show();
                        });
                        box.left();
                        addDesc(table.add(box).left().padTop(3f).get());
                        table.row();
                    }
                });
            });
        }
    }

    @Override
    public void loadContent(){
        EUAttribute.load();
        EUOverride.overrideUnit1();
        EUUnitTypes.load();
        EUOverride.overrideBlock1();
        EUBlocks.load();

        TDPlanet.load();
        TDSectorPresets.load();

        EUTechTree.load();
    }
}
