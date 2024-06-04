package org.overworld.tarotbooth.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.media.AudioClip;

public class SoundClip {
    
	private final AudioClip m;
	
    public SoundClip(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	m = new AudioClip(SoundClip.class.getResource(filename).toString());
    }
    
    public void play() {
    	m.play();
    }
}
