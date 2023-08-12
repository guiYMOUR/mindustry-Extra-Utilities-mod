package ExtraUtilities.content;

import ExtraUtilities.worlds.drawer.DrawFunc;
import ExtraUtilities.worlds.entity.bullet.HealCone;
import arc.Core;
import arc.flabel.effects.ShakeEffect;
import arc.func.Func;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.struct.FloatSeq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static ExtraUtilities.content.EUGet.*;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.math.Angles.*;
import static mindustry.content.Fx.*;
import static mindustry.Vars.*;

public class EUFx {

    public static Effect StormExp(Color cor, Color liC) {
        return new Effect(60f, e -> {
            Draw.color(liC, cor, e.fin());
            Fill.circle(e.x, e.y, e.fout() * 40);
            Lines.stroke(e.fout() * 4.5f);
            Lines.circle(e.x, e.y, e.fin() * 60);
            Lines.stroke(e.fout() * 2.75f);
            Lines.circle(e.x, e.y, e.fin() * 30);
            randLenVectors(e.id, 45, 1 + 65 * e.fin(), e.rotation, 360, (x, y) -> {
                Lines.stroke(e.fout() * 2);
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 1);
            });
            randLenVectors(e.id, 85, 1 + 160 * e.fin(),  Time.time * 4, 360, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 10);
            });
        });
    }

    public static Effect flameShoot(Color colorBegin, Color colorTo, Color colorEnd, float length, float cone, int number, float lifetime){
        return new Effect(lifetime, 80, e -> {
                Draw.color(colorBegin, colorTo, colorEnd, e.fin());
            randLenVectors(e.id, number, e.finpow() * length, e.rotation, cone, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
            });
        });
    }

    public static Effect elDsp(Color cor, Color cor2) {
        return new Effect(20, e -> {
            Draw.color(cor,cor2,e.fin());
            Lines.stroke(e.fout() * 3);
            Lines.circle(e.x, e.y, e.fin() * 60);
            Lines.stroke(e.fout() * 1.75f);
            Lines.circle(e.x, e.y, e.fin() * 45);
            Draw.color(cor);
            Fill.circle(e.x, e.y, e.fout() * 20);
            Draw.color(cor,cor2,e.fin());
            Fill.circle(e.x, e.y, e.fout() * 14);
        });
    }

    public static Effect absorbEffect = new Effect(38, e -> {
        Draw.color(Items.sand.color);
        Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3 + 1);
            Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 2);
        });
    });

    public static Effect absorbEffect2 = new Effect(50, e -> {
        Draw.color(Items.pyratite.color.cpy().a(0.7f));
        Angles.randLenVectors(e.id, 2, 1 + 5 * e.fout(), e.rotation, e.rotation + 120, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3 + 1);
            Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 2);
        });
    });

    public static Effect missileTrailSmokeSmall = new Effect(90f, 90f, b -> {
        float intensity = 1.1f;

        color(b.color, 0.7f);
        for(int i = 0; i < 3; i++){
            rand.setSeed(b.id* 2L + i);
            float lenScl = rand.random(0.5f, 1f);
            int fi = i;
            b.scaled(b.lifetime * lenScl, e -> {
                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2f * intensity), 9f * intensity, (x, y, in, out) -> {
                    float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    float rad = fout * ((2f + intensity) * 1.2f);

                    Fill.circle(e.x + x, e.y + y, rad);
                    Drawf.light(e.x + x, e.y + y, rad * 1.2f, b.color, 0.5f);
                });
            });
        }
    }).layer(Layer.bullet - 1f);

    public static Effect gone(Color color, float r, float t){
        return new Effect(12, e -> {
            Draw.color(color);
            Lines.stroke(t * e.fout());
            Lines.circle(e.x, e.y, r * e.fout());
        });
    }
    public static Effect gone(Color color){
        return gone(color, 5, 2);
    }

    public static Effect rainbowShoot = new Effect(16, e -> {
        Draw.blend(Blending.additive);
        Draw.color(Color.valueOf("ff0000ff").shiftHue(Time.time * 2.0f));
        Lines.stroke(e.fout() * 1.5f);
        Angles.randLenVectors(e.id, 1, e.finpow() * 70f, e.rotation, 80f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 8f + 1.5f);
        });
        Draw.blend();
        Draw.reset();
    });

    public static Effect lancerLaserCharge(Color color){
        return new Effect(38f, e -> {
            color(color);
            randLenVectors(e.id, 14, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f);
            });
        });
    }

    public static Effect chargeBeginEffect(float chargeTime, Color color){
        return new Effect(chargeTime * 1.5f, e -> {
            color(Color.valueOf("ffffff"), color, e.fin());
            Fill.circle(e.x, e.y, e.fin() * 8);
            color();
            Fill.circle(e.x, e.y, e.fin() * 5);
        });
    }

    public static Effect stingerShoot(Color color){
        return new Effect(10, e -> {
            color(Color.white, color, e.fin());
            Lines.stroke(e.fout() * 2f + 0.2f);
            Lines.circle(e.x, e.y, e.fin() * 28);
        });
    }

    public static Effect trail(Color color, float width, float length){
        return new Effect(12, e -> {
            Draw.color(color);
            Drawf.tri(e.x, e.y, width * e.fout(), length, e.rotation);
        });
    }

    public static Effect prismHit = new Effect(16, e -> {
        Draw.blend(Blending.additive);
        Draw.color(Color.valueOf("ff0000ff").shiftHue(Time.time * 2f));
        Lines.stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 1, e.finpow() * 70f, e.rotation, 80f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 8f + 1.5f);
        });
        Draw.blend();
        Draw.reset();
    });

    public static Effect LACraft = new Effect(60, e ->{
//        Draw.blend(Blending.additive);
//        Draw.color(Color.valueOf("ff0000ff").shiftHue(e.fout() * 360f));
        Draw.color(Pal.surge, EUItems.lightninAlloy.color, e.fin());
        Lines.stroke(e.fout() * 5);
        Lines.circle(e.x, e.y, 20* e.fin());
//        Draw.blend();
//        Draw.reset();
    });

    public static Effect Start = new Effect(30, e -> {
        Draw.color(EUItems.lightninAlloy.color);
        Lines.stroke(3 * e.fout());
        if(e.data instanceof Float){
            float range = (float) e.data;
            Lines.circle(e.x, e.y, range * e.fout());
        }
    });

    public static Effect shieldDefense = new Effect(20, e -> {
        Draw.color(e.color);
        Lines.stroke(e.fslope() * 2.5f);
        Lines.poly(e.x, e.y, 6, 3 * e.fout() + 9);
        Angles.randLenVectors(e.id, 2, 32 * e.fin(), 0, 360,(x, y) -> {
            Lines.poly(e.x + x, e.y + y, 6, 2 * e.fout() + 2);
        });
    });

    public static Effect casingContinue(float lifetime, int shots){
        return new Effect(lifetime, e->{
            Draw.z(Layer.bullet);
            for(int a = 0; a < shots; a++) {
                float time = lifetime / shots;
                e.scaled(time * a, b -> {
                    Draw.color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, b.fin());
                    Draw.alpha(b.fout(0.5f));
                    float rot = Math.abs(e.rotation) + 90;
                    int i = -Mathf.sign(e.rotation);
                    float len = (4 + b.finpow() * 9) * i;
                    float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20 * b.fin()) * i;
                    Draw.rect(Core.atlas.find("casing"),
                            e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3 * b.fin()),
                            e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3 * b.fin()),
                            3, 6,
                            rot + e.fin() * 50 * i
                    );
                });
            }
        });
    }

    //divide into two parts for easy adjustment of layers, but ... after writing it, I realized that seem useless : (
    public static Effect ellipse(float startRad, int num, float lifetime, Color color){
        return new Effect(lifetime, e ->{
            float length = startRad * e.fin();
            float width = length/2;

            Draw.color(color);

            //half
            for(int i = 0; i <= num; i++){
                float rot = -90f + 180f * i / (float)num;
                Tmp.v1.trnsExact(rot, width);

                point(
                        (Tmp.v1.x) / width * length, //convert to 0..1, then multiply by desired length and offset relative to previous segment
                        Tmp.v1.y, //Y axis remains unchanged
                        e.x, e.y,
                        e.rotation + 90,
                        2f * e.fout()
                );
            }

            //the other half
            for(int i = 0; i <= num; i++){
                float rot = 90f + 180f * i / (float)num;
                Tmp.v1.trnsExact(rot, width);

                point(
                        (Tmp.v1.x) / width * length,
                        Tmp.v1.y,
                        e.x, e.y,
                        e.rotation + 90,
                        2f * e.fout()
                );
            }
        });
    }
    private static void point(float x, float y, float baseX, float baseY, float rotation, float rad){
        Tmp.v1.set(x, y).rotateRadExact(rotation * Mathf.degRad);
        Fill.circle(Tmp.v1.x + baseX, Tmp.v1.y + baseY, rad);
    }

    public static Effect Mk2Shoot(float rotation){
        return new Effect(30, e ->{
            Draw.z(Layer.effect - 0.1f);
            Draw.color(Color.valueOf("ff0000ff").shiftHue(Time.time * 2.0f));
            for(float r : new float[]{-rotation, rotation}) {
                Angles.randLenVectors(e.id, 1, e.fin() * 20f, e.rotation + r, 0, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 2 * e.fout());
                });
            }
            Draw.blend();
            Draw.reset();
        });
    }

    public static Effect digTile(Color color){
        return new Effect(30, e -> {
            mixcol(color, 1);
            alpha(e.fout());
            Fill.square(e.x, e.y, Vars.tilesize/2f);
        });
    }

    public static Effect expDillEffect(int size, Color color){
        return new Effect(15, e -> {
            Lines.stroke(3 * e.fout(), color);
            Lines.square(e.x, e.y, size * Vars.tilesize/2f * e.fin(), 180 * e.fout());
        });
    }

    public static Effect colorBall(Color color, float range){
        return new Effect(80, e -> {
            Draw.color(color.cpy().a(1/range));
            for(int i = 0; i < range; i++){
                Fill.circle(e.x, e.y, range * i/range * e.fout());
            }
        });
    }

    public static Effect aimEffect(float lifetime, Color color, float width, float length, float spacing){
        return new Effect(lifetime, e -> {
            Draw.color(color);
            float track = Mathf.curve(e.fin(Interp.pow2Out), 0, 0.25f) * Mathf.curve(e.fout(Interp.pow4Out), 0, 0.3f) * e.fin();
            for(int i = 0; i <= length / spacing; i++){
                Tmp.v1.trns(e.rotation, i * spacing);
                float f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * length - i * spacing) / spacing)) * (0.6f + track * 0.4f);
                Draw.rect(Core.atlas.find(name("aim-shoot")), e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144 * Draw.scl * f, 144 * Draw.scl * f, e.rotation - 90);
            }
            Tmp.v1.trns(e.rotation, 0, (2 - track) * Vars.tilesize * width);
            Lines.stroke(track * 2);
            for(int i : Mathf.signs){
                Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, length * (0.75f + track / 4) * Mathf.curve(e.fout(Interp.pow5Out), 0, 0.1f));
            }
        });
    }

    public static Effect expFtEffect(int amount, float size, float len, float lifetime, float startDelay){
        return new Effect(lifetime, e -> {
            float length = len + e.finpow() * 20f;
            rand.setSeed(e.id);
            for(int i = 0; i < amount; i++){
                v.trns(rand.random(360f), rand.random(length));
                float sizer = rand.random(size/2, size);

                e.scaled(e.lifetime * rand.random(0.5f, 1f), b -> {
                    color(Pal.darkerGray, b.fslope() * 0.93f);

                    Fill.circle(e.x + v.x, e.y + v.y, sizer + b.fslope() * 1.2f);
                });
            }
        }).startDelay(startDelay);
    }

    public static Effect wind = new Effect(30, e -> {
        Draw.z(Layer.debris);
        Draw.color(e.color);
        Fill.circle(e.x, e.y, e.fout() * 6 + 0.3f);
    });

    public static Effect coneFade(float findRange, float findAngle){
        return new Effect(15, e -> {
            float range = findRange * e.fout();
            Draw.color(Pal.heal);
            Draw.z(Layer.buildBeam);
            Draw.alpha(0.8f);
            for(float i = e.rotation - findAngle /2; i < e.rotation + findAngle /2; i+=2){
                float px1 = posx(e.x, range, i);
                float py1 = posy(e.y, range, i);
                float px2 = posx(e.x, range, i+2);
                float py2 = posy(e.y, range, i+2);
                Fill.tri(e.x, e.y, px1, py1, px2, py2);
            }
        });
    }

    //感谢老y
    public static Effect chainLightningFade = new Effect(45f, 500f, e -> {
        if(!(e.data instanceof Position)) return;
        Position p = e.data();
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = e.rotation;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(2.5f * Mathf.curve(e.fout(), 0, 0.7f));
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();

        Fill.circle(e.x, e.y, Lines.getStroke() / 2);
        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id);

        float fin = Mathf.curve(e.fin(), 0, 0.5f);
        float i;
        for(i = 0; i < links * fin; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range/2f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false);

    public static Effect ElectricExp(float lifetime, float sw, float r){
        return new Effect(lifetime, e -> {
            if(e.time < sw) {
                float fin = e.time/sw, fout = 1 - fin;
                Lines.stroke(r/12 * fout, Pal.heal);
                Lines.circle(e.x, e.y, r * fout);
            } else {
                float fin = (e.time - sw) / (e.lifetime - sw), fout = 1 - fin;
                float fbig = Math.min(fin * 10, 1);
                Lines.stroke(r/2 * fout, Pal.heal);
                Lines.circle(e.x, e.y, r * fbig);
                for(int i = 0; i < 2; i++){
                    float angle = i * 180 + 60;
                    Drawf.tri(e.x + Angles.trnsx(angle, r * fbig), e.y + Angles.trnsy(angle, r * fbig), 40 * fout, r/1.5f, angle);
                }
                Draw.z(Layer.effect + 0.001f);
                Lines.stroke(r/18 * fout, Pal.heal);
                randLenVectors(e.id + 1, fin * fin + 0.001f, 20, r * 2, (x, y, in, out) -> {
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * r/4);
                    Drawf.light(e.x + x, e.y + y, out * r, Draw.getColor(), 0.8f);
                });
                Effect.shake(3, 3, EUGet.pos(e.x, e.y));
            }
        });
    }

    public static Effect diffuse = new Effect(30, e -> {
        if(!(e.data instanceof Integer)) return;
        int size = (int) e.data;
        float f = e.fout();
        float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
        Lines.stroke(3f * f, e.color);
        Lines.beginLine();
        for(int i = 0; i < 4; i++){
            Lines.linePoint(e.x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, e.y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
            if(f < 0.5f) Lines.linePoint(e.x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, e.y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
        }
        Lines.endLine(true);
    });

    public static Effect diffuse(int size, Color color, float life) {
        return new Effect(life, e -> {
            float f = e.fout();
            float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
            Lines.stroke(3f * f, color);
            Lines.beginLine();
            for (int i = 0; i < 4; i++) {
                Lines.linePoint(e.x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, e.y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
                if (f < 0.5f)
                    Lines.linePoint(e.x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, e.y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
            }
            Lines.endLine(true);
        });
    }

    public static Effect fiammettaExp(float r){
        return new Effect(30, e -> {
            float fin = Math.min(e.time/10, 1), fout = 1 - ((e.time - 10)/(e.lifetime - 10));
            Draw.color(EUItems.lightninAlloy.color.cpy().a(e.time > 10 ? 0.3f * fout : 0.3f));
            Fill.circle(e.x, e.y, r * fin);
            float ww = r * 2f * fin, hh = r * 2f * fin;
            Draw.color(EUItems.lightninAlloy.color.cpy().a(e.time > 10 ? fout : 1));
            Draw.rect(Core.atlas.find(name("firebird-light")), e.x, e.y, ww, hh);
        });
    }

    public static Effect normalTrail = new Effect(90, e -> {
        Draw.color(e.color);
        float r = e.rotation;
        Fill.circle(e.x, e.y, r * e.foutpow());
    }).layer(Layer.bullet - 1f);

    public static Effect normalIceTrail = new Effect(90, e -> {
        DrawFunc.drawSnow(e.x, e.y, e.rotation * e.foutpow(), e.fin() * 180f, e.color);
    });
}
