package com.daxie.testspace.joglf.g2.ocean;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class TildeHktComputation {
	private int N;
	private int L;
	private float t;
	
	private int fbo_id;
	//Input textures
	private int tilde_h0k_length_id;
	private int normalized_tilde_h0k_id;
	private int tilde_h0minusk_length_id;
	private int normalized_tilde_h0minusk_id;
	//Output textures
	private int tilde_hkt_length_id;
	private int normalized_tilde_hkt_id;
	
	private ShaderProgram program;
	
	public TildeHktComputation(int N) {
		this.N=N;
		L=1000;
		t=0.0f;
		
		this.SetupInputTextures();
		this.SetupOutputTextures();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTextures() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(4);
		GLWrapper.glGenTextures(4, texture_ids);
		tilde_h0k_length_id=texture_ids.get(0);
		normalized_tilde_h0k_id=texture_ids.get(1);
		tilde_h0minusk_length_id=texture_ids.get(2);
		normalized_tilde_h0minusk_id=texture_ids.get(3);
		
		for(int i=0;i<4;i++) {
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
		tilde_hkt_length_id=texture_ids.get(0);
		normalized_tilde_hkt_id=texture_ids.get(1);
		
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
				GL4.GL_TEXTURE_2D, tilde_hkt_length_id, 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT1, 
				GL4.GL_TEXTURE_2D, normalized_tilde_hkt_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("TildeHktComputation:Incomplete framebuffer");
		}
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0,GL4.GL_COLOR_ATTACHMENT1};
		GLWrapper.glDrawBuffers(2, Buffers.newDirectIntBuffer(draw_buffers));
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"tilde_hkt", 
				"./Data/Shader/330/ocean/tilde_hkt/vshader.glsl",
				"./Data/Shader/330/ocean/tilde_hkt/fshader.glsl");
		program=new ShaderProgram("tilde_hkt");
	}
	
	public void SetParameter(int L) {
		this.L=L;
	}
	public void AdvanceTime(float dt) {
		t+=dt;
	}
	public void SetTildeH0kLength(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0k_length_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	public void SetNormalizedTildeH0k(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0k_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	public void SetTildeH0minuskLength(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0minusk_length_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	public void SetNormalizedTildeMinusk(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0minusk_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	
	public void Compute() {
		program.Enable();
		program.SetUniform("N", N);
		program.SetUniform("L", L);
		program.SetUniform("t", t);
		
		GLWrapper.glViewport(0, 0, N, N);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0k_length_id);
		program.SetUniform("tilde_h0k_length", 0);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0k_id);
		program.SetUniform("normalized_tilde_h0k", 1);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE2);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0minusk_length_id);
		program.SetUniform("tilde_h0minusk_length", 2);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE3);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0minusk_id);
		program.SetUniform("normalized_tilde_h0minusk", 3);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	public FloatBuffer GetTildeHktLength() {
		FloatBuffer tilde_hkt_length_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_hkt_length_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, tilde_hkt_length_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return tilde_hkt_length_buf;
	}
	public FloatBuffer GetNormalizedTildeHkt() {
		FloatBuffer normalized_tilde_hkt_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_hkt_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, normalized_tilde_hkt_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return normalized_tilde_hkt_buf;
	}
}
