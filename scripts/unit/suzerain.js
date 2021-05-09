var ability = require("other/ability");

const spark = extend(ShrapnelBulletType, {
    draw(b){
        var realLength = b.fdata;
        Draw.color(this.fromColor, b.team.color, b.fin());
        for(var i = 0; i < Math.floor(this.serrations * realLength / this.length); i++){
            Tmp.v1.trns(b.rotation(), i * this.serrationSpacing);
            var sl = Mathf.clamp(b.fout() - this.serrationFadeOffset) * (this.serrationSpaceOffset - i * this.serrationLenScl);
            Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.serrationWidth, sl, b.rotation() + 90);
            Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, this.serrationWidth, sl, b.rotation() - 90);
        }
        Drawf.tri(b.x, b.y, this.width * b.fout(), (realLength + 50), b.rotation());
        Drawf.tri(b.x, b.y, this.width * b.fout(), 10, b.rotation() + 180);
        Draw.reset();
    }
});
spark.length = 140;
spark.damage = 150;
spark.width = 20;
spark.serrationLenScl = 7;
spark.serrationSpaceOffset = 60;
spark.serrationFadeOffset = 0;
spark.serrations = 10;
spark.serrationWidth = 6;
spark.shootEffect = Fx.sparkShoot;
spark.smokeEffect = Fx.sparkShoot;

const suzerain = extendContent(UnitType, 'suzerain', {});
suzerain.constructor = prov(() => extend(UnitTypes.reign.constructor.get().class, {}));
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon2");
        w.shake = 4;
        w.shootY = 7;
        w.bullet = spark;
        w.rotate = true;
        w.rotateSpeed = 2;
        w.x = 9;
        w.y = -2;
        w.shootSound = Sounds.railgun;
        w.reload = 30;
        w.recoil = 4;
        return w;
    })()
);
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon");
        w.shake = 4;
        w.shootY = 11;
        w.top = false;
        w.shots = 5;
        w.inaccuracy = 1;
        w.shotDelay = 3;
        w.bullet = Bullets.standardThoriumBig;
        w.rotate = false;
        w.x = 22;
        w.y = 1;
        w.shootSound = Sounds.bang;
        w.reload = 24;
        w.recoil = 5;
        return w;
    })()
);

suzerain.abilities.add(ability.TerritoryFieldAbility(150, 60 * 4, 200));
suzerain.abilities.add(new ShieldRegenFieldAbility(200, 800, 60 * 6, 200));
suzerain.armor = 14;
suzerain.flying = false;
suzerain.speed = 0.3;
suzerain.hitSize = 26;
suzerain.rotateSpeed = 1.8;
suzerain.canDrown = false;
suzerain.mechStepParticles = true;
suzerain.mechStepShake = 1;
suzerain.mechFrontSway = 1.9;
suzerain.mechSideSway = 0.6;
suzerain.health = 61000;
suzerain.itemCapacity = 300;
suzerain.rotateShooting = true;
suzerain.commandLimit = 8;
exports.suzerain = suzerain;