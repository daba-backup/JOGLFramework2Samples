package com.daxie.testspace.joglf.v2.model;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class DrawModelWindow extends JOGLFWindow{
	public DrawModelWindow() {
		
	}
	
	private int model_handle;
	private Vector camera_position;
	
	@Override
	protected void Init() {
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.SetModelScale(model_handle, VectorFunctions.VGet(0.1f, 0.1f, 0.1f));
		
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
	}
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(1.0f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
