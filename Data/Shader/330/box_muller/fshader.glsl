#version 330

in vec2 vs_out_uv;

uniform sampler2D input_texture;

out vec4 fs_out_color;

const float M_PI=3.1415926536;

//Box-Muller's method
vec4 GetGaussianRnd(){
    vec4 color=texture(input_texture,vs_out_uv);

    vec4 rnd;
    rnd.x=sqrt(-2.0*log(color.r))*cos(2.0*M_PI*color.g);
    rnd.y=sqrt(-2.0*log(color.r))*sin(2.0*M_PI*color.g);
    rnd.z=sqrt(-2.0*log(color.b))*cos(2.0*M_PI*color.a);
    rnd.w=sqrt(-2.0*log(color.b))*sin(2.0*M_PI*color.a);

    return rnd;
}

void main(){
    fs_out_color=GetGaussianRnd();
}
