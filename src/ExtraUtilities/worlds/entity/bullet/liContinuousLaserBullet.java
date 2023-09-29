package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUFx;
import ExtraUtilities.content.EUGet;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

public class liContinuousLaserBullet extends ContinuousLaserBulletType {
    public float lTime = 6;

    public int spacing = 5;
    @Nullable
    public ChainLightningFade chain = null;

    @Override
    public void draw(Bullet b) {
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);

        Effect e = EUFx.chainLightningFade(12).layer(Layer.bullet);
        if(colors.length > 0 && b.timer.get(2, lTime)) {
            float ex = b.x + Angles.trnsx(b.rotation(), realLength * fout), ey = b.y + Angles.trnsy(b.rotation(), realLength * fout);
            if(chain != null){
                chain.lifetime = 12;
                chain.linkSpace = realLength/spacing;
                chain.create(b, b.team, b.x, b.y, 1, -1, 1, 1, EUGet.pos(ex, ey));
            } else e.at(b.x, b.y, realLength/spacing * fout, colors[0], EUGet.pos(ex, ey));
        }
        super.draw(b);
    }
}
