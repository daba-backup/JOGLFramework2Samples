#version 330

in vec3 vs_out_position;
in vec2 vs_out_uv;
in vec3 vs_out_normal;

in vec4 shadow_coords;

uniform vec3 camera_position;
uniform vec3 camera_target;
uniform float camera_near;
uniform float camera_far;

uniform vec3 light_position;
uniform vec3 light_direction;
uniform float light_attenuation;
uniform float phi;
uniform float theta;
uniform float falloff;
uniform vec4 ambient_color;
uniform float diffuse_power;
uniform float specular_power;

uniform vec4 fog_color;
uniform float fog_start;
uniform float fog_end;

uniform sampler2D texture_sampler;
uniform sampler2D shadow_map;

uniform bool enable_shadow;

out vec4 fs_out_color;

float Shadow(){
    const float bias=0.001;
    float visibility=1.0;

    if(enable_shadow==true){
        float depth=texture(shadow_map,shadow_coords.xy/shadow_coords.w).r;
        if(depth<(shadow_coords.z-bias)/shadow_coords.w){
            visibility=0.5;
        }
    }

    return visibility;
}
vec4 Lighting(){
    vec3 r=vs_out_position-light_position;
    float length_r=length(r);
    float attenuation=1.0/(light_attenuation*length_r);

    vec3 normalized_r=normalize(r);

    float cos_alpha=dot(normalized_r,light_direction);
    float cos_half_theta=cos(theta/2.0);
    float cos_half_phi=cos(phi/2.0);

    vec4 color=vec4(0.0);
    if(cos_alpha<=cos_half_phi){
        color=ambient_color;
    }
    else{
        if(cos_alpha>cos_half_theta){
        
        }
        else{
            attenuation*=pow((cos_alpha-cos_half_phi)/(cos_half_theta-cos_half_phi),falloff);
        }

        vec3 half_le=-normalize(camera_target+light_direction);

        float diffuse=clamp(dot(vs_out_normal,-light_direction),0.0,1.0);
        float specular=pow(clamp(dot(vs_out_normal,half_le),0.0,1.0),2.0);

        vec4 diffuse_color=vec4(diffuse*diffuse_power);
        vec4 specular_color=vec4(specular*specular_power);

        color=ambient_color+diffuse_color*attenuation+specular_color;
        color.a=1.0;
    }

    return color;
}
float Fog(){
    float linear_pos=length(camera_position-vs_out_position);
    float fog_factor=clamp((fog_end-linear_pos)/(fog_end-fog_start),0.0,1.0);

    return fog_factor;
}

void main(){
    float visibility=Shadow();
    float fog_factor=Fog();

    vec4 color=Lighting();
    color*=visibility;
    color.a=1.0;

    fs_out_color=mix(
        fog_color,
        color*texture(texture_sampler,vs_out_uv),
        fog_factor);
}
