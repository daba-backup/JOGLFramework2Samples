package com.daxie.testspace.joglf.g2.input;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4bc);
		
		new KeyboardTestWindow().SetTitle("KeyboardTest");
		new MouseTestWindow().SetTitle("MouseTest");
	}
}
