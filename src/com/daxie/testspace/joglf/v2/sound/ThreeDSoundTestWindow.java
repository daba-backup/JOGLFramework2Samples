package com.daxie.testspace.joglf.v2.sound;

import com.daxie.basis.coloru8.ColorU8Functions;
import com.daxie.basis.vector.Vector;
import com.daxie.basis.vector.VectorFunctions;
import com.daxie.joglf.al.front.ALFront;
import com.daxie.joglf.al.sound.Sound3D;
import com.daxie.joglf.gl.front.CameraFront;
import com.daxie.joglf.gl.window.JOGLFWindow;
import com.daxie.joglf.gl.wrapper.GLDrawFunctions3D;

public class ThreeDSoundTestWindow extends JOGLFWindow{
	public ThreeDSoundTestWindow() {
		
	}
	
	private Vector position;
	
	private int sound_handle;
	
	@Override
	protected void Init() {
		ALFront.Initialize();
		
		position=VectorFunctions.VGet(50.0f, 50.0f, 50.0f);
		
		sound_handle=Sound3D.LoadSound("./Data/Sound/sound.wav");
		Sound3D.SetSoundReferenceDistance(sound_handle, 1.0f);
		Sound3D.SetSoundSourcePosition(sound_handle, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
		Sound3D.PlaySound(sound_handle);
	}
	@Override
	protected void Dispose() {
		ALFront.Dispose();
	}
	
	@Override
	protected void Update() {
		position=VectorFunctions.VAdd(position, VectorFunctions.VGet(-0.1f, -0.1f, -0.1f));
		
		Sound3D.SetListenerPosition(position);
		Sound3D.SetListenerOrientation(VectorFunctions.VGet(0.0f, 0.0f, 0.0f), VectorFunctions.VGet(0.0f, 1.0f, 0.0f));
		
		CameraFront.SetCameraPositionAndTarget_UpVecY(position, VectorFunctions.VGet(0.0f, 0.0f, 0.0f));
	}
	@Override
	protected void Draw() {
		GLDrawFunctions3D.DrawAxes(100.0f);
		GLDrawFunctions3D.DrawSphere3D(
				VectorFunctions.VGet(0.0f, 0.0f, 0.0f), 15.0f, 32, 32, ColorU8Functions.GetColorU8(0.0f, 1.0f, 1.0f, 1.0f));
	}
}
