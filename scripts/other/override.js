const lib = require("blib");

UnitTypes.corvus.mineTier = 2;
UnitTypes.corvus.mineSpeed = 7;
UnitTypes.corvus.buildSpeed = 2;

Blocks.cultivator.consumes.liquid(Liquids.water, 15 / 60);
Blocks.duct.buildVisibility = BuildVisibility.shown;
Blocks.ductBridge.buildVisibility = BuildVisibility.shown;
Blocks.ductRouter.buildVisibility = BuildVisibility.shown;

lib.addToResearch(Blocks.ductBridge, { parent: 'bridge-conveyor', });
lib.addToResearch(Blocks.ductRouter, { parent: 'router', });
lib.addToResearch(Blocks.duct, { parent: 'conveyor', });