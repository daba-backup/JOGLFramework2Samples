package com.daxie.testspace.joglf.g2.ocean;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class H0kTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int[] output_texture_ids;
	private int input_texture_id;
	
	private static final int TEXTURE_WIDTH=512;
	private static final int TEXTURE_HEIGHT=512;
	
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(3);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(3, texture_ids);
		
		fbo_id=fbo_ids.get(0);
		output_texture_ids=new int[] {texture_ids.get(0),texture_ids.get(1)};
		input_texture_id=texture_ids.get(2);
		
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
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, fbuf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupOutputTexture() {
		for(int i=0;i<2;i++) {
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[i]);
			GLWrapper.glTexImage2D(
					GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 
					TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);	
		}
	}
	private void SetupFramebuffer() {
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, output_texture_ids[0], 0);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT1, 
				GL4.GL_TEXTURE_2D, output_texture_ids[1], 0);
		int[] draw_buffers=new int[] {GL4.GL_COLOR_ATTACHMENT0,GL4.GL_COLOR_ATTACHMENT1};
		GLWrapper.glDrawBuffers(2, Buffers.newDirectIntBuffer(draw_buffers));
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer");
		}
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"tilde_h0k", 
				"./Data/Shader/330/ocean/visualization/tilde_h0k/vshader.glsl",
				"./Data/Shader/330/ocean/visualization/tilde_h0k/fshader.glsl");
		program=new ShaderProgram("tilde_h0k");
		
		program.Enable();
		program.SetUniform("N", 512);
		program.SetUniform("L", 1000);
		program.SetUniform("A", 4.0f);
		program.SetUniform("v", 40.0f);
		program.SetUniform("w", 1.0f, 1.0f);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glClear(GL4.GL_COLOR_BUFFER_BIT);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		program.SetUniform("input_texture", 0);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		this.SaveTextures();
		this.CloseWindow();
	}
	private void SaveTextures() {
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		ByteBuffer data0=Buffers.newDirectByteBuffer(size);
		ByteBuffer data1=Buffers.newDirectByteBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[0]);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_ids[1]);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		BufferedImage image0=new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage image1=new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		int pos=0;
		for(int y=TEXTURE_HEIGHT-1;y>=0;y--) {
			for(int x=0;x<TEXTURE_WIDTH;x++) {
				int r=Byte.toUnsignedInt(data0.get(pos));
				int g=Byte.toUnsignedInt(data0.get(pos+1));
				int b=Byte.toUnsignedInt(data0.get(pos+2));
				int a=Byte.toUnsignedInt(data0.get(pos+3));
				int rgb=(a<<24)|(r<<16)|(g<<8)|b;
				image0.setRGB(x, y, rgb);
				
				r=Byte.toUnsignedInt(data1.get(pos));
				g=Byte.toUnsignedInt(data1.get(pos+1));
				b=Byte.toUnsignedInt(data1.get(pos+2));
				a=Byte.toUnsignedInt(data1.get(pos+3));
				rgb=(a<<24)|(r<<16)|(g<<8)|b;
				image1.setRGB(x, y, rgb);
				
				pos+=4;
			}
		}
		
		try {
			ImageIO.write(image0, "bmp", new File("./Data/Screenshot/ocean/tilde_h0k.bmp"));
			ImageIO.write(image1, "bmp", new File("./Data/Screenshot/ocean/tilde_h0minusk.bmp"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
