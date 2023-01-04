package ExtraUtilities.net;

import ExtraUtilities.worlds.blocks.turret.wall.ReleaseShieldWall;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Tile;

public class ReleaseShieldWallBuildSyncPacket extends Packet {
    private byte[] DATA;
    public Tile tile;
    public float damage;

    @Override
    public void write(Writes WRITE) {
        TypeIO.writeTile(WRITE, this.tile);
        WRITE.f(this.damage);
    }

    @Override
    public void read(Reads READ, int length) {
        this.DATA = READ.b(length);
    }

    @Override
    public void handled() {
        BAIS.setBytes(this.DATA);
        this.tile = TypeIO.readTile(READ);
        this.damage = READ.f();
    }

    @Override
    public void handleClient(){
        ReleaseShieldWall.setDamage(this.tile, this.damage);
    }
}
