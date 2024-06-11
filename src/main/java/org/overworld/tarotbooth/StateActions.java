package org.overworld.tarotbooth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StateActions {

	@Autowired
	private ApplicationContext springContext;
	
	@Autowired
	private BoothController controller;
	
	private EzzieMachine stateMachine;

	@Autowired
	public void setStateMachine(@Lazy EzzieMachine stateMachine) {
		this.stateMachine = stateMachine;
	}
	
	private Stage mainStage, debugStage;
	private Scene debugScene, mainScene;
	
	private Parent boothRoot;

	public void initialize() throws IOException {

		mainStage = new Stage();
		debugStage = new Stage();

		debugScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("debug.fxml"), null, null, springContext::getBean),
				640, 480);

		boothRoot =	FXMLLoader.load(BoothApplication.class.getResource("booth.fxml"), null, null, springContext::getBean);

		mainScene = new Scene(
				FXMLLoader.load(BoothApplication.class.getResource("booth.fxml"), null, null, springContext::getBean),
				640, 480);
		
		debugStage.setScene(debugScene);
		debugStage.show();

		mainStage.setScene(mainScene);
		mainStage.show();
		
		stateMachine.fireInitialTransition();
	}

	public void idle() {
		System.out.println("idle");
	}

	public void curious() {
		System.out.println("curious");
		controller.fadeinezzie(); //TODO
	}

	public void engaged() {
		System.out.println("engaged");
		controller.fadeinbenny(); //TODO
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
