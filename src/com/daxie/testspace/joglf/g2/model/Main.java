package com.daxie.testspace.joglf.g2.model;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;

//JOGLFramework version:8.1.0

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		JOGLFWindow window=new ModelOperationsTestWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
