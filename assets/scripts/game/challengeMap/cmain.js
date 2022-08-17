//Will be added in version 1.4 or 1.3.30 onwards
const lib = require("blib");

//at first i want to draw... sorry, Carrot
function setFloor(target, liquid, floor, variants){
    var liquidBase = liquid.asFloor();
    var floorBase = floor.asFloor();
    target.variants = Math.min(variants, floorBase.variants);
    target.isLiquid = true;
    target.status = liquidBase.status;
    target.liquidDrop = liquidBase.liquidDrop;
    target.cacheLayer = liquidBase.cacheLayer;
}

/* * new Block(name) = Block(name) */

const stoneWater = new Floor("stone-water");
Object.assign(stoneWater, {
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(stoneWater, Blocks.water, Blocks.stone, 2);
stoneWater.asFloor().wall = Blocks.stoneWall;

const charrWater = Floor("char-water");
Object.assign(charrWater, {
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(charrWater, Blocks.water, Blocks.charr, 2);

const basaltWater = Floor("basalt-water");
Object.assign(basaltWater, {
    liquidMultiplier : 0.5,
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(basaltWater, Blocks.water, Blocks.dirt, 2);
basaltWater.asFloor().wall = Blocks.duneWall;

const dirtWater = Floor("dirt-water");
Object.assign(dirtWater, {
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(dirtWater, Blocks.water, Blocks.dirt, 2);
dirtWater.asFloor().wall = Blocks.dirtWall;

const grassWater = Floor("grass-water");
Object.assign(grassWater, {
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(grassWater, Blocks.water, Blocks.grass, 2);
grassWater.asFloor().wall = Blocks.shrubs;

const iceWater = Floor("ice-water");
Object.assign(iceWater, {
    liquidMultiplier : 0.5,
    speedMultiplier : 0.6,
    variants : 2,
    isLiquid : true,
    status : Liquids.cryofluid.effect,
    liquidDrop : Liquids.cryofluid,
    cacheLayer : CacheLayer.water,
    statusDuration : 30,
    albedo : 0.5,
});
iceWater.asFloor().wall = Blocks.iceWall;

const shaleWater = Floor("shale-water");
Object.assign(shaleWater, {
    speedMultiplier : 0.6,
    statusDuration : 50,
    albedo : 0.5,
});
setFloor(shaleWater, Blocks.water, Blocks.dirt, 2);
shaleWater.asFloor().wall = Blocks.shaleWall;

const graphiteBlock = extend(StaticWall, "graphite", {});
graphiteBlock.itemDrop = Items.graphite;
graphiteBlock.variants = 2;
graphiteBlock.solid = true;
graphiteBlock.playerUnmineable = true;

const thoriumBlock = extend(StaticWall, "thorium", {});
thoriumBlock.itemDrop = Items.thorium;
thoriumBlock.variants = 2;
thoriumBlock.solid = true;
thoriumBlock.playerUnmineable = true;

const pyratiteBlock = extend(StaticWall, "pyratite", {});
pyratiteBlock.itemDrop = Items.pyratite;
pyratiteBlock.variants = 2;
pyratiteBlock.solid = true;
pyratiteBlock.playerUnmineable = true;

const dark = extend(Floor, "dark-panel", {});
dark.variants = 0;
const light = extend(Floor, "light-dark-panel", {});
light.variants = 0;
light.attributes.set(Attribute.heat, 0.25);
light.attributes.set(Attribute.water, -0.1);
Object.assign(light, {
    emitLight : true,
    lightRadius : 30,
    lightColor : Color.valueOf("bf92f955"),
});

/*function child(parent, grid){
    const s = new Sector(parent, grid);
    s.threat = 0.7;
    return s;
}*/
/*const challenge = new JavaAdapter(Planet, {
    load(){
        this.meshLoader = prov(() => new HexMesh(challenge, 6));
        this.super$load();
    },
}, "challenge", Planets.sun, 1);
lib.setPlanet(challenge, 3);
challenge.generator = new SerpuloPlanetGenerator();
challenge.atmosphereColor = Color.valueOf("d31e1e");
challenge.accessible = true;
challenge.atmosphereRadIn = 0.04;
challenge.atmosphereRadOut = 0.3;
challenge.startSector = 12;
challenge.alwaysUnlocked = true;
challenge.meshLoader = prov(() => new HexMesh(challenge, 6));
exports.challenge = challenge;

const start = new SectorPreset("start", challenge, 12);
start.difficulty = 7;
exports.start = start;

const sporeArea = new SectorPreset("SporeArea", challenge, 101);
sporeArea.difficulty = 10;
exports.sporeArea = sporeArea;

const rail = new SectorPreset("rail", challenge, 58);
rail.captureWave = 129;
rail.difficulty = 10;
exports.rail = rail;

const Darkness = new SectorPreset("Darkness", challenge, 67);
Darkness.captureWave = 60;
Darkness.difficulty = 10;
exports.Darkness = Darkness;

const Colosseum = new SectorPreset("Colosseum", challenge, 98);
Colosseum.captureWave = 120;
Colosseum.difficulty = 10;
exports.Colosseum = Colosseum;

const RadiationIslands = new SectorPreset("RadiationIslands", challenge, 79);
RadiationIslands.captureWave = 56;
RadiationIslands.difficulty = 10;
exports.RadiationIslands = RadiationIslands;

const GlacialValley = new SectorPreset("GlacialValley", challenge, 58);
GlacialValley.difficulty = 10;
exports.GlacialValley = GlacialValley;*/