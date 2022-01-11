const lib = require("blib");
const items = require("game/items");

function aimShoot(color, length, width, lifetime, spacing){
    return new Effect(lifetime, length * 2, cons(e => {
        Draw.color(color);
        var track = Mathf.curve(e.fin(Interp.pow2Out), 0, 0.25) * Mathf.curve(e.fout(Interp.pow4Out), 0, 0.3) * e.fin();
        for(var i = 0; i <= length / spacing; i++){
            Tmp.v1.trns(e.rotation, i * spacing);
            var f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * length - i * spacing) / spacing)) * (0.6 + track * 0.4);
            Draw.rect(Core.atlas.find("btm-aim-shoot"), e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144 * Draw.scl * f, 144 * Draw.scl * f, e.rotation - 90);
        }
        Tmp.v1.trns(e.rotation, 0, (2 - track) * Vars.tilesize * width);
        Lines.stroke(track * 2);
        for(var i of Mathf.signs){
            Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, length * (0.75 + track / 4) * Mathf.curve(e.fout(Interp.pow5Out), 0, 0.1));
        }
    }));
}
exports.aimShoot = aimShoot;

function percent(x, y, tx, ty, radius) {
    var dst = Mathf.dst(x, y, tx, ty);
    var falloff = 0.4;
    var scaled = Mathf.lerp(1 - dst / radius, 1, falloff);
    return scaled;
}
const hole = extend(BasicBulletType, {
    draw(b) {
        var fin = b.time / this.lifetime;
        var fout = 1 - fin;
        Draw.z(Layer.turret + 1);
        Draw.blend(Blending.additive);
        var size = Math.min(1, fin * 6) * 100;
        Draw.rect(Core.atlas.find("btm-hole"), b.x, b.y, size, size, -(Time.time * 8));
        Draw.z();
        Draw.color(Color.valueOf("665c9f"), Color.valueOf("be92f9"), fout * 0.8 + 0.2);
        Draw.alpha(0.4 * fin + 0.6);
        Lines.stroke(fin * 3);
        Lines.circle(b.x, b.y, Mathf.sin(fout) * this.splashDamageRadius);
        Draw.blend();
    },
    update(b){
        var x = b.x;
        var y = b.y;
        var team = b.team;
        var rect = new Rect();
        rect.setSize(this.splashDamageRadius * 2).setCenter(x, y);
        var con = cons(unit => {
            if (unit.team == team || !unit.within(x, y, this.splashDamageRadius)) {
                return;
            }
            var p = percent(x, y, unit.getX(), unit.getY(), this.splashDamageRadius);
            unit.impulse(Tmp.v3.set(unit).sub(x, y).nor().scl(this.knockback * p * 80 * Time.delta));
            unit.vel.limit(3);
        });
        Units.nearbyEnemies(team, rect, con);
    },
});
hole.lifetime = 138;
Object.assign(hole, {
    knockback: -2.7,
    splashDamage: 0,
    splashDamageRadius: 160,
    shootEffect: Fx.none,
    hitEffect: Fx.none,
    smokeEffect: Fx.none,
    trailEffect: Fx.none,
    despawnEffect: Fx.none,
    damage: 0,
    speed: 0,
    collides: false,
    collidesAir: false,
    collidesGround: false,
    absorbable: false,
    hittable: false,
    keepVelocity: false,
    reflectable: false,
});
const missile = extend(BasicBulletType, {});
Object.assign(missile, {
    sprite : "btm-blackhole-missile",
    shootEffect : Fx.none,
    smokeEffect : Fx.none,
    width : 25,
    height : 25,
    shrinkY : 0,
    damage : 386,
    splashDamage : 500,
    splashDamageRadius : 88,
    homingPower : 1,
    homingRange : 80,
    lifetime : 52,
    speed : 8,
    trailChance : 1,
    trailColor : Color.valueOf("6f6f6f"),
    hitSound : Sounds.laser,
    despawnSound : Sounds.laser,
    hitShake : 3,
});
missile.trailEffect = new Effect(50, cons(e => {
    //Draw.z(Layer.bullet - 0.001);
    Draw.color(e.color);
    Fill.circle(e.x + Mathf.range(5), e.y + Mathf.range(5), e.rotation * 3 * e.fout());
    //Draw.z();
}));
missile.hitEffect = new Effect(50, 100, cons(e => {
    const rad = 88;
    Draw.blend(Blending.additive);
    e.scaled(15, cons(b => {
        Draw.color(Color.valueOf("be92f922"), b.fout());
        Fill.circle(e.x, e.y, rad);
    }));
    Draw.color(Color.valueOf("be92f9"));
    Lines.stroke(e.fout() * 3);
    Lines.circle(e.x, e.y, rad);
    var points = 10;
    var offset = Mathf.randomSeed(e.id, 360);
    Fill.circle(e.x, e.y, 12 * e.fout());
    Draw.color();
    Fill.circle(e.x, e.y, 6 * e.fout());
    Drawf.light(e.x, e.y, rad * 1.6, Color.valueOf("be92f9"), e.fout());
    Draw.blend();
}));

const chargeEffect = new Effect(40, cons(e => {
    Draw.color(Color.valueOf("665c9f"));
    Lines.circle(e.x, e.y, e.fout() * 40);
}));
const charge2 = new Effect(38, cons(e => {
    Draw.color(Color.valueOf("665c9f"));
    Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({
        get: (x, y) => {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5 + 1);
            Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 3);
        }
    }));
}));
const chargeSound = lib.loadSound("blackhole");

const blackhole = extendContent(PowerTurret, "blackhole", {});
blackhole.buildType = prov(() => {
    var holeBullet = null;
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        shoot(type){
            var vec = new Vec2();
            vec.trns(this.rotation, this.block.size * 8 / 2);
            if(holeBullet == null){
                var length = Math.hypot(Math.abs(this.x - this.targetPos.x), Math.abs(this.y - this.targetPos.y))
                aimShoot(Color.valueOf("be92f9"), Math.min(length, this.block.range), 1, hole.lifetime - 24, 14).at(this.x + vec.x, this.y + vec.y, this.rotation);
                if(this.within(this.targetPos, this.block.range + 8)){
                    holeBullet = hole;
                    holeBullet.create(this, this.team, this.targetPos.x, this.targetPos.y, this.rotation, 1, 1);
                    chargeSound.at(this.targetPos.x, this.targetPos.y, 1);
                }
            }
            vec.trns(this.rotation, (this.block.size/2) * 8 / 2);
            this.block.chargeBeginEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
            chargeSound.at(this.x + vec.x, this.y + vec.y, 1);
            for(var i = 0; i < 3; i++){
                 Time.run((120 / 3) * i, () => {
                    if(!this.isValid()) return;
                    vec.trns(this.rotation, (this.block.size/2) * 8 / 2);
                    chargeEffect.at(this.x + vec.x, this.y + vec.y, this.rotation);
                });
            }
            for(var i = 0; i < 8; i++){
                 Time.run((120 / 8) * i, () => {
                    if(!this.isValid()) return;
                    vec.trns(this.rotation, (this.block.size/2) * 8 / 2);
                    charge2.at(this.x + vec.x, this.y + vec.y, this.rotation);
                });
            }
            this.charging = true;
            Time.run(hole.lifetime - 24, () => {
                if(!this.isValid() || !this.hasAmmo()) return;
                vec.trns(this.rotation, this.block.size * 8 / 2);
                this.recoil = this.block.recoilAmount;
                this.heat = 1;
                this.bullet(type, this.rotation);
                this.effects();
                this.consume();
                this.charging = false;
                holeBullet = null;
            });
            this.useAmmo();
            this.reload = 0;
        },
        hasAmmo(){
            return this.items.get(items.lightninAlloy) > 1;
        },
    }, blackhole);
});
Object.assign(blackhole, {
    health : 180*5*5,
    powerUse : 17, 
    shootType : missile,
    size : 5,
    reloadTime : 180,
    range : 336,
    recoilAmount : 6,
    shootSound : lib.loadSound("launch2"),
    coolantMultiplier : 0.7,
    coolantUsage : 0.8,
    shootShake : 5,
});
blackhole.chargeBeginEffect = new Effect(120, cons(e => {
    Draw.color(Color.valueOf("665c9f"), Color.valueOf("be92f9"), e.fout());
    Fill.circle(e.x, e.y, e.fout() * 12 + 4);
    Draw.color();
}));
blackhole.consumes.items(ItemStack.with(
    items.lightninAlloy, 2
));
blackhole.requirements = ItemStack.with(
    Items.graphite, 750,
    Items.silicon, 700,
    Items.thorium, 420,
    Items.plastanium, 250,
    items.lightninAlloy, 175
);
blackhole.buildVisibility = BuildVisibility.shown;
blackhole.category = Category.turret;

exports.blackhole = blackhole;