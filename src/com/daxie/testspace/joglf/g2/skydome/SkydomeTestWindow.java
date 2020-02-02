package com.daxie.testspace.joglf.g2.skydome;

import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class SkydomeTestWindow extends JOGLFWindow{
	private int model_handle;
	private int skydome_handle;
	
	@Override
	protected void Init() {
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map0/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(0.1f, 0.1f, 0.1f));
		
		skydome_handle=Model3D.LoadModel("./Data/Model/OBJ/Sky/lsky.obj");
		
		GLShaderFunctions.CreateProgram(
				"simple_3dshader", 
				"./Data/Shader/330/simple_3dshader/vshader.glsl",
				"./Data/Shader/330/simple_3dshader/fshader.glsl");
		Model3D.RemoveAllPrograms(skydome_handle);
		Model3D.AddProgram(skydome_handle, "simple_3dshader");
		
		CameraFront.AddProgram("simple_3dshader");
	}
	
	@Override
	protected void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(20.0f, 5.0f, 20.0f), VectorFunctions.VGet(0.0f, 10.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
		Model3D.DrawModel(skydome_handle);
	}
}
