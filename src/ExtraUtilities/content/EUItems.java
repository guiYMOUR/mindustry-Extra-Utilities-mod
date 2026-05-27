package ExtraUtilities.content;

import arc.graphics.*;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.graphics.Pal;
import mindustry.type.Item;

public class EUItems {
    // Item subclass that generates a colored icon when no sprite file exists
    public static class EUItem extends Item {
        public EUItem(String name, Color color){
            super(name, color);
        }

        @Override
        public void createIcons(MultiPacker packer){
            super.createIcons(packer);
            if(!packer.has(name)){
                Pixmap pix = new Pixmap(32, 32);
                pix.fill(this.color);
                packer.add(PageType.main, name, pix);
                pix.dispose();
            }
        }
    }

    public static Item crispSteel = new EUItem("crisp-steel", Color.valueOf("c0ecff")){{
        cost = 1.1f;
    }};
    public static Item lightninAlloy = new EUItem("lightnin-alloy", Color.valueOf("FFD37F")){{
        frames = 14;
        radioactivity = 0.8f;
        flammability = 0.2f;
        explosiveness = 0.8f;
        charge = 1.3f;
        cost = 1.3f;
    }};
    public static Item stone = new EUItem("stone", Pal.gray){{
        alwaysUnlocked = true;
    }};
}
