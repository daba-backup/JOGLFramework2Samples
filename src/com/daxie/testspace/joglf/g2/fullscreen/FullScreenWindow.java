package com.daxie.testspace.joglf.g2.fullscreen;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.text.TextMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.window.WindowMode;

class FullScreenWindow extends JOGLFWindow{
	public FullScreenWindow() {
		
	}
	
	@Override
	protected void Init() {
		this.SetWindowMode(WindowMode.FULL_SCREEN);
	}
	@Override
	protected void Update() {
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_ESCAPE)==1) {
			this.CloseWindow();
		}
	}
	@Override
	protected void Draw() {
		TextMgr.DrawText(5, 5, "Full Screen", ColorU8Functions.GetColorU8(0.0f, 1.0f, 0.0f, 1.0f), 64, 64);
	}
}
