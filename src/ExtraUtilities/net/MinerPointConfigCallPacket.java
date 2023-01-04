package ExtraUtilities.net;

import ExtraUtilities.input.EUInputHandler;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.net.NetConnection;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class MinerPointConfigCallPacket extends Packet {
    private byte[] DATA;
    public Player player;
    public Building build;
    public Tile value;

    public MinerPointConfigCallPacket() {
        this.DATA = NODATA;
    }

    public void write(Writes WRITE) {
        if (Vars.net.server()) {
            TypeIO.writeEntity(WRITE, this.player);
        }

        TypeIO.writeBuilding(WRITE, this.build);
        TypeIO.writeTile(WRITE, this.value);
    }

    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    public void handled() {
        BAIS.setBytes(this.DATA);
        if (Vars.net.client()) {
            this.player = (Player)TypeIO.readEntity(READ);
        }

        this.build = TypeIO.readBuilding(READ);
        this.value = TypeIO.readTile(READ);
    }

    public void handleServer(NetConnection con) {
        if (con.player != null && !con.kicked) {
            Player player = con.player;
            EUInputHandler.minerPointConfig(player, this.build, this.value);
            EUCall.minerPointConfige__forward(con, player, this.build, this.value);
        }
    }

    public void handleClient() {
        EUInputHandler.minerPointConfig(this.player, this.build, this.value);
    }
}
