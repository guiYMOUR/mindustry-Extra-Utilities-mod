package ExtraUtilities.worlds.entity.ability;

import ExtraUtilities.content.EUStatusEffects;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static mindustry.Vars.*;

public class TerritoryFieldAbility extends Ability {
    public float range;
    public float healAm;
    public float damageAm;

    public float reload = 60f * 1.5f;

    public boolean active = true;
    public boolean open = false;

    public float applyParticleChance = 13f;

    protected float timer;

    public TerritoryFieldAbility(float range, float healAm, float damageAm){
        this.range = range;
        this.healAm = healAm;
        this.damageAm = damageAm;
    }

    @Override
    public String localized() {
        String re = Core.bundle.format("ability." + name("TerritoryFieldAbility"), range/tilesize);
        if(healAm > 0) re += Core.bundle.format("ability." + name("TerritoryFieldAbilityHeal"), healAm);
        if(damageAm > 0) re += Core.bundle.format("ability." + name("TerritoryFieldAbilityDamage"), damageAm);
        if(active) re += Core.bundle.get("ability.extra-utilities-TerritoryFieldAbilitySuppression");
        return re;
    }

    @Override
    public void update(Unit unit) {
        Units.nearby(unit.team, unit.x, unit.y, range, u -> {
            if(u != unit) {
                u.apply(EUStatusEffects.speedUp, 60);
                if (healAm > 0 && !u.dead && u.health < u.maxHealth) u.heal((healAm / 60f) * Time.delta);
            }
        });
        Units.nearbyEnemies(unit.team, unit.x, unit.y, range, u -> {
            u.apply(EUStatusEffects.speedDown, 60);
            if(damageAm > 0 && !u.dead && u.targetable(unit.team)) u.damage((damageAm / 60f) * Time.delta);
        });

        if(open){
            Units.nearbyEnemies(unit.team, unit.x, unit.y, range * 2, u -> {
                if(!u.dead && u.type != null && (u.health > unit.maxHealth * 2 || u.type.armor >= unit.type.armor * 2)) {
                    u.health -= u.health;
                    u.remove();
                }
            });
        }

        if(!active) return;

        if((timer += Time.delta) >= reload){
            Damage.applySuppression(unit.team, unit.x, unit.y, range, reload, reload, applyParticleChance, unit);
            timer = 0f;
        }
    }

    @Override
    public void draw(Unit unit) {
        Lines.stroke(2, unit.team.color);
        Lines.circle(unit.x, unit.y, range);
        Draw.alpha(0.08f);
        Fill.circle(unit.x, unit.y, range);
    }
}
