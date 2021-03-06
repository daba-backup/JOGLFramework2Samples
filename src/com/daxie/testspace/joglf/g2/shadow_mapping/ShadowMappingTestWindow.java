package com.daxie.testspace.joglf.g2.shadow_mapping;

import java.nio.IntBuffer;

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
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class ShadowMappingTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int texture_id;
	
	private static final int SHADOW_WIDTH=2048;
	private static final int SHADOW_HEIGHT=2048;
	
	private int model_handle;
	
	private ShaderProgram depth_program;
	private ShaderProgram draw_program;

	@Override
	protected void Init() {
		//Set up the programs.
		GLShaderFunctions.CreateProgram(
				"depth", 
				"./Data/Shader/330/shadow_mapping/depth_vshader.glsl",
				"./Data/Shader/330/shadow_mapping/depth_fshader.glsl");
		GLShaderFunctions.CreateProgram(
				"draw", 
				"./Data/Shader/330/shadow_mapping/draw_vshader.glsl",
				"./Data/Shader/330/shadow_mapping/draw_fshader.glsl");
		depth_program=new ShaderProgram("depth");
		draw_program=new ShaderProgram("draw");
		
		//Set up a framebuffer and a depth texture.
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(1, texture_ids);
		fbo_id=fbo_ids.get(0);
		texture_id=texture_ids.get(0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_DEPTH_COMPONENT, 
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
		
		//Load a model.
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/Objet/objet.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		Model3D.RemoveAllPrograms(model_handle);
		Model3D.AddProgram(model_handle, "draw");
		
		//Set up fronts.
		CameraFront.AddProgram("draw");
		FogFront.AddProgram("draw");
		LightingFront.AddProgram("draw");
	}
	
	@Override
	protected void Update() {
		Vector light_position=VectorFunctions.VGet(-20.0f, 20.0f, 20.0f);
		Vector light_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		
		Matrix projection=ProjectionMatrixFunctions.GetOrthogonalMatrix(-40.0f, 40.0f, -40.0f, 40.0f, 1.0f, 100.0f);
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
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(10.0f, 20.0f, 10.0f), VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		LightingFront.SetLightDirection(light_position, light_target);
	}
	
	@Override
	protected void Draw() {
		depth_program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
		GLWrapper.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		GLWrapper.glCullFace(GL4.GL_FRONT);
		Model3D.TransferModel(model_handle);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		depth_program.Disable();
		
		draw_program.Enable();
		GLWrapper.glCullFace(GL4.GL_BACK);
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		draw_program.SetUniform("shadow_map", 1);
		Model3D.DrawModel(model_handle,0,"texture_sampler");
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		draw_program.Disable();
	}
}
