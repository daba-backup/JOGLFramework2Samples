package com.daxie.testspace.joglf.g2.fbo_texture_onto_model;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.util.screen.Screen;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.MathFunctions;

class FBOTextureOntoModelTestWindow extends JOGLFWindow{
	private int cube_handle;
	private int model_handle;
	
	private static final int SCREEN_WIDTH=1024;
	private static final int SCREEN_HEIGHT=1024;
	private Screen screen;
	
	private Vector offscreen_camera_pos;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		cube_handle=Model3D.LoadModel("./Data/Model/BD1/XOPS/Cube/cube.bd1");
		Model3D.RescaleModel(cube_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		model_handle=Model3D.LoadModel("./Data/Model/BD1/XOPS/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		screen=new Screen(SCREEN_WIDTH, SCREEN_HEIGHT);
		int texture_handle=screen.Associate(false);
		
		Model3D.ChangeModelTexture(cube_handle, 2, texture_handle);
		Model3D.ChangeModelTexture(cube_handle, 3, texture_handle);
		
		offscreen_camera_pos=VectorFunctions.VGet(40.0f, 40.0f, 40.0f);
	}
	
	@Override
	protected void Draw() {
		screen.Bind();
		GLWrapper.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		screen.Clear();
		screen.Fit();
		this.SetupCameraForOffscreen();
		Model3D.DrawModel(model_handle);
		screen.Unbind();
		
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		this.SetupCameraForMainScreen();
		Model3D.DrawModel(cube_handle);
	}
	private void SetupCameraForOffscreen() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.5f));
		offscreen_camera_pos=VectorFunctions.VTransform(offscreen_camera_pos, rot_y);
		
		CameraFront.UpdateAspect(SCREEN_WIDTH, SCREEN_HEIGHT);
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				offscreen_camera_pos,
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		CameraFront.Update();
	}
	private void SetupCameraForMainScreen() {
		CameraFront.UpdateAspect(this.GetWidth(), this.GetHeight());
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(10.0f, 10.0f, 10.0f),
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		CameraFront.Update();
	}
}
