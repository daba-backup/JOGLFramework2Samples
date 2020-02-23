#version 330

layout(location=0) in vec2 vs_in_position;
layout(location=1) in vec2 vs_in_uv;

void main(){
    gl_Position=vec4(vs_in_position,0.0,1.0);
}
