/** @author guiY */

const timerBreak = 180;//自爆时限 60 * 时间，单位：秒
const maxsize = 1;//默认就是一格，不建议改

const breaker = extend(Block, "breaker", {
    drawPlace(x, y, rotation, valid){
        this.super$drawPlace(x, y, rotation, valid);

        x *= Vars.tilesize;
        y *= Vars.tilesize;
        x += this.offset;
        y += this.offset;
        var rect = Tmp.r1;
        rect.setCentered(x, y, maxsize * Vars.tilesize);
        var len = Vars.tilesize * maxsize;

        rect.x += Geometry.d4x[rotation] * len;
        rect.y += Geometry.d4y[rotation] * len;

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }
});
breaker.size = 1;
breaker.category = Category.effect;
breaker.requirements = ItemStack.with();
breaker.buildVisibility = BuildVisibility.shown;
breaker.rotate = true; //旋转找目标
breaker.drawArrow = false;
breaker.update = //设置了自爆，update是必要的，基本update设置true就行了
    breaker.solid = //设置固态，其实没必要
        breaker.destructible = true;
breaker.rebuildable = false;//设置不可重建，建议false
breaker.buildType = prov(() => {
    var timer = 0;

    return extend(Building, {
        updateTile() {
            this.bk();
        },

        bk(){
            var tile = Vars.world.tile(this.tileX() + Geometry.d4x[this.rotation], this.tileY() + Geometry.d4y[this.rotation]);
            if(tile != null && tile.block() != null && tile.build == null && tile.block().solid && !tile.block().breakable && tile.block().size <= maxsize){
                timer += Time.delta;
                if(timer >= timerBreak){
                    Call.removeTile(tile);
                    this.kill();
                }
            }
        },

        draw(){
            this.super$draw();
            var s = Math.ceil((timerBreak - timer)/60);
            var text = "|" + s + "|"

            Fonts.def.draw(text, this.x, this.y, Color.red, 0.35, true, Align.center);
        }
    });
})