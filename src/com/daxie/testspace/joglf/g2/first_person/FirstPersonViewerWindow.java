package com.daxie.testspace.joglf.g2.first_person;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexShape;
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
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.input.mouse.MouseEnum;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shape.Triangle;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.tool.MathFunctions;

class FirstPersonViewerWindow extends JOGLFWindow{
	private float seconds_per_frame;
	
	private DiscreteDynamicsWorld dynamics_world;
	private KinematicCamera camera;
	
	private int model_handle;
	
	public FirstPersonViewerWindow(int fps) {
		seconds_per_frame=1.0f/fps;
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
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		List<Triangle> faces=Model3D.GetModelFaces(model_handle);
		
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
			rb.setRestitution(0.0f);
			
			dynamics_world.addRigidBody(rb);
		}
		
		//Set up the camera.
		ConvexShape camera_shape=new CapsuleShape(0.3f, 1.4f);
		PairCachingGhostObject ghost_object=new PairCachingGhostObject();
		ghost_object.setWorldTransform(
				new Transform(new Matrix4f(new Quat4f(0.0f,0.0f,0.0f,1.0f),new Vector3f(-10.0f,50.0f,-10.0f),1.0f)));
		dynamics_world.getPairCache().setInternalGhostPairCallback(new GhostPairCallback());
		ghost_object.setCollisionShape(camera_shape);
		camera=new KinematicCamera(ghost_object, camera_shape, 0.3f);
		dynamics_world.addCollisionObject(ghost_object);
		dynamics_world.addAction(camera);
		
		camera.setGravity(0.3f);
		camera.setJumpSpeed(1.0f);
		camera.setMaxSlope(MathFunctions.DegToRad(50.0f));
		
		CameraFront.SetCameraNearFar(0.3f, 100.0f);
		FogFront.SetFogStartEnd(50.0f, 100.0f);
	}
	
	@Override
	protected void Update() {
		if(this.GetMousePressingCount(MouseEnum.MOUSE_MIDDLE)>=2) {
			this.SetFixMousePointerFlag(true);
			this.HideCursor();
		}
		else {
			this.SetFixMousePointerFlag(false);
			this.ShowCursor();
		}
		
		int diff_x=this.GetMouseDiffX();//0 when the pointer is not fixed.
		int diff_y=this.GetMouseDiffY();//0 when the pointer is not fixed.
		int forward_key_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_W);
		int backward_key_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_S);
		int right_key_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_D);
		int left_key_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_A);
		int jump_key_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_SPACE);
		
		camera.Translate(diff_x, diff_y, forward_key_count, backward_key_count, right_key_count, left_key_count, jump_key_count);
		
		//Perform simulation.
		dynamics_world.stepSimulation(1.0f,10,seconds_per_frame);
		
		camera.Update();
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
