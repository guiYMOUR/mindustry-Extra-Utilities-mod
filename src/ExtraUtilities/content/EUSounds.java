package ExtraUtilities.content;

import ExtraUtilities.ExtraUtilitiesMod;
import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.AssetLoaderParameters;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import arc.files.Fi;
import arc.func.Cons;
import arc.util.Threads;
import mindustry.Vars;
import mindustry.mod.Mods;

public class EUSounds {
    public static Mods.LoadedMod EU = Vars.mods.getMod(ExtraUtilitiesMod.class);
    public static Sound
        //prismShoot,
        prismLoop = new Sound();

    public static void load(){
//        prismShoot = loadSound("prismS");
        //prismLoop = loadSound("prism-beam.ogg");
        //Threads.thread("EUSoundsLoad", () ->{

        //});
        prismLoop = loadSound("prism-beam.ogg");
    }
    //public static Sound prismLoop = new Sound(getInternalFile("sounds").child("prism-beam.ogg"));

    public static Sound loadSound(String name){
        return new Sound(EU.root.child("sounds").child(name));
    }
}
