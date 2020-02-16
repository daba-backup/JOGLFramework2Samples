package com.daxie.testspace.joglf.g2.multiview;

import java.nio.IntBuffer;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.MathFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class MultiviewWindow2 extends JOGLFWindow{
	private IntBuffer fbo_ids;
	private IntBuffer renderbuffer_ids;
	private IntBuffer texture_ids;
	
	private int model_handle;
	
	private Vector camera_position;
	
	@Override
	protected void Init() {
		fbo_ids=Buffers.newDirectIntBuffer(2);
		renderbuffer_ids=Buffers.newDirectIntBuffer(2);
		texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenFramebuffers(2, fbo_ids);
		GLWrapper.glGenTextures(2, texture_ids);
		GLWrapper.glGenRenderbuffers(2, renderbuffer_ids);
		
		model_handle=Model3D.LoadModel("./Data/Model/OBJ/Teapot/teapot.obj");
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		
		GLWrapper.glDisable(GL4.GL_CULL_FACE);
		
		GLShaderFunctions.CreateProgram(
				"phong", 
				"./Data/Shader/330/texture/phong/vshader.glsl",
				"./Data/Shader/330/texture/phong/fshader.glsl");
		
		CameraFront.AddProgram("phong");
		LightingFront.AddProgram("phong");
		FogFront.AddProgram("phong");
	}
	
	@Override
	protected void Reshape(int x,int y,int width,int height) {
		//Renderbuffers
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, renderbuffer_ids.get(0));
		GLWrapper.glRenderbufferStorage(
				GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT, width/2, height);
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
		
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, renderbuffer_ids.get(1));
		GLWrapper.glRenderbufferStorage(
				GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT, width/2, height);
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
		
		//Textures
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(0));
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				width/2, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(1));
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				width/2, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		//Framebuffers
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_ids.get(0));
		GLWrapper.glFramebufferRenderbuffer(
				GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, 
				GL4.GL_RENDERBUFFER, renderbuffer_ids.get(0));
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, texture_ids.get(0), 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer (left)");
		}
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_ids.get(1));
		GLWrapper.glFramebufferRenderbuffer(
				GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, 
				GL4.GL_RENDERBUFFER, renderbuffer_ids.get(1));
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, texture_ids.get(1), 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer (right)");
		}
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.5f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 10.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		int width=this.GetWidth();
		int height=this.GetHeight();
		
		//Draw to the framebuffers.
		Model3D.SetDefaultProgram(model_handle);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_ids.get(0));
		GLWrapper.glViewport(0, 0, width/2, height);
		GLWrapper.glClear(GL4.GL_DEPTH_BUFFER_BIT|GL4.GL_COLOR_BUFFER_BIT);
		Model3D.DrawModel(model_handle);
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		Model3D.RemoveAllPrograms(model_handle);
		Model3D.AddProgram(model_handle, "phong");
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_ids.get(1));
		GLWrapper.glViewport(0, 0, width/2, height);
		GLWrapper.glClear(GL4.GL_DEPTH_BUFFER_BIT|GL4.GL_COLOR_BUFFER_BIT);
		Model3D.DrawModel(model_handle);
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		//Draw to the actual screen.
		GLShaderFunctions.UseProgram("texture_drawer");
		GLWrapper.glViewport(0, 0, width, height);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(0));
		GLDrawFunctions2D.TransferQuad(-1.0f, -1.0f, 0.0f, 1.0f);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(1));
		GLDrawFunctions2D.TransferQuad(0.0f, -1.0f, 1.0f, 1.0f);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
}
