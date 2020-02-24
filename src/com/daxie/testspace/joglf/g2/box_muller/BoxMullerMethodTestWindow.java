package com.daxie.testspace.joglf.g2.box_muller;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.transferer.FullscreenQuadTransferer;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.FileFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class BoxMullerMethodTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int input_texture_id;
	private int output_texture_id;
	
	private ShaderProgram program;
	
	private static final int TEXTURE_WIDTH=128;
	private static final int TEXTURE_HEIGHT=128;
	
	private FullscreenQuadTransferer fqt;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(2, texture_ids);
		fbo_id=fbo_ids.get(0);
		input_texture_id=texture_ids.get(0);
		output_texture_id=texture_ids.get(1);
		
		this.SetupInputTexture();
		this.SetupOutputTexture();
		this.SetupFramebuffer();
		this.SetupProgram();
		
		fqt=new FullscreenQuadTransferer();
	}
	private void SetupInputTexture() {
		Random random=new Random();
		
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer uniform_rnds_buf=Buffers.newDirectFloatBuffer(size);
		
		for(int i=0;i<size;i++) {
			uniform_rnds_buf.put(random.nextFloat());
		}
		((Buffer)uniform_rnds_buf).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, uniform_rnds_buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupOutputTexture() {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupFramebuffer() {
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, output_texture_id, 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer");
		}
		
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0};
		GLWrapper.glDrawBuffers(1, Buffers.newDirectIntBuffer(draw_buffers));
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"box_muller", 
				"./Data/Shader/330/box_muller/vshader.glsl",
				"./Data/Shader/330/box_muller/fshader.glsl");
		program=new ShaderProgram("box_muller");
		
		program.Enable();
		program.SetUniform("rnd_nums", TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		program.SetUniform("uniform_rnds", 0);
		fqt.Transfer();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer normal_rnds_buf=Buffers.newDirectFloatBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, normal_rnds_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		List<String> lines=new ArrayList<>();
		for(int i=0;i<size;i+=4) {
			float r=normal_rnds_buf.get();
			float g=normal_rnds_buf.get();
			float b=normal_rnds_buf.get();
			float a=normal_rnds_buf.get();
			String str="("+r+","+g+","+b+","+a+")";
			
			lines.add(str);
		}
		
		try {
			FileFunctions.CreateTextFile("./Data/Text/box_muller/normal_rnds.txt", "UTF-8", lines);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		this.CloseWindow();
	}
}
