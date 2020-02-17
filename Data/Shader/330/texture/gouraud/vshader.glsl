#version 330

layout(location=0) in vec3 vs_in_position;
layout(location=1) in vec2 vs_in_uv;
layout(location=2) in vec3 vs_in_normal;

uniform vec3 camera_position;
uniform vec3 camera_target;
uniform mat4 projection;
uniform mat4 view_transformation;
uniform float camera_near;
uniform float camera_far;

uniform vec3 light_direction;
uniform vec4 ambient_color;
uniform float diffuse_power;
uniform float specular_power;

uniform float fog_start;
uniform float fog_end;

out vec2 vs_out_uv;
out vec4 vs_out_color;
out float vs_out_fog_factor;

void SetPosition(){
    mat4 camera_matrix=projection*view_transformation;
    gl_Position=camera_matrix*vec4(vs_in_position,1.0);
}
void SetUVs(){
    vs_out_uv=vs_in_uv;
}
void SetLighting(){
    vec3 camera_direction=normalize(camera_target-camera_position);
    vec3 half_le=-normalize(camera_direction+light_direction);

    float diffuse=clamp(dot(vs_in_normal,-light_direction),0.0,1.0);
    float specular=pow(clamp(dot(vs_in_normal,half_le),0.0,1.0),2.0);

    vec4 diffuse_color=vec4(diffuse*diffuse_power);
    vec4 specular_color=vec4(specular*specular_power);

    vs_out_color=ambient_color+diffuse_color+specular_color;
    vs_out_color.a=1.0;
}
void SetFog(){
    float linear_pos=length(camera_position-vs_in_position);
    vs_out_fog_factor=clamp((fog_end-linear_pos)/(fog_end-fog_start),0.0,1.0);
}

void main(){
    SetPosition();
    SetUVs();
    SetLighting();
    SetFog();
}
