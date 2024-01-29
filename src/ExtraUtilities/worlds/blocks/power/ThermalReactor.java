package ExtraUtilities.worlds.blocks.power;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.draw.*;

public class ThermalReactor extends ThermalGenerator {
    public float addSpeed = 0.0003f;
    private final String[] load = new String[]{"loading", "loading .", "loading ..", "loading ..."};
    public float workdamage = 6;

    public ThermalReactor(String name) {
        super(name);
        drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma(), new DrawDefault());
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("load", (ThermalReactorBuild entity) ->
                new Bar(() ->
                        entity.warmup() > 0.999 ? "complete" : load[(int) (Time.time/(load.length*10)) % load.length],
                        () -> Color.valueOf("ffd06d"),
                        entity::warmup
                )
        );
    }

    public class ThermalReactorBuild extends ThermalGeneratorBuild{
        public float st = 0;

        @Override
        public float warmup() {
            return getPowerProduction() / (powerProduction * productionEfficiency);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(st > 0.001f){
                damage(workdamage/60f * Time.delta);
            }
        }

        @Override
        public float getPowerProduction() {
            st = Mathf.lerpDelta(st, 1f, addSpeed);
            if(Mathf.equal(st, 1f, 0.001f)){
                st = 1f;
            }
            return powerProduction *  productionEfficiency * st;
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(st);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            st = read.f();
        }
    }
}
