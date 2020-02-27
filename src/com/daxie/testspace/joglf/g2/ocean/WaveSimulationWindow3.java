package com.daxie.testspace.joglf.g2.ocean;

import java.nio.FloatBuffer;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicTrianglesDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.front.LightingFront;
import com.daxie.joglf.gl.shader.GLShaderFunctions;
import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.shape.Triangle;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class WaveSimulationWindow3 extends JOGLFWindow{
	private TildeHktComputation tilde_hkt_computation;
	private ButterflyComputation butterfly_computation;
	private InversionAndPermutation inv_and_perm;
	
	private static final int N=64;
	
	private DynamicTrianglesDrawer drawer;
	private ShaderProgram program;
	
	@Override
	protected void Init() {
		TildeH0kComputation tilde_h0k_computation=new TildeH0kComputation(N);
		tilde_hkt_computation=new TildeHktComputation(N);
		ButterflyTextureGeneration butterfly_texture_generation=new ButterflyTextureGeneration(N);
		butterfly_computation=new ButterflyComputation(N);
		inv_and_perm=new InversionAndPermutation(N);
		
		tilde_h0k_computation.Compute();
		
		tilde_hkt_computation.SetTildeH0k(tilde_h0k_computation.GetTildeH0k());
		tilde_hkt_computation.SetTildeH0minusk(tilde_h0k_computation.GetTildeH0minusk());
		
		butterfly_texture_generation.Compute();
		
		butterfly_computation.SetButterflyTexture(butterfly_texture_generation.GetOutColor());
		butterfly_computation.SetPingpongIn(tilde_hkt_computation.GetTildeHkt());
		
		inv_and_perm.SetInputTexture(butterfly_computation.GetComputationResult());
		
		drawer=new DynamicTrianglesDrawer();
		
		GLShaderFunctions.CreateProgram(
				"wave_drawer", 
				"./Data/Shader/330/ocean/wave_drawer/vshader.glsl",
				"./Data/Shader/330/ocean/wave_drawer/fshader.glsl");
		program=new ShaderProgram("wave_drawer");
		
		CameraFront.AddProgram("wave_drawer");
		FogFront.AddProgram("wave_drawer");
		LightingFront.AddProgram("wave_drawer");
		
		FogFront.SetFogStartEnd(100.0f, 1000.0f);
		
		LightingFront.SetAmbientColor(ColorU8Functions.GetColorU8(0.0f, 0.0f, 0.0f, 1.0f));
		LightingFront.SetDiffusePower(0.8f);
		LightingFront.SetSpecularPower(8.0f);
	}
	
	@Override
	protected void Update() {
		tilde_hkt_computation.Compute();
		tilde_hkt_computation.AdvanceTime(1.0f/30.0f);
		
		butterfly_computation.Compute();
		
		inv_and_perm.Compute();
		
		int heightmap_id=inv_and_perm.GetHeightmap();
		FloatBuffer heightmap=Buffers.newDirectFloatBuffer(N*N);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, heightmap_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RED, GL4.GL_FLOAT, heightmap);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		int size=N*N;
		Vector[] vertex_normals=new Vector[size];
		for(int i=0;i<size;i++) {
			vertex_normals[i]=VectorFunctions.VGet(0.0f, 0.0f, 0.0f);
		}
		
		int count=0;
		for(int z=0;z<N-1;z++) {
			for(int x=0;x<N-1;x++) {
				Triangle[] triangles=new Triangle[2];
				for(int k=0;k<2;k++)triangles[k]=new Triangle();
				
				Vector[] positions=new Vector[4];
				positions[0]=VectorFunctions.VGet(x*3.0f, heightmap.get(z*N+x)*0.2f, z*3.0f);
				positions[1]=VectorFunctions.VGet(x*3.0f, heightmap.get((z+1)*N+x)*0.2f, (z+1)*3.0f);
				positions[2]=VectorFunctions.VGet((x+1)*3.0f, heightmap.get((z+1)*N+(x+1))*0.2f, (z+1)*3.0f);
				positions[3]=VectorFunctions.VGet((x+1)*3.0f, heightmap.get(z*N+(x+1))*0.2f, z*3.0f);
				
				Vector edge1=VectorFunctions.VSub(positions[1], positions[0]);
				Vector edge2=VectorFunctions.VSub(positions[2], positions[0]);
				Vector edge3=VectorFunctions.VSub(positions[3], positions[2]);
				Vector edge4=VectorFunctions.VSub(positions[0], positions[2]);
				Vector n1=VectorFunctions.VCross(edge1, edge2);
				n1=VectorFunctions.VNorm(n1);
				Vector n2=VectorFunctions.VCross(edge3, edge4);
				n2=VectorFunctions.VNorm(n2);
				
				vertex_normals[z*N+x]=VectorFunctions.VAdd(vertex_normals[z*N+x], n1);
				vertex_normals[(z+1)*N+x]=VectorFunctions.VAdd(vertex_normals[(z+1)*N+x], n1);
				vertex_normals[(z+1)*N+(x+1)]=VectorFunctions.VAdd(vertex_normals[(z+1)*N+(x+1)], n1);
				vertex_normals[(z+1)*N+(x+1)]=VectorFunctions.VAdd(vertex_normals[(z+1)*N+(x+1)], n2);
				vertex_normals[z*N+(x+1)]=VectorFunctions.VAdd(vertex_normals[z*N+(x+1)], n2);
				vertex_normals[z*N+x]=VectorFunctions.VAdd(vertex_normals[z*N+x], n2);
				
				//First triangle
				for(int i=0;i<3;i++) {
					triangles[0].GetVertex(i).SetPos(positions[i]);
				}
				//Second triangle
				for(int i=0;i<3;i++) {
					triangles[1].GetVertex(i).SetPos(positions[(i+2)%4]);
				}
				
				drawer.AddTriangle(count, triangles[0]);
				drawer.AddTriangle(count+1, triangles[1]);
				
				count+=2;
			}
		}
		
		for(int i=0;i<size;i++) {
			vertex_normals[i]=VectorFunctions.VNorm(vertex_normals[i]);
		}
		
		count=0;
		for(int z=0;z<N-1;z++) {
			for(int x=0;x<N-1;x++) {
				drawer.GetTriangle(count).GetVertex(0).SetNorm(vertex_normals[z*N+x]);
				drawer.GetTriangle(count).GetVertex(1).SetNorm(vertex_normals[(z+1)*N+x]);
				drawer.GetTriangle(count).GetVertex(2).SetNorm(vertex_normals[(z+1)*N+(x+1)]);
				
				drawer.GetTriangle(count+1).GetVertex(0).SetNorm(vertex_normals[(z+1)*N+(x+1)]);
				drawer.GetTriangle(count+1).GetVertex(1).SetNorm(vertex_normals[z*N+(x+1)]);
				drawer.GetTriangle(count+1).GetVertex(2).SetNorm(vertex_normals[z*N+x]);
				
				count+=2;
			}
		}
		
		drawer.UpdateBuffers();
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(-20.0f, 50.0f, -20.0f), 
				VectorFunctions.VGet(20.0f, 10.0f, 20.0f));
	}
	
	@Override
	protected void Draw() {
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		
		program.Enable();
		drawer.Transfer();
	}
}
