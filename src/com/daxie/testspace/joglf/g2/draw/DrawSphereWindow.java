package com.daxie.testspace.joglf.g2.draw;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

class DrawSphereWindow extends JOGLFWindow{
	public DrawSphereWindow() {
		
	}
	
	private Vector camera_position;
	
	@Override
	protected void Init() {
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
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 15.0f, 16, 16, ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
	}
}
