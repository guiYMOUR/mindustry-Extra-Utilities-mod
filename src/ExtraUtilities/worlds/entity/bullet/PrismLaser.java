package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import ExtraUtilities.worlds.blocks.turret.Prism;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class PrismLaser extends ContinuousLaserBulletType {
    public float[] strokes = new float[]{1.2f, 1.1f, 1, 0.9f};
    public float[] tscales = new float[]{1, 0.74f, 0.5f, 0.24f};
    public float[] lenscales = new float[]{0.96f, 1, 1.017f, 1.025f};
    public int plan = 2;
    public PrismLaser(float damage, float width, float length){
        this.damage = damage;
        this.width = width;
        this.length = length;
        this.hitEffect = EUFx.prismHit;
        lifetime = 24;
        frontLength = 28f;
    }

    //plan 1
    public void drawPlanA(Bullet b) {
        if(!(b.owner instanceof Prism.PrismBuild)) return;
        float realLength = Damage.findLaserLength(b, this.length);
        float bulletHeat = 1 - ((Prism.PrismBuild)b.owner).bulletHeat + 0.03f;
        float realWidth = width * bulletHeat;
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1.0F - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1.0F);
        float baseLen = realLength * fout;
        float rot = b.rotation();
        Draw.blend(Blending.additive);
        //Draw.alpha(realWidth/width);
        Draw.color(EUGet.EC3.set(EUGet.rainBowRed).shiftHue((int)b.data));
        for(int i = 0; i <4; i++) {
            //Draw.color(Tmp.c1.set(cs[i]).mul(1.0F + Mathf.absin(Time.time, 1.0F, 0.1F)));
            float colorFin = (float) i / (float) (this.colors.length - 1);
            float baseStroke = Mathf.lerp(this.strokeFrom, this.strokeTo, colorFin);
            float stroke = (realWidth + Mathf.absin(Time.time, this.oscScl, this.oscMag)) * fout * baseStroke;
            float ellipseLenScl = Mathf.lerp(1f - (float) i / (float) this.colors.length, 1f, this.pointyScaling);
            Lines.stroke(stroke);
            Lines.lineAngle(b.x, b.y, rot, baseLen - this.frontLength, false);
            Drawf.flameFront(b.x, b.y, this.divisions, rot + 180f, this.backLength, stroke / 2f);
            Tmp.v1.trnsExact(rot, baseLen - this.frontLength);
            Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.divisions, rot, this.frontLength * ellipseLenScl, stroke / 2f);
        }
        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.lightStroke, this.lightColor, 0.7f);
        Draw.blend();
        Draw.reset();
    }
    //plan 2
    public void drawPlanB(Bullet b){
        if(!(b.owner instanceof Prism.PrismBuild)) return;
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
        float realLength = Damage.findLaserLength(b, this.length);
        float baseLen = realLength * fout;
        float bulletHeat = 1 - ((Prism.PrismBuild)b.owner).bulletHeat;
        float wide = Mathf.clamp(bulletHeat * bulletHeat + 0.03f);
        Draw.z(Layer.bullet - 1);
        //Draw.color(Color.valueOf("ff0000").shiftHue((int)b.data));
        Draw.color((Tmp.c1.set(EUGet.rainBowRed).mul(1.0F + Mathf.absin(Time.time, 1f, 0.1f))).shiftHue((int)b.data));
        Draw.alpha(0.04f);
        Draw.blend(Blending.additive);
        for(int s = 0; s < 4; s++){
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180, (lenscales[i] - 1) * frontLength);
                Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * wide * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }
        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
        Draw.blend();
    }

    public void drawPlanC(Bullet b){
        if(!(b.owner instanceof PrismCtr.ctr c)) return;
        float fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
        float realLength = Damage.findLaserLength(b, this.length);
        float baseLen = realLength * fout;
        float wide = Mathf.clamp(c.fin() + 0.03f);
        Draw.z(Layer.bullet - 1);
        //Draw.color(Color.valueOf("ff0000").shiftHue((int)b.data));
        Draw.color((Tmp.c1.set(EUGet.rainBowRed).mul(1.0f + Mathf.absin(Time.time, 1f, 0.1f))).shiftHue((int)b.data));
        Draw.alpha(0.04f);
        Draw.blend(Blending.additive);
        for(int s = 0; s < 4; s++){
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180, (lenscales[i] - 1) * frontLength);
                Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * wide * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }
        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
        Draw.blend();
    }

    @Override
    public void draw(Bullet b) {
        switch (plan) {
            case 1 -> drawPlanA(b);
            case 2 -> drawPlanB(b);
            case 3 -> drawPlanC(b);
        }
    }
}
