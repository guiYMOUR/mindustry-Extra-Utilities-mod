package ExtraUtilities.worlds.drawer;

import ExtraUtilities.content.EUGet;
import ExtraUtilities.content.EUItems;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;
import mindustry.world.draw.DrawBlock;

public class DrawBow extends DrawBlock {
    //弓弦走的距离，bowMoveY = 数值 - bowFY
    public float bowMoveY = -40 + 8, bowFY = -8;
    //弓位置和形态
    public float bowWidth = 36, bowHeight = 15, bowTk = 8, turretTk = 15;
    //拉弓引发的弹性形变
    public float visRad = 12;
    //弓的颜色
    public Color color = EUItems.lightninAlloy.color;

    public Drawable drawable;

    @Override
    public void draw(Building build) {
        if(!(build instanceof TurretBuild)) return;
        Turret block = (Turret) build.block;
        drawT(block, (TurretBuild) build);
    }

    public void drawT(Turret block, TurretBuild build){
        float rot = build.rotation - 90;
        float warmup = build.warmup();
        if(warmup < 0.001) return;
        float p = build.reloadCounter/block.reload;
        //float rp = build.curRecoil > 0 ? Math.min((1 - build.curRecoil) * 2, 1) : p;
        float rp = Math.min(p * 1.2f, 1);
        float bx = build.x + build.recoilOffset.x + Angles.trnsx(rot, 0, bowHeight), by = build.y + build.recoilOffset.y + Angles.trnsy(rot, 0, bowHeight);
        float rx = bx + Angles.trnsx(rot, 0, bowMoveY * rp + bowFY), ry = by + Angles.trnsy(rot, 0, bowMoveY * rp + bowFY);
        Draw.z(Layer.bullet - 5 + 15 * warmup);
        Draw.color(color);
        Fill.circle(rx, ry, 3 * warmup);
        Lines.stroke(3 * warmup);
        for(int i : new int[]{-1, 1}) {
            Tmp.v2.set(turretTk * i, bowHeight).rotate(rot);
            Tmp.v3.set(turretTk * i, bowHeight + bowTk).rotate(rot);
            float x1 = build.x + build.recoilOffset.x + Tmp.v2.x, x2 = build.x + build.recoilOffset.x + Tmp.v3.x, y1 = build.y + build.recoilOffset.y + Tmp.v2.y, y2 = build.y + build.recoilOffset.y + Tmp.v3.y;
            float dx = EUGet.dx(x1, bowWidth, rot + 270 + (70 * warmup - visRad * rp * warmup) * i), dy = EUGet.dy(y1, bowWidth, rot + 270 + (70 * warmup - visRad * rp * warmup) * i);
            Tmp.v1.set(bowTk/2f * warmup * i, 0).rotate(rot);
            float dx1 = dx + Tmp.v1.x, dy1 = dy + Tmp.v1.y;
            Fill.tri(x1, y1, x2, y2, dx, dy);
            Fill.tri(x2, y2, dx, dy, dx1, dy1);
            Lines.line(dx, dy, rx, ry);
        }
        Draw.z(Layer.effect);
        Tmp.v1.set(0, bowMoveY + bowFY).rotate(rot);
        float pullx = bx + Tmp.v1.x, pully = by + Tmp.v1.y;
        Lines.stroke(2 * warmup);
        Lines.circle(pullx, pully, 4);
        float sin = Mathf.absin(Time.time, 6, 1.5f);
        for(int i = 0; i < 3; i++){
            float angle = i* 360f / 3;
            Drawf.tri(pullx + Angles.trnsx(angle - Time.time, 5f + sin), pully + Angles.trnsy(angle - Time.time, 5f + sin), 4f, -2f * warmup, angle - Time.time);
        }

//        if(build.peekAmmo() instanceof BasicBulletType && Core.atlas.find(((BasicBulletType)build.peekAmmo()).sprite).found()){
//
//        }
        float arx = rx + Angles.trnsx(rot, 0, -bowMoveY/2), ary = ry + Angles.trnsy(rot, 0, -bowMoveY/2);
        Draw.color(color.cpy().a(p));
        Drawf.tri(arx, ary, 16 * warmup, 16, rot + 90);
        Drawf.tri(arx, ary, 12 * warmup, 8, rot - 90);
        Draw.reset();
    }
}
