package com.daxie.testspace.joglf.g2.take_screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

public class TakeScreenshotTestWindow extends JOGLFWindow{
	private int fbo_id;
	private int texture_id;
	private int renderbuffer_id;
	
	private int model_handle;
	
	private static final int TEXTURE_WIDTH=1024;
	private static final int TEXTURE_HEIGHT=1024;
	
	@Override
	protected void Init() {
		IntBuffer fbo_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		IntBuffer renderbuffer_ids=Buffers.newDirectIntBuffer(1);
		
		GLWrapper.glGenFramebuffers(1, fbo_ids);
		GLWrapper.glGenTextures(1, texture_ids);
		GLWrapper.glGenRenderbuffers(1, renderbuffer_ids);
		fbo_id=fbo_ids.get(0);
		texture_id=texture_ids.get(0);
		renderbuffer_id=renderbuffer_ids.get(0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, renderbuffer_id);
		GLWrapper.glRenderbufferStorage(
				GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT, 
				TEXTURE_WIDTH,TEXTURE_HEIGHT);
		GLWrapper.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
		
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glFramebufferTexture2D(
				GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, 
				GL4.GL_TEXTURE_2D, texture_id, 0);
		GLWrapper.glFramebufferRenderbuffer(
				GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, 
				GL4.GL_RENDERBUFFER, renderbuffer_id);
		if(GLWrapper.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER)!=GL4.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("Error:Incomplete framebuffer");
		}
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(0.1f, 0.1f, 0.1f));
	}
	
	@Override
	protected void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(50.0f, 50.0f, 50.0f), VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo_id);
		GLWrapper.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		GLWrapper.glClear(GL4.GL_DEPTH_BUFFER_BIT|GL4.GL_COLOR_BUFFER_BIT);
		Model3D.DrawModel(model_handle);
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLWrapper.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		ByteBuffer texture_data=Buffers.newDirectByteBuffer(TEXTURE_WIDTH*TEXTURE_HEIGHT*4);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, texture_data);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		BufferedImage image=new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		int pos=0;
		for(int y=TEXTURE_HEIGHT-1;y>=0;y--) {
			for(int x=0;x<TEXTURE_WIDTH;x++) {
				int r=Byte.toUnsignedInt(texture_data.get(pos));
				int g=Byte.toUnsignedInt(texture_data.get(pos+1));
				int b=Byte.toUnsignedInt(texture_data.get(pos+2));
				int a=Byte.toUnsignedInt(texture_data.get(pos+3));
				
				int rgb=(a<<24)|(r<<16)|(g<<8)|b;
				
				image.setRGB(x, y, rgb);
				
				pos+=4;
			}
		}
		
		try {
			ImageIO.write(image, "bmp", new File("./Data/Screenshot/screenshot.bmp"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		this.CloseWindow();
	}
}
