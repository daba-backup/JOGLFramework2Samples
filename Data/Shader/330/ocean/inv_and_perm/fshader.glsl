#version 330

uniform int N;

uniform sampler2D input_length;
uniform sampler2D normalized_input;

layout(location=0) out vec4 heightmap_length;
layout(location=1) out vec4 normalized_heightmap;

float GetTexelR(ivec2 x){
    vec2 uv=(vec2(x)+0.5)/float(N);
    return texture(normalized_input,uv).r*texture(input_length,uv).r;
}

void main(){
    ivec2 x=ivec2(gl_FragCoord.xy);

    float perms[]={1.0,-1.0};
    int index=int(mod(x.x+x.y,2));
    float perm=perms[index];

    float h=GetTexelR(x)*perm/float(N*N);
    vec4 heightmap=vec4(h,h,h,1.0);
    heightmap_length=vec4(length(heightmap),0.0,0.0,1.0);
    normalized_heightmap=heightmap/heightmap_length.r;
}
