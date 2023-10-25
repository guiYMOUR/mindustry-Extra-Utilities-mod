package ExtraUtilities.worlds.entity.unit;

import ExtraUtilities.content.EUGet;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Position;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Pal;
import mindustry.type.unit.ErekirUnitType;

import static mindustry.Vars.tilesize;

public class bossType extends ErekirUnitType {

    public bossType(String name) {
        super(name);
    }

    public static class pickedBlock extends Building {
        public int size;
        public String name;
        private float moTime, angleTime;

        public pickedBlock set(float health, int size, String name, Team team){
            this.maxHealth = health;
            this.health = health;
            this.size = size;
            this.name = name;
            this.team = team;
            return this;
        }

        public String name(){
            return name;
        }

        public pickedBlock(){}

        public int getSize() {
            return size;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setPos(float x, float y){
            this.x = x;
            this.y = y;
        }


        public void draw(){
            Draw.rect(Core.atlas.find(name), x, y);
            Lines.stroke(6, Pal.remove);
            Lines.lineAngle(x - size * tilesize, y - size * tilesize, 0, size * tilesize * health/maxHealth);
            Draw.reset();
        }

        public void update(){
            //(fake) collided
            if(health <= 0) remove();
            float finSize = size + 0.2f;
            Groups.bullet.intersect(x - finSize * tilesize, y - finSize * tilesize, finSize * tilesize * 2, finSize * tilesize * 2, bullet -> {
                if(bullet.team != team && !bullet.collided.contains(id)) {
                    if(bullet.type != null && bullet.type.collides){
                        if((!bullet.type.removeAfterPierce || bullet.type.pierceCap <= 0)) bullet.remove();
                        else if(bullet.type.hitEffect != Fx.none) bullet.type.hitEffect.at(bullet.x, bullet.y);
                        if(bullet.damage > 0) health -= bullet.damage;
                        bullet.collided.add(id);
                    }
                }
            });
        }

        public void moveTo(float toX, float toY){
            if(!Vars.state.isPaused()) moTime += Time.delta * 0.04f;
            x = EUGet.dx(toX, Math.max(dst(toX, toY) - moTime, 0), angleTo(toX, toY) + 180);
            y = EUGet.dy(toY, Math.max(dst(toX, toY) - moTime, 0), angleTo(toX, toY) + 180);
        }

        public void moveTo(Position pos){
            moveTo(pos.getX(), pos.getY());
        }

        public void rotateTo(float cx, float cy, float range, float angle){
            x = EUGet.dx(cx, range, angle);
            y = EUGet.dy(cy, range, angle);
        }

        public void rotateTo(Position pos, float range, float angle){
            if(!Vars.state.isPaused()) angleTime = Math.min(angleTime + Time.delta * 0.01f, 1);
            rotateTo(pos.getX(), pos.getY(), range, angle * angleTime);
        }
    }
}
