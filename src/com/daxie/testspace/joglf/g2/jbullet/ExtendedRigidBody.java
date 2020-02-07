package com.daxie.testspace.joglf.g2.jbullet;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import com.daxie.basis.matrix.Matrix;
import com.daxie.basis.matrix.MatrixFunctions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.model.Model3D;

public class ExtendedRigidBody extends RigidBody{
	private int model_handle;
	
	private Vector prev_origin;
	private Matrix prev_rot;
	
	public ExtendedRigidBody(RigidBodyConstructionInfo ci,int model_handle) {
		super(ci);
		
		this.model_handle=model_handle;
		
		Transform transform=new Transform();
		this.getMotionState().getWorldTransform(transform);
		
		prev_origin=VectorFunctions.VGet(transform.origin.x, transform.origin.y, transform.origin.z);
		prev_rot=MatrixFunctions.MGetIdent();
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				prev_rot.SetValue(i, j, transform.basis.getElement(i, j));
			}
		}
		
		//Move and rotate the model to fit the initial state.
		Matrix translate_mat=MatrixFunctions.MGetTranslate(prev_origin);
		Matrix mat=MatrixFunctions.MMult(translate_mat, prev_rot);
		Model3D.SetModelMatrix(model_handle, mat);
	}
	
	public void Update() {
		Transform transform=new Transform();
		this.getMotionState().getWorldTransform(transform);
		
		//Get the translation matrix.
		Vector current_origin=VectorFunctions.VGet(transform.origin.x, transform.origin.y, transform.origin.z);
		Vector translate=VectorFunctions.VSub(current_origin, prev_origin);
		prev_origin=current_origin;
		Matrix translate_mat=MatrixFunctions.MGetTranslate(translate);
		
		//Get the rotation matrix.
		Matrix current_rot=MatrixFunctions.MGetIdent();
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				current_rot.SetValue(i, j, transform.basis.getElement(i, j));
			}
		}
		
		Matrix diff_rot=MatrixFunctions.MMult(current_rot, MatrixFunctions.MInverse(prev_rot));
		prev_rot=current_rot;
		
		Matrix to_world_origin_mat=MatrixFunctions.MGetTranslate(VectorFunctions.VScale(current_origin, -1.0f));
		Matrix to_local_origin_mat=MatrixFunctions.MGetTranslate(current_origin);
		
		Matrix mat=MatrixFunctions.MMult(to_world_origin_mat, translate_mat);
		mat=MatrixFunctions.MMult(diff_rot, mat);
		mat=MatrixFunctions.MMult(to_local_origin_mat, mat);
		
		Model3D.SetModelMatrix(model_handle, mat);
	}
	
	public void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
