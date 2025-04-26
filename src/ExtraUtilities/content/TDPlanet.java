package ExtraUtilities.content;

import arc.graphics.Color;
import arc.util.noise.Noise;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.maps.planet.ErekirPlanetGenerator;
import mindustry.type.Planet;
import mindustry.type.Sector;

public class TDPlanet {
    public static Planet TD;

    public static void load(){
        TD = new Planet("TD", Planets.sun, 1, 2){{
            generator = new ErekirPlanetGenerator();
            iconColor = EUGet.MIKU;
            meshLoader = () -> new SunMesh(
                    this, 4,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("3587F2"),
                    Color.valueOf("00FF9F"),
                    Color.valueOf("C165F7"),
                    Color.valueOf("6F3C8D"),
                    Color.valueOf("BE47E5"),
                    Color.valueOf("6E2B84")
            );
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("3587F2").a(0.75f), 2, 0.42f, 1f, 0.43f),
                    new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("00FF9F").a(0.75f), 2, 0.42f, 1.2f, 0.45f)
            );
            landCloudColor = Color.valueOf("3587F2");
            atmosphereColor = Color.valueOf("00FF9F");
            alwaysUnlocked = true;
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 12;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            allowLaunchToNumbered = false;
            ruleSetter = r -> {
                r.enemyCoreBuildRadius = 1;
                r.bannedBlocks.addAll(Blocks.launchPad, Blocks.smite, EUBlocks.breaker, EUBlocks.quantumDomain);
            };
        }

            @Override
            public void init() {
                applyDefaultRules(campaignRules);
                loadRules();

                if(techTree == null){
                    techTree = TechTree.roots.find(n -> n.planet == this);
                }

                if(techTree != null && autoAssignPlanet){
                    techTree.addDatabaseTab(this);
                }

                for(Sector sector : sectors){
                    sector.loadInfo();
                }

                if(generator != null){
                    Noise.setSeed(sectorSeed < 0 ? id + 1 : sectorSeed);

                    for(Sector sector : sectors){
                        generator.generateSector(sector);
                    }

                    updateBaseCoverage();
                }

                clipRadius = Math.max(clipRadius, radius + atmosphereRadOut + 0.5f);
            }
        };
    }
}
