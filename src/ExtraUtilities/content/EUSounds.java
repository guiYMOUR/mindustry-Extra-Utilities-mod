package ExtraUtilities.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import mindustry.Vars;

public class EUSounds {
    public static Sound
        prismShoot = new Sound(),
        prismLoop = new Sound();

    public static void load(){
//        prismShoot = loadSound("prismS");
//        prismLoop = loadSound("prism-beam");
        Core.assets.load("sounds/prism-beam.ogg", Sound.class).loaded = (a) -> {
            prismLoop = a;
        };
    }
}
