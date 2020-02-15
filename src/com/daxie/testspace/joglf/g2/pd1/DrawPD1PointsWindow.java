package com.daxie.testspace.joglf.g2.pd1;

import java.io.IOException;
import java.util.List;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicSegmentsDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;
import com.daxie.xops.pd1.PD1Manipulator;
import com.daxie.xops.pd1.PD1Point;

public class DrawPD1PointsWindow extends JOGLFWindow{
	private int model_handle;
	private DynamicSegmentsDrawer drawer;
	
	private Vector camera_position;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		drawer=new DynamicSegmentsDrawer();
		
		camera_position=VectorFunctions.VGet(40.0f, 40.0f, 40.0f);
		
		PD1Manipulator pd1_manipulator;
		try {
			pd1_manipulator=new PD1Manipulator("./Data/Model/BD1/map2/ext.pd1");
		}
		catch(IOException e) {
			e.printStackTrace();
			this.CloseWindow();
			
			return;
		}
		pd1_manipulator.InvertZ();
		pd1_manipulator.Rescale(VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		List<PD1Point> points=pd1_manipulator.GetPoints();
		
		final float SEGMENT_HALF_LENGTH=1.5f;
		int segment_count=0;
		for(PD1Point point:points) {
			Vector pos=point.GetPosition();
			
			Vector segment_x_pos_1=VectorFunctions.VAdd(pos, VectorFunctions.VGet(-SEGMENT_HALF_LENGTH, 0.0f, 0.0f));
			Vector segment_x_pos_2=VectorFunctions.VAdd(pos, VectorFunctions.VGet(SEGMENT_HALF_LENGTH, 0.0f, 0.0f));
			Vector segment_y_pos_1=VectorFunctions.VAdd(pos, VectorFunctions.VGet(0.0f, -SEGMENT_HALF_LENGTH, 0.0f));
			Vector segment_y_pos_2=VectorFunctions.VAdd(pos, VectorFunctions.VGet(0.0f, SEGMENT_HALF_LENGTH, 0.0f));
			Vector segment_z_pos_1=VectorFunctions.VAdd(pos, VectorFunctions.VGet(0.0f, 0.0f, -SEGMENT_HALF_LENGTH));
			Vector segment_z_pos_2=VectorFunctions.VAdd(pos, VectorFunctions.VGet(0.0f, 0.0f, SEGMENT_HALF_LENGTH));
			
			Vertex3D[] vertices=new Vertex3D[2];
			
			vertices[0]=new Vertex3D();
			vertices[1]=new Vertex3D();
			vertices[0].SetPos(segment_x_pos_1);
			vertices[1].SetPos(segment_x_pos_2);
			vertices[0].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			vertices[1].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			drawer.AddSegment(segment_count, vertices[0], vertices[1]);
			segment_count++;
			
			vertices[0]=new Vertex3D();
			vertices[1]=new Vertex3D();
			vertices[0].SetPos(segment_y_pos_1);
			vertices[1].SetPos(segment_y_pos_2);
			vertices[0].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			vertices[1].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			drawer.AddSegment(segment_count, vertices[0], vertices[1]);
			segment_count++;
			
			vertices[0]=new Vertex3D();
			vertices[1]=new Vertex3D();
			vertices[0].SetPos(segment_z_pos_1);
			vertices[1].SetPos(segment_z_pos_2);
			vertices[0].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			vertices[1].SetDif(ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f));
			drawer.AddSegment(segment_count, vertices[0], vertices[1]);
			segment_count++;
		}
		
		drawer.UpdateBuffers();
	}
	
	@Override
	protected void Update() {
		Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.3f));
		camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
		drawer.Draw();
	}
}
