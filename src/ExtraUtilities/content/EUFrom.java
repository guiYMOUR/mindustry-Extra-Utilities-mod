package ExtraUtilities.content;

import ExtraUtilities.ui.ItemImage;
import ExtraUtilities.worlds.blocks.unit.DerivativeUnitFactory;
import arc.Core;
import arc.graphics.Color;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static ExtraUtilities.ExtraUtilitiesMod.onlyPlugIn;
import static arc.Core.app;
import static arc.Core.settings;
import static mindustry.Vars.*;

public class EUFrom {
    public static final StatCat WTMF = new StatCat("eu-wtmf");
    public static final Stat fromStat = new Stat("eu-from", WTMF);
    public static final Stat toStat = new Stat("eu-to", WTMF);
    public static final Stat reqStat = new Stat("eu-req", WTMF);

    private static final ObjectMap<UnlockableContent, ObjectMap<Block, Table>> fromBlock = new ObjectMap<>();
    public static final ObjectMap<UnlockableContent, Table> fromTables = new ObjectMap<>();

    private static final ObjectMap<UnlockableContent, ObjectMap<Block, Table>> toBlock = new ObjectMap<>();
    public static final ObjectMap<UnlockableContent, Table> toTables = new ObjectMap<>();

    public static final ObjectMap<UnlockableContent, Table> requireTables = new ObjectMap<>();

    public static final ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<UnlockableContent, Float>>> repMap_to = new ObjectMap<>();
    public static final ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<Drawable, Float>>> repMap_need = new ObjectMap<>();
    public static final ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<UnlockableContent, Float>>> repMap_back = new ObjectMap<>();

    private static final ObjectMap<UnlockableContent, Table> fromTables_pix = new ObjectMap<>();
    private static final ObjectMap<UnlockableContent, Table> toTables_ammo = new ObjectMap<>();
    private static final ObjectMap<UnlockableContent, Table> toTables_boost = new ObjectMap<>();

    public static void load(){
        if(ui == null || ui.settings == null) return;
        settings.defaults("eu-WTMF-open", app == null || app.isDesktop());
        if(settings.getBool("eu-WTMF-open")) {
            getProduce();
            getConsume();

            mixRep(repMap_to, fromBlock, Icon.leftSmall);
            mixPowerHeat();

            mixRep(repMap_back, toBlock, Icon.rightSmall);

            mixFromTo();
            mixToTable();

            showTable(fromTables, fromStat);
            showTable(toTables, toStat);
            showTable(requireTables, reqStat);
        }
    }

    public static void showTable(ObjectMap<UnlockableContent, Table> tables, Stat stat){
        var keys = tables.keys().toSeq();
        if(keys.size > 0) for(var c : keys){
            if(tables.get(c) != null) {
                c.stats.useCategories = true;
                c.stats.add(stat, t -> {
                    t.left().defaults().left();

                    Table ic = tables.get(c);
                    Collapser coll = new Collapser(ic, true);
                    coll.setDuration(0.1f);
                    t.row();
                    t.table(st -> {
                        st.add(Core.bundle.get("eu-clickToShow")).center();
                        st.row();
                        st.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).pad(5).size(8).center();
                    }).left();
                    t.row();
                    t.add(coll);
                    t.row();
                });
            }
        }
    }

    private static void getProduce(){
        for(Block b : content.blocks()){
            //pick
            if(b.itemDrop != null){
                setToTableSimple(fromTables_pix, b.itemDrop, b, "  " + Core.bundle.get("eu-asOre"));
            }

            if(b instanceof Floor f && f.liquidDrop != null){
                setToTableSimple(fromTables_pix, f.liquidDrop, f, "  " + Core.bundle.get("eu-asWater"));
            }

            if(b instanceof GenericCrafter g){
                LiquidStack[] lss = g.outputLiquids;
                ItemStack[] iss = g.outputItems;
                if(lss != null && lss.length > 0){
                    for(LiquidStack d : lss){
                        setToTable(fromBlock, d.liquid, b);
                        checkPower(b, d.liquid);
                    }
                }

                if(iss != null && iss.length > 0){
                    for(ItemStack d : iss){
                        setToTable(fromBlock, d.item, b);
                        checkPower(b, d.item);
                    }
                }
            }

            if(b instanceof Separator s){
                if(s.results != null && s.results.length > 0){
                    float all = 0;
                    for(ItemStack is : s.results){
                        all += is.amount;
                    }

                    for(ItemStack is : s.results){
                        setToTableSimple(fromTables, is.item, b,
                                " " + Core.bundle.get("eu-separator-probability") + "[accent]" + Strings.autoFixed(is.amount / all * 100, 2) + "[]%"
                                + (s.consPower != null ? ("  " + Iconc.power + "[accent]" + Strings.autoFixed(s.consPower.usage * 60, 2)) + "[]" : "")
                        );
                    }
                }
            }

            if(b instanceof ConsumeGenerator c){
                LiquidStack ls = c.outputLiquid;
                if(ls != null){
                    setToTable(fromBlock, ls.liquid, b);
                }
            }
            if(b instanceof ThermalGenerator c){
                LiquidStack ls = c.outputLiquid;
                if(ls != null){
                    setToTable(fromBlock, ls.liquid, b);
                }
            }
            if(b instanceof SolidPump sp){
                Liquid ls = sp.result;
                setToTable(fromBlock, ls, sp, (sp.consPower != null ? ("  " + Iconc.power + "[accent]" + Strings.autoFixed(sp.consPower.usage * 60, 2)) + "[]" : ""));
            }

            if(b instanceof UnitFactory uf){
                if(uf.plans.size > 0) for(UnitFactory.UnitPlan p : uf.plans) {
                    setToTable(fromBlock, p.unit, b);
                    if(p.requirements.length > 0) for(var is : p.requirements){
                        putOnRep(repMap_to, p.unit, is.item, b, (float) is.amount);
                    }
                    checkPower(b, p.unit);
                }
            }

            if(b instanceof Reconstructor rc){
                if(rc.upgrades.size > 0) for(UnitType[] ut : rc.upgrades){
                    if(ut.length == 2){
                        UnitType from = ut[0];
                        UnitType to = ut[1];
                        setToTable(fromBlock, to, b);
                        putOnRep(repMap_to, to, from, b, -1f);

                        setToTable(toBlock, from, b);
                        putOnRep(repMap_back, from, to, b, -1f);
                    }
                }
            }

            if(b instanceof UnitAssembler ua){
                if(ua.plans.size > 0) for(UnitAssembler.AssemblerUnitPlan ap : ua.plans){
                    UnitType to = ap.unit;
                    var req = ap.requirements;
                    setToTable(fromBlock, to, b);
                    if(req.size > 0) for(var q : req){
                        putOnRep(repMap_to, to, q.item, b, (float) q.amount);

                        if(q.item instanceof UnitType ut){
                            setToTable(toBlock, ut, b);
                            putOnRep(repMap_back, ut, ut, b, (float) q.amount);
                            putOnRep(repMap_back, ut, to, b, -1f);
                        }
                        putOnRep(repMap_back, q.item, to, b, (float) q.amount);
                    }
                }
            }
        }

        if(onlyPlugIn) return;
        var GC = content.block(name("GC"));
        if(GC != null){
            setToTable(fromBlock, Items.pyratite, GC);
        }

        for(UnitFactory.UnitPlan p : ((DerivativeUnitFactory)EUBlocks.finalF).plans){
            if(p instanceof EUBlocks.LiquidUnitPlan lq && lq.liquid.length > 0) for(var l : lq.liquid){
                putOnRep(repMap_to, p.unit, l.liquid, EUBlocks.finalF, l.amount * 60);
            }
        }
    }

    public static void checkPower(Block b, UnlockableContent content){
        var c = b.consPower;
        if(c != null) putOnRep_en(repMap_need, content, Icon.powerSmall, b, c.usage * 60);

        if(b instanceof HeatCrafter hc){
            putOnRep_en(repMap_need, content, Icon.wavesSmall, b, hc.heatRequirement);
        }
    }

    public static void checkBlock(Block b, UnlockableContent content, Float amount){
        if(b instanceof GenericCrafter g){
            LiquidStack[] lss = g.outputLiquids;
            ItemStack[] iss = g.outputItems;
            if(lss != null && lss.length > 0){
                for(LiquidStack d : lss){
                    putOnRep(repMap_to, d.liquid, content, b, amount);
                    putOnRep(repMap_back, content, d.liquid, b, -1f);
                }
            }

            if(iss != null && iss.length > 0){
                for(ItemStack d : iss){
                    putOnRep(repMap_to, d.item, content, b, amount);
                    putOnRep(repMap_back, content, d.item, b, -1f);
                }
            }
        }

        if(b instanceof Separator s){
            if(s.results != null && s.results.length > 0){
                for(ItemStack is : s.results){
                    putOnRep(repMap_back, content, is.item, b, -1f);
                }
            }
        }

        if(b instanceof SolidPump sp){
            putOnRep(repMap_to, sp.result, content, sp, amount);
            putOnRep(repMap_back, content, sp.result, sp, -1f);
        }

        if(b instanceof ConsumeGenerator cg){
            if(cg.outputLiquid != null) putOnRep(repMap_to, cg.outputLiquid.liquid, content, cg, amount);
        }

        if(b instanceof UnitFactory uf){
            if(uf.plans.size > 0) for(UnitFactory.UnitPlan p : uf.plans) {
                putOnRep(repMap_to, p.unit, content, b, amount);
            }
        }

        if(b instanceof Reconstructor rc){
            if(rc.upgrades.size > 0) for(UnitType[] ut : rc.upgrades){
                if(ut.length == 2){
                    UnitType from = ut[0];
                    UnitType to = ut[1];
                    putOnRep(repMap_to, to, content, b, amount);
                    checkPower(b, to);
                    putOnRep(repMap_back, from, content, b, amount);
                }
            }
        }

        if(b instanceof UnitAssembler ua) {
            if (ua.plans.size > 0) for (UnitAssembler.AssemblerUnitPlan ap : ua.plans) {
                UnitType to = ap.unit;
                putOnRep(repMap_to, to, content, b, amount);
                checkPower(b, to);
                var req = ap.requirements;
                if (req.size > 0) for (var q : req) {
                    if (q.item instanceof UnitType ut) {
                        for(var qq : req){
                            if(qq != q){
                                putOnRep(repMap_back, ut, qq.item, b, (float) qq.amount);
                            }
                        }

                        putOnRep(repMap_back, ut, to, b, -1f);
                        putOnRep(repMap_back, ut, content, b, amount);
                    }
                }
            }
        }
    }

    private static Stack ItemButtonImg(ItemStack subIs){
        Stack sc = new Stack();
        sc.add(new Table(c -> {
            c.left();
            c.button(new TextureRegionDrawable(subIs.item.uiIcon), Styles.emptyi, 28, () -> ui.content.show(subIs.item)).scaling(Scaling.fit);
        }));
        if (subIs.amount != 0) {
            sc.add(new Table(c -> {
                c.left().bottom();
                c.add(subIs.amount >= 1000 ? UI.formatAmount(subIs.amount) : subIs.amount + "").style(Styles.outlineLabel).touchable(Touchable.disabled);
                c.pack();
            }));
        }
        return sc;
    }

    private static void setRequire(Block b){
        var iss = b.requirements;
        if(iss.length > 0) {
            for(var is : iss){
                Table tb = new Table();

                tb.row();
                tb.table(Styles.grayPanel, t -> {
                    t.table(c -> {
                        c.button(new TextureRegionDrawable(b.uiIcon), Styles.emptyi, 28, () -> ui.content.show(b)).pad(2).left();
                        c.add(b.localizedName).left();
                    }).left();

                    t.table(c -> {
                        c.right();
                        c.add(new Image(Icon.starSmall));
                        c.add(new ItemImage(is)).pad(2).right();
                        c.row();
                        if(iss.length > 1) {
                            c.table(icon -> icon.add(new Image(Icon.treeSmall)));
                            c.table(re -> {
                                for (int i = 0; i < iss.length; i++) {
                                    var subIs = iss[i];
                                    if (subIs == is) continue;
                                    re.add(ItemButtonImg(subIs)).left().pad(2);
                                    if ((i + 1) % 5 == 0) re.row();
                                }
                            });
                        }
                    }).right().grow().pad(5);
                }).growX().pad(5);
                tb.row();

                setToTableSimple(requireTables, is.item, tb, true);
            }
        }
    }

    private static void getConsume(){
        for(Block b : content.blocks()){
            if(b == null) continue;

            setRequire(b);

            if(b.consumers != null && b.consumers.length > 0) {
                for (Consume c : b.consumers) {
                    boolean has = false;
                    if (c instanceof ConsumeItems ci) {
                        ItemStack[] is = ci.items;
                        if (is.length > 0) {
                            for (ItemStack ik : is) {
                                witchToTable(ci, ik.item, b);
                                if(!c.booster) checkBlock(b, ik.item, (float) ik.amount);
                                has = true;
                            }
                        }
                    }
                    else if (c instanceof ConsumeItemFilter cf) {
                        for (Item it : Vars.content.items()) {
                            if (cf.filter.get(it)) {
                                witchToTable(cf, it, b);
                                if(!c.booster) checkBlock(b, it, 1f);
                                has = true;
                            }
                        }
                    }

                    if(has) break;
                }
                for(Consume c : b.consumers){
                    boolean has = false;
                    if (c instanceof ConsumeLiquid cl) {
                        witchToTable(cl, cl.liquid, b);
                        if(!c.booster) checkBlock(b, cl.liquid, cl.amount * 60);
                        break;
                    }
                    else if (c instanceof ConsumeLiquids cls) {
                        LiquidStack[] ls = cls.liquids;
                        if (ls.length > 0) for (LiquidStack lk : ls) {
                            witchToTable(cls, lk.liquid, b);
                            if(!c.booster) checkBlock(b, lk.liquid, lk.amount * 60);
                            has = true;
                        }
                    }
                    else if (c instanceof ConsumeLiquidFilter lf) {
                        for (Liquid lq : Vars.content.liquids()) {
                            if (lf.filter.get(lq)) {
                                witchToTable(lf, lq, b);
                                if(!c.booster) checkBlock(b, lq, lf.amount * 60);
                                has = true;
                            }
                        }
                    }
                    if(has) break;
                }
            }
        }
    }

    public static void setToTableSimple(ObjectMap<UnlockableContent, Table> table, UnlockableContent content, Table add, boolean override){
        if(!table.containsKey(content)){
            table.put(content, new Table());
        }
        Table t = table.get(content);
        t.row();
        if(override) t.defaults().growX();
        t.add(add).left();
    }

    public static void setToTableSimple(ObjectMap<UnlockableContent, Table> table, UnlockableContent content, Table add){
        setToTableSimple(table, content, add, false);
    }

    public static void setToTableSimple(ObjectMap<UnlockableContent, Table> table, UnlockableContent content, Block b, String add){
        if(!table.containsKey(content)){
            table.put(content, new Table());
        }
        Table t = table.get(content);
        t.row();
        t.table(tb -> {
            tb.table(Styles.grayPanel, ttb ->
                    ttb.button(new TextureRegionDrawable(b.uiIcon), Styles.emptyi, 28,() -> ui.content.show(b)).left().pad(8)
            ).left();
            tb.add(b.localizedName);
            if(add != null) tb.add(add);
        }).padTop(10).padBottom(5).left();
    }

    public static void setToTableSimple(ObjectMap<UnlockableContent, Table> table, UnlockableContent content, Block b){
        setToTableSimple(table, content, b, null);
    }

    public static void setToTable(ObjectMap<UnlockableContent, ObjectMap<Block, Table>> fb, UnlockableContent content, Block b, String add){
        if(!fb.containsKey(content)){
            fb.put(content, new ObjectMap<>());
        }
        var map = fb.get(content);
        if(!map.containsKey(b)){
            map.put(b, new Table());
        }
        Table t = map.get(b);
        t.row();
        t.table(tb -> {
            tb.table(Styles.grayPanel, ttb ->
                    ttb.button(new TextureRegionDrawable(b.uiIcon), Styles.emptyi, 28,() -> ui.content.show(b)).left().pad(8)
            ).left();
            tb.add(b.localizedName);
            if(add != null) tb.add(add);
        }).padTop(10).padBottom(5).left();
    }

    public static void setToTable(ObjectMap<UnlockableContent, ObjectMap<Block, Table>> fb, UnlockableContent content, Block b){
        setToTable(fb, content, b, null);
    }

    public static void witchToTable(Consume c, UnlockableContent content, Block b){
        if(!isAmmo(content, b) && !isBoost(c)){
            setToTable(toBlock, content, b);
        } else {
            if(isAmmo(content, b)){
                setToTableSimple(toTables_ammo, content, b);
            } else if(isBoost(c)){
                setToTableSimple(toTables_boost, content, b);
            }
        }
    }

    public static void mixFromTo(){
        var key_from = fromBlock.keys().toSeq();
        if(key_from.size > 0) for(var c : key_from){
            var map = fromBlock.get(c);
            var key_block = map.keys().toSeq();
            if(key_block.size > 0) for(var b : key_block){
                setToTableSimple(fromTables, c, map.get(b));
            }
        }

        var key_to = toBlock.keys().toSeq();
        if(key_to.size > 0) for(var c : key_to){
            var map = toBlock.get(c);
            var key_block = map.keys().toSeq();
            if(key_block.size > 0) for(var b : key_block){
                setToTableSimple(toTables, c, map.get(b));
            }
        }
    }

    private static void toMixTable(UnlockableContent c, ObjectMap<UnlockableContent, Table> tables, ObjectMap<UnlockableContent, Table> tables_any, String add){
        if(!tables.containsKey(c)){
            tables.put(c, new Table());
        }
        var t = tables.get(c);
        t.row();
        Table ic = tables_any.get(c);
        Collapser coll = new Collapser(ic, true);
        coll.setDuration(0.1f);
        t.row();
        if(add != null) t.add(add).center();
        t.row();
        t.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).pad(5).size(8).center();
        t.row();
        t.add(coll).padBottom(40);
        t.row();
        t.row();
    }

    private static void mixToTable(){
        var key_pix = fromTables_pix.keys().toSeq();
        if(key_pix.size > 0) for(var c : key_pix){
            toMixTable(c, fromTables, fromTables_pix, Core.bundle.get("eu-from-needPick"));
        }

        var key_ammo = toTables_ammo.keys().toSeq();
        if(key_ammo.size > 0) for(var c : key_ammo){
            toMixTable(c, toTables, toTables_ammo, Core.bundle.get("eu-to-stat-ammo"));
        }

        var key_boost = toTables_boost.keys().toSeq();
        if(key_ammo.size > 0) for(var c : key_boost){
            toMixTable(c, toTables, toTables_boost, Core.bundle.get("eu-to-stat-boost"));
        }
    }

    public static void putOnRep(ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<UnlockableContent, Float>>> repMap, UnlockableContent to, UnlockableContent from, Block b, Float f){
        if(!repMap.containsKey(to)){
            repMap.put(to, new ObjectMap<>());
        }
        if(!repMap.get(to).containsKey(b)){
            repMap.get(to).put(b, new ObjectMap<>());
        }
        repMap.get(to).get(b).put(from, f);
    }

    public static void putOnRep_en(ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<Drawable, Float>>> repMap, UnlockableContent to, Drawable name, Block b, Float f){
        if(!repMap.containsKey(to)){
            repMap.put(to, new ObjectMap<>());
        }
        if(!repMap.get(to).containsKey(b)){
            repMap.get(to).put(b, new ObjectMap<>());
        }
        repMap.get(to).get(b).put(name, f);
    }

    public static void mixRep(ObjectMap<UnlockableContent, ObjectMap<Block, ObjectMap<UnlockableContent, Float>>> rep, ObjectMap<UnlockableContent, ObjectMap<Block, Table>> blockTable, Drawable name){
        var key_to = rep.keys().toSeq();
        if(key_to.size > 0) for(var c : key_to){
            if(blockTable.containsKey(c)){
                var map = blockTable.get(c);
                var key_block = map.keys().toSeq();
                var rep_block = rep.get(c).keys().toSeq();
                if(key_block.size > 0 && rep_block.size > 0) for(var b : rep_block){
                    if(map.containsKey(b)){
                        var tb = map.get(b);

                        var map_block = rep.get(c).get(b);
                        var block_key = map_block.keys().toSeq();
                        if(block_key.size > 0) {
                            tb.table(t -> t.image(name).color(Pal.darkishGray).pad(8f)).left();

                            var sort = sortContent(block_key);
                            for(int i = 0; i < sort.size; i++){
                                var cb = sort.get(i);
                                int finalI = i;
                                tb.table(t -> {
                                    t.button(new TextureRegionDrawable(cb.uiIcon), Styles.emptyi, 28,() -> ui.content.show(cb)).left().pad(8);
                                    float amount = map_block.get(cb);
                                    if(amount > 0) t.add("x[accent]" + Strings.autoFixed(amount, 2) + "[]");
                                    if(amount <= 0 && finalI < sort.size - 1) t.add("/");
                                    t.add(" ");
                                }).left();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void mixPowerHeat(){
        var key = fromBlock.keys().toSeq();
        if(key.size > 0) for(var c : key){
            if(repMap_need.containsKey(c)){
                var bt = repMap_need.get(c);
                var bs = bt.keys().toSeq();
                if(bs.size > 0) for(var b : bs){
                    if(!fromBlock.get(c).containsKey(b)) {
                        fromBlock.get(c).put(b, new Table());
                    }
                    var tb = fromBlock.get(c).get(b);
                    var dm = repMap_need.get(c).get(b);
                    var ds = dm.keys().toSeq();
                    if(ds.size > 0) for(Drawable d : ds){
                        tb.table(t -> {
                            t.image(d).color(Color.white).pad(8f);
                            t.add("[accent]" + Strings.autoFixed(dm.get(d), 2));
                        }).left();
                    }
                }
            }
        }
    }

    private static Seq<UnlockableContent> sortContent(Seq<UnlockableContent> seq){
        if(seq.size <= 0) return seq;
        Seq<UnlockableContent> copy = new Seq<>();
        copy.addAll(seq.copy().removeAll(c -> !(c instanceof UnitType)).sort(c -> -((UnitType)c).health));
        copy.addAll(seq.copy().removeAll(c -> c instanceof UnitType || c instanceof Liquid).sort(c -> c.id));
        copy.addAll(seq.copy().removeAll(c -> !(c instanceof Liquid)).sort(c -> c.id));
        return copy;
    }

    private static boolean isBoost(Consume c){
        return c.booster;
    }

    private static boolean isAmmo(UnlockableContent content, Block b){
        return (content instanceof Item && b instanceof Turret) || (content instanceof Liquid && (b instanceof LiquidTurret || b instanceof ContinuousLiquidTurret));
    }
}
