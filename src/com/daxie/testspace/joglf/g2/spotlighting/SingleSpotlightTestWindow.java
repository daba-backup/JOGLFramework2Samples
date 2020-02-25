package com.daxie.testspace.joglf.g2.spotlighting;

import com.daxie.basis.coloru8.ColorU8;
import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

class SingleSpotlightTestWindow extends JOGLFWindow{
	private Vector camera_position;
	
	private Vector light_position;
	private Vector light_direction;
	private float light_attenuation;
	private float phi;
	private float theta;
	private float falloff;
	private ColorU8 diffuse_color;
	private ColorU8 ambient_color;
	private float specular_power;
	
	private ShaderProgram program;
	
	private int model_handle;
	
	@Override
	protected void Init() {
		camera_position=VectorFunctions.VGet(10.0f, 10.0f, 10.0f);
		
		Vector light_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		light_position=VectorFunctions.VGet(10.0f, 10.0f, 10.0f);
		light_direction=VectorFunctions.VSub(light_target, light_position);
		light_direction=VectorFunctions.VNorm(light_direction);
		
		light_attenuation=0.08f;
		phi=MathFunctions.DegToRad(45.0f);
		theta=MathFunctions.DegToRad(10.0f);
		falloff=1.0f;
		diffuse_color=ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f);
		ambient_color=ColorU8Functions.GetColorU8(0.1f, 0.1f, 0.1f, 1.0f);
		specular_power=0.1f;
		
		//Create a program.
		GLShaderFunctions.CreateProgram(
			"spotlight", 
			"./Data/Shader/330/spotlight_phong/vshader.glsl",
			"./Data/Shader/330/spotlight_phong/fshader.glsl");
		
		CameraFront.AddProgram("spotlight");
		
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/Ground/ground.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		Model3D.RemoveAllPrograms(model_handle);
		Model3D.AddProgram(model_handle, "spotlight");
		
		program=new ShaderProgram("spotlight");
	}
	
	@Override
	protected void Update() {
		program.Enable();//Enable the "spotlight" program.
		
		//Set uniform variables.
		program.SetUniform("light_position", light_position);
		program.SetUniform("light_direction", light_direction);
		program.SetUniform("light_attenuation", light_attenuation);
		program.SetUniform("phi", phi);
		program.SetUniform("theta", theta);
		program.SetUniform("falloff", falloff);
		program.SetUniform("diffuse_color", diffuse_color);
		program.SetUniform("ambient_color", ambient_color);
		program.SetUniform("specular_color", specular_power);
		
		//Update the camera.
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
