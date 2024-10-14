package ExtraUtilities.content;

import ExtraUtilities.ExtraUtilitiesMod;
import arc.Core;
import arc.func.Cons;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.PixmapTextureData;
import arc.math.geom.Position;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.noise.Noise;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.Mover;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Iconc;
import mindustry.gen.Velc;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static mindustry.Vars.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.Items.*;

/**unfinished*/

public class EUGet {
    public static Color rainBowRed = Color.valueOf("ff8787");
    public static Color MIKU = Color.valueOf("39c5bb");

    public static Color EC1 = new Color();
    public static Color EC2 = new Color();
    public static Color EC3 = new Color();
    public static Color EC4 = new Color();
    public static Color EC5 = new Color();
    public static Color EC6 = new Color();
    public static Color EC7 = new Color();
    public static Color EC8 = new Color();
    public static Color EC9 = new Color();
    public static Color EC10 = new Color();
    public static Color EC11 = new Color();
    public static Color EC12 = new Color();
    public static Color EC13 = new Color();
    public static Color EC14 = new Color();
    public static Color EC15 = new Color();
    public static Color EC16 = new Color();
    public static Color EC17 = new Color();
    public static Color EC18 = new Color();
    public static Color EC19 = new Color();
    public static Color EC20 = new Color();
    public static Color EC21 = new Color();

    public static boolean breakStart = false;

    public static void setBreakStart(boolean breakStart) {
        EUGet.breakStart = breakStart;
    }

    //use for pool
    public static class EPos implements Position{
        public float x, y;

        public EPos set(float x, float y){
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }
    }

    //only for once usage
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Position pos(float x, float y){
        return new Position() {
            @Override
            public float getX() {
                return x;
            }

            @Override
            public float getY() {
                return y;
            }
        };
    }

    public static float dx(float px, float r, float angle){
        return px + r * (float) Math.cos(angle * Math.PI/180);
    }

    public static float dy(float py, float r, float angle){
        return py + r * (float) Math.sin(angle * Math.PI/180);
    }

    public static float posx(float x, float length, float angle){
        float a = (float) ((Math.PI * angle)/180);
        float cos = (float) Math.cos(a);
        return x + length * cos;
    }
    public static float posy(float y, float length, float angle){
        float a = (float) ((Math.PI * angle)/180);
        float sin = (float) Math.sin(a);
        return y + length * sin;
    }

    public static boolean isInstanceButNotSubclass(Object obj, Class<?> clazz) {
        if (clazz.isInstance(obj)) {
            try {
                if (getClassSubclassHierarchy(obj.getClass()).contains(clazz)) {
                    return false;
                }
            } catch (ClassCastException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static Seq<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
        Class<?> c = clazz.getSuperclass();
        Seq<Class<?>> hierarchy = new Seq<>();
        while (c != null) {
            hierarchy.add(c);
            Class<?>[] interfaces = c.getInterfaces();
            hierarchy.addAll(Arrays.asList(interfaces));
            c = c.getSuperclass();
        }
        return hierarchy;
    }

    public static Seq<Turret> turrets(){
        Seq<Turret> turretSeq = new Seq<>();
        int size = content.blocks().size;
        for(int i = 0; i < size; i++){
            Block b = content.block(i);
            if(b instanceof Turret){
                turretSeq.addUnique((Turret) b);
            }
        }
        return turretSeq;
    }

    /**turret and unit only, not use contents.bullets()*/
    public static Seq<BulletType> bulletTypes(){//use item
        Seq<BulletType> bullets = new Seq<>();
        for(Turret t : turrets()){
            if(t instanceof ItemTurret){
                for(Item i : ((ItemTurret) t).ammoTypes.keys()){
                    BulletType b = ((ItemTurret) t).ammoTypes.get(i);
                    if(t.shoot.shots == 1 || b instanceof PointBulletType || b instanceof ArtilleryBulletType){
                        bullets.add(b);
                    } else {
                        BulletType bulletType = new BulletType() {{
                            fragBullet = b;
                            fragBullets = t.shoot.shots;
                            fragAngle = 0;
                            if (t.shoot instanceof ShootSpread) {
                                fragSpread = ((ShootSpread) (t.shoot)).spread;
                            }
                            fragRandomSpread = t.inaccuracy;
                            fragVelocityMin = 1 - t.velocityRnd;
                            absorbable = hittable = collides = collidesGround = collidesAir = false;
                            despawnHit = true;
                            lifetime = damage = speed = 0;
                            hitEffect = despawnEffect = Fx.none;
                        }};
                        bullets.add(bulletType);
                    }
                }
            }
        }
        return bullets;
    }

    //use for cst bullet
    public static Bullet anyOtherCreate(Bullet bullet, BulletType bt, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY){
        bullet.type = bt;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if(!(aimX == -1f && aimY == -1f)){
            bullet.aimTile = world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, bt.speed * velocityScl);
        if(bt.backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = bt.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = bt.drag;
        bullet.hitSize = bt.hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? bt.damage : damage) * bullet.damageMultiplier();
        //reset trail
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(bt.keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc)owner).vel());

        return bullet;
    }

    public static void liquid(ObjectMap<Integer, Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        for (int i = 1 ; i < 10 ; i++){
            int index = i;
            var l = new Liquid(name + index, color){{
                explosiveness = exp * index;//爆炸性
                flammability = fla * index;//燃烧性
                heatCapacity = htc * index;//比热容
                viscosity = vis * index;//粘度
                temperature = temp / index;//温度
            }};
            if(cons != null && cons.size > 0 && cons.containsKey(i)){
                cons.get(i).get(l);
            }
        }
    }
    public static void liquid(String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        liquid(null, name, color, exp, fla, htc, vis, temp);
    }

    public static void item(ObjectMap<Integer, Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
        for (int i = 1 ; i < 10 ; i++){
            int index = i;
            var item = new Item(name + index, color){{
                explosiveness = exp * index;//爆炸性
                flammability = fla * index;//燃烧性
                cost = cos * index;
                radioactivity = radio * index;
                charge = chg * index;
                healthScaling = health * index;
            }};
            if(cons != null && cons.size > 0 && cons.containsKey(i)){
                cons.get(i).get(item);
            }
        }
    }

    /**
     * @author guiY
     * 1. 不能使用{@link Vars#content}
     * 2. 不能再init用（
    * */
    public static void test(){
        //其实有cons完全可以不用后面的哪些定义
//        //自定义
//        ObjectMap<Integer, Cons<Liquid>> cons = new ObjectMap<>();
//        //将id为1的液体颜色改为白色
//        cons.put(1, l -> l.color = Color.white);
//        //将id为2的液体燃烧性和爆炸性改为0， 温度改为0.1；
//        cons.put(2, l -> {
//            l.explosiveness = l.flammability = 0;
//            l.temperature = 0.1f;
//        });
//        liquid(cons,"t1", "111111", 1, 1, 1, 1, 1);
//
//        //默认定义
//        liquid("t2", "222222", 1, 1, 1, 1, 1);
        //数字大小
        int size = 40;
        for(var l : new Liquid[]{water, slag, oil, cryofluid,
                arkycite, gallium, neoplasm,
                ozone, hydrogen, nitrogen, cyanogen}){
            if(l.hidden) continue;
            ObjectMap<Integer, Cons<Liquid>> cons = new ObjectMap<>();
            for(int i = 1; i < 10; i++){
                int finalI = i;
                cons.put(i, ld -> {
                    PixmapRegion base = Core.atlas.getPixmap(l.uiIcon);
                    var mix = base.crop();
                    var number = Core.atlas.find(name("number-" + finalI));
                    if(number.found()) {
                        PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                        mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                    }

                    ld.uiIcon = ld.fullIcon = new TextureRegion(new Texture(mix));
                });
            }
            liquid(cons, l.name, l.color, l.explosiveness, l.flammability, l.heatCapacity, l.viscosity, l.temperature);
        }

        for(var item : new Item[]{scrap, copper, lead, graphite, coal, titanium, thorium, silicon, plastanium,
                phaseFabric, surgeAlloy, sporePod, sand, blastCompound, pyratite, metaglass,
                beryllium, tungsten, oxide, carbide, fissileMatter, dormantCyst}){
            if(item.hidden) continue;
            ObjectMap<Integer, Cons<Item>> cons = new ObjectMap<>();
            for(int i = 1; i < 10; i++){
                int finalI = i;
                cons.put(i, it -> {
                    PixmapRegion base = Core.atlas.getPixmap(item.uiIcon);
                    var mix = base.crop();
                    var number = Core.atlas.find(name("number-" + finalI));
                    if(number.found()) {
                        PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                        mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                    }

                    it.uiIcon = it.fullIcon = new TextureRegion(new Texture(mix));

                    it.buildable = item.buildable;
                    it.hardness = item.hardness + finalI;
                    it.lowPriority = item.lowPriority;
                });
            }
            item(cons, item.name, item.color, item.explosiveness, item.flammability, item.cost, item.radioactivity, item.charge, item.healthScaling);
        }
        Draw.color();
    }
}
