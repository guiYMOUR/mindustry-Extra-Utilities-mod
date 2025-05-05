package ExtraUtilities.worlds.meta;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.ui.ItemDisplay;
import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.*;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

public class EUStatValues {
    public static StatValue stringBoosters(float reload, float maxUsed, float multiplier, boolean baseReload, Boolf<Liquid> filter, String key){
        return table -> {
            table.row();
            table.table(c -> {
                for(Liquid liquid : content.liquids()){
                    if(!filter.get(liquid)) continue;

                    c.image(liquid.uiIcon).size(3 * 8).scaling(Scaling.fit).padRight(4).right().top();
                    c.add(liquid.localizedName).padRight(10).left().top();
                    c.table(Tex.underline, bt -> {
                        bt.left().defaults().padRight(3).left();

                        float reloadRate = (baseReload ? 1f : 0f) + maxUsed * multiplier * liquid.heatCapacity;
                        float standardReload = baseReload ? reload : reload / (maxUsed * multiplier * 0.4f);
                        float result = standardReload / (reload / reloadRate);
                        bt.add(Core.bundle.format(key, Strings.autoFixed(result, 2)));
                    }).left().padTop(-9);
                    c.row();
                }
            }).colspan(table.getColumns());
            table.row();
        };
    }

    /** Anuke写的液体也能用捏 */
    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, boolean all){
        return ammo(map, 0, false, all);
    }

    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, boolean showUnit, boolean all){
        return ammo(map, 0, showUnit, all);
    }

    public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, int indent, boolean showUnit, boolean all){
        return table -> {

            table.row();

            Seq<T> orderedKeys = map.keys().toSeq();
            orderedKeys.sort();

            for(T t : orderedKeys) {
                boolean compact = t instanceof UnitType && !showUnit || indent > 0;
                if (!compact && !(t instanceof Turret)) {
                    table.table(item -> {
                        item.image(icon(t)).size(3 * 8).padRight(4).left().top().with(i -> StatValues.withTooltip(i, t, false));
                        item.add(t.localizedName).padRight(10).left().top();
                    }).left().pad(10);
                }
                table.row();
                table.table(tip -> {
                    if(all) tip.add(Core.bundle.get("stat.eu-multi-all")).left();
                    else tip.add(Core.bundle.get("stat.eu-multi-flow")).left();
                }).left().padBottom(5);
                table.row();
                for(BulletType type : map.get(t)){
                    if (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
                        ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), indent, false, all).display(table);
                        return;
                    }

                    //no point in displaying unit icon twice

                    table.table(bt -> {
                        bt.left().defaults().padRight(3).left();

                        if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
                            if (type.continuousDamage() > 0) {
                                bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                            } else {
                                bt.add(Core.bundle.format("bullet.damage", type.damage));
                            }
                        }

                        if (type.buildingDamageMultiplier != 1) {
                            sep(bt, Core.bundle.format("bullet.buildingdamage", (int) (type.buildingDamageMultiplier * 100)));
                        }

                        if (type.rangeChange != 0 && !compact) {
                            sep(bt, Core.bundle.format("bullet.range", (type.rangeChange > 0 ? "+" : "-") + Strings.autoFixed(type.rangeChange / tilesize, 1)));
                        }

                        if (type.splashDamage > 0) {
                            sep(bt, Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                        }

                        if (!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret) || ((Turret)t).displayAmmoMultiplier)) {
                            sep(bt, Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
                        }

                        if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
                            sep(bt, Core.bundle.format("bullet.reload", Strings.autoFixed(type.reloadMultiplier, 2)));
                        }

                        if (type.knockback > 0) {
                            sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                        }

                        if (type.healPercent > 0f) {
                            sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                        }

                        if (type.healAmount > 0f) {
                            sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                        }

                        if (type.pierce || type.pierceCap != -1) {
                            sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                        }

                        if (type.incendAmount > 0) {
                            sep(bt, "@bullet.incendiary");
                        }

                        if (type.homingPower > 0.01f) {
                            sep(bt, "@bullet.homing");
                        }

                        if (type.lightning > 0) {
                            sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                        }

                        if (type.pierceArmor) {
                            sep(bt, "@bullet.armorpierce");
                        }

                        if (type.status != StatusEffects.none) {
                            sep(bt, (type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + "[lightgray] ~ [stat]" + ((int) (type.statusDuration / 60f)) + "[lightgray] " + Core.bundle.get("unit.seconds"));
                        }

                        if(type.intervalBullet != null){
                            bt.row();

                            Table ic = new Table();
                            StatValues.ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
                            Collapser coll = new Collapser(ic, true);
                            coll.setDuration(0.1f);

                            bt.table(it -> {
                                it.left().defaults().left();

                                it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                                it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }

                        if(type.fragBullet != null){
                            bt.row();

                            Table fc = new Table();
                            StatValues.ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
                            Collapser coll = new Collapser(fc, true);
                            coll.setDuration(0.1f);

                            bt.table(ft -> {
                                ft.left().defaults().left();

                                ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                                ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }
                    }).padTop(compact ? 0 : -9).padLeft(indent * 8).left().get().background(compact ? null : Tex.underline);

                    table.row();
                }
            }
        };
    }
    private static void sep(Table table, String text){
        table.row();
        table.add(text);
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }

    public static StatValue colorString(Color color, CharSequence s){
        return table -> {
            table.row();
            table.table(c -> {
                c.image(((TextureRegionDrawable)Tex.whiteui).tint(color)).size(32).scaling(Scaling.fit).padRight(4).left().top();
                c.add(s).padRight(10).left().top();
            }).left();
            table.row();
        };
    }

    public static <T extends UnlockableContent> StatValue ammoString(ObjectMap<T, BulletType> map){
        return table -> {
            for(T i : map.keys()){
                table.row();
                table.table(c -> {
                    c.image(icon(i)).size(32).scaling(Scaling.fit).padRight(4).left().top();
                    c.add(Core.bundle.get("stat-" + i.name + ".ammo")).padRight(10).left().top();
                    c.background(Tex.underline);
                }).left();
                table.row();
            }
        };
    }

    public static StatValue itemRangeBoosters(String unit, float timePeriod, StatusEffect[] status, float rangeBoost, ItemStack[] items, boolean replace, Boolf<Item> filter){
        return table -> {
            table.row();
            table.table(c -> {
                for(Item item : content.items()){
                    if(!filter.get(item)) continue;

                    c.table(Styles.grayPanel, b -> {
                        for(ItemStack stack : items){
                            if(timePeriod < 0){
                                b.add(new ItemDisplay(stack.item, stack.amount, true)).pad(20f).left();
                            }else{
                                b.add(new ItemDisplay(stack.item, stack.amount, timePeriod, true)).pad(20f).left();
                            }
                            if(items.length > 1) b.row();
                        }

                        b.table(bt -> {
                            bt.left().defaults().left();
                            if(status.length > 0){
                                for(StatusEffect s : status){
                                    if(s == StatusEffects.none) continue;
                                    bt.row();
                                    bt.add(EUGet.selfStyleImageButton(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, () -> ui.content.show(s))).padTop(2f).padBottom(6f).size(42);
                                    //bt.button(new TextureRegionDrawable(s.uiIcon), () -> ui.content.show(s)).padTop(2f).padBottom(6f).size(50);
                                    bt.add(s.localizedName).padLeft(5);
                                }
                                if(replace){
                                    bt.row();
                                    bt.add(Core.bundle.get("statValue.replace"));
                                }
                            }
                            bt.row();
                            if(rangeBoost != 0) bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / tilesize, 2) + "[lightgray] " + StatUnit.blocks.localized()).row();
                        }).right().grow().pad(10f).padRight(15f);
                    }).growX().pad(5).padBottom(-5).row();
                }
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }
}