package com.daxie.testspace.joglf.v2.tinter;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.TinterFront;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class FadeInTestWindow extends JOGLFWindow{
	public FadeInTestWindow() {
		
	}
	
	private float alpha;
	
	@Override
	protected void Init() {
		alpha=1.0f;
	}
	@Override
	protected void Update() {
		if(alpha>0.0f)alpha-=0.005f;
		TinterFront.SetTintColor(ColorU8Functions.GetColorU8(0.0f, 0.0f, 0.0f, alpha));
	}
	@Override
	protected void Draw() {
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 15.0f, 32, 32, ColorU8Functions.GetColorU8(1.0f, 1.0f, 0.0f, 1.0f));
	}
}
