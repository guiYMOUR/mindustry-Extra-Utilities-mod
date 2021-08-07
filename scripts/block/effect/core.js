///
const core = extendContent(CoreBlock, "core", {
    canBreak(tile) { return Vars.state.teams.cores(tile.team()).size > 1; },
    canReplace(other) { return other.alwaysReplace; },
    canPlaceOn(tile, team) { return Vars.state.teams.cores(team).size < 8; },
    placeBegan(tile, previous) {},
    beforePlaceBegan(tile, previous) {},

    drawPlace(x, y, rotation, valid){
        if(Vars.world.tile(x, y) == null) return;
        if(!this.canPlaceOn(Vars.world.tile(x, y), Vars.player.team())){
            this.drawPlaceText(
                Core.bundle.get(
                    (Vars.player.team().core() != null && Vars.player.team().core().items.has(this.requirements, Vars.state.rules.buildCostMultiplier)) || Vars.state.rules.infiniteResources ? "bar.btm-limitCore" : "bar.noresources"
                )
            , x, y, valid);
        }
    },
});
core.size= 3;
core.hasItems= true;
core.unloadable= true;
core.breakable= true;
core.rebuildable= false;
core.health= 3000;
core.itemCapacity= 1000;
core.buildCostMultiplier= 0.5;
core.unitCapModifier = 5;
core.requirements = ItemStack.with(
    Items.copper, 2200,
    Items.lead, 2000,
    Items.silicon, 800,
    Items.graphite, 650,
    Items.titanium, 500
);
core.buildVisibility = BuildVisibility.shown;
core.category = Category.effect;
exports.core = core;

const cargo = extendContent(StorageBlock, "cargo", {});
cargo.size= 4;
cargo.hasItems = true;
cargo.unloadable = true;
cargo.health = 3100;
cargo.itemCapacity = 5000;
cargo.buildCostMultiplier = 0.8;
cargo.requirements = ItemStack.with(
    Items.titanium, 285,
    Items.thorium, 150,
    Items.plastanium, 90,
);
cargo.buildVisibility = BuildVisibility.shown;
cargo.category = Category.effect;
exports.cargo = cargo;