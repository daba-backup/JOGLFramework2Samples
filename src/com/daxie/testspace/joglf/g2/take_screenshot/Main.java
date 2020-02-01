package com.daxie.testspace.joglf.g2.take_screenshot;

//JOGLFramework version:7.0.0

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;
import com.daxie.log.LogFile;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		LogFile.SetLogLevelFlags(LogFile.LOG_LEVEL_ALL);
		GLFront.Setup(GLVersion.GL4);
		
		TakeScreenshotTestWindow window=new TakeScreenshotTestWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
