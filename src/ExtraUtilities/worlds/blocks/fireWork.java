package ExtraUtilities.worlds.blocks;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;

import static ExtraUtilities.ExtraUtilitiesMod.name;


public class fireWork extends Block {
    Color[] colors = new Color[]{Color.valueOf("FF4B4B"), Color.valueOf("FEFF4A"), Color.valueOf("724AFF"), Color.valueOf("89C2FF"), Color.valueOf("39c5bb"), Color.white};
    public BulletType fireWorkBullet = new BulletType(){
        {
            damage = 0;
            collides = false;
            speed = 3;
            lifetime = 60;
            trailWidth = 2.2f;
            trailLength = 10;
            hittable = false;
            absorbable = false;
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            b.initVel(b.rotation(), speed * b.fout());
        }

        @Override
        public void despawned(Bullet b) {
            Sounds.unitExplode2.at(b);
            Color color = colors[(int)Mathf.random(0, colors.length - 0.1f)];
            if(Mathf.chance(0.4)){
                for(int i = 0; i < 15; i++){
                    miniFire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1,1,colors[(int)Mathf.random(0, colors.length - 0.1f)]);
                }
                return;
            }
            for(int i = 0; i < 15; i++){
                miniFire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1,1, color);
            }
            if(Mathf.chance(0.8)){
                int r = Mathf.random(1,4);
                switch (r) {
                    case 1 -> gui.create(b, b.team, b.x, b.y, 0, -1, 1, 1, color);
                    case 2 -> car.create(b, b.team, b.x, b.y, 0, -1, 1, 1, color);
                    default -> EU.create(b, b.team, b.x, b.y, 0, -1, 1, 1, color);
                }
            }
        }
        @Override
        public void drawTrail(Bullet b) {
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                b.trail.draw(EUGet.EC10.set(EUGet.rainBowRed).a(0.7f).shiftHue(b.time * 2), trailWidth);
                //b.trail.draw(trailColor, trailWidth);
                Draw.z(z);
            }
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            Draw.color(EUGet.EC11.set(EUGet.rainBowRed).shiftHue(b.time * 2));
            Lines.stroke(1);
            Lines.square(b.x, b.y, 5, b.rotation() + Time.time);
            Lines.square(b.x, b.y, 5, b.rotation() - Time.time);
            Draw.reset();
        }
    };

    public BulletType miniFire = new BulletType(){
        {
            damage = 0;
            collides = false;
            speed = 4;
            lifetime = 60;
            trailWidth = 2.2f;
            trailLength = 10;
            hitEffect = despawnEffect = Fx.none;
            despawnSound = Sounds.missile;
            hittable = false;
            absorbable = false;
        }
        @Override
        public void update(Bullet b) {
            super.update(b);
            b.initVel(b.rotation(), speed * Math.max(b.fout() - 0.5f, 0));
        }

        @Override
        public void draw(Bullet b) {
            if(!(b.data instanceof Color)) return;
            super.draw(b);
            Draw.color((Color)b.data == Color.white ? EUGet.EC12.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.z(Layer.bullet);
            for(int i = 0; i < 4; i++) {
                Drawf.tri(b.x, b.y, 2, 3, b.rotation() + 90 * i);
            }

            Draw.reset();
        }

        @Override
        public void drawTrail(Bullet b) {
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                b.trail.draw((Color)b.data == Color.white ? EUGet.EC13.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data, trailWidth);
                Draw.z(z);
            }
        }
    };

    public BulletType EU = new BulletType(){{
        damage = 0;
        collides = false;
        speed = 0;
        lifetime = 60;
        hitEffect = despawnEffect = Fx.none;
        despawnSound = Sounds.missile;
        hittable = false;
        absorbable = false;
    }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if(!(b.data instanceof Color)) return;
            Draw.z(Layer.bullet);
            Draw.color((Color)b.data == Color.white ? EUGet.EC14.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.rect(Core.atlas.find(name("fire-EU")), b.x, b.y,  96 * b.fout(), 96 * b.fout(), 0);
        }
    };

    public BulletType gui = new BulletType(){{
        damage = 0;
        collides = false;
        speed = 0;
        lifetime = 60;
        hitEffect = despawnEffect = Fx.none;
        despawnSound = Sounds.missile;
        hittable = false;
        absorbable = false;
    }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if(!(b.data instanceof Color)) return;
            Draw.z(Layer.bullet);
            Draw.color((Color)b.data == Color.white ? EUGet.EC15.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.rect(Core.atlas.find(name("fire-guiY")), b.x, b.y,  96 * b.fout(), 96 * b.fout(), 0);
        }
    };

    public BulletType car = new BulletType(){{
        damage = 0;
        collides = false;
        speed = 0;
        lifetime = 60;
        hitEffect = despawnEffect = Fx.none;
        despawnSound = Sounds.missile;
        hittable = false;
        absorbable = false;
    }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if(!(b.data instanceof Color)) return;
            Draw.z(Layer.bullet);
            Draw.color((Color)b.data == Color.white ? EUGet.EC16.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.rect(Core.atlas.find(name("fire-Carrot")), b.x, b.y,  96 * b.fout(), 96 * b.fout(), 0);
        }
    };
    public fireWork(String name) {
        super(name);
        update = true;
        solid = true;
    }
    public class fireWorkBuild extends Building{
        @Override
        public void updateTile() {
            if(timer.get(30)){
                fireWorkBullet.create(this, x, y, Mathf.random(360));
            }
        }
    }
}
