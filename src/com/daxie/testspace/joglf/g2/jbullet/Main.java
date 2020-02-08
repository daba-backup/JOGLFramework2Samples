package com.daxie.testspace.joglf.g2.jbullet;

//JOGLFramework version:8.1.1

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.window.WindowCommonInfoStock;
import com.daxie.joglf.gl.wrapper.GLVersion;
import com.daxie.log.LogFile;

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		LogFile.SetLogLevelFlags(LogFile.LOG_LEVEL_ALL);
		GLFront.Setup(GLVersion.GL4);
		
		final int FPS=30;
		WindowCommonInfoStock.SetFPS(FPS);
		
		JOGLFWindow window=new JBulletTestWindow3(FPS);
		window.SetTitle("JBullet");
		window.SetExitProcessWhenDestroyed();
	}
}
