const status = require("other/status");
const items = require("game/items");
const lib = require("blib");
const DrawRainbow = lib.getClass("ExtraUtilities.worlds.drawer.DrawRainbow");
//const EUStatusEffects = lib.getClass("ExtraUtilities.content.EUStatusEffects");

var gone = lib.Fx.gone;
const bSp = lib.aModName + "-shotgunShot";

const r1 = extend(BasicBulletType,{});
r1.sprite = bSp;
r1.speed = 7;
r1.damage = 60;
r1.width = 15;
r1.height = 24;
r1.shrinkY = 0;
r1.despawnEffect = gone(Color.valueOf("ffe800"));
r1.frontColor = Color.valueOf("ffe800");
r1.backColor = Color.valueOf("ffe800");
const r2 = extend(BasicBulletType,{});
r2.sprite = bSp;
r2.speed = 7;
r2.damage = 70;
r2.width = 15;
r2.height = 24;
r2.shrinkY = 0;
r2.makeFire = true;
r2.despawnEffect = gone(Color.valueOf("ff5252"));
r2.frontColor = Color.valueOf("ff5252");
r2.backColor = Color.valueOf("ff5252");
r2.status = StatusEffects.melting;
const r3 = extend(BasicBulletType,{});
r3.sprite = bSp;
r3.speed = 7;
r3.damage = 50;
r3.width = 15;
r3.height = 24;
r3.shrinkY = 0;
r3.despawnEffect = gone(Color.valueOf("d95eec"));
r3.frontColor = Color.valueOf("d95eec");
r3.backColor = Color.valueOf("d95eec");
r3.status = StatusEffects.unmoving;
r3.statusDuration = 20;
const r4 = extend(BasicBulletType,{});
r4.sprite = bSp;
r4.speed = 7;
r4.damage = 50;
r4.width = 15;
r4.height = 24;
r4.shrinkY = 0;
r4.despawnEffect = gone(Color.valueOf("5eecec"));
r4.frontColor = Color.valueOf("5eecec");
r4.backColor = Color.valueOf("5eecec");
r4.status = StatusEffects.freezing;
r4.statusDuration = 20;
const r5 = extend(BasicBulletType,{});
r5.sprite = bSp;
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

const rainbow = extend(PowerTurret, "rainbow", {
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.ammo);
        /*for(var i = 0; i < bullet.length; i++){
            this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, bullet[i])));
        }*/
        //The computer player says it will retreat.↑
        //So I took it step by step.↓
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, r2)));
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, r1)));
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, r5)));
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, r4)));
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, r3)));
    },
});
rainbow.buildType = prov(() => {
    const tr = new Vec2();
    //const tr2 = new Vec2();
    const block = rainbow;
    var rotation = 0;
    var x = 0, y = 0;
    return new JavaAdapter(PowerTurret.PowerTurretBuild, {
        shoot(type){
            //rotation = this.rotation;
            for(var i = 0; i < block.shots; i++){
                this.bullet(bullet[i], 0, 0, (i - (block.shots / 2)) * block.spread, null);
            }
        },
    }, rainbow);
});
rainbow.shootEffect = lib.Fx.rainbowShoot;

rainbow.drawer = new DrawMulti(new DrawTurret(), new DrawRainbow(2, 5));

rainbow.consumePower(18);
//rainbow.recoilTime = 2;
rainbow.spread = 5;
rainbow.recoil = 4;
rainbow.size = 3;
rainbow.range = 28 * 8;
rainbow.reload = 72;
rainbow.shots = 5;
rainbow.shootSound = Sounds.shootBig;
rainbow.health = 200 * 3 * 3;
rainbow.shootType = r1;
lib.Coolant(rainbow, 0.25);
rainbow.requirements = ItemStack.with(
    Items.lead, 240,
    Items.silicon, 200,
    Items.metaglass, 150,
    Items.thorium, 90,
    items.crispSteel, 55
);
rainbow.buildVisibility = BuildVisibility.shown;
rainbow.category = Category.turret;
exports.rainbow = rainbow;