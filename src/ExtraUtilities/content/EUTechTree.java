package ExtraUtilities.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;
import mindustry.world.Block;

import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;
import static ExtraUtilities.content.EUBlocks.*;


public class EUTechTree {
    public static TechNode context = null;

    public static void load() {
        addToNode(heatRedirector, () -> {
            nodeProduce(heatTransfer, () -> {
                node(heatDriver);
            });
        });
        addToNode(ventCondenser, () -> {
            nodeProduce(thermalHeater, () -> {});
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