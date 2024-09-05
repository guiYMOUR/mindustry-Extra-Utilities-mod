package ExtraUtilities.graphics;

import ExtraUtilities.content.EUGet;
import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.struct.*;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import mindustry.input.Binding;

import static arc.Core.*;

public class MainRenderer{
    private final Seq<BlackHole> holes = new Seq<>();
    private static MainRenderer renderer;

    private FrameBuffer buffer;

    private static final float[][] initFloat = new float[510][];
    //private static final Pool<BlackHole> holePool = Pools.get(BlackHole.class, BlackHole::new);

    protected MainRenderer(){
        if(!Vars.headless) {
            MainShader.createShader();

            buffer = new FrameBuffer();
            Events.run(Trigger.draw, this::advancedDraw);
        }
    }

    public static void init(){
        if(renderer == null) renderer = new MainRenderer();
        for(int i = 0; i < 510; i++){
            initFloat[i] = new float[i * 4];
        }
    }

    public static void addBlackHole(float x, float y, float inRadius, float outRadius, float alpha){
        if(!Vars.headless) renderer.addHole(x, y, inRadius, outRadius, alpha);
    }
    public static void addBlackHole(float x, float y, float inRadius, float outRadius){
        if(!Vars.headless) renderer.addHole(x, y, inRadius, outRadius, 1);
    }

    private void advancedDraw(){
        Draw.draw(Layer.background - 1, () -> {
            buffer.resize(graphics.getWidth(), graphics.getHeight());
            buffer.begin();
        });

        Draw.draw(Layer.max - 1, () -> {
            buffer.end();

            if(holes.size >= 510) return;
            if(holes.size >= MainShader.MaxCont) MainShader.createShader();

            float[] blackholes = initFloat[holes.size];

            for(int i = 0; i < holes.size; i++){
                var hole = holes.get(i);
                blackholes[i * 4] = hole.x;
                blackholes[i * 4 + 1] = hole.y;
                blackholes[i * 4 + 2] = hole.inRadius;
                blackholes[i * 4 + 3] = hole.outRadius;

                Draw.color(Tmp.c2.set(Color.black).a(hole.alpha));
                Fill.circle(hole.x, hole.y, hole.inRadius * 1.5f);
                Draw.color();
            }
            MainShader.holeShader.blackHoles = blackholes;
            buffer.blit(MainShader.holeShader);

            buffer.begin();
            Draw.rect();
            buffer.end();

            //holePool.freeAll(holes);
            holes.clear();
        });
    }

    private void addHole(float x, float y, float inRadius, float outRadius, float alpha){
        if(inRadius > outRadius || outRadius <= 0) return;

        holes.add(Pools.obtain(BlackHole.class, BlackHole::new).set(x, y, inRadius, outRadius, alpha));
    }

    private static class BlackHole{
        float x, y, inRadius, outRadius, alpha;

        public BlackHole set(float x, float y, float inRadius, float outRadius, float alpha){
            this.x = x;
            this.y = y;
            this.inRadius = inRadius;
            this.outRadius = outRadius;
            this.alpha = alpha;
            return this;
        }

        public BlackHole(){

        }
    }

    private void BreakerDraw(){
        EUGet.setBreakStart(input.keyDown(Binding.control));
    }

    public void allDraw(){
        advancedDraw();
        BreakerDraw();
    }
}
