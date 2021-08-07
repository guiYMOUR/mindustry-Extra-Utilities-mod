const lib = require('blib');
const items = require("game/items");

const shieldHealth = 2400;
const brokenReload = 3;
const normalReload = 5;
const range = 90;
const maxNode = 3;

const cor = Color.valueOf("#BF92F9");
const cureRatio = 0.06;
const reload = 600;
const baseColor = cor;
const phaseColor = Color.valueOf("de98b0");

const DCF = extendContent(Block, "DIMENSIONAL-COMPLEX-FIELD", {
    setBars() {
        this.super$setBars();
        this.bars.add("shield", func(entity => {
            var bar = new Bar(prov( () => Core.bundle.format("stat.shieldhealth")), prov( () => cor), floatp( () => entity.broken() ? 0 : entity.shield() / shieldHealth));
            return bar;
        }));
    },
    drawPlace(x, y, rotation, valid) {
        Drawf.dashCircle(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range, Pal.accent);
        Draw.color(Pal.gray);
        Lines.stroke(3);
        Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, 60);
        Draw.color(cor);
        Lines.stroke(1);
        Lines.poly(x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, 6, 60);
        Draw.color();
        Vars.indexer.eachBlock(Vars.player.team(), x * Vars.tilesize + this.offset, y * Vars.tilesize + this.offset, range, boolf(other => true), cons(other => {
            var tmp = Tmp.c1.set(baseColor);
            tmp.a = Mathf.absin(4, 1);
            Drawf.selected(other, tmp);
        }));
        //Draw.color();
    },
    setStats(){
        this.super$setStats();
        this.stats.add(Stat.shieldHealth, shieldHealth, StatUnit.none);
        this.stats.add(Stat.cooldownTime, Math.floor(shieldHealth / brokenReload / 60), StatUnit.seconds);
        this.stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks);
        this.stats.add(Stat.repairTime, Math.floor(100 / (cureRatio*100) * (reload/2.5) / 60), StatUnit.seconds);
        this.stats.add(Stat.abilities, Core.bundle.format("stat.btm-connection", maxNode));
    },
    pointConfig(config, transformer) {
        // Rotate relative points
        if (IntSeq.__javaObject__.isInstance(config)) {
            // ROTATE IT!
            var newSeq = new IntSeq(config.size);
            newSeq.add(config.get(0));
            newSeq.add(config.get(1));
            var linkX = null;
            for (var i = 2; i < config.size; i++) {
                var num = config.get(i);
                if (linkX == null) {
                    linkX = num;
                } else {
                    // The source position is relative to right bottom, transform it.
                    var point = new Point2(linkX * 2 - 1, num * 2 - 1);

                    transformer.get(point);
                    newSeq.add((point.x + 1) / 2);
                    newSeq.add((point.y + 1) / 2);
                    linkX = null;
                }
            }
            return newSeq;
        } else {
            return config;
        }
    },
});
DCF.size = 2;
DCF.health = 3000;
DCF.update = true;
DCF.solid = true;
DCF.configurable = true;
DCF.saveConfig = false;
DCF.itemCapacity = 100;
DCF.buildCostMultiplier = 0.35;
DCF.absorbLasers = true;
DCF.insulated = true;
DCF.requirements = ItemStack.with(
    Items.copper, 550,
    Items.silicon, 600,
    Items.graphite, 350,
    Items.titanium, 450,
    Items.thorium, 400,
    Items.plastanium, 300,
    Items.phaseFabric, 250,
    items.lightninAlloy, 150
);
DCF.buildVisibility = BuildVisibility.shown;
DCF.category = Category.effect;

DCF.config(java.lang.Integer, lib.cons2((tile, int) => {
    tile.setOneLink(int);
}));


DCF.buildType = prov(() => {

    var links = new Seq(java.lang.Integer);
    var ab = 1;
    var h = shieldHealth;
    var r = Mathf.random(reload);
    var radscl = 0;
    function fairLoopIndex(i, max, offset) {
        return (i + offset) % max;
    }

    function linkValid(the, pos) {
        if (pos === undefined || pos === null || pos == -1) return false;
        var linkTarget = Vars.world.build(pos);
        return linkTarget && linkTarget.team == the.team && the.within(linkTarget, range);
    }
    return extend(Building, {
        getLink() { return links; },
        setLink(v) { links = v; },
        setOneLink(v) {
            var int = new java.lang.Integer(v);
            if (!links.remove(boolf(i => i == int))) {
                links.add(int);
            }
        },
        broken(){
            return ab == 0;
        },
        shield(){
            return h;
        },
        updateTile(){
            if(h < 2500) h = Math.min(h + ab * normalReload, shieldHealth);
            radscl = Mathf.lerpDelta(radscl, ab == 0 ? ab : 1.2, 0.05);
            var speed = ab == 1 ? 2.5 : 5;
            var duration = 33.5 - speed;
            r += 1 * this.delta();
            if(r > reload / speed){
                r = 0;
                Vars.indexer.eachBlock(this, range, boolf(other => other.damaged()), cons(other => {
                    other.heal(cureRatio * other.maxHealth);
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, Tmp.c1.set(baseColor).lerp(phaseColor, 0.3));
                }));
            }
            for (var i = 0; i < links.size; i++){
                var pos = links.get(i);
                if (linkValid(this, pos)){
                    var linkTarget = Vars.world.build(pos);
                    linkTarget.applyBoost(speed, duration);
                } else {
                    links.remove(i);
                }
            }
            if(links.size < 1){
                if(ab != 0){
                    const RR = this.block.size * 30 * 1.9;
                    Groups.bullet.intersect(this.x - RR, this.y - RR, RR * 2, RR * 2, cons(trait =>{
                        if(trait.type.absorbable && trait.team != this.team && Intersector.isInsideHexagon(trait.getX(), trait.getY(), RR, this.x, this.y) ){
                             trait.absorb();
                             Fx.absorb.at(trait);
                             if(h <= trait.damage){
                                 Fx.shieldBreak.at(this.x, this.y, RR * 0.4 * radscl, cor);
                                 ab = 0;
                            }
                            h = Math.max(h - trait.damage, 0);
                        }
                    }));
                }else{
                    if(h < shieldHealth){
                        h += brokenReload;
                    }else{
                        ab = 1;
                    }
                }
            }
            for (var i = 0; i < links.size; i++) {
                var pos = links.get(i);
                if (linkValid(this, pos) && ab == 1) {
                    var linkTarget = Vars.world.build(pos);
                    const realRange = linkTarget.block.size * 30 * 1.9;
                    Groups.bullet.intersect(linkTarget.x - realRange, linkTarget.y - realRange, realRange * 2, realRange * 2, cons(trait =>{
                        if(trait.type.absorbable && trait.team != linkTarget.team && Intersector.isInsideHexagon(trait.getX(), trait.getY(), realRange, linkTarget.x, linkTarget.y) ){
                             trait.absorb();
                             Fx.absorb.at(trait);
                             if(h <= trait.damage){
                                 Fx.shieldBreak.at(linkTarget.x, linkTarget.y, realRange * 0.4 * radscl, cor);
                                 ab = 0;
                            }
                            h = Math.max(h - trait.damage, 0);
                        }
                    }));
                }
                if(ab == 0){
                    if(h < shieldHealth){
                          h += brokenReload;
                    }else{
                        ab = 1;
                    }
                }
            }
        },
        onRemoved(){
            if(links.size < 1 && ab != 0){
                const fxRange = this.block.size * 30 * 0.8;
                Fx.forceShrink.at(this.x, this.y, fxRange, cor);
            }
            for (var i = 0; i < links.size; i++) {
                var pos = links.get(i);
                if (linkValid(this, pos) && ab != 0) {
                    var linkTarget = Vars.world.build(pos);
                    if(linkTarget == null) return;
                    const fxRangeOther = linkTarget.block.size * 30 * 0.8;
                    Fx.forceShrink.at(linkTarget.x, linkTarget.y, fxRangeOther, cor);
                }
            }
            this.super$onRemoved();
        },
        draw(){
            this.super$draw();
            Draw.z();
            Draw.color(Color.white);
            Draw.alpha(1- (h / 2500));
            Draw.rect(Core.atlas.find("btm-DIMENSIONAL-COMPLEX-FIELD-capacity"), this.x, this.y);
            if(links.size < 1){
                if(ab != 0){
                    const R = this.block.size * 30 * 0.8;
                    Draw.z(Layer.shields);

                    Draw.color(cor/*, Color.white, Mathf.clamp(0)*/);

                    if(Core.settings.getBool("animatedshields")){
                        Fill.poly(this.x, this.y, 6, R * radscl);
                    }else{
                        Lines.stroke(1.5);
                        Draw.alpha(0.11);
                        Fill.poly(this.x, this.y, 6, R * radscl);
                        Draw.alpha(1);
                        Lines.poly(this.x, this.y, 6, R * radscl);
                    }
                }
            }
            for (var i = 0; i < links.size; i++) {
                var pos = links.get(i);
                if (linkValid(this, pos) && ab == 1) {
                    var linkTarget = Vars.world.build(pos);
                    const radius = linkTarget.block.size * 30 * 0.8;
                    Draw.z(Layer.shields);

                    Draw.color(cor/*, Color.white, Mathf.clamp(0)*/);

                    if(Core.settings.getBool("animatedshields")){
                        Fill.poly(linkTarget.x, linkTarget.y, 6, radius * radscl);
                    }else{
                        Lines.stroke(1.5);
                        Draw.alpha(0.09);
                        Fill.poly(linkTarget.x, linkTarget.y, 6, radius * radscl);
                        Draw.alpha(1);
                        Lines.poly(linkTarget.x, linkTarget.y, 6, radius * radscl);
                    }
                }
            }
        },
        
        drawConfigure() {
            const tilesize = Vars.tilesize;
            var sin = Mathf.absin(Time.time, 6, 1);

            Draw.color(cor);
            Lines.stroke(1);
            Drawf.circles(this.x, this.y, (this.tile.block().size / 2 + 1) * Vars.tilesize + sin - 2, Pal.accent);
            

            for (var i = 0; i < links.size; i++) {
                var pos = links.get(i);
                if (linkValid(this, pos)) {
                    var linkTarget = Vars.world.build(pos);
                    Drawf.square(linkTarget.x, linkTarget.y, linkTarget.block.size * tilesize / 2 + 1, cor);
                }
            }
            Drawf.dashCircle(this.x, this.y, range, cor);
        },
        drawSelect(){
            Vars.indexer.eachBlock(this, range, boolf(other => true), cons(other => {
                    var tmp = Tmp.c1.set(baseColor);
                    tmp.a = Mathf.absin(4, 1);
                    Drawf.selected(other, tmp);
                }));
                Drawf.dashCircle(this.x, this.y, range, baseColor);
        },
        onConfigureTileTapped(other) {
            if (this == other) {
                this.configure(-1);
                return false;
            }

            if (this.dst(other) <= range && other.team == this.team && links.size < maxNode) {
                this.configure(new java.lang.Integer(other.pos()));
                return false;
            }
            if(links.size >= maxNode){
                for (var i = 0; i < links.size; i++) {
                    var pos = links.get(i);
                    if (linkValid(this, pos)) {
                        var linkTarget = Vars.world.build(pos);
                        if(other == linkTarget){
                            links.remove(i);
                        }
                    }
                }
                return false;
            }
            return true;
        },
        write(write) {
            this.super$write(write);
            write.f(h);
            write.f(ab);
            write.f(radscl);
            write.s(links.size);
            var it = links.iterator();
            while (it.hasNext()) {
                var pos = it.next();
                write.i(pos);
            }
        },
        read(read, revision) {
            this.super$read(read, revision);
            h = read.f();
            ab = read.f();
            radscl = read.f();
            links = new Seq(java.lang.Integer);
             var linkSize = read.s();
             for (var i = 0; i < linkSize; i++) {
                 var pos = read.i();
                 links.add(new java.lang.Integer(pos));
                }
        },
    });
});

exports.DCF =DCF;