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
import com.bulletphysics.collision.shapes.TriangleShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shape.Triangle;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.window.JOGLFWindow;

//Simulation of a 3D model and boxes.
public class JBulletTestWindow3 extends JOGLFWindow{
	private float seconds_per_frame;
	
	private DiscreteDynamicsWorld dynamics_world;
	private List<ExtendedRigidBody> fall_rigidbodies;
	
	private Random random;
	
	private int container_model_handle;
	private int fall_base_model_handle;
	
	public JBulletTestWindow3(int fps) {
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
		
		//Set up the map.
		final float MODEL_SCALE=1.7f/20.0f;
		container_model_handle=Model3D.LoadModel("./Data/Model/BD1/Container/container.bd1");
		Model3D.RescaleModel(container_model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		List<Triangle> faces=Model3D.GetModelFaces(container_model_handle);
		
		for(Triangle face:faces) {
			Vertex3D[] vertices=face.GetVertices();
			
			Vector3f[] vertex_positions=new Vector3f[3];
			for(int i=0;i<3;i++) {
				Vector vtemp=vertices[i].GetPos();
				vertex_positions[i]=new Vector3f(vtemp.GetX(),vtemp.GetY(),vtemp.GetZ());
			}
			
			CollisionShape collision_shape=new TriangleShape(vertex_positions[0], vertex_positions[1], vertex_positions[2]);
			DefaultMotionState motion_state=new DefaultMotionState(
					new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(0.0f,0.0f,0.0f),1.0f)));
			RigidBodyConstructionInfo ci=new RigidBodyConstructionInfo(0.0f, motion_state, collision_shape, new Vector3f(0.0f,0.0f,0.0f));
			
			RigidBody rb=new RigidBody(ci);
			rb.setRestitution(0.3f);
			
			dynamics_world.addRigidBody(rb);
		}
		
		//Load a model for boxes.
		fall_base_model_handle=Model3D.LoadModel("./Data/Model/BD1/Cube/cube.bd1");
		Model3D.RescaleModel(fall_base_model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
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
		Model3D.DrawModel(container_model_handle);
		
		for(ExtendedRigidBody fall_rigidbody:fall_rigidbodies) {
			fall_rigidbody.Draw();
		}
	}
}
