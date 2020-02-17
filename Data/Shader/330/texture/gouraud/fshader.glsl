#version 330

in vec2 vs_out_uv;
in vec4 vs_out_color;
in float vs_out_fog_factor;

uniform sampler2D texture_sampler;
uniform vec4 fog_color;

out vec4 fs_out_color;

void main(){
    fs_out_color=mix(
        fog_color,
        vs_out_color*texture(texture_sampler,vs_out_uv),
        vs_out_fog_factor);
}
