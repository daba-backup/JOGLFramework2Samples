package com.daxie.testspace.joglf.g2.ocean;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.FileFunctions;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class HktTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int output_texture_id;
	private int[] input_texture_ids;
	
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
		output_texture_id=texture_ids.get(0);
		input_texture_ids=new int[] {texture_ids.get(1),texture_ids.get(2)};
		
		this.SetupInputTextures();
		this.SetupOutputTexture();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTextures() {
		List<String> h0k_lines;
		List<String> h0minusk_lines;
		try {
			h0k_lines=FileFunctions.GetFileAllLines("./Data/Text/ocean/tilde_h0k.txt", "UTF-8");
			h0minusk_lines=FileFunctions.GetFileAllLines("./Data/Text/ocean/tilde_h0minusk.txt", "UTF-8");
		}
		catch(IOException e) {
			e.printStackTrace();
			this.CloseWindow();
			
			return;
		}
		
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		FloatBuffer h0k_buf=Buffers.newDirectFloatBuffer(size);
		FloatBuffer h0minusk_buf=Buffers.newDirectFloatBuffer(size);
		
		int line_num=h0k_lines.size();
		for(int i=0;i<line_num;i+=2) {
			String str_r=h0k_lines.get(i);
			String str_g=h0k_lines.get(i+1);
			float r=Float.parseFloat(str_r);
			float g=Float.parseFloat(str_g);
			h0k_buf.put(r);
			h0k_buf.put(g);
			h0k_buf.put(0.0f);
			h0k_buf.put(1.0f);
			
			str_r=h0minusk_lines.get(i);
			str_g=h0minusk_lines.get(i+1);
			r=Float.parseFloat(str_r);
			g=Float.parseFloat(str_g);
			h0minusk_buf.put(r);
			h0minusk_buf.put(g);
			h0minusk_buf.put(0.0f);
			h0minusk_buf.put(1.0f);
		}
		
		((Buffer)h0k_buf).flip();
		((Buffer)h0minusk_buf).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_ids[0]);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, h0k_buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_ids[1]);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA32F, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_FLOAT, h0minusk_buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupOutputTexture() {
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
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
				"tilde_hkt", 
				"./Data/Shader/330/ocean/tilde_hkt/vshader.glsl",
				"./Data/Shader/330/ocean/tilde_hkt/fshader.glsl");
		program=new ShaderProgram("tilde_hkt");
		
		program.Enable();
		program.SetUniform("N", 512);
		program.SetUniform("L", 1000);
		program.SetUniform("t", 0.0f);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glClear(GL4.GL_COLOR_BUFFER_BIT);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_ids[0]);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		program.SetUniform("tilde_h0k", 0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_ids[1]);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		program.SetUniform("tilde_h0minusk", 1);
		GLDrawFunctions2D.TransferFullscreenQuad();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		this.SaveTexture();
		this.CloseWindow();
	}
	private void SaveTexture() {
		int size=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		ByteBuffer data=Buffers.newDirectByteBuffer(size);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, output_texture_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		BufferedImage image=new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		int pos=0;
		for(int y=TEXTURE_HEIGHT-1;y>=0;y--) {
			for(int x=0;x<TEXTURE_WIDTH;x++) {
				int r=Byte.toUnsignedInt(data.get(pos));
				int g=Byte.toUnsignedInt(data.get(pos+1));
				int b=Byte.toUnsignedInt(data.get(pos+2));
				int a=Byte.toUnsignedInt(data.get(pos+3));
				int rgb=(a<<24)|(r<<16)|(g<<8)|b;
				image.setRGB(x, y, rgb);
				
				pos+=4;
			}
		}
		
		try {
			ImageIO.write(image, "bmp", new File("./Data/Screenshot/ocean/tilde_hkt.bmp"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}