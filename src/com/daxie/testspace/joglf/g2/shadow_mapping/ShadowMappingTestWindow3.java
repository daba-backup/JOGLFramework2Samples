package com.daxie.testspace.joglf.g2.shadow_mapping;

import java.nio.IntBuffer;

import com.daxie.basis.coloru8.ColorU8;
import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.tool.matrix.ProjectionMatrixFunctions;
import com.daxie.joglf.gl.tool.matrix.TransformationMatrixFunctions;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.MathFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class ShadowMappingTestWindow3 extends JOGLFWindow{
	private int fbo_id;
	private int texture_id;
	
	private static final int SHADOW_WIDTH=2048;
	private static final int SHADOW_HEIGHT=2048;
	
	private int teapot_model_handle;
	private int ground_model_handle;
	
	private ShaderProgram depth_program;
	private ShaderProgram draw_program;
	
	private Vector light_position;
	private Vector light_target;
	
	private Vector light_direction;
	private float light_attenuation;
	private float phi;
	private float theta;
	private float falloff;
	private ColorU8 ambient_color;
	private float diffuse_power;
	private float specular_power;
	
	@Override
	protected void Init() {
		this.SetupPrograms();
		this.SetupFramebufferAndTexture();
		this.LoadModels();
		this.SetupLight();
		this.SetupFronts();
		
		GLWrapper.glDisable(GL4.GL_CULL_FACE);
	}
	private void SetupPrograms() {
		GLShaderFunctions.CreateProgram(
				"depth", 
				"./Data/Shader/330/shadow_mapping_3/depth_vshader.glsl",
				"./Data/Shader/330/shadow_mapping_3/depth_fshader.glsl");
		GLShaderFunctions.CreateProgram(
				"draw", 
				"./Data/Shader/330/shadow_mapping_3/draw_vshader.glsl",
				"./Data/Shader/330/shadow_mapping_3/draw_fshader.glsl");
		depth_program=new ShaderProgram("depth");
		draw_program=new ShaderProgram("draw");
	}
	private void SetupFramebufferAndTexture() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(1, texture_ids);
		fbo_id=fbo_ids.get(0);
		texture_id=texture_ids.get(0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_DEPTH_COMPONENT32, 
				SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, 
				GL4.GL_TEXTURE_2D, texture_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer");
		}
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void LoadModels() {
		teapot_model_handle=Model3D.LoadModel("./Data/Model/OBJ/Teapot/teapot.obj");
		Model3D.RescaleModel(teapot_model_handle, VectorFunctions.VGet(0.5f, 0.5f, 0.5f));
		
		final float MODEL_SCALE=0.2f;
		ground_model_handle=Model3D.LoadModel("./Data/Model/BD1/Ground/ground.bd1");
		Model3D.RescaleModel(ground_model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		Model3D.RemoveAllPrograms(teapot_model_handle);
		Model3D.AddProgram(teapot_model_handle, "draw");
		Model3D.RemoveAllPrograms(ground_model_handle);
		Model3D.AddProgram(ground_model_handle, "draw");
	}
	private void SetupLight() {
		light_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		light_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		
		light_direction=VectorFunctions.VSub(light_target, light_position);
		light_direction=VectorFunctions.VNorm(light_direction);
		
		light_attenuation=0.02f;
		phi=MathFunctions.DegToRad(50.0f);
		theta=MathFunctions.DegToRad(30.0f);
		falloff=1.0f;
		ambient_color=ColorU8Functions.GetColorU8(0.1f, 0.1f, 0.1f, 1.0f);
		diffuse_power=2.0f;
		specular_power=2.0f;
	}
	private void SetupFronts() {
		CameraFront.AddProgram("draw");
		FogFront.AddProgram("draw");
		LightingFront.AddProgram("draw");
	}
	
	@Override
	protected void Update() {
		this.UpdateLight();
		this.UpdateShadowMap();
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(-30.0f, 30.0f, 30.0f), VectorFunctions.VGet(0.0f, 10.0f, 0.0f));
	}
	private void UpdateLight() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.5f));
		light_position=VectorFunctions.VTransform(light_position, rot_y);
		light_direction=VectorFunctions.VSub(light_target, light_position);
		light_direction=VectorFunctions.VNorm(light_direction);
		
		draw_program.Enable();
		draw_program.SetUniform("light_position", light_position);
		draw_program.SetUniform("light_direction", light_direction);
		draw_program.SetUniform("light_attenuation", light_attenuation);
		draw_program.SetUniform("phi", phi);
		draw_program.SetUniform("theta", theta);
		draw_program.SetUniform("falloff", falloff);
		draw_program.SetUniform("ambient_color", ambient_color);
		draw_program.SetUniform("diffuse_power", diffuse_power);
		draw_program.SetUniform("specular_power", specular_power);
		draw_program.Disable();
	}
	private void UpdateShadowMap() {
		Matrix projection=ProjectionMatrixFunctions.GetPerspectiveMatrix(phi, 1.0f, 1.0f, 200.0f);
		Matrix transformation=
				TransformationMatrixFunctions.GetViewTransformationMatrix(
						light_position, light_target, VectorFunctions.VGet(0.0f, 1.0f, 0.0f));
		Matrix depth_mvp=MatrixFunctions.MMult(projection, transformation);
		
		Matrix bias=new Matrix();
		bias.SetValue(0, 0, 0.5f);
		bias.SetValue(0, 3, 0.5f);
		bias.SetValue(1, 1, 0.5f);
		bias.SetValue(1, 3, 0.5f);
		bias.SetValue(2, 2, 0.5f);
		bias.SetValue(2, 3, 0.5f);
		bias.SetValue(3, 3, 1.0f);
		
		Matrix depth_bias_mvp=MatrixFunctions.MMult(bias, depth_mvp);
		
		depth_program.Enable();
		depth_program.SetUniform("depth_mvp", true, depth_mvp);
		depth_program.Disable();
		
		draw_program.Enable();
		draw_program.SetUniform("depth_bias_mvp", true, depth_bias_mvp);
		draw_program.Disable();
	}
	
	@Override
	protected void Draw() {
		//Depth
		depth_program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
		GLWrapper.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		Model3D.TransferModel(teapot_model_handle);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		depth_program.Disable();
		
		//Draw
		draw_program.Enable();
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		draw_program.SetUniform("shadow_map", 1);
		
		this.DrawWithSelfShadowing();
		
		draw_program.Disable();
	}
	private void DrawWithSelfShadowing() {
		draw_program.SetUniform("enable_shadow", 1);
		Model3D.DrawModel(ground_model_handle,0,"texture_sampler");
		Model3D.DrawModel(teapot_model_handle,0,"texture_sampler");
	}
	private void DrawWithoutSelfShadowing() {
		draw_program.SetUniform("enable_shadow", 1);
		Model3D.DrawModel(ground_model_handle,0,"texture_sampler");
		draw_program.SetUniform("enable_shadow", 0);
		Model3D.DrawModel(teapot_model_handle,0,"texture_sampler");
	}
}
