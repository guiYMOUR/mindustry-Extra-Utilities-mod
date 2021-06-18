/*
*@readme <It's simple, right?>
*/
const und = extendContent(Unloader, "und", {});
und.buildType = prov(() => {
    var ts = 1;
    var td = 0;
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
            if(td > 0){
                td -= Time.delta;
                if(td <= 0){
                    ts = 1;
                }
            }
            this.timeScale = Math.max(ts * this.power.status, 0.001);
            this.timeScaleDuration = td / Math.max(this.power.status, 0.001);
            if(this.power.status >= 0.001){
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
und.speed = 2;
und.health = 80;
und.hasPower = true;
und.consumes.power(1);
exports.und = und;