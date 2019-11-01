package com.daxie.testspace.joglf.v2.drawer;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;

//JOGLFramework version:4.0.0

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		new DrawSegmentsWindow();
		//new DrawTrianglesWindow();
		//new DrawQuadranglesWindow();
		//new Draw2DFilledCirclesWindow();
	}
}
