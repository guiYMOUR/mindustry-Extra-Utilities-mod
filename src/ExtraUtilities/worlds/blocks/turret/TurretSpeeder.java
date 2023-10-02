package ExtraUtilities.worlds.blocks.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class TurretSpeeder extends TurretResupplyPoint {
    public float reload = 60f;
    public float speedBoost = 1.5f;
    public Color baseColor = Color.valueOf("feb380");

    public TurretSpeeder(String name) {
        super(name);
        range = 13 * 8;
        canOverdrive = false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.speedIncrease, "+" + (int)(speedBoost * 100f - 100) + "%");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        drawPotentialLinks(x, y);
        drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Drawf.dashSquare(baseColor, x, y, range);
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range), b -> b instanceof Turret.TurretBuild && b.block.canOverdrive, t -> Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));
    }

    public class TurretSpeederBuild extends TurretResupplyPointBuild{
        public float heat, charge = Mathf.random(reload), smoothEfficiency;

        @Override
        public void updateTile() {
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range), b -> (b.block instanceof Turret && b.block.hasItems), b ->{
                if((!hasPower || power.status > 0.999) && sortItem != null && items.get(sortItem) > 0 && b.acceptItem(this, sortItem)){
                    b.handleItem(this, sortItem);
                    Fx.itemTransfer.at(x, y, 2, sortItem.color, b);
                    items.clear();
                }
            });

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if(charge >= reload){

                charge = 0f;
                indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range), other -> other instanceof Turret.TurretBuild && other.block.canOverdrive, other -> other.applyBoost(speedBoost * efficiency, reload + 1f));
            }
        }

        @Override
        public void draw(){
            super.draw();

            float f = 1f - (Time.time / 100f) % 1f;

            Draw.color(baseColor);
            Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
            Draw.rect(Core.atlas.find(name + "-top"), x, y);
            Draw.alpha(1f);
            Lines.stroke((2f * f + 0.1f) * heat);

            float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
            Lines.beginLine();
            for(int i = 0; i < 4; i++){
                Lines.linePoint(x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
                if(f < 0.5f) Lines.linePoint(x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
            }
            Lines.endLine(true);

            Draw.reset();
        }

        @Override
        public void drawConfigure() {
            Draw.color(Pal.accent);
            Lines.stroke(1.0F);
            Lines.square(this.x, this.y, (float)(this.block.size * 8) / 2.0F + 1.0F);
            Draw.reset();
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range), b -> (b.block instanceof Turret && b.block.hasItems), b -> {
                if(sortItem != null && b.acceptItem(this, sortItem)) Drawf.square(b.x, b.y, b.block.size * tilesize / 2f + 1, sortItem.color);
            });
        }

        @Override
        public void drawSelect(){
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range), other -> other instanceof Turret.TurretBuild && other.block.canOverdrive, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));

            Drawf.dashSquare(baseColor, x, y, range);
        }

        @Override
        public BlockStatus status() {
            if (!this.enabled) {
                return BlockStatus.logicDisable;
            } else if (!this.shouldConsume()) {
                return BlockStatus.noOutput;
            } else if (!(this.efficiency <= 0.0F) && this.productionValid()) {
                return Vars.state.tick / 30.0D % 1.0D < (double)this.efficiency ? BlockStatus.active : BlockStatus.noInput;
            } else {
                return BlockStatus.noInput;
            }
        }
    }
}
