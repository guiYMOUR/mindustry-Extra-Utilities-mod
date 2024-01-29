package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.draw.DrawBlock;

public class DrawMk2 extends DrawBlock {
    public TextureRegion or;
    @Override
    public void draw(Building build) {
        if(build instanceof TurretBuild) drawOut((TurretBuild) build);
    }
    public void drawOut(TurretBuild build){
        Draw.color(EUGet.EC4.set(EUGet.rainBowRed).shiftHue((Time.time * 2)));
        if(build.wasShooting){
            Draw.rect(or, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
            float x = build.x + Angles.trnsx(build.rotation - 90, 0, ((Turret)build.block).shootY);
            float y = build.y + Angles.trnsy(build.rotation - 90, 0, ((Turret)build.block).shootY);
            EUFx.Mk2Shoot.at(x, y, build.rotation, 50f);
        }
        Draw.reset();
    }

    @Override
    public void load(Block block) {
        or = Core.atlas.find(block.name + "-or");
    }
}
