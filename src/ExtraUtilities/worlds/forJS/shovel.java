package ExtraUtilities.worlds.forJS;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Pal;
import mindustry.world.blocks.production.Drill;

public class shovel extends Drill {
    public shovel(String name) {
        super(name);
    }

    public class shovelBuild extends DrillBuild {
        @Override
        public void draw() {
            Draw.alpha(warmup);
            Draw.rect(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            Draw.alpha(1);
            Draw.color();
            Draw.rect(topRegion, x, y);
            Draw.color(heatColor);
            Draw.alpha(warmup * 0.6f * (1 - 0.3f + Mathf.absin(Time.time, 3, 0.3f)));
            Draw.blend(Blending.additive);
            Draw.rect(rimRegion, x, y);
            Draw.blend();
            Draw.color();
        }
    }
}
