#version 330

uniform int screen_width;
uniform int screen_height;

uniform float scale;
uniform float offset_x;
uniform float offset_y;

out vec4 fs_out_color;

void main(){
    vec2 p;
    p.x=(gl_FragCoord.x*2.0-screen_width)/screen_width;
    p.y=(gl_FragCoord.y*2.0-screen_height)/screen_height;

    int count=0;
    vec2 r=p+vec2(offset_x,offset_y);
    vec2 z=vec2(0.0,0.0);

    const int N=1000;
    for(int i=0;i<N;i++){
        count++;
        if(length(z)>2.0)break;
        z=vec2(z.x*z.x-z.y*z.y,2.0*z.x*z.y)+r*scale;
    }

    float value=float(count)/N;
    vec3 rgb=vec3(0.0,1.0,1.0);

    fs_out_color=vec4(rgb*value,1.0);
}
