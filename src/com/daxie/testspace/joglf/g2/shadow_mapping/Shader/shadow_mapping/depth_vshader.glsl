#version 330

layout(location=0) in vec3 vs_in_position;
layout(location=1) in vec2 vs_in_uv;
layout(location=2) in vec3 vs_in_normal;

uniform mat4 depth_mvp;

void main(){
    gl_Position=depth_mvp*vec4(vs_in_position,1.0);
}