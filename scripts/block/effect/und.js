/**
 *@readme <It's simple, right?>
 */
//可以用电的可加速的卸货器就是吧原版的加速的参数调出来，加速后赋值回去
const und = extendContent(Unloader, "und", {});
und.buildType = prov(() => {
    var ts = 1;
    var td = 0;
    
    var power = 0;
    
    return new JavaAdapter(Unloader.UnloaderBuild, {
        delta(){
            return Time.delta * ts;
        },
        applyBoost(intensity, duration){
            if(intensity >= ts){
                td = Math.max(td, duration);
            }
            ts = Math.max(ts, intensity);
        },
        updateTile(){
            power = this.power.status;
            if(td > 0){
                td -= Time.delta;
                if(td <= 0){
                    ts = 1;
                }
            }
            this.timeScale = Math.max(ts * power, 0.001);
            this.timeScaleDuration = td / Math.max(power, 0.001);
            if(power >= 0.001){
                this.super$updateTile();
            }
        },
    }, und);
});
und.requirements = ItemStack.with(
    Items.lead, 35,
    Items.silicon, 35,
    Items.titanium, 30,
    Items.thorium, 10
);
und.buildVisibility = BuildVisibility.shown;
und.category = Category.effect;
und.speed = 1;
und.health = 80;
und.hasPower = true;
und.consumes.power(1);
exports.und = und;