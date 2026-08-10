//One lightning bolt only deals damage once.

//package ExtraUtilities.worlds.entity.bullet;
//
//import ExtraUtilities.content.EUGet;
//import arc.graphics.Color;
//import arc.graphics.g2d.Draw;
//import arc.graphics.g2d.Fill;
//import arc.graphics.g2d.Lines;
//import arc.math.Mathf;
//import arc.math.Rand;
//import arc.math.geom.*;
//import arc.struct.IntSet;
//import arc.struct.Seq;
//import arc.util.Time;
//import arc.util.Tmp;
//import arc.util.pooling.Pool;
//import arc.util.pooling.Pools;
//import mindustry.content.Fx;
//import mindustry.content.StatusEffects;
//import mindustry.core.World;
//import mindustry.entities.Damage;
//import mindustry.entities.Effect;
//import mindustry.entities.Mover;
//import mindustry.entities.Units;
//import mindustry.entities.bullet.BulletType;
//import mindustry.game.Team;
//import mindustry.gen.*;
//import mindustry.world.Tile;
//
//import static mindustry.Vars.*;
//import static mindustry.Vars.tilesize;
//import static mindustry.content.Fx.rand;
//
//public class ChainLightning extends BulletType {
//    public Color color;
//
//    public float linkSpace;
//    public float stroke;
//    public boolean large = false;
//
//    public ChainLightning(float lifetime, float linkSpace, float stroke, Color color, float damage, Effect hitEffect){
//        absorbable = hittable = collides = collidesTiles = keepVelocity = false;
//        speed = 0;
//        despawnEffect = Fx.none;
//        this.lifetime = lifetime;
//        this.linkSpace = linkSpace;
//        this.stroke = stroke;
//        this.color = color;
//        this.damage = damage;
//        this.hitEffect = hitEffect;
//        status = StatusEffects.shocked;
//    }
//
//    private void init(chain b) {
//        if(!(b.data instanceof Position) || damage < 0) return;
//        Position p = (Position) b.data;
//        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(b.x, b.y, tx, ty);
//        Tmp.v1.set(p).sub(b.x, b.y).nor();
//
//        float normx = Tmp.v1.x, normy = Tmp.v1.y;
//        int links = Mathf.ceil(dst / linkSpace);
//        float spacing = dst / links;
//
//        b.random.setSeed(b.id);
//        b.resetPos = new float[links + 1][2];
//        b.hit.clear();
//        int i;
//
//        float ox = b.x, oy = b.y;
//        for(i = 0; i < links; i++){
//            float nx, ny;
//            if(i == links - 1){
//                nx = tx;
//                ny = ty;
//            }else{
//                float len = (i + 1) * spacing;
//                Tmp.v1.setToRandomDirection(b.random).scl(linkSpace/2f);
//                nx = b.x + normx * len + Tmp.v1.x;
//                ny = b.y + normy * len + Tmp.v1.y;
//            }
//
//            b.resetPos[i] = new float[]{nx, ny};
//
//            float length = EUGet.pos(ox, oy).dst(nx, ny);
//            float angle = EUGet.pos(ox, oy).angleTo(nx, ny);
//
//            b.vec.trnsExact(angle, length);
//
//            if(b.type.collidesGround){
//                b.seg1.set(ox, oy);
//                b.seg2.set(b.seg1).add(b.vec);
//                World.raycastEachWorld(ox, oy, b.seg2.x, b.seg2.y, (cx, cy) -> {
//                    Building tile = world.build(cx, cy);
//                    boolean collide = tile != null && tile.collide(b) && b.checkUnderBuild(tile, cx * tilesize, cy * tilesize)
//                            && ((tile.team != b.team && tile.collide(b)) || b.type.testCollision(b, tile)) && b.hit.add(tile.pos());
//                    if(collide){
//                        b.collided.addUnique(b.collidePool.obtain().set(cx * tilesize, cy * tilesize, tile));
//
//                        for(Point2 p2 : Geometry.d4){
//                            Tile other = world.tile(p2.x + cx, p2.y + cy);
//                            if(other != null && (large || Intersector.intersectSegmentRectangle(b.seg1, b.seg2, other.getBounds(Tmp.r1)))){
//                                Building build = other.build;
//                                if(build != null && b.checkUnderBuild(build, cx * tilesize, cy * tilesize) && b.hit.add(build.pos())){
//                                    b.collided.addUnique(b.collidePool.obtain().set((p2.x + cx * tilesize), (p2.y + cy) * tilesize, build));
//                                }
//                            }
//                        }
//                    }
//                    return false;
//                });
//            }
//
//            float expand = 3f;
//
//            b.rect.setPosition(ox, oy).setSize(b.vec.x, b.vec.y).normalize().grow(expand * 2f);
//            float x2 = b.vec.x + ox, y2 = b.vec.y + oy;
//
//            float finalOx = ox;
//            float finalOy = oy;
//            Units.nearbyEnemies(b.team, b.rect, u -> {
//                if(u.checkTarget(b.type.collidesAir, b.type.collidesGround) && u.hittable()){
//                    u.hitbox(b.hitrect);
//
//                    Vec2 vec = Geometry.raycastRect(finalOx, finalOy, x2, y2, b.hitrect.grow(expand * 2));
//
//                    if(vec != null){
//                        b.collided.addUnique(b.collidePool.obtain().set(vec.x, vec.y, u));
//                    }
//                }
//            });
//            ox = nx;
//            oy = ny;
//        }
//
//        int[] collideCount = {0};
//        b.collided.sort(c -> b.dst2(c.x, c.y));
//        b.collided.each(c -> {
//            if(damage > 0 && (pierceCap <= 0 || collideCount[0] < pierceCap)){
//                if(c.target instanceof Unit){
//                    Unit u = (Unit) c.target;
//                    hitEffect.at(c.x, c.y);
//                    u.collision(b, c.x, c.y);
//                    b.collision(u, c.x, c.y);
//                    collideCount[0]++;
//                }else if(c.target instanceof Building){
//                    Building tile = (Building)c.target;
//                    float health = tile.health;
//
//                    if(tile.team != b.team && tile.collide(b)){
//                        tile.collision(b);
//                        b.type.hit(b, c.x, c.y);
//                        collideCount[0]++;
//                    }
//
//                    if(b.type.testCollision(b, tile)){
//                        b.type.hitTile(b, tile, c.x, c.y, health, false);
//                    }
//                }
//            }
//        });
//
//        b.collidePool.freeAll(b.collided);
//        b.collided.clear();
//    }
//
//    @Override
//    public void init(Bullet b) {
//        super.init(b);
//        if(!(b instanceof chain)) return;
//        init((chain) b);
//    }
//
//    private void draw(chain b){
//        if(b.resetPos.length > 0) {
//            Lines.stroke(stroke * Mathf.curve(b.fout(), 0, 0.7f));
//            Draw.color(Color.white, color, b.fin());
//
//            Lines.beginLine();
//
//            Fill.circle(b.x, b.y, Lines.getStroke() / 2);
//            Lines.linePoint(b.x, b.y);
//
//
//            b.random.setSeed(b.id);
//            float fin = Mathf.curve(b.fin(), 0, 0.5f);
//            int i;
//
//            for (i = 0; i < (b.resetPos.length - 1) * fin; i++) {
//                float nx = b.resetPos[i][0], ny = b.resetPos[i][1];
//
//                Lines.linePoint(nx, ny);
//            }
//
//            Lines.endLine();
//        }
//    }
//
//    @Override
//    public void draw(Bullet b) {
//        if(!(b instanceof chain)) return;
//        draw((chain) b);
//    }
//
//    @Override
//    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
//        Bullet bullet = chain.create();
//        bullet.type = this;
//        bullet.owner = owner;
//        bullet.team = team;
//        bullet.time = 0f;
//        bullet.originX = x;
//        bullet.originY = y;
//        if(!(aimX == -1f && aimY == -1f)){
//            bullet.aimTile = world.tileWorld(aimX, aimY);
//        }
//        bullet.aimX = aimX;
//        bullet.aimY = aimY;
//
//        bullet.initVel(angle, speed * velocityScl);
//        if(backMove){
//            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
//        }else{
//            bullet.set(x, y);
//        }
//        bullet.lifetime = lifetime * lifetimeScl;
//        bullet.data = data;
//        bullet.drag = drag;
//        bullet.hitSize = hitSize;
//        bullet.mover = mover;
//        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
//        //reset trail
//        if(bullet.trail != null){
//            bullet.trail.clear();
//        }
//        bullet.add();
//
//        if(keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc)owner).vel());
//        return bullet;
//    }
//
//    public static class chain extends Bullet{
//        public final Rand random = new Rand();
//        public final Rect rect = new Rect();
//        public final Rect hitrect = new Rect();
//        public final Seq<Unitc> entities = new Seq<>();
//        public final Vec2 vec = new Vec2(), seg1 = new Vec2(), seg2 = new Vec2();
//        public final IntSet hit = new IntSet();
//        public final Seq<Damage.Collided> collided = new Seq<>();
//        public final Pool<Damage.Collided> collidePool = Pools.get(Damage.Collided.class, Damage.Collided::new);
//
//        public float[][] resetPos;
//
//        public static chain create() {
//            return Pools.obtain(chain.class, chain::new);
//        }
//    }
//
//}