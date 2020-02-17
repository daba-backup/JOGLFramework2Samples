#version 330

in vec2 vs_out_uv;
in vec4 vs_out_color;
in float vs_out_fog_factor;

in vec4 shadow_coords;

uniform sampler2D texture_sampler;
uniform sampler2D shadow_map;

uniform vec4 fog_color;

out vec4 fs_out_color;

void main(){
    float bias=0.0005;
    vec4 shadow_color=vec4(1.0);

    float depth=texture(shadow_map,shadow_coords.xy).r;
    if(depth<shadow_coords.z-bias){
        shadow_color=vec4(0.5,0.5,0.5,1.0);
    }

    fs_out_color=mix(
        shadow_color*fog_color,
        shadow_color*vs_out_color*texture(texture_sampler,vs_out_uv),
        vs_out_fog_factor);
}
