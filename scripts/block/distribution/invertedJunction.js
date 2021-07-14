exports.InvertedJunctionBuild = function(block, placeSprite){
    block.buildType = prov(() => {
        var loc = 1;
        return new JavaAdapter(Junction.JunctionBuild, {
            configured(player, value){
                this.super$configured(player, value);
                loc = value;
            },
            updateTile(){
                for(var i = 0; i < 4; i++){
                    var p = (i + loc) % 4;
                    if(this.buffer.indexes[i] > 0){
                        if(this.buffer.indexes[i] > this.block.capacity) this.buffer.indexes[i] = this.block.capacity;
                        var l = this.buffer.buffers[i][0];
                        var time = BufferItem.time(l);
                        if(Time.time >= time + this.block.speed / this.timeScale || Time.time < time){
                            var item = Vars.content.item(BufferItem.item(l));
                            var dest = this.nearby(p);
                            if(item == null || dest == null || !dest.acceptItem(this, item) || dest.team != this.team){
                                continue;
                            }
                            dest.handleItem(this, item);
                            java.lang.System.arraycopy(this.buffer.buffers[i], 1, this.buffer.buffers[i], 0, this.buffer.indexes[i] - 1);
                            this.buffer.indexes[i] --;
                        }
                    }
                }
            },
            draw(){
                //this.super$draw();
                Draw.rect(Core.atlas.find(placeSprite), this.x,this.y);
                Draw.rect(Core.atlas.find("btm-junction-" + loc),this.x,this.y);
            },
            acceptItem(source, item){
                var relative = source.relativeTo(this.tile);
                if(relative == -1 || !this.buffer.accepts(relative)) return false;
                var to = this.nearby((relative + loc) % 4);
                return to != null && to.team == this.team;
            },
            buildConfiguration(table) {
                table.button(new Packages.arc.scene.style.TextureRegionDrawable(Core.atlas.find("btm-flip", Core.atlas.find("clear"))), Styles.clearTransi, run(() => { this.switchf() })).size(36).tooltip("switch");
            },
            switchf(){
                loc = loc == 1 ? 3 : 1;
                this.deselect();
            },
            config(){
                return loc;
            },
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