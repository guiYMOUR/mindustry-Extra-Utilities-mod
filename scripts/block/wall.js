const absorbDamageChance = 0.08;
const cor1 = Color.valueOf("b9ff00");
const cor2 = Color.valueOf("b9ff22");

const clWall = extendContent(Wall,"clWall",{});
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

const clWallL = extendContent(Wall,"clWall-large",{});
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

const aws = extendContent(Wall,"allWallSmall",{
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-absorbDamageChance", absorbDamageChance * 100));
    },
});
aws.size = 1;
aws.health = 1180;
aws.lightningChance = 0.06;
aws.chanceDeflect = 15;
aws.flashHit = true;
aws.absorbLasers = true;
aws.insulated = true;

aws.buildType = prov(() => {
    return new JavaAdapter(Wall.WallBuild, {
        damage(damage){
            this.super$damage(damage);
            if(Mathf.chance(absorbDamageChance)){
                this.heal(damage * 2);
                Fx.healBlockFull.at(this.x, this.y, this.block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
            }
        },
    }, aws);
});
aws.requirements = ItemStack.with(
    Items.silicon, 6,
    Items.plastanium, 4,
    Items.phaseFabric, 4,
    Items.surgeAlloy, 5
);
aws.buildVisibility = BuildVisibility.shown;
aws.category = Category.defense;
exports.aws = aws;

const awl = extendContent(Wall,"allWallLarge",{
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-absorbDamageChance", absorbDamageChance * 100));
    },
});
awl.size = 2;
awl.health = 1180 * 4;
awl.lightningChance = 0.06;
awl.chanceDeflect = 15;
awl.flashHit = true;
awl.absorbLasers = true;
awl.insulated = true;

awl.buildType = prov(() => {
    return new JavaAdapter(Wall.WallBuild, {
        damage(damage){
            this.super$damage(damage);
            if(Mathf.chance(absorbDamageChance)){
                this.heal(damage * 4);
                Fx.healBlockFull.at(this.x, this.y, this.block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
            }
        },
    }, awl);
});
awl.requirements = ItemStack.with(
    Items.silicon, 24,
    Items.plastanium, 14,
    Items.phaseFabric, 14,
    Items.surgeAlloy, 18
);
awl.buildVisibility = BuildVisibility.shown;
awl.category = Category.defense;
exports.awl = awl;
