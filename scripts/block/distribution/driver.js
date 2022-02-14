/**
 * @author guiY<guiYMOUR>
 * @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
 * @readme <I am sorry that the former version of the code due to my negligence led to a lot of problems, has been fixed, I hope you have fun. Finally, I would like to express my apologies again.>
 */

const range = 55;//blocks
//接口基本就是质驱
const knockback = 2;
const reloadTime = 150;
const shootEffect = Fx.shootBig2;
const smokeEffect = Fx.shootBigSmoke2;
const receiveEffect = Fx.mineBig;
const shootSound = Sounds.shootBig;
const shake = 2;
const translation = 5;
const minDistribute = 24;

//定义一个默认data
const defData = {from : null, to : null, liquid : Liquids.water, amount : 0};
//自己写一个类似质驱子弹的运输子弹
const waterBullet = extend(BasicBulletType,{
    hit(b){    },
    update(b){
        var data = b.data == null ? defData : b.data;
        if(data.from == null || data.from.dead || data.to == null || data.to.dead) return;//owner或目标不存在或死亡，强制退出，防止闪退
        const hitDst = 7;//可以认为是shootLength吧
        var baseDst = data.from.dst(data.to);//owner到目标的距离
        var dst1 = b.dst(data.from);//子弹离开owner的距离
        var dst2 = b.dst(data.to);//子弹到目标的距离
        var intersect = false;//是不是到了

        if(dst1 > baseDst){
            var angleTo = b.angleTo(data.to);
            var baseAngle = data.to.angleTo(data.from);

            if(Angles.near(angleTo, baseAngle, 2)){
                intersect = true;
                b.set(data.to.x + Angles.trnsx(baseAngle, hitDst), data.to.y + Angles.trnsy(baseAngle, hitDst));
            }
        }

        if(Math.abs(dst1 + dst2 - baseDst) < 4 && dst2 <= hitDst || dst1 > baseDst){//保险，其实可以不用，效果优先，不过完全判定距离也是可以的，角度在方块里面判断了
            intersect = true;
        }
        if(intersect){
            receiveEffect.at(b);
            b.remove();
            Effect.shake(shake, shake, b);
            data.to.setReload(1);
            data.to.liquids.add(data.liquid, data.amount);
        }
    },
    draw(b){
        var data = b.data == null ? defData : b.data;
        Draw.color(data.liquid.color);
        Draw.rect(this.backRegion, b.x, b.y, this.width, this.height, b.rotation() - 90);
        Draw.rect(this.frontRegion, b.x, b.y, this.width, this.height, b.rotation() - 90);

        Draw.reset();
    },
    despawned(b){    },
});
waterBullet.lifetime = 200;
waterBullet.width = 14;
waterBullet.height = 16;
waterBullet.shrinkY = 0;
waterBullet.damage = 0;
waterBullet.speed = 6;
waterBullet.collidesTiles = false;
waterBullet.collidesTeam = true;
waterBullet.pierce = true;
waterBullet.pierceBuilding = true;

const driver = extendContent(LiquidExtendingBridge, "ld", {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.shootRange, range, StatUnit.blocks);
        this.stats.add(Stat.reload, 60 / reloadTime, StatUnit.perSecond);
    },
    drawBridge(req, ox, oy, flip){ },
    drawPlace(x, y, rotation, valid){
        Drawf.dashCircle(x * Vars.tilesize, y * Vars.tilesize, range * Vars.tilesize, Pal.accent);
    },
    linkValid(tile, other, checkDouble){
        if(other == null || tile == null || other == tile) return false;
        if(Math.pow(other.x - tile.x, 2) + Math.pow(other.y - tile.y, 2) > Math.pow(range + 0.5, 2)) return false;
        return ((other.block() == tile.block() && tile.block() == this) || (!(tile.block() instanceof ItemBridge) && other.block() == this))
            && (other.team() == tile.team() || tile.block() != this)
            && (!checkDouble || other.build.link != tile.pos());
        //return (other.block() == tile.block() && tile.block() == this) && other.team() == tile.team() && other.block() instanceof ItemBridge;
    },
    findLink(x, y){ return null },
});

driver.buildType = prov(() => {
    var rotation = 90;
    var reload = 0;
    var amount = 0;
    var liquid = null;
    var tr = new Vec2();
    
    const block = driver;
    
    return new JavaAdapter(LiquidExtendingBridge.LiquidExtendingBridgeBuild, {
        getR(){
            return rotation;
        },
        setR(v){
            rotation = v;
        },
        setReload(v){
            reload = v;
        },
        getA(){
            return amount;
        },
        setA(v){
            amount = v;
        },
        getL(){
            return liquid;
        },
        setL(v){
            liquid = v;
        },
        updateTile(){
            if(reload > 0){
                reload = Mathf.clamp(reload - this.edelta() / reloadTime);
            }
            const other = Vars.world.build(this.link);
            if(other != null/* && block.linkValid(this.tile, other.tile)*/){
                if(!block.linkValid(this.tile, other.tile)){
                    this.link = -1;
                    return;
                }
                if(this.liquids.get(this.liquids.current()) > minDistribute){
                if(Vars.world.build(other.link) == null || other.liquids.get(other.liquids.current()) < minDistribute){
                    other.setR(Mathf.slerpDelta(other.getR(), other.angleTo(this), 0.125 * other.power.status));
                } else {
                    var others = Vars.world.build(other.link);
                    other.setR(Mathf.slerpDelta(other.getR(), other.angleTo(others), 0.125 * other.power.status));
                }
                this.setR(Mathf.slerpDelta(this.getR(), this.angleTo(other), 0.125 * this.power.status));
                }
                var targetRotation = this.angleTo(other);
                if(this.liquids.total() > minDistribute && other.liquids.total() < other.block.liquidCapacity && Vars.world.build(this.link) != null && /*Angles.near(this.getR(), targetRotation, 1) && Angles.near(other.getR(), targetRotation + 180, 1) && */Angles.within(rotation, targetRotation, 1) && Angles.within(other.getR(), other.angleTo(this), 5) && reload <= 0){
                    this.setL(this.liquids.current());
                    this.setA(this.liquids.total()); 
                    this.shoot(other);
                }
            }
            //var otherL = other
            if(other == null) return;
            //if(other != null){
            if(other.link == -1 || Vars.world.build(other.link) == null){
                //未连接的液驱可以排出液体
                other.dumpLiquid(other.liquids.current());
            }
            //}
        },
        /**
         * @param {Building} target
         * 射击部分独立成函数了，记住target是Building就行
         */
        shoot(target){
            reload = 1;
            this.bullet(waterBullet, this.getR(), {from : this, to : target, liquid : this.getL(), amount : this.getA()});
            tr.trns(this.getR(), block.size * Vars.tilesize / 2);
            var angle = this.angleTo(target);
            shootEffect.at(this.x + Angles.trnsx(angle, translation),
            this.y + Angles.trnsy(angle, translation), angle);

            smokeEffect.at(this.x + Angles.trnsx(angle, translation),
            this.y + Angles.trnsy(angle, translation), angle);

            Effect.shake(shake, shake, this);
            
            shootSound.at(this.tile, Mathf.random(0.9, 1.1));
            this.liquids.remove(this.getL(), this.getA());
        },
        /**
         * @param {MyLiquidMassDriverBolt} type
         * @param {float} angle
         * @param {MyBulletData} data
         * 子弹是这里构建的，发射是上面发射的
         */
        bullet(type, angle, data){
            tr.trns(angle, block.size * Vars.tilesize / 2);
            type.create(this, this.team, this.x + tr.x, this.y + tr.y, angle, -1, 1, 1, data);
        },
        drawSelect(){    },
        drawConfigure(){
            const sin = Mathf.absin(Time.time, 6, 1);

            Draw.color(Pal.accent);
            Lines.stroke(1);
            Drawf.circles(this.x, this.y, (block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.accent);
            const other = Vars.world.build(this.link);
            if(other != null){
                Drawf.circles(other.x, other.y, (block.size / 2 + 1) * Vars.tilesize + sin - 2, Pal.place);
                Drawf.arrow(this.x, this.y, other.x, other.y, block.size * Vars.tilesize + sin, 4 + sin, Pal.accent);
            }
            Drawf.dashCircle(this.x, this.y, range * Vars.tilesize, Pal.accent);
        },
        canDumpLiquid(to, liquid){
            return Vars.world.build(this.link) == null;
        },
        //draw部分以自己需求
        draw(){
            Draw.rect(Core.atlas.find("btm-ld-base"),this.x,this.y);
            var region = Core.atlas.find("btm-ld-region");
            var liquidRegion = Core.atlas.find("btm-ld-liquid");
            var top = Core.atlas.find("btm-ld-top");
            var bottom = Core.atlas.find("btm-ld-bottom");
            Draw.z(Layer.turret);

            Drawf.shadow(region,
            this.x + Angles.trnsx(rotation + 180, reload * knockback) - (block.size / 2),
            this.y + Angles.trnsy(rotation + 180, reload * knockback) - (block.size / 2), rotation - 90);
            Draw.rect(bottom,
            this.x + Angles.trnsx(rotation + 180, reload * knockback),
            this.y + Angles.trnsy(rotation + 180, reload * knockback), rotation - 90);
            Draw.rect(region,
            this.x + Angles.trnsx(rotation + 180, reload * knockback),
            this.y + Angles.trnsy(rotation + 180, reload * knockback), rotation - 90);
            Draw.color(this.liquids.current().color);
            Draw.alpha(Math.min(this.liquids.get(this.liquids.current()) / block.liquidCapacity, 1));
            Draw.rect(liquidRegion,
            this.x + Angles.trnsx(rotation + 180, reload * knockback),
            this.y + Angles.trnsy(rotation + 180, reload * knockback), rotation - 90);
            Draw.color();
            Draw.alpha(1);
            Draw.rect(top,
            this.x + Angles.trnsx(rotation + 180, reload * knockback),
            this.y + Angles.trnsy(rotation + 180, reload * knockback), rotation - 90);
        },
        acceptLiquid(source, liquid){
            if(this.team != source.team || !block.hasLiquids) return false;
            var other = Vars.world.tile(this.link);
            return other != null && block.linkValid(this.tile, other) && this.liquids.total() < block.liquidCapacity;
        },
        write(write) {
            this.super$write(write);
            write.f(rotation);
            write.f(reload);
        },
        read(read, revision) {
            this.super$read(read, revision);
            rotation = read.f();
            reload = read.f();
        },
    }, driver);
});

driver.hasPower = true;
driver.consumes.power(1.8);
driver.size = 2;
driver.liquidCapacity = 300;
driver.requirements = ItemStack.with(
    Items.metaglass, 85,
    Items.silicon, 80,
    Items.titanium, 80,
    Items.thorium, 55
);
driver.buildVisibility = BuildVisibility.shown;
driver.category = Category.liquid;

exports.driver = driver;
