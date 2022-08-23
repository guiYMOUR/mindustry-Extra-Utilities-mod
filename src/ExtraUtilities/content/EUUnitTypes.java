package ExtraUtilities.content;

import ExtraUtilities.ai.MinerPointAI;
import arc.func.Prov;
import mindustry.ai.UnitCommand;
import mindustry.content.UnitTypes;
import mindustry.gen.EntityMapping;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.unit.ErekirUnitType;
import mindustry.world.meta.Env;

import static ExtraUtilities.ExtraUtilitiesMod.*;

public class EUUnitTypes {
    static {
        EntityMapping.nameMap.put(name("miner"), EntityMapping.idMap[36]);
        EntityMapping.nameMap.put(name("T2miner"), EntityMapping.idMap[36]);
    }

    public static UnitType
        miner, T2miner;
    public static void load(){
        miner = new ErekirUnitType("miner"){{
            defaultCommand = UnitCommand.mineCommand;
            controller = u -> new MinerPointAI();

            flying = true;
            drag = 0.06f;
            accel = 0.12f;
            speed = 1.5f;
            health = 100;
            engineSize = 1.8f;
            engineOffset = 5.7f;
            range = 50f;
            hitSize = 12f;
            itemCapacity = 20;
            isEnemy = false;

            mineTier = 10;//据点决定tier
            mineSpeed = 1.6f;
            mineWalls = true;
            mineFloor = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
            hidden = true;
            targetable = false;

            //alwaysUnlocked = true;

            setEnginesMirror(
                    new UnitEngine(24 / 4f, -24 / 4f, 2.3f, 315f)
            );
        }};

        T2miner = new ErekirUnitType("T2miner"){{
            defaultCommand = UnitCommand.mineCommand;
            controller = u -> new MinerPointAI();

            flying = true;
            drag = 0.06f;
            accel = 0.12f;
            speed = 1.5f;
            health = 100;
            engineSize = 2.6f;
            engineOffset = 8.9f;
            range = 50f;
            mineRange = 100f;
            hitSize = 16f;
            itemCapacity = 50;
            isEnemy = false;

            mineTier = 10;
            mineSpeed = 3.2f;
            mineWalls = true;
            mineFloor = true;
            useUnitCap = false;
            logicControllable = false;
            playerControllable = false;
            allowedInPayloads = false;
            createWreck = false;
            envEnabled = Env.any;
            envDisabled = Env.none;
            hidden = true;
            targetable = false;

            //alwaysUnlocked = true;

            setEnginesMirror(
                    new UnitEngine(40 / 4f, -40 / 4f, 3f, 315f)
            );
        }};
    }
}
