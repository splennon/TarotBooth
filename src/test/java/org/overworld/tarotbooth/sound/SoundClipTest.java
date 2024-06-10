package org.overworld.tarotbooth.sound;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.jupiter.api.Test;

class SoundClipTest {

	@Test
	void test() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		SoundClip sc = new SoundClip("U01.mp3");
		sc.play();
		Thread.sleep(10000);
	}
}
