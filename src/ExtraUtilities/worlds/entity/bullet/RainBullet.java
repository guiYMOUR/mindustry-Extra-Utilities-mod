package ExtraUtilities.worlds.entity.bullet;

import ExtraUtilities.content.EUGet;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class RainBullet extends BulletType {
    public float rainRange = 80;
    public BulletType rain;

    @Override
    public void update(Bullet b) {
        super.update(b);
        if(rain == null) throw new RuntimeException("无法制造弹雨");
        if(b.timer.get(3, 6)){
            float dx = EUGet.dx(b.x, rainRange, 360/lifetime * b.time + b.rotation()), dy = EUGet.dy(b.y, rainRange, 360/lifetime * b.time + b.rotation());
            rain.create(b, dx, dy, b.angleTo(dx,dy) + 180);
        }
    }
}
