package ExtraUtilities.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.content.Fx.rand;

public class EUStatusEffects {
    public static Seq<StatusEffect> elements = new Seq<>();


    public static StatusEffect poison = new StatusEffect("poison"){{
        transitionDamage = 156;
        color = Color.valueOf("#cbd97f");
        speedMultiplier = 0.4f;
        damage = 2f;
        healthMultiplier = 0.7f;
        effect = Fx.oily;
        init(() -> {
            affinity(StatusEffects.sapped, (unit, result, time) -> {
                unit.damagePierce(transitionDamage);
                Fx.oily.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                result.set(poison, Math.min(time + result.time, 200f));
            });
        });
    }};

    public static StatusEffect speedUp = new StatusEffect("speedUp"){{
        color = Color.valueOf("ea8878");
        buildSpeedMultiplier = 2;
        speedMultiplier = 1.3f;
        reloadMultiplier = 2;
        damage = -0.2f;
        effectChance = 0.07f;
        effect = Fx.overclocked;
    }};

    public static StatusEffect speedDown = new StatusEffect("speedDown"){{
        color = Color.valueOf("8b9bb4");
        speedMultiplier = 0.4f;
        reloadMultiplier = 0.5f;
        damage = 15/60f;
        effectChance = 0.07f;
        effect = Fx.overclocked;
    }};

    public static StatusEffect regenBoost = new StatusEffect("regenBoost"){{
        color = Pal.heal;
        reloadMultiplier = 1.2f;
        damage = -80/60f;
        healthMultiplier = 1.5f;
        effect = Fx.none;
    }};

    public static StatusEffect fireSpeedUp = new StatusEffect("fireSpeedUp"){{
        color = Color.valueOf("fa1111");
        reloadMultiplier = 1.5f;
        effect = Fx.none;
    }};

    public static StatusEffect fireDamageUp = new StatusEffect("fireDamageUp"){{
        color = Color.valueOf("fa1112");
        damageMultiplier = 2;
        effect = Fx.none;
    }};

    public static StatusEffect defenseUp = new StatusEffect("defenseUp"){{
        color = Color.valueOf("fa1113");
        healthMultiplier = 1.3f;
        effect = Fx.none;
    }};

    public static StatusEffect EUUnmoving = new StatusEffect("unmoving"){{
        color = Pal.gray;
        speedMultiplier = 0f;
    }};

    public static StatusEffect EUDisarmed = new StatusEffect("disarmed"){{
        color = Color.valueOf("e9ead3");
        disarm = true;
    }};

    public static StatusEffect awsl = new StatusEffect("awsl"){{
        disarm = true;
        speedMultiplier = 0;
        applyEffect = new Effect(60, e -> {
            for(int i = 0; i < 10; i++){
                float dx = EUGet.dx(e.x, 6, e.time * 12 + i * 8),
                        dy = EUGet.dy(e.y, 6, e.time * 12 + i * 8);
                int finalI = i;
                e.scaled(60, b -> {
                    Draw.color(Pal.accent);
                    Fill.circle(dx, dy, 2f * finalI /10f + 0.2f);
                });
            }
        });
    }};

    public static StatusEffect flamePoint = new StatusEffect("flame-point"){{
        damage = 0.2f;
        color = Pal.lightFlame;
        parentizeEffect = true;
        effect = new Effect(36, e -> {
            if(!(e.data instanceof Unit unit)) return;
            Lines.stroke(2 * e.foutpow(), Items.blastCompound.color);
            for(int i = 0; i < 3; i++){
                float a = 360/3f * i + e.time * 6;
                float x = EUGet.dx(e.x, Math.max(6, unit.hitSize/2f), a), y = EUGet.dy(e.y, Math.max(6, unit.hitSize/2f), a);
                Lines.lineAngle(x, y, a - 120, Math.max(3, unit.hitSize/4f) * e.foutpow());
                Lines.lineAngle(x, y, a + 120, Math.max(3, unit.hitSize/4f) * e.foutpow());
            }
        });
        speedMultiplier = 0.9f;
    }

        @Override
        public void update(Unit unit, StatusEntry entry) {
            if(damage > 0){
                unit.damageContinuousPierce(damage);
            }else if(damage < 0){
                unit.heal(-1f * damage * Time.delta);
            }

            if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
                effect.at(unit.x, unit.y, 0, color, parentizeEffect ? unit : null);
            }
        }
    };

    public static StatusEffect starFlame = new StatusEffect("star-flame"){{
        damage = 1;
        color = Pal.thoriumPink;
        effectChance = 0.03f;
        effect = new Effect(24, e -> {
            Angles.randLenVectors(e.id, 3, 13 * e.finpow(), e.rotation, 180, (x, y) -> {
                Draw.color(Tmp.c4.set(EUGet.rainBowRed).shiftHue(Mathf.randomSeed(e.id, 360) + x * x + y * y + e.time * 2));
                Drawf.tri(e.x + x, e.y + y, 3 * e.foutpow(), 7, e.rotation + 90);
                Drawf.tri(e.x + x, e.y + y, 3 * e.foutpow(), 7, e.rotation - 90);
                Drawf.tri(e.x + x, e.y + y, 3 * e.foutpow(), 5, e.rotation);
                Drawf.tri(e.x + x, e.y + y, 3 * e.foutpow(), 5, e.rotation + 180);
            });
        });
    }};


    public static StatusEffect breakage = new StatusEffect("breakage"){{
        damage = -1;
        parentizeApplyEffect = true;
        applyColor = Pal.techBlue;
        applyEffect = new Effect(45, e -> {
            if(!(e.data instanceof Unit u)) return;
            float size = u.hitSize * 2;
            rand.setSeed(e.id);
            float pin = (1 - e.foutpow());
            Lines.stroke(size/24 * e.foutpow(), e.color);
            Lines.circle(e.x, e.y, size * pin);
            for(int i = 0; i < 5; i++){
                float a = rand.random(180);
                float lx = EUGet.dx(e.x, size * pin, a);
                float ly = EUGet.dy(e.y, size * pin, a);
                Drawf.tri(lx, ly, size/32 * e.foutpow(), (size + rand.random(-size, size)) * e.foutpow(), a + 180);
            }
            for(int i = 0; i < 5; i++){
                float a = 180 + rand.random(180);
                float lx = EUGet.dx(e.x, size * pin, a);
                float ly = EUGet.dy(e.y, size * pin, a);
                Drawf.tri(lx, ly, size/32 * e.foutpow(), (size + rand.random(-size, size)) * e.foutpow(), a + 180);
            }
        });
    }

        @Override
        public void setStats() {
            super.setStats();
            stats.remove(Stat.healing);
            stats.addMultModifier(Stat.damageMultiplier, 0.8f);
            stats.addMultModifier(Stat.speedMultiplier, 0.4f);
            stats.addMultModifier(Stat.reloadMultiplier, 0.5f);
            stats.add(Stat.damage, 60f, StatUnit.perSecond);
        }

        @Override
        public void applied(Unit unit, float time, boolean extend) {
            super.applied(unit, time, extend);

            unit.health -= 100;
        }

        @Override
        public void update(Unit unit, StatusEntry entry) {

            unit.damageMultiplier *= 0.8f;
            unit.speedMultiplier *= 0.4f;
            unit.reloadMultiplier *= 0.5f;

            unit.health -= Time.delta;

            if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
                Tmp.v1.rnd(Mathf.range(unit.type.hitSize/2f));
                effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, color, parentizeEffect ? unit : null);
            }
        }
    };

    public static void load(){
        elements.addAll(breakage);
    }
}
