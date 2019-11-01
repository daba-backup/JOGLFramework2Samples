package com.daxie.testspace.joglf.v2.drawer;

import java.util.Random;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicSegmentsDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class DrawSegmentsWindow extends JOGLFWindow{
	private Vector camera_position;
	private DynamicSegmentsDrawer drawer;
	
	@Override
	protected void Init() {
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		
		drawer=new DynamicSegmentsDrawer();
		
		Random random=new Random();
		final float SCALE=30.0f;
		
		for(int i=0;i<1000;i++) {
			Vertex3D v1=new Vertex3D();
			Vertex3D v2=new Vertex3D();
			
			float x=(float)random.nextGaussian()*SCALE;
			float y=(float)random.nextGaussian()*SCALE;
			float z=(float)random.nextGaussian()*SCALE;
			
			float r=random.nextFloat();
			float g=random.nextFloat();
			float b=random.nextFloat();
			float a=random.nextFloat();
			
			v1.SetPos(VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
			v1.SetDif(ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 0.0f));
			v2.SetPos(VectorFunctions.VGet(x, y, z));
			v2.SetDif(ColorU8Functions.GetColorU8(r, g, b, a));
			
			drawer.AddSegment(i, v1, v2);
		}
		
		drawer.UpdateBuffers();
	}
	
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(1.0f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		drawer.Draw();
	}
}
