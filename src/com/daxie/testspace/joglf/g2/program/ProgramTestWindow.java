package com.daxie.testspace.joglf.g2.program;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;

class ProgramTestWindow extends JOGLFWindow{
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		GLShaderFunctions.CreateProgram(
				"program", 
				"./Data/Shader/330/program/vshader.glsl",
				"./Data/Shader/330/program/fshader.glsl");
		program=new ShaderProgram("program");
	}
	
	@Override
	protected void Update() {
		program.Enable();
		program.SetUniform("i", 50);
		program.SetUniform("f", 0.5f);
		program.SetUniform("v2", 1.0f, 1.0f);
		program.SetUniform("v3", 1.0f, 1.0f, 1.0f);
		program.SetUniform("v4", 1.0f, 0.0f, 1.0f, 1.0f);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLDrawFunctions2D.TransferFullscreenQuad();
	}
}
