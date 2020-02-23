#version 330

in vec2 vs_out_uv;

uniform sampler2D input_texture;

uniform int N;
uniform int L;
uniform float A;
uniform float v;//Wind speed
uniform vec2 w;//Direction of the wind

layout(location=0) out vec4 tilde_h0k;
layout(location=1) out float tilde_h0k_size;
layout(location=2) out ivec4 tilde_h0k_signs;
layout(location=3) out vec4 tilde_h0minusk;
layout(location=4) out float tilde_h0minusk_size;
layout(location=5) out ivec4 tilde_h0minusk_signs;

const float M_PI=3.1415926536;
const float g=9.81;//Gravitational constant

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
    vec2 x=gl_FragCoord.xy-float(N)/2.0;
    vec2 k=vec2(2.0*M_PI*x.x/L,2.0*M_PI*x.y/L);

    float L_=v*v/g;
    float mag=length(k);
    if(mag<0.00001)mag=0.00001;
    float mag_sq=mag*mag;

    //h0(k)
    float temp0=exp(-1.0/(mag_sq*L_*L_));
    float temp1=pow(dot(normalize(k),normalize(w)),6.0);
    float temp2=mag_sq*mag_sq;
    float Phk=A*temp0*temp1/temp2;
    
    float h0k=(1.0/sqrt(2.0))*sqrt(Phk);

    //h0(-k)
    temp1=pow(dot(normalize(-k),normalize(w)),6.0);
    float Phminusk=A*temp0*temp1/temp2;

    float h0minusk=(1.0/sqrt(2.0))*sqrt(Phminusk);

    vec4 rnd=GetGaussianRnd();
    tilde_h0k=vec4(rnd.xy*h0k,0.0,1.0);
    tilde_h0minusk=vec4(rnd.zw*h0minusk,0.0,1.0);

    tilde_h0k_size=length(tilde_h0k);
    tilde_h0k=abs(tilde_h0k)/tilde_h0k_size;
    tilde_h0k_signs=ivec4(sign(tilde_h0k));
    tilde_h0minusk_size=length(tilde_h0minusk);
    tilde_h0minusk=abs(tilde_h0minusk)/tilde_h0minusk_size;
    tilde_h0minusk_signs=ivec4(sign(tilde_h0minusk));
}
