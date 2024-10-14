package ExtraUtilities.net;

import ExtraUtilities.input.EUInputHandler;
import ExtraUtilities.worlds.blocks.production.MinerPoint;
import ExtraUtilities.worlds.blocks.turret.wall.Domain;
import ExtraUtilities.worlds.blocks.turret.wall.ReleaseShieldWall;
import ExtraUtilities.worlds.entity.bullet.CtrlMissile;
import arc.math.geom.Position;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.net.Net;
import mindustry.net.NetConnection;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class EUCall {
    public static void minerPointDroneSpawned(Tile tile, int id) {
        if (Vars.net.server() || !Vars.net.active()) {
            MinerPoint.minerPointDroneSpawned(tile, id);
        }

        if (Vars.net.server()) {
            MinerPointDroneSpawnedCallPacket packet = new MinerPointDroneSpawnedCallPacket();
            packet.tile = tile;
            packet.id = id;
            Vars.net.send(packet, true);
        }

    }

    public static void minerPointConfig(Player player, Building build, Tile value) {
        EUInputHandler.minerPointConfig(player, build, value);
        if (Vars.net.server() || Vars.net.client()) {
            MinerPointConfigCallPacket packet = new MinerPointConfigCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }

            packet.build = build;
            packet.value = value;
            Vars.net.send(packet, true);
        }
    }

    public static void minerPointConfige__forward(NetConnection exceptConnection, Player player, Building build, Tile value) {
        if (Vars.net.server() || Vars.net.client()) {
            MinerPointConfigCallPacket packet = new MinerPointConfigCallPacket();
            if (Vars.net.server()) {
                packet.player = player;
            }

            packet.build = build;
            packet.value = value;
            Vars.net.sendExcept(exceptConnection, packet, true);
        }
    }

    public static void ReleaseShieldWallBuildSync(Tile tile, float damage){
        if (Vars.net.server()) {
            ReleaseShieldWallBuildSyncPacket packet = new ReleaseShieldWallBuildSyncPacket();
            packet.tile = tile;
            packet.damage = damage;
            Vars.net.send(packet, true);
        }
    }

    public static void DomainBroken(Tile tile){
        if (Vars.net.server() || !Vars.net.active()) {
            Domain.broken(tile);
        }

        if (Vars.net.server()) {
            DomainSyncPacket packet = new DomainSyncPacket();
            packet.tile = tile;
            Vars.net.send(packet, true);
        }
    }

    public static void setFloor(Tile tile, Block floor) {
        if(!(floor instanceof Floor asFloor)) return;
        if (Vars.net.server() || !Vars.net.active()) {
            tile.setFloor(asFloor);
        }

        if (Vars.net.server()) {
            SetFloorOnlyPacket packet = new SetFloorOnlyPacket();
            packet.tile = tile;
            packet.floor = floor;
            Vars.net.send(packet, true);
        }

    }

    public static void registerPackets(){
        Net.registerPacket(MinerPointDroneSpawnedCallPacket::new);
        Net.registerPacket(MinerPointConfigCallPacket::new);
        Net.registerPacket(ReleaseShieldWallBuildSyncPacket::new);
        Net.registerPacket(DomainSyncPacket::new);
        Net.registerPacket(SetFloorOnlyPacket::new);
    }
}
