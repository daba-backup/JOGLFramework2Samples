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

public class ThreeSpotlightsTestWindow extends JOGLFWindow{
	private Vector camera_position;
	
	private static final int LIGHT_NUM=3;
	
	private Vector[] light_positions;
	private Vector[] light_directions;
	private float[] light_attenuations;
	private float[] phis;
	private float[] thetas;
	private float[] falloffs;
	private ColorU8[] diffuse_colors;
	private ColorU8[] ambient_colors;
	private float[] specular_powers;
	
	private ShaderProgram program;
	
	private int model_handle;
	
	@Override
	protected void Init() {
		camera_position=VectorFunctions.VGet(-10.0f, 10.0f, -10.0f);
		
		this.SetupLightProperties();
		
		//Create a program.
		GLShaderFunctions.CreateProgram(
			"spotlight", 
			"./Data/Shader/330/three_spotlights_phong/vshader.glsl",
			"./Data/Shader/330/three_spotlights_phong/fshader.glsl");
		
		//Camera info is automatically transmitted to all registered programs.
		CameraFront.AddProgram("spotlight");
		
		model_handle=Model3D.LoadModel("./Data/Model/OBJ/Plane/plane.obj");
		Model3D.RemoveAllPrograms(model_handle);
		Model3D.AddProgram(model_handle, "spotlight");
		
		program=new ShaderProgram("spotlight");
	}
	private void SetupLightProperties() {
		light_positions=new Vector[LIGHT_NUM];
		light_directions=new Vector[LIGHT_NUM];
		light_attenuations=new float[LIGHT_NUM];
		phis=new float[LIGHT_NUM];
		thetas=new float[LIGHT_NUM];
		falloffs=new float[LIGHT_NUM];
		diffuse_colors=new ColorU8[LIGHT_NUM];
		ambient_colors=new ColorU8[LIGHT_NUM];
		specular_powers=new float[LIGHT_NUM];
		
		light_positions[0]=VectorFunctions.VGet(-10.0f, 10.0f, -10.0f);
		light_positions[1]=VectorFunctions.VGet(10.0f, 10.0f, -10.0f);
		light_positions[2]=VectorFunctions.VGet(0.0f, 10.0f, -15.0f);
		
		Vector light_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		for(int i=0;i<LIGHT_NUM;i++) {
			light_directions[i]=VectorFunctions.VSub(light_target, light_positions[i]);
			light_directions[i]=VectorFunctions.VNorm(light_directions[i]);
		}
		
		for(int i=0;i<LIGHT_NUM;i++) {
			light_attenuations[i]=0.08f;
			phis[i]=MathFunctions.DegToRad(45.0f);
			thetas[i]=MathFunctions.DegToRad(20.0f);
			falloffs[i]=1.0f;
		}
		
		diffuse_colors[0]=ColorU8Functions.GetColorU8(1.0f, 0.0f, 0.0f, 1.0f);
		diffuse_colors[1]=ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f);
		diffuse_colors[2]=ColorU8Functions.GetColorU8(0.0f, 0.0f, 1.0f, 1.0f);
		ambient_colors[0]=ColorU8Functions.GetColorU8(0.1f, 0.0f, 0.0f, 1.0f);
		ambient_colors[1]=ColorU8Functions.GetColorU8(0.0f, 0.1f, 0.0f, 1.0f);
		ambient_colors[2]=ColorU8Functions.GetColorU8(0.0f, 0.0f, 0.1f, 1.0f);
		
		for(int i=0;i<LIGHT_NUM;i++) {
			specular_powers[i]=0.1f;
		}
	}
	
	@Override
	protected void Update() {
		program.Enable();//Enable the "spotlight" program.
		
		for(int i=0;i<LIGHT_NUM;i++) {
			//Set uniform variables.
			program.SetUniform("light_positions"+"["+i+"]", light_positions[i]);
			program.SetUniform("light_directions"+"["+i+"]", light_directions[i]);
			program.SetUniform("light_attenuations"+"["+i+"]", light_attenuations[i]);
			program.SetUniform("phis"+"["+i+"]", phis[i]);
			program.SetUniform("thetas"+"["+i+"]", thetas[i]);
			program.SetUniform("falloffs"+"["+i+"]", falloffs[i]);
			program.SetUniform("diffuse_colors"+"["+i+"]", diffuse_colors[i]);
			program.SetUniform("ambient_colors"+"["+i+"]", ambient_colors[i]);
			program.SetUniform("specular_colors"+"["+i+"]", specular_powers[i]);	
		}
		
		//Update the camera.
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
