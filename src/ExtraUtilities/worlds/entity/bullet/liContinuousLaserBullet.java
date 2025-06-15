package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class liContinuousLaserBullet extends ContinuousLaserBulletType {
    public float lTime = 6;

    public int spacing = 5;
    @Nullable
    public ChainLightningFade chain = null;

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(chain == null) chain = new ChainLightningFade(12, -1, 2.5f, colors[0], 0, Fx.none);
    }

    @Override
    public void draw(Bullet b) {
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);

        if(colors.length > 0 && b.timer.get(2, lTime)) {
            float ex = b.x + Angles.trnsx(b.rotation(), realLength * fout), ey = b.y + Angles.trnsy(b.rotation(), realLength * fout);
            float len = Mathf.dst(b.x, b.y, ex, ey);
            float angle = Angles.angle(b.x, b.y, ex, ey);
            if(chain != null){
                chain.lifetime = 12;
                chain.create(b, b.team, b.x, b.y, angle, -1, 1, 1, len, realLength/spacing * fout);
            }
        }
        super.draw(b);
    }
}
