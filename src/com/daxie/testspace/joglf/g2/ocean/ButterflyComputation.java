package com.daxie.testspace.joglf.g2.ocean;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.transferer.FullscreenQuadTransferer;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class ButterflyComputation {
	private int N;
	private int total_stage_num;
	
	private int fbo_id;
	//Input textures
	private int butterfly_texture_id;
	private int pingpong_in_id;
	//Output texture
	private int pingpong_out_id;
	
	private ShaderProgram program;
	private FullscreenQuadTransferer transferer;
	
	public ButterflyComputation(int N) {
		this.N=N;
		total_stage_num=(int)Math.round(Math.log(N)/Math.log(2));
		
		transferer=new FullscreenQuadTransferer();
		
		this.SetupInputTextures();
		this.SetupOutputTexture();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTextures() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenTextures(2, texture_ids);
		butterfly_texture_id=texture_ids.get(0);
		pingpong_in_id=texture_ids.get(1);
		
		for(int i=0;i<2;i++) {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(i));
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
	}
	private void SetupOutputTexture() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenTextures(1, texture_ids);
		pingpong_out_id=texture_ids.get(0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_out_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupFramebuffer() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		fbo_id=fbo_ids.get(0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0};
		GLWrapper.glDrawBuffers(1, Buffers.newDirectIntBuffer(draw_buffers));
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"butterfly_computation", 
				"./Data/Shader/330/ocean/butterfly_computation/vshader.glsl",
				"./Data/Shader/330/ocean/butterfly_computation/fshader.glsl");
		program=new ShaderProgram("butterfly_computation");
	}
	
	public void SetButterflyTexture(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, butterfly_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				total_stage_num, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	public void SetPingpongIn(FloatBuffer buf) {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_in_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	
	public void Compute() {
		program.Enable();
		program.SetUniform("N", N);
		program.SetUniform("total_stage_num", total_stage_num);
		
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, butterfly_texture_id);
		program.SetUniform("butterfly_texture", 0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		
		//Horizontal
		program.SetUniform("direction", 0);
		for(int i=0;i<total_stage_num;i++) {
			program.SetUniform("stage", i);
			
			if(i%2==0)this.innerCompute1();
			else this.innerCompute2();
			
			transferer.Transfer();
		}
		
		//Vertical
		program.SetUniform("direction", 1);
		for(int i=0;i<total_stage_num;i++) {
			program.SetUniform("stage", i);
			
			if(total_stage_num%2==0) {
				if(i%2==0)this.innerCompute1();
				else this.innerCompute2();	
			}
			else {
				if(i%2==0)this.innerCompute2();
				else this.innerCompute1();	
			}
			
			transferer.Transfer();
		}
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void innerCompute1() {
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, pingpong_out_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("ButterflyComputation:Incomplete framebuffer");
		}
		
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_in_id);
		program.SetUniform("pingpong_in", 1);
	}
	private void innerCompute2() {
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, pingpong_in_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("ButterflyComputation:Incomplete framebuffer");
		}
		
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_out_id);
		program.SetUniform("pingpong_in", 1);
	}
	
	public FloatBuffer GetComputationResult() {
		FloatBuffer buf=Buffers.newDirectFloatBuffer(N*N*4);
		
		if(total_stage_num%2==0) {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_in_id);
			GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
		else {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_out_id);
			GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, buf);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		}
		
		return buf;
	}
}
