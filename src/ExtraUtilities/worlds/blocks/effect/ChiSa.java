package ExtraUtilities.worlds.blocks.effect;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUStatusEffects;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class ChiSa extends Doll{
    public float reloadTime = 12 * 60f;
    public float range = 30 * 8;

    public Effect applyE = new Effect(12, e -> {
        Draw.color(Pal.remove, Pal.sap, e.finpow());
        Lines.stroke(6.1803f * e.foutpow());
        float a = Mathf.randomSeed(e.id, 360f);
        Lines.poly(e.x, e.y, 3, range * e.finpow() * 2/3f, a);
        Lines.poly(e.x, e.y, 3, range * e.finpow() * 1/3f, a);
        Lines.poly(e.x, e.y, 3, range * e.finpow(), a);
    });

    public Color color = Color.valueOf("a639b9");

    public ChiSa(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, range, Pal.accent);
    }

    public class ChiSaBuild extends DollBuild{
        public float reload = 0;
        public boolean inReload = false;
        boolean killed;
        float clearTimer = 0;
        boolean inClear = false;

        @Override
        public void damage(float damage) {
            linchpin();
            super.damage(damage);
        }

        public void linchpin(){
            killed = false;
            if(!inReload){
                applyE.at(x + 1f, y + 1f);
                Units.nearbyEnemies(team, x, y, range, u -> {
                    u.apply(EUStatusEffects.ullification, reloadTime * 2);
                    Sounds.shootEnergyField.at(u);
                    //我千咲的攻击力(2156) * 解弦之眼的技能倍率(35.79%)，取整
                    if(u.health <= 772) {
                        u.kill();
                        if(!inClear) killed = true;
                    } else {
                        u.health -= 772;
                    }

                    EUFx.numberJump.at(u.x, u.y, 0, color, "772");
                });

                Sounds.shootToxopidShotgun.at(this);
                if(!killed) inReload = true;
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(inReload){
                reload += Time.delta;
            }

            if(reload >= reloadTime){
                inReload = false;
                reload = 0;
            }

            if(inClear){
                clearTimer += Time.delta;
            }

            if(clearTimer >= 3 * 60f){
                inClear = false;
                clearTimer = 0;
            }
        }

        @Override
        public void draw() {
            super.draw();

            Draw.color(Pal.accent);
            float dx = x + size/2f * Vars.tilesize;
            float dy = y - size/2f * Vars.tilesize;

            Lines.stroke(2);
            Lines.lineAngle(dx, dy, 90, size * Vars.tilesize * reload/reloadTime);
            Draw.color();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(reload);
            write.bool(inReload);
            write.f(clearTimer);
            write.bool(inClear);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            reload = read.f();
            inReload = read.bool();
            clearTimer = read.f();
            inClear = read.bool();
        }
    }
}
