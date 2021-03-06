#version 330

layout(location=0) in vec3 vs_in_position;
layout(location=1) in vec2 vs_in_uv;
layout(location=2) in vec3 vs_in_normal;

uniform mat4 projection;
uniform mat4 view_transformation;
uniform float camera_near;
uniform float camera_far;

out vec3 vs_out_position; 
out vec2 vs_out_uv;
out vec3 vs_out_normal;

void SetPosition(){
    mat4 camera_matrix=projection*view_transformation;
    gl_Position=camera_matrix*vec4(vs_in_position,1.0);

    vs_out_position=vs_in_position;
}
void SetUVs(){
    vs_out_uv=vs_in_uv;
}
void SetNormal(){
    vs_out_normal=vs_in_normal;
}

void main(){
    SetPosition();
    SetUVs();
    SetNormal();
}
