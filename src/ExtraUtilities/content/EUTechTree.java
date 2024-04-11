package ExtraUtilities.content;

import arc.struct.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;
import static ExtraUtilities.content.EUBlocks.*;
import static ExtraUtilities.content.EUUnitTypes.*;
import static ExtraUtilities.content.TDSectorPresets.*;


public class EUTechTree {
    public static TechNode context = null;

    public static void load() {
        //S
        addToNode(plastaniumConveyor, () -> node(stackHelper));
        addToNode(phaseConveyor, () -> node(itemNode));
        addToNode(phaseConduit, () -> node(liquidNode));
        addToNode(thermalGenerator, () -> {
            node(heatPower, () -> {
                node(windPower, () -> {
                    node(waterPower);
                });
            });
        });
        addToNode(sporePress, () -> {
            node(T2sporePress);
        });
        addToNode(blastMixer, () -> {
            node(T2blast);
        });
        addToNode(surgeSmelter, ()->{
            node(LA, () ->{
                node(LG);
            });
        });
        addToNode(siliconSmelter, () -> node(siliconFurnace));
        addToNode(pyrolysisGenerator, () -> {
            node(liquidConsumeGenerator);
        });
        addToNode(thermalGenerator, () -> {
            node(thermalReactor);
        });
        addToNode(tetrativeReconstructor, () -> {
            node(imaginaryReconstructor, () -> {
                node(suzerain);
                node(nebula);
                node(asphyxia);
                node(Tera);
                node(apocalypse);
                node(nihilo);
                node(narwhal);
            });
        });
        addToNode(airFactory, () -> {
            node(winglet);
        });
        addToNode(segment, () -> {
            node(dissipation);
        });
        addToNode(Vars.content.block(name("shotgun")), () -> {
            node(onyxBlaster);
        });
        addToNode(cyclone, () -> {
            node(turretResupplyPoint);
            node(celebration, () -> {
                node(celebrationMk2);
            });
            node(antiaircraft);
        });
        addToNode(foreshadow, () -> {
            node(sancta);
        });

        addToNode(memoryCell, () -> {
            node(buffrerdMemoryBank);
        });
        //E
        addToNode(turbineCondenser, () -> {
            node(nitrogenWell);
            node(ventHeater);
        });
        addToNode(ductBridge, () -> {
            node(reinforcedDuctBridge);
            node(ekMessDriver);
        });
        addToNode(reinforcedBridgeConduit, () -> {
            node(phaseReinforcedBridgeConduit);
        });
        addToNode(reinforcedPump, () -> {
            node(ekPump);
        });
        addToNode(electricHeater, () -> {
            node(largeElectricHeater);
        });
        addToNode(heatRedirector, () -> {
            node(heatTransfer, () -> {
                node(heatDriver);
            });
        });
        addToNode(heatRouter, () -> {
            node(heatDistributor);
        });
        addToNode(slagHeater, () -> {
            node(slagReheater);
        });
        addToNode(electricHeater, () -> {
            node(thermalHeater, () -> {});
        });
        addToNode(oxidationChamber, () -> {
            node(T2oxide, () -> {});
        });
        addToNode(plasmaBore, () -> {
            node(minerPoint, () ->{
                node(minerCenter);
            });
        });
        addToNode(reinforcedPump, () -> {
            node(arkyciteExtractor, () ->{
                node(cyanogenPyrolysis);
            });
        });
        addToNode(reinforcedLiquidRouter, () -> {
            node(liquidSorter, () ->{});
            node(liquidValve, () -> {
                node(communicatingValve);
            });
        });

        addToNode(unitRepairTower, () -> node(unitBooster));
        addToNode(basicAssemblerModule, () -> {
            node(advAssemblerModule, () -> {
                node(napoleon);
                node(havoc);
                node(arcana);
            });
        });

        addToNode(breach, () -> {
            node(guiY, () -> {
                node(turretSpeeder);
                node(fiammetta);
                node(anti_Missile);
            });
            node(javelin);
        });
        addToNode(surgeCrucible, () -> {
            node(ELA);
        });
        addToNode(slagIncinerator, () ->{
            node(liquidIncinerator);
        });

        //TD
        TDPlanet.TD.techTree = nodeRoot("TD", TD1, () -> {
            addToNode(TD1, () -> {
                node(TD2, Seq.with(new SectorComplete(TD1)), () -> {});
                node(guiYTD1, () -> {
                    node(BossTD, Seq.with(new SectorComplete(TD1)), () -> {
                        node(regency, ItemStack.with(EUItems.lightninAlloy, 15000), () -> {});
                    });
                });
                node(guiYCL1, Seq.with(new SectorComplete(TD1)), () -> {});
                node(EUItems.stone, () -> {
                    node(stoneExtractor, () -> {
                        node(stoneCrusher);
                        node(stoneMelting);
                    });
                });
                node(breaker);
                node(coreKeeper);
                node(mineCellT1, () -> node(mineCellT2));
            });
        });
    }

    public static void addToNode(UnlockableContent p, Runnable c) {
        context = TechTree.all.find(t -> t.content == p);
        c.run();
    }
    //本来想偷懒直接写个用的，结果发现还是这样来的好，哎)(嘿
    //我直接进行一个工厂源码的转↓↓↓
    public static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    public static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null){
            node.objectives.addAll(objectives);
        }

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    public static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    public static void node(UnlockableContent block){
        node(block, () -> {});
    }

    public static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
    }

    public static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, new Seq<>(), children);
    }

//    public static @Nullable TechNode context(){
//        return context;
//    }
}