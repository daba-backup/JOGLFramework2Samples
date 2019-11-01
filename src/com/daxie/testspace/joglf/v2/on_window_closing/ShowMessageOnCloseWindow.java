package com.daxie.testspace.joglf.v2.on_window_closing;

import com.daxie.joglf.gl.window.JOGLFWindow;

public class ShowMessageOnCloseWindow extends JOGLFWindow{
	@Override
	protected void onWindowClosing() {
		System.out.println("Close");
	}
}
