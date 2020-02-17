#version 330

layout(location=0) in vec3 vs_in_position;
layout(location=1) in vec4 vs_in_color;

uniform mat4 projection;
uniform mat4 view_transformation;

out vec4 vs_out_color;

void main(){
    mat4 camera_matrix=projection*view_transformation;
    gl_Position=camera_matrix*vec4(vs_in_position,1.0);

    vs_out_color=vs_in_color;
}
