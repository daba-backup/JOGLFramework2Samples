package com.daxie.testspace.joglf.g2.model;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;
import com.daxie.log.LogWriter;

//JOGLFramework version:8.1.0

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		LogWriter.SetLogLevelFlags(LogWriter.LOG_LEVEL_ALL);
		GLFront.Setup(GLVersion.GL4);
		
		JOGLFWindow window=new DrawModelWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
