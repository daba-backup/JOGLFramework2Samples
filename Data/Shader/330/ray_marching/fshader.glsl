#version 330

uniform int resolution_x;
uniform int resolution_y;

out vec4 fs_out_color;

void main(){
    vec2 p;
    p.x=(gl_FragCoord.x*2.0-resolution_x)/resolution_x;
    p.y=(gl_FragCoord.y*2.0-resolution_y)/resolution_y;

    vec3 camera_target=vec3(0.0,0.0,-1.0);
    vec3 camera_up=vec3(0.0,1.0,0.0);
    vec3 camera_side=cross(camera_target,camera_up);

    float target_depth=0.1;

    vec3 ray=normalize(camera_side*p.x+camera_up*p.y+camera_target*target_depth);

    fs_out_color=vec4(ray.xy,-ray.z,1.0);
}
