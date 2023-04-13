package ExtraUtilities.net;

import ExtraUtilities.worlds.blocks.turret.wall.Domain;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class DomainSyncPacket extends Packet {
    private byte[] DATA;
    public Tile tile;

    @Override
    public void write(Writes WRITE) {
        TypeIO.writeTile(WRITE, this.tile);
    }

    @Override
    public void read(Reads READ, int length) {
        this.DATA = READ.b(length);
    }

    @Override
    public void handled() {
        BAIS.setBytes(this.DATA);
        this.tile = TypeIO.readTile(READ);
    }

    @Override
    public void handleClient() {
        Domain.broken(this.tile);
    }
}
