package com.daxie.testspace.joglf.g2.ocean;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class TildeH0kComputation {
	private int N;
	private int L;
	private float A;
	private float v;
	private float wx;
	private float wz;
	
	private int fbo_id;
	//Input texture
	private int uniform_rnds_id;
	//Output textures
	private int tilde_h0k_length_id;
	private int normalized_tilde_h0k_id;
	private int tilde_h0minusk_length_id;
	private int normalized_tilde_h0minusk_id;
	
	private ShaderProgram program;
	
	public TildeH0kComputation(int N) {
		this.N=N;
		L=1000;
		A=4.0f;
		v=40.0f;
		wx=1.0f;
		wz=1.0f;
		
		this.SetupInputTexture();
		this.SetupOutputTextures();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTexture() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenTextures(1, texture_ids);
		uniform_rnds_id=texture_ids.get(0);
		
		Random random=new Random();
		
		int size=N*N*4;
		FloatBuffer uniform_rnds=Buffers.newDirectFloatBuffer(size);
		
		for(int i=0;i<size;i++) {
			uniform_rnds.put(random.nextFloat());
		}
		((Buffer)uniform_rnds).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, uniform_rnds_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, uniform_rnds);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupOutputTextures() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(4);
		GLWrapper.glGenTextures(4, texture_ids);
		tilde_h0k_length_id=texture_ids.get(0);
		normalized_tilde_h0k_id=texture_ids.get(1);
		tilde_h0minusk_length_id=texture_ids.get(2);
		normalized_tilde_h0minusk_id=texture_ids.get(3);
		
		for(int i=0;i<4;i++) {
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
				GL4.GL_TEXTURE_2D, tilde_h0k_length_id, 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT1, 
				GL4.GL_TEXTURE_2D, normalized_tilde_h0k_id, 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT2, 
				GL4.GL_TEXTURE_2D, tilde_h0minusk_length_id, 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT3, 
				GL4.GL_TEXTURE_2D, normalized_tilde_h0minusk_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("TildeH0kComputation:Incomplete framebuffer");
		}
		int[] draw_buffers=new int[] {
				GL4.GL_COLOR_ATTACHMENT0,GL4.GL_COLOR_ATTACHMENT1,
				GL4.GL_COLOR_ATTACHMENT2,GL4.GL_COLOR_ATTACHMENT3};
		GLWrapper.glDrawBuffers(4, Buffers.newDirectIntBuffer(draw_buffers));
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"tilde_h0k", 
				"./Data/Shader/330/ocean/tilde_h0k/vshader.glsl",
				"./Data/Shader/330/ocean/tilde_h0k/fshader.glsl");
		program=new ShaderProgram("tilde_h0k");
	}
	
	public void SetParameters(int L,float A,float v,float wx,float wz) {
		this.L=L;
		this.A=A;
		this.v=v;
		this.wx=wx;
		this.wz=wz;
	}
	
	public void Compute() {
		program.Enable();
		program.SetUniform("N", N);
		program.SetUniform("L", L);
		program.SetUniform("A", A);
		program.SetUniform("v", v);
		program.SetUniform("w", wx, wz);
		
		GLWrapper.glViewport(0, 0, N, N);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, uniform_rnds_id);
		program.SetUniform("uniform_rnds", 0);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	public FloatBuffer GetTildeH0kLength() {
		FloatBuffer tilde_h0k_length_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0k_length_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, tilde_h0k_length_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return tilde_h0k_length_buf;
	}
	public FloatBuffer GetNormalizedTildeH0k() {
		FloatBuffer normalized_tilde_h0k_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0k_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, normalized_tilde_h0k_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return normalized_tilde_h0k_buf;
	}
	public FloatBuffer GetTildeH0minuskLength() {
		FloatBuffer tilde_h0minusk_length_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, tilde_h0minusk_length_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, tilde_h0minusk_length_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return tilde_h0minusk_length_buf;
	}
	public FloatBuffer GetNormalizedTildeH0minusk() {
		FloatBuffer normalized_tilde_h0minusk_buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, normalized_tilde_h0minusk_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, normalized_tilde_h0minusk_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return normalized_tilde_h0minusk_buf;
	}
}
