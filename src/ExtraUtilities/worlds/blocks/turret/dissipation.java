package ExtraUtilities.worlds.blocks.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.PointDefenseTurret;
import mindustry.world.meta.Stat;

import static ExtraUtilities.ExtraUtilitiesMod.name;

public class dissipation extends PointDefenseTurret {
    public float maxShot = 60 * 80;

    public dissipation(String name) {
        super(name);
        //noUpdateDisabled = false;
    }

    @Override
    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.reload);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("amount", (dissipationBuild entity) ->
                new Bar(() ->
                        Core.bundle.get("bar.extra-utilities-shotAmount"),
                        () -> entity.cooldown ? Pal.gray : Pal.accent,
                        entity::getShot
                )
        );
    }

    public class dissipationBuild extends PointDefenseBuild{
        float shot = 0;
        boolean cooldown = true;

        public float getShot() {
            return shot/maxShot;
        }

        @Override
        public void updateTile() {
            if(coolant == null) return;
            float e = power.status;
            if(shot <= 0 && !cooldown) cooldown = true;
            if(shot >= maxShot && cooldown) cooldown = false;
            if(cooldown){
                float maxUsed = coolant.amount;
                Liquid liquid = liquids.current();
                float used = Math.min(liquids.get(liquid), maxUsed * Time.delta) * e * timeScale;
                shot = liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 4 * (1 + (liquid.heatCapacity - 0.4f) * 0.9f) * coolantMultiplier * e * 0.28f * timeScale * Time.delta) : Math.min(maxShot, shot + 4 * e * timeScale * Time.delta);
                liquids.remove(liquid, used);
                if(Mathf.chance(0.06 * used)){
                    coolEffect.at(x + Mathf.range(block.size * Vars.tilesize / 2), y + Mathf.range(block.size * Vars.tilesize / 2));
                }
            } else {
                //cooldown = false;
                float maxUsed = coolant.amount;
                Liquid liquid = liquids.current();
                float used = Math.min(liquids.get(liquid), maxUsed * Time.delta) * e * timeScale;
                shot = liquids.get(liquid) > 1 ? Math.min(maxShot, shot + 8 * (1 + (liquid.heatCapacity - 0.4f) * 0.9f) * coolantMultiplier * 0.28f * timeScale * Time.delta * e) : Math.min(maxShot, shot + 8 * timeScale * Time.delta * e);
                target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != this.team && b.type.hittable, b -> b.dst2(this));
                if(target != null && !target.isAdded()){
                    target = null;
                }
                if(coolant != null && target != null){
                    liquids.remove(liquid, used);

                    if(Mathf.chance(0.06 * used)){
                        coolEffect.at(x + Mathf.range(block.size * Vars.tilesize / 2), y + Mathf.range(block.size * Vars.tilesize / 2));
                    }
                }
                if(e < 0.01f) return;
                if(this.target != null && target.within(this, range) && target.team != team && target.type != null && target.type.hittable){
                    float dest = angleTo(target);
                    this.rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta());
                    if(Angles.within(rotation, dest, shootCone)){
                        this.target.remove();
                        shot = Math.max(0, shot - 60);
                        Tmp.v1.trns(rotation, shootLength);

                        beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, new Vec2().set(target));
                        shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color);
                        hitEffect.at(target.x, target.y, color);
                        shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.f));
                    }
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color();
            Draw.alpha(cooldown ? 1 : 0);
            Draw.rect(Core.atlas.find(name("dissipation-low")), this.x, this.y, this.rotation - 90);
        }

        public boolean shouldConsume(){
            return super.shouldConsume() && (target != null || cooldown);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(shot);
            write.bool(cooldown);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            shot = read.f();
            cooldown = read.bool();
        }
    }
}
