package ExtraUtilities.input;

import mindustry.Vars;

import java.util.Objects;

import static ExtraUtilities.ExtraUtilitiesMod.EU;

public class EUAtLoad {
    public static boolean hasOtherContentMod;

    public static void init(){
        hasOtherContentMod = hasContent();
    }

    private static Boolean hasContent(){
        for(var mod : Vars.mods.list()){
            if(mod.meta == null) continue;
            if("guiY".equals(mod.meta.author)) continue;
            if(mod.enabled() && !mod.meta.hidden){
                return true;
            }
        }
        return false;
    }
}
