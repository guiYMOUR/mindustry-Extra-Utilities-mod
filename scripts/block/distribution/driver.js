/*
    * @author <guiY>
    * @Extra mod <https://github.com/guiYMOUR/mindustry-Extra-Utilities-mod>
    * @readme <I am sorry that the former version of the code due to my negligence led to a lot of problems, has been fixed, I hope you have fun. Finally, I would like to express my apologies again.>
    * I will use bullet data (maybe, If the update is over and there is time to change)
*/
//因为未来可能会改，这个的中文注释等改版后加
const range = 58;//blocks
const knockback = 2;
const reloadTime = 150;
const shootEffect = Fx.shootBig2;
const smokeEffect = Fx.shootBigSmoke2;
const receiveEffect = Fx.mineBig;
const shootSound = Sounds.shootBig;
const shake = 2;
const translation = 5;
const minDistribute = 24;

const waterBullet = extend(BasicBulletType,{
    hit(b){    },
    update(b){
        var owner = b.owner;
        var other = Vars.world.tile(owner.link);
        if(other == null || other.build == null || !owner.block.linkValid(owner.tile, other)){
            b.remove();
            return;//Here is redundant, do not care, will not quit on the line.
        }
        if(other != null && other.build != null && b.within(other.build, 16) && b.team == other.build.team){
            receiveEffect.at(b);
            b.remove();
            Effect.shake(shake, shake, b);
            other.build.setReload(1);
            other.build.liquids.add(owner.getL(), owner.getA());
        }
    },
    draw(b){
        Draw.color(b.owner.getL().color);
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
                if(this.liquids.total() > minDistribute && other.liquids.total() < other.block.liquidCapacity && Vars.world.build(this.link) != null && Angles.near(this.getR(), targetRotation, 1) && Angles.near(other.getR(), targetRotation + 180, 1) && reload == 0){
                    this.setL(this.liquids.current());
                    this.setA(this.liquids.total()); 
                    this.shoot(other);
                }
            }
            //var otherL = other
            if(other == null) return;
            //if(other != null){
            if(other.link == -1){
                other.dumpLiquid(other.liquids.current());
            }
            //}
        },
        shoot(target){
            reload = 1;
            this.bullet(waterBullet, this.getR());
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
        bullet(type, angle){
            tr.trns(angle, block.size * Vars.tilesize / 2);
            type.create(this, this.team, this.x + tr.x, this.y + tr.y, angle, 1);
        },
        drawSelect(){    },
        drawConfigure() {
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