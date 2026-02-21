package ExtraUtilities.worlds.blocks.effect;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

public class Doll extends Block {
    public float LEFT = 10;
    public float UP = 10;
    public float rx = 0,
            ry = -20,
            ryt = 4;
    public float dt  = 0.005f;
    public float scl = 2;

    public Sound s = Sounds.none;
    public float v = 3;

    public boolean exp = false;
    public float timerHash = 3 * 60;

    public TextureRegion base;

    public Doll(String name) {
        super(name);
        solid = false;
        update = true;
        configurable = true;
        hasShadow = customShadow = false;
        config(int[].class, (DollBuild tile, int[] ints) -> {
            tile.left = ints[0];
            tile.up = ints[1];
        });
        alwaysUnlocked = true;
    }

    @Override
    public void load() {
        super.load();

        base = Core.atlas.find(name + "-base");
    }

    @Override
    public boolean canBreak(Tile tile) {
        return true;
    }

    public class DollBuild extends Building{
        public float left = 0,
                up = 0;
        public float timer = 0;

        @Override
        public void updateTile() {
            left = Mathf.lerpDelta(left, 0, dt * LEFT/10);
            up = Mathf.lerpDelta(up, 0, dt * UP/10);
            timer = Mathf.lerpDelta(timer, 0, 0.01f);
            if(Mathf.equal(left, 0, 0.01f)) left = 0;
            if(Mathf.equal(up, 0, 0.01f)) up = 0;
            if(!exp) return;
            if(Mathf.equal(up, UP, 0.1f) || Mathf.equal(left, LEFT, 0.1f)){
                timer += Time.delta * 10;
            }
            if(timer > timerHash){
                if(!Vars.headless) s.stop();
                Call.buildDestroyed(this);
            }
        }

        @Override
        public void draw() {
            Draw.rect(base, x, y);
            float sin = Mathf.sin(scl, 1);
            float dx = x + rx,
                    dy = y + ry + ryt + Math.abs(sin) * up;
            float fx = EUGet.dx(dx, Math.abs(ry), left * sin + 90),
                    fy = EUGet.dy(dy, Math.abs(ry), left * sin + 90);
            Draw.z(Layer.blockBuilding + 1);
            Drawf.shadow(Core.atlas.find(name), fx - 2, fy - 2, left * sin);
            Draw.rect(Core.atlas.find(name), fx, fy, left * sin);
            if(!exp) return;
            Draw.color(Color.white);
            Fill.rect(x, y - size * tilesize/2f, size * tilesize, size/2f);
            Draw.color(Color.red);
            Fill.rect(x, y - size * tilesize/2f, size * tilesize *  timer/timerHash, size/2f);
        }

        @Override
        public void buildConfiguration(Table table) {
            configure(new int[]{(int) LEFT, (int) UP});
            deselect();
            if(s == Sounds.none || Vars.headless) return;
            s.stop();
            s.at(x, y, 1, v);
        }

        @Override
        public int[] config() {
            return new int[]{(int) LEFT, (int) UP};
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(left);
            write.f(up);
            write.f(timer);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            left = read.f();
            up = read.f();
            timer = read.f();
        }
    }
}

