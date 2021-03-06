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
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.drawer.DynamicCapsulesDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.shape.Capsule;
import com.daxie.joglf.gl.window.JOGLFWindow;

class JBulletTestWindow4 extends JOGLFWindow{
	private float seconds_per_frame;
	
	private DiscreteDynamicsWorld dynamics_world;
	private RigidBody ground_rigidbody;
	private List<RigidBody> fall_rigidbodies;
	
	private Random random;
	
	private DynamicCapsulesDrawer capsule_drawer;
	private int capsule_count;
	
	public JBulletTestWindow4(int fps) {
		seconds_per_frame=1.0f/fps;
		
		fall_rigidbodies=new ArrayList<>();
		random=new Random();
		capsule_count=0;
	}
	
	@Override
	protected void Init() {
		//Set up the dynamics world.
		BroadphaseInterface broadphase=new DbvtBroadphase();
		DefaultCollisionConfiguration collision_configuration=new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher=new CollisionDispatcher(collision_configuration);
		
		SequentialImpulseConstraintSolver solver=new SequentialImpulseConstraintSolver();
		
		dynamics_world=new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collision_configuration);
		dynamics_world.setGravity(new Vector3f(0.0f,-9.81f,0.0f));
		
		//Set up the ground.
		CollisionShape ground_shape=new StaticPlaneShape(new Vector3f(0.0f,1.0f,0.0f),0.0f);
		DefaultMotionState ground_motion_state=new DefaultMotionState(
				new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(0.0f,0.0f,0.0f),1.0f)));
		RigidBodyConstructionInfo ground_rigidbody_ci=
				new RigidBodyConstructionInfo(0.0f, ground_motion_state, ground_shape, new Vector3f(0.0f,0.0f,0.0f));
		ground_rigidbody=new RigidBody(ground_rigidbody_ci);
		ground_rigidbody.setRestitution(0.5f);
		dynamics_world.addRigidBody(ground_rigidbody);
		
		//Set up the drawer.
		capsule_drawer=new DynamicCapsulesDrawer();
	}
	
	@Override
	protected void Update() {
		final float CAPSULE_RADIUS=2.0f;
		final float CAPSULE_HEIGHT=4.0f;
		
		//Generate a capsule.
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_ENTER)==1) {
			//Slightly change the initial location.
			final float OFFSET_SCALE=1.0f;
			float x_offset=random.nextFloat()*OFFSET_SCALE;
			float z_offset=random.nextFloat()*OFFSET_SCALE;
			if(random.nextInt()%2==1)x_offset*=(-1.0f);
			if(random.nextInt()%2==1)z_offset*=(-1.0f);
			
			final float INITIAL_HEIGHT=20.0f;
			final float MASS=1.0f;
			
			CollisionShape fall_shape=new CapsuleShape(CAPSULE_RADIUS, CAPSULE_HEIGHT);
			DefaultMotionState fall_motion_state=new DefaultMotionState(
					new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(x_offset,INITIAL_HEIGHT,z_offset),1.0f)));
			Vector3f fall_inertia=new Vector3f(0.0f,0.0f,0.0f);
			//fall_shape.calculateLocalInertia(MASS, fall_inertia);
			RigidBodyConstructionInfo fall_rigidbody_ci=
					new RigidBodyConstructionInfo(MASS, fall_motion_state, fall_shape, fall_inertia);
			RigidBody fall_rigidbody=new RigidBody(fall_rigidbody_ci);
			fall_rigidbody.setRestitution(0.5f);
			dynamics_world.addRigidBody(fall_rigidbody);
			
			fall_rigidbodies.add(fall_rigidbody);
			
			capsule_count++;
		}
		
		//Perform simulation.
		dynamics_world.stepSimulation(1.0f,10,seconds_per_frame);
		
		//Add capsules to the drawer.
		for(int i=0;i<capsule_count;i++) {
			RigidBody fall_rigidbody=fall_rigidbodies.get(i);
			
			Transform transform=new Transform();
			fall_rigidbody.getMotionState().getWorldTransform(transform);
			
			Vector center=VectorFunctions.VGet(transform.origin.x, transform.origin.y, transform.origin.z);
			Vector capsule_pos_1=VectorFunctions.VAdd(center, VectorFunctions.VGet(0.0f, -CAPSULE_HEIGHT/2.0f, 0.0f));
			Vector capsule_pos_2=VectorFunctions.VAdd(center, VectorFunctions.VGet(0.0f, CAPSULE_HEIGHT/2.0f, 0.0f));
			
			Capsule capsule=new Capsule();
			capsule.SetCapsulePos1(capsule_pos_1);
			capsule.SetCapsulePos2(capsule_pos_2);
			capsule.SetRadius(CAPSULE_RADIUS);
			capsule.SetSliceNum(8);
			capsule.SetStackNum(8);
			capsule.SetColor(ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
			
			capsule_drawer.AddCapsule(i, capsule);
		}
		capsule_drawer.UpdateBuffers();
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(20.0f, 20.0f, 20.0f), VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		//Draw the ground.
		GLDrawFunctions3D.DrawQuadrangle3D(
				VectorFunctions.VGet(-50.0f, 0.0f, -50.0f), VectorFunctions.VGet(-50.0f, 0.0f, 50.0f), 
				VectorFunctions.VGet(50.0f, 0.0f, 50.0f), VectorFunctions.VGet(50.0f, 0.0f, -50.0f), 
				ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
		
		//Draw capsules.
		capsule_drawer.Draw();
	}
}
