package com.daxie.testspace.joglf.g2.skydome;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		JOGLFWindow window=new SkydomeTestWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
