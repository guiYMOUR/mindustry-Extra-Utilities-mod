package ExtraUtilities.worlds.drawer;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.draw.*;

public class DrawRainbow extends DrawBlock {
    public float shiftSpeed = 2f;
    public int size = 6;
    public TextureRegion[] rainbowRegions = new TextureRegion[6];

    public DrawRainbow(float shiftSpeed, int size){
        this.shiftSpeed = shiftSpeed;
        this.size = size;
        this.rainbowRegions = new TextureRegion[size];
    }
    public DrawRainbow(){
    }

    @Override
    public void draw(Building build) {
        TurretBuild b = (TurretBuild) build;
        drawRainbow(b);
    }
    public void drawRainbow(TurretBuild build){
        Draw.blend(Blending.additive);
        for(int h = 0; h < size; h++){
            Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * shiftSpeed) + (h * (360f / size))));
            Draw.rect(rainbowRegions[h], build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.rotation - 90);
        }
        Draw.blend();
        Draw.color();
    }

    @Override
    public void load(Block block) {
        for (int i = 0; i < size; i++){
            rainbowRegions[i] = Core.atlas.find(block.name + "-rainbow-" + (i + 1));
        }
    }
}
