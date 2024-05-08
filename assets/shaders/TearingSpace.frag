#define HIGHP

#define HALFPI 3.1415926535897932384626433832795 / 2.0

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;

uniform int u_blackholecount;
uniform vec4 u_blackholes[MAX_COUNT];

varying vec2 v_texCoords;

/*
* 原作者 MEEPofFaith
*/

float interp(float a){
    float f = a - 1.0;
    float t = a;

    //Interp.pow5Out
    a = f * f * f * f * f + 1.0;

    //Interp.circle
    if(a <= 0.5){
        a *= 2.0;
        a = (1.0 - sqrt(1.0 - a * a)) / 2.0;
    }else{
        --a;
        a *= 2.0;
        a = (sqrt(1.0 - a * a) + 1.0) / 2.0;
    }

    //Lerp between two linear functions
    return f + (t - f) * a;
}

void main() {
    vec2 c = v_texCoords.xy;
    vec2 coords = (c * u_resolution) + u_campos;

    vec2 offset = vec2(0.0);
    for(int i = 0; i < u_blackholecount; ++i){
        vec4 blackhole = u_blackholes[i];
        float cX = blackhole.r;
        float cY = blackhole.g;
        float iR = blackhole.b;
        float oR = blackhole.a;

        float dst = distance(blackhole.xy, coords);

//        if(dst < iR * 1.5){
//            //Inside black hole, set to black
//            //up 1.5 times, make black area bigger
//            gl_FragColor = vec4(0.0);
//            return;
//        }else
        if(dst > oR){
            //Outside black hole, skip
            continue;
        }else{
            //Influence target position
            float p = (dst - iR) / (oR - iR);
            p = interp(p);
            float a = atan(coords.x - cX, coords.y - cY) + HALFPI;
            vec2 pos = vec2(cX - oR * cos(a) * p, cY + oR * sin(a) * p);
            offset += pos - coords;
        }
    }

    coords += offset;
    gl_FragColor = texture2D(u_texture, (coords - u_campos) / u_resolution);
}
