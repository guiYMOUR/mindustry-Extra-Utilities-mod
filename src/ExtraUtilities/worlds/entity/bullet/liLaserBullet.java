package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.math.Angles;
import arc.util.Nullable;
import mindustry.entities.Effect;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

public class liLaserBullet extends LaserBulletType {
    public int lAmount = 3;
    public Color color = Pal.lancerLaser;

    @Nullable public ChainLightningFade chain = null;
    public int spacing = 5;

    @Override
    public void init(Bullet b) {
        super.init(b);
        float realLength = b.fdata + width * 1.5f;
        float ex = b.x + Angles.trnsx(b.rotation(), realLength), ey = b.y + Angles.trnsy(b.rotation(), realLength);
        Effect e = EUFx.chainLightningFade(lifetime);
        for(int i = 0; i < lAmount; i++){
            if(chain != null) {
                chain.lifetime = lifetime;
                chain.linkSpace = realLength/spacing;
                chain.create(b, b.team, b.x, b.y, 1, -1, 1, 1, EUGet.pos(ex, ey));
            }
            else e.at(b.x, b.y, length/spacing, color, EUGet.pos(ex, ey));
        }
    }
}
