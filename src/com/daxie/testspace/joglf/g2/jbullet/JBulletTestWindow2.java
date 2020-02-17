package com.daxie.testspace.joglf.g2.jbullet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;

//Simulation of a static plane and boxes.
public class JBulletTestWindow2 extends JOGLFWindow{
	private float seconds_per_frame;
	
	private DiscreteDynamicsWorld dynamics_world;
	private RigidBody ground_rigidbody;
	private List<ExtendedRigidBody> fall_rigidbodies;
	
	private Random random;
	
	private int ground_model_handle;
	private int fall_base_model_handle;
	
	public JBulletTestWindow2(int fps) {
		seconds_per_frame=1.0f/fps;
		
		fall_rigidbodies=new ArrayList<>();
		random=new Random();
	}
	
	@Override
	protected void Init() {
		//Set up the dynamics world.
		BroadphaseInterface broadphase=new DbvtBroadphase();
		DefaultCollisionConfiguration collision_configuration=new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher=new CollisionDispatcher(collision_configuration);
		
		SequentialImpulseConstraintSolver solver=new SequentialImpulseConstraintSolver();
		
		dynamics_world=new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collision_configuration);
		dynamics_world.setGravity(new Vector3f(0.0f,-9.8f,0.0f));
		
		//Set up the ground.
		CollisionShape ground_shape=new StaticPlaneShape(new Vector3f(0.0f,1.0f,0.0f),0.0f);
		DefaultMotionState ground_motion_state=new DefaultMotionState(
				new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(0.0f,0.0f,0.0f),1.0f)));
		RigidBodyConstructionInfo ground_rigidbody_ci=
				new RigidBodyConstructionInfo(0.0f, ground_motion_state, ground_shape, new Vector3f(0.0f,0.0f,0.0f));
		ground_rigidbody=new RigidBody(ground_rigidbody_ci);
		ground_rigidbody.setRestitution(0.3f);
		dynamics_world.addRigidBody(ground_rigidbody);
		
		//Load a model for the ground.
		final float MODEL_SCALE=1.7f/20.0f;
		ground_model_handle=Model3D.LoadModel("./Data/Model/BD1/Ground/ground.bd1");
		Model3D.RescaleModel(ground_model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		//Load a model for boxes.
		fall_base_model_handle=Model3D.LoadModel("./Data/Model/BD1/Cube/cube.bd1");
		Model3D.RescaleModel(fall_base_model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		LightingFront.SetSpecularPower(0.0f);
		LightingFront.SetDiffusePower(0.0f);
		LightingFront.SetAmbientColor(ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
	}
	
	@Override
	protected void Update() {
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_ENTER)==1) {
			//Slightly change the initial location.
			final float OFFSET_SCALE=1.0f;
			float x_offset=random.nextFloat()*OFFSET_SCALE;
			float z_offset=random.nextFloat()*OFFSET_SCALE;
			if(random.nextInt()%2==1)x_offset*=(-1.0f);
			if(random.nextInt()%2==1)z_offset*=(-1.0f);
			
			final float INITIAL_HEIGHT=20.0f;
			final float MASS=1.0f;
			
			//Make a box with every edge 1.7m long.
			CollisionShape fall_shape=new BoxShape(new Vector3f(0.85f,0.85f,0.85f));
			DefaultMotionState fall_motion_state=new DefaultMotionState(
					new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(x_offset,INITIAL_HEIGHT,z_offset),1.0f)));
			Vector3f fall_inertia=new Vector3f(0.0f,0.0f,0.0f);
			fall_shape.calculateLocalInertia(MASS, fall_inertia);
			RigidBodyConstructionInfo fall_rigidbody_ci=
					new RigidBodyConstructionInfo(MASS, fall_motion_state, fall_shape, fall_inertia);
			
			int fall_model_handle=Model3D.DuplicateModel(fall_base_model_handle);
			
			ExtendedRigidBody fall_rigidbody=new ExtendedRigidBody(fall_rigidbody_ci, fall_model_handle);
			fall_rigidbody.setRestitution(0.3f);
			dynamics_world.addRigidBody(fall_rigidbody);
			
			fall_rigidbodies.add(fall_rigidbody);
		}
		
		//Perform simulation.
		dynamics_world.stepSimulation(1.0f,10,seconds_per_frame);
		
		for(ExtendedRigidBody fall_rigidbody:fall_rigidbodies) {
			fall_rigidbody.Update();
		}
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(20.0f, 20.0f, 20.0f), VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(ground_model_handle);
		
		for(ExtendedRigidBody fall_rigidbody:fall_rigidbodies) {
			fall_rigidbody.Draw();
		}
	}
}
