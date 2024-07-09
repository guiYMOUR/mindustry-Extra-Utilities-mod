package ExtraUtilities.worlds.entity.ability;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUStatusEffects;
import ExtraUtilities.content.EUUnitTypes;
import ExtraUtilities.worlds.drawer.DrawFunc;
import ExtraUtilities.worlds.entity.unit.bossEntity;
import ExtraUtilities.worlds.entity.unit.bossType;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.blocks.defense.Wall;

import static ExtraUtilities.ExtraUtilitiesMod.name;

//Of course, these can all be written in the bossEntity class, but this Ability class was written before the bossEntity class, so...
public class bossUnitAbi extends Ability{
    public int maxPick = 20;
    public float pickRange = 60 * 8;

    private boolean c1 = false;
    private boolean d1 = false, s1 = false;
    private float time = 0, uee = 0;
    private boolean outq = false;
    private float lastHealth = 0;
    private float healTimer = 0;
    public float maxPerSec = 800;

    public bossUnitAbi(){ }

    public void reset(){
        c1 = d1 = s1 = outq = false;
    }

    public boolean isD1() {
        return d1;
    }

    public void setD1(boolean d1) {
        this.d1 = d1;
    }

    public boolean isS1() {
        return s1;
    }

    public void setS1(boolean s1) {
        this.s1 = s1;
    }

    private final Effect espEffect = EUFx.edessp(20);
    private final Object[] objects = new Object[4];

    @Override
    public void update(Unit unit) {
        if(!(unit instanceof bossEntity bt)) return;

        if(!outq && !Vars.net.client()){
            unit.heal(unit.maxHealth);
            outq = true;
        }

        if(!Vars.state.isPaused()) {
            time += Time.delta;
            healTimer -= Time.delta;
        }

        if(unit.health < lastHealth - maxPerSec){
            unit.health = lastHealth - maxPerSec;
        }
        if(healTimer <= 0){
            lastHealth = unit.health;
            healTimer = 60;
        }
        if(unit.health < lastHealth - unit.maxHealth/2){
            unit.health = lastHealth;
            unit.dead = false;
            d1 = s1 = false;
        }

        if(unit.health < unit.maxHealth/1.5f && !c1){
            Units.nearbyBuildings(unit.x, unit.y, pickRange, b -> {
                if(b.team != unit.team && b instanceof Wall.WallBuild) {
                    bt.bs.addUnique(b);
                }
            });

            bt.bs.sort(building -> building.dst(unit));

            if(bt.bs.size > 0){
                for(Building b : bt.bs){
                    if(b != null && b.block instanceof Wall w && bt.pb.size <= maxPick){
                        bossType.pickedBlock p = new bossType.pickedBlock();
                        p.set(w.health, w.size, w.name, unit.team);
                        p.setPos(b.x, b.y);
                        bt.pb.addUnique(p);

                        if(!Vars.net.client()) Call.removeTile(b.tile);
                    }
                }
            }
            bt.bs.clear();
            c1 = true;
        }

        bt.pb.removeAll(p -> p == null || p.dead || p.health <= 0);
        for (int i = 0; i < bt.pb.size; i++){
            bossType.pickedBlock p = bt.pb.get(i);
            if(p != null){
                p.update();
                if(!p.within(unit, pickRange/2 + 24)) {
                    p.moveTo(unit);
                } else {
                    float angle = i * (120f / maxPick) + time;
                    p.rotateTo(unit, pickRange / 2, angle);
                }
            }
        }

        if(d1){
            unit.dead = false;
            unit.elevation = 1;

            unit.heal(unit.maxHealth/(60 * 6 * 10) * Time.delta);
            unit.apply(EUStatusEffects.EUUnmoving, 10);
            unit.apply(EUStatusEffects.EUDisarmed, 10);
            if(unit.health >= unit.maxHealth/2){
                EUFx.gone(Color.valueOf("87CEEB"), pickRange, 30);

                Units.nearbyEnemies(unit.team, unit.x, unit.y, pickRange, u -> {
                    if (!u.dead && !Vars.net.client() && u.type != null && u.type.killable && u.targetable(unit.team)) {
                        Call.unitDeath(u.id);
                        uee += 0.5f;
                    }
                });

                for(int i = 0; i < Math.ceil(uee) + 1; i++){
                    if(!Vars.net.client()){
                        UnitType ut = EUUnitTypes.havoc;
                        Unit u = ut.create(unit.team);
                        u.set(unit.x + Angles.trnsx(unit.rotation - 90, Mathf.random(-pickRange/1.5f, pickRange/1.5f)), unit.y + Angles.trnsx(unit.rotation - 90, Mathf.random(-pickRange/2)));
                        u.rotation(unit.rotation);
                        EUFx.EUUtSp.at(u.x, u.y, u.rotation - 90, ut);
                        u.add();
                    }
                }
                s1 = true;
                c1 = false;
                d1 = false;
            }
        }
        if(s1 && !unit.dead){
            unit.apply(EUStatusEffects.defenseUp, 10);
            unit.apply(EUStatusEffects.fireDamageUp, 10);
            unit.apply(EUStatusEffects.fireSpeedUp, 10);
            unit.heal((200/60f) * Time.delta);
        }
    }

    public boolean targetable() {
        return (d1 && ! s1);
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);
        if(!(unit instanceof bossEntity bt)) return;
        Draw.z(Layer.blockOver);
        if (bt.pb.size > 0) {
            for (bossType.pickedBlock p : bt.pb) {
                p.draw();
            }
        }
        Draw.z(Layer.effect);
        Lines.stroke(3, Color.valueOf("87CEEB"));
        Lines.circle(unit.x, unit.y, pickRange);
        Lines.stroke(2, Color.valueOf("6D90BC"));
        Lines.arc(unit.x, unit.y, pickRange/2, unit.health/unit.maxHealth);

        if(d1 && !s1){
            float fin = unit.health/(unit.maxHealth/2);
            float fout = 1 - fin;
            if(unit.type != null && bt.timer.get(10 + 30 * fout) && unit.health < (unit.maxHealth/2 - 800)){
                objects[0] = Core.atlas.find(unit.type.name);
                objects[1] = pickRange/1.5f;
                objects[2] = unit.rotation - 90f;
                objects[3] = 270f;
                espEffect.at(unit.x, unit.y, Mathf.random(360), objects);
            }

            float lfin = Mathf.curve(fin, 0, 0.1f);
            for(int i = 0; i < 2; i++){
                float angle = i* 360f / 2 + 90;
                Drawf.tri(unit.x + Angles.trnsx(angle, 25 * 8), unit.y + Angles.trnsy(angle, 25 * 8), 15, pickRange * lfin, angle);
            }
            Lines.stroke(5 * lfin);
            for(int i = 0; i < 12; i++){
                float angle = i* 360f / 12;
                float lx = unit.x + Angles.trnsx(angle, pickRange), ly = unit.y + Angles.trnsy(angle, pickRange);
                Lines.lineAngle(lx, ly, unit.angleTo(lx, ly) + 180, pickRange/8);
            }

            Fill.circle(unit.x, unit.y, 10);

            Lines.stroke(7, Color.valueOf("87CEEB"));
            float aag = 360 * fout + 90;
            Lines.lineAngle(unit.x, unit.y, aag, pickRange);

            float ax = unit.x + Angles.trnsx(aag, pickRange - 24), ay = unit.y + Angles.trnsy(aag, pickRange - 24);
            Draw.rect(Core.atlas.find(name("aim-shoot")), ax, ay, 80 * lfin, 90, aag - 90);

            DrawFunc.circlePercent(unit.x, unit.y, pickRange + 16, fout, 90);

            Units.nearbyEnemies(unit.team, unit.x, unit.y, pickRange, u -> {
                if (!u.dead && !Vars.net.client() && u.type != null && u.type.killable && u.targetable(unit.team)) {
                    Lines.stroke(4, Pal.remove);
                    for(int i = 0; i < 3; i++){
                        float s = u.type.hitSize/1.1f + Mathf.absin(unit.health/10, 10, u.type.hitSize/3);
                        float ang = 30 + 120 * i;
                        float cx = EUGet.dx(u.x, s, ang);
                        float cy = EUGet.dy(u.y, s, ang);
                        for(int a : Mathf.zeroOne){
                            Lines.lineAngle(cx, cy, ang - 90 + 180 * a, s/1.3f);
                        }
                    }
                    Lines.square(u.x, u.y, u.type.hitSize/2, unit.health/10);
                }
            });
        }

        Draw.reset();
    }

    @Override
    public String localized() {
        return "[red]BOSS[]";
    }
}
