package ExtraUtilities.content;

import mindustry.type.SectorPreset;

public class TDSectorPresets {
    public static SectorPreset pd, TD1;
    public static void load(){
        pd = new SectorPreset("preparation", TDPlanet.TD, 12){{
            alwaysUnlocked = true;
        }};
        TD1 = new SectorPreset("TD1", TDPlanet.TD, 13){{
            difficulty = 1;
            captureWave = 21;
            alwaysUnlocked = true;
        }};
    }
}
