package ExtraUtilities.worlds.blocks.turret;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUItems;
import ExtraUtilities.worlds.meta.EUStatValues;
import arc.Graphics;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Posc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;

public class Fiammetta extends ItemTurret {
    /** only for fiammetta, a character I love in arknights */

    public Fiammetta(String name) {
        super(name);
        logicConfigurable = false;

        configurable = true;

        config(Point2.class, (FiammettaBuild build, Point2 point2) -> build.pos.set(point2.x, point2.y));
    }

    @Override
    public void setStats() {
        super.setStats();
        if(ammoTypes == null) return;

        stats.add(Stat.ammo, EUStatValues.ammoString(ammoTypes, "fa"));
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
    }

    public class FiammettaBuild extends ItemTurretBuild{
        public int link = -1;
        public Vec2 pos = new Vec2();

        @Override
        public void targetPosition(Posc p) {
            if(peekAmmo() == ammoTypes.get(EUItems.lightninAlloy)){
                if(!hasAmmo() || pos == null) return;
                targetPos.set(pos);
            } else {
                super.targetPosition(p);
            }
        }

        @Override
        public void updateTile() {
            if(link == -1) {
                int rtx = (int)EUGet.dx(x, range(), rotation), rty = (int)EUGet.dy(y, range(), rotation);
                pos.set(rtx, rty);
            }
            link = Point2.pack((int)pos.x, (int)pos.y);

            if(!isControlled() && peekAmmo() == ammoTypes.get(EUItems.lightninAlloy) && pos != null){
                targetPos = pos;
                if(shouldTurn()){
                    turnToTarget(angleTo(pos));
                }
                if(Angles.angleDist(rotation, angleTo(pos)) < shootCone) {
                    wasShooting = true;
                    updateShooting();
                }
            }
            super.updateTile();
        }

        @Override
        public Graphics.Cursor getCursor(){
            return (peekAmmo() != ammoTypes.get(EUItems.lightninAlloy)) ? Graphics.Cursor.SystemCursor.arrow : super.getCursor();
        }

        @Override
        public void drawConfigure() {
            if(peekAmmo() != ammoTypes.get(EUItems.lightninAlloy)) {
                deselect();
                return;
            }
            super.drawConfigure();
            Drawf.dashCircle(x, y, range(), team.color);
            Drawf.dashCircle(x, y, minRange, Pal.remove);
            Lines.stroke(2, Pal.gray);
            Lines.line(x, y, pos.x, pos.y);
            Lines.stroke(1.4f, Pal.place);
            Lines.line(x, y, pos.x, pos.y);
            Fill.circle(pos.x, pos.y, 5);
            for(int i = 0; i < 4; i++){
                float angle = i * 90;
                Draw.color(Pal.gray);
                Drawf.tri(pos.x + Angles.trnsx(angle + Time.time, 24), pos.y + Angles.trnsy(angle + Time.time, 24), 12, -7.2f, angle + Time.time);
                Draw.color(Pal.place);
                Drawf.tri(pos.x + Angles.trnsx(angle + Time.time, 24), pos.y + Angles.trnsy(angle + Time.time, 24), 10, -6, angle + Time.time);
            }
        }

        @Override
        public boolean onConfigureTapped(float x, float y) {
            if(within(x, y, range()) && !within(x, y, minRange)){
                configure(Point2.unpack(Point2.pack((int)x, (int)y)));
                //deselect();
                return true;
            } else return false;
        }

        @Override
        public Point2 config(){
            if(tile == null) return null;
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(link);
            write.i((int)pos.x);
            write.i((int)pos.y);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            link = read.i();
            pos.set(read.i(), read.i());
        }
    }
}
