package com.daxie.testspace.joglf.g2.ocean;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class InversionAndPermutation {
	private int N;
	
	private int fbo_id;
	//Input textures
	private int input_length_id;
	private int normalized_input_id;
	//Output textures
	private int heightmap_length_id;
	private int normalized_heightmap_id;
	
	private ShaderProgram program;
	
	public InversionAndPermutation(int N) {
		this.N=N;
		
		this.SetupInputTextures();
		this.SetupOutputTextures();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTextures() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenTextures(2, texture_ids);
		input_length_id=texture_ids.get(0);
		normalized_input_id=texture_ids.get(1);
		
		for(int i=0;i<2;i++) {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(i));
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}
	private void SetupOutputTextures() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenTextures(2, texture_ids);
		heightmap_length_id=texture_ids.get(0);
		normalized_heightmap_id=texture_ids.get(1);
		
		for(int i=0;i<2;i++) {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(i));
			GLWrapper.glTexImage2D(
					GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
					N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, null);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}
	private void SetupFramebuffer() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		fbo_id=fbo_ids.get(0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, heightmap_length_id, 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT1, 
				GL4.GL_TEXTURE_2D, normalized_heightmap_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("InversionAndPermutation:Incomplete framebuffer");
		}
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0,GL4.GL_COLOR_ATTACHMENT1};
		GLWrapper.glDrawBuffers(2, Buffers.newDirectIntBuffer(draw_buffers));
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"inv_and_perm", 
				"./Data/Shader/330/ocean/inv_and_perm/vshader.glsl",
				"./Data/Shader/330/ocean/inv_and_perm/fshader.glsl");
		program=new ShaderProgram("inv_and_perm");
	}
	
	public void SetInputLength(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_length_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	public void SetNormalizedInput(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_input_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	
	public void Compute() {
		program.Enable();
		program.SetUniform("N", N);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_length_id);
		program.SetUniform("input_length", 0);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_input_id);
		program.SetUniform("normalized_input", 1);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	public FloatBuffer GetHeightmapLength() {
		FloatBuffer buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, heightmap_length_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return buf;
	}
	public FloatBuffer GetNormalizedHeightmap() {
		FloatBuffer buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_heightmap_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return buf;
	}
}
