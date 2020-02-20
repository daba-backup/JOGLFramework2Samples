#version 330

out vec4 fs_out_color;

uniform int i;
uniform float f;
uniform vec2 v2;
uniform vec3 v3;
uniform vec4 v4;

vec4 IntTest(){
    vec4 color=vec4(0.01*i,0.0,0.0,1.0);
    return color;
}
vec4 FloatTest(){
    vec4 color=vec4(0.0,f,0.0,1.0);
    return color;
}
vec4 Vec2Test(){
    vec4 color=vec4(v2,0.0,1.0);
    return color;
}
vec4 Vec3Test(){
    vec4 color=vec4(v3,1.0);
    return color;
}
vec4 Vec4Test(){
    vec4 color=v4;
    return color;
}

void main(){
    fs_out_color=Vec4Test();
}
