#version 330

layout(location=0) in vec2 vs_in_position;
layout(location=1) in vec4 vs_in_color;

out vec4 vs_out_color;

void main(){
    gl_Position=vec4(vs_in_position,-1.0,1.0);
    vs_out_color=vs_in_color;
}
