package ExtraUtilities.worlds.meta;

import ExtraUtilities.worlds.blocks.turret.Cobweb;
import arc.struct.Seq;
import mindustry.gen.Unit;

public class TurretManager {
    //所有炮塔
    private final Seq<Cobweb.CobwebBuild> turrets;
    //所有单位
    private final Seq<Unit> enemies;

    public TurretManager() {
        this.turrets = new Seq<>();
        this.enemies = new Seq<>();
    }

    public void addTurret(Cobweb.CobwebBuild turret) {
        this.turrets.addUnique(turret);
    }

    public void addEnemy(Unit enemy) {
        this.enemies.addUnique(enemy);
    }

    public void update(){
        turrets.removeAll(t -> t == null || t.dead() || !t.isAdded());
        turrets.sort(t -> -t.efficiency);

        enemies.removeAll(u -> u == null || u.dead() || !u.isAdded());
        enemies.sort(e -> -e.vel.len());

        updateTurrets();
    }

    public void updateTurrets() {
        Seq<Unit> lockedEnemies = new Seq<>();

        for (Cobweb.CobwebBuild turret : turrets) {
            //更新炮塔目标挪到这里了
            turret.updateTarget(enemies, lockedEnemies);
            if (turret.target != null) {
                lockedEnemies.add(turret.target);
            }
        }
    }

}
