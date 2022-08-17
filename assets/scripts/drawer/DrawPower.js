const DrawPower = (color) => {
    return new JavaAdapter(DrawBlock, {
        draw(build){
            Draw.color(color);
            Draw.alpha(build.power.status);
            Draw.rect(Core.atlas.find(build.block.name + "-light"),build.x,build.y);
            Draw.alpha(1);
            Draw.color();
        }
    });
}

exports.DrawPower = DrawPower;