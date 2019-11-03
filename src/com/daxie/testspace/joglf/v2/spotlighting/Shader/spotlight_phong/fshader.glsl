#version 330

in vec3 vs_out_position;
in vec2 vs_out_uv;
in vec3 vs_out_normal;

uniform sampler2D texture_sampler;

uniform vec3 camera_position;
uniform vec3 camera_target;

uniform vec3 light_position;
uniform vec3 light_direction;
uniform float light_attenuation;
uniform float phi;
uniform float theta;
uniform float falloff;

uniform vec4 diffuse_color;
uniform vec4 ambient_color;

uniform float specular_power;

out vec4 fs_out_color;

void SetLighting(){
    vec3 r=vs_out_position-light_position;
    float length_r=length(r);
    float attenuation=1.0/(light_attenuation*length_r);

    vec3 normalized_r=normalize(r);

    float cos_alpha=dot(normalized_r,light_direction);
    float cos_half_theta=cos(theta/2.0);
    float cos_half_phi=cos(phi/2.0);

    if(cos_alpha<=cos_half_phi){
        fs_out_color=ambient_color;
        return;
    }
    else{
        if(cos_alpha>cos_half_theta){
        
        }
        else{
            attenuation*=pow((cos_alpha-cos_half_phi)/(cos_half_theta-cos_half_phi),falloff);
        }
    }

    vec3 half_le=normalize(camera_target+light_direction);
    float specular=pow(clamp(dot(vs_out_normal,half_le),0.0,1.0),2.0);

    vec4 specular_color=vec4(specular*specular_power);

    fs_out_color=ambient_color+diffuse_color*attenuation+specular_color;
    fs_out_color.a=1.0;
}
void ApplyTexture(){
    fs_out_color=fs_out_color*texture(texture_sampler,vs_out_uv);
}

void main(){
    SetLighting();
    ApplyTexture();
}
