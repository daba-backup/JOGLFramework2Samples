#version 330

uniform ivec2 rnd_nums;
uniform sampler2D uniform_rnds;

layout(location=0) out vec4 normal_rnds;

const float M_PI=3.1415926536;

vec4 GetUniformRnds(ivec2 x){
    vec2 uv=(vec2(x)+0.5)/vec2(rnd_nums);
    return texture(uniform_rnds,uv);
}

//Box-Muller's method
vec4 GetNormalRnds(){
    vec4 color=GetUniformRnds(ivec2(gl_FragCoord.xy));

    vec4 rnds;
    rnds.x=sqrt(-2.0*log(color.r))*cos(2.0*M_PI*color.g);
    rnds.y=sqrt(-2.0*log(color.r))*sin(2.0*M_PI*color.g);
    rnds.z=sqrt(-2.0*log(color.b))*cos(2.0*M_PI*color.a);
    rnds.w=sqrt(-2.0*log(color.b))*sin(2.0*M_PI*color.a);

    return rnds;
}

void main(){
    normal_rnds=GetNormalRnds();
}
