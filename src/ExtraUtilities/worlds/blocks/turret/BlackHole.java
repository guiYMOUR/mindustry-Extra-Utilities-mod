//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.ExtraUtilitiesMod;
import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import java.util.Arrays;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;

public class BlackHole extends aimBulletTurret {
    static TextureRegion bp;
    static Color accColor = Color.valueOf("B778FF");
    BulletType blackPionter = new BulletType() {
        {
            this.damage = this.splashDamage = 350;
            this.pierceArmor = true;
            this.splashDamageRadius = 96;
            this.trailWidth = 3;
            this.trailLength = 7;
            this.trailColor = BlackHole.accColor;
            this.drawSize = 240;
            this.lifetime = 180;
            this.speed = 0;
            this.pierce = this.pierceBuilding = true;
            this.absorbable = this.hittable = this.reflectable = false;
            this.hitEffect = this.despawnEffect = new Effect(42, (e) -> {
                float fin = Math.min(1, e.finpow() * 5);

                for(int i = 0; i < 4; ++i) {
                    float a = (float)(45 + 90 * i);
                    float z = Draw.z();
                    Draw.z(90);
                    Draw.color(Color.black);
                    Drawf.tri(e.x, e.y, 50 * e.foutpow(), 110 * fin, a);
                    Draw.z(z);
                    Draw.color(BlackHole.accColor);
                    Drawf.tri(e.x, e.y, 42 * e.foutpow(), 100 * fin, a);
                }

            });
        }

        public void load() {
            super.load();
            BlackHole.bp = Core.atlas.find(ExtraUtilitiesMod.name("black-pointer"));
        }

        public void update(Bullet b) {
            super.update(b);
            Object data = b.data;
            if (data instanceof Vec2 p) {
                if (b.time < 150) {
                    float fin = b.time / 150;
                    fin = Interp.fastSlow.apply(fin);
                    float dx = EUGet.dx(p.x, 134 * fin + 30, 45 + 90 * b.fdata);
                    float dy = EUGet.dy(p.y, 134 * fin + 30, 45 + 90 * b.fdata);
                    EUGet.movePoint(b, dx, dy, 0.05F, true, EUGet::hollow);
                } else {
                    EUGet.movePoint(b, p.x, p.y, 0.05F, true, EUGet::hollow);
                }
            }

        }

        public void draw(Bullet b) {
            super.draw(b);
            Draw.z(115);
            Draw.rect(BlackHole.bp, b.x, b.y, 16, 16, b.rotation() + 90);
            Draw.color(BlackHole.accColor);
            float fin = Math.min(1, b.time / 150);
            fin = Interp.fastSlow.apply(fin);
            Object data = b.data;
            if (data instanceof Vec2 p) {
                if (b.time < 150) {
                    Draw.alpha(fin * 0.4F);
                    Drawf.buildBeam(b.x, b.y, p.x, p.y, 24 * fin);
                }

                Draw.alpha(1);
                Draw.z(100);

                for(int i : Mathf.signs) {
                    float dx = EUGet.dx(b.x, 6, b.rotation() + (float)(90 * i));
                    float dy = EUGet.dy(b.y, 6, b.rotation() + (float)(90 * i));
                    Drawf.tri(dx, dy, 5, 24 * fin, b.rotation() + (float)(90 * i));
                }
            }

        }
    };

    public BlackHole(String name) {
        super(name);
    }

    public class BlackHoleBuild extends aimBulletTurret.aimBulletBuild {
        public Bullet[] pointers = new Bullet[4];
        public Vec2 shootVec = new Vec2();
        

        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
            super.bullet(type, xOffset, yOffset, angleOffset, mover);
            Arrays.fill(this.pointers, null);
        }

        protected void shoot(BulletType type) {
            super.shoot(type);
            if (aim != null) {
                this.shootVec.set(aim.x, aim.y);

                for(int i = 0; i < 4; ++i) {
                    this.pointers[i] = BlackHole.this.blackPionter.create(this, this.team, this.x, this.y, this.rotation - 180, -1, 1, 1, this.shootVec);
                    this.pointers[i].fdata = (float)i;
                }
            }

        }
    }
}
