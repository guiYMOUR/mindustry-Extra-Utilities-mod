package ExtraUtilities.content;

import arc.Core;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.type.SectorPreset;

public class TDSectorPresets {
    public static SectorPreset
            //by carrot
            pd, TD1, TD2,
            //by guiY
            st1,
            guiYTD1, guiYCL1, BossTD;
    public static void load(){
        pd = new SectorPreset("preparation", TDPlanet.TD, 12){{
            alwaysUnlocked = true;
        }
            @Override
            public void loadIcon(){
                if(!Vars.headless && Core.atlas != null)
                    uiIcon = fullIcon = Core.atlas.find("extra-utilities-guiY-preview");
            }
        };
        TD1 = new SectorPreset("TD1", TDPlanet.TD, 13){{
            difficulty = 1;
            captureWave = 21;
            alwaysUnlocked = true;
        }};
        TD2 = new SectorPreset("TD2", TDPlanet.TD, 14){{
            difficulty = 6;
            captureWave = 41;
        }};

        st1 = new SectorPreset("st1", TDPlanet.TD, 15){{
            difficulty = 1;
            //captureWave = 1;
            alwaysUnlocked = true;
        }};

        guiYTD1 = new SectorPreset("guiYTD1", TDPlanet.TD, 20){{
           difficulty = 1;
           captureWave = 41;
        }};

        guiYCL1 = new SectorPreset("guiYCL1", TDPlanet.TD, 22){{
           difficulty = 4;
           captureWave = 25;
        }};

        BossTD = new SectorPreset("TD-boss", TDPlanet.TD, 24){{
            difficulty = 8;
            captureWave = 121;
        }};
    }
}
