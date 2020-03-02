package com.daxie.testspace.joglf.g2.drawer;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicQuadranglesDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shape.Quadrangle;
import com.daxie.joglf.gl.util.screen.Screen;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.daxie.tool.MathFunctions;

class DrawQuadranglesWindow2 extends JOGLFWindow{
	private int model_handle;
	private Screen screen;
	
	private DynamicQuadranglesDrawer drawer;
	
	private Vector camera_position;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/XOPS/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		screen=new Screen(1024, 1024);
		int texture_handle=screen.Associate(false);
		
		drawer=new DynamicQuadranglesDrawer();
		drawer.SetTextureHandle(texture_handle);
		
		Quadrangle quad=new Quadrangle();
		quad.GetVertex(0).SetPos(VectorFunctions.VGet(-20.0f, 0.0f, 20.0f));
		quad.GetVertex(1).SetPos(VectorFunctions.VGet(20.0f, 0.0f, 20.0f));
		quad.GetVertex(2).SetPos(VectorFunctions.VGet(20.0f, 0.0f, -20.0f));
		quad.GetVertex(3).SetPos(VectorFunctions.VGet(-20.0f, 0.0f, -20.0f));
		quad.GetVertex(0).SetU(0.0f);
		quad.GetVertex(0).SetV(0.0f);
		quad.GetVertex(1).SetU(1.0f);
		quad.GetVertex(1).SetV(0.0f);
		quad.GetVertex(2).SetU(1.0f);
		quad.GetVertex(2).SetV(1.0f);
		quad.GetVertex(3).SetU(0.0f);
		quad.GetVertex(3).SetV(1.0f);
		for(int i=0;i<4;i++) {
			quad.GetVertex(i).SetNorm(VectorFunctions.VGet(0.0f, 1.0f, 0.0f));
		}
		drawer.AddQuadrangle(0, quad);
		drawer.UpdateBuffers();
		
		camera_position=VectorFunctions.VGet(30.0f, 30.0f, 30.0f);
	}
	
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.5f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
	}
	
	@Override
	protected void Draw() {
		screen.Bind();
		screen.Clear();
		screen.Fit();
		CameraFront.UpdateAspect(1024, 1024);
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(50.0f, 50.0f, 50.0f), 
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		CameraFront.Update();
		/*
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 10.0f, 
				16, 16, ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
		GLDrawFunctions3D.DrawAxes(100.0f);
		*/
		Model3D.DrawModel(model_handle);
		screen.Unbind();
		
		CameraFront.UpdateAspect(this.GetWidth(), this.GetHeight());
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				camera_position, 
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		CameraFront.Update();
		
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		drawer.Draw();
	}
}
