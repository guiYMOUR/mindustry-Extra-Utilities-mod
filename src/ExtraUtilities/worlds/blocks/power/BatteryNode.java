package ExtraUtilities.worlds.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.BlockStatus;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class BatteryNode extends PowerNode {
    public Color emptyLightColor = Color.valueOf("f8c266");//f7bd5d
    public Color fullLightColor = Color.valueOf("fb9567");
    public String cap = "png-capacity";
    public BatteryNode(String name) {
        super(name);
        outputsPower = true;
        consumesPower = true;
        consumePowerBuffered(30000);
    }
    public class BatteryNodeBuild extends PowerNodeBuild{
        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.power - 1);
            Draw.color(emptyLightColor, fullLightColor, power.status);
            Draw.rect(Core.atlas.find(name(cap)), x, y);
        }

        @Override
        public BlockStatus status(){
            if(Mathf.equal(power.status, 0f, 0.001f)) return BlockStatus.noInput;
            if(Mathf.equal(power.status, 1f, 0.001f)) return BlockStatus.active;
            return BlockStatus.noOutput;
        }
    }
}
