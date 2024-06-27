/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.overworld.tarotbooth;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

@Component
public class BoothController implements Initializable {
		
    @FXML
    private AnchorPane beaches;

    @FXML
    private AnchorPane bandy;
    
    @FXML
    private AnchorPane curtains;

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
    
    @FXML
    private AnchorPane vacate;

    @FXML
    private Label meaningText;

    @FXML
    private ImageView meaningCard;

    @FXML
    private ImageView pastCard;

    @FXML
    private ImageView presentCard;
    
    @FXML
    private ImageView futureCard;
    
    private Pane current;
    
    private Pane[] allPanes;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	allPanes = new Pane[] {beaches, bandy, curtains, benny, drawing, ezzie, reading, estralada, vacate, quinn};
    	
    	/* TODO Try using this method to advance the state machine to curtains instead of delay */
    	
    	pastCard.setImage(null);
    	presentCard.setImage(null);
    	futureCard.setImage(null);
    	
    	meaningText.setText("");
    	meaningCard.setImage(null);
    }
    
    public void readingSetup(Image past, Image present, Image future) {
    	
    	pastCard.setImage(past);
    	presentCard.setImage(present);
    	futureCard.setImage(future);
    }
    
    public void meaningSetup(Image card, String meaning) {
    	
    	meaningCard.setImage(card);
    	meaningText.setText(meaning);
    }
    
    public void estraladaMode() {
    	fadeTo(estralada);
    }
    public void bandyMode() {
    	fadeTo(bandy);
    }
    public void beachesMode() {
    	fadeTo(beaches);
    }
    public void readingMode() {
    	fadeTo(reading);
    }
    public void drawingMode() {
    	fadeTo(drawing);	
    }
    
    public void quinnMode() {
    	fadeTo(quinn);
    }
    
    public void curtainskMode() {
    	fadeTo(curtains);
    }

	public void ezzieMode() {
		fadeTo(ezzie);
	}

	public void bennyMode() {
		fadeTo(benny);
	}
	
	public void vacateMode() {
		fadeTo(vacate);
	}
	
    private void fadeTo(Pane pane) {
    	
		FadeTransition fadeIn = new FadeTransition(Duration.millis(900), pane);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		
		if (pane != current) {
	    	if (current == null) {
	    		fadeIn.play();
				current = pane;
	    	} else {
	    		FadeTransition fadeOut = new FadeTransition(Duration.millis(900), current);
	    		fadeOut.setFromValue(1);
	    		fadeOut.setToValue(0);
	    		fadeOut.setOnFinished(e -> {
	    			fadeIn.play();
	    			current = pane;
	    		});
	    		fadeOut.play();
	    	}
		}
    	
    	if (allPanes != null ) {
	    	for (Pane p : allPanes) {
	    		
	    		if (p != current && p != pane)
	    			p.setOpacity(0);
	    	}
    	}
    }
}
