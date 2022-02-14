/**
 * @author guiY<guiYMOUR>
 */

//直接导出
exports.InvertedJunctionBuild = function(block, placeSprite){
    block.buildType = prov(() => {
        var buffer = null;
        var loc = 1;
        return new JavaAdapter(Junction.JunctionBuild, {
            setLoc(v){
                loc = v;
            },
            //复制点击状态
            configured(player, value){
                this.super$configured(player, value);
                loc = value;
            },
            updateTile(){
                buffer = this.buffer;
                for(var i = 0; i < 4; i++){
                    var p = (i + loc) % 4;
                    if(buffer.indexes[i] > 0){
                        if(buffer.indexes[i] > block.capacity) buffer.indexes[i] = block.capacity;
                        var l = buffer.buffers[i][0];
                        var time = BufferItem.time(l);
                        if(Time.time >= time + block.speed / this.timeScale || Time.time < time){
                            var item = Vars.content.item(BufferItem.item(l));
                            var dest = this.nearby(p);
                            if(item == null || dest == null || !dest.acceptItem(this, item) || dest.team != this.team){
                                continue;
                            }
                            dest.handleItem(this, item);
                            java.lang.System.arraycopy(buffer.buffers[i], 1, buffer.buffers[i], 0, buffer.indexes[i] - 1);
                            buffer.indexes[i] --;
                        }
                    }
                }
                this.buffer = buffer;
            },
            draw(){
                //this.super$draw();
                Draw.rect(Core.atlas.find(placeSprite), this.x,this.y);
                Draw.rect(Core.atlas.find("btm-junction-" + loc),this.x,this.y);
            },
            //更改物品进入原则
            acceptItem(source, item){
                var relative = source.relativeTo(this.tile);
                if(relative == -1 || !this.buffer.accepts(relative)) return false;
                var to = this.nearby((relative + loc) % 4);
                return to != null && to.team == this.team;
            },
            //点击显示按钮
            buildConfiguration(table) {
                table.button(new Packages.arc.scene.style.TextureRegionDrawable(Core.atlas.find("btm-flip", Core.atlas.find("clear"))), Styles.clearTransi, run(() => { this.switchf() })).size(36).tooltip("switch");
            },
            //用于转换
            switchf(){
                loc = loc == 1 ? 3 : 1;
                this.deselect();
                this.configure(loc);
            },
            //点击反馈
            config(){
                return loc;
            },
            /*onConfigureTileTapped(other){
                if(this == other){
                    this.deselect();
                    this.configure(1);
                    return false;
                }
                return true;
            },*/
            write(write) {
                this.super$write(write);
                write.f(loc);
            },
            read(read, revision) {
                this.super$read(read, revision);
                loc = read.f();
            },
        }, block);
    });
};