package com.daxie.testspace.joglf.v2.drawer;

import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicQuadranglesDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.shape.Quadrangle;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.texture.TextureMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class DrawQuadranglesWindow extends JOGLFWindow{
	private Vector camera_position;
	
	private int texture_handle;
	private DynamicQuadranglesDrawer drawer;
	
	@Override
	protected void Init() {
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		
		texture_handle=TextureMgr.LoadTexture("./Data/Texture/wood.jpg");
		drawer=new DynamicQuadranglesDrawer();
		drawer.SetTextureHandle(texture_handle);
		
		for(int i=0;i<10;i++) {
			Quadrangle quadrangle=new Quadrangle();
			
			Vertex3D[] vertices=new Vertex3D[4];
			for(int j=0;j<4;j++)vertices[j]=new Vertex3D();
			
			Vector v1=VectorFunctions.VGet(-30.0f+i*2.0f, i*2.0f, -30.0f);
			Vector v2=VectorFunctions.VGet(-30.0f+i*2.0f, i*2.0f, 30.0f);
			Vector v3=VectorFunctions.VGet(30.0f+i*2.0f, i*2.0f, 30.0f);
			Vector v4=VectorFunctions.VGet(30.0f+i*2.0f, i*2.0f, -30.0f);
			vertices[0].SetPos(v1);
			vertices[1].SetPos(v2);
			vertices[2].SetPos(v3);
			vertices[3].SetPos(v4);
			
			Vector edge1=VectorFunctions.VSub(v2, v1);
			Vector edge2=VectorFunctions.VSub(v4, v1);
			Vector face_normal=VectorFunctions.VCross(edge1, edge2);
			face_normal=VectorFunctions.VNorm(face_normal);
			for(int j=0;j<4;j++)vertices[j].SetNorm(face_normal);
			
			 vertices[0].SetU(0.0f);
			 vertices[0].SetV(0.0f);
			 vertices[1].SetU(0.0f);
			 vertices[1].SetV(1.0f);
			 vertices[2].SetU(1.0f);
			 vertices[2].SetV(1.0f);
			 vertices[3].SetU(1.0f);
			 vertices[3].SetV(0.0f);
			 
			 for(int j=0;j<4;j++)quadrangle.SetVertex(j, vertices[j]);
			 
			 drawer.AddQuadrangle(i, quadrangle);
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
