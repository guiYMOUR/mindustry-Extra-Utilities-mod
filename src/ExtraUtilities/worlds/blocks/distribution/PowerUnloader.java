package ExtraUtilities.worlds.blocks.distribution;

import arc.util.Time;
import mindustry.world.blocks.storage.Unloader;

public class PowerUnloader extends Unloader {
    public PowerUnloader(String name) {
        super(name);

        hasPower = true;
    }

    public class PowerUnloaderBuild extends UnloaderBuild{
        @Override
        public float delta() {
            return efficiency * Time.delta * timeScale;
        }
    }
}
