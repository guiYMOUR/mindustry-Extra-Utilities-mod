package ExtraUtilities.worlds.entity.ability;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.worlds.entity.bullet.ElectricStorm;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;

public class BatteryAbility extends Ability {
    public float capacity, shieldRange, range, px, py;
    public Effect abEff = EUFx.shieldDefense;

    public BatteryAbility(float capacity, float shieldRange, float range, float px, float py){
        this.capacity = capacity;
        this.shieldRange = shieldRange;
        this.range = range;
        this.px = px;
        this.py = py;
    }

    public static Effect absorb;
    private static Unit paramUnit;
    public static float rangeS;
    
    private static final Cons<Bullet> cons = b -> {
        if(b.team != paramUnit.team && b.type.absorbable && Intersector.isInsideHexagon(paramUnit.x, paramUnit.y, rangeS * 2, b.getX(), b.getY()) && paramUnit.shield > 0){
            b.absorb();
            absorb.at(b.getX(), b.getY(), Pal.heal);
            paramUnit.shield = Math.max(paramUnit.shield - b.damage, 0);
        }
    };

    Building target = null;
    float timerRetarget = 0;
    float amount = 0;

    protected void setupColor(float satisfaction){
        Draw.color(Color.white, Pal.powerLight, (1 - satisfaction) * 0.86f + Mathf.absin(3, 0.1f));
        Draw.alpha(Renderer.laserOpacity);
    }

    protected void findTarget(Unit unit){
        if(target != null) return;
        Vars.indexer.allBuildings(unit.x, unit.y, range, other -> {
            if(other.block != null && other.team == unit.team && other.block instanceof PowerNode){
                target = other;
            }
        });
    }

    protected void updateTarget(Unit unit){
        timerRetarget += Time.delta;
        if(timerRetarget > 5){
            target = null;
            findTarget(unit);
            timerRetarget = 0;
        }
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.extra-utilities-BatteryAbility", capacity, range/8);
    }

    @Override
    public void draw(Unit unit) {
        float x = unit.x + Angles.trnsx(unit.rotation, py, px);
        float y = unit.y + Angles.trnsy(unit.rotation, py, px);
        if(unit.shield > 0){
            Draw.color(Pal.heal);
            Draw.z(Layer.effect);
            Lines.stroke(1.5f);
            Lines.poly(unit.x, unit.y, 6, shieldRange);
        }
        if(target == null || target.block == null) return;
        if(Mathf.zero(Renderer.laserOpacity)) return;
        Draw.z(Layer.power);
        setupColor(target.power.graph.getSatisfaction());
        ((PowerNode)target.block).drawLaser(x, y, target.x, target.y, 2, target.block.size);
    }

    @Override
    public void update(Unit unit) {
        paramUnit = unit;
        rangeS = shieldRange;
        absorb = abEff;
        updateTarget(unit);
        Groups.bullet.intersect(unit.x - shieldRange, unit.y - shieldRange, shieldRange * 2, shieldRange * 2, cons);
        amount = unit.shield * 10;
        if(Vars.state.rules.unitAmmo && amount > 0){
            Units.nearby(unit.team, unit.x, unit.y, range, other -> {
                if(other.type.ammoType instanceof PowerAmmoType){
                    float powerPerAmmo = ((PowerAmmoType)other.type.ammoType).totalPower / other.type.ammoCapacity;
                    float ammoRequired = other.type.ammoCapacity - other.ammo;
                    float powerRequired = ammoRequired * powerPerAmmo;
                    float powerTaken = Math.min(amount, powerRequired);
                    if(powerTaken > 1){
                        unit.shield -= powerTaken / 10;
                        other.ammo += powerTaken / powerPerAmmo;
                        Fx.itemTransfer.at(unit.x, unit.y, Math.max(powerTaken / 100, 1), Pal.power, other);
                    }
                }
            });
        }
        if(target == null || target.block == null) return;
        PowerGraph g = target.power.graph;
        if(g.getPowerBalance() > 0) amount = Math.min(amount + (g.getLastPowerProduced()) * Time.delta, capacity);
        unit.shield = amount / 10;
    }

    @Override
    public void displayBars(Unit unit, Table bars) {
        bars.add(new Bar(Core.bundle.format("bar.extra-utilities-unitBattery"), Pal.power, () -> amount / capacity)).row();
    }

    @Override
    public void death(Unit unit) {
        new ElectricStorm(capacity/100 + amount/100, Pal.heal, 20 + (int)amount/1000){{
            lifetime = 300;
            splashDamageRadius = 20 * 8;
            despawnEffect = hitEffect = EUFx.ElectricExp(60, 15, splashDamageRadius);
        }}.create(unit, unit.x, unit.y, 0);
    }
}
