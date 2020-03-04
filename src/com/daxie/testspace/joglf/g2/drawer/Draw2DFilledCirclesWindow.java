package com.daxie.testspace.joglf.g2.drawer;

import java.awt.Point;
import java.util.Random;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.joglf.gl.drawer.Dynamic2DFilledCirclesDrawer;
import com.daxie.joglf.gl.shape.Circle2D;
import com.daxie.joglf.gl.window.JOGLFWindow;

class Draw2DFilledCirclesWindow extends JOGLFWindow{
	private Dynamic2DFilledCirclesDrawer drawer;
	
	@Override
	protected void Init() {
		drawer=new Dynamic2DFilledCirclesDrawer();
		drawer.SetDefaultProgram();
		
		Random random=new Random();
		
		for(int i=0;i<10;i++) {
			Circle2D circle=new Circle2D();
			
			float r=random.nextFloat();
			float g=random.nextFloat();
			float b=random.nextFloat();
			
			circle.SetCenter(new Point(i*50, i*50));
			circle.SetColor(ColorU8Functions.GetColorU8(r, g, b, 1.0f));
			circle.SetRadius(100);
			circle.SetDivNum(32);
			
			drawer.AddCircle(i, circle);
		}
		
		drawer.UpdateBuffers();
	}
	
	@Override
	protected void Reshape(int x, int y, int width, int height) {
		drawer.SetWindowSize(width, height);
		drawer.UpdateBuffers();
	}
	
	@Override
	protected void Draw() {
		drawer.Draw();
	}
}
