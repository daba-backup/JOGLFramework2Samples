package com.daxie.testspace.joglf.g2.model;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class DrawModelWindow extends JOGLFWindow{
	private int model_handle;
	
	@Override
	protected void Init() {
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
		Model3D.DrawModel(model_handle);
	}
}
