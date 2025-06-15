package ExtraUtilities.content;

import ExtraUtilities.ExtraUtilitiesMod;
import arc.Core;
import arc.Events;
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
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.noise.Noise;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Mover;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static arc.Core.settings;
import static mindustry.Vars.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.Items.*;

/**unfinished*/

public class EUGet {
    public static int[] signsZero = {-1, 0, 1};

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

    //public static Rect r1 = new Rect();

    //因为有中文，所以用数字代替了
    public static String[] donors = {
            "冷冻液",
            "zhraa11",
            "标枪",
            "花杨永瀛",
            "灰尘庙",
            "维生素",
            "鱼鱼"
    };
    public static String[] developers = {
            "guiY",
            "carrot"
    };
    public static IntMap<Seq<UnlockableContent>> donorMap = new IntMap<>();
    public static IntMap<Seq<UnlockableContent>> developerMap = new IntMap<>();
    static {
        //冷冻液
        donorMap.put(0, new Seq<>());
        //zhraa11
        donorMap.put(1, new Seq<>());
        //标枪
        donorMap.put(2, new Seq<>());
        //花杨永瀛
        donorMap.put(3, new Seq<>());
        //灰尘庙
        donorMap.put(4, new Seq<>());
        //维生素
        donorMap.put(5, new Seq<>());
        //鱼鱼
        donorMap.put(6, new Seq<>());

        //guiY
        developerMap.put(0, new Seq<>());
        //carrot
        developerMap.put(1, new Seq<>());
    }
    public static Seq<UnlockableContent> donorItems = new Seq<>();
    public static Seq<UnlockableContent> developerItems = new Seq<>();
    private static final String DONOR = Core.bundle.get("mod.extra-utilities-donor-item");
    private static final String DEVELOPER = Core.bundle.get("mod.extra-utilities-developer-item");
    public static void loadItems(){
        //if(headless) return;
//        Events.run(EventType.Trigger.update, () -> {
//
//        });
        for(var c : donorItems){
            c.description = (c.description == null ? DONOR : c.description + "\n" + DONOR);
        }
        for(var c : developerItems){
            c.description = (c.description == null ? DEVELOPER : c.description + "\n" + DEVELOPER);
        }
    }


    //use for pool
    public static class EPos implements arc.math.geom.Position {
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

    public static float pointAngleX(float px, float angle, float dst){
        return (px + dst * Mathf.cosDeg(angle));
    }
    public static float pointAngleY(float py, float angle, float dst){
        return (py + dst * Mathf.sinDeg(angle));
    }

//    public static Position pos(float x, float y){
//        Position p = Pools.obtain(Position.class, Position::new);
//        p.setX(x);
//        p.setY(y);
//        return p;
//    }

    public static float dx(float px, float r, float angle){
        return px + r * (float) Math.cos(angle * Math.PI/180);
    }

    public static float dy(float py, float r, float angle){
        return py + r * (float) Math.sin(angle * Math.PI/180);
    }

    public static float txy(float px, float py, float a, float b, float theta, float angle, int xy){
        float x = a * Mathf.cosDeg(angle);
        float y = b * Mathf.sinDeg(angle);
        float xRotated = x * Mathf.cosDeg(theta) - y * Mathf.sinDeg(theta) + px;
        float yRotated = x * Mathf.sinDeg(theta) + y * Mathf.cosDeg(theta) + py;
        return xy == 0 ? xRotated : yRotated;
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
    public static Bullet anyOtherCreate(Bullet bullet, BulletType bt, Entityc shooter, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, @Nullable Teamc target){
        if(bt == null) return null;
        bullet.type = bt;
        bullet.owner = owner;
        bullet.shooter = (shooter == null ? owner : shooter);
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if(!(aimX == -1f && aimY == -1f)){
            bullet.aimTile = target instanceof Building b ? b.tile : world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, bt.speed * velocityScl * (bt.velocityScaleRandMin != 1f || bt.velocityScaleRandMax != 1f ? Mathf.random(bt.velocityScaleRandMin, bt.velocityScaleRandMax) : 1f));
        if(bt.backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = bt.lifetime * lifetimeScl * (bt.lifeScaleRandMin != 1f || bt.lifeScaleRandMax != 1f ? Mathf.random(bt.lifeScaleRandMin, bt.lifeScaleRandMax) : 1f);
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

        if(bt.keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
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

    public static ImageButton selfStyleImageButton(Drawable imageUp, ImageButton.ImageButtonStyle is, Runnable listener){
        ImageButton ib = new ImageButton(new ImageButton.ImageButtonStyle(null, null, null, imageUp, null, null));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(is);
        style.imageUp = imageUp;
        ib.setStyle(style);
        if(listener != null) ib.changed(listener);
        return ib;
    }

    public static void CollapseTextToTable(Table t, String text){
        Table ic = new Table();
        ic.add(text).wrap().fillX().width(500f).padTop(2).padBottom(6).left();
        ic.row();
        Collapser coll = new Collapser(ic, true);
        coll.setDuration(0.1f);
        t.row();
        t.table(st -> {
            st.add(Core.bundle.get("eu-clickToShow")).center();
            st.row();
            st.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(true)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).pad(5).size(8).center();
        }).left();
        t.row();
        t.add(coll);
        t.row();
    }

    /**
     * 人为移动子弹
     * @param b 要移动的子弹
     * @param endX 结束坐标X
     * @param endY 结束坐标Y
     * @param speed 速度
     */
    public static void movePoint(Bullet b, float endX, float endY, float speed) {
        // 计算两点之间的距离
        float distance = (float) Math.sqrt(Math.pow(endX - b.x, 2) + Math.pow(endY - b.y, 2));

        float moveSpeed = distance * speed;

        // 计算移动方向的单位向量
        float dx = (endX - b.x) / distance;
        float dy = (endY - b.y) / distance;

        // 计算每个tick内移动的距离
        float moveDistance = moveSpeed * Time.delta;

        // 更新子弹的位置
        b.x += dx * moveDistance;
        b.y += dy * moveDistance;

        // 检查是否到达或超过终点
        if (Math.abs(b.x - endX) < 1e-4f && Math.abs(b.y - endY) < 1e-4f) {
            b.x = endX;
            b.y = endY;
        }
    }

    /**
     * 根据已知两点和x坐标求y坐标
     * @param x1 第一个点的x坐标
     * @param y1 第一个点的y坐标
     * @param x2 第二个点的x坐标
     * @param y2 第二个点的y坐标
     * @param x  要求y坐标的点的x坐标
     * @return   对应的y坐标
     */
    public float lineY(float x1, float y1, float x2, float y2, float x) {
        // 计算斜率
        float slope = (y2 - y1) / (x2 - x1);

        // 计算截距
        float intercept = y1 - slope * x1;

        // 根据直线方程 y = mx + b 计算y坐标
        return slope * x + intercept;
    }

    /**
     * 根据已知点、线的角度和另一个点的x坐标，求解该点的y坐标。
     * @param x1 已知点的x坐标
     * @param y1 已知点的y坐标
     * @param angle 线与x轴正方向的夹角（以度为单位）
     * @param x2 另一个点的x坐标
     * @return 求解的y坐标
     */
    public float angleY(float x1, float y1, float angle, float x2) {
        // 处理角度为90度或270度的特殊情况
        if (angle == 90 || angle == 270) {
            // 如果角度为90度或270度，直线是垂直的，y坐标与y1相同
            return y1;
        }
        // 斜率
        float slope = (float) Math.tan(Math.toRadians(angle));
        // 计算y坐标并返回
        return slope * (x2 - x1) + y1;
    }
}
