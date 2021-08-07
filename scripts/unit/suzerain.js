var ability = require("other/ability");

function casingContinue(lifetime, shots){
    return new Effect(lifetime, cons(e => {
        Draw.z(Layer.bullet);
        for(var a = 0; a < shots; a++){
            var time = lifetime / shots;
            e.scaled(time * a, cons(b => {
                Draw.color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, b.fin());
                Draw.alpha(b.fout(0.5));
                var rot = Math.abs(e.rotation) + 90;
                var i = -Mathf.sign(e.rotation);
                var len = (4 + b.finpow() * 9) * i;
                var lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20 * b.fin()) * i;
                Draw.rect(Core.atlas.find("casing"),
                e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3 * b.fin()),
                e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3 * b.fin()),
                3, 6,
                rot + e.fin() * 50 * i
                );
            }));
        }
    }));
}

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
spark.length = 150;
spark.damage = 180;
spark.width = 20;
spark.serrationLenScl = 7;
spark.serrationSpaceOffset = 60;
spark.serrationFadeOffset = 0;
spark.serrations = 10;
spark.serrationWidth = 6;
spark.shootEffect = Fx.sparkShoot;
spark.smokeEffect = Fx.sparkShoot;

const suzerain = extendContent(UnitType, 'suzerain', {
    /*//我摊牌了，这个木大
    remove(){
        if(this.constructor.get().healthf() < 100/61000){
            this.super$remove();
        }
    },
    //kill(){    },*/
});
suzerain.constructor = prov(() => extend(UnitTypes.reign.constructor.get().class, {
    /*remove(){
        if(this.healthf() < 100/61000){
            this.super$remove();
        }
    },*/
}));
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon2");
        w.shake = 4;
        w.shootY = 7;
        w.bullet = spark;
        w.rotate = true;
        w.rotateSpeed = 2;
        w.x = 15;
        w.y = -2;
        w.shootSound = Sounds.railgun;
        w.reload = 27;
        w.recoil = 4;
        return w;
    })()
);
suzerain.weapons.add(
    (() => {
        const w = new Weapon("btm-suzerain-weapon");
        const shots = 5;
        w.shake = 4;
        w.shootY = 11;
        w.top = false;
        w.shots = shots;
        w.inaccuracy = 1;
        w.shotDelay = 3;
        w.ejectEffect = casingContinue(90, shots);
        w.bullet = Bullets.standardThoriumBig;
        w.rotate = false;
        w.x = 30;
        w.y = 1;
        w.shootSound = Sounds.bang;
        w.reload = 24;
        w.recoil = 5;
        return w;
    })()
);

suzerain.abilities.add(ability.TerritoryFieldAbility(150, 60 * 4, 216), ability.preventCheatingAbility(true));
suzerain.abilities.add(new ShieldRegenFieldAbility(200, 800, 60 * 6, 200));
suzerain.armor = 16;
suzerain.flying = false;
suzerain.speed = 0.3;
suzerain.hitSize = 40;
suzerain.rotateSpeed = 1.8;
suzerain.canDrown = false;
suzerain.mechStepParticles = true;
suzerain.mechStepShake = 1;
suzerain.mechFrontSway = 1.9;
suzerain.mechSideSway = 0.6;
suzerain.singleTarget = true;
suzerain.health = 63000;
suzerain.itemCapacity = 300;
suzerain.rotateShooting = true;
suzerain.commandLimit = 8;
suzerain.ammoType = new ItemAmmoType(Items.thorium);
exports.suzerain = suzerain;