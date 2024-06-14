package org.overworld.tarotbooth;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static org.overworld.tarotbooth.EzzieMachine.State.*;
import static org.overworld.tarotbooth.EzzieMachine.Trigger.*;

import org.overworld.tarotbooth.EzzieMachine.State;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.model.GameModel;
import org.overworld.tarotbooth.model.Position;
import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.sound.MediaChain;
import org.overworld.tarotbooth.sound.SoundCarousel;
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
import lombok.val;

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
	
	@Bean
	public EzzieMachine stateMachine() {

		StateMachineConfig<State, Trigger> config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		/* Unused super state for syntactic simplicity, holds all ignores */
		
		config.configure(LOADED)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)
			.ignore(ADVANCE)
			.ignore(BAD_PLACEMENT_PAST)
			.ignore(BAD_PLACEMENT_PREESNT)
			.ignore(BAD_PLACEMENT_FUTURE)
			.ignore(BAD_PLACEMENT)
			.ignore(BAD_PLACEMENT_JOCKER)
			;

		/* Start the music, Ezzie is in session! */
		
		config.configure(RUNNING)
			.substateOf(LOADED)
			.onEntry(() -> System.out.println("Entering Superstate RUNNING"))
			.permit(ADVANCE, ATTRACTING)
			.onEntry(this::advance)
			;

		/* Ezzie is attracting people into her empty booth */
		
		config.configure(ATTRACTING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate ATTRACTING"))
			.permit(ADVANCE, IDLE)
			.onEntry(this::advance)
			;
		config.configure(IDLE)
			.substateOf(ATTRACTING)
			.onEntry(() -> System.out.println("Entering IDLE"))
			.permit(APPROACH_SENSOR, ASIDE)
			;
		config.configure(CURIOUS)
			.substateOf(ATTRACTING)
			.onEntry(() -> System.out.println("Entering CURIOUS"))
			.permit(PRESENCE_SENSOR, ENGAGED)
			;
		
		/* Ezzie has a customer! */
		
		config.configure(ENGAGED)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate ENGAGED"))
			;
		config.configure(HELLO)
			.substateOf(ENGAGED)
			.onEntry(() -> System.out.println("Entering HELLO"))
			;
		config.configure(QUINN)
			.substateOf(ENGAGED)
			.onEntry(() -> System.out.println("Entering QUINN"))
			;
		config.configure(ASIDE)
			.substateOf(ENGAGED)
			.onEntry(() -> System.out.println("Entering ASIDE"))
			;
		config.configure(INTRO)
			.substateOf(ENGAGED)
			.onEntry(() -> System.out.println("Entering INTRO"))
			;
		
		/* Ezzie is requesting cards be drawn */
		
		config.configure(REQUESTING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate REQUESTING"))
			;
		config.configure(REQUESTING_PAST)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering REQUESTING_PAST"))
			;
		config.configure(RECEIVING_PAST)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering RECEIVING_PAST"))
			;
		config.configure(REQUESTING_PRESENT)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering REQUESTING_PRESENT"))
			;
		config.configure(RECEIVING_PRESENT)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering RECEIVING_PRESENT"))
			;
		config.configure(REQUESTING_FUTURE)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering REQUESTING_FUTURE"))
			;
		config.configure(RECEIVING_FUTURE)
			.substateOf(REQUESTING)
			.onEntry(() -> System.out.println("Entering RECEIVING_FUTURE"))
			;
		
		/* Ezzie is offering her reading with a little help from Benny */
		
		config.configure(READING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate READING"))
			;
		config.configure(READING_INTRO)
			.substateOf(READING)
			.onEntry(() -> System.out.println("Entering READING_INTRO"))
			;
		config.configure(READING_NARRATION)
			.substateOf(READING)
			.onEntry(() -> System.out.println("Entering READING_NARRATION"))
			;
		config.configure(READING_CLOSE)
			.substateOf(READING)
			.onEntry(() -> System.out.println("Entering READING_CLOSE"))
			;

		/* Ezzie is delivering printout and vouchers */
		
		config.configure(PRINTING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate PRINTING"))
			;
		config.configure(PRINTING_INTRO)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering PRINTING_INTRO"))
			;
		config.configure(PRINTING_READING)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering PRINTING_READING"))
			;
		config.configure(BANDY)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering BANDY"))
			;
		config.configure(BEACHES)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("Entering BEACHES"))
			;
		config.configure(ESTRALADA)
			.substateOf(PRINTING)
			.onEntry(() -> System.out.println("ESTRALADA"))
			;
		
		/* Ezzie's reading is ending */
		
		config.configure(CLOSING)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate CLOSING"))
			;
		
		/* Some twat has put cards in the wrong place at the wrong time */
		
		config.configure(FIX_PLACEMENT)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate FIX_PLACEMENT"))
			;
		config.configure(FIX_PAST)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_PAST"))
			;
		config.configure(FIX_PRESENT)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_PRESENT"))
			;
		config.configure(FIX_FUTURE)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_FUTURE"))
			;
		config.configure(FIX_JOCKER)
			.substateOf(FIX_PLACEMENT)
			.onEntry(() -> System.out.println("Entering FIX_JOCKER"))
			;
		
		/* The printer made a booboo */
		
		config.configure(FIX_PRINTER)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate FIX_PRINTER"))
			;
		
		/* Cards were not put away for next reading */
		
		config.configure(RESET_BOOTH)
			.substateOf(RUNNING)
			.onEntry(() -> System.out.println("Entering Superstate RESET_BOOTH"))
			;
		
		/* OLD */
		
		config.configure(IDLE)
			.permit(APPROACH_SENSOR, CURIOUS)
			.permit(PRESENCE_SENSOR, ENGAGED)
			.onEntry(this::idle)
			.onExit(this::idleExit)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)
			.ignore(ADVANCE)
			.ignore(BAD_PLACEMENT);
	
		config.configure(CURIOUS)
			.permit(PRESENCE_SENSOR, ENGAGED)
			.permit(ADVANCE, IDLE)
			.onEntry(this::curious)
			.onExit(this::curiousExit)
			.ignore(APPROACH_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)		
			.ignore(BAD_PLACEMENT);
		
		config.configure(ENGAGED)
			.permit(PAST_READ, REQUESTING_PRESENT)
			.permit(ADVANCE, QUINN)
			.permit(TIMEOUT, IDLE)
			.permit(BAD_PLACEMENT, RESET_BOOTH)
			.onEntry(this::engaged)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR);
		
		config.configure(QUINN)
			.permit(ADVANCE, ASIDE)
			.substateOf(ENGAGED)
			.onEntry(this::quinn);
	
		config.configure(ASIDE)
			.permit(ADVANCE, INTRO)
			.substateOf(ENGAGED)
			.onEntry(this::aside);
		
		config.configure(INTRO)
			.permit(ADVANCE, REQUESTING_PAST)
			.substateOf(ENGAGED)
			.onEntry(this::intro);
		
		config.configure(REQUESTING_PAST)
			.permit(PAST_READ, REQUESTING_PRESENT)
			.permit(TIMEOUT, IDLE)
			.permit(BAD_PLACEMENT, FIX_PLACEMENT)
			.onEntry(this::requestingPast)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(ADVANCE);
		
		config.configure(REQUESTING_PRESENT)
			.permit(PRESENT_READ, REQUESTING_FUTURE)
			.permit(TIMEOUT, IDLE)
			.permit(BAD_PLACEMENT, FIX_PLACEMENT)
			.onEntry(this::requestingPresent)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(ADVANCE);
		
		config.configure(REQUESTING_FUTURE)
			.permit(FUTURE_READ, READING)
			.permit(TIMEOUT, IDLE)
			.permit(BAD_PLACEMENT, FIX_PLACEMENT)
			.onEntry(this::requestingFuture)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(PRINTER_ERROR)
			.ignore(ADVANCE);
		
		config.configure(READING)
			.permit(ADVANCE, CLOSING)
			.onEntry(this::reading)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)
			.ignore(BAD_PLACEMENT);
		
		config.configure(FIX_PLACEMENT)
			.permit(ADVANCE, REQUESTING_PAST)
			.permit(TIMEOUT, RESET_BOOTH)
			.onEntry(this::fixPlacement)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(BAD_PLACEMENT);
		
		config.configure(RESET_BOOTH)
			.permit(ADVANCE, ENGAGED)
			.onEntry(this::resetBooth)
			.ignore(APPROACH_SENSOR)
			.ignore(PRESENCE_SENSOR)
			.ignore(PAST_READ)
			.ignore(PRESENT_READ)
			.ignore(FUTURE_READ)
			.ignore(PRINTER_ERROR)
			.ignore(TIMEOUT)
			.ignore(BAD_PLACEMENT);

					
		/* @formatter:on */

//		try {
//			config.generateDotFileInto(System.out, true);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		EzzieMachine stateMachine = new EzzieMachine(RUNNING, config);
		stateMachine.fireInitialTransition();
	    
		return stateMachine;
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

		//stateMachine.fire(ADVANCE);
		
		/* Wrapping in a timer to trick spring into ignoring the circularity */
		
		timer.schedule(new TimerTask() {
	        public void run() {
	        	stateMachine.fire(ADVANCE);
	        }
	    }, 100);
	}
}
