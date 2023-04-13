package ExtraUtilities.worlds.blocks.turret.TowerDefence;

import arc.Core;
import arc.Graphics;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.logic.Ranged;
import mindustry.type.StatusEffect;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;

public class CrystalTower extends Block {
    public float range = 200;
    public float speedUp = 1.75f;
    public Color bloomColor = Color.valueOf("#bf92f9");

    public boolean ctrlColor = false;

    public CrystalTower(String name) {
        super(name);
        solid = true;
        noUpdateDisabled = false;
        configurable = true;
        drawTeamOverlay = false;
        update = true;
        canOverdrive = false;
        privileged = true;
        targetable = false;
        underBullets = true;
        breakable = false;
        forceDark = true;
        config(int[].class, (CrystalTowerBuild tile, int[] value) -> {
            if(!accessible()) return;
            tile.conf = value;
        });
    }

    public boolean accessible(){
        return !privileged || Vars.state.rules.editor || Vars.state.playtestingMap != null;
    }

    @Override
    public boolean checkForceDark(Tile tile){
        return !accessible();
    }

    @Override
    public boolean canBreak(Tile tile) {
        return accessible();
    }

    public class CrystalTowerBuild extends Building implements Ranged {
        public StatusEffect statusEffect = StatusEffects.none;
        public int[] conf = new int[]{Pal.accent.rgba(), StatusEffects.none.id};

        @Override
        public void updateTile() {
            if(Vars.content.statusEffects().get(conf[1]) == null){
                statusEffect = StatusEffects.none;
                conf[1] = StatusEffects.none.id;
            } else {
                statusEffect = Vars.content.statusEffects().get(conf[1]);
            }
            Vars.indexer.eachBlock(null, x, y, range, other -> other.block.canOverdrive, other -> other.applyBoost(speedUp, 61));
            Units.nearby(x - range, y - range, range * 2, range * 2, unit -> {
                if(team == Team.derelict || unit.team == team) {
                    if (unit.within(x, y, range + unit.hitSize / 2f) && statusEffect != null && statusEffect != StatusEffects.none) {
                        unit.apply(statusEffect, 30);
                    }
                }
            });
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(bloomColor);
            Draw.rect(Core.atlas.find(name + "-bloom"), x, y);
            Lines.stroke(2, Tmp.c1.set(conf[0]));
            Lines.square(x, y, 9, Time.time);
            Lines.square(x, y, 6, -Time.time);
            for(int i = 0; i < 5; i++){
                float rot = i * 360f/5 + Time.time * 0.8f;
                Lines.arc(x, y, range, 0.15f, rot);
            }
        }

        @Override
        public boolean collide(Bullet other){
            return !privileged;
        }

        @Override
        public boolean displayable(){
            return accessible();
        }

        @Override
        public void damage(float damage){
            if(!privileged){
                super.damage(damage);
            }
        }

        @Override
        public Graphics.Cursor getCursor(){
            return !accessible() ? Graphics.Cursor.SystemCursor.arrow : super.getCursor();
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        public float range(){
            return range;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.color){
                conf[0] = Tmp.c1.fromDouble(p1).rgba8888();
            }
            super.control(type, p1, p2, p3, p4);
        }

        public void setConf(StatusEffect statusEffect){
            conf[1] = statusEffect == null ? StatusEffects.none.id : statusEffect.id;
            configure(conf);
        }

        @Override
        public void buildConfiguration(Table table){
            if(!accessible() && !ctrlColor){
                deselect();
                return;
            }
            if(accessible()) {
                ItemSelection.buildTable(CrystalTower.this, table, Vars.content.statusEffects(), () -> {
                    if (Vars.content.statusEffects().get(conf[1]) == null) return StatusEffects.none;
                    else {
                        statusEffect = Vars.content.statusEffects().get(conf[1]);
                        return statusEffect;
                    }
                }, this::setConf, selectionRows, selectionColumns);
            }

            table.button(Icon.pencil, Styles.cleari, () -> {
                Vars.ui.picker.show(Tmp.c1.set(conf[0]).a(0.8f), false, res -> {
                    conf[0] = res.rgba();
                    configure(conf);
                });
                deselect();
            }).size(40f);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(this == other){
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public int[] config(){
            return conf;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(conf[0]);
            write.i(conf[1]);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            conf[0] = read.i();
            conf[1] = read.i();
        }
    }
}
