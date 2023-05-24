package ExtraUtilities.content;

import mindustry.world.meta.Attribute;

public class EUAttribute {
    public static Attribute stone;
    public static void init(){
        stone = Attribute.add("stone");
    }
}
