package org.overworld.tarotbooth;

import java.io.IOException;

import org.overworld.tarotbooth.model.GameModel;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

public class BoothApplication extends Application {

	public enum State {
		IDLE, CURIOUS, ENGAGED, REQUESTING_PAST, REQUESTING_PRESENT, REQUESTING_FUTURE, FIX_PLACEMENT, READING, CLOSING,
		RESET_BOOTH, FIX_PRINTER
	}

	public enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR, TIMEOUT, ADVANCE,
		BAD_PLACEMENT
	}

	private Scene threeCardScene, debugScene, ezzieScene;

	private Stage ezzieStage = new Stage();
	private Stage debugStage = new Stage();

	@Getter
	private static StateMachine<State, Trigger> stateMachine;

	@Getter
	private static GameModel gameModel = new GameModel();

	@Override
	public void start(Stage prinaryStage) throws IOException {

		threeCardScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("threeCardSpread.fxml")).load(),
				640, 480);

		debugScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("debug.fxml")).load(), 640, 480);

		debugStage.setScene(debugScene);
		debugStage.show();

		ezzieScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("ezzie.fxml")).load(), 640, 480);
		ezzieStage.setScene(ezzieScene);
		ezzieStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	public BoothApplication() throws IOException {

		var config = new StateMachineConfig<State, Trigger>();

		/* @formatter:off */
		
		config.configure(State.IDLE)
			.permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.onEntry(this::idle)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.TIMEOUT)
			.ignore(Trigger.ADVANCE)
			.ignore(Trigger.BAD_PLACEMENT);

		config.configure(State.CURIOUS)
			.permit(Trigger.PRESENCE_SENSOR, State.ENGAGED)
			.permit(Trigger.TIMEOUT, State.IDLE)
			.onEntry(this::curious)
			.ignore(Trigger.APPROACH_SENSOR)
			.ignore(Trigger.PAST_READ)
			.ignore(Trigger.PRESENT_READ)
			.ignore(Trigger.FUTURE_READ)
			.ignore(Trigger.PRINTER_ERROR)
			.ignore(Trigger.ADVANCE)
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

		stateMachine = new StateMachine<>(State.IDLE, config);
		stateMachine.fireInitialTransition();
	}

	private void idle() {
		System.out.println("idle");
	}

	private void curious() {
		System.out.println("curious");
	}

	private void engaged() {
		System.out.println("engaged");
	}

	private void requestingPast() {
		System.out.println("requesting past");
	}

	private void requestingPresent() {
		System.out.println("requesting present");
	}

	private void requestingFuture() {
		System.out.println("requesting future");
	}

	private void reading() {
		System.out.println("reading");
	}

	private void fixPlacement() {
		System.out.println("fix placement");
	}

	private void resetBooth() {
		System.out.println("reset booth");
	}

	private void closing() {
		System.out.println("closing");
	}

	private void fixPrinter() {
		System.out.println("fix printer");
	}
}
