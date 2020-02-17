package com.daxie.testspace.joglf.g2.ray_marching;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class RayMarchingTestWindow extends JOGLFWindow{
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		GLShaderFunctions.CreateProgram(
				"ray_marching", 
				"./Data/Shader/330/ray_marching/vshader.glsl",
				"./Data/Shader/330/ray_marching/fshader.glsl");
		program=new ShaderProgram("ray_marching");
	}
	
	@Override
	protected void Reshape(int x,int y,int width,int height) {
		program.Enable();
		program.SetUniform("resolution_x", width);
		program.SetUniform("resolution_y", height);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLDrawFunctions2D.TransferFullscreenQuad();
	}
}
