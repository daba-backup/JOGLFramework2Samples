package com.daxie.testspace.joglf.g2.first_person;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.ConvexShape;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
import com.bulletphysics.linearmath.Transform;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.window.WindowCommonInfoStock;
import com.daxie.tool.MathFunctions;

class KinematicCamera extends KinematicCharacterController{
	private float camera_h_rot;
	private float camera_v_rot;
	
	private float rotate_speed;
	private float move_speed;
	
	private static final float VROT_MIN_ANGLE=MathFunctions.DegToRad(-80.0f);
	private static final float VROT_MAX_ANGLE=MathFunctions.DegToRad(80.0f);
	
	public KinematicCamera(PairCachingGhostObject ghost_object, ConvexShape convex_shape, float step_height) {
		super(ghost_object, convex_shape, step_height);
		
		Transform transform=new Transform();
		ghost_object.getWorldTransform(transform);
		
		Vector camera_position=VectorFunctions.VGet(transform.origin.x, transform.origin.y, transform.origin.z);
		Vector initial_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		Vector direction=VectorFunctions.VSub(initial_target,camera_position);
		direction=VectorFunctions.VNorm(direction);
		camera_h_rot=VectorFunctions.VAngleH(direction);
		camera_v_rot=VectorFunctions.VAngleV(direction);
		
		rotate_speed=0.02f;
		move_speed=0.5f/WindowCommonInfoStock.GetFPS();
	}
	
	public void Translate(int diff_x,int diff_y,
			int forward_key_count,int backward_key_count,
			int right_key_count,int left_key_count,int jump_key_count) {
		camera_h_rot+=(-diff_x)*rotate_speed;
		camera_v_rot+=(-diff_y)*rotate_speed;
		if(camera_h_rot>Math.PI)camera_h_rot-=2.0f*Math.PI;
		if(camera_h_rot<-Math.PI)camera_h_rot+=2.0f*Math.PI;
		if(camera_v_rot<VROT_MIN_ANGLE)camera_v_rot=VROT_MIN_ANGLE;
		if(camera_v_rot>VROT_MAX_ANGLE)camera_v_rot=VROT_MAX_ANGLE;
		
		Vector front=VectorFunctions.VGetFromAngles(camera_v_rot, camera_h_rot);
		Vector right=VectorFunctions.VCross(front, VectorFunctions.VGet(0.0f, 1.0f, 0.0f));
		
		Vector translate=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		
		if(forward_key_count>0) {
			translate=VectorFunctions.VAdd(translate, front);
		}
		if(backward_key_count>0) {
			translate=VectorFunctions.VAdd(translate, VectorFunctions.VScale(front, -1.0f));
		}
		if(right_key_count>0) {
			translate=VectorFunctions.VAdd(translate, right);
		}
		if(left_key_count>0) {
			translate=VectorFunctions.VAdd(translate, VectorFunctions.VScale(right, -1.0f));
		}
		
		if(VectorFunctions.VSize(translate)>1.0E-6f) {
			translate=VectorFunctions.VNorm(translate);
			translate=VectorFunctions.VScale(translate, move_speed);
			this.setWalkDirection(new Vector3f(translate.GetX(),translate.GetY(),translate.GetZ()));
		}
		else {
			this.setWalkDirection(new Vector3f(0.0f,0.0f,0.0f));
		}
		
		if(jump_key_count>0) {
			this.jump();
		}
	}
	
	public void Update() {
		Transform transform=new Transform();
		this.ghostObject.getWorldTransform(transform);
		
		Vector camera_position=VectorFunctions.VGet(transform.origin.x, transform.origin.y+0.8f, transform.origin.z);
		
		CameraFront.SetCameraPositionAndAngle(camera_position, camera_v_rot, camera_h_rot, 0.0f);
	}
}
