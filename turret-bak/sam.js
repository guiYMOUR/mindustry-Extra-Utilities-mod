const lib = require("blib");
const mb = extend(BasicBulletType, {
    update(b){
        if(b.time < this.homingDelay/b.owner.reloadSpeed()){
            b.vel.trns(b.owner.rotation, 0.1);
        } else {
            var target = Units.closestTarget(b.team, b.x, b.y, this.homingRange,
                boolf(e => (e.isGrounded() && this.collidesGround) || (e.isFlying() && this.collidesAir)),
                boolf(t => this.collidesGround)
            );
            if(target != null) {
                b.vel.setAngle(Mathf.slerpDelta(b.rotation(), b.angleTo(target), this.homingPower));
                b.vel.trns(b.rotation(), this.speed);
            } else {
                b.vel.trns(b.owner.rotation, this.speed);
            }
            if(Mathf.chanceDelta(this.trailChance)){
                this.trailEffect.at(b.x, b.y, this.trailParam, this.trailColor);
            }
        }
    },
    draw(b){
        var height = this.height * Math.min(1, (b.time * 3)/(this.homingDelay/b.owner.baseReloadSpeed()));
        Draw.z(Layer.turret);
        Draw.color();
        Draw.rect(this.backRegion, b.x, b.y, this.width, height, b.rotation() - 90);
        //Draw.color(this.frontColor);
        Draw.rect(this.frontRegion, b.x, b.y, this.width, height, b.rotation() - 90);

        Draw.reset();
    },
    despawned(b){
        this.super$despawned(b);
        new Effect(15, cons(e => {
            Draw.color(Color.white, Color.valueOf("a4a4a4"), e.fin());
            Lines.stroke(e.fout() * 2 + 0.2);
            Lines.circle(e.x, e.y, e.fin() * this.splashDamageRadius);
        })).at(b.x, b.y);
    },
});
mb.sprite = "btm-mb";
mb.shootEffect = Fx.none;
mb.smokeEffect = Fx.none;
mb.width = 12;
mb.height = 18;
mb.damage = 300;
mb.splashDamage = 330;
mb.splashDamageRadius = 112;
mb.homingPower = 1;
mb.homingDelay = 60;
mb.homingRange = 888;
mb.lifetime = 300;
mb.speed = 8;
mb.trailChance = 0.6;
mb.collidesGround = false;

const sam = extendContent(Turret, 'sam', {
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.ammo, StatValues.ammo(OrderedMap.of(this, mb)));
    },
});
sam.buildType = prov(() => {
    var bullet = null;
    var shotCounter = 0;
    const tr = new Vec2();
    const tr2 = new Vec2();
    //var region = Core.atlas.find("btm-sam-top");
    //var bottom = Core.atlas.find("btm-sam-bottom");
    var drawer = cons(tile => Draw.rect(Core.atlas.find("btm-sam-top"), tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90));
    var bottomDrawer = cons(tile => Draw.rect(Core.atlas.find("btm-sam-bottom"), tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90));
    return new JavaAdapter(Turret.TurretBuild, {
        reloadSpeed(){
            return this.delta() * this.baseReloadSpeed()
        },
        /*delta(){
            return Time.delta * Math.min(3.75, this.timeScale);
        },*/
        updateTile(){
            this.super$updateTile();
            var i = (shotCounter % this.block.shots) - (this.block.shots - 1)/2;
            if(bullet != null){
                if(bullet.time < 60/this.reloadSpeed()){
                    tr.trns(this.rotation - 90, this.block.spread * i, (this.block.size * Vars.tilesize / 2) - (this.block.size * Vars.tilesize) * (1 - (bullet.time * 0.6)/(60/this.reloadSpeed())));
                    bullet.rotation(this.rotation);
                    bullet.set(this.x + tr.x, this.y + tr.y);
                } else {
                    bullet = null;
                }
            }
        },
        draw(){
            Draw.rect(this.block.baseRegion, this.x, this.y);
            Draw.color();
            tr2.trns(this.rotation, -this.recoil);
            Drawf.shadow(Core.atlas.find("btm-sam-top"), this.x + tr2.x - this.block.elevation, this.y + tr2.y - this.block.elevation, this.rotation - 90);
            bottomDrawer.get(this);
            Draw.z(Layer.turret + 1);
            drawer.get(this);

            /*if(this.heatRegion != Core.atlas.find("error")){
                this.block.heatDrawer.get(this);
            }*/
        },
        shoot(type){
            this.bullet(type, this.rotation);
            shotCounter ++;
            Time.run(60/this.reloadSpeed(), () => {
                if(!this.consValid()) return;
                this.recoil = this.block.recoilAmount;
                this.heat = 1;
                this.useAmmo();
                this.effects();
            });
        },
        bullet(type, angle){
            tr.trns(angle, this.block.size * Vars.tilesize / 2);
            bullet = type.create(this.tile.build, this.team, this.x + tr.x, this.y + tr.y, angle);
        },
        hasAmmo() { return this.consValid(); },
        peekAmmo() { return mb; },
        useAmmo() { 
            this.consume();
            return mb; 
        },
    }, sam);
});
sam.health = 1960;
sam.recoilAmount = 3;
sam.range = 888;
sam.shots = 2;
sam.spread = 7;
sam.reloadTime = 120;
sam.targetGround = false;
sam.size = 3;
sam.shootSound = lib.loadSound("launch");
sam.consumes.items(ItemStack.with(
    Items.silicon, 3,
    Items.blastCompound, 2
));
sam.requirements = ItemStack.with(
    Items.lead, 500,
    Items.silicon, 550,
    Items.graphite, 305,
    Items.titanium, 325,
    Items.surgeAlloy, 170
);
sam.buildVisibility = BuildVisibility.shown;
sam.category = Category.turret;

exports.sam = sam;
