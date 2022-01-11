const items = require("game/items");
const bullets = require("other/bullets");

const absorbDamageChance = 0.1;
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
aws.health = 1280;
aws.lightningChance = 0.06;
aws.chanceDeflect = 20;
aws.flashHit = true;
aws.absorbLasers = true;
aws.insulated = true;
aws.update = true;
aws.buildCostMultiplier = 3;

aws.buildType = prov(() => {
    var damageAbsorb = 0;
    return new JavaAdapter(Wall.WallBuild, {
        updateTile(){
            if(damageAbsorb > 0){
                this.heal(damageAbsorb * 2);
                Fx.healBlockFull.at(this.x, this.y, this.block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
                damageAbsorb = 0;
            }
        },
        handleDamage(amount){
            if(Mathf.chance(absorbDamageChance)) damageAbsorb += amount;
            return amount;
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
awl.health = 1280 * 4;
awl.lightningChance = 0.06;
awl.chanceDeflect = 20;
awl.flashHit = true;
awl.absorbLasers = true;
awl.insulated = true;
awl.update = true;
awl.buildCostMultiplier = 3;

awl.buildType = prov(() => {
    var damageAbsorb = 0;
    return new JavaAdapter(Wall.WallBuild, {
        updateTile(){
            if(damageAbsorb > 0){
                this.heal(damageAbsorb * 4);
                Fx.healBlockFull.at(this.x, this.y, this.block.size, Tmp.c1.set(cor1).lerp(cor2, 0.3));
                damageAbsorb = 0;
            }
        },
        handleDamage(amount){
            if(Mathf.chance(absorbDamageChance)) damageAbsorb += amount;
            return amount;
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

const chargeChance = 0.8;
const rws = extendContent(Wall,"rws",{
    setBars(){
        this.super$setBars();
        this.bars.add("charge", func(entity => {
            var bar = new Bar(prov(() => Core.bundle.get("bar.btm-charge")), prov(() => items.lightninAlloy.color), floatp(() => entity.getCharge()));
            return bar;
        }));
    },
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-charge", 180, chargeChance * 100));
    },
});
rws.size = 1;
rws.health = 1100;
rws.lightningChance = 0.06;
rws.chanceDeflect = 10;
rws.flashHit = true;
rws.absorbLasers = true;
rws.insulated = true;
rws.update = true;
rws.placeableLiquid = true;
rws.buildType = prov(() => {
    var damage = 0;
    var shieldBullet = null;
    var shieldLife = 0;
    var acceptDamage = true;
    const max = 180;
    const lifetime = 150;
    return new JavaAdapter(Wall.WallBuild, {
        multDamage(v){
            if(Mathf.chance(chargeChance)) damage += v;
            if(damage > max){
                shieldBullet = bullets.shieldBullet({ splashDamageRadius : this.block.size * 64, }).create(this.tile.build, this.team, this.x, this.y, 0);
                shieldLife = lifetime;
                acceptDamage = false;
                damage = 0;
            }
        },
        getCharge(){
            return damage / max;
        },
        updateTile(){
            this.timeScale = this.getCharge();
            if(shieldLife > 0){
                if(shieldBullet != null){
                    shieldBullet.set(this.x, this.y);
                    shieldBullet.time = 0;
                }
                shieldLife -= Time.delta;
            } else {
                shieldBullet = null;
                acceptDamage = true;
            }
        },
        handleDamage(amount){
            if(acceptDamage){
                this.multDamage(amount);
            }
            return amount;
        },
    }, rws);
});
rws.requirements = ItemStack.with(
    items.lightninAlloy, 6
);
rws.buildVisibility = BuildVisibility.shown;
rws.category = Category.defense;
exports.rws = rws;

const rwl = extendContent(Wall,"rwl",{
    setBars(){
        this.super$setBars();
        this.bars.add("charge", func(entity => {
            var bar = new Bar(prov(() => Core.bundle.get("bar.btm-charge")), prov(() => items.lightninAlloy.color), floatp(() => entity.getCharge()));
            return bar;
        }));
    },
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-charge", 180*4, chargeChance * 100));
    },
});
rwl.size = 2;
rwl.health = 1100*4;
rwl.lightningChance = 0.06;
rwl.chanceDeflect = 10;
rwl.flashHit = true;
rwl.absorbLasers = true;
rwl.insulated = true;
rwl.update = true;
rwl.placeableLiquid = true;
rwl.buildType = prov(() => {
    var damage = 0;
    var shieldBullet = null;
    var shieldLife = 0;
    var acceptDamage = true;
    const max = 180*4;
    const lifetime = 150;
    return new JavaAdapter(Wall.WallBuild, {
        multDamage(v){
            if(Mathf.chance(chargeChance)) damage += v;
            if(damage > max){
                shieldBullet = bullets.shieldBullet({ splashDamageRadius : this.block.size * 64, }).create(this.tile.build, this.team, this.x, this.y, 0);
                shieldLife = lifetime;
                acceptDamage = false;
                damage = 0;
            }
        },
        getCharge(){
            return damage / max;
        },
        updateTile(){
            this.timeScale = this.getCharge()
            if(shieldLife > 0){
                if(shieldBullet != null){
                    shieldBullet.set(this.x, this.y);
                    shieldBullet.time = 0;
                }
                shieldLife -= Time.delta;
            } else {
                shieldBullet = null;
                acceptDamage = true;
            }
        },
        handleDamage(amount){
            if(acceptDamage){
                this.multDamage(amount);
            }
            return amount;
        },
        write(write) {
            this.super$write(write);
            write.f(damage);
        },
        read(read, revision) {
            this.super$read(read, revision);
            damage = read.f();
        },
    }, rwl);
});
rwl.requirements = ItemStack.with(
    items.lightninAlloy, 24
);
rwl.buildVisibility = BuildVisibility.shown;
rwl.category = Category.defense;
exports.rwl = rwl;
