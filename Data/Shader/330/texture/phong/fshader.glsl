#version 330

uniform vec3 camera_position;
uniform vec3 camera_target;
uniform float camera_near;
uniform float camera_far;

uniform vec3 light_direction;
uniform vec4 ambient_color;
uniform float diffuse_power;
uniform float specular_power;

uniform float fog_start;
uniform float fog_end;

in vec3 vs_out_position;
in vec2 vs_out_uv;
in vec3 vs_out_normal;

uniform sampler2D texture_sampler;
uniform vec4 fog_color;

out vec4 fs_out_color;

vec4 Lighting(){
    vec3 camera_direction=normalize(camera_target-camera_position);
    vec3 half_le=-normalize(camera_direction+light_direction);

    float diffuse=clamp(dot(vs_out_normal,-light_direction),0.0,1.0);
    float specular=pow(clamp(dot(vs_out_normal,half_le),0.0,1.0),2.0);

    vec4 diffuse_color=vec4(diffuse*diffuse_power);
    vec4 specular_color=vec4(specular*specular_power);

    vec4 color=ambient_color+diffuse_color+specular_color;
    color.a=1.0;

    return color;
}
float Fog(){
    float linear_pos=length(camera_position-vs_out_position);
    float fog_factor=clamp((fog_end-linear_pos)/(fog_end-fog_start),0.0,1.0);

    return fog_factor;
}

void main(){
    vec4 color=Lighting();
    float fog_factor=Fog();

    fs_out_color=mix(
        fog_color,
        color*texture(texture_sampler,vs_out_uv),
        fog_factor);
}
