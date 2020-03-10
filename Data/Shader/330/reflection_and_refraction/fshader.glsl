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

vec4 Reflection(){
    vec3 ref=reflect(vs_out_position-camera_position,vs_out_normal);
    vec4 env_color=textureCube(cube_texture,ref);
    env_color.a=1.0;

    return env_color;
}
vec4 Refraction(){
    vec3 ref=refract(vs_out_position-camera_position,vs_out_normal,eta);
    vec4 env_color=textureCube(cube_texture,ref);
    env_color.a=1.0;

    return env_color;
}
float GetMixCoefficient(){
    float f=pow(1.0-eta,2.0)/pow(1.0+eta,2.0);

    vec3 to_camera_vec=normalize(camera_position-vs_out_position);
    float coefficient=f+(1.0-f)*pow((1.0-dot(to_camera_vec,vs_out_normal)),5.0);

    return coefficient;
}

void main(){
    vec4 refraction_color=Refraction();
    vec4 reflection_color=Reflection();
    float coefficient=GetMixCoefficient();

    vec4 env_color=mix(refraction_color,reflection_color,coefficient);

    if(apply_texture==true){
        fs_out_color=texture(texture_sampler,vs_out_uv)*env_color;
    }
    else{
        fs_out_color=env_color;
    }
}
