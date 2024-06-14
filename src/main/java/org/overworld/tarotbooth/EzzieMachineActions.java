package org.overworld.tarotbooth;

import static org.overworld.tarotbooth.EzzieMachine.Trigger.ADVANCE;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.model.GameModel;
import org.overworld.tarotbooth.model.Position;
import org.overworld.tarotbooth.sound.MediaChain;
import org.overworld.tarotbooth.sound.SoundCarousel;
import org.overworld.tarotbooth.sound.SoundLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import lombok.val;

@Component
public class EzzieMachineActions {
	
	@Autowired
	private BoothController controller;
	
	@Autowired
	private SoundLibrary sounds;
	
	@Autowired
	private SoundCarousel carousel;
	
	@Autowired
	private GameModel gameModel;
	
	private Random random = new Random();

	private EzzieMachine stateMachine;

	@Autowired
	public void setStateMachine(@Lazy EzzieMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	@Autowired
	private ApplicationContext springContext;
	
	private Stage mainStage, debugStage;
	private Scene debugScene, mainScene;

	private Timer timer = new Timer("Autoadvance");
	
	private MediaChain sceneChain;
	
	public void initialize() throws IOException {

		mainStage = new Stage();
		debugStage = new Stage();

		debugScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("debug.fxml"), null, null, springContext::getBean),
				640, 480);

		mainScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("booth.fxml"), null, null, springContext::getBean),
				640, 480);
		
		debugStage.setScene(debugScene);
		debugStage.show();

		mainStage.setScene(mainScene);
		mainStage.show();
	}
	
	private MediaPlayer music;
	
	public void idle() {
		
		music = sounds.getPlayerFor("U01");
		music.setCycleCount(Integer.MAX_VALUE);
		music.play();
		
		System.out.println("idle");
		carousel.setFast(false);
		carousel.add(sounds.getPlayerFor("A01"));
		carousel.add(sounds.getPlayerFor("A02"));
		carousel.add(sounds.getPlayerFor("A03"));
		carousel.add(sounds.getPlayerFor("A04"));
		carousel.add(sounds.getPlayerFor("A05"));
		carousel.add(sounds.getPlayerFor("A06"));
		carousel.setNextIndex(random.nextInt(4));
		controller.blackMode();
	}
	
	public void idleExit() {
		carousel.clear();
	}

	public void curious() {
		System.out.println("curious");
		carousel.setFast(true);
		carousel.setNextIndex(0); /* MmeZ's curious announcement will be the same every time */
		carousel.add(sounds.getPlayerFor("E01"));
		carousel.add(sounds.getPlayerFor("E02"));
		carousel.add(sounds.getPlayerFor("E03"));
		controller.blackMode();
		
		timer.schedule(new TimerTask() {
	        public void run() {
	        	stateMachine.fire(ADVANCE);
	        }
	    }, 30000);
	}

	public void curiousExit() {
		carousel.clear();
		music.stop();
	}
	
	public void engaged() {
		System.out.println("engaged");
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R01");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}
	
	public void quinn() {
		System.out.println("quinn");
		controller.quinnMode();
		val sceneSound = sounds.getPlayerFor("R02");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}

	public void aside() {
		System.out.println("aside");
		controller.bennyMode();
		val sceneSound = sounds.getPlayerFor("R0" + random.nextInt(3, 5));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}
	
	public void intro() {
		System.out.println("intro");
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R05");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}
	
	public void requestingPast() {
		System.out.println("requesting past");
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R06");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}

	public void requestingPresent() {
		System.out.println("requesting present");
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R15");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		sceneChain = new MediaChain(sceneSound);
		sceneChain.wrap(sounds.getPlayerFor("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.PAST);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().tag()));
		}
		sceneChain.getHead().play();	
	}

	public void requestingFuture() {
		System.out.println("requesting future");
		controller.ezzieMode();	
		val sceneSound = sounds.getPlayerFor("R16");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		sceneChain = new MediaChain(sceneSound);
		sceneChain.wrap(sounds.getPlayerFor("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.PRESENT);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().tag()));
		}
		sceneChain.getHead().play();		
	}

	public void readPast() {

	}
	
	public void readPresent() {}
	
	public void readFuture() {}
	
	public void reading() {
		System.out.println("reading");
		
		music = sounds.getPlayerFor("U02");
		music.setCycleCount(1);
		music.play();
		
		val sceneSound = sounds.getPlayerFor("R08");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		
		sceneChain = new MediaChain(sceneSound);
		sceneChain.wrap(sounds.getPlayerFor("R07"));
		sceneChain.wrap(sounds.getPlayerFor("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.FUTURE);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().tag()));
		}		
		sceneChain.getHead().play();	
	}

	public void fixPlacement() {
		System.out.println("fix placement");
	}

	public void resetBooth() {
		System.out.println("reset booth");
	}

	public void closing() {
		System.out.println("closing");
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R13");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		
	}

	public void fixPrinter() {
		System.out.println("fix printer");
	}
	
	/* new methods */
	
	public void advance() {
		
		/* Wrapping in a timer to trick spring into ignoring the circularity */
		
		timer.schedule(new TimerTask() {
	        public void run() {
	        	stateMachine.fire(ADVANCE);
	        }
	    }, 100);
	}
}
