package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.Item;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.*;
import static ExtraUtilities.content.EUGet.*;

public class DrawLA extends DrawBlock {
    public Color color;
    public float move;

    public DrawLA(Color color, float move){
        this.color = color;
        this.move = move;
        this.lightBullet.trailColor = color;
    }
    public static void drawBeam(GenericCrafterBuild build, int id, float x, float y, float rotation, float length, float strength, float beamWidth, Vec2 lastEnd, Vec2 offset, Color laserColor){
        rand.setSeed(build.id + id);
        float
                originX = x + Angles.trnsx(rotation, length),
                originY = y + Angles.trnsy(rotation, length);

        lastEnd.set(build).sub(originX, originY);
        lastEnd.setLength(Math.max(2f, lastEnd.len()));

        lastEnd.add(offset.trns(
                rand.random(360f) + Time.time/2f,
                Mathf.sin(Time.time + rand.random(200f), 15f/Math.max(build.efficiency, 0.00001f), rand.random(0.2f*tilesize, 0.7f * tilesize))
        ).rotate(0));

        lastEnd.add(originX, originY);

        if(strength > 0.001f){
            Draw.alpha(strength);
            Lines.stroke(beamWidth * strength, laserColor);
            Lines.line(originX, originY, lastEnd.x, lastEnd.y);
            Fill.circle(lastEnd.x, lastEnd.y, 0.7f);
            Draw.color();
        }
    }

    @Override
    public void draw(Building build) {
        GenericCrafter block = (GenericCrafter) build.block;
        GenericCrafterBuild b = (GenericCrafterBuild) build;
        realDraw(block, b);
    }

    public void realDraw(GenericCrafter block, GenericCrafterBuild build){
        float bx = build.x, by = build.y;

        Item item = block.outputItem.item;
        Draw.alpha(build.progress * 1.2f);
        Draw.rect(item.uiIcon, bx, by);
        float sin = Mathf.sin(build.totalProgress, 30f/Math.max(build.efficiency, 0.00001f), 6f);
        Lines.stroke(build.warmup);
        Draw.color(color);
        //Draw.z(Layer.effect);
        for(int i = -1; i <= 1; i++){
            if(i == 0)continue;
            Lines.lineAngleCenter(bx + i * sin, by, 90, 7);
            Lines.lineAngleCenter(bx, by + i * sin, 0, 7);
        }
        if(move == 0) move = block.size / 2f * tilesize;
        Lines.stroke(1f);
        if(build.efficiency > 0){
            for(float mx : new float[]{move, -move}){
                for(float my : new float[]{move, -move}) {
                    Draw.z(Layer.buildBeam);
                    Draw.alpha(Math.max(Mathf.absin(Time.time, 6, 1f) * build.warmup(), 0.9f));
                    float x = bx + mx, y = by + my;
                    Drawf.buildBeam(x, y, bx, by, tilesize * 0.9f / 2f);
                }
            }
            //int i = -1;
            Draw.alpha(build.warmup);
            for(float mx : new float[]{move, -move}){
                for(float my : new float[]{move, -move}) {
                    //i++;
                    Draw.color(color);
                    Draw.z(Layer.effect);
                    float x = bx + mx, y = by + my;
                    Fill.circle(x, y, 1);
                    drawBeam(build, 0, x, y, Angles.angle(x, y, bx, by), build.dst(x, y) * 2, build.warmup, 0.8f, new Vec2(), new Vec2(), color);
                }
            }
            if(build.wasVisible && !state.isPaused() && Mathf.chance(block.updateEffectChance)){
                float range = block.size * tilesize/2f;
                float x1 = bx + Mathf.random(-range, range),
                        x2 = bx + Mathf.random(-range, range),
                        y1 = by + Mathf.random(-range, range),
                        y2 = by + Mathf.random(-range, range);
                Fx.chainLightning.at(x1, y1, 0, color, pos(x2, y2));
                if(Mathf.chance(0.1f)){
                    lightBullet.create(build, build.team, bx, by, Mathf.random(360f), -1, 1, 1, pos(bx, by));
                    Sounds.malignShoot.at(bx, by, 0.6f, 0.5f);
                }
            }
        }
        Draw.color();
        //Draw.z();
    }
    public BulletType lightBullet = new BulletType(){{
        collides = false;
        collidesAir = collidesGround = false;
        absorbable = false;
        hittable = false;
        lifetime = 240;
        speed = 5;
        trailLength = 15;
        trailWidth = 1.3f;
        despawnEffect = Fx.none;
    }
        @Override
        public void update(Bullet b) {
            super.update(b);
            //灾厄——创世纪
            if(b.time > 10 && b.time <= 30) b.rotation(b.rotation() + 18f * Time.delta);
            //控制间距（
            if(b.time > 33 && b.time <= 53) b.rotation(b.rotation() - 18f * Time.delta);
            if(b.time > 63 && b.data instanceof Position pos){
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(pos), Time.delta * 6f));
                b.initVel(b.rotation(), speed + b.dst(pos)/40f);
                if(b.within(pos, tilesize)) b.remove();
            }
            //整活er
            //if(b.time > 50) b.time = 10;
        }

        @Override
        public void drawTrail(Bullet b) {
            if(trailLength > 0 && b.trail != null){
                float z = Draw.z();
                Draw.z(z - 0.0001f);
                b.trail.draw(EUGet.EC17.set(EUGet.rainBowRed).shiftHue(30 + b.time/4), trailWidth);
                //b.trail.draw(trailColor, trailWidth);
                Draw.z(z);
            }
        }
    };
}
