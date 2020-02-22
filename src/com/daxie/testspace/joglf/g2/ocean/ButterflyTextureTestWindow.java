package com.daxie.testspace.joglf.g2.ocean;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class ButterflyTextureTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int output_texture_id;
	private int input_texture_id;
	
	private static final int TEXTURE_WIDTH=9;//=log2(512)
	private static final int TEXTURE_HEIGHT=512;
	
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(2);
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(2, texture_ids);
		fbo_id=fbo_ids.get(0);
		output_texture_id=texture_ids.get(0);
		input_texture_id=texture_ids.get(1);
		
		this.SetupInputTexture();
		this.SetupOutputTexture();
		this.SetupFramebuffer();
		this.SetupProgram();
	}
	private void SetupInputTexture() {
		IntBuffer bit_reversed_indices=this.MakeBitReversedIndices();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0, GL4.GL_R32UI, 
				512, 1, 0, GL4.GL_RED_INTEGER, GL4.GL_UNSIGNED_INT, bit_reversed_indices);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);	
	}
	private IntBuffer MakeBitReversedIndices() {
		IntBuffer indices=Buffers.newDirectIntBuffer(512);
		
		//Even indices
		for(int i=0;i<256;i+=2) {
			indices.put(i);
			indices.put(i+256);
		}
		//Odd indices
		for(int i=1;i<256;i+=2) {
			indices.put(i);
			indices.put(i+256);
		}
		
		((Buffer)indices).flip();
		
		return indices;
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
				"butterfly_texture", 
				"./Data/Shader/330/ocean/butterfly_texture/vshader.glsl",
				"./Data/Shader/330/ocean/butterfly_texture/fshader.glsl");
		program=new ShaderProgram("butterfly_texture");
		
		program.Enable();
		program.SetUniform("N", 512);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glClear(GL4.GL_COLOR_BUFFER_BIT);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, input_texture_id);
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		program.SetUniform("bit_reversed_indices", 0);
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
			ImageIO.write(image, "bmp", new File("./Data/Screenshot/ocean/butterfly_texture.bmp"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
