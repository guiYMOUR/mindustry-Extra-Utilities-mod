const lib = require("blib");
const angleShiftStrength = 7;
const shiftAngel = 55;
const sideOffset = 3;
const fade = 0.012;
const shootDuration = 480;
const firingMoveFract = 0.25;
const reloadTime = 240;

const shiftSpeed = 2;
var rainbowRegions = [];

const prism = extendContent(PowerTurret, "prism", {
    load(){
        this.super$load();
        for(var i = 0; i < 16; i++){
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
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        _bulletHeat: 0,
        getBulletHeat(){
            return this._bulletHeat;
        },
        draw(){
            this.super$draw();
            tr2.trns(this.rotation, -this.recoil);
            Draw.blend(Blending.additive);
            for(var h = 0; h < 16; h++){
                Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * shiftSpeed) + (h * (360 / 16))));
                Draw.rect(rainbowRegions[h], this.x + tr2.x, this.y + tr2.y, this.rotation - 90);
            }
            Draw.blend();
            Draw.color();
        },
        updateTile() {
            this.super$updateTile();
            if (bulletLife > 0 && bullet[1] != null) {
                this.wasShooting = true;
                for(var n = 0; n < bullet.length; n++){
                    var data = (n * (360 / bullet.length));
                    var sine = Mathf.sinDeg(data + (bulletTime * angleShiftStrength));
                    tr.trns(this.rotation, (this.block.size * Vars.tilesize / 2) + -this.block.recoilAmount, sine * sideOffset);
                    bullet[n].rotation(this.rotation + ((sine * shiftAngel) * this._bulletHeat));
                    bullet[n].set(this.x + tr.x, this.y + tr.y);
                    bullet[n].time = 0;
                }
                this.heat = 1;
                this.recoil = this.block.recoilAmount;
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
                var liquid = this.liquids.current();
                var maxUsed = this.block.consumes.get(ConsumeType.liquid).amount;

                var used = (this.cheating() ? maxUsed * Time.delta : Math.min(this.liquids.get(liquid), maxUsed * Time.delta)) * (1 + (liquid.heatCapacity - Liquids.water.heatCapacity) * this.block.coolantMultiplier);
                _reload -= used;
                this.liquids.remove(liquid, used);

                if (Mathf.chance(0.06 * used)) {
                    this.block.coolEffect.at(this.x + Mathf.range(this.block.size * Vars.tilesize / 2), this.y + Mathf.range(this.block.size * Vars.tilesize / 2));
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
                tr.trns(angle, this.block.size * Vars.tilesize / 2, sine * sideOffset);
                bullet[s] = type.create(this.tile.build, this.team, this.x + tr.x, this.y + tr.y, angle + (sine * shiftAngel));
                bullet[s].data = data;
            }
        
            this._bulletHeat = 1;
        },
    
        turnToTarget(targetRot) {
            this.rotation = Angles.moveToward(this.rotation, targetRot, this.efficiency() * this.block.rotateSpeed * this.delta() * (bulletLife > 0 ? firingMoveFract : 1));
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
prism.size = 4;
prism.shootShake = 2;
prism.range = 28 * 8;
prism.reloadTime = reloadTime;
prism.powerUse = 20;
prism.shootSound = lib.loadSound("prismS");
prism.loopSound = lib.loadSound("prism-beam");
prism.loopSoundVolume = 2;
prism.canOverdrive = false;

prism.shootType = (() => {
    const cl = new JavaAdapter(ContinuousLaserBulletType, {
        draw(b){
            var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
            var wide = Mathf.clamp(1 - b.owner.getBulletHeat());
        
            Draw.blend(Blending.additive);
            for(var s = 0; s < 4; s++){
                Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * 2) + b.data));
                for(var i = 0; i < this.tscales.length; i++){
                    Tmp.v1.trns(b.rotation() + 180, (this.lenscales[i] - 1) * this.spaceMag);
                    Lines.stroke((this.width + Mathf.absin(Time.time, this.oscScl, this.oscMag)) * fout * wide * this.strokes[s] * this.tscales[i]);
                    Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), this.length * this.lenscales[i], false);
                }
            }
            Draw.reset();
            Draw.blend();
         },
    });
    cl.width = 8;
    cl.damage = 45;
    cl.strokes = [1.2, 1.1, 1, 0.9];
    cl.tscales = [1, 0.74, 0.5, 0.24];
    cl.lenscales = [0.92, 1, 1.017, 1.025];
    cl.length = 31 * 8;
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
prism.health = 200 * 4 * 4;
prism.coolantMultiplier = 1;
prism.consumes.add(new ConsumeCoolant(0.5));
prism.requirements = ItemStack.with(
    Items.lead, 300,
    Items.silicon, 350,
    Items.metaglass, 430,
    Items.thorium, 285,
    Items.surgeAlloy, 110
);
prism.buildVisibility = BuildVisibility.shown;
prism.category = Category.turret;
exports.prism = prism;