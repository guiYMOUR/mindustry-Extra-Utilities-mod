//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ExtraUtilities.content;

import ExtraUtilities.ExtraUtilitiesMod;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.SectorPresets;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

public class EUTechTree {
    public static TechTree.TechNode context = null;

    public static void load() {
        //S
        addToNode(Blocks.plastaniumConveyor, () -> node(EUBlocks.stackHelper));
        addToNode(Blocks.phaseConveyor, () -> node(EUBlocks.miniItemNode, () -> node(EUBlocks.itemNode)));
        addToNode(Blocks.phaseConduit, () -> node(EUBlocks.miniLiquidNode, () -> node(EUBlocks.liquidNode)));
        addToNode(Blocks.thermalGenerator, () -> node(EUBlocks.heatPower, () -> node(EUBlocks.windPower, () -> node(EUBlocks.waterPower))));
        addToNode(Blocks.sporePress, () -> node(EUBlocks.T2sporePress));
        addToNode(Blocks.blastMixer, () -> node(EUBlocks.T2blast));
        addToNode(Blocks.surgeSmelter, () -> node(EUBlocks.LA, () -> node(EUBlocks.LG)));
        addToNode(Blocks.siliconSmelter, () -> node(EUBlocks.siliconFurnace));
        addToNode(Blocks.pyrolysisGenerator, () -> node(EUBlocks.liquidConsumeGenerator));
        addToNode(Blocks.thermalGenerator, () -> node(EUBlocks.thermalReactor));
        addToNode(Blocks.steamGenerator, () -> node(EUBlocks.thermoelectricGenerator));
        addToNode(Blocks.tetrativeReconstructor, () -> node(EUBlocks.imaginaryReconstructor, () -> {
            node(EUUnitTypes.suzerain, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.reign)}), () -> {
            });
            node(EUUnitTypes.nebula, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.corvus)}), () -> {
            });
            node(EUUnitTypes.asphyxia, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.toxopid)}), () -> {
            });
            node(EUUnitTypes.Tera, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.oct)}), () -> {
            });
            node(EUUnitTypes.apocalypse, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.eclipse)}), () -> {
            });
            node(EUUnitTypes.nihilo, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.omura)}), () -> {
            });
            node(EUUnitTypes.narwhal, Seq.with(new Objectives.Objective[]{new Objectives.Research(UnitTypes.navanax)}), () -> {
            });
        }));
        addToNode(Blocks.airFactory, () -> node(EUUnitTypes.winglet));
        addToNode(Blocks.segment, () -> node(EUBlocks.dissipation));
        if (Vars.content.block(ExtraUtilitiesMod.name("shotgun")) != null) {
            addToNode(Vars.content.block(ExtraUtilitiesMod.name("shotgun")), () -> node(EUBlocks.onyxBlaster));
        }

        if (Vars.content.block(ExtraUtilitiesMod.name("magstorm")) != null) {
            addToNode(Vars.content.block(ExtraUtilitiesMod.name("magstorm")), () -> node(EUBlocks.arbiter));
        }

        addToNode(Blocks.cyclone, () -> {
            node(EUBlocks.turretResupplyPoint);
            node(EUBlocks.celebration, () -> node(EUBlocks.celebrationMk2));
            node(EUBlocks.antiaircraft);
        });
        addToNode(Blocks.swarmer, () -> node(EUBlocks.blackhole));
        addToNode(Blocks.foreshadow, () -> node(EUBlocks.sancta));
        addToNode(Blocks.parallax, () -> node(EUBlocks.cobweb));
        addToNode(Blocks.mendProjector, () -> node(EUBlocks.mendTurret));
        addToNode(Blocks.memoryCell, () -> node(EUBlocks.buffrerdMemoryBank));
        addToNode(Blocks.message, () -> node(EUBlocks.clock));

        //E
        addToNode(Blocks.turbineCondenser, () -> {
            node(EUBlocks.nitrogenWell);
            node(EUBlocks.ventHeater);
        });
        addToNode(Blocks.ductBridge, () -> {
            node(EUBlocks.reinforcedDuctBridge);
            node(EUBlocks.ekMessDriver);
        });
        addToNode(EUBlocks.reinforcedDuctBridge, () -> node(EUBlocks.ekLiquidDriver));
        addToNode(Blocks.reinforcedBridgeConduit, () -> node(EUBlocks.phaseReinforcedBridgeConduit));
        addToNode(Blocks.reinforcedPump, () -> node(EUBlocks.ekPump));
        addToNode(Blocks.electricHeater, () -> node(EUBlocks.largeElectricHeater));
        addToNode(Blocks.heatRedirector, () -> node(EUBlocks.heatDriver));
        addToNode(Blocks.heatRouter, () -> node(EUBlocks.heatDistributor));
        addToNode(Blocks.slagHeater, () -> node(EUBlocks.slagReheater));
        addToNode(Blocks.electricHeater, () -> node(EUBlocks.thermalHeater));
        addToNode(Blocks.oxidationChamber, () -> node(EUBlocks.T2oxide));
        addToNode(Blocks.plasmaBore, () -> node(EUBlocks.minerPoint, () -> node(EUBlocks.minerCenter)));
        addToNode(Blocks.reinforcedPump, () -> node(EUBlocks.arkyciteExtractor, () -> node(EUBlocks.cyanogenPyrolysis)));
        addToNode(Blocks.reinforcedLiquidRouter, () -> {
            node(EUBlocks.liquidSorter);
            node(EUBlocks.liquidValve, () -> node(EUBlocks.communicatingValve));
        });
        addToNode(Blocks.unitRepairTower, () -> node(EUBlocks.unitBooster));
        addToNode(Blocks.basicAssemblerModule, () -> node(EUBlocks.advAssemblerModule, () -> {
            node(EUUnitTypes.napoleon);
            node(EUUnitTypes.havoc);
            node(EUUnitTypes.arcana);
        }));
        addToNode(Blocks.breach, () -> {
            node(EUBlocks.guiY, () -> {
                node(EUBlocks.sandGo);
                node(EUBlocks.turretSpeeder);
                node(EUBlocks.fiammetta);
                node(EUBlocks.anti_Missile);
            });
            node(EUBlocks.javelin);
        });
        addToNode(Blocks.diffuse, () -> node(EUBlocks.shootingStar));
        addToNode(Blocks.reinforcedSurgeWall, () -> node(EUBlocks.aparajito, () -> node(EUBlocks.aparajitoLarge)));
        addToNode(Blocks.surgeCrucible, () -> node(EUBlocks.ELA));
        addToNode(Blocks.slagIncinerator, () -> node(EUBlocks.liquidIncinerator));
        addToNode(Blocks.slagIncinerator, () -> node(EUBlocks.ekSeparator));
        addToNode(Blocks.blastDrill, () -> node(EUBlocks.phasicDrill));

        //TD
        TDPlanet.TD.techTree = TechTree.nodeRoot("TD", TDSectorPresets.pd, () -> addToNode(TDSectorPresets.pd, () -> {
            node(TDSectorPresets.TD1, () -> {
                node(TDSectorPresets.TD2, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(TDSectorPresets.TD1)}), () -> {
                });
                node(TDSectorPresets.guiYTD1, () -> node(TDSectorPresets.BossTD, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(TDSectorPresets.TD1)}), () -> node(EUUnitTypes.regency, ItemStack.with(new Object[]{EUItems.lightninAlloy, 15000}), () -> node(EUBlocks.randomer, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(TDSectorPresets.BossTD)}), () -> {
                }))));
                node(TDSectorPresets.guiYCL1, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(TDSectorPresets.TD1)}), () -> {
                });
            });
            node(TDSectorPresets.st1, () -> node(EUItems.stone, () -> node(EUBlocks.stoneExtractor, () -> {
                node(EUBlocks.adaptiveMiner, () -> node(EUBlocks.adaptiveMinerII));
                node(EUBlocks.stoneCrusher);
                node(EUBlocks.stoneMelting);
            })));
            node(EUBlocks.breaker);
            node(EUBlocks.waterBomb);
            node(EUBlocks.coreKeeper);
            node(EUBlocks.mineCellT1, () -> node(EUBlocks.mineCellT2));
        }));

        //EX
        TDPlanet.supEX.techTree = TechTree.nodeRoot(TDPlanet.supEX.localizedName, TDPlanet.supEX,
                () -> addToNode(TDPlanet.supEX,
                        () -> node(TDSectorPresets.groundZEx, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(SectorPresets.planetaryTerminal)}),
                                () -> node(TDSectorPresets.relicValley, Seq.with(new Objectives.Objective[]{new Objectives.SectorComplete(TDSectorPresets.groundZEx)}),
                                        () -> {}))));
    }

    public static void addToNode(UnlockableContent p, Runnable c) {
        context = TechTree.all.find((t) -> t.content == p);
        c.run();
    }

    public static void node(UnlockableContent content, Runnable children) {
        node(content, content.researchRequirements(), children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
        node(content, requirements, null, children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children) {
        TechTree.TechNode node = new TechTree.TechNode(context, content, requirements);
        if (objectives != null) {
            node.objectives.addAll(objectives);
        }

        TechTree.TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    public static void node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives, children);
    }

    public static void node(UnlockableContent block) {
        node(block, () -> {
        });
    }

    public static void nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
        node(content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), children);
    }

    public static void nodeProduce(UnlockableContent content, Runnable children) {
        nodeProduce(content, new Seq<>(), children);
    }
}
