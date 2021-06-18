const status = require("other/status");

function gone(color){
    return new Effect(12, cons(e => {
        Draw.color(color);
        Lines.stroke(2 * e.fout());
        Lines.circle(e.x, e.y, 5 * e.fout());
    }));
};

const shiftSpeed = 2;
var rainbowRegions = [];

const r1 = extend(BasicBulletType,{});
r1.sprite = "btm-shotgunShot";
r1.speed = 7;
r1.damage = 60;
r1.width = 15;
r1.height = 24;
r1.shrinkY = 0;
r1.despawnEffect = gone(Color.valueOf("ffe800"));
r1.frontColor = Color.valueOf("ffe800");
r1.backColor = Color.valueOf("ffe800");
const r2 = extend(BasicBulletType,{});
r2.sprite = "btm-shotgunShot";
r2.speed = 7;
r2.damage = 80;
r2.width = 15;
r2.height = 24;
r2.shrinkY = 0;
r2.makeFire = true;
r2.despawnEffect = gone(Color.valueOf("ff5252"));
r2.frontColor = Color.valueOf("ff5252");
r2.backColor = Color.valueOf("ff5252");
r2.status = StatusEffects.melting;
const r3 = extend(BasicBulletType,{});
r3.sprite = "btm-shotgunShot";
r3.speed = 7;
r3.damage = 55;
r3.width = 15;
r3.height = 24;
r3.shrinkY = 0;
r3.despawnEffect = gone(Color.valueOf("d95eec"));
r3.frontColor = Color.valueOf("d95eec");
r3.backColor = Color.valueOf("d95eec");
r3.status = StatusEffects.unmoving;
r3.statusDuration = 60 * 2;
const r4 = extend(BasicBulletType,{});
r4.sprite = "btm-shotgunShot";
r4.speed = 7;
r4.damage = 55;
r4.width = 15;
r4.height = 24;
r4.shrinkY = 0;
r4.despawnEffect = gone(Color.valueOf("5eecec"));
r4.frontColor = Color.valueOf("5eecec");
r4.backColor = Color.valueOf("5eecec");
r4.status = StatusEffects.freezing;
const r5 = extend(BasicBulletType,{});
r5.sprite = "btm-shotgunShot";
r5.speed = 7;
r5.damage = 20;
r5.width = 15;
r5.height = 24;
r5.shrinkY = 0;
r5.despawnEffect = gone(Color.valueOf("5ee766"));
r5.frontColor = Color.valueOf("5ee766");
r5.backColor = Color.valueOf("5ee766");
r5.status = status.poison;

const bullet = [r2, r1, r5, r4, r3];

const rainbow = extendContent(PowerTurret, "rainbow", {
    load(){
        this.super$load();
        for(var i = 0; i < 5; i++){
            rainbowRegions[i] = Core.atlas.find("btm-rainbow-rainbow-" + (i + 1));
        }
    },
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.ammo);
        for(var i = 0; i < bullet.length; i++){
            this.stats.add(Stat.ammo, new AmmoListValue(OrderedMap.of(this, bullet[i])));
        }
    },
});
rainbow.buildType = prov(() => {
    const tr = new Vec2();
    const tr2 = new Vec2();
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        draw(){
            this.super$draw();
            tr2.trns(this.rotation, -this.recoil);
            Draw.blend(Blending.additive);
            for(var h = 0; h < 5; h++){
                Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * shiftSpeed) + (h * (360 / 16))));
                Draw.rect(rainbowRegions[h], this.x + tr2.x, this.y + tr2.y, this.rotation - 90);
            }
            Draw.blend();
            Draw.color();
        },
        shoot(type){
            tr.trns(this.rotation, this.block.shootLength, Mathf.range(this.block.xRand));

            for(var i = 0; i < this.block.shots; i++){
                this.bullet(bullet[i], this.rotation + (i - (this.block.shots / 2)) * this.block.spread);
            }
            this.shotCounter++;
            this.recoil = this.block.recoilAmount;
            this.heat = 1;
            this.effects();
            this.useAmmo();
        },
    }, rainbow);
});
rainbow.shootEffect = new Effect(16, cons(e => {
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
rainbow.recoilAmount = 4;
rainbow.size = 3;
rainbow.range = 28 * 8;
rainbow.reloadTime = 72;
rainbow.shots = 5;
rainbow.shootSound = Sounds.shootBig;
rainbow.powerUse = 9;
rainbow.health = 200 * 3 * 3;
rainbow.shootType = r1;
rainbow.requirements = ItemStack.with(
    Items.lead, 200,
    Items.silicon, 180,
    Items.metaglass, 130,
    Items.thorium, 105
);
rainbow.buildVisibility = BuildVisibility.shown;
rainbow.category = Category.turret;
exports.rainbow = rainbow;