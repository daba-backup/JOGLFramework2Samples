package com.daxie.testspace.joglf.g2.shadow_mapping;

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
		
		ShadowMappingTestWindow window=new ShadowMappingTestWindow();
		window.SetTitle("Shadow Mapping");
	}
}
