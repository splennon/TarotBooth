package org.overworld.tarotbooth.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.Test;
import org.overworld.tarotbooth.model.Deck;
import org.overworld.tarotbooth.model.Position;

class SoundClipTest {

	@Test
	void testSimple() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundLibrary sc = new SoundLibrary();
		sc.get("U01").play();
		Thread.sleep(5000);

	}
	
	@Test
	void testCard() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundLibrary sc = new SoundLibrary();
		sc.get((new Deck()).get("S03"), Position.FUTURE).play();
		Thread.sleep(5000);
		sc.get("S03").stop();
	}
	
	@Test
	void testCardbById() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundLibrary sc = new SoundLibrary();
		sc.get("S04", Position.PAST).play();
		Thread.sleep(5000);
		sc.get("S04").stop();
	}
	
	@Test
	void testStopAll() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundLibrary sc = new SoundLibrary();
		sc.stopAll();
	}
	
}
