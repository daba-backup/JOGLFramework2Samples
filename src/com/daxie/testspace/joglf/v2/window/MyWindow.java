package com.daxie.testspace.joglf.v2.window;

import com.daxie.joglf.gl.window.JOGLFWindow;

//Extend JOGLFWindow and make MyWindow.
public class MyWindow extends JOGLFWindow{
	public MyWindow() {
		
	}
	
	@Override
	protected void Init() {
		System.out.println("Init");
	}
	@Override
	protected void Reshape(int x,int y,int width,int height) {
		System.out.println("Reshape");
	}
	@Override
	protected void Update() {
		System.out.println("Update");
	}
	@Override
	protected void Draw() {
		System.out.println("Draw");
	}
	@Override
	protected void Dispose() {
		System.out.println("Dispose");
	}
}
