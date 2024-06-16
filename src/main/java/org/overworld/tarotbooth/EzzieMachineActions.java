package org.overworld.tarotbooth;

import static org.overworld.tarotbooth.EzzieMachine.Trigger.ADVANCE;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.overworld.tarotbooth.images.ImageLibrary;
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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.val;

@Component
public class EzzieMachineActions {

	@Autowired
	private ImageLibrary imageLibrary;
	
	private TimeoutService timeoutService;
	@Autowired
	public void setTimeoutService(@Lazy TimeoutService timeoutService) {
		this.timeoutService = timeoutService;
	}
	
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

	@Getter
	private FXMLLoader mainLoader = new FXMLLoader();

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
	private MediaChain ominousMusic;
	private MediaPlayer carnivalMusic;
	private Timer curiousTimeoutTimer;
	 

	public void initialize() throws IOException {

		mainStage = new Stage();
		debugStage = new Stage();

		debugScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("debug.fxml"), null, null, springContext::getBean),
				640, 480);

		mainLoader.setControllerFactory(springContext::getBean);
		mainLoader.setLocation(BoothApplication.class.getResource("booth.fxml"));
		mainScene = new Scene(mainLoader.load(), 640, 480);

		debugStage.setScene(debugScene);
		debugStage.show();

		mainStage.setScene(mainScene);
		mainStage.show();
	}

	public void advance() {

		/* Wrapping in a timer to trick spring into ignoring the circularity */

		timer.schedule(new TimerTask() {
			public void run() {
				stateMachine.fire(ADVANCE);
			}
		}, 100);
	}

	public void advanceAfterFxmlLoadDelay() {

		/*
		 * This is a horrible hack to delay the state machine until FXMLLoader is
		 * finished initialising the BoothController, but there is no event to signal
		 * this so a delay is used instead, you may need to increase it for smaller
		 * hardware
		 */

		timer.schedule(new TimerTask() {
			public void run() {
				stateMachine.fire(ADVANCE);
			}
		}, 2500);
	}

	public void fadeVolumeTo(double target) {
		
		Timeline timeline = new Timeline(
		    new KeyFrame(Duration.seconds(2),
		        new KeyValue(carnivalMusic.volumeProperty(), target)));
		timeline.play();
	}
	
	public void running() {
		carnivalMusic = sounds.getPlayerFor("U01");
		carnivalMusic.setCycleCount(Integer.MAX_VALUE);
		carnivalMusic.play();
	}

	public void runningExit() {
		carnivalMusic.stop();
	}

	public void attracting() {
		controller.curtainskMode();
		gameModel.clear();
		fadeVolumeTo(1.0);
	}

	public void idle() {
		carousel.setFast(false);
		carousel.add(sounds.getPlayerFor("A01"));
		carousel.add(sounds.getPlayerFor("A02"));
		carousel.add(sounds.getPlayerFor("A03"));
		carousel.add(sounds.getPlayerFor("A04"));
		carousel.add(sounds.getPlayerFor("A05"));
		carousel.add(sounds.getPlayerFor("A06"));
		carousel.setNextIndex(random.nextInt(4));
	}

	public void idleExit() {
		carousel.clear();
	}

	public void curious() {
		carousel.setFast(true);
		carousel.setNextIndex(0); /* MmeZ's curious announcement will be the same every time */
		carousel.add(sounds.getPlayerFor("E01"));
		carousel.add(sounds.getPlayerFor("E02"));
		carousel.add(sounds.getPlayerFor("E03"));

		curiousTimeoutTimer = new Timer("Curious Timeout");
		
		curiousTimeoutTimer.schedule(new TimerTask() {
			public void run() {
				stateMachine.fire(ADVANCE);
			}
		}, 30000);
	}

	public void curiousExit() {
		curiousTimeoutTimer.cancel();
		carousel.clear();
	}

	public void hello() {
		controller.ezzieMode();
		fadeVolumeTo(0.09);
		val sceneSound = sounds.getPlayerFor("R01");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}
	
	public void helloExit() {
		sceneChain.stopAll();
	}

	public void quinn() {
		controller.quinnMode();
		fadeVolumeTo(0.0);
		val sceneSound = sounds.getPlayerFor("R02");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}

	public void quinnExit() {
		sceneChain.stopAll();
	}
	
	public void aside() {
		controller.bennyMode();
		val sceneSound = sounds.getPlayerFor("R0" + random.nextInt(3, 5));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}

	public void asideExit() {
		sceneChain.stopAll();
	}
	
	public void intro() {
		fadeVolumeTo(0.08);
		controller.ezzieMode();
		val sceneSound = sounds.getPlayerFor("R05");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}

	public void introExit() {
		sceneChain.stopAll();
	}
	
	public void requesting() {
		controller.readingSetup(null, null, null);
		controller.drawingMode();
	}
	
	public void requestingPast() {
		controller.readingSetup(imageLibrary.getImage("placeCard.png"), null, null);
		(sceneChain = new MediaChain(sounds.getPlayerFor("R06"))).getHead().play();
	}
	
	public void requestingPastExit() {
		sceneChain.stopAll();
	}

	public void receivingPast() {
		
		controller.readingSetup(imageLibrary.getImageForCard(gameModel.getPast().get().cardId()), null, null);
		
		val sceneSound = sounds.getPlayerFor("I0" + random.nextInt(1, 8));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		sceneChain = new MediaChain(sceneSound);
		
		Optional<Card> card = gameModel.getCardInPosition(Position.PAST);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().cardId()));
		}
		sceneChain.getHead().play();
		timeoutService.poke();
	}
	
	public void receivingPastExit() {
		sceneChain.stopAll();
	}
		
	public void requestingPresent() {
		controller.readingSetup(imageLibrary.getImageForCard(gameModel.getPast().get().cardId()), imageLibrary.getImage("placeCard.png"), null);
		(sceneChain = new MediaChain(sounds.getPlayerFor("R15"))).getHead().play();
	}
	
	public void requestingPresentExit() {
		sceneChain.stopAll();
	}

	public void receivingPresent() {
		
		controller.readingSetup(imageLibrary.getImageForCard(gameModel.getPast().get().cardId()), imageLibrary.getImageForCard(gameModel.getPresent().get().cardId()), null);
		
		val sceneSound = sounds.getPlayerFor("I0" + random.nextInt(1, 8));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		sceneChain = new MediaChain(sceneSound);
		
		Optional<Card> card = gameModel.getCardInPosition(Position.PRESENT);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().cardId()));
		}
		sceneChain.getHead().play();
		timeoutService.poke();
	}
	
	public void receivingPresentExit() {
		sceneChain.stopAll();
	}

	public void requestingFuture() {
		controller.readingSetup(imageLibrary.getImageForCard(gameModel.getPast().get().cardId()), imageLibrary.getImageForCard(gameModel.getPresent().get().cardId()), imageLibrary.getImage("placeCard.png"));
		(sceneChain = new MediaChain(sounds.getPlayerFor("R16"))).getHead().play();
	}
	
	public void requestingFutureExit() {
		sceneChain.stopAll();
	}	

	public void receivingFuture() {
		
		controller.readingSetup(imageLibrary.getImageForCard(gameModel.getPast().get().cardId()), imageLibrary.getImageForCard(gameModel.getPresent().get().cardId()), imageLibrary.getImageForCard(gameModel.getFuture().get().cardId()));
		
		val sceneSound = sounds.getPlayerFor("I0" + random.nextInt(1, 8));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		sceneChain = new MediaChain(sceneSound);
		
		Optional<Card> card = gameModel.getCardInPosition(Position.FUTURE);
		if (card.isPresent()) {
			sceneChain.wrap(sounds.getPlayerFor(card.get().cardId()));
		}
		sceneChain.getHead().play();
		timeoutService.poke();
	}
	
	public void receivingFutureExit() {
		sceneChain.stopAll();
	}
	
	public void reading() {
		controller.readingMode();
	}
	
	public void readingIntro() {
		controller.bennyMode();
		
		MediaPlayer ominous = sounds.getPlayerFor("U02");
		ominous.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(ominousMusic = new MediaChain(ominous)).getHead().play();
		
		sceneChain = new MediaChain(sounds.getPlayerFor("R08"));
		sceneChain.wrap(sounds.getPlayerFor("R07")).getHead().play();
		
		fadeVolumeTo(0.0);
	}

	public void readingIntroExit()  {
		fadeVolumeTo(0.09);
		ominousMusic.stopAll();
		sceneChain.stopAll();
	}
	
	public void readingPast() {
		
		Optional<Card> card = gameModel.getCardInPosition(Position.PAST);
		if (card.isPresent()) {
			val sceneSound = sounds.getPlayerFor(card.get().cardId(), Position.PAST);
			sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
			sceneChain = new MediaChain(sceneSound);
			sceneChain.wrap(sounds.getPlayerFor("R09"));
			sceneChain.getHead().play();
			
			controller.meaningSetup(imageLibrary.getImageForCard(card.get().cardId()), card.get().pastText());
		}
	}
	
	public void readingPastExit() {
		sceneChain.stopAll();
	}
	
	public void readingPresent() {
		
		Optional<Card> card = gameModel.getCardInPosition(Position.PRESENT);
		if (card.isPresent()) {
			val sceneSound = sounds.getPlayerFor(card.get().cardId(), Position.PRESENT);
			sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
			sceneChain = new MediaChain(sceneSound);
			sceneChain.wrap(sounds.getPlayerFor("R10"));
			sceneChain.getHead().play();
			
			controller.meaningSetup(imageLibrary.getImageForCard(card.get().cardId()), card.get().presentText());
		}	
	}
	
	public void readingPresentExit() {
		sceneChain.stopAll();
	}
	
	public void readingFuture() {
		
		Optional<Card> card = gameModel.getCardInPosition(Position.FUTURE);
		if (card.isPresent()) {
			val sceneSound = sounds.getPlayerFor(card.get().cardId(), Position.FUTURE);
			sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
			sceneChain = new MediaChain(sceneSound);
			sceneChain.wrap(sounds.getPlayerFor("R11"));
			sceneChain.getHead().play();
			
			controller.meaningSetup(imageLibrary.getImageForCard(card.get().cardId()), card.get().futureText());
		}	
	}
	
	public void readingFutureExit() {
		sceneChain.stopAll();
	}
	
	public void readingClose() {
		val sceneSound = sounds.getPlayerFor("R12");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(ADVANCE));
		(sceneChain = new MediaChain(sceneSound)).getHead().play();
	}
	
	public void readingCloseExit() {
		sceneChain.stopAll();
	}
	
	/* OLD */
	
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
}
