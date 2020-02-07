package com.daxie.testspace.joglf.g2.model;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class ModelOperationsTestWindow extends JOGLFWindow{
	private int[] model_handles;
	
	@Override
	protected void Init() {
		model_handles=new int[2];
		
		model_handles[0]=Model3D.LoadModel("./Data/Model/BD1/Cube/cube.bd1");
		Model3D.RescaleModel(model_handles[0], VectorFunctions.VGet(0.1f, 0.1f, 0.1f));
		model_handles[1]=Model3D.DuplicateModel(model_handles[0]);
		
		Model3D.TranslateModel(model_handles[0],VectorFunctions.VGet(-10.0f, 0.0f, 0.0f));
		Model3D.TranslateModel(model_handles[1], VectorFunctions.VGet(10.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Update() {
		float rad=MathFunctions.DegToRad(1.0f);
		
		Model3D.RotateModel(model_handles[0], VectorFunctions.VGet(rad, rad, rad));
		Model3D.RotateModelLocally(
				model_handles[1], 
				VectorFunctions.VGet(10.0f, 0.0f, 0.0f),
				VectorFunctions.VGet(rad, rad, rad));
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(25.0f, 25.0f, 25.0f), 
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handles[0]);
		Model3D.DrawModel(model_handles[1]);
		
		GLDrawFunctions3D.DrawAxes(100.0f);
	}
}
