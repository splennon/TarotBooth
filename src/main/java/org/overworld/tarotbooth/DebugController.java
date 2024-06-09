/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.overworld.tarotbooth.BoothApplication.State;
import org.overworld.tarotbooth.BoothApplication.Trigger;
import org.overworld.tarotbooth.model.Deck.Card;
import org.overworld.tarotbooth.model.GameModel;

import com.github.oxo42.stateless4j.StateMachine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

public class DebugController implements Initializable {
    
	private StateMachine<State, Trigger> statemachine = BoothApplication.getStateMachine();
	private GameModel model = BoothApplication.getGameModel();
	
    @FXML
    private Button pastButton;

    @FXML
    private TextField presentText;

    @FXML
    private TextField pastText;

    @FXML
    private TextField futureText;

    @FXML
    private Button futureButton;

    @FXML
    private Button presentButton;

    @FXML
    private ToggleButton presenceToggle;

    @FXML
    private ToggleButton approachToggle;
	
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    	pastButton.setOnMouseClicked(this::past);
    	presentButton.setOnMouseClicked(this::present);
    	futureButton.setOnMouseClicked(this::future);
    	approachToggle.setOnAction(e -> statemachine.fire(Trigger.APPROACH_SENSOR));
    	presenceToggle.setOnAction(e -> statemachine.fire(Trigger.PRESENCE_SENSOR));
    }
    
    private void past(MouseEvent e) {
    	
    	Card c = GameModel.getDeck().get(pastText.getText());
    	model.setPast(c);
    	statemachine.fire(Trigger.PAST_READ);
    };
    
    private void present(MouseEvent e) {

    	Card c = GameModel.getDeck().get(presentText.getText());
    	model.setPresent(c);
    	statemachine.fire(Trigger.PRESENT_READ);
    };
    
    private void future(MouseEvent e) {

    	Card c = GameModel.getDeck().get(futureText.getText());
    	model.setFuture(c);
    	statemachine.fire(Trigger.FUTURE_READ);
    };
}
