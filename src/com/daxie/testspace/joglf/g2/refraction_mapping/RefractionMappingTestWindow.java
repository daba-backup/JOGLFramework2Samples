package com.daxie.testspace.joglf.g2.refraction_mapping;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.texture.TextureMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class RefractionMappingTestWindow extends JOGLFWindow{
	private int refraction_mapping_texture_id;
	private int model_handle;
	private int skybox_model_handle;
	
	private ShaderProgram program;
	
	private Vector camera_position;
	
	@Override
	protected void Init() {
		this.GenerateCubemap();
		this.SetupProgram();
		this.SetupModel();
		
		camera_position=VectorFunctions.VGet(-40.0f, 40.0f, -40.0f);
		
		GLWrapper.glDisable(GL4.GL_CULL_FACE);
	}
	private void GenerateCubemap() {
		int[] texture_handles=new int[6];
		texture_handles[0]=TextureMgr.LoadTexture("./Data/Texture/Skybox/px.png");
		texture_handles[1]=TextureMgr.LoadTexture("./Data/Texture/Skybox/py.png");
		texture_handles[2]=TextureMgr.LoadTexture("./Data/Texture/Skybox/pz.png");
		texture_handles[3]=TextureMgr.LoadTexture("./Data/Texture/Skybox/nx.png");
		texture_handles[4]=TextureMgr.LoadTexture("./Data/Texture/Skybox/ny.png");
		texture_handles[5]=TextureMgr.LoadTexture("./Data/Texture/Skybox/nz.png");
		
		int[] targets=new int[] {
				GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
				GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
				GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
				GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
				GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
				GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
		};
		
		ByteBuffer[] data_buffers=new ByteBuffer[6];
		for(int i=0;i<6;i++) {
			data_buffers[i]=TextureMgr.GetTextureImage(texture_handles[i]);
		}
		
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenTextures(1, texture_ids);
		refraction_mapping_texture_id=texture_ids.get(0);
		
		final int TEXTURE_WIDTH=TextureMgr.GetTextureWidth(texture_handles[0]);
		final int TEXTURE_HEIGHT=TextureMgr.GetTextureHeight(texture_handles[0]);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, refraction_mapping_texture_id);
		for(int i=0;i<6;i++) {
			GLWrapper.glTexImage2D(
					targets[i], 0, GL4.GL_RGBA, TEXTURE_WIDTH, TEXTURE_HEIGHT, 
					0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data_buffers[i]);
		}
		
		GLWrapper.glGenerateMipmap(GL4.GL_TEXTURE_CUBE_MAP);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_CUBE_MAP, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, 0);
	}
	private void SetupProgram() {
		GLShaderFunctions.CreateProgram(
				"refraction_mapping", 
				"./Data/Shader/330/refraction_mapping/vshader.glsl", 
				"./Data/Shader/330/refraction_mapping/fshader.glsl");
		program=new ShaderProgram("refraction_mapping");
		program.Enable();
		program.SetUniform("apply_texture", 1);
		program.SetUniform("eta", 1.0f/1.33f);
		
		CameraFront.AddProgram("refraction_mapping");
		
		GLShaderFunctions.CreateProgram(
				"simple_3d", 
				"./Data/Shader/330/simple_3d/vshader.glsl", 
				"./Data/Shader/330/simple_3d/fshader.glsl");
		CameraFront.AddProgram("simple_3d");
	}
	private void SetupModel() {
		model_handle=Model3D.LoadModel("./Data/Model/OBJ/Teapot/teapot.obj");
		Model3D.TranslateModel(model_handle, VectorFunctions.VGet(0.0f, -10.0f, 0.0f));
	
		skybox_model_handle=Model3D.LoadModel("./Data/Model/OBJ/Skybox/skybox.obj");
		Model3D.RemoveAllPrograms(skybox_model_handle);
		Model3D.AddProgram(skybox_model_handle, "simple_3d");
	}
	
	@Override
	protected void Update() {
		//Matrix rot_y=MatrixFunctions.MGetRotY(MathFunctions.DegToRad(0.3f));
		//camera_position=VectorFunctions.VTransform(camera_position, rot_y);
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				camera_position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE1);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_CUBE_MAP, refraction_mapping_texture_id);
		program.SetUniform("cube_texture", 1);
		Model3D.DrawModelWithProgram(model_handle, "refraction_mapping", 0, "texture_sampler");
		
		Model3D.DrawModel(skybox_model_handle);
	}
}
