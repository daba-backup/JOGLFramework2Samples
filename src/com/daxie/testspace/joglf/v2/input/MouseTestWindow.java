package com.daxie.testspace.joglf.v2.input;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.input.mouse.MouseEnum;
import com.daxie.joglf.gl.text.TextMgr;
import com.daxie.joglf.gl.window.JOGLFWindow;

public class MouseTestWindow extends JOGLFWindow{
	public MouseTestWindow() {
		
	}
	
	@Override
	protected void Draw() {
		int mouse_x=this.GetMouseX();
		int mouse_y=this.GetMouseY();
		int pressing_count=this.GetMousePressingCount(MouseEnum.MOUSE_LEFT);
		float wheel_rot=this.GetMouseWheelVerticalRotation();
		float radius=pressing_count*0.1f;
		
		String pos_str="("+mouse_x+", "+mouse_y+")";
		String wheel_rot_str=""+wheel_rot;
		TextMgr.DrawText(5, 5, pos_str, ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f), 64, 64);
		TextMgr.DrawText(5, 100, wheel_rot_str, ColorU8Functions.GetColorU8(1.0f, 1.0f, 1.0f, 1.0f), 64, 64);
		
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), radius, 32, 32, ColorU8Functions.GetColorU8(0.0f, 1.0f, 1.0f, 1.0f));
	}
}
