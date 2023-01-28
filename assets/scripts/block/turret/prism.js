//引用部分，类似import，对应的是exports导出
const lib = require("blib");
const items = require("game/items");
const Prism = lib.getClass("ExtraUtilities.worlds.blocks.turret.Prism");
const PrismLaser = lib.getClass("ExtraUtilities.worlds.entity.bullet.PrismLaser")
const DrawRainbow = lib.getClass("ExtraUtilities.worlds.drawer.DrawRainbow");
const prism = new Prism("prism");
prism.shootEffect = Fx.shootBigSmoke2;
prism.shootCone = 40;
prism.recoil = 4;
prism.shootX = 1;
prism.recoilPow = 0.9;
prism.recoilTime = 60;
prism.size = 5;
prism.shake = 2;
prism.range = 36 * 8;
prism.shootDuration = 10*60;
prism.reloadTime = 200;
prism.consumePower(40);
// prism.shootSound = lib.loadSound("prismS");
// lib.loadSound("prism-beam.ogg", (a) => {
//     prism.loopSound = a;
// });
// prism.loopSoundVolume = 2;
//prism.canOverdrive = false;

prism.shootType = (() => {
    // const width = 8;
    // const strokes = [1.2, 1.1, 1, 0.9];
    // const tscales = [1, 0.74, 0.5, 0.24];
    // const lenscales = [0.92, 1, 1.017, 1.025];
    // const length = 32 * 8;
    // const spaceMag = 35;
    // const oscMag = 1.5;
    // const oscScl = 0.8;
    // const cl = new JavaAdapter(ContinuousLaserBulletType, {
    //     draw(b){
    //         var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
    //         var wide = Mathf.clamp(1 - b.owner.getBulletHeat());
    //
    //         Draw.blend(Blending.additive);
    //         for(var s = 0; s < 4; s++){
    //             Draw.color(Color.valueOf("ff0000").shiftHue((Time.time * 2) + b.data));
    //             for(var i = 0; i < tscales.length; i++){
    //                 Tmp.v1.trns(b.rotation() + 180, (lenscales[i] - 1) * spaceMag);
    //                 Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * wide * strokes[s] * tscales[i]);
    //                 Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), length * lenscales[i], false);
    //             }
    //         }
    //         Draw.reset();
    //         Draw.blend();
    //      },
    // });
    // cl.damage = 40;
    // cl.hitEffect = new Effect(16, cons(e => {
    //     Draw.blend(Blending.additive);
    //     Draw.color(Color.valueOf("ff0000ff").shiftHue(Time.time * 2.0));
    //     Lines.stroke(e.fout() * 1.5);
    //     const hl = new Floatc2({get: function(x, y){
    //         const ang = Mathf.angle(x, y);
    //         Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 8 + 1.5);
    //         }});
    //     Angles.randLenVectors(e.id, 1, e.finpow() * 70.0, e.rotation, 80.0, hl);
    //     Draw.blend();
    //     Draw.reset();
    // }));
    const cl = new PrismLaser(900/12, 11, prism.range);
    cl.drawSize = 320;
    cl.incendChance = -1;
    cl.incendAmount = -1;
    cl.ammoMultiplier = 6;
    return cl;
})();
prism.drawer = new DrawMulti(new DrawTurret("reinforced-"), new DrawRainbow(2, 8));
prism.health = 220 * 5 * 5;
prism.coolant = prism.consumeCoolant(1);
prism.coolantMultiplier = 1;
prism.requirements = ItemStack.with(
    Items.lead, 620,
    Items.silicon, 550,
    Items.metaglass, 430,
    Items.thorium, 385,
    items.lightninAlloy, 300
);
prism.buildVisibility = BuildVisibility.shown;
prism.category = Category.turret;
exports.prism = prism;