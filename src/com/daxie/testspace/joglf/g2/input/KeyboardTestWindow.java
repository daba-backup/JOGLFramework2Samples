package com.daxie.testspace.joglf.g2.input;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class KeyboardTestWindow extends JOGLFWindow{
	public KeyboardTestWindow() {
		
	}
	
	@Override
	protected void Draw() {
		int pressing_count=this.GetKeyboardPressingCount(KeyboardEnum.KEY_ENTER);
		float radius=pressing_count*0.1f;
		
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 
				radius, 32, 32, ColorU8Functions.GetColorU8(0.0f, 1.0f, 1.0f, 1.0f));
	}
}
