package com.daxie.testspace.joglf.g2.text;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.joglf.gl.text.TextMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class DrawTextWindow extends JOGLFWindow{
	public DrawTextWindow() {
		
	}
	
	@Override
	protected void Draw() {
		TextMgr.DrawText(5, 5, "Text", ColorU8Functions.GetColorU8(1.0f, 1.0f, 0.0f, 1.0f), 64, 64);
	}
}
