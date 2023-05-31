package ExtraUtilities.content;

import arc.graphics.Color;
import mindustry.graphics.Pal;
import mindustry.type.Item;

public class EUItems {
    public static Item crispSteel = new Item("crisp-steel", Color.valueOf("c0ecff")){{
        cost = 1.1f;
    }};
    public static Item lightninAlloy = new Item("lightnin-alloy", Color.valueOf("FFD37F")){{
        radioactivity = 0.8f;
        flammability = 0.2f;
        explosiveness = 0.8f;
        charge = 1.3f;
        cost = 1.2f;
    }};
    public static Item stone = new Item("stone", Pal.gray){{
        alwaysUnlocked = true;
    }};
}
