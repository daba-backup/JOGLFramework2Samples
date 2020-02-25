package com.daxie.testspace.joglf.g2.camera;

import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

class CameraTestWindow extends JOGLFWindow{
	private Vector camera_position;
	private float camera_h_rot;
	
	private int model_handle;
	
	@Override
	protected void Init() {
		camera_position=VectorFunctions.VGet(0.0f, 30.0f, 0.0f);
		camera_h_rot=0.0f;
		
		CameraFront.SetCameraNearFar(1.0f, 1000.0f);
		CameraFront.SetupCamera_Perspective(MathFunctions.DegToRad(60.0f));
		
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
	}
	
	@Override
	protected void Update() {
		CameraFront.SetCameraPositionAndAngle(camera_position, MathFunctions.DegToRad(-30.0f), camera_h_rot, 0.0f);
		
		camera_h_rot+=MathFunctions.DegToRad(0.5f);
		if(camera_h_rot>Math.PI)camera_h_rot-=2.0f*Math.PI;
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
