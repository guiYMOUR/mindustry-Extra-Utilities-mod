package ExtraUtilities.worlds.entity.animation;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.graphics.MainRenderer;
import ExtraUtilities.worlds.entity.bullet.BlackHoleBullet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static arc.graphics.g2d.Draw.*;

public class DeathAnimation extends AnimationType {
    public DeathAnimation(){
        lifetime = 512;

        BulletType hole = new BlackHoleBullet(){{
            impulse = 0;
            inRad = 3.2f * 8;
            outRad = 14 * 8;
            accColor = EUGet.MIKU;
            lifetime = 270;
        }};

        start = new AnimationType(320){{
            lifetime = 48;
            sprite = "extra-utilities-regency-full";
        }

            @Override
            public void draw(Bullet b) {
                float z = Draw.z();
                Draw.z(Layer.max - 0.1f);
                if(b.time < b.lifetime - 12){
                    float shake = Mathf.sin(Time.time, 120 * b.foutpow() + 6, 40 * b.finpow());
                    float bx = b.x + shake;
                    Draw.rect(frontRegion, bx, b.y, b.rotation() - 90);
                } else {
                    float out = (b.lifetime - b.time)/12;
                    out = Interp.fastSlow.apply(out);
                    Draw.rect(frontRegion, b.x, b.y,
                            (float) frontRegion.width * frontRegion.scl() * xscl * out,
                            (float) frontRegion.height * frontRegion.scl() * yscl * out, b.rotation() - 90);
                    MainRenderer.addBlackHole(b.x, b.y, 0, 42 * 8f * out);
                    Draw.color(Color.white);
                    for(int i = 0; i < 4; i++){
                        float a = 90 * i;
                        float l = i % 2 == 0 ? 150 : 75;
                        Drawf.tri(b.x, b.y, 8 * out, l * (1 - out), a);
                    }
                }
                Draw.z(z);
            }
        };
        loop = new AnimationType(360){{
            sprite = "extra-utilities-regency-full";
        }

            @Override
            public void draw(Bullet b) {
                float z = Draw.z();
                Draw.z(Layer.max - 0.1f);

                if(b.time < 12){
                    float in = b.time/12;
                    float out = 1 - in;
                    in = Interp.fastSlow.apply(in);
                    out = Interp.fastSlow.apply(out);
                    MainRenderer.addBlackHole(b.x, b.y, 0, 42 * 8f * out);
                    Draw.color(Color.white);
                    for(int i = 0; i < 4; i++){
                        float a = 90 * i;
                        float l = i % 2 == 0 ? 150 : 75;
                        Drawf.tri(b.x, b.y, 8 * out, l * (1 - out), a);
                    }
                    Draw.rect(frontRegion, b.x, b.y,
                            (float) frontRegion.width * frontRegion.scl() * xscl * in,
                            (float) frontRegion.height * frontRegion.scl() * yscl * in, b.rotation() - 90);
                } else if(b.time > b.lifetime - 12){
                    float out = (b.lifetime - b.time)/12;
                    out = Interp.fastSlow.apply(out);
                    float shake = Mathf.sin(Time.time, 0.9f, 20 * out);
                    MainRenderer.addBlackHole(b.x, b.y, 0, 36 * 8f * out);
                    Draw.color(Color.white);
                    for(int i = 0; i < 4; i++){
                        float a = 90 * i;
                        float l = i % 2 == 0 ? 150 : 75;
                        Drawf.tri(b.x, b.y, 8 * out, l * (1 - out), a);
                    }
                    Draw.rect(frontRegion, b.x + shake, b.y,
                            (float) frontRegion.width * frontRegion.scl() * xscl * out,
                            (float) frontRegion.height * frontRegion.scl() * yscl * out, b.rotation() - 90);
                } else {
                    float shake = Mathf.sin(Time.time, 0.9f, 20);
                    Draw.rect(frontRegion, b.x + shake, b.y, b.rotation() - 90);
                }
                Draw.z(z);
            }
        };
        end = new AnimationType(320){{
            lifetime = 180;
            sprite = "extra-utilities-regency-full";
        }

            @Override
            public void draw(Bullet b) {
                float z = Draw.z();
                Draw.z(Layer.max - 0.1f);
                if(b.time < 12){
                    float in = b.time/12;
                    float out = 1 - in;
                    in = Interp.fastSlow.apply(in);
                    out = Interp.fastSlow.apply(out);
                    MainRenderer.addBlackHole(b.x, b.y, 0, 36 * 8f * out);
                    Draw.color(Color.white);
                    for(int i = 0; i < 4; i++){
                        float a = 90 * i;
                        float l = i % 2 == 0 ? 150 : 75;
                        Drawf.tri(b.x, b.y, 8 * out, l * (1 - out), a);
                    }
                    Draw.rect(frontRegion, b.x, b.y,
                            (float) frontRegion.width * frontRegion.scl() * xscl * in,
                            (float) frontRegion.height * frontRegion.scl() * yscl * in, b.rotation() - 90);
                } else {
                    float in = (b.time - 12)/(b.lifetime - 12);
                    float out = 1 - in;
                    out = Interp.fastSlow.apply(out);
                    if(b.owner instanceof Posc pos){
                        float dx = EUGet.dx(pos.getX(), b.fdata * out, pos.angleTo(b) + out * 720),
                                dy = EUGet.dy(pos.getY(), b.fdata * out, pos.angleTo(b) + out * 720);
                        Draw.rect(frontRegion, dx, dy,
                                (float) frontRegion.width * frontRegion.scl() * xscl * out,
                                (float) frontRegion.height * frontRegion.scl() * yscl * out, b.rotation() - 90);
                    }
                }
                Draw.z(z);
            }

            @Override
            public void init(Bullet b) {
                if(b.owner instanceof Posc pos){
                    hole.create(b, pos.getX(), pos.getY(), 0, 0);
                    b.fdata = pos.dst(b);
                }
            }
        };
    }

    @Override
    public void update(Bullet b) {
        if(!(b instanceof animation at)) return;
        if(b.time < start.lifetime) {
            if (!at.started) {
                at.started = true;
                start.create(b, b.x, b.y, b.rotation());
            }
            at.afx = b.x + Mathf.random(-200, 200);
            at.afy = b.y + Mathf.random(-200, 200);
        } else if(b.time > lifetime - start.lifetime - end.lifetime){
            if (!at.ended) {
                at.ended = true;
                end.create(b, at.afx, at.afy, b.rotation());
            }
        } else {
            //loop
            float endTime = lifetime - start.lifetime - end.lifetime;
            float loopTime = b.time - start.lifetime;
            if(at.loop == null || !at.loop.isAdded()){
                at.loop = loop.create(b, at.afx, at.afy, b.rotation());
                if(loopTime > endTime/3) at.loop.lifetime = 24.001f;
                else at.loop.lifetime = 48f;
                at.afx = b.x + Mathf.random(-200, 200);
                at.afy = b.y + Mathf.random(-200, 200);
            }
        }
    }
}
