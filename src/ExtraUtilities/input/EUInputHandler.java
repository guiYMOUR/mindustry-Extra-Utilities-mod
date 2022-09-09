package ExtraUtilities.input;

import ExtraUtilities.net.MinerPointConfigCallPacket;
import arc.Core;
import arc.Events;
import arc.input.GestureDetector.*;
import arc.input.InputProcessor;
import arc.util.Nullable;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.ValidateException;
import mindustry.world.Tile;

import static mindustry.Vars.net;
import static mindustry.Vars.netServer;

public class EUInputHandler implements InputProcessor, GestureListener {
    public static void minerPointConfig(@Nullable Player player, Building build, @Nullable Tile value){
        if(build == null) return;
        if(net.server() && (!Units.canInteract(player, build) ||
                !netServer.admins.allowAction(player, Administration.ActionType.configure, build.tile, action -> action.config = value))){
            MinerPointConfigCallPacket packet = new MinerPointConfigCallPacket(); //undo the config on the client
            packet.player = player;
            packet.build = build;
            packet.value = (Tile) build.config();
            player.con.send(packet, true);
            throw new ValidateException(player, "Player cannot configure a tile.");
        }
        build.configured(player == null || player.dead() ? null : player.unit(), value);
        Core.app.post(() -> Events.fire(new EventType.ConfigEvent(build, player, value)));
    }
}
