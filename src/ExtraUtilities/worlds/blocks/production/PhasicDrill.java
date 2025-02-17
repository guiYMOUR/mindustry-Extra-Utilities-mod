package ExtraUtilities.worlds.blocks.production;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.ui.ItemDisplay;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Icosphere;
import arc.math.geom.MeshResult;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.Stat;

public class PhasicDrill extends Drill {
    public Item boostItem = Items.phaseFabric;
    public float consumeTime = 120;
    public float pop = 0.1f;
    public float popAfter = 0.5f;
    public int upTimes = 2;
    public float camHeight = 80;
    public Effect boostEffect = Fx.none;
    public PhasicDrill(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.abilities, t -> {
            t.row();
            t.table(Styles.grayPanel, nt -> {
                nt.add(Core.bundle.format("stat.doubleGet", upTimes, pop * 100)).pad(10).left();
                nt.table(p2 -> {
                    p2.right().defaults().padRight(3).left();
                    p2.add(Core.bundle.get("stat.doubleGetCan")).pad(8f).left();
                    p2.row();
                    p2.add(new ItemDisplay(boostItem, 1, consumeTime, true)).pad(10f).left();
                    p2.add(Core.bundle.format("stat.doubleGetBoost", (popAfter - pop) * 100)).left();
                }).growX().pad(5).padRight(15).padBottom(-5).row();
            }).growX().colspan(t.getColumns());
            t.row();
        });
    }

    @Override
    public void init() {
        if(boostItem == null) boostItem = Items.silicon;
        this.blockedItem = boostItem;
        boostEffect = EUFx.diffuse(size, boostItem.color, 30);

        itemCapacity = 20;
        super.init();
    }

    public class PhasicDrillBuild extends DrillBuild{
        public float consumeTimer = 0;
        public float boostWarm = 0;

        Vec2 tmpVec = new Vec2();
        MeshResult r = Icosphere.create(0);

        @Override
        public void updateTile() {
            int amountGet = dominantItem == null ? items.total() : items.get(dominantItem);
            for(int i = 0; i < amountGet; i++){
                dump(dominantItem);
            }


            if(dominantItem == null || dominantItem == boostItem) return;

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if(items.get(dominantItem) < itemCapacity && dominantItems > 0 && efficiency > 0){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

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

            if(dominantItems > 0 && progress >= delay && items.get(dominantItem) < itemCapacity){
                boolean hasBoost = (items.has(boostItem) && Mathf.chance(popAfter)) || (!items.has(boostItem) && Mathf.chance(pop));
                int up = hasBoost ? upTimes : 1;
                for(int i = 0; i < up; i++) {
                    offload(dominantItem);
                }


                progress %= delay;

                if(wasVisible) {
                    if(hasBoost) boostEffect.at(x, y);
                    else if (Mathf.chanceDelta(updateEffectChance * warmup))
                        drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }

            if(items.has(boostItem)) {
                boostWarm = Mathf.lerpDelta(boostWarm, 1, 0.05f);
                consumeTimer += edelta();
                if (consumeTimer >= consumeTime) {
                    items.remove(boostItem, 1);
                    consumeTimer -= consumeTime;
                }
            } else {
                boostWarm = Mathf.lerpDelta(boostWarm, 0, 0.05f);
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return item == boostItem && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean shouldConsume(){
            return dominantItem != null && items.get(dominantItem) < itemCapacity && enabled;
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0.01f && dominantItem != null && items.get(dominantItem) < itemCapacity;
        }

        @Override
        public void draw() {

            //私下定了，应该不会给其他类用我就直接在这里draw了，要是其他类用了再做drawer

            float w = Math.min(1, warmup);

            Draw.rect(region, x, y);

            Draw.z(Layer.blockCracks);
            drawDefaultCracks();

            Draw.z(Layer.blockAfterCracks);
            Lines.stroke(2f * w, boostItem.color);
            Lines.poly(x, y, 6, 6, -timeDrilled * rotateSpeed);
            Draw.reset();

            if(drawSpinSprite){
                Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            }else{
                Draw.rect(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            }

            Draw.rect(topRegion, x, y);

            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }

            w *= boostWarm;
            if(w < 1e-3f) return;
            float z = Draw.z();
            Draw.color(boostItem.color);
            for (int i = 0; i < r.indices.size - 1; i+=3){
                Vec3 v31 = Tmp.v31.set(r.vertices.items, r.indices.items[i] * 3).setLength(size * 5 * w);
                Vec3 v32 = Tmp.v32.set(r.vertices.items, r.indices.items[(i + 1)] * 3).setLength(size * 5 * w);
                Vec3 v33 = Tmp.v33.set(r.vertices.items, r.indices.items[(i + 2)] * 3).setLength(size * 5 * w);

                v31.rotate(Vec3.Y, timeDrilled * .8f);
                v32.rotate(Vec3.Y, timeDrilled * .8f);
                v33.rotate(Vec3.Y, timeDrilled * .8f);

                v31.add(x, y, 0f);
                v32.add(x, y, 0f);
                v33.add(x, y, 0f);

                int amount = 3;
                for (int k = 1; k <= amount; k++) {
                    Vec3 vz1 = Tmp.v34.set(v31).lerp(v32, 1f / (amount + 1) * k);
                    Vec2 v = calculation(vz1);
                    Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + vz1.z, Layer.blockOver)));
                    Fill.circle(v.x, v.y, Math.max(0.1f, (vz1.z) / 8f * 0.5f * w));

                    vz1 = Tmp.v34.set(v32).lerp(v33, 1f / (amount + 1) * k);
                    v = calculation(vz1);
                    Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + vz1.z, Layer.blockOver)));
                    Fill.circle(v.x, v.y, Math.max(0.1f, (vz1.z) / 8f * 0.5f * w));


                    vz1 = Tmp.v34.set(v33).lerp(v31, 1f / (amount + 1) * k);
                    v = calculation(vz1);
                    Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + vz1.z, Layer.blockOver)));
                    Fill.circle(v.x, v.y, Math.max(0.1f, (vz1.z) / 8f * 0.5f * w));

                }


                Vec2 v1 = calculation(v31);
                Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + v31.z, Layer.blockOver)));
                Fill.circle(v1.x, v1.y, Math.max(0.1f, (v31.z) / 8 * 0.55f * w));


                Vec2 v2 = calculation(v32);
                Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + v32.z, Layer.blockOver)));
                Fill.circle(v2.x, v2.y, Math.max(0.1f, (v32.z) / 8 * 0.55f * w));

                Vec2 v3 = calculation(v33);
                Draw.z(Math.max(Layer.block - 0.01f, Math.min(Layer.block + v33.z, Layer.blockOver)));
                Fill.circle(v3.x, v3.y, Math.max(0.1f, (v33.z) / 8 * 0.55f * w));
            }
            Draw.z(z);
        }

        public Vec2 calculation(Vec3 vec){
            Posc c = Vars.player;
            float z = vec.z / 8f;
            float s = z / (z - getCamHeight());
            return tmpVec.set(s,s).scl(c.x() - vec.x, c.y() - vec.y).add(vec.x, vec.y);
        }
        float getCamHeight(){
            return camHeight / Vars.renderer.getDisplayScale();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(consumeTimer);
            write.f(boostWarm);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                consumeTimer = read.f();
                boostWarm = read.f();
            }
        }
    }
}
