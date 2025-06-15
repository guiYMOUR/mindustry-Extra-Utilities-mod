package ExtraUtilities;

import ExtraUtilities.ai.DefenderHealAI;
import ExtraUtilities.content.*;
import ExtraUtilities.graphics.MainRenderer;
import ExtraUtilities.net.EUCall;
import ExtraUtilities.ui.EUI;
import arc.*;
import arc.Graphics;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.Slider;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType.*;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.maps.Map;
import mindustry.maps.Maps;
import mindustry.mod.*;
import mindustry.type.Liquid;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.*;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static arc.Core.app;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class ExtraUtilitiesMod extends Mod{
    public static String ModName = "extra-utilities";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public static void addToTable(UnlockableContent c, Table t){
        t.row();
        t.table(log -> {
            log.table(Styles.grayPanel, img -> {
                img.button(bt -> bt.image(c.uiIcon).size(64).pad(5), Styles.cleari, () -> ui.content.show(c)).left().tooltip("click to show");
            }).pad(10).margin(10).left();
            log.image(Tex.whiteui, Pal.accent).growY().width(3).pad(4).margin(5).left();
            log.table(info -> {
                var n = info.add(c.localizedName).wrap().fillX().left().maxWidth(Core.graphics.getWidth()/2f).get();
                info.row();
                info.image(Tex.whiteui, Pal.accent).left().width(n.getWidth() * 1.3f).height(3f).row();
                info.add(c.description).wrap().fillX().left().width(Core.graphics.getWidth()/2f).padTop(10).row();
                info.image(Tex.whiteui, Pal.accent).left().width(Core.graphics.getWidth()/2f).height(3f).row();
            }).left().pad(6);
        });
    }

    public static String toText(String str){
        return Core.bundle.format(str);
    }

    public static boolean hardMod;
    public static boolean onlyPlugIn;
    public static boolean coreResetV7;
    public static boolean coreReset;
    public static boolean overrideUnitArm;
    public static boolean overrideUnitMissile;
    public static String massageRand;

    private static boolean show = false;

    private static final Mat setMat = new Mat();
    private static final Mat reMat = new Mat();
    private static final Vec2 vec2 = new Vec2();
    private static final Seq<UnlockableContent> updateLog = new Seq<>();

    public static Mods.LoadedMod EU;

    public static EUI eui;

    public static void setColorName(){
        Mods.LoadedMod mod = mods.locateMod(ModName);
        String st = isAps() ? Core.bundle.get("mod.extra-utilities.displayNameAp") : Core.bundle.get("mod.extra-utilities.displayName");
        StringBuilder fin = new StringBuilder();

        for(int i = 0; i < st.length(); i++){
            String s = String.valueOf(st.charAt(i));
            Color c = Tmp.c1.set(EUGet.MIKU).shiftHue(i * (int)(80f/st.length()));
            int ci = c.rgb888();
            String ct = Integer.toHexString(ci);
            String fct = "[" + "#" + ct + "]";
            fin.append(fct).append(s);
        }
        mod.meta.displayName = fin + ("[gray] - " + massageRand);
        if(ui != null) rebuildRandSubTitle(massageRand);
    }

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
                cont.table(top -> {
                    top.add("ExtraUtilities").row();
                    top.add(toText("eu-log-attention")).row();
                    top.add(toText("eu-log-open")).row();
                    top.add("朋友朋友，[red]看这里[]").row();
                    top.add("Extra Utilities，更多实用设备，作者：guiY，[red]中国人[]我的朋友").row();
                    top.add("bilibili找guiY归某人，QQ群：613420467").row();
                    top.add("[blue]第一次模组英文？看下面操作").row();
                    top.add("[green]打开设置>找到语言>设置成English>退出重进>打开Settings>设置成简体中文>退出重进[]").row();
                    top.image(Core.atlas.find(name("LOGO"))).pad(3f).height(70).width(460).row();
                    //top.add(Core.bundle.format("tips.name")).row();
                    //top.add(Core.bundle.format("tips.description")).row();
                });
                cont.row();
                updateLog.clear();
                Block minisp = content.block(name("minisp"));
                Block ld = content.block(name("ld"));
                updateLog.addAll(
                        EUStatusEffects.breakage, EUBlocks.rust, EUBlocks.ekLiquidDriver,
                        ld, minisp,
                        EUUnitTypes.regency, EUUnitTypes.havoc, EUUnitTypes.arcana, EUBlocks.sancta, EUBlocks.arbiter,
                        EUBlocks.LG, EUBlocks.LA, EUBlocks.adaptiveMiner, EUBlocks.adaptiveMinerII,
                        EUBlocks.guiY
                );
                ScrollPane p = cont.pane(t -> {
                    //t.left().defaults().left();
                    t.table(log -> {
                        log.image(Tex.whiteui, Pal.accent).left().growX().height(3f).row();
                        log.table(main -> {
                            main.table(title -> {
                                title.add(Core.bundle.format("extra-utilities.update-log", EU.meta.version)).left();
                            }).left().padTop(4);
                            main.row();

                            main.table(info -> {
                                for(int i = 0; i < updateLog.size; i++){
                                    UnlockableContent content = updateLog.get(i);
                                    info.table(img -> {
                                        img.add(EUGet.selfStyleImageButton(new TextureRegionDrawable(content.uiIcon), Styles.emptyi, () -> ui.content.show(content))).size(40).pad(10).left().scaling(Scaling.fit);
                                    }).left().pad(4);
                                    info.image(Tex.whiteui, Pal.accent).growY().width(3).pad(4).margin(5).left();
                                    int finalI = i;
                                    info.table(c -> {
                                        c.add(content.localizedName).left();
                                        c.row();
                                        c.add(Core.bundle.get("extra-utilities.update-log-" + finalI)).wrap().fillX().left().width(Core.graphics.getWidth()/2f).padTop(10).row();
                                        if(finalI != updateLog.size - 1) c.image(Tex.whiteui, Pal.accent).left().growX().height(3f).row();
                                    }).left();
                                    info.row();
                                }
                            }).pad(4);
                            main.image(Tex.whiteui, Pal.accent).growY().width(3).pad(4).margin(5).left();
                        }).left().pad(6);
                        log.row();
                        log.image(Tex.whiteui, Pal.accent).left().growX().height(3f).row();
                    });
                    addToTable(EUStatusEffects.breakage, t);
                    addToTable(EUBlocks.rust, t);
                    addToTable(EUBlocks.ekLiquidDriver, t);
                    addToTable(TDSectorPresets.st1, t);
                    addToTable(EUBlocks.adaptiveMiner, t);
                    addToTable(EUBlocks.adaptiveMinerII, t);
                    addToTable(EUBlocks.ekSeparator, t);
                    addToTable(EUBlocks.shootingStar, t);
                    addToTable(EUBlocks.guiY, t);
                }).grow().center().maxWidth(Core.graphics.getWidth()/1.1f).get();
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
            cont.add(Core.bundle.get("eu.ap.main"));
            buttons.button("", this::hide).update(b ->{
                b.setText(con > 0 ? con == 5 ? Core.bundle.get("eu.ap.happy") : Core.bundle.get("eu.ap.click") : Core.bundle.get("eu.ap.ok"));
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

    public static void afterEnterLoad(){
        EUUnitTypes.suzerain.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> !EUUnitTypes.suzerain.immunities.contains(s) && (s.reloadMultiplier >= 1 && !s.disarm)));
        EUUnitTypes.Tera.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> (s == StatusEffects.none || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1) && !s.disarm));
        EUUnitTypes.nihilo.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s.reloadMultiplier >= 1 && !s.disarm));
        EUUnitTypes.narwhal.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> (s == StatusEffects.none || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1) && !s.disarm));
        EUUnitTypes.regency.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s == StatusEffects.none || s == EUStatusEffects.EUUnmoving || s == EUStatusEffects.EUDisarmed || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1));

        if(ui != null){
            eui = new EUI();
            eui.init();
            ui.menufrag.addButton(toText("eu-rogue-like-start"), Icon.defense, () -> eui.roguelike.toShow());
        }
    }

    public ExtraUtilitiesMod() {
        //WTMF - What This May From
        Log.info("Extra Utilities: try load WTMF");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(1f, EUFrom::load));
        Log.info("Loading completed, congratulations! :)");

        Log.info("Loaded ExtraUtilities Mod constructor.");
        Events.on(ClientLoadEvent.class, e -> Time.runTask(6f, ExtraUtilitiesMod::log));

        Log.info("Extra Utilities: building rand subtitle...");
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
        Graphics.Cursor.SystemCursor.arrow.set(newCursor("cursor.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.hand.set(newCursor("hand.png", Fonts.cursorScale()));
        Graphics.Cursor.SystemCursor.ibeam.set(newCursor("ibeam.png", Fonts.cursorScale()));
        ui.drillCursor = newCursor("drill.png", Fonts.cursorScale());
        ui.unloadCursor = newCursor("unload.png", Fonts.cursorScale());
        ui.targetCursor = newCursor("target.png", Fonts.cursorScale());
        //TODO new cursor
        ui.repairCursor = newCursor("repair.png", Fonts.cursorScale());
    }

    public static boolean isAps(){
        var date = LocalDate.now();
        var sdf = DateTimeFormatter.ofPattern("MMdd");
        var fd = sdf.format(date);
        return fd.equals("0401");
        //test
        //return true;
    }

    private boolean EUVerUnChange(String ver){
        String verHard = ver + "-hard";
        String verV8 = ver + "-V8";
        String verAll = ver + "-all";
        return ver == null || ver.equals(EU.meta.version) || verHard.equals(EU.meta.version) || verV8.equals(EU.meta.version) ||verAll.equals(EU.meta.version);
    }

    @Override
    public void init() {
        boolean aps = isAps();
        if(aps) EU.meta.version = "2.4.8p5";

        //settings.remove("eu-override-unit");

        if(!onlyPlugIn) {
            MainRenderer.init();
            EUCall.registerPackets();
            EUOverride.overrideUnitForAll(overrideUnitArm, coreReset);
            EUOverride.overrideTDRules(coreReset);
            EUOverride.overrideBlockAll(hardMod, coreResetV7, coreReset);
            EUOverride.overrideJs();
            afterEnterLoad();

            if(aps) EUOverride.ap4sOverride();
            if(ui != null) if(aps) Events.on(ClientLoadEvent.class, e -> Time.runTask(10f, ExtraUtilitiesMod::log2));
        }


        //settings.remove("eu-WTMF-open");

        Vars.mods.locateMod(ModName).meta.hidden = onlyPlugIn;
        if(onlyPlugIn){
            Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
            mod.meta.displayName = mod.meta.displayName + "-Plug-In";
            mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-plug-in";
        }

        if(hardMod){
            EUOverride.overrideHard();
            Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
            mod.meta.displayName = mod.meta.displayName + " Hard!";
            mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-hard";
        }

        if(!onlyPlugIn){
            if(!coreResetV7 && !coreReset){
                Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
                mod.meta.displayName = mod.meta.displayName + "-V8";
                mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-V8";
            }
            if(coreReset){
                Mods.LoadedMod mod = Vars.mods.locateMod(ModName);
                mod.meta.displayName = mod.meta.displayName + "-all";
                mod.meta.version = Vars.mods.locateMod(ModName).meta.version + "-all";
            }
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
                    settingsTable.checkPref("eu-WTMF-open", false);

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
                        settingsTable.checkPref("eu-reset-core-to-V7", true);
                        settingsTable.checkPref("eu-reset-core-to-all", false);
                        settingsTable.checkPref("eu-show-miner-point", true);
                        settingsTable.checkPref("eu-show-hole-acc-disk", true);
                        settingsTable.checkPref("eu-show-rust-range", true);

                        settingsTable.checkPref("eu-first-load", true);
                        if(!EUVerUnChange((String) settings.get("eu-version", ""))){
                            settings.put("eu-first-load", true);
                            settings.put("eu-version", EU.meta.version);
                        }
                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-me-now")) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                table.button(name, ExtraUtilitiesMod::toShow).margin(14).width(200f).pad(6);
                                table.row();
                            }
                        });

                        settingsTable.checkPref("eu-override-unit", false);

                        settingsTable.checkPref("eu-override-unit-missile", true);

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

                        settingsTable.pref(new SettingsMenuDialog.SettingsTable.Setting(Core.bundle.get("eu-show-donor-and-develop")) {
                            @Override
                            public void add(SettingsMenuDialog.SettingsTable table) {
                                table.button(name, eui.ddItemsList::toShow).margin(14).width(200f).pad(6);
                                table.row();
                            }
                        });
                    }
                });
            }

            if(!onlyPlugIn) {
                UnitCommand c = new UnitCommand("EUAssist", "defense", u -> new DefenderHealAI());
                EUUnitTypes.narwhal.commands.add(c);
                EUUnitTypes.narwhal.defaultCommand = c;
            }
        }

        setColorName();
    }

    @Override
    public void loadContent(){
        EU = Vars.mods.getMod(ExtraUtilitiesMod.class);

        String ms = isAps() ? Core.bundle.get("mod.random-massageAp") : Core.bundle.get("mod.random-massage");
        String[] me = ms.split(",");
        int len = me.length;
        massageRand = me[Mathf.random(len - 1)];

        if(settings != null){
            settings.defaults("eu-override-unit", false);
            settings.defaults("eu-plug-in-mode", false);
            settings.defaults("eu-hard-mode", false);
            settings.defaults("use-eu-cursor", true);
            settings.defaults("eu-show-version", true);
            settings.defaults("eu-override-unit-missile", true);
            settings.defaults("eu-reset-core-to-V7", true);
            settings.defaults("eu-reset-core-to-all", false);

            hardMod = Core.settings.getBool("eu-hard-mode");
            onlyPlugIn = Core.settings.getBool("eu-plug-in-mode");
            coreResetV7 = Core.settings.getBool("eu-reset-core-to-V7");
            coreReset = Core.settings.getBool("eu-reset-core-to-all");
            overrideUnitArm = Core.settings.getBool("eu-override-unit");
            overrideUnitMissile = Core.settings.getBool("eu-override-unit-missile");
        }

        if(onlyPlugIn) return;

        if(isAps()) hardMod = true;

        EUOverride.overrideItem();
        EUStatusEffects.load();

        EUAttribute.load();
        EUOverride.overrideUnit1();
        if(overrideUnitMissile) EUOverride.overrideUnitMissile();
        EUUnitTypes.load();
        EUUnitTypes.loadBoss();

        EUOverride.overrideBlock1();
        EUBlocks.load();

        TDPlanet.load();
        TDSectorPresets.load();

        EUTechTree.load();

        if(!headless) EUGet.loadItems();
    }

    public static void rebuildRandSubTitle(String title){
        var subTitle = new EUMenuFragment(title);
        subTitle.build(ui.menuGroup);
    }

    private static class EUMenuFragment {
        private final String title;

        public EUMenuFragment(String title){
            this.title = title;
        }
        //@Override
        public void build(Group parent) {
            //super.build(parent);
            parent.fill((x, y, w, h) -> {
                TextureRegion logo = Core.atlas.find("logo");
                float width = Core.graphics.getWidth(), height = Core.graphics.getHeight() - Core.scene.marginTop;
                float logoscl = Scl.scl(1) * logo.scale;
                float logow = Math.min(logo.width * logoscl, Core.graphics.getWidth() - Scl.scl(20));
                float logoh = logow * (float)logo.height / logo.width;

                float fx = (int)(width / 2f);
                float fy = (int)(height - 6 - logoh) + logoh / 2 - (Core.graphics.isPortrait() ? Scl.scl(30f) : 0f);
                if(Core.settings.getBool("macnotch") ){
                    fy -= Scl.scl(macNotchHeight);
                }

                float ex = fx + logow/3 - Scl.scl(1f), ey = fy - logoh/3f - Scl.scl(2f);
                float ang = 12 + Mathf.sin(Time.time, 8, 2f);

                float dst = Mathf.dst(ex, ey, 0, 0);
                vec2.set(0, 0);
                float dx = EUGet.dx(0, dst, vec2.angleTo(ex, ey) + ang);
                float dy = EUGet.dy(0, dst, vec2.angleTo(ex, ey) + ang);

                reMat.set(Draw.trans());

                Draw.trans(setMat.setToTranslation(ex - dx, ey - dy).rotate(ang));
                Fonts.outline.draw(title, ex, ey, Color.yellow, Math.min(30f/title.length(), 1.5f) + Mathf.sin(Time.time, 8, 0.2f), false, Align.center);

                Draw.trans(reMat);

            }).touchable = Touchable.disabled;
        }
    }
}
