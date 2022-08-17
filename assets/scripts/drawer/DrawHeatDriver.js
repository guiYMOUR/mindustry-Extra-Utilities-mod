const DrawHeatDriver = (arrowSpacing, arrowOffset, arrowPeriod, arrowTimeScl) => {
    var turretPart, turretLine, lPart, rPart, lLine, rLine, eff1, eff2, arrow;
    var x = 0, y = 0;
    var progress = 0, resProgress = 0;
    var rotation = 0;
    return new JavaAdapter(DrawBlock,{
        draw(build){
            var block = build.block;
            x = build.x;
            y = build.y;
            rotation = build.getRotation();
            progress = build.getProgress();
            resProgress = build.getResProgress();
            Draw.z(Layer.turret);
            var move = 3 * progress;
            Drawf.shadow(turretPart,
                x,
                y,
                rotation - 90
            );
            Drawf.shadow(lPart,
                x + Angles.trnsx(rotation + 180, 0, -move) - block.size/2,
                y + Angles.trnsy(rotation + 180, 0, -move) - block.size/2,
                rotation - 90
            );
            Drawf.shadow(rPart,
                x + Angles.trnsx(rotation + 180, 0, move) - block.size/2,
                y + Angles.trnsy(rotation + 180, 0, move) - block.size/2,
                rotation - 90
            );
            Draw.rect(rLine,
                x + Angles.trnsx(rotation + 180, 0, move),
                y + Angles.trnsy(rotation + 180, 0, move),
                rotation - 90
            );
            Draw.rect(lLine,
                x + Angles.trnsx(rotation + 180, 0, -move),
                y + Angles.trnsy(rotation + 180, 0, -move),
                rotation - 90
            );
            Draw.rect(turretLine,
                x,
                y,
                rotation - 90
            );
            Draw.rect(turretPart,
                x,
                y,
                rotation - 90
            );
            Draw.rect(lPart,
                x + Angles.trnsx(rotation + 180, 0, -move),
                y + Angles.trnsy(rotation + 180, 0, -move),
                rotation - 90
            );
            Draw.rect(rPart,
                x + Angles.trnsx(rotation + 180, 0, move),
                y + Angles.trnsy(rotation + 180, 0, move),
                rotation - 90
            );
            var p = Math.min((build.heat/block.visualMaxHeat) * build.power.status, 1);
            Draw.color(Color.valueOf("ea8878"));
            Draw.alpha(p);
            Draw.z(Layer.effect);
            if(progress > 0.01){
                Draw.rect(eff1, 
                    x + Angles.trnsx(rotation + 180, -8),
                    y + Angles.trnsy(rotation + 180, -8),
                    10*progress,
                    10*progress,
                    rotation - 90 - Time.time * 2
                );
                Draw.rect(eff2, 
                    x + Angles.trnsx(rotation + 180, -8),
                    y + Angles.trnsy(rotation + 180, -8),
                    6*progress,
                    6*progress,
                    rotation - 90 + Time.time * 2
                );
                for(var i = 0; i < 4; i++){
                    var angle = i* 360 / 4;
                    Drawf.tri(x + Angles.trnsx(rotation + 180, -8) + Angles.trnsx(angle + Time.time, 5), y + Angles.trnsy(rotation + 180, -8) + Angles.trnsy(angle + Time.time, 5), 6, 2 * progress, angle + Time.time);
                }
            }
            if(resProgress > 0.01){
                Draw.alpha(1);
                Lines.stroke(1*resProgress);
                Lines.circle(x + Angles.trnsx(rotation + 180, 10), y + Angles.trnsy(rotation + 180, 10), 5);
                Lines.circle(x + Angles.trnsx(rotation + 180, 10), y + Angles.trnsy(rotation + 180, 10), 3);
            //Draw.color(Color.valueOf("ea8878"));
                for(var i = 0; i < 3; i++){
                    var angle = i* 360 / 3;
                    Drawf.tri(x + Angles.trnsx(rotation + 180, 10) + Angles.trnsx(angle + Time.time, 5), y + Angles.trnsy(rotation + 180, 10) + Angles.trnsy(angle + Time.time, 5), 4, -2 * resProgress, angle + Time.time);
                    Drawf.tri(x + Angles.trnsx(rotation + 180, 10) + Angles.trnsx(angle - Time.time, 3), y + Angles.trnsy(rotation + 180, 10) + Angles.trnsy(angle - Time.time, 3), 3, 1 * resProgress, angle - Time.time);
                }
            }
            if(build.linkValid()){
                var other = Vars.world.build(build.getLink());
                if(!Angles.near(rotation, build.angleTo(other), 2)) return;
                Draw.color();
                var dist = build.dst(other)/arrowSpacing - block.size;
                var arrows = Math.floor(dist / arrowSpacing);

                for(var a = 0; a < arrows; a++){
                    Draw.alpha(Mathf.absin(a - Time.time / arrowTimeScl, arrowPeriod, 1) * progress * Renderer.bridgeOpacity * p);
                    Draw.rect(arrow,
                    x + Angles.trnsx(rotation + 180, -arrowSpacing) * (Vars.tilesize / 2 + a * arrowSpacing + arrowOffset),
                    y + Angles.trnsy(rotation + 180, -arrowSpacing) * (Vars.tilesize / 2 + a * arrowSpacing + arrowOffset),
                    25,
                    25,
                    rotation);
                }
            }
        },
        load(block){
            turretPart = Core.atlas.find(block.name + "-turret");
            turretLine = Core.atlas.find(block.name + "-turret-outline");
            lPart = Core.atlas.find(block.name + "-l");
            lLine = Core.atlas.find(block.name + "-l-outline");
            rPart = Core.atlas.find(block.name + "-r");
            rLine = Core.atlas.find(block.name + "-r-outline");
            eff1 = Core.atlas.find(block.name + "-effect");
            eff2 = Core.atlas.find(block.name + "-effect");
            arrow = Core.atlas.find(block.name + "-arrow");
        },
    });
}
exports.DrawHeatDriver = DrawHeatDriver;