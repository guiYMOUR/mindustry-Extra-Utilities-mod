package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

public class liLaserBullet extends LaserBulletType {
    public int lAmount = 3;
    public Color color = Pal.lancerLaser;

    @Nullable public ChainLightningFade chain = null;
    public int spacing = 5;

    public Effect e = null;

    public liLaserBullet(float damage) {
        super(damage);
    }
    public liLaserBullet(){ }

    @Override
    public void init(Bullet b) {
        super.init(b);
        float realLength = b.fdata + width * 1.5f;
        float ex = b.x + Angles.trnsx(b.rotation(), realLength), ey = b.y + Angles.trnsy(b.rotation(), realLength);
        if(chain == null) chain = new ChainLightningFade(lifetime, -1, 2.5f, colors[0], 0, Fx.none);
        for(int i = 0; i < lAmount; i++){
            float len = Mathf.dst(b.x, b.y, ex, ey);
            float angle = Angles.angle(b.x, b.y, ex, ey);
            if(chain != null) {
                chain.lifetime = lifetime;
                chain.create(b, b.team, b.x, b.y, angle, -1, 1, 1, len, realLength/spacing);
            }
        }
    }
}
