package org.overworld.tarotbooth;

import java.io.IOException;

import org.overworld.tarotbooth.StateMachineConfiguration.State;
import org.overworld.tarotbooth.StateMachineConfiguration.Trigger;
import org.overworld.tarotbooth.model.GameModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.oxo42.stateless4j.StateMachine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class BoothApplication extends Application {

	public static ConfigurableApplicationContext springContext;
	private Parent rootNode;

	private Scene threeCardScene, debugScene, ezzieScene;

	@Autowired
	private StateMachineConfiguration stateMachineSupplier;

	@Autowired
	private GameModel gameModel;

	@Override
	public void start(Stage prinaryStage) throws IOException {
		
		/* TODO: This should all be in the statemachine */

		Stage mainStage = new Stage();
		Stage debugStage = new Stage();
		
		threeCardScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("threeCardSpread.fxml")).load(),
				640, 480);

		debugScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("debug.fxml")).load(), 640, 480);

		debugStage.setScene(debugScene);
		debugStage.show();

		ezzieScene = new Scene(new FXMLLoader(BoothApplication.class.getResource("ezzie.fxml")).load(), 640, 480);
		mainStage.setScene(ezzieScene);
		mainStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(BoothApplication.class);
		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/org/overworld/tarotbooth/ezzie.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		rootNode = fxmlLoader.load();
	}
	
	@Override
	public void stop() throws Exception {
		springContext.close();
	}
}
