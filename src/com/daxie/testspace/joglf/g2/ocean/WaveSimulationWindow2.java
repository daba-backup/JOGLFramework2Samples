package com.daxie.testspace.joglf.g2.ocean;

import java.nio.FloatBuffer;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.drawer.DynamicSegmentsDrawer;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.shape.Vertex3D;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;

public class WaveSimulationWindow2 extends JOGLFWindow{
	private TildeHktComputation tilde_hkt_computation;
	private ButterflyComputation butterfly_computation;
	private InversionAndPermutation inv_and_perm;
	
	private static final int N=256;

	private DynamicSegmentsDrawer drawer;
	
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
		
		drawer=new DynamicSegmentsDrawer();
	}
	
	@Override
	protected void Update() {
		tilde_hkt_computation.Compute();
		tilde_hkt_computation.AdvanceTime(1.0f/30.0f);
		
		butterfly_computation.SetPingpongIn(tilde_hkt_computation.GetTildeHkt());
		butterfly_computation.Compute();
		
		inv_and_perm.SetInputTexture(butterfly_computation.GetComputationResult());
		inv_and_perm.Compute();
		
		FloatBuffer heightmap=inv_and_perm.GetHeightmap();
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<N;j++) {
				float height=heightmap.get()*5.0f;
				
				float x=(float)i;
				float z=(float)j;
				
				Vertex3D[] vertices=new Vertex3D[2];
				vertices[0]=new Vertex3D();
				vertices[0].SetPos(VectorFunctions.VGet(x,height-0.1f,z));
				vertices[0].SetDif(ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
				vertices[1]=new Vertex3D();
				vertices[1].SetPos(VectorFunctions.VGet(x,height+0.1f,z));
				vertices[1].SetDif(ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f));
				
				drawer.AddSegment(i*N+j, vertices[0], vertices[1]);
			}
		}
		drawer.UpdateBuffers();
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(
				VectorFunctions.VGet(-10.0f, 10.0f, -10.0f), 
				VectorFunctions.VGet(100.0f, 5.0f, 100.0f));
	}
	
	@Override
	protected void Draw() {
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		drawer.Draw();
	}
}
