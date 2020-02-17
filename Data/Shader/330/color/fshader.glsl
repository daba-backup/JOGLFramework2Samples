#version 330

in vec4 vs_out_color;

out vec4 fs_out_color;

void main(void){
  fs_out_color=vs_out_color;
}
