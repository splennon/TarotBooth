package org.overworld.tarotbooth;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
@EnableScheduling
public class BoothApplication extends Application {

	public static ConfigurableApplicationContext springContext;

	private Scene threeCardScene, debugScene, ezzieScene;

	@Override
	public void start(Stage prinaryStage) throws IOException {

		/* TODO: This should all be in the statemachine */

		Stage mainStage = new Stage();
		Stage debugStage = new Stage();

		threeCardScene = new Scene(FXMLLoader.load(BoothApplication.class.getResource("threeCardSpread.fxml"), null,
				null, springContext::getBean), 640, 480);

		debugScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("debug.fxml"), null, null, springContext::getBean),
				640, 480);

		debugStage.setScene(debugScene);
		debugStage.show();

		ezzieScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("debug.fxml"), null, null, springContext::getBean),
				640, 480);
		mainStage.setScene(ezzieScene);
		mainStage.show();
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(BoothApplication.class);
	}

	@Override
	public void stop() throws Exception {
		springContext.close();
	}
}
