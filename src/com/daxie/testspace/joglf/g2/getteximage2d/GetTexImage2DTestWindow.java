package com.daxie.testspace.joglf.g2.getteximage2d;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.texture.TextureMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class GetTexImage2DTestWindow extends JOGLFWindow{
	private int model_handle;
	
	@Override
	protected void Init() {
		this.SetupModel();
		this.SetupTexture();
	}
	private void SetupModel() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/XOPS/Cube/cube.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
	}
	private void SetupTexture() {
		final int TEXTURE_WIDTH=256;
		final int TEXTURE_HEIGHT=256;
		
		Random random=new Random();
		ByteBuffer buf=Buffers.newDirectByteBuffer(TEXTURE_WIDTH*TEXTURE_HEIGHT*4);
		
		int bound=TEXTURE_WIDTH*TEXTURE_HEIGHT*4;
		for(int i=0;i<bound;i+=4) {
			int r=random.nextInt(256);
			int g=random.nextInt(256);
			int b=random.nextInt(256);
			int a=255;
			
			buf.put((byte)r);
			buf.put((byte)g);
			buf.put((byte)b);
			buf.put((byte)a);
		}
		((Buffer)buf).flip();
		
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenTextures(1, texture_ids);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, texture_ids.get(0));
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, buf);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		int texture_handle=TextureMgr.AssociateTexture(
				texture_ids.get(0), TEXTURE_WIDTH, TEXTURE_HEIGHT, false);
		Model3D.ChangeModelTexture(model_handle, 0, texture_handle);
	}
	
	@Override
	protected void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(10.0f, 10.0f, 10.0f), 
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
