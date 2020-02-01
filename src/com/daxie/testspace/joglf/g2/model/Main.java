package com.daxie.testspace.joglf.g2.model;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;

//JOGLFramework version:7.0.0

public class Main {
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GLFront.Setup(GLVersion.GL4);
		
		for(int i=0;i<4;i++) {
			JOGLFWindow window=new DrawModelWindow();
			window.SetTitle("Window "+i);
			if(i==0)window.SetExitProcessWhenDestroyed();
		}
	}
}
