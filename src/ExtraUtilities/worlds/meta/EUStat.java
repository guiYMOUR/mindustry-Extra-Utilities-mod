package ExtraUtilities.worlds.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class EUStat {
    public static final StatCat WTMF = new StatCat("eu-wtmf");
    public static final Stat fromStat = new Stat("eu-from", WTMF);
    public static final Stat toStat = new Stat("eu-to", WTMF);
    public static final Stat reqStat = new Stat("eu-req", WTMF);

    public static final StatCat boost = new StatCat("eu-boostRu");
    public static final Stat boostRu = new Stat("eu-boostru", StatCat.general);
}
