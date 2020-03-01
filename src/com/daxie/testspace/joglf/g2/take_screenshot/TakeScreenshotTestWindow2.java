package com.daxie.testspace.joglf.g2.take_screenshot;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.util.screen.Screen;
import com.daxie.joglf.gl.window.JOGLFWindow;

class TakeScreenshotTestWindow2 extends JOGLFWindow{
	private int model_handle;
	private Screen screen;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/XOPS/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		screen=new Screen(1024, 1024);
	}
	
	@Override
	protected void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(50.0f, 50.0f, 50.0f), VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		screen.Bind();
		screen.Clear();
		screen.Fit();
		Model3D.DrawModel(model_handle);
		GLDrawFunctions3D.DrawAxes(100.0f);
		screen.Unbind();
		
		screen.TakeScreenshot("./Data/Screenshot/temp.bmp");
		this.CloseWindow();
	}
}
