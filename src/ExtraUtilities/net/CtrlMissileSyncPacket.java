package ExtraUtilities.net;

import ExtraUtilities.worlds.entity.bullet.CtrlMissile;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.io.TypeIO;
import mindustry.net.Packet;

public class CtrlMissileSyncPacket extends Packet {
    private byte[] DATA;
    public Unit shooter;
    public int id;

    public CtrlMissileSyncPacket() {
        this.DATA = NODATA;
    }

    public void write(Writes WRITE) {
        TypeIO.writeUnit(WRITE, shooter);
        WRITE.i(this.id);
    }

    public void read(Reads READ, int LENGTH) {
        this.DATA = READ.b(LENGTH);
    }

    public void handled() {
        BAIS.setBytes(this.DATA);
        this.shooter = TypeIO.readUnit(READ);
        this.id = READ.i();
    }
}
