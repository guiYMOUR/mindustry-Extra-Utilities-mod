package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.worlds.entity.bullet.RainBullet;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class Stinger extends PowerTurret {
    public float delay = 60 * 5f;
    public float killChance = 0.05f;
    public BulletType rainBullet;

    public Stinger(String name) {
        super(name);

        //为js减小内存
        rainBullet = new RainBullet(){{
            speed = damage = 0;
            lifetime = 66;
            hitEffect = despawnEffect = Fx.none;
            hittable = false;
            absorbable = false;
            keepVelocity = false;
            collides = false;
            rain =  new LaserBulletType(60){{
                colors = new Color[]{Color.valueOf("C6D676"), Color.valueOf("C6D676"), Color.white};
                hitSize = 4;
                lifetime = 16f;
                drawSize = 340f;
                collidesAir = false;
                length = 130f;
                keepVelocity = false;
                pierceCap = 3;
            }};
        }};
    }

    public class StingerBuild extends PowerTurretBuild {
        public ObjectMap<Unit, Float> unitMap = new ObjectMap<>();

        @Override
        public void updateTile() {
            super.updateTile();

            for(Unit unit : unitMap.keys()) {
                if (unit == null || unit.dead || unit.type == null || !unit.type.isEnemy || !unit.type.killable) {
                    unitMap.remove(unit);
                }
            }
            for(Unit unit : unitMap.keys()){
                if(unit == null) continue;
                unitMap.put(unit, unitMap.get(unit) + Time.delta);
                if(unitMap.get(unit) > delay){
                    if(unit.health < unit.maxHealth/2 && Mathf.chance(killChance)){
                        unit.health -= unit.health;
                        unit.kill();
                    } else {
                        unit.apply(StatusEffects.unmoving, 60f * 1.5f);
                        rainBullet.create(this, unit.x, unit.y, Mathf.random(360));
                    }
                    unitMap.remove(unit);
                }
            }
        }
    }
}
