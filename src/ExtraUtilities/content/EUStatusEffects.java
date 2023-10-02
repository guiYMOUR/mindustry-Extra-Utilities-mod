package ExtraUtilities.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectSet;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

public class EUStatusEffects {

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
}
