package com.daxie.testspace.joglf.g2.take_screenshot;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.wrapper.GLVersion;
import com.daxie.log.LogWriter;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		LogWriter.SetLogLevelFlags(LogWriter.LOG_LEVEL_ALL);
		GLFront.Setup(GLVersion.GL4);
		
		TakeScreenshotTestWindow window=new TakeScreenshotTestWindow();
		window.SetExitProcessWhenDestroyed();
	}
}
