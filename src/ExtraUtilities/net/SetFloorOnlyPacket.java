package ExtraUtilities.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.io.TypeIO;
import mindustry.net.Packet;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class SetFloorOnlyPacket extends Packet {
        private byte[] DATA;
        public Tile tile;
        public Block floor;

    public SetFloorOnlyPacket() {
            this.DATA = NODATA;
        }

        public void write(Writes WRITE) {
            TypeIO.writeTile(WRITE, this.tile);
            TypeIO.writeBlock(WRITE, this.floor);
        }

        public void read(Reads READ, int LENGTH) {
            this.DATA = READ.b(LENGTH);
        }

        public void handled() {
            BAIS.setBytes(this.DATA);
            this.tile = TypeIO.readTile(READ);
            this.floor = TypeIO.readBlock(READ);
        }

        public void handleClient() {
            this.tile.setFloor((Floor) this.floor);
        }
    }

