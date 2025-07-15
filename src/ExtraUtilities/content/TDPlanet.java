package ExtraUtilities.content;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.TextureRegionDrawable;
import arc.util.noise.Noise;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.ErekirPlanetGenerator;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;
import mindustry.type.Sector;

public class TDPlanet {
    public static Planet TD, supEX;

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

        supEX = new Planet("serpuloEX", Planets.serpulo, 0.3f, 1){{
            generator = new SerpuloPlanetGenerator(){
                @Override
                public void generateSector(Sector sector) {

                }
            };
            meshLoader = () -> new HexMesh(this, 8);
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 11, 0.15f, 0.13f, 5, (new Color()).set(Pal.remove).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f),
                    new HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Pal.spore.cpy().lerp(Pal.remove, 0.55f).a(0.75f), 2, 0.45f, 1.0f, 0.41f));
            launchCapacityMultiplier = 0.5f;
            //sectorSeed = 2;
            camRadius = 0.6f;
            //minZoom = 1f;
            orbitRadius = 3f;
            allowWaves = true;
            allowWaveSimulation = true;
            enemyCoreSpawnReplace = true;
            allowLaunchToNumbered = false;
            prebuildBase = false;
            ruleSetter = (r) -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
                r.coreDestroyClear = true;
            };
            allowCampaignRules = true;
            showRtsAIRule = true;
            //iconColor = Color.valueOf("7d4dff").mul(Pal.remove);
            atmosphereColor = Color.valueOf("3c1b8f");
            atmosphereRadIn = 0.01f;
            atmosphereRadOut = 0.15f;
            startSector = 23;
            alwaysUnlocked = true;
            allowSelfSectorLaunch = true;
            landCloudColor = Pal.spore.cpy().a(0.5f);
        }};

        Planets.serpulo.children.add(supEX);
    }
}
