package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class FireWorkBullet extends BulletType {
    public String sprite;
    public boolean colorful = false;
    public boolean childColorful = true;
    public Color[] colors = new Color[]{Color.valueOf("FF4B4B"), Color.valueOf("FEFF4A"), Color.valueOf("724AFF"), Color.valueOf("89C2FF"), Color.valueOf("39c5bb"), Color.white};
    public Color color;

    public boolean outline = false;
    public float width = 15;
    public float height = 15;

    public boolean breaker = false;

    public int num = 30;

    public @Nullable BulletType fire;
    public @Nullable BulletType textFire = null;

    public TextureRegion ammo;

    public FireWorkBullet(float damage, float speed, String sprite, Color color, float rad){
        this.damage = damage;
        this.speed = speed;
        this.sprite = sprite;
        this.color = color;
        trailColor = color;
        trailInterval = 3;
        splashDamage = damage;
        splashDamageRadius = rad;
        hitEffect = despawnEffect = new ExplosionEffect(){{
            lifetime = 60f;
            waveStroke = 5f;
            waveLife = 8f;
            waveColor = Color.white;
            sparkColor = color;
            smokeColor = Pal.darkerGray;
            waveRad = rad;
            smokeSize = rad/8f;
            smokes = 7;
            smokeSizeBase = 0f;
            sparks = 10;
            sparkRad = rad;
            sparkLen = 6f;
            sparkStroke = 2f;
        }};
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
        despawnSound = hitSound = Sounds.unitExplode3;
        lifetime = 50;

        ammoMultiplier = 1;

        status = StatusEffects.blasted;
        statusDuration = 3 * 60f;

        fire = new colorFire(true);
    }

    public FireWorkBullet(float damage, float speed, Color color){
        this(damage, speed, name("mb-b"), color, 6 * 8);
    }

    public FireWorkBullet(float damage, float speed){
        this(damage, speed, name("mb-b"), Color.gray, 6 * 8);
    }

    @Override
    public void load() {
        super.load();

        ammo = Core.atlas.find(sprite);
    }

    @Override
    public void drawTrail(Bullet b) {
        if(trailLength > 0 && b.trail != null){
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(colorful ? EUGet.EC1.set(EUGet.rainBowRed).a(0.7f).shiftHue(b.time * 2) : color, trailWidth);
            Draw.z(z);
        }
    }

    @Override
    public void draw(Bullet b) {
        super.draw(b);
        if(outline){
            Draw.color(colorful ? EUGet.EC2.set(EUGet.rainBowRed).shiftHue(b.time * 2) : color);
            Draw.rect(ammo, b.x, b.y, width * 1.1f, height * 1.1f, b.rotation() - 90);
            Draw.color(Color.darkGray);
            Draw.rect(ammo, b.x, b.y, width * 0.8f, height * 0.8f, b.rotation() - 90);
        } else {
            Draw.color(colorful ? EUGet.EC2.set(EUGet.rainBowRed).shiftHue(b.time * 2) : color);
            Draw.rect(ammo, b.x, b.y, width, height, b.rotation() - 90);
        }
        Draw.reset();
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        super.hitEntity(b, entity, health);
        if(!pierce || b.collided.size >= pierceCap) explode(b);
    }

    @Override
    public void hit(Bullet b) {
        super.hit(b);
        explode(b);
    }

//    @Override
//    public void update(Bullet b) {
//        super.update(b);
//        if(!breaker) return;
//        Tile tile = Vars.world.tileWorld(b.x, b.y);
//        if(checkEvnWall(tile)){
//
//        }
//    }
//
//    private boolean checkEvnWall(Tile tile){
//        return tile != null && tile.block() != null && tile.build == null && tile.block().solid && !tile.block().breakable;
//    }
//
//    public void explodeWall(Bullet b) {
//
//    }

    public void explode(Bullet b) {
        if(fire == null) return;
        for(int i = 0; i < num; i++){
            if(colorful && childColorful){
                Color c = colors[Mathf.random(0, colors.length - 1)];
                fire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1, 1, c);
            } else fire.create(b, b.team, b.x, b.y, Mathf.random(360), -1, 1, 1, color);
        }
        if(textFire != null){
            if(colorful){
                Color c = colors[Mathf.random(0, colors.length - 1)];
                textFire.create(b, b.team, b.x, b.y, 0, -1, 1, 1, c);
            } else textFire.create(b, b.team, b.x, b.y, 0, -1, 1, 1, color);
        }
    }

    public static class colorFire extends BulletType{
        public boolean stop;
        public float stopFrom = 0.3f;
        public float stopTo = 0.6f;
        public float rotSpeed = 4f;
        public float speedRod = 1;

        public colorFire(boolean stop, float speed, float lifetime){
            this.stop = stop;
            damage = 0;
            collides = collidesAir = collidesGround = false;
            this.speed = speed;
            this.lifetime = lifetime;
            trailWidth = 1.7f;
            trailLength = 6;
            hitEffect = despawnEffect = Fx.none;
            hittable = reflectable = false;
            absorbable = false;
            keepVelocity = false;
        }

        public colorFire(boolean stop){
            this(stop, 5, 60);
        }

        @Override
        public void update(Bullet b) {
            super.update(b);
            if(stop) b.initVel(b.rotation(), speed * Math.max(b.fout() - Mathf.random(stopFrom, stopTo), 0) * Mathf.random(speedRod, 1));
            else{
                b.initVel(b.rotation(), speed * b.fout() * Mathf.random(speedRod, 1));
                b.rotation(Angles.moveToward(b.rotation(), -90, rotSpeed * Math.max(b.fin() - Mathf.random(stopFrom, stopTo), 0)));
            }
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if(!(b.data instanceof Color)) return;
            Draw.color(b.data == Color.white ? EUGet.EC18.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.z(Layer.bullet);
            for(int i = 0; i < 4; i++) {
                Drawf.tri(b.x, b.y, 1.6f, 2.2f, b.rotation() + 90 * i);
            }

            Draw.reset();
        }

        @Override
        public void drawTrail(Bullet b) {
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                b.trail.draw(b.data == Color.white ? EUGet.EC19.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data, trailWidth);
                Draw.z(z);
            }
        }
    }
    public static class spriteBullet extends BulletType{
        public String sprite;
        public float width;
        public float height;

        public TextureRegion string;

        public spriteBullet(String sprite, float width, float height){
            this.sprite = sprite;
            this.width = width;
            this.height = height;
            damage = 0;
            collides = collidesAir = collidesGround = false;
            speed = 0;
            lifetime = 60;
            hitEffect = despawnEffect = Fx.none;
            hittable = false;
            absorbable = false;
            reflectable = false;

            keepVelocity = false;
        }
        public spriteBullet(String sprite){
            this(sprite, 96, 96);
        }

        @Override
        public void load() {
            super.load();

            string = Core.atlas.find(sprite);
        }

        @Override
        public void draw(Bullet b) {
            super.draw(b);
            if(!(b.data instanceof Color)) return;
            Draw.z(Layer.bullet);
            Draw.color(b.data == Color.white ? EUGet.EC20.set(EUGet.rainBowRed).shiftHue(b.time * 2) : (Color)b.data);
            Draw.rect(string, b.x, b.y,  width * b.fout(), height * b.fout(), 0);
        }
    }
}
