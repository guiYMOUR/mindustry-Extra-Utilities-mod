package ExtraUtilities.worlds.blocks.heat;

import arc.Core;
import arc.math.Mathf;
import arc.util.io.*;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static ExtraUtilities.ExtraUtilitiesMod.*;

public class ThermalHeater extends ThermalGenerator {
    public float basicHeatOut = 5f;
    public float warmupRate = 0.15f;//as HeaterGenerator
    public ThermalHeater(String name) {
        super(name);

        drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
        rotateDraw = false;
        rotate = true;
        canOverdrive = false;
        drawArrow = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.output, Core.bundle.format("stat." + name("basicHeat"), basicHeatOut));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (ThermalHeaterBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.heat / basicHeatOut * entity.productionEfficiency));
    }

    public class ThermalHeaterBuild extends ThermalGeneratorBuild implements HeatBlock{
        public float heat;
        @Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.approachDelta(heat, basicHeatOut * efficiency * productionEfficiency, warmupRate * delta());
        }

        @Override
        public float heatFrac(){
            return heat / basicHeatOut;
        }

        @Override
        public float heat(){
            return heat;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            heat = read.f();
        }
    }
}
