package com.daxie.testspace.joglf.g2.fundamental;

import com.daxie.joglf.gl.front.GLFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLVersion;

class CreateWindows {
	public static void main(String[] args) {
		new CreateWindows();
	}
	public CreateWindows() {
		GLFront.Setup(GLVersion.GL4);
		
		//Create four windows.
		for(int i=0;i<4;i++) {
			JOGLFWindow window=new JOGLFWindow();
			window.SetTitle("Window "+i);
		}
	}
}
