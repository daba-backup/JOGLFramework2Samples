package com.daxie.testspace.joglf.g2.box_muller;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.FileFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class BoxMullerMethodWindow extends JOGLFWindow{
	private int fbo_id;
	private int input_texture_id;
	private int output_texture_id;
	
	private static final int TEXTURE_WIDTH=256;
	private static final int TEXTURE_HEIGHT=256;
	
	private ShaderProgram program;
	
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
	}
	private void SetupInputTexture() {
		Random random=new Random();
		
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer fbuf=Buffers.newDirectFloatBuffer(size);
		
		for(int i=0;i<size;i++) {
			fbuf.put(random.nextFloat());
		}
		((Buffer)fbuf).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, fbuf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupOutputTexture() {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA32F, 
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
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"box_muller", 
				"./Data/Shader/330/box_muller/vshader.glsl",
				"./Data/Shader/330/box_muller/fshader.glsl");
		program=new ShaderProgram("box_muller");
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		program.SetUniform("input_texture", 0);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		this.OutputResult();
		this.CloseWindow();
	}
	private void OutputResult() {
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer data=Buffers.newDirectFloatBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, data);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		List<String> lines=new ArrayList<>(size);
		for(int i=0;i<size;i++) {
			lines.add(String.valueOf(data.get()));
		}
		
		try {
			FileFunctions.CreateTextFile("./Data/Text/normal_distribution.txt", "UTF-8", lines);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
