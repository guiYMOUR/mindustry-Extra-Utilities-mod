package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.distribution.PhaseNode.*;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;


public class DrawPhaseNode extends DrawBlock {
    public TextureRegion bridgeRegion;
    public TextureRegion endRegion;

    @Override
    public void draw(Building build) {
        if(!(build instanceof PhaseNodeBuild)) return;
        drawR((PhaseNodeBuild) build);
    }
    public void drawR(PhaseNodeBuild build){
        Draw.z(Layer.power);
        Building other = Vars.world.build(build.link);
        if(other == null) return;
        float op = Core.settings.getInt("bridgeopacity") / 100f;
        if(Mathf.zero(op)) return;

        Draw.color(Color.white);
        if(build.block.hasPower) Draw.alpha(Math.max(build.power.status, 0.25f) * op);
        else Draw.alpha(op);

        Draw.rect(endRegion, build.x, build.y);
        Draw.rect(endRegion, other.x, other.y);

        Lines.stroke(8);

        Tmp.v1.set(build.x, build.y).sub(other.x, other.y).setLength(Vars.tilesize/2f).scl(-1);

        Lines.line(bridgeRegion,
                build.x,
                build.y,
                other.x,
                other.y, false);
        Draw.reset();
    }

    @Override
    public void load(Block block) {
        bridgeRegion = Core.atlas.find(block.name + "-bridge");
        endRegion = Core.atlas.find(block.name + "-end");
    }
}
