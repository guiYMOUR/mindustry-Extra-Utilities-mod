package ExtraUtilities.net;

import ExtraUtilities.worlds.blocks.production.MinerPoint;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;
import mindustry.world.blocks.units.UnitCargoLoader;

public class MinerPointDroneSpawnedCallPacket extends Packet {
    private byte[] DATA;
    public Tile tile;
    public int id;

    public MinerPointDroneSpawnedCallPacket() {
        this.DATA = NODATA;
    }

    public void write(Writes WRITE) {
        TypeIO.writeTile(WRITE, this.tile);
        WRITE.i(this.id);
    }

    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    public void handled() {
        BAIS.setBytes(this.DATA);
        this.tile = TypeIO.readTile(READ);
        this.id = READ.i();
    }

    public void handleClient() {
        MinerPoint.minerPointDroneSpawned(this.tile, this.id);
    }
}
