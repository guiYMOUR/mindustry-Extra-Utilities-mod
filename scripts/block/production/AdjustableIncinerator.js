/*
*@author <guiY>
*/

const loadRegionCache = {};
const loadRegion = (name) => {
    if (Vars.headless === true) {
        return null
    }
    var c = loadRegionCache[name]
    if (c) {
        return c
    }
    c = Core.atlas.find('btm' + '-' + name, Core.atlas.find("error"))
    print('find ' + 'btm' + '-' + name + ' result: ' + c)
    loadRegionCache[name] = c
    return c
};

const ai = extendContent(Incinerator, "adjustable-incinerator", {});
ai.buildType = prov(() => {
    var cI = true;
    var cL = true;
    var choice1;
    var choice2;
    return new JavaAdapter(Incinerator.IncineratorBuild, {
        draw(){
            this.super$draw();
            choice1 = cI ? loadRegion("c1t") : loadRegion("c1f");
            choice2 = cL ? loadRegion("c2t") : loadRegion("c2f");
        },
        acceptItem(source, item){
            return this.heat > 0.5 && cI;
        },
        acceptLiquid(source,liquid){
            return this.heat > 0.5 && cL;
        },
        switchItem(){
            if(cI){
                cI = false;
            } else {
                cI = true;
            }
            this.deselect();
        },
        switchLiquid(){
            if(cL){
                cL = false;
            } else {
                cL = true;
            }
            this.deselect();
        },
        buildConfiguration(table) {
            table.button(new Packages.arc.scene.style.TextureRegionDrawable(choice1), Styles.clearTransi, run(() => { this.switchItem() })).size(40).tooltip("switch mode");
            table.button(new Packages.arc.scene.style.TextureRegionDrawable(choice2), Styles.clearTransi, run(() => { this.switchLiquid() })).size(40).tooltip("switch mode");
        },
        write(write) {
            this.super$write(write);
            write.bool(cI);
            write.bool(cL);
        },
        read(read, revision) {
            this.super$read(read, revision);
            cI = read.bool();
            cL = read.bool();
        },
    }, ai);
});
ai.requirements = ItemStack.with(
    Items.lead, 8,
    Items.graphite, 5,
    Items.silicon, 3
);
ai.health = 110;
ai.buildVisibility = BuildVisibility.shown;
ai.category = Category.crafting;
ai.configurable = true;
ai.consumes.power(0.5);

exports.ai = ai;