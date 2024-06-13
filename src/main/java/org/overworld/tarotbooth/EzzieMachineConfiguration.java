package org.overworld.tarotbooth;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.overworld.tarotbooth.EzzieMachine.State;
import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.sound.SoundLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.github.oxo42.stateless4j.StateMachineConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Configuration
public class EzzieMachineConfiguration {
	
	@Autowired
	private BoothController controller;
	
	@Autowired
	private SoundLibrary sounds;
	
	@Autowired
	private SoundCarousel carousel;
	
	EzzieMachine stateMachine;
	
	Random random = new Random();
	
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
			.permit(Trigger.ADVANCE, State.REQUESTING_PAST)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.permit(Trigger.BAD_PLACEMENT, State.RESET_BOOTH)
			.onEntry(this::engaged)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PRESENCE_SENSOR)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR);
		
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

		// config.generateDotFileInto(System.out, true);
		EzzieMachine stateMachine = new EzzieMachine(State.IDLE, config);
		stateMachine.fireInitialTransition();
	    
		return stateMachine;
	}

	public void idle() {
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
	}
	
	public void engaged() {
		System.out.println("engaged");
		controller.ezzieMode();
	}

	public void requestingPast() {
		System.out.println("requesting past");
	}

	public void requestingPresent() {
		System.out.println("requesting present");
	}

	public void requestingFuture() {
		System.out.println("requesting future");
	}

	public void reading() {
		System.out.println("reading");
	}

	public void fixPlacement() {
		System.out.println("fix placement");
	}

	public void resetBooth() {
		System.out.println("reset booth");
	}

	public void closing() {
		System.out.println("closing");
	}

	public void fixPrinter() {
		System.out.println("fix printer");
	}
}
