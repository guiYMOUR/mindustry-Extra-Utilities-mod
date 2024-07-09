package ExtraUtilities.worlds.blocks.logic;

import ExtraUtilities.worlds.drawer.DrawFunc;
import arc.Core;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class Clock extends Block {
    public Color backColor = Pal.gray.cpy().a(0.7f);

    public Clock(String name) {
        super(name);
        solid = true;
        destructible = true;
        group = BlockGroup.logic;
        drawDisabled = false;
        envEnabled = Env.any;
        canOverdrive = false;
    }

    public class ClockBuild extends Building {
        public int mod = 0;

        @Override
        public void drawSelect() {
            super.drawSelect();
            if(mod == 0) DrawFunc.drawClockTable(this, size, 64, 0.3f, 16, backColor,
                    Core.atlas.find(name("clockBack")),
                    Core.atlas.find(name("clockSt1")),
                    Core.atlas.find(name("clockMt1")),
                    Core.atlas.find(name("clockHt1"))
            );
        }
    }
}
