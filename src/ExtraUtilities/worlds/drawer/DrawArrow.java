package ExtraUtilities.worlds.drawer;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class DrawArrow extends DrawPart {
    public float arrowSpacing = 2, arrowOffset = 0f, arrowPeriod = 0.4f, arrowTimeScl = 12f, arrowSize = 35f;
    public float len = 6;
    public float x, y;
    public float layer = -1, layerOffset = 0;
    public PartProgress progress = PartProgress.warmup;
    public Color color = Color.white;
    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under && turretShading) Draw.z(z - 0.0001f);

        Draw.z(Draw.z() + layerOffset);

        int i = params.sideOverride == -1 ? 0 : params.sideOverride;

        float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
        Tmp.v1.set((x) * sign, y).rotate(params.rotation - 90);

        float
                rx = params.x + Tmp.v1.x,
                ry = params.y + Tmp.v1.y;

        int arrows = (int)(len / arrowSpacing);

        Draw.color(color);

        for(int a = 0; a < arrows; a++){
            Draw.alpha(Mathf.absin(a - Time.time / arrowTimeScl, arrowPeriod, 1f) * progress.getClamp(params));
            Draw.rect(Core.atlas.find(name("arrow")),
                    rx + Angles.trnsx(params.rotation + 180f, -arrowSpacing) * (a * arrowSpacing + arrowOffset),
                    ry + Angles.trnsy(params.rotation + 180f, -arrowSpacing) * (a * arrowSpacing + arrowOffset),
                    arrowSize,
                    arrowSize,
                    params.rotation);
        }
        Draw.color();
        Draw.alpha(1);
    }

    @Override
    public void load(String name) {

    }
}
