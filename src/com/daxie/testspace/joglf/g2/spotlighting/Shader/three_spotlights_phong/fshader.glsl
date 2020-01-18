#version 330

in vec3 vs_out_position;
in vec2 vs_out_uv;
in vec3 vs_out_normal;

uniform sampler2D texture_sampler;

uniform vec3 camera_position;
uniform vec3 camera_target;

const int LIGHT_NUM=3;

uniform vec3 light_positions[LIGHT_NUM];
uniform vec3 light_directions[LIGHT_NUM];
uniform float light_attenuations[LIGHT_NUM];
uniform float phis[LIGHT_NUM];
uniform float thetas[LIGHT_NUM];
uniform float falloffs[LIGHT_NUM];
uniform vec4 diffuse_colors[LIGHT_NUM];
uniform vec4 ambient_colors[LIGHT_NUM];
uniform float specular_powers[LIGHT_NUM];

out vec4 fs_out_color;

void SetLighting(){
    vec4 diffuse_total=vec4(0.0);
    vec4 specular_total=vec4(0.0);
    vec4 ambient_total=vec4(0.0);

    for(int i=0;i<LIGHT_NUM;i++){
        vec3 r=vs_out_position-light_positions[i];
        float length_r=length(r);
        float attenuation=1.0/(light_attenuations[i]*length_r);

        vec3 normalized_r=normalize(r);

        float cos_alpha=dot(normalized_r,light_directions[i]);
        float cos_half_theta=cos(thetas[i]/2.0);
        float cos_half_phi=cos(phis[i]/2.0);

        if(cos_alpha<=cos_half_phi){
            ambient_total+=ambient_colors[i];
        }
        else{
            if(cos_alpha>cos_half_theta){
            
            }
            else{
                attenuation*=pow((cos_alpha-cos_half_phi)/(cos_half_theta-cos_half_phi),falloffs[i]);
            }

            vec3 half_le=normalize(camera_target+light_directions[i]);
            float specular=pow(clamp(dot(vs_out_normal,half_le),0.0,1.0),2.0);

            vec4 specular_color=vec4(specular*specular_powers[i]);

            diffuse_total+=diffuse_colors[i]*attenuation;
            specular_total+=specular_color;
            ambient_total+=ambient_colors[i];
        }
    }

    fs_out_color=ambient_total+diffuse_total+specular_total;
    fs_out_color.a=1.0;
}
void ApplyTexture(){
    fs_out_color=fs_out_color*texture(texture_sampler,vs_out_uv);
}

void main(){
    SetLighting();
    ApplyTexture();
}
