//引用部分，类似import，对应的是exports导出
const lib = require("blib");
const items = require("game/items");
const bullets = require("other/bullets");
const ReleaseShieldWall = lib.getClass("ExtraUtilities.worlds.blocks.turret.wall.ReleaseShieldWall");
//全能墙的伤害吸收概率
const absorbDamageChance = 0.1;
//吸收时的颜色
const cor1 = Color.valueOf("b9ff00");
const cor2 = Color.valueOf("b9ff22");

//cl:copper lead 就是铜铅墙
//小
const clWall = extend(Wall,"clWall",{});
clWall.size = 1;
clWall.health = 420;
clWall.absorbLasers = true;
clWall.hasPower = true;
clWall.consumesPower = false;
clWall.outputsPower = true;
clWall.requirements = ItemStack.with(
    Items.copper, 5,
    Items.lead, 6
);
clWall.buildVisibility = BuildVisibility.shown;
clWall.category = Category.defense;
exports.clWall = clWall;
//大
const clWallL = extend(Wall,"clWall-large",{});
clWallL.size = 2;
clWallL.health = 1680;
clWallL.absorbLasers = true;
clWallL.hasPower = true;
clWallL.consumesPower = false;
clWallL.outputsPower = true;
clWallL.requirements = ItemStack.with(
    Items.copper, 20,
    Items.lead, 24
);
clWallL.buildVisibility = BuildVisibility.shown;
clWallL.category = Category.defense;
exports.clWallL = clWallL;

//全能墙部分
//小
// const aws = extend(Wall,"allWallSmall",{
//     setStats() {
//         this.super$setStats();
//         this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-absorbDamageChance", absorbDamageChance * 100));
//     },
// });
// aws.size = 1;
// aws.health = 1280;
// aws.lightningChance = 0.06;
// aws.chanceDeflect = 20;
// aws.flashHit = true;
// aws.absorbLasers = true;
// aws.insulated = true;
// aws.update = true;
// aws.buildCostMultiplier = 3;
//
// aws.buildType = prov(() => {
//     var damageAbsorb = 0;
//
//     const block = aws;
//
//     return new JavaAdapter(Wall.WallBuild, {
//         updateTile(){
//             if(damageAbsorb > 0){
//                 this.heal(damageAbsorb * 2);
//                 Fx.healBlockFull.at(this.x, this.y, block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
//                 damageAbsorb = 0;
//             }
//         },
//         handleDamage(amount){
//             if(Mathf.chance(absorbDamageChance)) damageAbsorb += amount;
//             return amount;
//         },
//     }, aws);
// });
// aws.requirements = ItemStack.with(
//     Items.silicon, 6,
//     Items.plastanium, 4,
//     Items.phaseFabric, 4,
//     Items.surgeAlloy, 5
// );
// aws.buildVisibility = BuildVisibility.shown;
// aws.category = Category.defense;
// exports.aws = aws;
// //大
// const awl = extend(Wall,"allWallLarge",{
//     setStats() {
//         this.super$setStats();
//         this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-absorbDamageChance", absorbDamageChance * 100));
//     },
// });
// awl.size = 2;
// awl.health = 1280 * 4;
// awl.lightningChance = 0.06;
// awl.chanceDeflect = 20;
// awl.flashHit = true;
// awl.absorbLasers = true;
// awl.insulated = true;
// awl.update = true;
// awl.buildCostMultiplier = 3;
//
// awl.buildType = prov(() => {
//     var damageAbsorb = 0;
//
//     const block = awl;
//
//     return new JavaAdapter(Wall.WallBuild, {
//         updateTile(){
//             if(damageAbsorb > 0){
//                 this.heal(damageAbsorb * 4);
//                 Fx.healBlockFull.at(this.x, this.y, block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
//                 damageAbsorb = 0;
//             }
//         },
//         handleDamage(amount){
//             if(Mathf.chance(absorbDamageChance)) damageAbsorb += amount;
//             return amount;
//         },
//     }, awl);
// });
// awl.requirements = ItemStack.with(
//     Items.silicon, 24,
//     Items.plastanium, 14,
//     Items.phaseFabric, 14,
//     Items.surgeAlloy, 18
// );
// awl.buildVisibility = BuildVisibility.shown;
// awl.category = Category.defense;
// exports.awl = awl;

//吸收伤害的概率，意味着不是所有伤害都会给他充能
// const chargeChance = 0.8;
// //充能墙部分
// //小
// const rws = extend(Wall,"rws",{
//     setBars(){
//         this.super$setBars();
//         this.bars.add("charge", func(entity => {
//             var bar = new Bar(prov(() => Core.bundle.get("bar.btm-charge")), prov(() => items.lightninAlloy.color), floatp(() => entity.getCharge()));
//             return bar;
//         }));
//     },
//     setStats() {
//         this.super$setStats();
//         this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-charge", 180, chargeChance * 100));
//     },
// });
// rws.size = 1;
// rws.health = 1100;
// rws.lightningChance = 0.06;
// rws.chanceDeflect = 10;
// rws.flashHit = true;
// rws.absorbLasers = true;
// rws.insulated = true;
// rws.update = true;
// rws.placeableLiquid = true;
// rws.buildType = prov(() => {
//     var damage = 0;
//     var shieldBullet = null;
//     var shieldLife = 0;
//     var acceptDamage = true;
//     const max = 180;
//     const lifetime = 150;
//
//     const block = rws;
//
//     return new JavaAdapter(Wall.WallBuild, {
//         //处理命中伤害的地方
//         multDamage(v){
//             if(Mathf.chance(chargeChance)) damage += v;
//             if(damage > max){
//                 //护盾子弹看'other/bullets'
//                 shieldBullet = bullets.shieldBullet({ splashDamageRadius : block.size * 64, }).create(this.tile.build, this.team, this.x, this.y, 0);
//                 shieldLife = lifetime;
//                 acceptDamage = false;
//                 damage = 0;
//             }
//         },
//         //用于bar
//         getCharge(){
//             return damage / max;
//         },
//         //墙体自身update是false，要运行update需要把update接口改成true
//         updateTile(){
//             //用于逻辑显示屏显示，显示接口为timeScale
//             this.timeScale = this.getCharge();
//             if(shieldLife > 0){
//                 if(shieldBullet != null){
//                     shieldBullet.set(this.x, this.y);
//                     shieldBullet.time = 0;
//                 }
//                 shieldLife -= Time.delta;
//             } else {
//                 shieldBullet = null;
//                 acceptDamage = true;
//             }
//         },
//         //原版反馈伤害的func
//         handleDamage(amount){
//             if(acceptDamage){
//                 this.multDamage(amount);
//             }
//             return amount;
//         },
//         //储存
//         write(write) {
//             this.super$write(write);
//             write.f(damage);
//         },
//         //读取
//         read(read, revision) {
//             this.super$read(read, revision);
//             damage = read.f();
//         },
//     }, rws);
// });
// rws.requirements = ItemStack.with(
//     items.lightninAlloy, 6
// );
// rws.buildVisibility = BuildVisibility.shown;
// rws.category = Category.defense;
// exports.rws = rws;
// //大，部分和小墙同理，看小墙注释
// const rwl = extend(Wall,"rwl",{
//     setBars(){
//         this.super$setBars();
//         this.bars.add("charge", func(entity => {
//             var bar = new Bar(prov(() => Core.bundle.get("bar.btm-charge")), prov(() => items.lightninAlloy.color), floatp(() => entity.getCharge()));
//             return bar;
//         }));
//     },
//     setStats() {
//         this.super$setStats();
//         this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-charge", 180*4, chargeChance * 100));
//     },
// });
// rwl.size = 2;
// rwl.health = 1100*4;
// rwl.lightningChance = 0.06;
// rwl.chanceDeflect = 10;
// rwl.flashHit = true;
// rwl.absorbLasers = true;
// rwl.insulated = true;
// rwl.update = true;
// rwl.placeableLiquid = true;
// rwl.buildType = prov(() => {
//     var damage = 0;
//     var shieldBullet = null;
//     var shieldLife = 0;
//     var acceptDamage = true;
//     const max = 180*4;
//     const lifetime = 150;
//
//     const block = rwl;
//
//     return new JavaAdapter(Wall.WallBuild, {
//         multDamage(v){
//             if(Mathf.chance(chargeChance)) damage += v;
//             if(damage > max){
//                 shieldBullet = bullets.shieldBullet({ splashDamageRadius : block.size * 64, }).create(this.tile.build, this.team, this.x, this.y, 0);
//                 shieldLife = lifetime;
//                 acceptDamage = false;
//                 damage = 0;
//             }
//         },
//         getCharge(){
//             return damage / max;
//         },
//         updateTile(){
//             this.timeScale = this.getCharge()
//             if(shieldLife > 0){
//                 if(shieldBullet != null){
//                     shieldBullet.set(this.x, this.y);
//                     shieldBullet.time = 0;
//                 }
//                 shieldLife -= Time.delta;
//             } else {
//                 shieldBullet = null;
//                 acceptDamage = true;
//             }
//         },
//         handleDamage(amount){
//             if(acceptDamage){
//                 this.multDamage(amount);
//             }
//             return amount;
//         },
//         write(write) {
//             this.super$write(write);
//             write.f(damage);
//         },
//         read(read, revision) {
//             this.super$read(read, revision);
//             damage = read.f();
//         },
//     }, rwl);
// });
// rwl.requirements = ItemStack.with(
//     items.lightninAlloy, 24
// );
// rwl.buildVisibility = BuildVisibility.shown;
// rwl.category = Category.defense;
// exports.rwl = rwl;
const rws = new ReleaseShieldWall("rws");
rws.size = 1;
rws.health = 1100;
rws.lightningChance = 0.06;
rws.chanceDeflect = 10;
rws.flashHit = true;
rws.absorbLasers = true;
rws.insulated = true;
rws.placeableLiquid = true;
rws.armor = 10;
rws.requirements = ItemStack.with(
    items.lightninAlloy, 6
);
rws.buildVisibility = BuildVisibility.shown;
rws.category = Category.defense;
exports.rws = rws;
const rwl = new ReleaseShieldWall("rwl");
rwl.size = 2;
rwl.maxHandle = 180 * 4;
rwl.health = 1100 * 4;
rwl.lightningChance = 0.06;
rwl.chanceDeflect = 10;
rwl.flashHit = true;
rwl.absorbLasers = true;
rwl.insulated = true;
rwl.placeableLiquid = true;
rwl.armor = 12;
rwl.requirements = ItemStack.with(
    items.lightninAlloy, 24
);
rwl.buildVisibility = BuildVisibility.shown;
rwl.category = Category.defense;
exports.rwl = rwl;