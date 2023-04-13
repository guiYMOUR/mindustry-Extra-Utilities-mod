// /*const lib = require("blib");
// const up = require("unit/UF");

/*
//Blocks.cultivator.consumes.liquid(Liquids.water, 15 / 60);

Blocks.duct.buildVisibility = BuildVisibility.shown;
Blocks.ductBridge.buildVisibility = BuildVisibility.shown;
//if Anuke sees this, I hope he can change it early, it will cause the game to crash
Blocks.ductBridge.buildType = prov(() => {
    return new JavaAdapter(DuctBridge.DuctBridgeBuild, {
        findLink(){
            for(var i = 1; i <= this.block.range; i++){
                var other = this.tile.nearby(Geometry.d4x[this.rotation] * i, Geometry.d4y[this.rotation] * i);
                
                if(other != null && other.build instanceof DuctBridge.DuctBridgeBuild && other.build.team == this.team){
                    return other.build;
                }
            }
            return null;
        },
    }, Blocks.ductBridge);
});

Blocks.ductRouter.buildVisibility = BuildVisibility.shown;

lib.addToResearch(Blocks.ductBridge, { parent: 'bridge-conveyor', });
lib.addToResearch(Blocks.ductRouter, { parent: 'router', });
lib.addToResearch(Blocks.duct, { parent: 'conveyor', });

const { winglet } = require("unit/AirSapper/winglet");
const { moth } = require("unit/AirSapper/moth");
const { vb } = require("unit/AirSapper/VenomBee");
const { phantom } = require("unit/AirSapper/phantom");
const { skyline } = require("unit/AirSapper/skyline");
Blocks.airFactory.plans.add(new UnitFactory.UnitPlan(winglet, 60 * 30, ItemStack.with(Items.silicon, 20, Items.titanium, 10, Items.lead, 15)));
Blocks.additiveReconstructor.upgrades.add(
    up.unitType(winglet, moth),
);
Blocks.multiplicativeReconstructor.upgrades.add(
    up.unitType(moth, vb),
);
Blocks.exponentialReconstructor.upgrades.add(
    up.unitType(vb, phantom),
);
Blocks.tetrativeReconstructor.upgrades.add(
    up.unitType(phantom, skyline),
);*/


//StatusEffects.sapped.reactive = true;