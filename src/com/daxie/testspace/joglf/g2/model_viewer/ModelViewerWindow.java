package com.daxie.testspace.joglf.g2.model_viewer;

import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

public class ModelViewerWindow extends JOGLFWindow{
	private int model_handle;
	
	private Vector camera_position;
	private float camera_h_rot;
	private float camera_v_rot;
	
	private static final float ROTATE_SPEED=0.02f;
	private static final float MOVE_SPEED=0.5f;
	
	private static final float VROT_MIN_ANGLE=MathFunctions.DegToRad(-80.0f);
	private static final float VROT_MAX_ANGLE=MathFunctions.DegToRad(80.0f);
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		camera_position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		
		Vector initial_target=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		Vector direction=VectorFunctions.VSub(initial_target, camera_position);
		direction=VectorFunctions.VNorm(direction);
		
		camera_h_rot=VectorFunctions.VAngleH(direction);
		camera_v_rot=VectorFunctions.VAngleV(direction);
		
		CameraFront.SetCameraPositionAndAngle(camera_position, camera_v_rot, camera_h_rot, 0.0f);
	}
	
	@Override
	protected void Update() {
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_LEFT)>0)camera_h_rot+=ROTATE_SPEED;
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_RIGHT)>0)camera_h_rot-=ROTATE_SPEED;
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_UP)>0)camera_v_rot+=ROTATE_SPEED;
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_DOWN)>0)camera_v_rot-=ROTATE_SPEED;
		if(camera_h_rot>Math.PI)camera_h_rot-=2.0f*Math.PI;
		if(camera_h_rot<-Math.PI)camera_h_rot+=2.0f*Math.PI;
		if(camera_v_rot<VROT_MIN_ANGLE)camera_v_rot=VROT_MIN_ANGLE;
		if(camera_v_rot>VROT_MAX_ANGLE)camera_v_rot=VROT_MAX_ANGLE;
		
		Vector front=VectorFunctions.VGetFromAngles(camera_v_rot, camera_h_rot);
		Vector right=VectorFunctions.VCross(front, VectorFunctions.VGet(0.0f, 1.0f, 0.0f));
		right=VectorFunctions.VNorm(right);
		
		Vector translate=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_W)>0) {
			translate=VectorFunctions.VAdd(translate, front);
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_S)>0) {
			translate=VectorFunctions.VAdd(translate, VectorFunctions.VScale(front, -1.0f));
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_D)>0) {
			translate=VectorFunctions.VAdd(translate, right);
		}
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_A)>0) {
			translate=VectorFunctions.VAdd(translate, VectorFunctions.VScale(right, -1.0f));
		}
		
		if(VectorFunctions.VSize(translate)>1.0E-6f) {
			translate=VectorFunctions.VNorm(translate);
			translate=VectorFunctions.VScale(translate, MOVE_SPEED);
			
			camera_position=VectorFunctions.VAdd(camera_position, translate);
		}
		
		CameraFront.SetCameraPositionAndAngle(camera_position, camera_v_rot, camera_h_rot, 0.0f);
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
