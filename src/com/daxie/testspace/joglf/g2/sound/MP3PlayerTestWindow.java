package com.daxie.testspace.joglf.g2.sound;

import com.daxie.joglf.al.player.mp3.MP3Player;
import com.daxie.joglf.gl.input.keyboard.KeyboardEnum;
import com.daxie.joglf.gl.window.JOGLFWindow;

class MP3PlayerTestWindow extends JOGLFWindow{
	public MP3PlayerTestWindow() {
		
	}
	
	private int sound_handle;
	
	@Override
	protected void Init() {
		sound_handle=MP3Player.LoadSound("./Data/Sound/sound.mp3");
		MP3Player.PlaySound(sound_handle);
	}
	@Override
	protected void Update() {
		if(this.GetKeyboardPressingCount(KeyboardEnum.KEY_SPACE)==1) {
			MP3Player.DeleteSound(sound_handle);
		}
	}
}
