package org.overworld.tarotbooth.images;

import org.overworld.tarotbooth.model.Deck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javafx.scene.image.Image;

@Component
public class ImageLibrary {
	
	@Autowired
	private Deck deck;
	
	@Value("${cardRootDirectory}")
	private String cardRootDirectory;
	
	public Image getImageForCard(String cardId) {

		String s = ImageLibrary.class.getResource("./").toString() + cardRootDirectory + deck.get(cardId).filename();
		return new Image(s);
	}
	
	public Image getImage(String name) {

		return new Image(ImageLibrary.class.getResource(name).toString());	
	}
}
