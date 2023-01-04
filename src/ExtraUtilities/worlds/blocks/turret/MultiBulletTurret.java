//package ExtraUtilities.worlds.blocks.turret;
//
//import ExtraUtilities.worlds.meta.EUStatValues;
//import arc.math.Angles;
//import arc.math.Mathf;
//import arc.struct.ObjectMap;
//import mindustry.entities.Mover;
//import mindustry.entities.bullet.BulletType;
//import mindustry.type.Item;
//import mindustry.world.blocks.defense.turrets.ItemTurret;
//import mindustry.world.meta.Stat;
//
//import java.util.Objects;
//
////对，只是显示,本来想写完的，但是想着就一个子弹还是不麻烦自己了
//public class MultiBulletTurret extends ItemTurret {
//    public ObjectMap<Item, BulletType[]> ammo = new ObjectMap<>();
//    public BulletType[] multiBullets;
//
//    public MultiBulletTurret(String name) {
//        super(name);
//    }
//
//    @Override
//    public void ammo(Object... objects) {
//        //super.ammo(objects);
//        ammo = ObjectMap.of(objects);
//    }
//
//    @Override
//    public void setStats() {
//        super.setStats();
//        stats.remove(Stat.ammo);
//        stats.add(Stat.ammo, EUStatValues.ammo(ammo));
//    }
//
//    public class MultiBulletTurretBuild extends ItemTurretBuild{
//        @Override
//        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
//            super.bullet(type, xOffset, yOffset, angleOffset, mover);
//            for (BulletType multiBullet : multiBullets) {
//                if (multiBullet == null) return;
//                float xSpread = Mathf.range(xRand),
//                        bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
//                        bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
//                        shootAngle = rotation + angleOffset + Mathf.range(inaccuracy);
//                float lifeScl = multiBullet.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / multiBullet.range, minRange / multiBullet.range, range() / multiBullet.range) : 1f;
//                multiBullet.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y);
//            }
//        }
//    }
//}
