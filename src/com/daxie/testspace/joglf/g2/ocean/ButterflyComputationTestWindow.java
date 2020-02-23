package com.daxie.testspace.joglf.g2.ocean;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.FileFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class ButterflyComputationTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int butterfly_texture_id;
	private int[] pingpong_texture_ids;
	
	private static final int TEXTURE_WIDTH=512;
	private static final int TEXTURE_HEIGHT=512;
	
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(2);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(3);
		GLWrapper.glGenFramebuffers(2, fbo_ids);
		GLWrapper.glGenTextures(3, texture_ids);
		fbo_id=fbo_ids.get(0);
		butterfly_texture_id=texture_ids.get(0);
		pingpong_texture_ids=new int[] {texture_ids.get(1),texture_ids.get(2)};
		
		this.SetupButterflyTexture();
		this.SetupPingpongTextures();
		this.SetupPrograms();
	}
	private void SetupButterflyTexture() {
		List<String> butterfly_lines;
		try {
			butterfly_lines=FileFunctions.GetFileAllLines("./Data/Text/ocean/butterfly_texture.txt", "UTF-8");
		}
		catch(IOException e) {
			e.printStackTrace();
			this.CloseWindow();
			
			return;
		}
		
		int line_num=butterfly_lines.size();
		int size=9*512*4;//=log2(N)*N*(RGBA components)
		FloatBuffer butterfly_buf=Buffers.newDirectFloatBuffer(size);
		
		for(int i=0;i<line_num;i+=4) {
			String str_r=butterfly_lines.get(i);
			String str_g=butterfly_lines.get(i+1);
			String str_b=butterfly_lines.get(i+2);
			String str_a=butterfly_lines.get(i+3);
			float r=Float.parseFloat(str_r);
			float g=Float.parseFloat(str_g);
			float b=Float.parseFloat(str_b);
			float a=Float.parseFloat(str_a);
			
			butterfly_buf.put(r);
			butterfly_buf.put(g);
			butterfly_buf.put(b);
			butterfly_buf.put(a);
		}
		((Buffer)butterfly_buf).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, butterfly_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA32F, 
				9, 512, 0, GL4.GL_RGBA, GL4.GL_FLOAT, butterfly_buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);	
	}
	private void SetupPingpongTextures() {
		List<String> hkt_lines;
		try {
			hkt_lines=FileFunctions.GetFileAllLines("./Data/Text/ocean/tilde_hkt.txt", "UTF-8");
		}
		catch(IOException e) {
			e.printStackTrace();
			this.CloseWindow();
			
			return;
		}
		
		int line_num=hkt_lines.size();
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer hkt_buf=Buffers.newDirectFloatBuffer(size);
		
		for(int i=0;i<line_num;i+=2) {
			String str_r=hkt_lines.get(i);
			String str_g=hkt_lines.get(i+1);
			float r=Float.parseFloat(str_r);
			float g=Float.parseFloat(str_g);
			
			hkt_buf.put(r);
			hkt_buf.put(g);
			hkt_buf.put(0.0f);
			hkt_buf.put(1.0f);
		}
		((Buffer)hkt_buf).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[0]);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, hkt_buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);	
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[1]);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);	
	}
	private void SetupPrograms() {
		GLShaderFunctions.CreateProgram(
				"butterfly_computation", 
				"./Data/Shader/330/ocean/butterfly_computation/vshader.glsl",
				"./Data/Shader/330/ocean/butterfly_computation/fshader.glsl");
		program=new ShaderProgram("butterfly_computation");
		
		program.Enable();
		program.SetUniform("N", 512);
		program.SetUniform("total_stage_num", 9);
	}
	
	@Override
	protected void Draw() {
		this.ButterflyComputation();
		
		this.SaveResult();
		this.CloseWindow();
	}
	private void ButterflyComputation() {
		program.Enable();
		
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, butterfly_texture_id);
		program.SetUniform("butterfly_texture", 0);
		
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		
		//Horizontal
		program.SetUniform("direction", 0);
		for(int i=0;i<9;i++) {
			program.SetUniform("stage", i);
			
			GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
			
			if(i%2==0) {
				GLWrapper.glFramebufferTexture2D(
						GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
						GL4.GL_TEXTURE_2D, pingpong_texture_ids[1], 0);
				if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("Error:Incomplete framebuffer");
				}
				GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
				GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[0]);
				program.SetUniform("input_texture", 1);
			}
			else {
				GLWrapper.glFramebufferTexture2D(
						GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
						GL4.GL_TEXTURE_2D, pingpong_texture_ids[0], 0);
				if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("Error:Incomplete framebuffer");
				}
				GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
				GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[1]);
				program.SetUniform("input_texture", 1);
			}
			
			GLDrawFunctions2D.TransferFullscreenQuad();
			GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		}
		
		//Vertical
		program.SetUniform("direction", 1);
		for(int i=0;i<9;i++) {
			program.SetUniform("stage", i);
			
			GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
			
			if(i%2==0) {
				GLWrapper.glFramebufferTexture2D(
						GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
						GL4.GL_TEXTURE_2D, pingpong_texture_ids[1], 0);
				if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("Error:Incomplete framebuffer");
				}
				GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
				GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[0]);
				program.SetUniform("input_texture", 1);	
			}
			else {
				GLWrapper.glFramebufferTexture2D(
						GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
						GL4.GL_TEXTURE_2D, pingpong_texture_ids[0], 0);
				if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
					System.out.println("Error:Incomplete framebuffer");
				}
				GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
				GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[1]);
				program.SetUniform("input_texture", 1);
			}
			
			GLDrawFunctions2D.TransferFullscreenQuad();
			GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		}
	}
	private void SaveResult() {
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer data=Buffers.newDirectFloatBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, pingpong_texture_ids[1]);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, data);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		List<String> data_lines=new ArrayList<>();
		for(int i=0;i<size;i+=4) {
			data_lines.add(String.valueOf(data.get(i)));
			data_lines.add(String.valueOf(data.get(i+1)));
		}
		
		try {
			FileFunctions.CreateTextFile("./Data/Text/ocean/pingpong.txt", "UTF-8", data_lines);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
