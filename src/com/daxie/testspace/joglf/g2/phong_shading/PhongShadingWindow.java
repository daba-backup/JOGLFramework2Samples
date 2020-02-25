package com.daxie.testspace.joglf.g2.phong_shading;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

class PhongShadingWindow extends JOGLFWindow{
	private int model_handle;
	
	private Vector camera_position;
	private Vector light_position;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		camera_position=VectorFunctions.VGet(40.0f, 40.0f, 40.0f);
		light_position=VectorFunctions.VGet(100.0f, 100.0f, 100.0f);
		
		GLShaderFunctions.CreateProgram(
				"phong", 
				"./Data/Shader/330/texture/phong/vshader.glsl",
				"./Data/Shader/330/texture/phong/fshader.glsl");
		
		CameraFront.AddProgram("phong");
		LightingFront.AddProgram("phong");
		FogFront.AddProgram("phong");
		
		Model3D.RemoveAllPrograms(model_handle);
		Model3D.AddProgram(model_handle, "phong");
	}
	
	@Override
	protected void Update() {
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_LEFT)>0) {
			Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(1.0f));
			camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_RIGHT)>0) {
			Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(-1.0f));
			camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		}
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		LightingFront.SetLightDirection(light_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
