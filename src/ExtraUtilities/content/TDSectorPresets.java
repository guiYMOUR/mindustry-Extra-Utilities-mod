package ExtraUtilities.content;

import mindustry.type.SectorPreset;

public class TDSectorPresets {
    public static SectorPreset
            //by carrot
            pd, TD1, TD2,
            //by guiY
            guiYTD1, guiYCL1;
    public static void load(){
        pd = new SectorPreset("preparation", TDPlanet.TD, 12){{
            alwaysUnlocked = true;
        }};
        TD1 = new SectorPreset("TD1", TDPlanet.TD, 13){{
            difficulty = 1;
            captureWave = 21;
            alwaysUnlocked = true;
        }};
        TD2 = new SectorPreset("TD2", TDPlanet.TD, 14){{
            difficulty = 6;
            captureWave = 41;
        }};

        guiYTD1 = new SectorPreset("guiYTD1", TDPlanet.TD, 20){{
           difficulty = 1;
           captureWave = 41;
        }};

        guiYCL1 = new SectorPreset("guiYCL1", TDPlanet.TD, 22){{
           difficulty = 4;
           captureWave = 25;
        }};
    }
}
