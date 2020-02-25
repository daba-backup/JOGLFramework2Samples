package com.daxie.testspace.joglf.g2.fog;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.front.FogFront;
import com.daxie.joglf.gl.model.Model3D;
import com.daxie.joglf.gl.window.JOGLFWindow;

class FogTestWindow extends JOGLFWindow{
	private int model_handle;
	
	@Override
	protected void Init() {
		final float MODEL_SCALE=1.7f/20.0f;
		model_handle=Model3D.LoadModel("./Data/Model/BD1/map2/temp.bd1");
		Model3D.RescaleModel(model_handle, VectorFunctions.VGet(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE));
		
		FogFront.SetFogColor(ColorU8Functions.GetColorU8(0.4f, 0.0f, 1.0f, 1.0f));
		FogFront.SetFogStartEnd(20.0f, 100.0f);
		
		this.SetBackgroundColor(ColorU8Functions.GetColorU8(0.4f, 0.0f, 1.0f, 1.0f));
	}
	
	@Override
	protected void Draw() {
		Model3D.DrawModel(model_handle);
	}
}
