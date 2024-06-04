package org.overworld.tarotbooth;

import java.io.IOException;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

public class BoothApplication extends Application {

	private enum State {
		IDLE, CURIOUS, ENGAGED, REQUESTING_PAST, TIMEOUT_PAST, REQUESTING_PRESENT, TIMEOUT_PRESENT, REQUESTING_FUTURE,
		TIMEOUT_FUTURE, PLACEMENT_ERROR, PRE_READING, READING_PAST, READING_PRESENT, READING_FUTURE, ABANDONED, CLOSING,
		PRINTING
	}

	private enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR
	}

	private static Scene threeCardScene, ezzieScene;

	private Stage ezzieStage = new Stage();
	private Stage threeCardStage = new Stage();

	@Getter
	private final StateMachine<State, Trigger> stateMachine;

	@Override
	public void start(Stage prinaryStage) throws IOException {

		threeCardScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("threeCardSpread.fxml")).load(),
				640, 480);
		threeCardStage.setScene(threeCardScene);
		threeCardStage.show();

		ezzieScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("ezzie.fxml")).load(), 640, 480);
		ezzieStage.setScene(ezzieScene);
		ezzieStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	public BoothApplication() {

		var config = new StateMachineConfig<State, Trigger>();

		config.configure(State.IDLE).permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
				.onEntry(BoothApplication::idleNoise);

		stateMachine = new StateMachine<>(State.IDLE, config);
		stateMachine.fireInitialTransition();
	}

	private static void idleNoise() {
		System.out.println("IdleNoise");
	}
}
