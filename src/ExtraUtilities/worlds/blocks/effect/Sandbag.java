package ExtraUtilities.worlds.blocks.effect;

import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.io.TypeIO;
import mindustry.world.Block;
import mindustry.world.Tile;

public class Sandbag extends Block {
    public Sandbag(String name) {
        super(name);

        commandable = true;
    }

    public class SandbagBuild extends Building{
        public @Nullable Vec2 commandPos;
        public Team initTeam;

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            this.initTeam = team;
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            TypeIO.writeVecNullable(write, commandPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                commandPos = TypeIO.readVecNullable(read);
            }
        }
    }
}
