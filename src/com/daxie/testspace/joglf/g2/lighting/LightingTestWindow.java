package com.daxie.testspace.joglf.g2.lighting;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class LightingTestWindow extends JOGLFWindow{
	private Vector light_position;
	private Vector camera_position;
	
	private int model_handle;
	
	@Override
	protected void Init() {
		light_position=VectorFunctions.VGet(30.0f, 30.0f, 30.0f);
		camera_position=VectorFunctions.VGet(40.0f, 40.0f, 40.0f);
		
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		LightingFront.SetAmbientColor(ColorU8Functions.GetColorU8(0.0f, 0.0f, 0.0f, 1.0f));
		LightingFront.SetDiffusePower(1.0f);
		LightingFront.SetSpecularPower(0.0f);
	}
	
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(1.0f));
		
		light_position=VectorFunctions.VTransform(light_position, rot_y);
		//camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		LightingFront.SetLightDirection(light_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		GLDrawFunctions3D.DrawSphere3D(light_position, 5.0f, 16, 16, ColorU8Functions.GetColorU8(1.0f, 0.0f, 0.0f, 1.0f));
		
		Model3D.DrawModel(model_handle);
	}
}
