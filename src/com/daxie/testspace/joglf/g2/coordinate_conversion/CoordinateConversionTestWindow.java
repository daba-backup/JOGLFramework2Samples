package com.daxie.testspace.joglf.g2.coordinate_conversion;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.gl.draw.GLDrawFunctions2D;
import com.daxie.joglf.gl.draw.GLDrawFunctions3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.tool.CoordinateFunctions;
import com.daxie.joglf.gl.window.JOGLFWindow;

class CoordinateConversionTestWindow extends JOGLFWindow{
	@Override
	protected void Draw() {
		this.WorldPosToScreenPosTest();
		this.ScreenPosToWorldPosTest();
		
		GLDrawFunctions3D.DrawAxes(100.0f);
	}
	private void WorldPosToScreenPosTest() {
		Vector world_pos=VectorFunctions.VGet(30.0f, 0.0f, 0.0f);
		Vector screen_pos=CameraFront.ConvertWorldPosToScreenPos(world_pos);
		
		int x=(int)screen_pos.GetX();
		int y=(int)screen_pos.GetY();
		GLDrawFunctions2D.DrawCircle2D(x, y, 50, 32, ColorU8Functions.GetColorU8(0.0f, 1.0f, 1.0f, 1.0f));
	}
	private void ScreenPosToWorldPosTest() {
		int x=this.GetMouseX();
		int y=this.GetMouseY();
		int height=this.GetHeight();
		y=CoordinateFunctions.ConvertWindowCoordinateAndOpenGLCoordinate_Y(y, height);
		
		Vector screen_pos=VectorFunctions.VGet(x, y, 0.98f);
		Vector world_pos=CameraFront.ConvertScreenPosToWorldPos(screen_pos);
		
		GLDrawFunctions3D.DrawSphere3D(world_pos, 10.0f, 16, 16, ColorU8Functions.GetColorU8(1.0f, 1.0f, 0.0f, 1.0f));
	}
}
