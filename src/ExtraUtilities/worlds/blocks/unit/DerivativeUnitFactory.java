package ExtraUtilities.worlds.blocks.unit;

import ExtraUtilities.content.EUFx;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.EnumSet;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BlockFlag;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.*;

public class DerivativeUnitFactory extends UnitFactory {
    public int areaSize = 14;
    public Effect aboveEffect = new Effect(24, e -> {
        color(e.color);
        Fill.circle(e.x, e.y, e.rotation * e.fout());
    });


    public DerivativeUnitFactory(String name) {
        super(name);

        ambientSound = Sounds.loopBio;
        ambientSoundVolume = 0.1f;
        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override
    public void init() {
        super.init();
        for(UnitPlan plan : plans){
            areaSize = Math.max((int) plan.unit.hitSize/tilesize, areaSize);
        }
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        rect.setCentered(x, y, areaSize * tilesize);
        float len = tilesize * (areaSize + size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        //same as UnitAssembler
        Rect rect = getRect(Tmp.r1, tile.worldx() + offset, tile.worldy() + offset, rotation).grow(0.1f);
        return !indexer.getFlagged(team, BlockFlag.factory).contains(b -> b instanceof DerivativeBuild && getRect(Tmp.r2, b.x, b.y, b.rotation).overlaps(rect));
    }

    public class DerivativeBuild extends UnitFactoryBuild{
        public Vec2 v1 = new Vec2();
        public Vec2 v2 = new Vec2();
        public Vec2 offset = new Vec2(), end = new Vec2();

        private final Object[] objects = new Object[4];
        private final Effect espEffect = EUFx.edessp(24);

        public Vec2 getUnitSpawn(){
            float len = tilesize * (areaSize + size)/2f;
            float unitX = x + Geometry.d4x(rotation) * len, unitY = y + Geometry.d4y(rotation) * len;
            v2.set(unitX, unitY);
            return v2;
        }

        @Override
        public void updateTile() {
            if(!configurable){
                currentPlan = 0;
            }

            if(currentPlan < 0 || currentPlan >= plans.size){
                currentPlan = -1;
            }

            if(efficiency > 0 && currentPlan != -1){
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            moveOutPayload();

            if(currentPlan != -1 && payload == null){
                UnitPlan plan = plans.get(currentPlan);

                //make sure to reset plan when the unit got banned after placement
                if(plan.unit.isBanned()){
                    currentPlan = -1;
                    return;
                }

                if(progress >= plan.time){
                    progress %= 1f;

                    Unit unit = plan.unit.create(team);
                    if(unit.type != null) {
                        Vec2 v = getUnitSpawn();
                        float dst = v.dst(this);
                        float a = angleTo(v);
                        objects[0] = unit.type.fullIcon;
                        objects[1] = dst;
                        objects[2] = 90f * rotation - 90f;
                        objects[3] = 180f;
                        espEffect.lifetime = 24/(timeScale + 0.001f);
                        espEffect.at(x, y, a, objects);
                    }
                    if(commandPos != null && unit.isCommandable()){
                        unit.command().commandPosition(commandPos);
                    }
                    payload = new UnitPayload(unit);
                    payVector.setZero();
                    consume();
                    Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                }

                progress = Mathf.clamp(progress, 0, plan.time);
            }else{
                progress = 0f;
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            Draw.rect(topRegion, x, y);

            Vec2 v = getUnitSpawn();
            float z = Draw.z();
            if(currentPlan != -1) {
                UnitPlan plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () -> Drawf.construct(v.x, v.y, plan.unit.fullIcon, rotdeg() - 90f, progress / plan.time + 0.05f, speedScl, time));
                if(efficiency > 0.001f) {
                    Draw.color(Pal.accent);
                    Draw.z(Layer.buildBeam);
                    Fill.circle(x, y, 3 * efficiency * speedScl);
                    Drawf.buildBeam(x, y, v.x, v.y, plan.unit.hitSize / 2f * efficiency * speedScl);

                    if(plan.unit != null) {
                        Draw.z(Layer.effect);
                        Fill.circle(x, y, 1.8f * efficiency * speedScl);
                        Lines.stroke(2.5f * efficiency * speedScl);
                        for(int i = 1; i <= 3; i++){
                            end.set(v).sub(x, y);
                            end.setLength(Math.max(2f, end.len()));
                            end.add(offset.trns(
                                    time/2 + 60 * i,
                                    Mathf.sin(time * 2 + 30 * i, 50f, plan.unit.hitSize * 0.6f)
                            ));
                            end.add(x, y);
                            Lines.line(x, y, end.x, end.y);
                            aboveEffect.at(end.x, end.y, 2, Pal.accent);
                            if(!state.isPaused() && Mathf.chance(0.01f)) {
                                Fx.hitLancer.at(end);
                                Sounds.shootArc.at(end.x, end.y, 0.5f, 0.3f);
                            }
                        }
                        Draw.color(team.color);
                        Lines.arc(v.x, v.y, plan.unit.hitSize * 1.2f, 1 - progress / plan.time, rotation * 90);
                        control.sound.loop(ambientSound, self(), ambientSoundVolume * speedScl * efficiency);
                        for(int i = 0; i < 2; i++){
                            float rot = rotation * 90 - 90 + 180 * i;
                            float ax = v.x + Angles.trnsx(rot, plan.unit.hitSize * 1.1f);
                            float ay = v.y + Angles.trnsy(rot, plan.unit.hitSize * 1.1f);
                            for(int a = 0; a < 3; a++){
                                float sin = Math.max(0, Mathf.sin(time + a * 60f, 55f, 1f)) * speedScl;
                                Draw.rect(
                                        Core.atlas.find(name("aim-shoot")),
                                        ax + Angles.trnsx(rot + 180, -4) * (tilesize / 2f + a * 2.8f),
                                        ay + Angles.trnsy(rot + 180, -4) * (tilesize / 2f + a * 2.8f),
                                        45f * sin,
                                        45f * sin,
                                        rot + 90
                                );
                            }
                        }
                    }
                }
            }

            Draw.z(z);
            Draw.reset();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashRect(Pal.accent, getRect(Tmp.r1, x, y, rotation));
        }
    }
}
