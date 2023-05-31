package ExtraUtilities.content;

import mindustry.world.meta.Attribute;

public class EUAttribute {
    public static Attribute stone, EKOil;
    public static void load(){
        stone = Attribute.add("stone");
        EKOil = Attribute.add("EKOil");
    }
}
