package com.daxie.testspace.joglf.g2.swing;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		new SwingMenuTestWindow().SetExitProcessWhenDestroyed();
		new SwingButtonTestWindow();
	}
}
