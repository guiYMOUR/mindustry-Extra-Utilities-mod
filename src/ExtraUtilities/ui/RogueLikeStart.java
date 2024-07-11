package ExtraUtilities.ui;

import ExtraUtilities.ExtraUtilitiesMod;
import arc.Core;
import arc.func.Cons;
import arc.scene.ui.layout.Table;
import mindustry.content.UnitTypes;
import mindustry.game.Gamemode;
import mindustry.game.Rules;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.io.MapIO;
import mindustry.maps.Map;
import mindustry.type.Category;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.io.IOException;

import static mindustry.Vars.*;

public class RogueLikeStart extends BaseDialog {
    private Weaves weaves = Weaves.limit;
    public Difficult difficult = Difficult.normal;

    private Rules rules;

    public RogueLikeStart() {
        super("start");
    }

    public void toShow(){
        Map map;// = maps.loadInternalMap("MitoKenos");
        try {
            map = MapIO.createMap(ExtraUtilitiesMod.EU.root.child("roguelike").child("MitoKenos.msav"), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cont.clear();

        Table selweave = new Table();
        selweave.add("weave").colspan(2);
        selweave.row();

        selweave.table(Tex.button, diff -> {
            int i = 0;
            for(Weaves w : Weaves.values()){
                i++;
                diff.button(w.toString(), Styles.flatToggleMenut, () -> weaves = w).update(b -> b.setChecked(weaves == w)).size(140f, mobile ? 44f : 54f);
                if(i % 2 == 0) diff.row();
            }
        });

        cont.add(selweave);

        cont.row();

        Table selmode = new Table();
        selmode.add("difficult").colspan(2);
        selmode.row();

        rules = map.applyRules(Gamemode.survival);

        selmode.table(Tex.button, diff -> {
            int i = 0;
            for(Difficult d : Difficult.values()){
                i++;
                diff.button(d.toString(), Styles.flatToggleMenut, () -> difficult = d).update(b -> b.setChecked(difficult == d)).size(140f, mobile ? 44f : 54f);
                if(i % 2 == 0) diff.row();
            }
        });

        cont.add(selmode);
        cont.row();
        cont.image(Tex.whiteui, Pal.accent).left().width(500f).height(3f).pad(4f).row();
        cont.pane(ds -> {
            Table dt = new Table();
            dt.add(difficult.description()).wrap().fillX().padLeft(10).width(500f).padTop(10).left();
            Table dw = new Table();
            if(weaves == Weaves.limit){
                dw.add(Core.bundle.format("eu-rogue.endWeave", difficult.end)).wrap().fillX().padLeft(10).width(500f).padTop(10).left();
            }
            selmode.changed(() -> rebuildShow(dt, dw));
            selweave.changed(() -> rebuildShow(dt, dw));
            ds.add(dt).row();
            ds.add(dw).row();

            ds.add(Core.bundle.get("eu-rogue-like-tip")).wrap().fillX().padLeft(10).width(500f).padTop(10);
        });

        buttons.clearChildren();

        addCloseButton();
        addCloseListener();
        buttons.button("@play", Icon.play, () -> {
            if(rules == null) return;
            difficult.apply(rules, weaves == Weaves.endless);
            control.playMap(map, rules, false);
            hide();
            ui.custom.hide();
        }).size(210f, 64f);

        show();
    }

    private void rebuildShow(Table dt, Table dw){
        dt.clear();
        dt.add(difficult.description()).wrap().fillX().padLeft(10).width(500f).padTop(10).left();
        dw.clear();
        if(weaves == Weaves.limit){
            dw.add(Core.bundle.format("eu-rogue.endWeave", difficult.end)).wrap().fillX().padLeft(10).width(500f).padTop(10).left();
        }
    }

    public enum Difficult{
        easy(rules -> {
            rules.blockHealthMultiplier = 2;
            rules.blockDamageMultiplier = 1.5f;
            rules.buildCostMultiplier = 0.5f;
            defRule(rules);
        }, 40),
        normal(Difficult::defRule, 60),
        hard(rules -> {
            rules.blockDamageMultiplier = 0.8f;
            rules.buildCostMultiplier = 1.1f;
            rules.bannedBlocks.addAll(content.blocks().copy().removeAll(b -> b.category != Category.turret || b.size < 5));
            defRule(rules);
        }, 80),
        impossible(rules -> {
            rules.blockDamageMultiplier = 0.6f;
            rules.buildCostMultiplier = 1.2f;
            rules.deconstructRefundMultiplier = 0;
            rules.bannedBlocks.addAll(content.blocks().copy().removeAll(b -> b.category != Category.turret || b.size < 5));
            defRule(rules);
        }, 100);

        private final Cons<Rules> rules;
        private final int end;

        Difficult(Cons<Rules> rules, int end){
            this.rules = rules;
            this.end = end;
        }

        public Rules apply(Rules in, boolean endless){
            rules.get(in);
            if(!endless) in.winWave = end;
            return in;
        }

        @Override
        public String toString() {
            return Core.bundle.get("eu-rogue." + name() + ".name");
        }

        public String description() {
            return Core.bundle.get("eu-rogue." + name() + ".description");
        }

        public static void defRule(Rules rules){
            rules.bannedUnits.addAll(content.units());
            rules.bannedUnits.remove(UnitTypes.mono);
            rules.bannedUnits.remove(UnitTypes.poly);
        }
    }

    public enum Weaves{
        limit,
        endless;

        @Override
        public String toString() {
            return Core.bundle.get("eu-rogue." + name() + ".name");
        }
    }
}
