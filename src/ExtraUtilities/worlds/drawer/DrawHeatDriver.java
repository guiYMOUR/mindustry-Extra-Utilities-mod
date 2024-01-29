package ExtraUtilities.worlds.drawer;

import ExtraUtilities.worlds.blocks.heat.HeatDriver;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.*;

public class DrawHeatDriver extends DrawBlock {
    public TextureRegion turretPart, turretLine, lPart, rPart, lLine, rLine, eff1, eff2, arrow, preview;
    //public float x = 0f, y = 0f, rotation = 90f, progress = 0f, resProgress = 0f;
    public float arrowSpacing = 4f, arrowOffset = 2f, arrowPeriod = 0.4f, arrowTimeScl = 6.2f;

    public Color dc1 = Color.valueOf("ea8878");

    @Override
    public void draw(Building build) {
        HeatDriver block = (HeatDriver) build.block;
        HeatDriver.HeatDriverBuild b = (HeatDriver.HeatDriverBuild) build;
        float x = build.x, y = build.y;
        drawMain(block, b, x,y);
    }

    public void drawMain(HeatDriver block, HeatDriver.HeatDriverBuild build, float x, float y){
        float progress = build.progress;
        float rotation = build.rotation;
        float resProgress = build.resProgress;
        Draw.z(Layer.turret);
        float move = 3f * progress;
        Drawf.shadow(turretPart,
                x,
                y,
                rotation - 90f
        );
        Drawf.shadow(lPart,
                x + Angles.trnsx(rotation + 180f, 0f, -move) - block.size/2f,
                y + Angles.trnsy(rotation + 180f, 0, -move) - block.size/2f,
                rotation - 90f
        );
        Drawf.shadow(rPart,
                x + Angles.trnsx(rotation + 180f, 0f, move) - block.size/2f,
                y + Angles.trnsy(rotation + 180f, 0f, move) - block.size/2f,
                rotation - 90f
        );
        Draw.rect(rLine,
                x + Angles.trnsx(rotation + 180f, 0f, move),
                y + Angles.trnsy(rotation + 180f, 0f, move),
                rotation - 90f
        );
        Draw.rect(lLine,
                x + Angles.trnsx(rotation + 180f, 0f, -move),
                y + Angles.trnsy(rotation + 180f, 0f, -move),
                rotation - 90f
        );
        Draw.rect(turretLine,
                x,
                y,
                rotation - 90f
        );
        Draw.rect(turretPart,
                x,
                y,
                rotation - 90f
        );
        Draw.rect(lPart,
                x + Angles.trnsx(rotation + 180f, 0f, -move),
                y + Angles.trnsy(rotation + 180f, 0f, -move),
                rotation - 90f
        );
        Draw.rect(rPart,
                x + Angles.trnsx(rotation + 180f, 0f, move),
                y + Angles.trnsy(rotation + 180f, 0f, move),
                rotation - 90f
        );
        float p = Math.min((build.heat/block.visualMaxHeat) * build.power.status, 1);
        Draw.color(dc1);
        Draw.alpha(p);
        Draw.z(Layer.effect);
        if(progress > 0.01f){
            Draw.rect(eff1,
                    x + Angles.trnsx(rotation + 180f, -8f),
                    y + Angles.trnsy(rotation + 180f, -8f),
                    10f * progress,
                    10f * progress,
                    rotation - 90f - Time.time * 2
            );
            Draw.rect(eff2,
                    x + Angles.trnsx(rotation + 180f, -8f),
                    y + Angles.trnsy(rotation + 180f, -8f),
                    6f * progress,
                    6f * progress,
                    rotation - 90f + Time.time * 2
            );
            for(int i = 0; i < 4; i++){
                float angle = i* 360f / 4;
                Drawf.tri(x + Angles.trnsx(rotation + 180f, -8f) + Angles.trnsx(angle + Time.time, 5f), y + Angles.trnsy(rotation + 180f, -8f) + Angles.trnsy(angle + Time.time, 5f), 6f, 2f * progress, angle + Time.time);
            }
        }
        if(resProgress > 0.01f){
            Draw.alpha(1);
            Lines.stroke(1*resProgress);
            Lines.circle(x + Angles.trnsx(rotation + 180f, 10f), y + Angles.trnsy(rotation + 180f, 10f), 5);
            Lines.circle(x + Angles.trnsx(rotation + 180f, 10f), y + Angles.trnsy(rotation + 180f, 10f), 3);
            //Draw.color(Color.valueOf("ea8878"));
            for(int i = 0; i < 3; i++){
                float angle = i* 360f / 3;
                Drawf.tri(x + Angles.trnsx(rotation + 180f, 10f) + Angles.trnsx(angle - Time.time, 5), y + Angles.trnsy(rotation + 180f, 10f) + Angles.trnsy(angle - Time.time, 5f), 4f, -2f * resProgress, angle - Time.time);
                Drawf.tri(x + Angles.trnsx(rotation + 180f, 10f) + Angles.trnsx(angle + Time.time, 3), y + Angles.trnsy(rotation + 180f, 10f) + Angles.trnsy(angle + Time.time, 3f), 3f, 1 * resProgress, angle + Time.time);
            }
        }
        if(build.linkValid()){
            Building other = world.build(build.link);
            if(!Angles.near(rotation, build.angleTo(other), 2f)) return;
            Draw.color();
            float dist = build.dst(other)/arrowSpacing - block.size;
            int arrows = (int)(dist / arrowSpacing);

            for(int a = 0; a < arrows; a++){
                Draw.alpha(Mathf.absin(a - Time.time / arrowTimeScl, arrowPeriod, 1f) * progress * Renderer.bridgeOpacity * p);
                Draw.rect(arrow,
                        x + Angles.trnsx(rotation + 180f, -arrowSpacing) * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        y + Angles.trnsy(rotation + 180f, -arrowSpacing) * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        25f,
                        25f,
                        rotation);
            }
        }
    }

    @Override
    public void load(Block block) {
        turretPart = Core.atlas.find(block.name + "-turret");
        turretLine = Core.atlas.find(block.name + "-turret-outline");
        lPart = Core.atlas.find(block.name + "-l");
        lLine = Core.atlas.find(block.name + "-l-outline");
        rPart = Core.atlas.find(block.name + "-r");
        rLine = Core.atlas.find(block.name + "-r-outline");
        eff1 = Core.atlas.find(block.name + "-effect");
        eff2 = Core.atlas.find(block.name + "-effect");
        arrow = Core.atlas.find(block.name + "-arrow");
        preview = Core.atlas.find(block.name + "-preview");
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(preview, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{preview};
    }
}
