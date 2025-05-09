package ExtraUtilities.worlds.entity.ability;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static mindustry.Vars.indexer;
import static mindustry.Vars.tilesize;

public class BoostAbility extends Ability {
    public float boost = 3f;
    public float boostTime = 90;
    public float slow = 0.5f;
    public float slowTime = 120;
    public float range = 20 * 8;
    public float reload = 300;
    public float deepSeek = 0.7f;
    public Color color = Pal.heal;

    private float timer = 0;
    private float scp = 0;
    private final Seq<Building> builds = new Seq<>();
    private final Seq<Building> ems = new Seq<>();

    @Override
    public void update(Unit unit) {
        if(scp >= range) {
            timer = 0;
            scp = 0;
            builds.clear();
            ems.clear();
        }

        if(timer >= reload){
            scp += Time.delta * deepSeek;

            indexer.eachBlock(unit, scp, b -> b.block != null && b.block.canOverdrive && !builds.contains(b), b -> {
                b.applyBoost(boost, boostTime);
                Fx.healBlockFull.at(b.x, b.y, 0, Pal.heal, b.block);
                builds.add(b);
            });
            indexer.eachBlock(null, unit.x, unit.y, scp, e -> e.team != unit.team && e.block != null && !ems.contains(e), e -> {
                e.applySlowdown(slow, slowTime);
                Fx.healBlockFull.at(e.x, e.y, 0, Pal.sap, e.block);
                ems.add(e);
            });
        } else {
            timer += Time.delta;
        }
    }

    @Override
    public void draw(Unit unit) {
        float z = Draw.z();
        Draw.z(Layer.blockUnder);
        Draw.color(Tmp.c1.set(color).a(0.2f));
        float fin = Interp.smoother.apply(scp/range);
        float fout = 1 - fin;
        if(fin < 0.5f){
            Lines.stroke(range * fin * 1.3f);
        } else {
            Lines.stroke(range * fout * 1.3f);
        }
        Lines.circle(unit.x, unit.y, range * fin);

        Draw.z(z);
        Draw.reset();
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.extra-utilities-boost-abi");
    }

    @Override
    public void addStats(Table t){
        super.addStats(t);
        t.row();
        t.add(Core.bundle.format("stat.eu-boost-abi-reload", Strings.autoFixed(reload / 60, 2)));
        t.row();
        t.add(Core.bundle.format("stat.eu-boost-abi-range", Strings.autoFixed(range / tilesize, 2)));
        t.row();
        t.add(Core.bundle.format("stat.eu-boost-abi-boost", Strings.autoFixed(boostTime/60, 2), Strings.autoFixed((boost - 1) * 100, 2)));
        t.row();
        t.add(Core.bundle.format("stat.eu-boost-abi-slow", Strings.autoFixed(slowTime/60, 2), Strings.autoFixed((1 - slow) * 100, 2)));
        t.row();
    }
}
