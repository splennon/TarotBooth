/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

@Component
public class BoothController implements Initializable {
    
    @FXML
    private AnchorPane beaches;

    @FXML
    private AnchorPane benny;

    @FXML
    private AnchorPane drawing;

    @FXML
    private AnchorPane ezzie;

    @FXML
    private AnchorPane reading;

    @FXML
    private AnchorPane estralada;
    
    @FXML
    private AnchorPane quinn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

	public void fadeinezzie() {
		
		FadeTransition fade = new FadeTransition(Duration.millis(900), ezzie);
		fade.setFromValue(0);
		fade.setToValue(1);
		fade.play();
	}

	public void fadeinbenny() {
		
		FadeTransition fadeIn = new FadeTransition(Duration.millis(900), benny);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		
		FadeTransition fadeOut = new FadeTransition(Duration.millis(900), ezzie);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setOnFinished(e -> {fadeIn.play();});
		fadeOut.play();
		

	}    
}
