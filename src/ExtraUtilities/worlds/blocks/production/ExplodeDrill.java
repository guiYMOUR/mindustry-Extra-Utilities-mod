package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.ExtraUtilitiesMod;
import ExtraUtilities.content.EUFx;
import ExtraUtilities.worlds.drawer.DrawFunc;
import ExtraUtilities.worlds.meta.EUStatValues;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.struct.ObjectFloatMap;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.*;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.Drill;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

/**
 * 超级缝合怪！
 * 说实话这个不难理解甚至不难写
 * */

public class ExplodeDrill extends Drill {
    public float drillTimeBurst = 60f * 4.5f;
    public Interp speedCurve = Interp.pow2In;
    public float shake = 2f;
    public float invertedTime = 200f;

    public Color glowColor = Color.valueOf("bf92f9");

    public @Nullable ConsumeLiquidBase coolant;
    public float coolantMultiplier = 1f;

    public ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

    public TextureRegion bottom, top;
    public TextureRegion[] plasmaRegions;
    public int plasmas = 4;
    public float circleRange = 0f;
    public float stroke = 1.5f;

    //private Bloom bloom;

    public Sound burstSound = Sounds.laser;

    public ExplodeDrill(String name){
        super(name);

        hardnessDrillMultiplier = 0f;
        liquidBoostIntensity = 1f;
    }

    @Override
    public void load() {
        super.load();
        bottom = Core.atlas.find(name + "-bottom");
        plasmaRegions = new TextureRegion[plasmas];
        for(int i = 0; i < plasmaRegions.length; i++){
            plasmaRegions[i] = Core.atlas.find(name + "-plasma-" + i);
        }
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.drillSpeed, 60f / drillTimeBurst * size * size, StatUnit.itemsSecond);
        if(coolant != null){
            stats.add(Stat.booster, EUStatValues.stringBoosters(drillTime, coolant.amount, coolantMultiplier, false, l -> l.coolant && consumesLiquid(l), "stat.extra-utilities-upSpeed"));
        }
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar(ExtraUtilitiesMod.name("drillspeed"), (ExplodeDrillBuild e) ->
                new Bar(() -> Core.bundle.format("bar." + ExtraUtilitiesMod.name("drillspeed"), Strings.fixed(e.lastDrillSpeedBurst * 60 * e.timeScale(), 2)), () -> Pal.ammo, () -> e.warmupBurst));
    }

    public float getDrillTimeBurst(Item item){
        return drillTimeBurst / drillMultipliers.get(item, 1f);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{bottom, region};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(bottom, plan.drawx(), plan.drawy());
        Draw.rect(region, plan.drawx(), plan.drawy());
    }

    public class ExplodeDrillBuild extends DrillBuild{
        public float smoothProgress = 0f;
        public float invertTime = 0f;

        public float progressBurst;
        public float warmupBurst;
        public float timeDrilledBurst;
        public float lastDrillSpeedBurst;

        public boolean over = false;
        
        @Override
        public void updateTile() {
            if(dominantItem == null){
                return;
            }

            if(invertTime > 0f) invertTime -= delta() / invertedTime;

            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency * EFF();

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

                if(Mathf.chanceDelta(updateEffectChance * warmup))
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            }else{
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
                offload(dominantItem);

                progress %= delay;

                if(wasVisible) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }

            float drillTime = getDrillTimeBurst(dominantItem);

            smoothProgress = Mathf.lerpDelta(smoothProgress, progressBurst / (drillTime - 20f), 0.1f);

            if(items.total() <= itemCapacity - dominantItems && dominantItems > 0 && efficiency > 0){
                warmupBurst = Mathf.approachDelta(warmupBurst, progressBurst / drillTime, 0.01f);

                float speed = efficiency * EFF() * EFF();

                timeDrilledBurst += speedCurve.apply(progressBurst / drillTime) * speed;

                lastDrillSpeedBurst = 1f / drillTime * speed * dominantItems;
                progressBurst += delta() * speed;
            }else{
                warmupBurst = Mathf.approachDelta(warmupBurst, 0f, 0.01f);
                lastDrillSpeedBurst = 0f;
                return;
            }

            if(dominantItems > 0 && progressBurst >= drillTime && items.total() < itemCapacity){
                for(int i = 0; i < dominantItems; i++){
                    offload(dominantItem);
                }

                invertTime = 1f;
                progressBurst %= drillTime;

                if(wasVisible){
                    Effect.shake(shake, shake, this);
                    burstSound.at(this);
                    //到时候在独立？
                    for(int i = 0; i < 6; i++) {
                        float rx = x + Mathf.range(size * size), ry = y + Mathf.range(size * size);
                        EUFx.gone(dominantItem.color, 10, 5).at(rx, ry);
                        Fx.chainLightning.at(rx, ry, 0, glowColor, this);
                    }
                }
            }
        }

        @Override
        public void draw() {
            Draw.rect(bottom, x, y);
            Draw.blend(Blending.additive);
            for(int i = 0; i < plasmaRegions.length; i++){
                float r = ((float)plasmaRegions[i].width * 1.2f * Draw.scl - 3f + Mathf.absin(Time.time, 2f + i * 1f, 5f - i * 0.5f));

                Draw.color(dominantItem.color, glowColor, (float)i / plasmaRegions.length);
                Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)) * warmup);
                Draw.rect(plasmaRegions[i], x, y, r, r, totalProgress()/2 * (12 + i * 6f));
            }
            Draw.color();
            Draw.blend();
            Draw.rect(region, x, y);
            Draw.color(dominantItem.color);
            Draw.rect(itemRegion, x, y);
            Draw.z(Layer.bullet-0.01f);
            Lines.stroke(stroke);
            DrawFunc.circlePercent(x, y, circleRange > 0 ? circleRange : size * size, warmup, 135f);
            Draw.color(glowColor);
            DrawFunc.circlePercent(x, y, circleRange > 0 ? circleRange : size * size, Math.min(warmupBurst, warmup), 135f);
            Draw.color();
        }

        public float EFF() {
            Liquid liquid = liquids.current();
            return (liquid.heatCapacity - Liquids.water.heatCapacity + 1) * coolantMultiplier;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progressBurst);
            write.f(warmupBurst);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                progressBurst = read.f();
                warmupBurst = read.f();
            }
        }
    }
}
