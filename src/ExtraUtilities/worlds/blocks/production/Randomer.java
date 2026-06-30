//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.ExtraUtilitiesMod;
import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUStatusEffects;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class Randomer extends OmniSource {
    public int range = 28;
    public float healP = 0.002f;
    public float healA = 15;
    public float speedBoost = 3;
    public float unitDamage = 120;

    public Randomer(String name) {
        super(name);
    }

    public void setStats() {
        super.setStats();
        stats.add(Stat.range, (float)range, StatUnit.blocks);
    }

    public void setBars() {
        super.setBars();
        addBar("boost", (entity) -> new Bar(() -> Core.bundle.format("bar.boost", new Object[]{Mathf.round(Math.max(speedBoost * 100 - 100, 0))}), () -> Pal.accent, () -> 1));
    }

    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        if (Vars.world.tile(x, y) != null && !canPlaceOn(Vars.world.tile(x, y), Vars.player.team(), rotation)) {
            drawPlaceText(Core.bundle.get("bar.extra-utilities-hardOnly"), x, y, valid);
        }

        x *= 8;
        y *= 8;
        x = (int)((float)x + offset);
        y = (int)((float)y + offset);
        Drawf.dashSquare(EUGet.MIKU, (float)x, (float)y, (float)(range * 8));
        Vars.indexer.eachBlock(Vars.player.team(), Tmp.r1.setCentered((float)x, (float)y, (float)(range * 8)), (b) -> true, (t) -> Drawf.selected(t, Tmp.c1.set(EUGet.MIKU).a(Mathf.absin(4, 1))));
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return super.canPlaceOn(tile, team, rotation) && ExtraUtilitiesMod.hardMod;
    }

    public class RandomerBuild extends OmniSource.FillerBuild {
        public Rect br = new Rect();
        public Rect ur = new Rect();
        public float purifiedTimer = 0;
        public boolean enyIn = false;
        public float warmup = 0;
        float fin = 1;

        public void updateTile() {
            Vars.indexer.eachBlock(team, br.setCentered(x, y, (float)(range * 8)), (b) -> true, (b) -> {
                if (b.block != null) {
                    if (!b.block.canOverdrive) {
                        b.block.canOverdrive = true;
                    }

                    b.applyBoost(speedBoost, 45);
                }

                if (b.damaged()) {
                    b.heal(healA * Time.delta + b.maxHealth * healP * Time.delta);
                }

            });
            enyIn = false;
            Units.nearby(ur.setCentered(x, y, (float)(range * 8)), (u) -> {
                if (u.team == team) {
                    u.apply(EUStatusEffects.speedUp, 10);
                    u.apply(EUStatusEffects.fireDamageUp, 10);
                    u.apply(EUStatusEffects.defenseUp, 10);
                    if (u.damaged()) {
                        u.heal(healA * Time.delta + u.maxHealth * healP * Time.delta);
                    }

                    purifiedTimer += Time.delta;
                    if (purifiedTimer > 60) {
                        for(StatusEffect s : Vars.content.statusEffects()) {
                            if (EUGet.isDeBuff(s) && u.hasEffect(s)) {
                                u.unapply(s);
                            }
                        }

                        purifiedTimer = 0;
                    }
                } else if (u.targetable(team)) {
                    float halfSize = (float)range / 2 * 8;
                    float hitSize = u.hitSize / 2;
                    float unitAng = angleTo(u);
                    float rx = EUGet.squarePointX(x, halfSize, unitAng);
                    float ry = EUGet.squarePointY(y, halfSize, unitAng);
                    float dstRaw = dst(rx, ry) + hitSize;
                    float overlapDst = dstRaw - u.dst(this);
                    if (overlapDst > 0) {
                        u.vel.setZero();
                        u.move(Tmp.v1.set(u).sub(this).setLength(overlapDst + 0.01f));
                        if (Mathf.chanceDelta((double)(0.12f * Time.delta))) {
                            Fx.circleColorSpark.at(u.x, u.y, team.color);
                        }

                        u.damagePierce(unitDamage / 60 * Time.delta);
                        enyIn = true;
                    }
                }

            });
            Groups.bullet.intersect(x - (float)(range * 8) / 2, y - (float)(range * 8) / 2, (float)(range * 8), (float)(range * 8), (bullet) -> {
                if (bullet.team != team) {
                    if (bullet.type != null && bullet.type.reflectable && Mathf.chanceDelta(0.2)) {
                        bullet.team(team);
                        bullet.owner(this);
                        bullet.rotation(bullet.rotation() + 180);
                        ++bullet.time;
                    }

                    if (bullet.team != team) {
                        if (Mathf.chanceDelta(0.2)) {
                            Entityc owner = bullet.owner;
                            if (owner instanceof Healthc hc) {
                                hc.damagePierce(bullet.damage);
                            }
                        }

                        bullet.remove();
                    }

                    EUFx.shieldDefense.at(bullet.x, bullet.y, bullet.rotation(), team.color);
                    enyIn = true;
                }

                if (bullet.team == team && bullet.type != null) {
                    float damage = Math.max(bullet.type.damage, bullet.type.splashDamage);
                    bullet.damage += damage / 60 * Time.delta;
                }

            });
            if (enyIn) {
                warmup = Mathf.lerpDelta(warmup, 1, 0.1f);
            } else {
                warmup = Mathf.lerpDelta(warmup, 0, 0.1f);
            }

            super.updateTile();
        }

        public void draw() {
            float z = Draw.z();
            Draw.z(110);
            Lines.stroke(warmup * 4, team.color);
            Lines.square(x, y, (float)(range * 8) / 2);
            fin -= Time.delta * 0.06f;
            if (fin < 0) {
                fin = 1;
            }

            for(int i = 0; i < 4; ++i) {
                float lin = (fin * 10 + (float)i) % 10 / 10;
                Lines.stroke(warmup * 4 * (1 - lin));
                Lines.square(x, y, (float)(range * 8) * Mathf.clamp(fin, 0.4f, 1) / 2);
            }

            Draw.z(z);
            Draw.color();
            super.draw();
        }

        public void drawSelect() {
            super.drawSelect();
            Drawf.dashSquare(EUGet.MIKU, x, y, (float)(range * 8));
            Vars.indexer.eachBlock(team, br.setCentered(x, y, (float)(range * 8)), (b) -> true, (b) -> Drawf.selected(b, Tmp.c1.set(EUGet.MIKU).a(Mathf.absin(4, 1))));
        }

        public void write(Writes write) {
            super.write(write);
            write.f(purifiedTimer);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            purifiedTimer = read.f();
        }
    }
}
