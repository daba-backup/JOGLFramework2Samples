package com.daxie.testspace.joglf.g2.ocean;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.daxie.joglf.gl.shader.ShaderProgram;
import com.daxie.joglf.gl.transferrer.FullscreenQuadTransferrerWithUV;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLWrapper;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;

class WaveSimulationWindow extends JOGLFWindow{
	private TildeHktComputation tilde_hkt_computation;
	private ButterflyComputation butterfly_computation;
	private InversionAndPermutation inv_and_perm;
	
	private static final int N=256;
	
	private int b_heightmap_id;
	
	private ShaderProgram program;
	private FullscreenQuadTransferrerWithUV transferrer;
	
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
		
		this.SetupHeightmap();
		this.SetupProgram();
		
		transferrer=new FullscreenQuadTransferrerWithUV();
	}
	private void SetupHeightmap() {
		IntBuffer texture_ids=Buffers.newDirectIntBuffer(1);
		GLWrapper.glGenTextures(1, texture_ids);
		b_heightmap_id=texture_ids.get(0);
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, b_heightmap_id);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	private void SetupProgram() {
		program=new ShaderProgram("texture_drawer");
	}
	
	@Override
	protected void Update() {
		tilde_hkt_computation.Compute();
		tilde_hkt_computation.AdvanceTime(1.0f/30.0f);
		
		butterfly_computation.SetPingpongIn(tilde_hkt_computation.GetTildeHkt());
		butterfly_computation.Compute();
		
		inv_and_perm.SetInputTexture(butterfly_computation.GetComputationResult());
		inv_and_perm.Compute();
		
		int heightmap_id=inv_and_perm.GetHeightmap();
		FloatBuffer heightmap=Buffers.newDirectFloatBuffer(N*N*4);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, heightmap_id);
		GLWrapper.glGetTexImage(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, GL4.GL_FLOAT, heightmap);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		ByteBuffer b_heightmap=Buffers.newDirectByteBuffer(N*N*4);
		
		int size=N*N*4;
		for(int i=0;i<size;i+=4) {
			float height=heightmap.get(i);
			
			byte b;
			if(height<0.0f)b=0;
			else b=(byte)(height*255.0f);
			
			b_heightmap.put(b);
			b_heightmap.put(b);
			b_heightmap.put(b);
			b_heightmap.put((byte)255);
		}
		((Buffer)b_heightmap).flip();
		
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, b_heightmap_id);
		GLWrapper.glTexImage2D(
				GL4.GL_TEXTURE_2D, 0,GL4.GL_RGBA, 
				N, N, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, b_heightmap);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
	
	@Override
	protected void Draw() {
		program.Enable();
		GLWrapper.glViewport(0, 0, this.GetWidth(), this.GetHeight());
		GLWrapper.glActiveTexture(GL4.GL_TEXTURE0);
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, b_heightmap_id);
		program.SetUniform("texture_sampler", 0);
		transferrer.Transfer();
		GLWrapper.glBindTexture(GL4.GL_TEXTURE_2D, 0);
	}
}
