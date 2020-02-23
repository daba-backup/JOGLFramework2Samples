package com.daxie.testspace.joglf.g2.ocean;

import com.daxie.joglf.gl.window.JOGLFWindow;

public class WaveSimulationWindow extends JOGLFWindow{
	private TildeHktComputation tilde_hkt_computation;
	private ButterflyComputation butterfly_computation;
	private InversionAndPermutation inv_and_perm;
	
	private static final int N=256;
	
	@Override
	protected void Init() {
		TildeH0kComputation tilde_h0k_computation=new TildeH0kComputation(N);
		tilde_hkt_computation=new TildeHktComputation(N);
		ButterflyTextureGeneration butterfly_texture_generation=new ButterflyTextureGeneration(N);
		butterfly_computation=new ButterflyComputation(N);
		inv_and_perm=new InversionAndPermutation(N);
		
		tilde_h0k_computation.Compute();
		
		tilde_hkt_computation.SetTildeH0kLength(tilde_h0k_computation.GetTildeH0kLength());
		tilde_hkt_computation.SetTildeH0minuskLength(tilde_h0k_computation.GetTildeH0minuskLength());
		tilde_hkt_computation.SetNormalizedTildeH0k(tilde_h0k_computation.GetNormalizedTildeH0k());
		tilde_hkt_computation.SetNormalizedTildeMinusk(tilde_h0k_computation.GetNormalizedTildeH0minusk());
		
		butterfly_texture_generation.Compute();
		
		butterfly_computation.SetButterflyLength(butterfly_texture_generation.GetOutColorLength());
		butterfly_computation.SetNormalizedButterfly(butterfly_texture_generation.GetNormalizedOutColor());
	}
	
	@Override
	protected void Update() {
		tilde_hkt_computation.Compute();
		tilde_hkt_computation.AdvanceTime(1.0f/30.0f);
		
		butterfly_computation.SetPingpongInLength(tilde_hkt_computation.GetTildeHktLength());
		butterfly_computation.SetNormalizedPingpongIn(tilde_hkt_computation.GetNormalizedTildeHkt());
		butterfly_computation.Compute();
		
		inv_and_perm.SetInputLength(butterfly_computation.GetOutLength());
		inv_and_perm.SetNormalizedInput(butterfly_computation.GetNormalizedOut());
	}
}
