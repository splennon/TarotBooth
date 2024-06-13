package org.overworld.tarotbooth.sound;

import java.util.Arrays;
import java.util.List;

import javafx.scene.media.MediaPlayer;
import lombok.Getter;

public class MediaChain {

	@Getter
	private MediaPlayer head;
	
	public MediaChain(MediaPlayer last) {
		this.head = last;
	}
	
	public MediaChain(MediaPlayer first, MediaPlayer second, MediaPlayer... remainder) {
		
		List<MediaPlayer> playerList = Arrays.asList(remainder);
		playerList.add(0, second);
		playerList.add(0, first);
		
		this.head = playerList.remove(playerList.size() - 1);

		for (int i = playerList.size() -1 ; i <= 0 ; i--) {
			
			playerList.get(i).setOnEndOfMedia(this.head::play);
			this.head = playerList.get(i);
		}
	}
	
	public MediaChain wrap(MediaPlayer outer) {
		outer.setOnEndOfMedia(this.head::play);
		this.head = outer;
		return this;
	}
}
