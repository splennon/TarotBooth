package org.overworld.tarotbooth;

import java.util.ArrayList;

import org.overworld.tarotbooth.sound.SoundLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;

@Component
public class SoundCarousel extends ArrayList<MediaPlayer>{
	
	
	@Autowired
	private SoundLibrary sounds;
	
	@Getter
	@Setter
	private boolean fast;
	
	private static final long serialVersionUID = 4001239993158228575L;
	
	@Setter
	private int nextIndex = 0;
	
	@Scheduled(fixedRate = 30000)
	public void slowPlay() {
		if (!fast)
			playOut();
	}
	
	@Scheduled(fixedRate = 10000)
	public void fastPlay() {
		if (fast)
			playOut();
	}
	
	public void playOut() {
		
		if (this.isEmpty())
			return;
		
		if (nextIndex >= this.size())
			nextIndex = 0;
		
		MediaPlayer sound = this.get(nextIndex++);
		sound.setOnEndOfMedia(sound::stop);
		sound.play();
	}	
}
