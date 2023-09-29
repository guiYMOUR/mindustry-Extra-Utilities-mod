package ExtraUtilities.worlds.entity.ability;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static mindustry.Vars.*;

public class RepairField extends Ability {
    public float range = 200, reload = 180, rotSpeed = 0.9f;
    public float unitHeal = 100, blockHealPercent = 10;
    public Color color = Pal.heal;
    public Effect healFx = new Effect(50, e -> {
        Draw.color(Pal.heal);
        Angles.randLenVectors(e.id, 3, 8 * e.finpow(), e.rotation, 360, (x, y) -> {
            Fill.square(e.x + x, e.y + y, 6 * e.foutpow());
        });
    });
    public float fxChance = 0.05f;
    public StatusEffect status = StatusEffects.none;


    protected float timer = 0;
    protected boolean healing, wasHeal;
    protected float warmup = 0;
    protected float remoteTime;

    @Override
    public String localized() {
        return Core.bundle.format("ability." + name("RepairFieldAbility"));
    }

    @Override
    public void update(Unit unit) {
        healing = false;
        Units.nearby(unit.team, unit.x, unit.y, range, u -> {
            if(u != unit && status != StatusEffects.none) u.apply(status, 60);
            if (unitHeal > 0 && !u.dead && u.health < u.maxHealth) {
                u.heal((unitHeal / 60f) * Time.delta);
                if(Mathf.chance(fxChance)){
                    healFx.at(u.x + Mathf.random(-u.hitSize/2, u.hitSize/2), u.y + Mathf.random(-u.hitSize/2, u.hitSize/2));
                }

                healing = true;
            }
        });


        //@guiY, why don't you write a scanning repair method?
        //I tried, but the effect was not satisfactory...
        //TODO maybe can do better?
        indexer.eachBlock(unit, range, b -> b.damaged() && !b.isHealSuppressed(), other -> healing = true);
        timer += Time.delta;
        if(timer >= reload){
            timer -= reload;
            indexer.eachBlock(unit, range, b -> b.damaged() && !b.isHealSuppressed(), other -> {
                other.heal(other.maxHealth() * blockHealPercent / 100f);
                other.recentlyHealed();
                Fx.healBlockFull.at(other.x, other.y, other.block.size, color, other.block);
            });
        }
        wasHeal = healing;
    }

    @Override
    public void draw(Unit unit) {
        if(!state.isPaused()) {
            if (wasHeal) {
                remoteTime = (remoteTime + (Time.delta * rotSpeed)) % 360;
                warmup = Mathf.lerpDelta(warmup, 1, 0.02f);
            } else {
                warmup = Mathf.lerpDelta(warmup, 0, 0.06f);
            }
        }
        Lines.stroke(2 * warmup, color);
        Lines.circle(unit.x, unit.y, range);
        Draw.alpha(0.05f * warmup);
        Fill.circle(unit.x, unit.y, range);
        float ag = 20 * warmup;
        for(int i = 0; i < ag; i++){
            Draw.alpha((1 - i/ag) * warmup);
            float r = remoteTime - i + 0.1f;
            float tx = unit.x + Angles.trnsx(r, range), ty = unit.y + Angles.trnsy(r, range);
            Drawf.tri(tx, ty, 2 * range * (float) Math.tan(Math.PI/360), range, r - 180);
        }

        Draw.reset();
    }

    @Override
    public void addStats(Table t) {
        t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(unitHeal, 2) + StatUnit.perSecond.localized());
        t.row();
        t.add("[lightgray]" + Stat.repairTime.localized() + ": [white]" + Strings.autoFixed(100 / blockHealPercent * reload/60, 2) + StatUnit.seconds.localized());
        t.row();
        t.add("[lightgray]" + Stat.range.localized() + ": [white]" + Strings.autoFixed(range / 8, 2) + " " + StatUnit.blocks.localized());
        t.row();
        if(status == StatusEffects.none) return;
        t.add("[lightgray]status: ").row();
        t.button(new TextureRegionDrawable(status.uiIcon), () -> {
            ui.content.show(status);
        }).size(50).row();
        t.add("[white]" + status.localizedName);
        t.row();
    }
}
