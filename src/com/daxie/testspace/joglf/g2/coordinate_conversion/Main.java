package com.daxie.testspace.joglf.g2.coordinate_conversion;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;

//JOGLFramework version:6.0.0

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		new CoordinateConversionTestWindow();
	}
}