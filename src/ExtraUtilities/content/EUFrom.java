package ExtraUtilities.content;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.blocks.production.SolidPump;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static mindustry.Vars.*;

public class EUFrom {
    public static final Stat fromStat = new Stat("eu-from");
    public static final Stat toStat = new Stat("eu-to");
    public static final ObjectMap<UnlockableContent, Table> fromTables = new ObjectMap<>();
    public static final ObjectMap<UnlockableContent, Table> toTables = new ObjectMap<>();

    private static final ObjectMap<UnlockableContent, Table> fromTables_pix = new ObjectMap<>();
    private static final ObjectMap<UnlockableContent, Table> toTables_ammo = new ObjectMap<>();
    private static final ObjectMap<UnlockableContent, Table> toTables_boost = new ObjectMap<>();

    public static void load(){
        if(ui == null || ui.settings == null) return;
        clearTable(fromTables);
        clearTable(toTables);

        getProduce();
        getConsume();

        mixToTable();

        showTable(fromTables, fromStat);
        showTable(toTables, toStat);
    }

    public static void clearTable(ObjectMap<UnlockableContent, Table> tables){
        tables.clear();
    }

    public static void showTable(ObjectMap<UnlockableContent, Table> tables, Stat stat){
        var keys = tables.keys().toSeq();
        if(keys.size > 0) for(var c : keys){
            if(tables.get(c) != null) c.stats.add(stat, t -> {
                t.left().defaults().left();

                Table ic = tables.get(c);
                Collapser coll = new Collapser(ic, true);
                coll.setDuration(0.1f);
                t.row();
                t.add(Core.bundle.get("eu-clickToShow")).left();
                t.row();
                t.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).left();
                t.row();
                t.add(coll);
                t.row();
            });
        }
    }

    private static void getProduce(){
        for(Block b : content.blocks()){
            //pick
            if(b.itemDrop != null){
                setToTable(fromTables_pix, b.itemDrop, b, "  " + Core.bundle.get("eu-asOre"));
            }

            if(b instanceof Floor f && f.liquidDrop != null){
                setToTable(fromTables_pix, f.liquidDrop, f, "  " + Core.bundle.get("eu-asWater"));
            }

            if(b instanceof GenericCrafter g){
                LiquidStack[] lss = g.outputLiquids;
                ItemStack[] iss = g.outputItems;
                if(lss != null && lss.length > 0){
                    for(LiquidStack d : lss){
                        setToTable(fromTables, d.liquid, b);
                    }
                }

                if(iss != null && iss.length > 0){
                    for(ItemStack d : iss){
                        setToTable(fromTables, d.item, b);
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
                        setToTable(fromTables, is.item, b, " " + Core.bundle.get("eu-separator-probability") + "[accent]" + Strings.autoFixed(is.amount / all * 100, 2) + "[]%");
                    }
                }
            }

            if(b instanceof ConsumeGenerator c){
                LiquidStack ls = c.outputLiquid;
                if(ls != null){
                    setToTable(fromTables, ls.liquid, b);
                }
            }
            if(b instanceof ThermalGenerator c){
                LiquidStack ls = c.outputLiquid;
                if(ls != null){
                    setToTable(fromTables, ls.liquid, b);
                }
            }
            if(b instanceof SolidPump sp){
                Liquid ls = sp.result;
                setToTable(fromTables, ls, sp);
            }

            if(b instanceof UnitFactory uf){
                if(uf.plans.size > 0) for(UnitFactory.UnitPlan p : uf.plans) {
                    setToTable(fromTables, p.unit, b);
                }
            }

            if(b instanceof Reconstructor rc){
                if(rc.upgrades.size > 0) for(UnitType[] ut : rc.upgrades){
                    if(ut.length == 2){
                        UnitType from = ut[0];
                        UnitType to = ut[1];
                        setToTable(fromTables, to, b);
                        Table tf = fromTables.get(to);
                        tf.table(t -> {
                            t.image(Icon.left).color(Pal.darkishGray).size(32).pad(8f);
                            t.button(new TextureRegionDrawable(from.uiIcon), Styles.emptyi, 32,() -> ui.content.show(from)).left();
                        }).fill().padTop(5).padBottom(5);

                        setToTable(toTables, from, b);
                        Table tt = toTables.get(from);
                        tt.table(t -> {
                            t.image(Icon.right).color(Pal.darkishGray).size(32).pad(8f);
                            t.button(new TextureRegionDrawable(to.uiIcon), Styles.emptyi, 32,() -> ui.content.show(to)).left();
                        }).fill().padTop(5).padBottom(5);
                    }
                }
            }

            if(b instanceof UnitAssembler ua){
                if(ua.plans.size > 0) for(UnitAssembler.AssemblerUnitPlan ap : ua.plans){
                    UnitType to = ap.unit;
                    var req = ap.requirements;
                    setToTable(fromTables, to, b);
                    Table tf = fromTables.get(to);
                    tf.table(t -> t.image(Icon.left).color(Pal.darkishGray).size(32).pad(8f)).fill().padTop(5).padBottom(5);
                    if(req.size > 0) for(var q : req){
                        tf.table(t -> {
                            t.button(new TextureRegionDrawable(q.item.uiIcon), Styles.emptyi, 32, () -> ui.content.show(q.item)).left().pad(2);
                            t.add(" x [accent]" + q.amount + "[]");
                        });

                        if(q.item instanceof UnitType ut){
                            setToTable(toTables, ut, b);
                            toTables.get(ut).table(t -> {
                                t.image(Icon.right).color(Pal.darkishGray).size(32).pad(8f);
                                t.button(new TextureRegionDrawable(to.uiIcon), Styles.emptyi, 32, () -> ui.content.show(to)).left().pad(2);
                            });
                        }
                    }
                }
            }
        }

        var GC = content.block(name("GC"));
        if(GC != null){
            setToTable(fromTables, Items.pyratite, GC);
        }
    }

    private static void getConsume(){
        for(Block b : content.blocks()){
            if(b != null && b.consumers != null && b.consumers.length > 0) {
                for (Consume c : b.consumers) {
                    boolean has = false;
                    if (c instanceof ConsumeItems ci) {
                        ItemStack[] is = ci.items;
                        if (is.length > 0) for (ItemStack ik : is) {
                            setToTable(toTables(ci, ik.item, b), ik.item, b);
                            has = true;
                        }
                    }
                    else if (c instanceof ConsumeItemFilter cf) {
                        for (Item it : Vars.content.items()) {
                            if (cf.filter.get(it)) {
                                setToTable(toTables(cf, it, b), it, b);
                                has = true;
                            }
                        }
                    }

                    if(has) break;
                }
                for(Consume c : b.consumers){
                    boolean has = false;
                    if (c instanceof ConsumeLiquid cl) {
                        setToTable(toTables(cl, cl.liquid, b), cl.liquid, b);
                        break;
                    }
                    else if (c instanceof ConsumeLiquids cls) {
                        LiquidStack[] ls = cls.liquids;
                        if (ls.length > 0) for (LiquidStack lk : ls) {
                            setToTable(toTables(cls, lk.liquid, b), lk.liquid, b);
                            has = true;
                        }
                    }
                    else if (c instanceof ConsumeLiquidFilter lf) {
                        for (Liquid lq : Vars.content.liquids()) {
                            if (lf.filter.get(lq)) {
                                setToTable(toTables(lf, lq, b), lq, b);
                                has = true;
                            }
                        }
                    }
                    if(has) break;
                }
            }
        }
    }

    public static void setToTable(ObjectMap<UnlockableContent, Table> tables, UnlockableContent content, Block b, String add){
        if(!tables.containsKey(content)){
            tables.put(content, new Table());
        }
        Table t = tables.get(content);
        t.row();
        t.table(tb -> {
            tb.table(Styles.grayPanel, ttb ->
                    ttb.button(new TextureRegionDrawable(b.uiIcon), Styles.emptyi, 28,() -> ui.content.show(b)).left().pad(8)
            ).left();
            tb.add(b.localizedName);
            if(add != null) tb.add(add);
        }).padTop(10).padBottom(5).left();
    }

    private static ObjectMap<UnlockableContent, Table> toTables(Consume c, UnlockableContent content, Block b){
        return isBoost(c) ? toTables_boost : isAmmo(content, b) ? toTables_ammo : toTables;
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
        t.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).marginRight(15).size(8).left();
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

    public static void setToTable(ObjectMap<UnlockableContent, Table> tables, UnlockableContent content, Block b){
        setToTable(tables, content, b, null);
    }

    private static boolean isBoost(Consume c){
        return c.booster;
    }

    private static boolean isAmmo(UnlockableContent content, Block b){
        return (content instanceof Item && b instanceof Turret) || (content instanceof Liquid && (b instanceof LiquidTurret || b instanceof ContinuousLiquidTurret));
    }

    public static String getOpt(Consume c, UnlockableContent content, Block b){
        String boost = isBoost(c) ? Core.bundle.get("eu-to-stat-boost") : null;
        String ammo = isAmmo(content, b) ? Core.bundle.get("eu-to-stat-ammo") : null;
        return boost != null ? ammo != null ? ammo + boost : boost : ammo;
    }
}
