#version 330

uniform vec3 camera_position;
uniform vec3 camera_target;
uniform float camera_near;
uniform float camera_far;

in vec3 vs_out_position;
in vec2 vs_out_uv;
in vec3 vs_out_normal;

uniform sampler2D texture_sampler;
uniform samplerCube cube_texture;

uniform bool apply_texture;

uniform float eta;

out vec4 fs_out_color;

vec4 Refraction(){
    vec3 ref=refract(vs_out_position-camera_position,vs_out_normal,eta);
    vec4 env_color=textureCube(cube_texture,ref);
    env_color.a=1.0;

    return env_color;
}

void main(){
    vec4 env_color=Refraction();
    if(apply_texture==true){
        fs_out_color=texture(texture_sampler,vs_out_uv)*env_color;
    }
    else{
        fs_out_color=env_color;
    }
}
