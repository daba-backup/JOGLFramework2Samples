package com.daxie.testspace.joglf.g2.box_muller;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;
import com.daxie.log.LogWriter;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		LogWriter.SetLogLevelFlags(LogWriter.LOG_LEVEL_ALL);
		GLFront.Setup(GLVersion.GL4);
		
		JOGLFWindow window=new BoxMullerMethodTestWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
