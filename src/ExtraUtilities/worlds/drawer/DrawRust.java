package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.turret.WitchService;
import ExtraUtilities.worlds.entity.bullet.ChainLightningFade;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.tilesize;

public class DrawRust extends DrawBlock {
    public TextureRegion rot, base;
    protected ChainLightningFade chain = new ChainLightningFade(30, 10, 1.3f, Pal.techBlue, 0, Fx.none){{
        layer = Layer.blockUnder;
        drawSize = 60 * tilesize;
    }};

    @Override
    public void draw(Building build) {
        Draw.rect(base, build.x, build.y);
        Draw.rect(rot, build.x, build.y, build.rotation * 90);

        if(!Vars.headless && (Core.settings != null && Core.settings.getBool("eu-show-rust-range"))) {
            if (build instanceof WitchService.ServiceBuild s) {
                Rect r = s.getR();
                //矩形的四个点
                float
                        rx1 = r.x + r.width, ry1 = r.y,
                        rx2 = r.x, ry2 = r.y,
                        rx3 = r.x, ry3 = r.y + r.height,
                        rx4 = r.x + r.width, ry4 = r.y + r.height;
                float status = s.getDelta() / Time.delta;
                if (status < 0.0001f) return;
                if (build.timer.get(15 / (status + 0.0001f))) {
                    float len = Mathf.dst(rx1, ry1, rx2, ry2);
                    float angle = Angles.angle(rx1, ry1, rx2, ry2);
                    chain.create(build, build.team, rx1, ry1, angle, -1, 1, 1, len);
                    len = Mathf.dst(rx2, ry2, rx3, ry3);
                    angle = Angles.angle(rx2, ry2, rx3, ry3);
                    chain.create(build, build.team, rx2, ry2, angle, -1, 1, 1, len);
                    len = Mathf.dst(rx3, ry3, rx4, ry4);
                    angle = Angles.angle(rx3, ry3, rx4, ry4);
                    chain.create(build, build.team, rx3, ry3, angle, -1, 1, 1, len);
                    len = Mathf.dst(rx4, ry4, rx1, ry1);
                    angle = Angles.angle(rx4, ry4, rx1, ry1);
                    chain.create(build, build.team, rx4, ry4, angle, -1, 1, 1, len);
                }
            }
        }
    }

    @Override
    public void load(Block block) {
        rot = Core.atlas.find(block.name + "-rot");
        base = Core.atlas.find(block.name + "-base");
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{base, rot};
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(base, plan.drawx(), plan.drawy());

        Draw.rect(rot, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }
}
