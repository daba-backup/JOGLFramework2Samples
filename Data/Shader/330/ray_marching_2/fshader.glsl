#version 330

uniform int resolution_x;
uniform int resolution_y;

out vec4 fs_out_color;

const float sphere_radius=1.0;

float DistanceFunction(vec3 p){
    return length(p)-sphere_radius;
}

void main(){
    vec2 resolution=vec2(resolution_x,resolution_y);
    vec2 p=(gl_FragCoord.xy*2.0-resolution)/min(resolution.x,resolution.y);

    vec3 camera_position=vec3(0.0,0.0,1.01);
    vec3 camera_target=vec3(0.0,0.0,-1.0);
    vec3 camera_up=vec3(0.0,1.0,0.0);
    vec3 camera_side=cross(camera_target,camera_up);
    float target_depth=0.1;

    vec3 ray=normalize(camera_side*p.x+camera_up*p.y+camera_target*target_depth);

    float distance=0.0;
    float ray_len=0.0;
    vec3 ray_position=camera_position;

    for(int i=0;i<16;i++){
        distance=DistanceFunction(ray_position);
        ray_len+=distance;
        ray_position=camera_position+ray*ray_len;
    }

    if(abs(distance)<0.001){
        fs_out_color=vec4(1.0,1.0,1.0,1.0);
    }
    else{
        fs_out_color=vec4(0.0,0.0,0.0,1.0);
    }
}
