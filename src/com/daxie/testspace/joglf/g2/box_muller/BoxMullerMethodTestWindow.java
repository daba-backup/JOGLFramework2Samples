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

public class BoxMullerMethodTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int input_texture_id;
	private int[] output_texture_ids;
	
	private ShaderProgram program;
	
	private static final int TEXTURE_WIDTH=256;
	private static final int TEXTURE_HEIGHT=256;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(3);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(3, texture_ids);
		fbo_id=fbo_ids.get(0);
		input_texture_id=texture_ids.get(0);
		output_texture_ids=new int[] {texture_ids.get(1),texture_ids.get(2)};
		
		this.SetupInputTexture();
		this.SetupOutputTextures();
		this.SetupFramebuffer();
		this.SetupProgram();
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
	private void SetupOutputTextures() {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[0]);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[1]);
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
				GL4.GL_TEXTURE_2D, output_texture_ids[0], 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT1, 
				GL4.GL_TEXTURE_2D, output_texture_ids[1], 0);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer");
		}
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0,GL4.GL_COLOR_ATTACHMENT1};
		GLWrapper.glDrawBuffers(2, Buffers.newDirectIntBuffer(draw_buffers));
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
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		program.SetUniform("uniform_rnds", 0);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer normalized_rnds_buf=Buffers.newDirectFloatBuffer(size);
		FloatBuffer rnds_size_buf=Buffers.newDirectFloatBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[0]);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, normalized_rnds_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[1]);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, rnds_size_buf);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		List<String> lines=new ArrayList<>();
		for(int i=0;i<size;i+=4) {
			float rnds_size=rnds_size_buf.get(i);
			
			for(int j=0;j<4;j++) {
				float rnd=normalized_rnds_buf.get(i+j)*rnds_size;
				lines.add(String.valueOf(rnd));
			}
		}
		
		try {
			FileFunctions.CreateTextFile("./Data/Text/box_muller/rnds.txt", "UTF-8", lines);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		this.CloseWindow();
	}
}
