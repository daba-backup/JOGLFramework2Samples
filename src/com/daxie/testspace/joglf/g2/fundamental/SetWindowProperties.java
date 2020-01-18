package com.daxie.testspace.joglf.g2.fundamental;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;

//JOGLFramework version:2.0.0

public class SetWindowProperties {
	public static void main(String[] args) {
		new SetWindowProperties();
	}
	public SetWindowProperties() {
		GLFront.Setup(GLVersion.GL4);
		
		JOGLFWindow window=new JOGLFWindow();
		window.SetTitle("Test");
		window.SetSize(1280, 960);
		window.SetBackgroundColor(ColorU8Functions.GetColorU8(0.0f, 1.0f, 1.0f, 1.0f));
		
		//Call System.exit(0) when this window is destroyed.
		window.SetExitProcessWhenDestroyed();
	}
}
