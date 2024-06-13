package org.overworld.tarotbooth;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.overworld.tarotbooth.EzzieMachine.State;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.model.GameModel;
import org.overworld.tarotbooth.model.Position;
import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.sound.MediaChain;
import org.overworld.tarotbooth.sound.SoundLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.github.oxo42.stateless4j.StateMachineConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

@Configuration
public class EzzieMachineConfiguration {
	
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
	
	@Bean
	public EzzieMachine stateMachine() {

		StateMachineConfig<State, Trigger> config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		config.configure(State.IDLE)
			.permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.onEntry(this::idle)
			.onExit(this::idleExit)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.ADVANCE)
			.ignore(Trigger.BAD_PLACEMENT);
	
		config.configure(State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.permit(Trigger.ADVANCE, State.IDLE)
			.onEntry(this::curious)
			.onExit(this::curiousExit)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)		
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.ENGAGED)
			.permit(Trigger.PAST_READ, State.REQUESTING_PRESENT)
			.permit(Trigger.ADVANCE, State.QUINN)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.RESET_BOOTH)
			.onEntry(this::engaged)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR);
		
		config.configure(State.QUINN)
			.permit(Trigger.ADVANCE, State.ASIDE)
			.substateOf(State.ENGAGED)
			.onEntry(this::quinn);
	
		config.configure(State.ASIDE)
			.permit(Trigger.ADVANCE, State.INTRO)
			.substateOf(State.ENGAGED)
			.onEntry(this::aside);
		
		config.configure(State.INTRO)
			.permit(Trigger.ADVANCE, State.REQUESTING_PAST)
			.substateOf(State.ENGAGED)
			.onEntry(this::intro);
		
		config.configure(State.REQUESTING_PAST)
			.permit(Trigger.PAST_READ, State.REQUESTING_PRESENT)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(this::requestingPast)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.REQUESTING_PRESENT)
			.permit(Trigger.PRESENT_READ, State.REQUESTING_FUTURE)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(this::requestingPresent)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.REQUESTING_FUTURE)
			.permit(Trigger.FUTURE_READ, State.READING)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.FIX_PLACEMENT)
			.onEntry(this::requestingFuture)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE);
		
		config.configure(State.READING)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(this::reading)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.READING_PAST)
			.permit(Trigger.ADVANCE, State.READING_PRESENT)
			.substateOf(State.READING)
			.onEntry(this::readPast);
		
		config.configure(State.READING_PRESENT)
			.permit(Trigger.ADVANCE, State.READING_FUTURE)
			.substateOf(State.READING_FUTURE)
			.onEntry(this::readPresent);
		
		config.configure(State.READING_FUTURE)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.substateOf(State.READING)
			.onEntry(this::readFuture);
		
		config.configure(State.FIX_PLACEMENT)
			.permit(Trigger.ADVANCE, State.REQUESTING_PAST)
			.permit(Trigger.TIMEOUT, State.RESET_BOOTH)
			.onEntry(this::fixPlacement)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.RESET_BOOTH)
			.permit(Trigger.ADVANCE, State.ENGAGED)
			.onEntry(this::resetBooth)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.CLOSING)
			.permit(Trigger.ADVANCE, State.IDLE)
			.permit(Trigger.PRINTER_ERROR, State.FIX_PRINTER)
			.onEntry(this::closing)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
		
		config.configure(State.FIX_PRINTER)
			.permit(Trigger.ADVANCE, State.CLOSING)
			.onEntry(this::fixPrinter)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.BAD_PLACEMENT);
					
		/* @formatter:on */

		try {
			config.generateDotFileInto(System.out, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EzzieMachine stateMachine = new EzzieMachine(State.IDLE, config);
		stateMachine.fireInitialTransition();
	    
		return stateMachine;
	}

	private MediaPlayer music;
	
	public void idle() {
		
		sounds.stopAll();
		
		music = sounds.get("U01");
		music.setCycleCount(Integer.MAX_VALUE);
		music.play();
		
		System.out.println("idle");
		carousel.setFast(false);
		carousel.add(sounds.get("A01"));
		carousel.add(sounds.get("A02"));
		carousel.add(sounds.get("A03"));
		carousel.add(sounds.get("A04"));
		carousel.add(sounds.get("A05"));
		carousel.add(sounds.get("A06"));
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
		carousel.add(sounds.get("E01"));
		carousel.add(sounds.get("E02"));
		carousel.add(sounds.get("E03"));
		controller.blackMode();
		
		timer.schedule(new TimerTask() {
	        public void run() {
	        	stateMachine.fire(Trigger.ADVANCE);
	        }
	    }, 30000);
	}

	public void curiousExit() {
		carousel.clear();
		music.stop();
	}
	
	private MediaChain chain;
	
	private MediaPlayer sceneSound;
	
	public void engaged() {
		System.out.println("engaged");
		controller.ezzieMode();
		sounds.stopAll();
		sceneSound = sounds.get("R01");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		sceneSound.play();
	}
	
	public void quinn() {
		System.out.println("quinn");
		controller.quinnMode();
		sounds.stopAll();
		sceneSound = sounds.get("R02");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		sceneSound.play();
	}

	public void aside() {
		System.out.println("aside");
		controller.bennyMode();
		sounds.stopAll();
		sceneSound = sounds.get("R0" + random.nextInt(3, 5));
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		sceneSound.play();
	}
	
	public void intro() {
		System.out.println("intro");
		controller.ezzieMode();
		sounds.stopAll();
		sceneSound = sounds.get("R05");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		sceneSound.play();
	}
	
	public void requestingPast() {
		System.out.println("requesting past");
		controller.ezzieMode();
		sounds.stopAll();
		sceneSound = sounds.get("R06");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		sceneSound.play();
	}

	public void requestingPresent() {
		System.out.println("requesting present");
		controller.ezzieMode();
		sounds.stopAll();
		sceneSound = sounds.get("R15");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		chain = new MediaChain(sceneSound);
		chain.wrap(sounds.get("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.PAST);
		if (card.isPresent()) {
			chain.wrap(sounds.get(card.get().tag()));
		}
		chain.getHead().play();	
	}

	public void requestingFuture() {
		System.out.println("requesting future");
		controller.ezzieMode();
		sounds.stopAll();		
		sceneSound = sounds.get("R16");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		chain = new MediaChain(sceneSound);
		chain.wrap(sounds.get("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.PRESENT);
		if (card.isPresent()) {
			chain.wrap(sounds.get(card.get().tag()));
		}
		chain.getHead().play();		
	}

	public void readPast() {

	}
	
	public void readPresent() {}
	
	public void readFuture() {}
	
	public void reading() {
		System.out.println("reading");
		
		sounds.stopAll();
		music = sounds.get("U02");
		music.setCycleCount(1);
		music.play();
		
		sceneSound = sounds.get("R08");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		
		chain = new MediaChain(sceneSound);
		chain.wrap(sounds.get("R07"));
		chain.wrap(sounds.get("I0" + random.nextInt(1, 8)));
		Optional<Card> card = gameModel.getCardInPosition(Position.FUTURE);
		if (card.isPresent()) {
			chain.wrap(sounds.get(card.get().tag()));
		}		
		chain.getHead().play();	
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
		sounds.stopAll();
		sceneSound = sounds.get("R13");
		sceneSound.setOnEndOfMedia(() -> stateMachine.fire(Trigger.ADVANCE));
		
	}

	public void fixPrinter() {
		System.out.println("fix printer");
	}
}
