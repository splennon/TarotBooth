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
		IDLE, CURIOUS, ENGAGED, REQUESTING_PAST, TIMEOUT_PAST, REQUESTING_PRESENT, TIMEOUT_PRESENT, REQUESTING_FUTURE,
		TIMEOUT_FUTURE, PLACEMENT_ERROR, PRE_READING, READING_PAST, READING_PRESENT, READING_FUTURE, ABANDONED, CLOSING,
		PRINTING
	}

	public enum Trigger {
		APPROACH_SENSOR, PRESENCE_SENSOR, PAST_READ, PRESENT_READ, FUTURE_READ, PRINTER_ERROR
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
		
		debugScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("debug.fxml")).load(),
				640, 480);	
		
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
		

		config.configure(State.IDLE).permit(Trigger.APPROACH_SENSOR, State.CURIOUS)
				.onEntry(BoothApplication::idleNoise);
		
		config.configure(State.CURIOUS).onEntry(() -> System.out.println("We got a live one benny!"));

		
		// config.generateDotFileInto(System.out);

		
		stateMachine = new StateMachine<>(State.IDLE, config);
		stateMachine.fireInitialTransition();
	}

	private static void idleNoise() {
		System.out.println("IdleNoise");
	}
}
