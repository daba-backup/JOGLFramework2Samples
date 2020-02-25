package com.daxie.testspace.joglf.g2.on_window_closing;

import com.daxie.joglf.gl.window.JOGLFWindow;

class ShowMessageOnCloseWindow extends JOGLFWindow{
	@Override
	protected void onWindowClosing() {
		System.out.println("Close");
	}
}
