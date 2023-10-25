package ExtraUtilities.worlds.drawer;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class arcanaPart extends DrawPart {
    public PartProgress r = PartProgress.reload;
    //final PartProgress w = PartProgress.warmup;
    public float rad = 7;
    @Override
    public void draw(PartParams params) {
        float z = Draw.z();
        float reload = 1 - r.getClamp(params);
        Tmp.v1.set(0, 13).rotate(params.rotation - 90);
        float px = params.x + Tmp.v1.x, py = params.y + Tmp.v1.y;

        Draw.z(Layer.bullet);
        Draw.color(Pal.techBlue);
        Fill.circle(px, py, rad * reload);

        for(int i = 0; i < 3; i++){
            float sin = Mathf.absin(Time.time + 120 * i, 15, 6 * reload);
            float sin_m = Mathf.absin(Time.time + 120 * i, 30, 6 * reload);
            float r = 360/3f * i + Time.time;
            float r2 = r + 190;
            Drawf.tri(px + Angles.trnsx(r, (rad - 1) * reload), py + Angles.trnsy(r, (rad - 1) * reload), 6 * reload, sin, r);
            Drawf.tri(px + Angles.trnsx(r2, (rad - 1) * reload), py + Angles.trnsy(r2, (rad - 1) * reload), 6 * reload, sin_m, r2);
        }

        Draw.reset();
        Draw.z(z);
    }

    @Override
    public void load(String name) {

    }
}
