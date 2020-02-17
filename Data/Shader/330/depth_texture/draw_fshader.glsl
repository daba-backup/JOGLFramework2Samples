#version 330

in vec2 vs_out_uv;

uniform sampler2D texture_sampler;

out vec4 fs_out_color;

void main(){
    float depth=texture(texture_sampler,vs_out_uv).r;
    fs_out_color=vec4(depth,depth,depth,1.0);
}
