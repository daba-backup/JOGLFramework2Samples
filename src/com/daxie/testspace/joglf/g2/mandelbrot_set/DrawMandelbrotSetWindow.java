package com.daxie.testspace.joglf.g2.mandelbrot_set;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class DrawMandelbrotSetWindow extends JOGLFWindow{
	private ShaderProgram program;
	
	private float scale;
	private float offset_x;
	private float offset_y;
	
	public DrawMandelbrotSetWindow() {
		this.SetSize(1000, 1000);
		
		this.SetInitialValues();
	}
	private void SetInitialValues() {
		scale=1.0f;
		offset_x=-0.5f;
		offset_y=0.0f;
	}
	
	@Override
	protected void Init() {
		GLShaderFunctions.CreateProgram(
				"mandelbrot_set", 
				"./Data/Shader/330/mandelbrot_set/vshader.glsl",
				"./Data/Shader/330/mandelbrot_set/fshader.glsl");
		program=new ShaderProgram("mandelbrot_set");
	}
	
	@Override
	protected void Reshape(int x,int y,int width,int height) {
		program.Enable();
		program.SetUniform("screen_width", width);
		program.SetUniform("screen_height", height);
	}
	
	@Override
	protected void Update() {
		float mouse_rot_vol=this.GetMouseWheelVerticalRotation();
		if(Math.abs(mouse_rot_vol)>1.0E-8) {
			if(mouse_rot_vol>0.0f)scale*=0.75f;
			else scale*=1.5f;
		}
		
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_RIGHT)>0) {
			offset_x+=0.05f;
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_LEFT)>0) {
			offset_x-=0.05f;
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_UP)>0) {
			offset_y+=0.05f;
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_DOWN)>0) {
			offset_y-=0.05f;
		}
		
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_O)==1) {
			this.SetInitialValues();
		}
		
		program.Enable();
		program.SetUniform("scale", scale);
		program.SetUniform("offset_x", offset_x);
		program.SetUniform("offset_y", offset_y);
	}
	@Override
	protected void Draw() {
		program.Enable();
		GLDrawFunctions2D.TransferFullscreenQuad();
	}
}
