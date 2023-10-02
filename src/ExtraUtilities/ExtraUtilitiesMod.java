package ExtraUtilities;

import ExtraUtilities.content.*;
import ExtraUtilities.net.EUCall;
import arc.*;
import arc.Graphics;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Label;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;

import static arc.Core.settings;
import static mindustry.Vars.ui;

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
    public static void addToTable(UnitType unitType, Table t){
        t.image(unitType.uiIcon).pad(3f).row();
        t.add(Core.bundle.format("unit."+unitType.name + ".name")).row();
        t.add(Core.bundle.format("unit."+unitType.name + ".description")).row();
    }
    public static String toText(String str){
        return Core.bundle.format(str);
    }

    public static boolean hardMod = Core.settings.getBool("eu-hard-mode");
    public static boolean onlyPlugIn = Core.settings.getBool("eu-plug-in-mode");

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
                    addToTable(EUBlocks.unitBooster, t);
                    addToTable(EUBlocks.turretSpeeder, t);
                    addToTable(EUUnitTypes.arcana, t);
                    addToTable(EUUnitTypes.napoleon, t);
                    addToTable(EUUnitTypes.havoc, t);
                    addToTable(EUBlocks.advAssemblerModule, t);
                    addToTable(EUBlocks.T2sporePress, t);
                    addToTable(EUBlocks.ventHeater, t);
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
        if(show || onlyPlugIn) return;
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

    public Graphics.Cursor newCursor(String filename){
        Pixmap p = new Pixmap(EU.root.child("cursor").child(filename));
        return Core.graphics.newCursor(p, p.width /2, p.height /2);
    }

    public Graphics.Cursor newCursor(String filename, int scale){
        if(scale == 1 || OS.isAndroid || OS.isIos) return newCursor(filename);
        Pixmap base = new Pixmap(EU.root.child("cursor").child(filename));
        Pixmap result = Pixmaps.scale(base, base.width * scale, base.height * scale);
        base.dispose();
        return Core.graphics.newCursor(result, result.width /2, result.height /2);
    }

    private void overrideUI(){
        EU = Vars.mods.getMod(ExtraUtilitiesMod.class);
        Graphics.Cursor.SystemCursor.arrow.set(newCursor("cursor.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.hand.set(newCursor("hand.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.ibeam.set(newCursor("ibeam.png", Fonts.cursorScale()));
        ui.drillCursor = newCursor("drill.png", Fonts.cursorScale());
        ui.unloadCursor = newCursor("unload.png", Fonts.cursorScale());
        ui.targetCursor = newCursor("target.png", Fonts.cursorScale());
    }

    @Override
    public void init() {
        if(!onlyPlugIn) {
            EUCall.registerPackets();
            EUOverride.overrideBuilder();
            EUOverride.overrideAmr();
            //EUOverride.ap4sOverride();
        }

        settings.defaults("eu-plug-in-mode", false);
        settings.defaults("eu-hard-mode", false);
        settings.defaults("use-eu-cursor", true);
        settings.defaults("eu-show-version", true);

        Vars.mods.locateMod(ModName).meta.hidden = onlyPlugIn;
        if(onlyPlugIn){
            Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
            mod.meta.displayName = mod.meta.displayName + "-Plug-In";
            mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-plug-in";
        }

        if(hardMod){
            EUOverride.overrideBlockAll();
            Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
            mod.meta.displayName = mod.meta.displayName + " Hard!";
            mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-hard";
        }

        if(ui != null) {
            if(Core.settings.getBool("use-eu-cursor")) overrideUI();
            if(Core.settings.getBool("eu-show-version")) EUOverride.overrideVersion();
            if (ui.settings != null) {
                BaseDialog dialog = new BaseDialog("tips");
                Runnable exit = () -> {
                    dialog.hide();
                    Core.app.exit();
                };
                dialog.cont.add(toText("eu-reset-exit"));
                dialog.buttons.button("OK", exit).center().size(150, 50);

                ui.settings.addCategory(toText("EU-SET"), name("fireWork"), settingsTable -> {
//                    settingsTable.sliderPref("min-zoom", 10, 0, 60, (i) -> {
//                        if(!Vars.headless) Vars.renderer.minZoom = i / 10f;
//                        return Vars.renderer.minZoom + "x";
//                    });
//                    settingsTable.sliderPref("max-zoom", 10, 5, 50, (i) -> {
//                        if(!Vars.headless) Vars.renderer.maxZoom = i;
//                        return Vars.renderer.maxZoom + "x";
//                    });


                    //auto- listening
                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting("min-zoom"){
                        final int def = 10;
                        final float min = 1f, max = 50f, step = 1f;

                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            Core.settings.defaults(name, def);
                            Slider slider = new Slider(min, max, step, false);
                            slider.setValue((float)Core.settings.getInt(this.name));
                            Label value = new Label("", Styles.outlineLabel);
                            Table content = new Table();
                            content.add(this.title, Styles.outlineLabel).left().growX().wrap();
                            content.add(value).padLeft(10.0F).right();
                            content.margin(3.0F, 33.0F, 3.0F, 33.0F);
                            content.touchable = Touchable.disabled;
                            slider.update(() -> {
                                float v = Vars.renderer.minZoom * 10;
                                slider.setValue(v);
                                String st = v > max ? Core.bundle.get("zoom-over") : v < min ? Core.bundle.get("zoom-below") : "";
                                value.setText(st + v/10 + "x");
                            });
                            slider.changed(() -> {
                                Core.settings.put(this.name, (int)slider.getValue());
                                if(!Vars.headless) Vars.renderer.minZoom = slider.getValue()/10f;
                            });
                            slider.change();
                            this.addDesc(table.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get());
                            table.row();
                        }
                    });

                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting("max-zoom"){
                        final int def = 10;
                        final float min = 5f, max = 50f, step = 1f;

                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            Core.settings.defaults(name, def);
                            Slider slider = new Slider(min, max, step, false);
                            slider.setValue((float)Core.settings.getInt(this.name));
                            Label value = new Label("", Styles.outlineLabel);
                            Table content = new Table();
                            content.add(this.title, Styles.outlineLabel).left().growX().wrap();
                            content.add(value).padLeft(10.0F).right();
                            content.margin(3.0F, 33.0F, 3.0F, 33.0F);
                            content.touchable = Touchable.disabled;
                            slider.update(() -> {
                                float v = Vars.renderer.maxZoom;
                                slider.setValue(v);
                                String st = v > max ? Core.bundle.get("zoom-over") : v < min ? Core.bundle.get("zoom-below") : "";
                                value.setText(st + v + "x");
                            });
                            slider.changed(() -> {
                                Core.settings.put(this.name, (int)slider.getValue());
                                if(!Vars.headless) Vars.renderer.maxZoom = slider.getValue();
                            });
                            slider.change();
                            this.addDesc(table.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get());
                            table.row();
                        }
                    });

                    settingsTable.checkPref("use-eu-cursor", true);
                    settingsTable.checkPref("eu-show-version", true);

                    settingsTable.pref(new SettingsMenuDialog.SettingsTable.CheckSetting("eu-plug-in-mode", false, null) {
                        @Override
                        public void add(SettingsMenuDialog.SettingsTable table) {
                            CheckBox box = new CheckBox(title);

                            box.update(() -> box.setChecked(settings.getBool(name)));

                            box.changed(() -> {
                                settings.put(name, box.isChecked());
                                settings.remove("eu-hard-mode");
                                dialog.show();
                            });
                            box.left();
                            addDesc(table.add(box).left().padTop(3f).get());
                            table.row();
                        }
                    });

                    if(!onlyPlugIn) {
                        settingsTable.checkPref("eu-first-load", true);
                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-me-now")) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                table.button(name, ExtraUtilitiesMod::toShow).margin(14).width(200f).pad(6);
                                table.row();
                            }
                        });
                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.CheckSetting("eu-hard-mode", false, null) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                CheckBox box = new CheckBox(title);

                                box.update(() -> box.setChecked(settings.getBool(name)));

                                box.changed(() -> {
                                    if (!onlyPlugIn) {
                                        settings.put(name, box.isChecked());
                                        settings.put("eu-open-hard", hardMod);
                                        dialog.show();
                                    }
                                });
                                box.left();
                                addDesc(table.add(box).left().padTop(3f).get());
                                table.row();
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void loadContent(){
        if(onlyPlugIn) return;
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
