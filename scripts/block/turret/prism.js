//引用部分，类似import，对应的是exports导出
const lib = require("blib");
const items = require("game/items");
//三棱镜光线聚集的强度
const angleShiftStrength = 7;
//角度
const shiftAngel = 55;
const sideOffset = 3;
//激光粗度变化速度，<1
const fade = 0.012;
//射击间隔
const shootDuration = 480;
//烎移动炮台速度
const firingMoveFract = 0.25;
//reload还要注释?
const reloadTime = 240;
//炮塔颜色变化速度
const shiftSpeed = 1.8;
var rainbowRegions = [];

const prism = extendContent(PowerTurret, "prism", {
    load(){
        this.super$load();
        for(var i = 0; i < 8; i++){
            rainbowRegions[i] = Core.atlas.find("btm-prism-rainbow-" + (i + 1));
        }
    },
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.booster);
        this.stats.add(Stat.input, StatValues.boosters(reloadTime, this.consumes.get(ConsumeType.liquid).amount, this.coolantMultiplier, false, l => this.consumes.liquidfilters.get(l.id)));
        this.stats.remove(Stat.ammo);
        this.stats.add(Stat.ammo, Core.bundle.format("bullet.damage", "540 (× 6shoots) / s"));
    },
});
prism.buildType = prov(() => {
    var bullet = [null, null, null, null, null, null];
    var bulletLife = 0;
    var bulletTime = 0;
    var _reload = reloadTime;
    const tr = new Vec2();
    const tr2 = new Vec2();
    const block = prism;
    var x=0,y=0;
    var rotation =0;
    var recoil = 0;
    var liquids = null;
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        _bulletHeat: 0,
        getBulletHeat(){
            return this._bulletHeat;
        },
        draw(){
            rotation = this.rotation;
            recoil = this.recoil;
            this.super$draw();
            tr2.trns(rotation, -recoil);
            Draw.blend(Blending.additive);
            for(var h = 0; h < 8; h++){
                Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * shiftSpeed) + (h * (360 / 8))));
                Draw.rect(rainbowRegions[h], x + tr2.x, y + tr2.y, rotation - 90);
            }
            Draw.blend();
            Draw.color();
        },
        updateTile() {
            x = this.x;
            y = this.y;
            rotation = this.rotation;
            liquids = this.liquids;
            this.super$updateTile();
            if (bulletLife > 0 && bullet[1] != null) {
                this.wasShooting = true;
                for(var n = 0; n < bullet.length; n++){
                    var data = (n * (360 / bullet.length));
                    var sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength));
                    tr.trns(rotation, (block.size * Vars.tilesize / 2) + -block.recoilAmount, sine * sideOffset);
                    bullet[n].rotation(rotation + ((sine * shiftAngel) * this._bulletHeat));
                    bullet[n].set(x + tr.x, y + tr.y);
                    bullet[n].time = 0;
                }
                this.heat = 1;
                this.recoil = block.recoilAmount;
                bulletTime += Time.delta;
                bulletLife -= Time.delta / Math.max(this.efficiency(), 0.00001);
                if (bulletLife <= 0) {
                    for(var b = 0; b < bullet.length; b++){
                        bullet[b] = null;
                    }
                    bulletTime = 0;
                    this._bulletHeat = 0;
                }
            } else if (_reload > 0) {
                this.wasShooting = true;
                var liquid = liquids.current();
                var maxUsed = block.consumes.get(ConsumeType.liquid).amount;

                var used = (this.cheating() ? maxUsed * Time.delta : Math.min(liquids.get(liquid), maxUsed * Time.delta)) * (1 + (liquid.heatCapacity - Liquids.water.heatCapacity) * block.coolantMultiplier);
                _reload -= used;
                liquids.remove(liquid, used);

                if (Mathf.chance(0.06 * used)) {
                    block.coolEffect.at(x + Mathf.range(block.size * Vars.tilesize / 2), y + Mathf.range(block.size * Vars.tilesize / 2));
                }
            }
            this._bulletHeat = Mathf.lerpDelta(this._bulletHeat, 0, fade);
        },
        updateShooting(){
        
            if(bulletLife > 0 && bullet[1] != null){
                return;
            }
            if(_reload <= 0 && (this.consValid() || this.cheating())){
                var type = this.peekAmmo();
                this.shoot(type);
            
                _reload = reloadTime;
            }
        },
    
        bullet(type, angle){
            bulletTime = 0;
            bulletLife = shootDuration;
            for(var s = 0; s < 6; s++){
                var data = (s * (360 / 6));
                var sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength));
                tr.trns(angle, block.size * Vars.tilesize / 2, sine * sideOffset);
                bullet[s] = type.create(this.tile.build, this.team, x + tr.x, y + tr.y, angle + (sine * shiftAngel));
                bullet[s].data = data;
            }
        
            this._bulletHeat = 1;
        },
    
        turnToTarget(targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, this.efficiency() * block.rotateSpeed * this.delta() * (bulletLife > 0 ? firingMoveFract : 1));
        },
    
        shouldActiveSound(){
            return bulletLife > 0 && bullet[1] != null;
        },
        write(write){
            this.super$write(write);
            write.f(_reload);
        },
        read(read, revision){
            this.super$read(read, revision);
            _reload = read.f();
        },
    }, prism);
});
prism.shootEffect = Fx.shootBigSmoke2;
prism.shootCone = 40;
prism.recoilAmount = 4;
prism.size = 5;
prism.shootShake = 2;
prism.range = 29 * 8;
prism.reloadTime = reloadTime;
prism.powerUse = 34;
prism.shootSound = lib.loadSound("prismS");
prism.loopSound = lib.loadSound("prism-beam");
prism.loopSoundVolume = 2;
prism.canOverdrive = false;

prism.shootType = (() => {
    const width = 8;
    const strokes = [1.2, 1.1, 1, 0.9];
    const tscales = [1, 0.74, 0.5, 0.24];
    const lenscales = [0.92, 1, 1.017, 1.025];
    const length = 32 * 8;
    const spaceMag = 35;
    const oscMag = 1.5;
    const oscScl = 0.8;
    const cl = new JavaAdapter(ContinuousLaserBulletType, {
        draw(b){
            var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
            var wide = Mathf.clamp(1 - b.owner.getBulletHeat());
        
            Draw.blend(Blending.additive);
            for(var s = 0; s < 4; s++){
                Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * 2) + b.data));
                for(var i = 0; i < tscales.length; i++){
                    Tmp.v1.trns(b.rotation() + 180, (lenscales[i] - 1) * spaceMag);
                    Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * wide * strokes[s] * tscales[i]);
                    Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), length * lenscales[i], false);
                }
            }
            Draw.reset();
            Draw.blend();
         },
    });
    cl.damage = 40;
    cl.hitEffect = new Effect(16, cons(e => {
        Draw.blend(Blending.additive);
        Draw.color(Color.valueOf("ff0000ff").shiftHue(Time.time * 2.0));
        Lines.stroke(e.fout() * 1.5);
        const hl = new Floatc2({get: function(x, y){
            const ang = Mathf.angle(x, y);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 8 + 1.5);
            }});
        Angles.randLenVectors(e.id, 1, e.finpow() * 70.0, e.rotation, 80.0, hl);
        Draw.blend();
        Draw.reset();
    }));
    cl.drawSize = 380;
    cl.incendChance = 0;
    return cl;
})();
prism.health = 200 * 5 * 5;
prism.coolantMultiplier = 1;
prism.consumes.add(new ConsumeCoolant(0.7)).update = false;
prism.requirements = ItemStack.with(
    Items.lead, 620,
    Items.silicon, 550,
    Items.metaglass, 430,
    Items.thorium, 385,
    items.lightninAlloy, 180
);
prism.buildVisibility = BuildVisibility.shown;
prism.category = Category.turret;
exports.prism = prism;