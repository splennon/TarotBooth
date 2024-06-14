package org.overworld.tarotbooth;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javafx.application.Application;
import javafx.stage.Stage;

@SpringBootApplication
@EnableScheduling
public class BoothApplication extends Application {

	private ConfigurableApplicationContext springContext;

	@Override
	public void start(Stage primaryStage) throws IOException {

		/*
		 * JavaFX Initialisation has been moved to this bean, but it needs to happen on
		 * the JavaFX application thread
		 */
		springContext.getBean(EzzieMachineActions.class).initialize();
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
