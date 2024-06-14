/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.overworld.tarotbooth.EzzieMachine.Trigger;
import org.overworld.tarotbooth.model.Deck;
import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.model.GameModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

@Component
public class DebugController implements Initializable {
    
	@Autowired
	private EzzieMachine stateMachine;
	
	@Autowired
	TimeoutService timeout;

	@Autowired
	private GameModel gameModel;
	
	@Autowired
	private Deck deck;

    @FXML
    private Button pastButton;

    @FXML
    private Button advanceButton;

    @FXML
    private Button placementButton;

    @FXML
    private TextField presentText;

    @FXML
    private Button timeoutButton;

    @FXML
    private TextField pastText;

    @FXML
    private TextField futureText;

    @FXML
    private Button futureButton;

    @FXML
    private Button presentButton;

    @FXML
    private Button printerButton;

    @FXML
    private ToggleButton presenceToggle;

    @FXML
    private ToggleButton approachToggle;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	//stateMachine = BoothApplication.springContext.getBean(StateMachine.class);
    	
    	pastButton.setOnMouseClicked(this::past);
    	presentButton.setOnMouseClicked(this::present);
    	futureButton.setOnMouseClicked(this::future);
    	approachToggle.setOnAction(e -> stateMachine.fire(Trigger.APPROACH_SENSOR));
    	presenceToggle.setOnAction(e -> {timeout.poke(); stateMachine.fire(Trigger.PRESENCE_SENSOR);});
    	advanceButton.setOnAction(e -> stateMachine.fire(Trigger.ADVANCE));
    	printerButton.setOnAction(e -> stateMachine.fire(Trigger.PRINTER_ERROR));
    	timeoutButton.setOnAction(e -> stateMachine.fire(Trigger.TIMEOUT));
    	placementButton.setOnAction(e -> stateMachine.fire(Trigger.BAD_PLACEMENT));
    }
    
    private void past(MouseEvent e) {
    	
    	timeout.poke();
    	Card c = deck.get(pastText.getText());
    	gameModel.setPast(c);
    	stateMachine.fire(Trigger.PAST_READ);
    };
    
    private void present(MouseEvent e) {
    	
    	timeout.poke();
    	Card c = deck.get(presentText.getText());
    	gameModel.setPresent(c);
    	stateMachine.fire(Trigger.PRESENT_READ);
    };
    
    private void future(MouseEvent e) {

    	timeout.poke();
    	Card c = deck.get(futureText.getText());
    	gameModel.setFuture(c);
    	stateMachine.fire(Trigger.FUTURE_READ);
    }
}
