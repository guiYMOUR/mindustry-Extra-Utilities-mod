package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

import static ExtraUtilities.ExtraUtilitiesMod.*;

public class MagneticStormBulletType extends BasicBulletType {
    public Color cor = Pal.ammo;
    public Color liC = Pal.ammo;
    /** damage[min, mid, max, des]*/
    public float[] damages = {15, 18, 20, 32};

    public MagneticStormBulletType(Color c1, Color c2){
        cor = c1;
        liC = c2;
        despawnEffect = EUFx.StormExp(cor, liC);
    }
    public MagneticStormBulletType(){
    }

    @Override
    public void update(Bullet b) {
        updateHoming(b);
        if(b.timer.get(1,4)){
            for(int i = 0; i < 3; i++){
                Lightning.create(b.team, cor, damages[1], b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(8,20));
            }
            for(int i = 0; i < 5; i++){
                Lightning.create(b.team, liC, damages[0], b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(5,10));
            }
            for(int i = 0; i < 7; i++){
                Lightning.create(b.team, cor, damages[2], b.x + Mathf.random(-40,40), b.y + Mathf.random(-40,40), Mathf.random(360), Mathf.random(3,7));
            }
            if(Mathf.chance(Time.delta * 0.075)){
                int len = Mathf.random(1, 7);
                float a = b.rotation() + Mathf.range(fragSpread/2) + fragAngle;
                Lightning.create(b.team, liC, damages[0], b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, (int) (0.5 + Mathf.random(14)));
            }
        }
    }

    @Override
    public void draw(Bullet b) {
        int plasmas = 6;
        for(int i = 0; i < plasmas; i++){
            //float r = 29 + Mathf.absin(Time.time, 2 + i * 1f, 5f - i * 0.5f);
            Draw.color(liC, Color.valueOf("a7d8fe"), i / 6f);
            Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)) * 1);
            Draw.blend(Blending.additive);
            Draw.rect(Core.atlas.find( ModName + "-plasma-" + i), b.x, b.y, width, height, Time.time * (12 + i * 6) * 1);
            Draw.blend();
        }
    }

    @Override
    public void despawned(Bullet b) {
        despawnEffect.at(b.x, b.y, b.rotation());
        for(int i = 0; i < 12; i++){
            Lightning.create(b.team, cor, damages[3], b.x , b.y , Mathf.random(360), Mathf.random(25, 40));
            int len = Mathf.random(1, 7);
            float a = b.rotation() + Mathf.range(fragSpread/2) + fragAngle;
            Lightning.create(b.team, liC, damages[3], b.x - Angles.trnsx(a, len), b.y - Angles.trnsy(a, len), a, (int) (0.5 + Mathf.random(14)));
        }
    }
}
